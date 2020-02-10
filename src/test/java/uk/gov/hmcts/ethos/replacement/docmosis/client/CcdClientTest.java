package uk.gov.hmcts.ethos.replacement.docmosis.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkCaseSearchResult;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.SubmitBulkEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.ALL_VENUES;

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
    private BulkDetails bulkDetails;
    private CaseData caseData;
    private BulkData bulkData;
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
        caseData = new CaseData();
        caseDetails.setCaseData(caseData);
        bulkDetails = new BulkDetails();
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseTypeId("Type1");
        bulkData = new BulkData();
        bulkDetails.setCaseData(bulkData);
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
        when(caseDataBuilder.buildCaseDataContent(eq(caseData), eq(ccdRequest), anyString())).thenReturn(CaseDataContent.builder().build());
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
        ccdClient.retrieveCase("authToken", caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), "111111");
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(SubmitEvent.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void retrieveCases() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        List<SubmitEvent> submitEvents = new ArrayList<>(Arrays.asList(new SubmitEvent(), new SubmitEvent()));
        ResponseEntity<List<SubmitEvent>> responseEntity = new ResponseEntity<>(submitEvents, HttpStatus.OK);
        PaginatedSearchMetadata metadata = new PaginatedSearchMetadata();
        metadata.setTotalPagesCount(1);
        ResponseEntity<PaginatedSearchMetadata> paginatedSearchMetadata = new ResponseEntity<>(metadata, HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildRetrieveCasesUrl(any(), any(), any(), any())).thenReturn(uri);
        when(ccdClientConfig.buildPaginationMetadataCaseUrl(any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(new ParameterizedTypeReference<List<SubmitEvent>>(){}))).thenReturn(responseEntity);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(PaginatedSearchMetadata.class))).thenReturn(paginatedSearchMetadata);
        ccdClient.retrieveCases("authToken", caseDetails.getCaseTypeId(), caseDetails.getJurisdiction());
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(new ParameterizedTypeReference<List<SubmitEvent>>(){}));
    }

    @Test
    public void retrieveCasesElasticSearch() throws IOException {
        String jsonQuery = "{\"size\":10000,\"query\":{\"terms\":{\"data.ethosCaseReference.keyword\":[\"2420117/2019\",\"2420118/2019\"],\"boost\":1.0}}}";
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonQuery, null);
        CaseSearchResult caseSearchResult = new CaseSearchResult(2L, Arrays.asList(new SubmitEvent(), new SubmitEvent()));
        ResponseEntity<CaseSearchResult> responseEntity = new ResponseEntity<>(caseSearchResult, HttpStatus.OK);
        when(ccdClientConfig.buildRetrieveCasesUrlElasticSearch(any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class))).thenReturn(responseEntity);
        ccdClient.retrieveCasesElasticSearch("authToken", caseDetails.getCaseTypeId(), new ArrayList<>(Arrays.asList("2420117/2019", "2420118/2019")));
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void retrieveBulkCasesElasticSearch() throws IOException {
        String jsonQuery = "{\"size\":10000,\"query\":{\"terms\":{\"data.multipleReference.keyword\":[\"2400001/2020\"],\"boost\":1.0}}}";
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonQuery, null);
        BulkCaseSearchResult bulkCaseSearchResult = new BulkCaseSearchResult(2L, Arrays.asList(new SubmitBulkEvent(), new SubmitBulkEvent()));
        ResponseEntity<BulkCaseSearchResult> responseEntity = new ResponseEntity<>(bulkCaseSearchResult, HttpStatus.OK);
        when(ccdClientConfig.buildRetrieveCasesUrlElasticSearch(any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(BulkCaseSearchResult.class))).thenReturn(responseEntity);
        ccdClient.retrieveBulkCasesElasticSearch("authToken", caseDetails.getCaseTypeId(), "2400001/2020");
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(BulkCaseSearchResult.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void retrieveCasesVenueAndRangeDateElasticSearch() throws IOException {
        String jsonQuery = "{\"size\":10000,\"query\":{\"bool\":{\"filter\":[{\"term\":{\"listedDate\":{\"value\":\"Manchester\",\"boost\":1.0}}}," +
                "{\"range\":{\"listedDate\":{\"from\":\"2019-09-23 18:30:00\",\"to\":\"2019-09-24 18:30:00\",\"include_lower\":true,\"include_upper\":true,\"boost\":1" +
                ".0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}";
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonQuery, null);
        CaseSearchResult caseSearchResult = new CaseSearchResult(2L, Arrays.asList(new SubmitEvent(), new SubmitEvent()));
        ResponseEntity<CaseSearchResult> responseEntity = new ResponseEntity<>(caseSearchResult, HttpStatus.OK);
        when(ccdClientConfig.buildRetrieveCasesUrlElasticSearch(any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class))).thenReturn(responseEntity);
        ccdClient.retrieveCasesVenueAndDateElasticSearch("authToken", caseDetails.getCaseTypeId(), "2019-09-23 18:30:00",
                "2019-09-24 18:30:00", "Manchester", "listedDate");
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void retrieveCasesVenueAndSingleDateElasticSearch() throws IOException {
        String jsonQuery = "{\"size\":10000,\"query\":{\"bool\":{\"filter\":[{\"term\":{\"listedDate\":{\"value\":\"Manchester\",\"boost\":1.0}}}," +
                "{\"range\":{\"listedDate\":{\"from\":\"2019-09-23 18:30:00\",\"to\":\"2019-09-23 18:30:00\",\"include_lower\":true,\"include_upper\":true,\"boost\":1" +
                ".0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}";
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonQuery, null);
        CaseSearchResult caseSearchResult = new CaseSearchResult(2L, Arrays.asList(new SubmitEvent(), new SubmitEvent()));
        ResponseEntity<CaseSearchResult> responseEntity = new ResponseEntity<>(caseSearchResult, HttpStatus.OK);
        when(ccdClientConfig.buildRetrieveCasesUrlElasticSearch(any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class))).thenReturn(responseEntity);
        ccdClient.retrieveCasesVenueAndDateElasticSearch("authToken", caseDetails.getCaseTypeId(), "2019-09-23 18:30:00",
                "2019-09-23 18:30:00", "Manchester", "listedDate");
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void retrieveCasesAllVenuesAndSingleDateElasticSearch() throws IOException {
        String jsonQuery = "{\"size\":10000,\"query\":{\"bool\":{\"filter\":[{\"range\":{\"listedDate\":{\"from\":\"2019-09-23 18:30:00\",\"to\":\"2019-09-23 " +
                "18:30:00\",\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}";
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonQuery, null);
        CaseSearchResult caseSearchResult = new CaseSearchResult(2L, Arrays.asList(new SubmitEvent(), new SubmitEvent()));
        ResponseEntity<CaseSearchResult> responseEntity = new ResponseEntity<>(caseSearchResult, HttpStatus.OK);
        when(ccdClientConfig.buildRetrieveCasesUrlElasticSearch(any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class))).thenReturn(responseEntity);
        ccdClient.retrieveCasesVenueAndDateElasticSearch("authToken", caseDetails.getCaseTypeId(), "2019-09-23 18:30:00",
                "2019-09-23 18:30:00", ALL_VENUES, ALL_VENUES);
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(CaseSearchResult.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void retrieveBulkCases() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        List<SubmitBulkEvent> submitBulkEvents = new ArrayList<>(Arrays.asList(new SubmitBulkEvent(), new SubmitBulkEvent()));
        ResponseEntity<List<SubmitBulkEvent>> responseEntity = new ResponseEntity<>(submitBulkEvents, HttpStatus.OK);
        PaginatedSearchMetadata metadata = new PaginatedSearchMetadata();
        metadata.setTotalPagesCount(1);
        ResponseEntity<PaginatedSearchMetadata> paginatedSearchMetadata = new ResponseEntity<>(metadata, HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildRetrieveCasesUrl(any(), any(), any(), any())).thenReturn(uri);
        when(ccdClientConfig.buildPaginationMetadataCaseUrl(any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(new ParameterizedTypeReference<List<SubmitBulkEvent>>(){}))).thenReturn(responseEntity);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(PaginatedSearchMetadata.class))).thenReturn(paginatedSearchMetadata);
        ccdClient.retrieveBulkCases("authToken", bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction());
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(new ParameterizedTypeReference<List<SubmitBulkEvent>>(){}));
    }

    @Test
    public void startEventForCase() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<CCDRequest> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildStartEventForCaseUrl(any(), any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class))).thenReturn(responseEntity);
        ccdClient.startEventForCase("authToken", caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), anyString());
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void startEventForCaseBulkSingle() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<CCDRequest> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildStartEventForCaseUrlBulkSingle(any(), any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class))).thenReturn(responseEntity);
        ccdClient.startEventForCaseBulkSingle("authToken", caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), anyString());
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void startEventForCasePreAcceptBulkSingle() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<CCDRequest> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildStartEventForCaseUrlPreAcceptBulkSingle(any(), any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class))).thenReturn(responseEntity);
        ccdClient.startEventForCasePreAcceptBulkSingle("authToken", caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), anyString());
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void startBulkEventForCase() throws IOException {
        HttpEntity<Object> httpEntity = new HttpEntity<>(null);
        ResponseEntity<CCDRequest> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildStartEventForBulkCaseUrl(any(), any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class))).thenReturn(responseEntity);
        ccdClient.startBulkEventForCase("authToken", bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction(), anyString());
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), eq(httpEntity), eq(CCDRequest.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void submitEventForCase() throws IOException {
        HttpEntity<CaseDataContent> httpEntity = new HttpEntity<>(CaseDataContent.builder().build(), null);
        ResponseEntity<SubmitEvent> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(caseDataBuilder.buildCaseDataContent(eq(caseData), eq(ccdRequest), anyString())).thenReturn(CaseDataContent.builder().build());
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildSubmitEventForCaseUrl(any(), any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class))).thenReturn(responseEntity);
        ccdClient.submitEventForCase("authToken", caseData, caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), ccdRequest, "111111");
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void submitBulkEventForCase() throws IOException {
        HttpEntity<CaseDataContent> httpEntity = new HttpEntity<>(CaseDataContent.builder().build(), null);
        ResponseEntity<SubmitBulkEvent> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(caseDataBuilder.buildBulkDataContent(eq(bulkData), eq(ccdRequest), anyString())).thenReturn(CaseDataContent.builder().build());
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        when(ccdClientConfig.buildSubmitEventForCaseUrl(any(), any(), any(), any())).thenReturn(uri);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitBulkEvent.class))).thenReturn(responseEntity);
        ccdClient.submitBulkEventForCase("authToken", bulkData, bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction(), ccdRequest, "111111");
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitBulkEvent.class));
        verifyNoMoreInteractions(restTemplate);
    }

}