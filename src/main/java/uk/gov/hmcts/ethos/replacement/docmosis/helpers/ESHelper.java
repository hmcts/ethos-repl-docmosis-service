package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.util.List;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.MAX_ES_SIZE;

public class ESHelper {

    private static final String ETHOS_CASE_REFERENCE_KEYWORD = "data.ethosCaseReference.keyword";
    private static final String MULTIPLE_CASE_REFERENCE_KEYWORD = "data.multipleReference.keyword";

    public static String getSearchQuery(List<String> caseIds) {
        TermsQueryBuilder termsQueryBuilder = termsQuery(ETHOS_CASE_REFERENCE_KEYWORD, caseIds);
        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(termsQueryBuilder).toString();
    }

    public static String getBulkSearchQuery(String multipleReference) {
        TermsQueryBuilder termsQueryBuilder = termsQuery(MULTIPLE_CASE_REFERENCE_KEYWORD, multipleReference);
        return new SearchSourceBuilder()
                .size(MAX_ES_SIZE)
                .query(termsQueryBuilder).toString();
    }

}
