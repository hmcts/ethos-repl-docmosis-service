package uk.gov.hmcts.ethos.replacement.docmosis.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class CcdClientConfigTest {

    @InjectMocks
    private CcdClientConfig ccdClientConfig;
    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Test
    public void buildStartCaseCreationUrl() {
        String uri = ccdClientConfig.buildStartCaseCreationUrl("1123", "TRIBUNALS", "TRIB_03");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/TRIB_03/event-triggers/" +
                "initiateCase/token?ignore-warning=true", uri);
    }

    @Test
    public void buildSubmitCaseCreationUrl() {
        String uri = ccdClientConfig.buildSubmitCaseCreationUrl("1123", "TRIBUNALS", "TRIB_03");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/TRIB_03/cases", uri);
    }

    @Test
    public void buildRetrieveCaseUrl() {
        String uri = ccdClientConfig.buildRetrieveCaseUrl("1123", "TRIBUNALS", "TRIB_03", "1222222");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/TRIB_03/cases/1222222", uri);
    }

    @Test
    public void buildRetrieveCasesUrl() {
        String uri = ccdClientConfig.buildRetrieveCasesUrl("1123", "TRIBUNALS", "TRIB_03");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/TRIB_03/cases?", uri);
    }

    @Test
    public void buildStartEventForCaseUrl() {
        String uri = ccdClientConfig.buildStartEventForCaseUrl("1123", "TRIBUNALS", "TRIB_03", "1222222");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/TRIB_03/cases/1222222/event-triggers/amendCaseDetails/token", uri);
    }

    @Test
    public void buildStartEventForCaseUrlBulkSingle() {
        String uri = ccdClientConfig.buildStartEventForCaseUrlBulkSingle("1123", "TRIBUNALS", "TRIB_03", "1222222");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/TRIB_03/cases/1222222/event-triggers/amendCaseDetailsBulk/token", uri);
    }

    @Test
    public void buildStartEventForBulkCaseUrl() {
        String uri = ccdClientConfig.buildStartEventForBulkCaseUrl("1123", "TRIBUNALS", "BULK_03", "1222222");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/BULK_03/cases/1222222/event-triggers/updateBulkAction/token", uri);
    }

    @Test
    public void buildSubmitEventForCaseUrl() {
        String uri = ccdClientConfig.buildSubmitEventForCaseUrl("1123", "TRIBUNALS", "TRIB_03", "1222222");
        assertEquals("null/caseworkers/1123/jurisdictions/TRIBUNALS/case-types/TRIB_03/cases/1222222/events", uri);
    }

    @Test
    public void buildHeaders() throws IOException {
        when(authTokenGenerator.generate()).thenReturn("authString");
        HttpHeaders httpHeaders = ccdClientConfig.buildHeaders("authString");
        assertEquals("[Authorization:\"authString\", ServiceAuthorization:\"authString\", " +
                "Content-Type:\"application/json;charset=UTF-8\"]", httpHeaders.toString());
    }
}