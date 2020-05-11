package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDocumentInfo;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.SearchType;
import uk.gov.hmcts.ecm.common.model.ccd.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_BULK_CASE_TYPE_ID;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentGenerationServiceTest {

    @InjectMocks
    private DocumentGenerationService documentGenerationService;
    @Mock
    private TornadoService tornadoService;
    private CCDRequest ccdRequest;
    private BulkRequest bulkRequest;
    private DocumentInfo documentInfo;
    private BulkDocumentInfo bulkDocumentInfo;
    @Mock
    private CcdClient ccdClient;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        caseDetails.setCaseData(caseData);
        ccdRequest.setCaseDetails(caseDetails);
        bulkRequest = new BulkRequest();
        BulkDetails bulkDetails = new BulkDetails();
        BulkData bulkData = new BulkData();
        SearchType searchType = new SearchType();
        searchType.setCaseIDS("1");
        SearchTypeItem searchTypeItem = new SearchTypeItem();
        searchTypeItem.setValue(searchType);
        bulkData.setSearchCollection(new ArrayList<>(Collections.singletonList(searchTypeItem)));
        bulkDetails.setCaseData(bulkData);
        bulkDetails.setCaseTypeId(MANCHESTER_DEV_BULK_CASE_TYPE_ID);
        bulkRequest.setCaseDetails(bulkDetails);
        documentGenerationService = new DocumentGenerationService(tornadoService, ccdClient);
        documentInfo = DocumentInfo.builder().description("resources/exampleV1.json").build();
        documentInfo.setMarkUp("Markup");
        documentInfo.setType("Document");
        documentInfo.setUrl("http://google.com");
        bulkDocumentInfo = new BulkDocumentInfo();
        bulkDocumentInfo.setMarkUps(documentInfo.getMarkUp());
        bulkDocumentInfo.setErrors(new ArrayList<>());
    }

    @Test(expected = Exception.class)
    public void processDocumentRequestException() throws IOException {
        when(tornadoService.documentGeneration(anyString(), any())).thenThrow(new RuntimeException());
        documentGenerationService.processDocumentRequest(ccdRequest, "authToken");
    }

    @Test
    public void processDocumentRequest() throws IOException {
        when(tornadoService.documentGeneration(anyString(), any())).thenReturn(documentInfo);
        DocumentInfo documentInfo1 = documentGenerationService.processDocumentRequest(ccdRequest, "authToken");
        assertEquals(documentInfo, documentInfo1);
    }

    @Test
    public void processBulkDocumentRequest() throws IOException {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(1);
        submitEvent.setCaseData(new CaseData());
        List<SubmitEvent> submitEvents = Collections.singletonList(submitEvent);
        when(tornadoService.documentGeneration(anyString(), any())).thenReturn(documentInfo);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), any(), any())).thenReturn(submitEvents);
        BulkDocumentInfo bulkDocumentInfo1 = documentGenerationService.processBulkDocumentRequest(bulkRequest, "authToken");
        assertEquals(bulkDocumentInfo.toString(), bulkDocumentInfo1.toString());
    }

    @Test
    public void processBulkDocumentRequestWithErrors() throws IOException {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(1);
        submitEvent.setCaseData(new CaseData());
        bulkRequest.getCaseDetails().getCaseData().setSearchCollection(null);
        List<SubmitEvent> submitEvents = Collections.singletonList(submitEvent);
        when(tornadoService.documentGeneration(anyString(), any())).thenReturn(documentInfo);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), any(), any())).thenReturn(submitEvents);

        BulkDocumentInfo bulkDocumentInfo1 = documentGenerationService.processBulkDocumentRequest(bulkRequest, "authToken");
        assertEquals("BulkDocumentInfo(markUps=, errors=[There are not cases searched to " +
                "generate letters], documentInfo=null)", bulkDocumentInfo1.toString());
    }

    @Test(expected = Exception.class)
    public void processBulkDocumentRequestException() throws IOException {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(1);
        submitEvent.setCaseData(new CaseData());
        List<SubmitEvent> submitEvents = Collections.singletonList(submitEvent);
        when(tornadoService.documentGeneration(anyString(), any())).thenThrow(new RuntimeException());
        when(ccdClient.retrieveCasesElasticSearch(anyString(), any(), any())).thenReturn(submitEvents);

        documentGenerationService.processBulkDocumentRequest(bulkRequest, "authToken");
    }

    @Test
    public void processBulkScheduleRequest() throws IOException {
        when(tornadoService.scheduleGeneration(anyString(), any())).thenReturn(documentInfo);
        BulkDocumentInfo bulkDocumentInfo1 = documentGenerationService.processBulkScheduleRequest(bulkRequest, "authToken");
        assertEquals("BulkDocumentInfo(markUps=Markup, errors=[], documentInfo=DocumentInfo(type=Document, " +
                "description=resources/exampleV1.json, url=http://google.com, markUp=Markup))", bulkDocumentInfo1.toString());
    }

    @Test
    public void processBulkScheduleRequestWithErrors() throws IOException {
        bulkRequest.getCaseDetails().getCaseData().setSearchCollection(null);
        when(tornadoService.scheduleGeneration(anyString(), any())).thenReturn(documentInfo);
        BulkDocumentInfo bulkDocumentInfo1 = documentGenerationService.processBulkScheduleRequest(bulkRequest, "authToken");
        assertEquals("BulkDocumentInfo(markUps= , errors=[There are not cases searched to generate schedules], " +
                "documentInfo=DocumentInfo(type=null, description=null, url=null, markUp=null))", bulkDocumentInfo1.toString());
    }

    @Test(expected = Exception.class)
    public void processBulkScheduleRequestException() throws IOException {
        when(tornadoService.scheduleGeneration(anyString(), any())).thenThrow(new RuntimeException());
        documentGenerationService.processBulkScheduleRequest(bulkRequest, "authToken");
    }
}