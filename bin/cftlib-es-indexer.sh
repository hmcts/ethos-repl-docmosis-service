#!/usr/bin/env bash
# =============================================================================
# cftlib-es-indexer.sh
# =============================================================================
#
# PURPOSE
#   Shell script equivalent of the CFTLib built-in ESIndexer:
#     cftlib/lib/runtime/src/main/java/uk/gov/hmcts/rse/ccd/lib/ESIndexer.java
#
#   The built-in ESIndexer is disabled when running in CCD decentralised mode
#   because the CCD SDK gradle plugin sets the environment variable
#   CCD_SDK_DECENTRALISED=true, which Spring Boot maps to the property
#   ccd.sdk.decentralised=true. This causes the ESIndexer's
#   @ConditionalOnProperty(havingValue="false") to fail, so the bean is never
#   created and cases are never indexed.
#
#   This script fills that gap: it replicates the same SQL query and
#   Elasticsearch bulk-index logic so cases remain searchable locally when
#   running bootWithCCD in decentralised mode.
#
# WHAT IT DOES
#   1. Waits for the CCD datastore PostgreSQL database to become available.
#   2. Enters a poll loop (every 1 second).
#   3. Each tick: runs a CTE that atomically marks all un-indexed rows in
#      case_data (marked_by_logstash = false → true) and returns them.
#   4. Builds an Elasticsearch NDJSON bulk request from the returned rows.
#   5. For cases that contain a SearchCriteria field, also writes a trimmed
#      entry into the global_search index (mirrors CFTLib ESIndexer behaviour).
#   6. POSTs the bulk request to Elasticsearch and logs the result.
#
# PREREQUISITES
#   - psql    (PostgreSQL client)
#   - curl
#   - jq      (brew install jq if missing)
#   - bootWithCCD must be running (provides the datastore DB and ES)
#
# USAGE
#   Run in the foreground:
#     ./bin/cftlib-es-indexer.sh
#
#   Run in the background (survives terminal close with nohup):
#     nohup ./bin/cftlib-es-indexer.sh > /tmp/es-indexer.log 2>&1 &
#
#   Run in the background in the current shell session:
#     ./bin/cftlib-es-indexer.sh &
#
#   Stop a background instance:
#     kill $(pgrep -f cftlib-es-indexer)
#
# CONFIGURATION (environment variables — all optional)
#   DB_HOST   PostgreSQL host          (default: localhost)
#   DB_PORT   PostgreSQL port          (default: 6432)
#   DB_NAME   Database name            (default: datastore)
#   DB_USER   Database user            (default: postgres)
#   DB_PASS   Database password        (default: postgres)
#   ES_URL    Elasticsearch base URL   (default: http://localhost:9200)
#
# NOTES
#   - The script is safe to restart at any time; already-indexed cases have
#     marked_by_logstash=true in the DB and will not be re-processed.
#   - If Elasticsearch returns indexing errors they are logged but the script
#     continues — it does NOT re-mark affected rows as unindexed.
#   - The DB password is passed via PGPASSWORD (never echoed to the terminal).
# =============================================================================

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-6432}"
DB_NAME="${DB_NAME:-datastore}"
DB_USER="${DB_USER:-postgres}"
DB_PASS="${DB_PASS:-postgres}"
ES_URL="${ES_URL:-http://localhost:9200}"

export PGPASSWORD="$DB_PASS"

# ── helpers ───────────────────────────────────────────────────────────────────

psql_exec() {
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" "$@"
}

log() { echo "[$(date '+%H:%M:%S')] $*"; }

# ── wait for DB ───────────────────────────────────────────────────────────────

log "Waiting for datastore DB at $DB_HOST:$DB_PORT/$DB_NAME ..."
until psql_exec -c "SELECT 1" > /dev/null 2>&1; do
    sleep 3
done
log "DB ready. Starting indexer (polling every 1s) ..."

