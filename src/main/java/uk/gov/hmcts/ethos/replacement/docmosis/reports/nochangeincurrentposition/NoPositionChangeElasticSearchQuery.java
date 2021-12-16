package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.time.LocalDate;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MAX_ES_SIZE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;

class NoPositionChangeElasticSearchQuery {
    private NoPositionChangeElasticSearchQuery() {
        // Access through static methods
    }

    static String create(String reportDateLimit) {
        // using EPOCH/UNIX date as lower limit for search query.
        var lowerDateLimit = LocalDate.of(1970, 1, 1).format(OLD_DATE_TIME_PATTERN2);
        var boolQueryBuilder = boolQuery()
                .filter(new RangeQueryBuilder("data.dateToPosition").gte(lowerDateLimit).lte(reportDateLimit));

        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(boolQueryBuilder).toString();
    }
}
