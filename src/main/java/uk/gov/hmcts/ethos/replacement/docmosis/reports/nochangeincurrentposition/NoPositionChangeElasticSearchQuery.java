package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MAX_ES_SIZE;

class NoPositionChangeElasticSearchQuery {
    private NoPositionChangeElasticSearchQuery() {
        // Access through static methods
    }

    static String create(String reportDateLimit) {
        var boolQueryBuilder = boolQuery()
                .filter(new RangeQueryBuilder("data.dateToPosition").lte(reportDateLimit).includeUpper(false));

        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(boolQueryBuilder).toString();
    }
}