# ── main loop ─────────────────────────────────────────────────────────────────

while true; do
    sleep 1

    # Query: mark unindexed cases and return their data as a JSON array.
    # Using json_agg so jq can handle all escaping cleanly.
    RESULT=$(psql_exec -t -A <<'SQL'
WITH updated AS (
  UPDATE case_data
  SET    marked_by_logstash = true
  WHERE  NOT marked_by_logstash
  RETURNING *
)
SELECT coalesce(json_agg(x), '[]'::json)::text
FROM (
  SELECT
    id::text                                AS id,
    lower(case_type_id) || '_cases'         AS index_id,
    row_to_json(r)                          AS row
  FROM (
    SELECT
      now()                                            AS "@timestamp",
      version::text                                    AS "@version",
      case_type_id,
      created_date,
      data,
      data_classification,
      id,
      jurisdiction,
      reference,
      last_modified,
      last_state_modified_date,
      supplementary_data,
      lower(case_type_id) || '_cases'                  AS index_id,
      state,
      security_classification
    FROM updated
  ) r
) x
SQL
    )

    # Empty array → nothing to index this tick
    if [ "$RESULT" = "[]" ] || [ -z "$RESULT" ]; then
        continue
    fi

    # Build NDJSON bulk body using jq
    BULK_BODY=$(echo "$RESULT" | jq -r '
        .[] |
        "{\"index\":{\"_index\":\"" + .index_id + "\",\"_id\":\"" + .id + "\"}}",
        (.row | tostring)
    ')

    COUNT=$(echo "$RESULT" | jq 'length')

    # Global search: for cases with a SearchCriteria field, add a second
    # entry into the global_search index (mirrors CFTLib ESIndexer logic)
    GS_BODY=$(echo "$RESULT" | jq -r '
        .[] | select(.row.data.SearchCriteria != null) |
        . as $entry |
        ($entry.row | {
            "@timestamp",
            "@version",
            case_type_id,
            data: { SearchCriteria: .data.SearchCriteria,
                    caseManagementLocation: .data.caseManagementLocation,
                    CaseAccessCategory: .data.CaseAccessCategory,
                    caseNameHmctsInternal: .data.caseNameHmctsInternal,
                    caseManagementCategory: .data.caseManagementCategory },
            supplementary_data: { HMCTSServiceId: .supplementary_data.HMCTSServiceId },
            data_classification: { SearchCriteria: .data_classification.SearchCriteria,
                                   CaseAccessCategory: .data_classification.CaseAccessCategory,
                                   caseManagementLocation: .data_classification.caseManagementLocation,
                                   caseNameHmctsInternal: .data_classification.caseNameHmctsInternal,
                                   caseManagementCategory: .data_classification.caseManagementCategory },
            id,
            jurisdiction,
            reference,
            state,
            security_classification,
            index_id: "global_search"
        }) as $gs_row |
        "{\"index\":{\"_index\":\"global_search\",\"_id\":\"" + $entry.id + "\"}}",
        ($gs_row | tostring)
    ')

    FULL_BODY="${BULK_BODY}"$'\n'
    if [ -n "$GS_BODY" ]; then
        FULL_BODY+="${GS_BODY}"$'\n'
        GS_COUNT=$(echo "$RESULT" | jq '[.[] | select(.row.data.SearchCriteria != null)] | length')
        COUNT=$((COUNT + GS_COUNT))
    fi

    # Send to Elasticsearch
    RESPONSE=$(printf '%s\n' "$FULL_BODY" | curl -s -X POST "$ES_URL/_bulk" \
        -H "Content-Type: application/x-ndjson" \
        --data-binary @-)

    if echo "$RESPONSE" | jq -e '.errors == true' > /dev/null 2>&1; then
        log "ERROR: ES bulk indexing errors: $RESPONSE"
    else
        log "Indexed $COUNT document(s)"
    fi
done
