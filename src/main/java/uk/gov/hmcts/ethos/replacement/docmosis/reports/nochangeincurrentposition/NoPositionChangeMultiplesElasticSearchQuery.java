package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MAX_ES_SIZE;

public class NoPositionChangeMultiplesElasticSearchQuery {
    private NoPositionChangeMultiplesElasticSearchQuery() {
        // Access through static methods
    }

    static String create(List<String> multiplesList) {
        var boolQueryBuilder = boolQuery()
                .filter(new TermsQueryBuilder("data.multipleReference.keyword", multiplesList));

        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(boolQueryBuilder).toString();
    }
}
