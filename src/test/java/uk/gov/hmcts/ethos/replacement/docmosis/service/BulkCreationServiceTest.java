package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.CaseType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.MultipleType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringJUnit4ClassRunner.class)
public class BulkCreationServiceTest {

    @InjectMocks
    private BulkCreationService bulkCreationService;
    @Mock
    private CcdClient ccdClient;
    @Mock
    private MultipleReferenceService multipleReferenceService;
    @Mock
    private BulkSearchService bulkSearchService;
    private BulkRequest bulkRequest;
    private BulkRequest bulkRequest1;
    private SubmitEvent submitEvent;
    private SubmitEvent submitEvent1;
    private SubmitEvent submitEvent2;
    private SubmitEvent submitEvent3;
    private BulkCasesPayload bulkCasesPayload;

    private BulkDetails getBulkDetails(String lead, String caseIdSize) {
        BulkData bulkData = new BulkData();
        BulkDetails bulkDetails = new BulkDetails();
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseTypeId("Manchester_V3");
        bulkData.setCaseSource(MANUALLY_CREATED_POSITION);
        bulkData.setFeeGroupReference("111111");
        bulkData.setClaimantSurname("Fernandez");
        bulkData.setRespondentSurname("Mr Respondent");
        bulkData.setClaimantRep("Mike Johnson");
        bulkData.setRespondentRep("Juan Pedro");
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("AB");
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setValue(jurCodesType);
        bulkData.setJurCodesCollection(new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));
        bulkDetails.setCaseData(getSimpleBulkData(bulkData, lead, caseIdSize));
        return bulkDetails;
    }

    private BulkData getSimpleBulkData(BulkData bulkData, String lead, String caseIdSize) {
        CaseType caseType = new CaseType();
        caseType.setEthosCaseReference("1111");
        CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
        caseIdTypeItem.setId("1111");
        caseIdTypeItem.setValue(caseType);
        if (caseIdSize.equals("Complex")) {
            CaseType caseType1 = new CaseType();
            caseType1.setEthosCaseReference("1122");
            CaseIdTypeItem caseIdTypeItem1 = new CaseIdTypeItem();
            caseIdTypeItem1.setId("1122");
            caseIdTypeItem1.setValue(caseType1);
            bulkData.setCaseIdCollection(new ArrayList<>(Arrays.asList(caseIdTypeItem, caseIdTypeItem1)));
        } else {
            bulkData.setCaseIdCollection(new ArrayList<>(Collections.singletonList(caseIdTypeItem)));
        }
        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("281231");
        if (lead.equals("Yes")) {
            multipleType.setLeadClaimantM("Yes");
        } else {
            multipleType.setLeadClaimantM("No");
        }
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        multipleTypeItem.setValue(multipleType);
        multipleTypeItem.setId("22222");
        bulkData.setMultipleCollection(new ArrayList<>(Collections.singletonList(multipleTypeItem)));
        return bulkData;
    }

    private CaseData getCaseData(String ethosCaseRef) {
        CaseData caseData = new CaseData();
        caseData.setFeeGroupReference("111122211");
        caseData.setEthosCaseReference(ethosCaseRef);
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Fernandez");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Mr Respondent");
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));
        return caseData;
    }

    @Before
    public void setUp() {
        bulkRequest = new BulkRequest();
        bulkRequest.setCaseDetails(getBulkDetails("Yes", "Single"));
        bulkRequest1 = new BulkRequest();
        bulkRequest1.setCaseDetails(getBulkDetails("No Lead", "Complex"));
        submitEvent = new SubmitEvent();
        submitEvent.setState("Accepted");
        submitEvent.setCaseData(getCaseData("1111"));
        submitEvent1 = new SubmitEvent();
        submitEvent1.setState("Accepted");
        submitEvent1.setCaseData(getCaseData("281231"));
        submitEvent2 = new SubmitEvent();
        submitEvent2.setState("Accepted");
        submitEvent2.setCaseData(getCaseData("111111"));
        submitEvent3 = new SubmitEvent();
        submitEvent3.setState("Accepted");
        submitEvent3.setCaseData(getCaseData("1122"));
        bulkSearchService = new BulkSearchService(ccdClient, multipleReferenceService);
        bulkCreationService = new BulkCreationService(ccdClient, bulkSearchService);

        bulkCasesPayload = new BulkCasesPayload();
        bulkCasesPayload.setAlreadyTakenIds(new ArrayList<>());
        bulkCasesPayload.setSubmitEvents(new ArrayList<>(Arrays.asList(submitEvent, submitEvent2, submitEvent3)));
        bulkCasesPayload.setErrors(new ArrayList<>());
    }

    @Test(expected = Exception.class)
    public void caseCreationRequestException() throws IOException {
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenThrow(feignError());
        bulkSearchService.bulkCasesRetrievalRequest(getBulkDetails("Yes", "Single"), "authToken", true);
    }

    @Test
    public void caseCreationRequest() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequest(getBulkDetails("Yes", "Single"), "authToken", true);
        assertEquals(submitEventList, bulkCasesPayload.getSubmitEvents());
    }

    @Test
    public void caseCreationRequestWithCaseAlreadyAssigned() throws IOException {
        submitEvent.getCaseData().setMultipleReference("123345");
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequest(getBulkDetails("Yes", "Single"), "authToken", true);
        assertEquals(submitEventList, bulkCasesPayload.getSubmitEvents());
    }

    @Test
    public void caseCreationRequestWithEmptyCaseIds() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkDetails bulkDetails = getBulkDetails("Yes", "Single");
        bulkDetails.getCaseData().setCaseIdCollection(null);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequest(bulkDetails, "authToken", true);
        assertEquals("[]", bulkCasesPayload.getSubmitEvents().toString());
    }

    @Test(expected = Exception.class)
    public void caseCreationRequestExceptionElasticSearch() throws IOException {
        when(ccdClient.retrieveCasesElasticSearchForCreation(anyString(), anyString(), anyList(), anyString())).thenThrow(feignError());
        bulkSearchService.bulkCasesRetrievalRequestElasticSearch(getBulkDetails("Yes", "Single"), "authToken", true, true);
    }

    @Test
    public void caseCreationRequestElasticSearch() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCasesElasticSearchForCreation(anyString(), anyString(), anyList(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(getBulkDetails("Yes", "Single"), "authToken", true, true);
        assertEquals(submitEventList, bulkCasesPayload.getSubmitEvents());
    }

    @Test
    public void caseCreationRequestElasticSearchWithCaseSubmitted() throws IOException {
        submitEvent.setState(SUBMITTED_STATE);
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCasesElasticSearchForCreation(anyString(), anyString(), anyList(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(getBulkDetails("Yes", "Single"), "authToken", true, true);
        assertEquals("[The state of these cases: [1111] have not been accepted]", bulkCasesPayload.getErrors().toString());
    }

    @Test
    public void caseCreationRequestElasticSearchWithCaseAlreadyAssigned() throws IOException {
        submitEvent.getCaseData().setMultipleReference("123345");
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCasesElasticSearchForCreation(anyString(), anyString(), anyList(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(getBulkDetails("Yes", "Single"), "authToken", true, true);
        assertEquals("[These cases are already assigned to a multiple case: [1111]]", bulkCasesPayload.getErrors().toString());
    }

    @Test
    public void caseCreationRequestWithEmptyCaseIdsElasticSearch() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCasesElasticSearchForCreation(anyString(), anyString(), anyList(), anyString())).thenReturn(submitEventList);
        BulkDetails bulkDetails = getBulkDetails("Yes", "Single");
        bulkDetails.getCaseData().setCaseIdCollection(null);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(bulkDetails, "authToken", true, true);
        assertEquals("[]", bulkCasesPayload.getSubmitEvents().toString());
    }

    @Test
    public void caseCreationRequestComplexCase() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Arrays.asList(submitEvent, submitEvent2, submitEvent3));
        String expectedResult = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, leadClaimantM=Yes, multipleReferenceM=null, " +
                "clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , receiptDateM= , " +
                "positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , stateM=Accepted, subMultipleM= , subMultipleTitleM= , currentPositionM= , " +
                "claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , respondentRepOrgM= , " +
                "claimantRepOrgM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=111111, leadClaimantM=No, multipleReferenceM=null, " +
                "clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , receiptDateM= , " +
                "positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , stateM=Accepted, subMultipleM= , subMultipleTitleM= , currentPositionM= , " +
                "claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , respondentRepOrgM= , " +
                "claimantRepOrgM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1122, leadClaimantM=No, multipleReferenceM=null, " +
                "clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , receiptDateM= , " +
                "positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , stateM=Accepted, subMultipleM= , subMultipleTitleM= , currentPositionM= , " +
                "claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , respondentRepOrgM= , " +
                "claimantRepOrgM= ))]";
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest1, "authToken");
        assertEquals(expectedResult, bulkCasesPayload.getMultipleTypeItems().toString());
    }

    @Test
    public void updateBulkRequest() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        String expectedResult = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, leadClaimantM=Yes, multipleReferenceM=null, " +
                "clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , receiptDateM= , " +
                "positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , stateM=Accepted, subMultipleM= , subMultipleTitleM= , currentPositionM= , " +
                "claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , respondentRepOrgM= , " +
                "claimantRepOrgM= ))]";
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
        assertEquals(expectedResult, bulkCasesPayload.getMultipleTypeItems().toString());
    }

    @Test
    public void updateBulkRequestUpdateFlagNoFreeSingleCases() throws IOException {
        submitEvent.getCaseData().setMultipleReference("111111111");
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
        assertNull(bulkCasesPayload.getMultipleTypeItems());
    }

    @Test
    public void updateBulkRequestPendingStateException() throws IOException {
        submitEvent.setState(PENDING_STATE);
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(submitEventList);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenThrow(feignError());
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
        assertEquals("[The state of these cases: [1111] have not been accepted]", bulkCasesPayload.getErrors().toString());
    }

    @Test
    public void updateBulkRequestDeletions() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Arrays.asList(submitEvent, submitEvent1));
        String expectedResult = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, leadClaimantM=Yes, " +
                "multipleReferenceM=null, clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , " +
                "respondentRepM= , fileLocM= , receiptDateM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , " +
                "stateM=Accepted, subMultipleM= , subMultipleTitleM= , currentPositionM= , claimantAddressLine1M= , claimantPostCodeM= , " +
                "respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , respondentRepOrgM= , claimantRepOrgM= ))]";
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
        assertEquals(expectedResult, bulkCasesPayload.getMultipleTypeItems().toString());
    }

    @Test
    public void updateBulkRequestException() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenThrow(feignError());
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
        assertEquals("[]", bulkCasesPayload.getErrors().toString());
    }

    @Test
    public void bulkCreationLogic() {
        String result = "BulkDetails(super=GenericCaseDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseTypeId=Manchester_V3, createdDate=null, " +
                "lastModified=null, dataClassification=null), caseData=BulkData(bulkCaseTitle=null, multipleReference=null, feeGroupReference=111111, " +
                "claimantSurname=Fernandez, respondentSurname=Mr Respondent, claimantRep=Mike Johnson, respondentRep=Juan Pedro, ethosCaseReference=null, " +
                "clerkResponsible=null, fileLocation=null, jurCodesCollection=[JurCodesTypeItem(id=null, value=JurCodesType(juridictionCodesList=AB, " +
                "judgmentOutcome=null, juridictionCodesSubList1=null))], fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, " +
                "respondentSurnameV2=null, multipleReferenceV2=null, clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, " +
                "fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, " +
                "subMultipleRef=null, caseIdCollection=[CaseIdTypeItem(id=1111, value=CaseType(ethosCaseReference=1111))], searchCollection=null, " +
                "midSearchCollection=null, multipleCollection=[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, leadClaimantM=No, " +
                "multipleReferenceM= , clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , " +
                "receiptDateM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , stateM=Accepted, subMultipleM= , subMultipleTitleM= , " +
                "currentPositionM= , claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , " +
                "respondentRepOrgM= , claimantRepOrgM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=111111, leadClaimantM=No, " +
                "multipleReferenceM= , clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , " +
                "receiptDateM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , stateM=Accepted, subMultipleM= , subMultipleTitleM= , " +
                "currentPositionM= , claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , " +
                "respondentRepOrgM= , claimantRepOrgM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1122, leadClaimantM=No, " +
                "multipleReferenceM= , clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , " +
                "receiptDateM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , stateM=Accepted, subMultipleM= , subMultipleTitleM= , " +
                "currentPositionM= , claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , respondentPostCodeM= , flag1M= , flag2M= , EQPM= , " +
                "respondentRepOrgM= , claimantRepOrgM= ))], subMultipleCollection=null, subMultipleDynamicList=null, searchCollectionCount=null, " +
                "multipleCollectionCount=3, correspondenceType=null, correspondenceScotType=null, selectAll=null, scheduleDocName=null, positionType=null, flag1=null, " +
                "flag2=null, EQP=null, submissionRef=null, claimantOrg=null, respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, " +
                "jurCodesDynamicList=null, outcomeUpdate=null, filterCases=null, docMarkUp=null, caseSource=Manually Created))";
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkCreationLogic(getBulkDetails("Yes", "Single"),
                bulkCasesPayload, "authToken", false);
        assertEquals(result, bulkRequestPayload.getBulkDetails().toString());
    }

    @Test
    public void bulkCreationLogicAfterSubmittedCallback() {
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkCreationLogic(getBulkDetails("Yes", "Single"),
                bulkCasesPayload, "authToken", true);
        assertNull(bulkRequestPayload.getBulkDetails().getCaseData().getMultipleReference());
    }

    @Test
    public void bulkCreationLogicWithErrors() {
        bulkCasesPayload.setErrors(new ArrayList<>(Collections.singleton("Errors")));
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkCreationLogic(getBulkDetails("Yes", "Single"),
                bulkCasesPayload, "authToken", true);
        assertEquals("[Errors]", bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void bulkUpdateCaseIdsLogic() {
        String result = "BulkDetails(super=GenericCaseDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseTypeId=Manchester_V3, createdDate=null, " +
                "lastModified=null, dataClassification=null), caseData=BulkData(bulkCaseTitle=null, multipleReference=null, feeGroupReference=111111, " +
                "claimantSurname=Fernandez, respondentSurname=Mr Respondent, claimantRep=Mike Johnson, respondentRep=Juan Pedro, ethosCaseReference=null, " +
                "clerkResponsible=null, fileLocation=null, jurCodesCollection=[JurCodesTypeItem(id=null, value=JurCodesType(juridictionCodesList=AB, " +
                "judgmentOutcome=null, juridictionCodesSubList1=null))], fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, " +
                "respondentSurnameV2=null, multipleReferenceV2=null, clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, " +
                "fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, " +
                "subMultipleName=null, subMultipleRef=null, caseIdCollection=[], searchCollection=[], midSearchCollection=null, " +
                "multipleCollection=[MultipleTypeItem(id=22222, value=MultipleType(caseIDM=null, ethosCaseReferenceM=281231, leadClaimantM=Yes, " +
                "multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, claimantRepM=null, respondentRepM=null, " +
                "fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null, subMultipleM=null, " +
                "subMultipleTitleM=null, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, respondentAddressLine1M=null, " +
                "respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], subMultipleCollection=null, " +
                "subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=1, correspondenceType=null, correspondenceScotType=null, " +
                "selectAll=null, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, claimantOrg=null, " +
                "respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, caseSource=Manually Created))";
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkUpdateCaseIdsLogic(bulkRequest, "authToken");
        assertEquals(result, bulkRequestPayload.getBulkDetails().toString());
    }

    private BulkDetails getBulkDetailsForLead() {
        BulkDetails bulkDetails = new BulkDetails();
        BulkData bulkData = new BulkData();
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("111");
        multipleType.setLeadClaimantM("Yes");
        multipleType.setCaseIDM("121212");
        multipleTypeItem.setId("22222");
        multipleTypeItem.setValue(multipleType);
        CaseType caseType = new CaseType();
        caseType.setEthosCaseReference("111");
        CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
        caseIdTypeItem.setId("11111");
        caseIdTypeItem.setValue(caseType);
        bulkData.setCaseIdCollection(new ArrayList<>(Collections.singletonList(caseIdTypeItem)));
        bulkData.setMultipleCollection(new ArrayList<>(Collections.singletonList(multipleTypeItem)));
        bulkDetails.setCaseData(bulkData);
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseTypeId(Constants.MANCHESTER_BULK_CASE_TYPE_ID);
        return bulkDetails;
    }

    @Test
    public void retrievalCasesForPreAcceptRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), any())).thenReturn(submitEventList);
        List<SubmitEvent> casesForPreAcceptRequest = bulkSearchService.retrievalCasesForPreAcceptRequest(getBulkDetails("Yes", "Single"), "authToken");
        assertEquals(submitEventList, casesForPreAcceptRequest);
    }

    @Test
    public void retrievalCasesForPreAcceptRequestWithEmptyCaseIds() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkDetails bulkDetails = getBulkDetails("Yes", "Single");
        bulkDetails.getCaseData().setCaseIdCollection(null);
        List<SubmitEvent> casesForPreAcceptRequest = bulkSearchService.retrievalCasesForPreAcceptRequest(bulkDetails, "authToken");
        assertEquals("[]", casesForPreAcceptRequest.toString());
    }

    @Test(expected = Exception.class)
    public void retrievalCasesForPreAcceptRequestException() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), any())).thenThrow(feignError());
        bulkSearchService.retrievalCasesForPreAcceptRequest(getBulkDetails("Yes", "Single"), "authToken");
    }

}