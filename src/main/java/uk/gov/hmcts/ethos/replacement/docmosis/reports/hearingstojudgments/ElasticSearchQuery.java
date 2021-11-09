package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MAX_ES_SIZE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReport.VALID_CASE_STATES;

class ElasticSearchQuery {

    private ElasticSearchQuery() {
        // Access through static methods
    }

    static String create(String dateToSearchFrom, String dateToSearchTo) {
        var boolQueryBuilder = boolQuery()
                .must(new ExistsQueryBuilder("data.judgementCollection"))
                .must(new ExistsQueryBuilder("data.hearingCollection"))
                .filter(new TermsQueryBuilder("state.keyword", VALID_CASE_STATES))
                .filter(new RangeQueryBuilder(
                        "data.hearingCollection.value.hearingDateCollection.value.listedDate"
                ).gte(dateToSearchFrom).lte(dateToSearchTo));

        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(boolQueryBuilder).toString();
    }
}
