package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MAX_ES_SIZE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReport.VALID_POSITION_TYPES;

public class ElasticSearchQuery {

    static String create() {
        var boolQueryBuilder = boolQuery()
                .mustNot(new MatchQueryBuilder("state.keyword", "Closed"))
                .mustNot(new ExistsQueryBuilder("data.JudgementCollection"))
                .must(new ExistsQueryBuilder("data.hearingCollection"))
                .filter(new TermsQueryBuilder("data.positionType.keyword", VALID_POSITION_TYPES));

        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(boolQueryBuilder).toString();
    }
}
