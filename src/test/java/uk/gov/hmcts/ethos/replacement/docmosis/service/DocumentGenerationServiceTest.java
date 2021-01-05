package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_BULK_CASE_TYPE_ID;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentGenerationServiceTest {

    @InjectMocks
    private DocumentGenerationService documentGenerationService;
    @Mock
    private TornadoService tornadoService;
    private CaseDetails caseDetailsScot1;
    private CaseDetails caseDetails9;
    private CaseDetails caseDetails10;
    private CaseDetails caseDetails11;
    private CaseDetails caseDetails12;
    private CaseDetails caseDetails13;
    private CaseDetails caseDetails14;
    private CaseDetails caseDetails15;
    private CCDRequest ccdRequest;
    private BulkRequest bulkRequest;
    private DocumentInfo documentInfo;
    private BulkDocumentInfo bulkDocumentInfo;
    @Mock
    private CcdClient ccdClient;

    @Before
    public void setUp() throws Exception {
        caseDetailsScot1 = generateCaseDetails("caseDetailsScotTest1.json");
        caseDetails9 = generateCaseDetails("caseDetailsTest9.json");
        caseDetails10 = generateCaseDetails("caseDetailsTest10.json");
        caseDetails11 = generateCaseDetails("caseDetailsTest11.json");
        caseDetails12 = generateCaseDetails("caseDetailsTest12.json");
        caseDetails13 = generateCaseDetails("caseDetailsTest13.json");
        caseDetails14 = generateCaseDetails("caseDetailsTest14.json");
        caseDetails15 = generateCaseDetails("caseDetailsTest15.json");

        ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
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

    @Test
    public void midAddressLabelsInvalidTemplateName() {
        CaseData caseData = caseDetails9.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        assertNull(caseData.getAddressLabelCollection());
    }

    @Test
    public void midAddressLabelsCustomiseSelectedAddresses() {
        CaseData caseData = caseDetails10.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        assertEquals(5, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsCustomiseSelectedAddressesNoAddressLabelsSelectionFields() {
        CaseData caseData = caseDetails11.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        assertNull(caseData.getAddressLabelCollection());
    }

    @Test
    public void midAddressLabelsAllAvailableAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        assertEquals("Individual", caseData.getClaimantTypeOfClaimant());
        assertEquals("CLAIMANT : Mr A J Rodriguez", caseData.getAddressLabelCollection().get(0).getValue().getFullName());
        assertEquals(6, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsAllAvailableAddressesMissingEntities() {
        CaseData caseData = caseDetails13.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        assertEquals("Company", caseData.getClaimantTypeOfClaimant());
        assertEquals("CLAIMANT : Orlando LTD", caseData.getAddressLabelCollection().get(0).getValue().getFullName());
        assertEquals(1, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsAllAvailableAddressesMissingClaimantType() {
        CaseData caseData = caseDetails14.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        assertEquals("Individual", caseData.getClaimantTypeOfClaimant());
        assertEquals("CLAIMANT : Mr A J Rodriguez", caseData.getAddressLabelCollection().get(0).getValue().getFullName());
        assertEquals(6, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsAllAvailableAddressesMissingClaimantRepFields() {
        CaseData caseData = caseDetails15.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        assertEquals("Individual", caseData.getClaimantTypeOfClaimant());
        assertEquals("CLAIMANT : Mr A J Rodriguez", caseData.getAddressLabelCollection().get(0).getValue().getFullName());
        assertEquals(6, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsClaimantAddress() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.3");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(1, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsClaimantRepAddress() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.4");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(1, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsClaimantAndClaimantRepAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.5");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(2, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsRespondentsAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.6");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(3, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsRespondentsRepsAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.7");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(1, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsRespondentsAndRespondentsRepsAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.8");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(4, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsClaimantAndRespondentsAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.9");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(4, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsClaimantRepAndRespondentsRepsAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.10");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(2, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsClaimantAndRespondentsRepsAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.11");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(2, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midAddressLabelsClaimantRepAndRespondentsAddresses() {
        CaseData caseData = caseDetails12.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.12");
        documentGenerationService.midAddressLabels(caseData);
        caseData.getCorrespondenceType().setPart0Documents("0.2");
        assertEquals(4, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midSelectedAddressLabelsNullCollection() {
        CaseData caseData = caseDetails11.getCaseData();
        documentGenerationService.midSelectedAddressLabels(caseData);
        assertNull(caseData.getAddressLabelCollection());
    }

    @Test
    public void midSelectedAddressLabelsFullCollection() {
        CaseData caseData = caseDetails12.getCaseData();
        documentGenerationService.midAddressLabels(caseData);
        documentGenerationService.midSelectedAddressLabels(caseData);
        assertEquals(6, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midSelectedAddressLabelsEmptyCollection() {
        CaseData caseData = caseDetails13.getCaseData();
        caseData.getCorrespondenceType().setPart0Documents("0.4");
        documentGenerationService.midAddressLabels(caseData);
        documentGenerationService.midSelectedAddressLabels(caseData);
        assertEquals(0, caseData.getAddressLabelCollection().size());
    }

    @Test
    public void midValidateAddressLabelsNoErrors() {
        CaseData caseData = caseDetails10.getCaseData();
        List<String> errors = documentGenerationService.midValidateAddressLabels(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    public void midValidateAddressLabelsWithErrors() {
        CaseData caseData = caseDetails11.getCaseData();
        List<String> errors = documentGenerationService.midValidateAddressLabels(caseData);
        assertEquals(2, errors.size());
    }

    @Test
    public void clearUserChoicesScotland() {
        CaseDetails caseDetails = caseDetailsScot1;
        caseDetails.setCaseTypeId("Scotland");
        documentGenerationService.clearUserChoices(caseDetails);
        assertNull(caseDetails.getCaseData().getCorrespondenceScotType());
        assertNull(caseDetails.getCaseData().getAddressLabelsSelectionType());
        assertNull(caseDetails.getCaseData().getAddressLabelCollection());
        assertNull(caseDetails.getCaseData().getAddressLabelsAttributesType());
    }

    @Test
    public void clearUserChoicesEngland() {
        CaseDetails caseDetails = caseDetails9;
        caseDetails.setCaseTypeId("Not Scotland");
        documentGenerationService.clearUserChoices(caseDetails);
        assertNull(caseDetails.getCaseData().getCorrespondenceType());
        assertNull(caseDetails.getCaseData().getAddressLabelsSelectionType());
        assertNull(caseDetails.getCaseData().getAddressLabelCollection());
        assertNull(caseDetails.getCaseData().getAddressLabelsAttributesType());
    }

    @Test
    public void clearUserChoicesForMultiplesScotland() {
        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
        bulkDetails.setCaseTypeId("Scotland_Multiple");
        documentGenerationService.clearUserChoicesForMultiples (bulkDetails);
        assertNull(bulkDetails.getCaseData().getCorrespondenceScotType());
    }

    @Test
    public void clearUserChoicesForMultiplesEngland() {
        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
        bulkDetails.setCaseTypeId("Not Scotland Multiple");
        documentGenerationService.clearUserChoicesForMultiples (bulkDetails);
        assertNull(bulkDetails.getCaseData().getCorrespondenceType());
    }

    @Test
    public void processDocumentRequest() throws IOException {
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any())).thenReturn(documentInfo);
        DocumentInfo documentInfo1 = documentGenerationService.processDocumentRequest(ccdRequest, "authToken");
        assertEquals(documentInfo, documentInfo1);
    }

    @Test(expected = Exception.class)
    public void processDocumentRequestException() throws IOException {
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any())).thenThrow(new RuntimeException());
        documentGenerationService.processDocumentRequest(ccdRequest, "authToken");
    }

    @Test
    public void processBulkDocumentRequest() throws IOException {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(1);
        submitEvent.setCaseData(new CaseData());
        List<SubmitEvent> submitEvents = Collections.singletonList(submitEvent);
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any())).thenReturn(documentInfo);
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
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any())).thenReturn(documentInfo);
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
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any())).thenThrow(new RuntimeException());
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

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }
}