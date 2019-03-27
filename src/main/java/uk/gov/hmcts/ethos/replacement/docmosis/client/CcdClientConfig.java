package uk.gov.hmcts.ethos.replacement.docmosis.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@Slf4j
@Component
public class CcdClientConfig {

    private static final String START_CASE_CREATION_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/event-triggers/%s/token?ignore-warning=true";
    private static final String SUBMIT_CASE_CREATION_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases";
    private static final String RETRIEVE_CASE_URL_CASEWORKER_FORMAT =
            "%s/caseworkers/%s/jurisdictions/%s/case-types/%s/cases/%s";

    @Value("${ccd.data.store.api.url}")
    private String CCD_DATA_STORE_API_BASE_URL;

    private static final String EVENT_TRIGGER_ID = "initiateCase";

    private AuthTokenGenerator authTokenGenerator;
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @Autowired
    public CcdClientConfig(AuthTokenGenerator authTokenGenerator) {
        this.authTokenGenerator = authTokenGenerator;
    }

    String buildStartCaseCreationUrl(String uid, String jid, String ctid) {
        return String.format(START_CASE_CREATION_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, EVENT_TRIGGER_ID);
    }

    String buildSubmitCaseCreationUrl(String uid, String jid, String ctid) {
        return String.format(SUBMIT_CASE_CREATION_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid);
    }

    String buildRetrieveCaseUrl(String uid, String jid, String ctid, String cid) {
        return String.format(RETRIEVE_CASE_URL_CASEWORKER_FORMAT, CCD_DATA_STORE_API_BASE_URL, uid, jid, ctid, cid);
    }

    HttpHeaders buildHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authToken);
        headers.add(SERVICE_AUTHORIZATION, authTokenGenerator.generate());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        return headers;
    }

}
