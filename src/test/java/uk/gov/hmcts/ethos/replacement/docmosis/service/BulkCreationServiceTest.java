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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.PENDING_STATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringJUnit4ClassRunner.class)
public class BulkCreationServiceTest {

    @InjectMocks
    private BulkCreationService bulkCreationService;
    @Mock
    private CcdClient ccdClient;
    @Mock
    private BulkUpdateService bulkUpdateService;
    @Mock
    private BulkSearchService bulkSearchService;
    private BulkRequest bulkRequest;
    private BulkRequest bulkRequest1;
    private BulkRequest bulkRequest2;
    private SubmitEvent submitEvent;
    private SubmitEvent submitEvent1;
    private SubmitEvent submitEvent2;
    private SubmitEvent submitEvent3;
    private BulkCasesPayload bulkCasesPayload;
    private BulkCasesPayload bulkCasesPayloadWithErrors;
    private BulkRequestPayload bulkRequestPayload;

    private BulkDetails getBulkDetails(String lead, String caseIdSize) {
        BulkData bulkData = new BulkData();
        BulkDetails bulkDetails = new BulkDetails();
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseTypeId("Manchester_V3");
        bulkData.setFeeGroupReference("111111");
        bulkData.setClaimantSurname("Fernandez");
        bulkData.setRespondentSurname("Mr Respondent");
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
        caseData.setRespondentSumType(respondentSumType);
        return caseData;
    }

    @Before
    public void setUp() {
        bulkRequest = new BulkRequest();
        bulkRequest.setCaseDetails(getBulkDetails("Yes", "Single"));
        bulkRequest1 = new BulkRequest();
        bulkRequest1.setCaseDetails(getBulkDetails("No Lead", "Single"));
        bulkRequest2 = new BulkRequest();
        bulkRequest2.setCaseDetails(getBulkDetails("No Lead", "Complex"));
        submitEvent = new SubmitEvent();
        submitEvent.setState("1_Submitted");
        submitEvent.setCaseData(getCaseData("1111"));
        submitEvent1 = new SubmitEvent();
        submitEvent1.setState("1_Submitted");
        submitEvent1.setCaseData(getCaseData("281231"));
        submitEvent2 = new SubmitEvent();
        submitEvent2.setState("1_Submitted");
        submitEvent2.setCaseData(getCaseData("111111"));
        submitEvent3 = new SubmitEvent();
        submitEvent3.setState("1_Submitted");
        submitEvent3.setCaseData(getCaseData("1122"));
        bulkSearchService = new BulkSearchService(ccdClient);
        bulkCreationService = new BulkCreationService(ccdClient, bulkUpdateService, bulkSearchService);

        bulkCasesPayload = new BulkCasesPayload();
        bulkCasesPayloadWithErrors = new BulkCasesPayload();
        bulkCasesPayloadWithErrors.setDuplicateIds(new ArrayList<>(Arrays.asList("1", "2")));
        bulkCasesPayload.setDuplicateIds(new ArrayList<>());
        bulkCasesPayload.setSubmitEvents(new ArrayList<>(Arrays.asList(submitEvent, submitEvent2, submitEvent3)));

        bulkRequestPayload = new BulkRequestPayload();
        bulkRequestPayload.setErrors(new ArrayList<>());
        bulkRequestPayload.setBulkDetails(getBulkDetailsForLead());
    }

    @Test(expected = Exception.class)
    public void caseCreationRequestException() throws IOException {
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenThrow(feignError());
        bulkSearchService.bulkCasesRetrievalRequest(getBulkDetails("Yes", "Single"), "authToken");
    }

