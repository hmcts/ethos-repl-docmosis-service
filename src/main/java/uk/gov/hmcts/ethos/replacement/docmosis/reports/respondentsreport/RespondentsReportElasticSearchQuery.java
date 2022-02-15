package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MAX_ES_SIZE;

public class RespondentsReportElasticSearchQuery {

    private RespondentsReportElasticSearchQuery() {
        // Access through static methods
    }

    static String create(String dateToSearchFrom, String dateToSearchTo) {

        var boolQueryBuilder = boolQuery()
                .must(new ExistsQueryBuilder("data.respondentCollection"))
                .filter(new RangeQueryBuilder(
                "data.receiptDate")
                        .gte(dateToSearchFrom).lte(dateToSearchTo));

        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(boolQueryBuilder).toString();
    }
}
