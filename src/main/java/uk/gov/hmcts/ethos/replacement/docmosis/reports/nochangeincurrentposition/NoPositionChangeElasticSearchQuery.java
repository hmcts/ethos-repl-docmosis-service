package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MAX_ES_SIZE;

class NoPositionChangeElasticSearchQuery {
    private NoPositionChangeElasticSearchQuery() {
        // Access through static methods
    }

    static String create(String reportDateLimit) {
        var rangeQueryBuilder = rangeQuery("data.dateToPosition").lte(reportDateLimit).includeUpper(false);
        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(rangeQueryBuilder).toString();
    }
}