    @Test
    public void caseCreationRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequest(getBulkDetails("Yes", "Single"), "authToken");
        assertEquals(submitEventList, bulkCasesPayload.getSubmitEvents());
    }

    @Test
    public void caseCreationRequestComplexCase() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Arrays.asList(submitEvent, submitEvent2, submitEvent3));
        String expectedResult = "[MultipleTypeItem(id=22222, value=MultipleType(caseIDM=null, ethosCaseReferenceM=281231, leadClaimantM=No, " +
                "multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, claimantRepM=null, " +
                "respondentRepM=null, fileLocM=null, receiptDateM=null, acasOfficeM=null, positionTypeM=null, feeGroupReferenceM=null, " +
                "jurCodesCollectionM=null, stateM=null)), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, " +
                "leadClaimantM=null, multipleReferenceM=null, clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, " +
                "claimantRepM= , respondentRepM= , fileLocM= , receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM=111122211, " +
                "jurCodesCollectionM= , stateM=1_Submitted)), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1122, " +
                "leadClaimantM=null, multipleReferenceM=null, clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, " +
                "claimantRepM= , respondentRepM= , fileLocM= , receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM=111122211, " +
                "jurCodesCollectionM= , stateM=1_Submitted))]";
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest2, "authToken");
        assertEquals(expectedResult, bulkCasesPayload.getMultipleTypeItems().toString());
    }

    @Test
    public void updateBulkRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        String expectedResult = "[MultipleTypeItem(id=22222, value=MultipleType(caseIDM=null, ethosCaseReferenceM=281231, " +
                "leadClaimantM=Yes, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, " +
                "claimantRepM=null, respondentRepM=null, fileLocM=null, receiptDateM=null, acasOfficeM=null, positionTypeM=null, " +
                "feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null)), MultipleTypeItem(id=0, " +
                "value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, leadClaimantM=null, multipleReferenceM=null, " +
                "clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , " +
                "fileLocM= , receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , " +
                "stateM=1_Submitted))]";
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
        assertEquals(expectedResult, bulkCasesPayload.getMultipleTypeItems().toString());
    }

    @Test
    public void updateBulkRequestDeletions() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Arrays.asList(submitEvent, submitEvent1));
        String expectedResult = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, " +
                "leadClaimantM=null, multipleReferenceM=null, clerkRespM= , claimantSurnameM=Fernandez, " +
                "respondentSurnameM=Mr Respondent, claimantRepM= , respondentRepM= , fileLocM= , receiptDateM= , " +
                "acasOfficeM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , " +
                "stateM=1_Submitted))]";
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenReturn(submitEventList);
        BulkCasesPayload bulkCasesPayload = bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
        assertEquals(expectedResult, bulkCasesPayload.getMultipleTypeItems().toString());
    }

    @Test(expected = Exception.class)
    public void updateBulkRequestException() throws IOException {
        when(ccdClient.retrieveCases(anyString(), anyString(), anyString())).thenThrow(feignError());
        bulkCreationService.updateBulkRequest(bulkRequest, "authToken");
    }

    @Test
    public void bulkCreationLogicWithErrors() {
        String result = "[These cases are already assigned to a multiple bulk: [1, 2]]";
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkCreationLogic(getBulkDetails("Yes", "Single"),
                bulkCasesPayloadWithErrors, "authToken");
        assertEquals(result, bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void bulkCreationLogic() {
        String result = "BulkRequestPayload(errors=[], bulkDetails=BulkDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, " +
                "caseData=BulkData(bulkCaseTitle=null, multipleReference=null, feeGroupReference=111111, claimantSurname=Fernandez, " +
                "respondentSurname=Mr Respondent, ethosCaseReference=null, clerkResponsible=null, fileLocation=null, " +
                "jurCodesCollection=[JurCodesTypeItem(id=null, value=JurCodesType(juridictionCodesList=AB, juridictionCodesSubList1=null))], " +
                "fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, " +
                "caseIdCollection=[CaseIdTypeItem(id=1111, value=CaseType(ethosCaseReference=1111))], searchCollection=null, " +
                "multipleCollection=[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1111, leadClaimantM=null, " +
                "multipleReferenceM= , clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , " +
                "respondentRepM= , fileLocM= , receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM=111122211, " +
                "jurCodesCollectionM= , stateM=1_Submitted)), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=111111, " +
                "leadClaimantM=null, multipleReferenceM= , clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , " +
                "respondentRepM= , fileLocM= , receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , " +
                "stateM=1_Submitted)), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=1122, leadClaimantM=null, " +
                "multipleReferenceM= , clerkRespM= , claimantSurnameM=Fernandez, respondentSurnameM=Mr Respondent, claimantRepM= , " +
                "respondentRepM= , fileLocM= , receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM=111122211, jurCodesCollectionM= , " +
                "stateM=1_Submitted))], searchCollectionCount=null, multipleCollectionCount=3), caseTypeId=Manchester_V3, createdDate=null, " +
                "lastModified=null, dataClassification=null))";
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkCreationLogic(getBulkDetails("Yes", "Single"),
                bulkCasesPayload, "authToken");
        assertEquals(result, bulkRequestPayload.toString());
    }

    @Test
    public void bulkUpdateCaseIdsLogic() {
        String result = "BulkRequestPayload(errors=[], bulkDetails=BulkDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, " +
                "caseData=BulkData(bulkCaseTitle=null, multipleReference=null, feeGroupReference=111111, claimantSurname=Fernandez, " +
                "respondentSurname=Mr Respondent, ethosCaseReference=null, clerkResponsible=null, fileLocation=null, " +
                "jurCodesCollection=[JurCodesTypeItem(id=null, value=JurCodesType(juridictionCodesList=AB, juridictionCodesSubList1=null))], " +
                "fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, " +
                "caseIdCollection=[CaseIdTypeItem(id=1111, value=CaseType(ethosCaseReference=1111))], searchCollection=[], " +
                "multipleCollection=[MultipleTypeItem(id=22222, value=MultipleType(caseIDM=null, ethosCaseReferenceM=281231, " +
                "leadClaimantM=Yes, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, " +
                "claimantRepM=null, respondentRepM=null, fileLocM=null, receiptDateM=null, acasOfficeM=null, positionTypeM=null, " +
                "feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null))], searchCollectionCount=null, multipleCollectionCount=1), " +
                "caseTypeId=Manchester_V3, createdDate=null, lastModified=null, dataClassification=null))";
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkUpdateCaseIdsLogic(bulkRequest, "authToken");
        assertEquals(result, bulkRequestPayload.toString());
    }

    @Test
    public void updateLeadCase() throws IOException {
        when(ccdClient.retrieveCase(anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvent);
        String result = "[MultipleTypeItem(id=22222, value=MultipleType(caseIDM=121212, ethosCaseReferenceM=111, " +
                "leadClaimantM=Yes, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, " +
                "claimantRepM=null, respondentRepM=null, fileLocM=null, receiptDateM=null, acasOfficeM=null, positionTypeM=null, " +
                "feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null))]";
        BulkRequestPayload bulkRequestPayloadResult = bulkCreationService.updateLeadCase(bulkRequestPayload, "authToken");
        assertEquals(result, bulkRequestPayloadResult.getBulkDetails().getCaseData().getMultipleCollection().toString());
    }

    @Test
    public void updateLeadCaseNoLead() throws IOException {
        when(ccdClient.retrieveCase(anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvent);
        bulkRequestPayload.getBulkDetails().getCaseData().getCaseIdCollection().get(0).getValue().setEthosCaseReference("2222");
        String result = "[MultipleTypeItem(id=22222, value=MultipleType(caseIDM=121212, ethosCaseReferenceM=111, " +
                "leadClaimantM=No, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, " +
                "claimantRepM=null, respondentRepM=null, fileLocM=null, receiptDateM=null, acasOfficeM=null, positionTypeM=null, " +
                "feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null))]";
        BulkRequestPayload bulkRequestPayloadResult = bulkCreationService.updateLeadCase(bulkRequestPayload, "authToken");
        assertEquals(result, bulkRequestPayloadResult.getBulkDetails().getCaseData().getMultipleCollection().toString());
    }

    @Test
    public void updateLeadCasePendingState() throws IOException {
        submitEvent.setState(PENDING_STATE);
        when(ccdClient.retrieveCase(anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvent);
        String result = "[MultipleTypeItem(id=22222, value=MultipleType(caseIDM=121212, ethosCaseReferenceM=111, " +
                "leadClaimantM=Yes, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, " +
                "claimantRepM=null, respondentRepM=null, fileLocM=null, receiptDateM=null, acasOfficeM=null, positionTypeM=null, " +
                "feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null))]";
        BulkRequestPayload bulkRequestPayloadResult = bulkCreationService.updateLeadCase(bulkRequestPayload, "authToken");
        assertEquals(result, bulkRequestPayloadResult.getBulkDetails().getCaseData().getMultipleCollection().toString());
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

}