package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.types.MultipleType;
import uk.gov.hmcts.ecm.common.model.bulk.types.SearchType;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PENDING_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;

public class BulkHelperTest {

    private List<SubmitEvent> submitEvents;
    private MultipleType multipleType;
    private SubmitEvent submitEventComplete;
    private BulkDetails bulkDetailsListCases;
    private BulkDetails bulkDetailsScheduleDetailed;

    private BulkDetails generateBulkDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, BulkDetails.class);
    }

    @Before
    public void setUp() throws Exception {
        bulkDetailsListCases = generateBulkDetails("bulkDetailsTest1.json");
        bulkDetailsScheduleDetailed = generateBulkDetails("bulkDetailsTest2.json");
        CaseData caseData = new CaseData();
        caseData.setClerkResponsible("JuanFran");
        caseData.setEthosCaseReference("111");
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        caseData.setClaimantIndType(claimantIndType);
        ClaimantType claimantType = new ClaimantType();
        Address address = new Address();
        address.setAddressLine1("Line1");
        address.setPostCode("PostCode");
        claimantType.setClaimantAddressUK(address);
        caseData.setClaimantType(claimantType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Andrew Smith");
        respondentSumType.setRespondentAddress(address);
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
        String result = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=No, multipleReferenceM=1234, " +
                "clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , respondentRepM= , fileLocM=Manchester, " +
                "receiptDateM= , positionTypeM= , feeGroupReferenceM= , jurCodesCollectionM=AA, stateM= , subMultipleM= , subMultipleTitleM= , currentPositionM= , " +
                "claimantAddressLine1M=Line1, claimantPostCodeM=PostCode, respondentAddressLine1M=Line1, respondentPostCodeM=PostCode, flag1M= , flag2M= , EQPM= , " +
                "respondentRepOrgM= , claimantRepOrgM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=No, " +
                "multipleReferenceM=1234, clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , respondentRepM= , " +
                "fileLocM=Manchester, receiptDateM= , positionTypeM= , feeGroupReferenceM= , jurCodesCollectionM=AA, stateM= , subMultipleM= , subMultipleTitleM= , " +
                "currentPositionM= , claimantAddressLine1M=Line1, claimantPostCodeM=PostCode, respondentAddressLine1M=Line1, respondentPostCodeM=PostCode, flag1M= , " +
                "flag2M= , EQPM= , respondentRepOrgM= , claimantRepOrgM= ))]";
        assertEquals(result, BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents, "1234").toString());
    }

    @Test
    public void getMultipleTypeListBySubmitEventListWithStates() {
        submitEvents.get(0).setState(SUBMITTED_STATE);
        submitEvents.get(1).setState(PENDING_STATE);
        String result = "[MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, leadClaimantM=No, multipleReferenceM=1234, " +
                "clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , respondentRepM= , fileLocM=Manchester, " +
                "receiptDateM= , positionTypeM= , feeGroupReferenceM= , jurCodesCollectionM=AA, stateM=Submitted, subMultipleM= , subMultipleTitleM= , " +
                "currentPositionM= , claimantAddressLine1M=Line1, claimantPostCodeM=PostCode, respondentAddressLine1M=Line1, respondentPostCodeM=PostCode, " +
                "flag1M= , flag2M= , EQPM= , respondentRepOrgM= , claimantRepOrgM= )), MultipleTypeItem(id=0, value=MultipleType(caseIDM=0, ethosCaseReferenceM=222, " +
                "leadClaimantM=No, multipleReferenceM=1234, clerkRespM=JuanFran, claimantSurnameM=Mike, respondentSurnameM=Andrew Smith, claimantRepM= , " +
                "respondentRepM= , fileLocM=Manchester, receiptDateM= , positionTypeM= , feeGroupReferenceM= , jurCodesCollectionM=AA, stateM=Submitted, " +
                "subMultipleM= , subMultipleTitleM= , currentPositionM= , claimantAddressLine1M=Line1, claimantPostCodeM=PostCode, respondentAddressLine1M=Line1, " +
                "respondentPostCodeM=PostCode, flag1M= , flag2M= , EQPM= , respondentRepOrgM= , claimantRepOrgM= ))]";
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
    public void getMultipleTypeFromSubmitEvent() {
        String result = "MultipleType(caseIDM=0, ethosCaseReferenceM= , leadClaimantM=No, multipleReferenceM= , clerkRespM= , " +
                "claimantSurnameM=Mike, respondentSurnameM=Juan Pedro, claimantRepM= , respondentRepM= , fileLocM=Manchester, " +
                "receiptDateM= , positionTypeM= , feeGroupReferenceM=11111, jurCodesCollectionM= , stateM= , subMultipleM= , " +
                "subMultipleTitleM= , currentPositionM= , claimantAddressLine1M= , claimantPostCodeM= , respondentAddressLine1M= , " +
                "respondentPostCodeM= , flag1M= , flag2M= , EQPM= , respondentRepOrgM= , claimantRepOrgM= )";
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

    private JurCodesTypeItem getJurCodesWithOutcome(String codes, String outcome) {
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList(codes);
        jurCodesType.setJudgmentOutcome(outcome);
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setValue(jurCodesType);
        return jurCodesTypeItem;
    }

    @Test
    public void containsAllJurCodes() {
        List<JurCodesTypeItem> jurCodesTypeItemsInput = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        assertTrue(BulkHelper.containsAllJurCodes(jurCodesTypeItemsInput, jurCodesTypeItems2));
        jurCodesTypeItemsInput = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        assertFalse(BulkHelper.containsAllJurCodes(jurCodesTypeItemsInput, jurCodesTypeItems2));
        assertFalse(BulkHelper.containsAllJurCodes(null, jurCodesTypeItems2));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), jurCodesTypeItems2));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), new ArrayList<>()));
        assertFalse(BulkHelper.containsAllJurCodes(null, null));
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

        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(null)));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString("")));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(" ")));
    }

    @Test
    public void getJurCodesCollectionWithHide_ListFromString() {
        List<JurCodesTypeItem> jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        String jurCodes = BulkHelper.getJurCodesCollectionWithHide(jurCodesTypeItems2);
        assertTrue(BulkHelper.containsAllJurCodes(jurCodesTypeItems1, BulkHelper.getJurCodesListFromString(jurCodes)));

        jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        jurCodes = BulkHelper.getJurCodesCollectionWithHide(jurCodesTypeItems2);
        assertFalse(BulkHelper.containsAllJurCodes(jurCodesTypeItems1, BulkHelper.getJurCodesListFromString(jurCodes)));

        assertFalse(BulkHelper.containsAllJurCodes(null, BulkHelper.getJurCodesListFromString(jurCodes)));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(jurCodes)));

        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(null)));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString("")));
        assertFalse(BulkHelper.containsAllJurCodes(new ArrayList<>(), BulkHelper.getJurCodesListFromString(" ")));
    }

    @Test
    public void getJurCodesCollectionWithHide_AllJurCodesAreFiltered_ReturnsEmptyString() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Acas conciliated settlement");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Withdrawn or private settlement");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Input in error");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1, jurCodesTypeItem2,
                jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals(" ", BulkHelper.getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

    @Test
    public void getJurCodesCollectionWithHide_NoJurCodeFiltered_ReturnsAllOutputs() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Successful at hearing");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Unsuccessful at hearing");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Dismissed at hearing - out of scope");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1,
                jurCodesTypeItem2, jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals("A, B, C", BulkHelper.getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

    @Test
    public void getJurCodesCollectionWithHide_PartJurCodesFiltered_ReturnsSomeOutputs() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Acas conciliated settlement");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Successful at hearing");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Withdrawn or private settlement");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1,
                jurCodesTypeItem2, jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals("B", BulkHelper.getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

    @Test
    public void buildScheduleDocumentContentListCases() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SUM-ENG-00220.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Multiple_No\":\"210001\",\n" +
                "\"Multiple_title\":\"Title1\",\n" +
                "\"subMultiple\":[\n" +
                "{\"SubMultiple_No\":\"211000\",\n" +
                "\"SubMultiple_title\":\"SubMultiple1\",\n" +
                "\"multiple\":[\n" +
                "{\"Claimant\":\"Lady C Collins\",\n" +
                "\"Current_position\":\"With judge\",\n" +
                "\"Case_No\":\"2120000/2019\",\n" +
                "\"claimant_full_name\":\"Lady C Collins\",\n" +
                "\"claimant_addressLine1\":\"\",\n" +
                "\"claimant_postCode\":\"\",\n" +
                "\"respondent_full_name\":\"Emma Watson\",\n" +
                "\"respondent_addressLine1\":\"Sillavan Way\",\n" +
                "\"respondent_postCode\":\"M12 122\"},\n" +
                "{\"Claimant\":\"Mrs F Watson\",\n" +
                "\"Current_position\":\"Nowhere\",\n" +
                "\"Case_No\":\"2120001/2019\",\n" +
                "\"claimant_full_name\":\"Mrs F Watson\",\n" +
                "\"claimant_addressLine1\":\"Avenue\",\n" +
                "\"claimant_postCode\":\"L232323\",\n" +
                "\"respondent_full_name\":\"Mr Steve Martin\",\n" +
                "\"respondent_addressLine1\":\"Sillavan Street\",\n" +
                "\"respondent_postCode\":\"M12 222\"}]\n" +
                "},\n" +
                "{\"SubMultiple_No\":\"211001\",\n" +
                "\"SubMultiple_title\":\"SubMultiple2\",\n" +
                "\"multiple\":[\n" +
                "{\"Claimant\":\"Juan Pedro Martin\",\n" +
                "\"Current_position\":\"\",\n" +
                "\"Case_No\":\"2120002/2019\",\n" +
                "\"claimant_full_name\":\"Juan Pedro Martin\",\n" +
                "\"claimant_addressLine1\":\"\",\n" +
                "\"claimant_postCode\":\"\",\n" +
                "\"respondent_full_name\":\"Emma Watson\",\n" +
                "\"respondent_addressLine1\":\"\",\n" +
                "\"respondent_postCode\":\"\"}]\n" +
                "}],\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, BulkHelper.buildScheduleDocumentContent(bulkDetailsListCases.getCaseData(), "").toString());
    }

    @Test
    public void buildScheduleDocumentContentMultipleScheduleDetailed() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SUM-ENG-00222.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Multiple_No\":\"210001\",\n" +
                "\"Multiple_title\":\"Title1\",\n" +
                "\"multiple\":[\n" +
                "{\"Claimant\":\"Lady C Collins\",\n" +
                "\"Current_position\":\"With judge\",\n" +
                "\"Case_No\":\"2120000/2019\",\n" +
                "\"claimant_full_name\":\"Lady C Collins\",\n" +
                "\"claimant_addressLine1\":\"\",\n" +
                "\"claimant_postCode\":\"\",\n" +
                "\"respondent_full_name\":\"Emma Watson\",\n" +
                "\"respondent_addressLine1\":\"Sillavan Way\",\n" +
                "\"respondent_postCode\":\"M12 122\"},\n" +
                "{\"Claimant\":\"Mrs F Watson\",\n" +
                "\"Current_position\":\"Nowhere\",\n" +
                "\"Case_No\":\"2120001/2019\",\n" +
                "\"claimant_full_name\":\"Mrs F Watson\",\n" +
                "\"claimant_addressLine1\":\"Avenue\",\n" +
                "\"claimant_postCode\":\"L232323\",\n" +
                "\"respondent_full_name\":\"Mr Steve Martin\",\n" +
                "\"respondent_addressLine1\":\"Sillavan Street\",\n" +
                "\"respondent_postCode\":\"M12 222\"},\n" +
                "{\"Claimant\":\"Juan Pedro Martin\",\n" +
                "\"Current_position\":\"\",\n" +
                "\"Case_No\":\"2120002/2019\",\n" +
                "\"claimant_full_name\":\"Juan Pedro Martin\",\n" +
                "\"claimant_addressLine1\":\"\",\n" +
                "\"claimant_postCode\":\"\",\n" +
                "\"respondent_full_name\":\"Emma Watson\",\n" +
                "\"respondent_addressLine1\":\"\",\n" +
                "\"respondent_postCode\":\"\"}],\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, BulkHelper.buildScheduleDocumentContent(bulkDetailsScheduleDetailed.getCaseData(), "").toString());
    }

    private SubmitEvent createSubmitEvent(String ethosCaseReference) {
        SubmitEvent submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference(ethosCaseReference);
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

    @Test
    public void calculateLeadCase() {
        List<SubmitEvent> submitEventList = new ArrayList<>(Arrays.asList(createSubmitEvent("1"), createSubmitEvent("2"),
                createSubmitEvent("3"), createSubmitEvent("4")));
        List<String> caseIds = new ArrayList<>(Arrays.asList("5", "2", "3"));
        List<SubmitEvent> submitEventListResult = BulkHelper.calculateLeadCase(submitEventList, caseIds);
        assertEquals("[2, 1, 3, 4]", submitEventListResult.stream()
                .map(submitEvent1 -> submitEvent1.getCaseData().getEthosCaseReference()).collect(Collectors.toList()).toString());
    }

    @Test
    public void calculateLeadCase2() {
        List<SubmitEvent> submitEventList = new ArrayList<>(Arrays.asList(createSubmitEvent("1"), createSubmitEvent("2"),
                createSubmitEvent("3"), createSubmitEvent("5")));
        List<String> caseIds = new ArrayList<>(Arrays.asList("5", "2", "3"));
        List<SubmitEvent> submitEventListResult = BulkHelper.calculateLeadCase(submitEventList, caseIds);
        assertEquals("[5, 1, 2, 3]", submitEventListResult.stream()
                .map(submitEvent1 -> submitEvent1.getCaseData().getEthosCaseReference()).collect(Collectors.toList()).toString());
    }

}