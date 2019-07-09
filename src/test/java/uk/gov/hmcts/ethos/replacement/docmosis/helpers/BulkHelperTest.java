package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.MultipleType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.SearchType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

public class BulkHelperTest {

    private List<SubmitEvent> submitEvents;
    private MultipleType multipleType;
    private SubmitEvent submitEventComplete;

    @Before
    public void setUp() {
        CaseData caseData = new CaseData();
        caseData.setClerkResponsible("JuanFran");
        caseData.setEthosCaseReference("111");
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Andrew Smith");
        caseData.setRespondentSumType(respondentSumType);
        caseData.setFileLocation("Manchester");
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseData(caseData);
        SubmitEvent submitEvent2 = new SubmitEvent();
        caseData.setEthosCaseReference("222");
        submitEvent2.setCaseData(caseData);
        submitEvents = new ArrayList<>(Arrays.asList(submitEvent1, submitEvent2));
        multipleType = getMultipleType();
        submitEventComplete = new SubmitEvent();
        submitEventComplete = getSubmitEvent();
    }

    @Test
    public void getMultipleTypeListBySubmitEventList() {
        String result = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM= , multipleReferenceM=1234, " +
                "clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , respondentRepM= , " +
                "fileLocM=Manchester, receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM= , jurCodesCollectionM= , " +
                "stateM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM= , " +
                "multipleReferenceM=1234, clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , " +
                "respondentRepM= , fileLocM=Manchester, receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM= , " +
                "jurCodesCollectionM= , stateM= ))]";
        assertEquals(result, BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents, "1234", "No Lead").toString());
    }

    @Test
    public void getMultipleTypeListBySubmitEventListWithLead() {
        String result = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=Yes, " +
                "multipleReferenceM=1234, clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , " +
                "respondentRepM= , fileLocM=Manchester, receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM= , " +
                "jurCodesCollectionM= , stateM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, " +
                "leadClaimantM=Yes, multipleReferenceM=1234, clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, " +
                "claimantRepM= , respondentRepM= , fileLocM=Manchester, receiptDateM= , acasOfficeM= , positionTypeM= , " +
                "feeGroupReferenceM= , jurCodesCollectionM= , stateM= ))]";
        String leadId = "222";
        assertEquals(result, BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents, "1234", leadId).toString());
    }

    @Test
    public void getMultipleTypeListEmptyBySubmitEventList() {
        String result = "[]";
        assertEquals(result, BulkHelper.getMultipleTypeListBySubmitEventList(new ArrayList<>(), "1234", "No Lead").toString());
    }

    @Test
    public void getSearchTypeFromMultipleType() {
        SearchType searchType = new SearchType();
        searchType.setClaimantSurnameS("Mike");
        searchType.setFileLocS("Manchester");
        searchType.setFeeGroupReferenceS("11111");
        searchType.setStateS("1_Submitted");
        searchType.setJurCodesCollectionS("");
        assertEquals(searchType, BulkHelper.getSearchTypeFromMultipleType(multipleType));
    }

    private MultipleType getMultipleType() {
        MultipleType multipleType = new MultipleType();
        multipleType.setClaimantSurnameM("Mike");
        multipleType.setFileLocM("Manchester");
        multipleType.setFeeGroupReferenceM("11111");
        multipleType.setStateM("1_Submitted");
        multipleType.setJurCodesCollectionM("");
        return multipleType;
    }

//    @Test
//    public void updateIncurCaseRemoval1() {
//        assertTrue(BulkHelper.updateIncurCaseRemoval(getBulkData1()));
//    }
//
//    @Test
//    public void updateIncurCaseRemoval2() {
//        assertTrue(BulkHelper.updateIncurCaseRemoval(getBulkData2()));
//    }
//
//    @Test
//    public void updateIncurCaseRemoval3() {
//        assertTrue(BulkHelper.updateIncurCaseRemoval(getBulkData3()));
//    }

//    private BulkData getBulkData1() {
//        BulkData bulkData = new BulkData();
//        bulkData.setClaimantSurname("Mikey");
//        bulkData.setRespondentSurname("Jordan");
//        bulkData.setFeeGroupReference("");
//        bulkData.setFileLocation("Manchester");
//        bulkData.setClaimantSurnameV2("Mikey");
//        bulkData.setRespondentSurnameV2("Jordan");
//        bulkData.setFeeGroupReferenceV2("1222");
//        bulkData.setFileLocationV2("");
//        return bulkData;
//    }
//
//    private BulkData getBulkData2() {
//        BulkData bulkData = new BulkData();
//        bulkData.setClaimantSurname("");
//        bulkData.setRespondentSurname("Jordan");
//        bulkData.setFeeGroupReference("");
//        bulkData.setFileLocation("Manchester");
//        bulkData.setClaimantSurnameV2("Mikey");
//        bulkData.setRespondentSurnameV2("Jordan");
//        bulkData.setFeeGroupReferenceV2("1222");
//        bulkData.setFileLocationV2("");
//        return bulkData;
//    }
//
//    private BulkData getBulkData3() {
//        BulkData bulkData = new BulkData();
//        bulkData.setClaimantSurname("");
//        bulkData.setRespondentSurname("");
//        bulkData.setFeeGroupReference("");
//        bulkData.setFileLocation("Manchester");
//        bulkData.setClaimantSurnameV2("Mikey");
//        bulkData.setRespondentSurnameV2("Jordan");
//        bulkData.setFeeGroupReferenceV2("1222");
//        bulkData.setFileLocationV2("Manchester");
//        return bulkData;
//    }

    @Test
    public void getCaseTypeId() {
        String caseId = MANCHESTER_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(ETHOS_BULK_CASE_TYPE_ID));
        caseId = GLASGOW_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId("GLASGOW_BULK_CASE_ID"));
    }

    @Test
    public void getMultipleTypeFromSubmitEvent() {
        String result = "MultipleType(caseIDM=0, ethosCaseReferenceM= , leadClaimantM= , multipleReferenceM=null, clerkRespM= , " +
                "claimantSurnameM=Mike, respondentSurnameM=Juan Pedro, claimantRepM= , respondentRepM= , fileLocM=Manchester, " +
                "receiptDateM= , acasOfficeM= , positionTypeM= , feeGroupReferenceM=11111, jurCodesCollectionM= , stateM= )";
        assertEquals(result, BulkHelper.getMultipleTypeFromSubmitEvent(submitEventComplete, "No Lead").toString());
    }

    private SubmitEvent getSubmitEvent() {
        SubmitEvent submitEvent = new SubmitEvent();
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Juan Pedro");
        CaseData caseData = new CaseData();
        caseData.setClaimantIndType(claimantIndType);
        caseData.setRespondentSumType(respondentSumType);
        caseData.setFileLocation("Manchester");
        caseData.setFeeGroupReference("11111");
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

}