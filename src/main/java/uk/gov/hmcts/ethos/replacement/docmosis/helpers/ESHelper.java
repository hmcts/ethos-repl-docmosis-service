package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.util.List;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

public class ESHelper {

    private static final String ETHOS_CASE_REFERENCE_KEYWORD = "data.ethosCaseReference.keyword";

    public static String getSearchQuery(List<String> caseIds) {
        TermsQueryBuilder termsQueryBuilder = termsQuery(ETHOS_CASE_REFERENCE_KEYWORD, caseIds);
        return new SearchSourceBuilder()
                .query(termsQueryBuilder).toString();
    }

}
