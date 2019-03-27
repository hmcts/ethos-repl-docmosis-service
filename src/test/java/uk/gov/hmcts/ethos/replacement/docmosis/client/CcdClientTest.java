package uk.gov.hmcts.ethos.replacement.docmosis.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

import java.io.IOException;
import java.util.ArrayList;

import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CcdClientTest {

    @InjectMocks
    private CcdClient ccdClient;
    @Mock
    private CcdClientConfig ccdClientConfig;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private UserService userService;
    @Mock
    private CaseDataBuilder caseDataBuilder;
    private UserDetails userDetails;
    private CaseDetails caseDetails;
    private CCDRequest ccdRequest;
    private String uri = "http://example.com";

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        ccdRequest.setEventId("1111");
        ccdRequest.setToken("Token");
        userDetails = new UserDetails("12", "example@gmail.com", "Smith", "John", new ArrayList<>());
        caseDetails = new CaseDetails();
        caseDetails.setJurisdiction("TRIBUNALS");
        caseDetails.setCaseTypeId("Type1");
        caseDetails.setCaseData(new CaseData());
    }
    @Test
    public void startCaseCreation() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<CCDRequest> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildStartCaseCreationUrl(any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class))).thenReturn(responseEntity);
        ccdClient.startCaseCreation("authToken", caseDetails);
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void submitCaseCreation() throws IOException {
        HttpEntity<CaseDataContent> httpEntity = new HttpEntity<>(CaseDataContent.builder().build(), null);
        ResponseEntity<SubmitEvent> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(caseDataBuilder.buildCaseDataContent(eq(caseDetails), eq(ccdRequest))).thenReturn(CaseDataContent.builder().build());
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildSubmitCaseCreationUrl(any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class))).thenReturn(responseEntity);
        ccdClient.submitCaseCreation("authToken", caseDetails, ccdRequest);
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void retrieveCase() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<SubmitEvent> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildRetrieveCaseUrl(any(), any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(SubmitEvent.class))).thenReturn(responseEntity);
        ccdClient.retrieveCase("authToken", caseDetails, "111111");
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(SubmitEvent.class));
        verifyNoMoreInteractions(restTemplate);
    }
}