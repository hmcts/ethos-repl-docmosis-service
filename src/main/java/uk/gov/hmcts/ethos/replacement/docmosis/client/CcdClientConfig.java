package uk.gov.hmcts.ethos.replacement.docmosis.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Component
public class CcdClientConfig {

    private static final String START_CASE_CREATION_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/event-triggers/%s/token?ignore-warning=true";
    private static final String SUBMIT_CASE_CREATION_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases";
    private static final String RETRIEVE_CASE_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/%s";
    private static final String RETRIEVE_CASES_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases?%s";
    private static final String START_EVENT_FOR_CASE_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/%s/event-triggers/%s/token";
    private static final String SUBMIT_EVENT_FOR_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/%s/events";
    private static final String PAGINATION_METADATA_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/pagination_metadata";

    @Value("${ccd.data.store.api.url}")
    private String CCD_DATA_STORE_API_BASE_URL;

    private AuthTokenGenerator authTokenGenerator;
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @Autowired
    public CcdClientConfig(AuthTokenGenerator authTokenGenerator) {
        this.authTokenGenerator = authTokenGenerator;
    }

    String buildStartCaseCreationUrl(String uid, String jid, String ctid) {
        return String.format(START_CASE_CREATION_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, CREATION_EVENT_TRIGGER_ID);
    }

    String buildSubmitCaseCreationUrl(String uid, String jid, String ctid) {
        return String.format(SUBMIT_CASE_CREATION_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid);
    }

    String buildRetrieveCaseUrl(String uid, String jid, String ctid, String cid) {
        return String.format(RETRIEVE_CASE_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, cid);
    }

    String buildRetrieveCasesUrl(String uid, String jid, String ctid, String page) {
//        Map<String, String> params = new HashMap<>();
//        params.put("state", "1_Initiation");
        //String param = "state=1_Initiation";
        String param = "page=" + page;
        log.info("Looking cases by: uid: " + uid + " jid: " + jid + " ctid: " + ctid + " param: " + param);
        log.info("Format: " + String.format(RETRIEVE_CASES_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, param));
        return String.format(RETRIEVE_CASES_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, param);
    }
    //    JURISDICTION("jurisdiction"),
    //    CASE_TYPE("case_type"),
    //    STATE("state"),
    //    CASE_REFERENCE("case_reference"),
    //    CREATED_DATE("created_date"),
    //    LAST_MODIFIED_DATE("last_modified_date"),
    //    SECURITY_CLASSIFICATION("security_classification");

    String buildStartEventForCaseUrl(String uid, String jid, String ctid, String cid) {
        return String.format(START_EVENT_FOR_CASE_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, cid, UPDATE_EVENT_TRIGGER_ID);
    }

    String buildStartEventForCaseUrlBulkSingle(String uid, String jid, String ctid, String cid) {
        return String.format(START_EVENT_FOR_CASE_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, cid, UPDATE_EVENT_TRIGGER_ID_BULK);
    }

    String buildStartEventForBulkCaseUrl(String uid, String jid, String ctid, String cid) {
        return String.format(START_EVENT_FOR_CASE_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, cid, UPDATE_BULK_EVENT_TRIGGER_ID);
    }

    String buildSubmitEventForCaseUrl(String uid, String jid, String ctid, String cid) {
        return String.format(SUBMIT_EVENT_FOR_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, cid);
    }

    String buildPaginationMetadataCaseUrl(String uid, String jid, String ctid) {
        return String.format(PAGINATION_METADATA_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid);
    }

    HttpHeaders buildHeaders(String authToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        if (!authToken.matches("[a-zA-Z0-9._\\s\\S]+$")) {
            throw new IOException("authToken regex exception");
        }
        headers.add(HttpHeaders.AUTHORIZATION, authToken);
        headers.add(SERVICE_AUTHORIZATION, authTokenGenerator.generate());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        return headers;
    }

}
