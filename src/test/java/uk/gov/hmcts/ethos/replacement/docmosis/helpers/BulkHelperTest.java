package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.MultipleType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.SearchType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;
import java.util.*;

import static org.junit.Assert.*;
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
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));

        caseData.setFileLocation("Manchester");
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("AA");
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setValue(jurCodesType);
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));
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
        String result = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=null, multipleReferenceM=1234, " +
                "clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , respondentRepM= , " +
                "fileLocM=Manchester, receiptDateM= , positionTypeM= , feeGroupReferenceM= , jurCodesCollectionM=AA, " +
                "stateM= , subMultipleM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=null, " +
                "multipleReferenceM=1234, clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , " +
                "respondentRepM= , fileLocM=Manchester, receiptDateM= , positionTypeM= , feeGroupReferenceM= , " +
                "jurCodesCollectionM=AA, stateM= , subMultipleM= ))]";
        assertEquals(result, BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents, "1234").toString());
    }

    @Test
    public void getMultipleTypeListBySubmitEventListWithStates() {
        submitEvents.get(0).setState("Submitted");
        submitEvents.get(1).setState("Pending");
        String result = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=null, multipleReferenceM=1234, " +
                "clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , respondentRepM= , " +
                "fileLocM=Manchester, receiptDateM= , positionTypeM= , feeGroupReferenceM= , jurCodesCollectionM=AA, " +
                "stateM=Submitted, subMultipleM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=null, " +
                "multipleReferenceM=1234, clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , " +
                "respondentRepM= , fileLocM=Manchester, receiptDateM= , positionTypeM= , feeGroupReferenceM= , " +
                "jurCodesCollectionM=AA, stateM=Submitted, subMultipleM= ))]";
        assertEquals(result, BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents, "1234").toString());
    }

    @Test
    public void getMultipleTypeListEmptyBySubmitEventList() {
        String result = "[]";
        assertEquals(result, BulkHelper.getMultipleTypeListBySubmitEventList(new ArrayList<>(), "1234").toString());
    }

    @Test
    public void getSearchTypeFromMultipleType() {
        SearchType searchType = new SearchType();
        searchType.setClaimantSurnameS("Mike");
        searchType.setFileLocS("Manchester");
        searchType.setFeeGroupReferenceS("11111");
        searchType.setStateS("Submitted");
        searchType.setJurCodesCollectionS("");
        assertEquals(searchType, BulkHelper.getSearchTypeFromMultipleType(multipleType));
    }

    private MultipleType getMultipleType() {
        MultipleType multipleType = new MultipleType();
        multipleType.setClaimantSurnameM("Mike");
        multipleType.setFileLocM("Manchester");
        multipleType.setFeeGroupReferenceM("11111");
        multipleType.setStateM("Submitted");
        multipleType.setJurCodesCollectionM("");
        return multipleType;
    }

    @Test
    public void getCaseTypeId() {
        String caseId = MANCHESTER_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(MANCHESTER_BULK_CASE_TYPE_ID));
        caseId = SCOTLAND_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(SCOTLAND_BULK_CASE_TYPE_ID));
        caseId = MANCHESTER_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(MANCHESTER_USERS_BULK_CASE_TYPE_ID));
        caseId = BRISTOL_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(BRISTOL_USERS_BULK_CASE_TYPE_ID));
        caseId = LEEDS_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(LEEDS_USERS_BULK_CASE_TYPE_ID));
        caseId = LONDON_CENTRAL_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID));
        caseId = LONDON_EAST_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(LONDON_EAST_USERS_BULK_CASE_TYPE_ID));
        caseId = LONDON_SOUTH_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID));
        caseId = MIDLANDS_EAST_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID));
        caseId = MIDLANDS_WEST_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID));
        caseId = NEWCASTLE_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(NEWCASTLE_USERS_BULK_CASE_TYPE_ID));
        caseId = WALES_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(WALES_USERS_BULK_CASE_TYPE_ID));
        caseId = WATFORD_USERS_CASE_TYPE_ID;
        assertEquals(caseId, BulkHelper.getCaseTypeId(WATFORD_USERS_BULK_CASE_TYPE_ID));
    }

    @Test
    public void getMultipleTypeFromSubmitEvent() {
        String result = "MultipleType(caseIDM=0, ethosCaseReferenceM= , leadClaimantM=null, multipleReferenceM= , clerkRespM= , " +
                "claimantSurnameM=Mike, respondentSurnameM=Juan Pedro, claimantRepM= , respondentRepM= , fileLocM=Manchester, " +
                "receiptDateM= , positionTypeM= , feeGroupReferenceM=11111, jurCodesCollectionM= , stateM= , subMultipleM= )";
        assertEquals(result, BulkHelper.getMultipleTypeFromSubmitEvent(submitEventComplete).toString());
    }

    private SubmitEvent getSubmitEvent() {
        SubmitEvent submitEvent = new SubmitEvent();
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Juan Pedro");
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        CaseData caseData = new CaseData();
        caseData.setClaimantIndType(claimantIndType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));
        caseData.setFileLocation("Manchester");
        caseData.setFeeGroupReference("11111");
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

    private List<JurCodesTypeItem> getJurCodesTypeItems(String ... codes) {
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList(codes[0]);
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setValue(jurCodesType);
        JurCodesType jurCodesType1 = new JurCodesType();
        jurCodesType1.setJuridictionCodesList(codes[1]);
        JurCodesTypeItem jurCodesTypeItem1 = new JurCodesTypeItem();
        jurCodesTypeItem1.setValue(jurCodesType1);
        JurCodesType jurCodesType2 = new JurCodesType();
        jurCodesType2.setJuridictionCodesList(codes[2]);
        JurCodesTypeItem jurCodesTypeItem2 = new JurCodesTypeItem();
        jurCodesTypeItem2.setValue(jurCodesType2);
        return new ArrayList<>(Arrays.asList(jurCodesTypeItem, jurCodesTypeItem1, jurCodesTypeItem2));
    }

    @Test
    public void containsAllJurCodes() {
        List<JurCodesTypeItem> jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        assertTrue(BulkHelper.containsAllJurCodes(jurCodesTypeItems1, jurCodesTypeItems2));
        jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        assertFalse(BulkHelper.containsAllJurCodes(jurCodesTypeItems1, jurCodesTypeItems2));
        assertFalse(BulkHelper.containsAllJurCodes(null, jurCodesTypeItems2));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), jurCodesTypeItems2));
        assertTrue(BulkHelper.containsAllJurCodes(new ArrayList<>(), new ArrayList<>()));
        assertTrue(BulkHelper.containsAllJurCodes(null, null));
    }

    @Test
    public void getJurCodesListFromString() {
        List<JurCodesTypeItem> jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        String jurCodes = BulkHelper.getJurCodesCollection(jurCodesTypeItems2);
        assertTrue(BulkHelper.containsAllJurCodes(jurCodesTypeItems1, BulkHelper.getJurCodesListFromString(jurCodes)));

        jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        jurCodes = BulkHelper.getJurCodesCollection(jurCodesTypeItems2);
        assertFalse(BulkHelper.containsAllJurCodes(jurCodesTypeItems1, BulkHelper.getJurCodesListFromString(jurCodes)));

        assertFalse(BulkHelper.containsAllJurCodes(null, BulkHelper.getJurCodesListFromString(jurCodes)));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(jurCodes)));

        assertTrue(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(null)));
        assertTrue(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString("")));
        assertTrue(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(" ")));
    }

}