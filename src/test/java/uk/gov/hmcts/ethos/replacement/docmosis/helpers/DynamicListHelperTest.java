package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgmentReconsiderationType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicDepositOrder;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicLetters;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicRespondentRepresentative;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicRestrictedReporting;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

public class DynamicListHelperTest {

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails2;
    private CaseDetails caseDetails4;
    private CaseDetails caseDetails6;
    private CaseDetails caseDetailsScotTest1;
    private DynamicValueType dynamicValueType;
    private MultipleDetails multipleDetails;
    private List<SubmitEvent> submitEvents;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetails6 = generateCaseDetails("caseDetailsTest6.json");
        caseDetailsScotTest1 = generateCaseDetails("caseDetailsScotTest1.json");
        dynamicValueType = new DynamicValueType();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        submitEvents = MultipleUtil.getSubmitEvents();    }


    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    public void createDynamicListForRespondentRepresentative() {
        DynamicRespondentRepresentative.dynamicRespondentRepresentativeNames(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getRepCollection());
        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRepCollection().get(0)
                .getValue().getDynamicRespRepName().getListItems().get(0));
    }

    @Test
    public void populateDynamicRespondentRepList() {
        DynamicRespondentRepresentative.dynamicRespondentRepresentativeNames(caseDetails6.getCaseData());
        assertNotNull(caseDetails6.getCaseData().getRepCollection().get(0).getValue().getDynamicRespRepName());
        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails6.getCaseData().getRepCollection().get(0)
                .getValue().getDynamicRespRepName().getListItems().get(0));
    }

    @Test
    public void createDynamicListForRestrictedReporting() {
        DynamicRestrictedReporting.dynamicRestrictedReporting(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getRestrictedReporting());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRestrictedReporting()
                .getDynamicRequestedBy().getListItems().get(0));
    }

    @Test
    public void populateDynamicListForRestrictedReporting() {
        DynamicRestrictedReporting.dynamicRestrictedReporting(caseDetails4.getCaseData());
        assertNotNull(caseDetails4.getCaseData().getRestrictedReporting().getDynamicRequestedBy());
        dynamicValueType.setCode("Judge");
        dynamicValueType.setLabel("Judge");
        assertEquals(dynamicValueType, caseDetails4.getCaseData().getRestrictedReporting()
                .getDynamicRequestedBy().getValue());
    }

    @Test
    public void dynamicValueTypeRespondent() {
        var dynamicValueRespondent = new DynamicValueType();
        dynamicValueRespondent.setCode("R: Antonio Vazquez");
        dynamicValueRespondent.setLabel("Antonio Vazquez");
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicRespondentName(caseDetails1.getCaseData().getRespondentCollection());
        listItems.add(DynamicListHelper.getDynamicCodeLabel("C: " + caseDetails1.getCaseData().getClaimant(), caseDetails1.getCaseData().getClaimant()));
        var dynamicValue = DynamicListHelper.getDynamicValueParty(caseDetails1.getCaseData(), listItems, "Respondent");
        assertEquals(dynamicValue, dynamicValueRespondent);
    }

    @Test
    public void dynamicDepositOrder() {
        DynamicDepositOrder.dynamicDepositOrder(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getDepositCollection());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().get(0)
                .getValue().getDynamicDepositOrderAgainst().getValue());
        dynamicValueType.setCode("Tribunal");
        dynamicValueType.setLabel("Tribunal");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().get(0)
                .getValue().getDynamicDepositRequestedBy().getValue());
    }

    @Test
    public void dynamicDepositRefund() {
        caseDetails1.getCaseData().getDepositCollection().get(0).getValue().setDepositRefund("Yes");
        DynamicDepositOrder.dynamicDepositOrder(caseDetails1.getCaseData());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().get(0)
                .getValue().getDynamicDepositRefundedTo().getValue());
    }

    @Test
    public void dynamicHearingList() {
        List<DynamicValueType> dynamicHearingList = DynamicListHelper.createDynamicHearingList(caseDetails1.getCaseData());
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 - Single - Manchester - 01 Nov 2019");
        assertEquals(dynamicValueType, dynamicHearingList.get(0));
        dynamicValueType.setCode("2");
        dynamicValueType.setLabel("2 - Single - Manchester - 25 Nov 2019");
        assertEquals(dynamicValueType, dynamicHearingList.get(1));
    }

    @Test
    public void dynamicLettersEngWales() {
        DynamicLetters.dynamicLetters(caseDetails1.getCaseData(), "Manchester");
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 - Single - Manchester - 01 Nov 2019");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getCorrespondenceType().getDynamicHearingNumber().getListItems().get(0));
        assertNull(caseDetails1.getCaseData().getCorrespondenceScotType());
    }

    @Test
    public void dynamicLettersScotland() {
        DynamicLetters.dynamicLetters(caseDetailsScotTest1.getCaseData(), "Scotland");
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 - Single - Glasgow - 25 Nov 2019");
        assertEquals(dynamicValueType, caseDetailsScotTest1.getCaseData().getCorrespondenceScotType().getDynamicHearingNumber().getListItems().get(0));
        assertNull(caseDetailsScotTest1.getCaseData().getCorrespondenceType());
    }

    @Test
    public void dynamicMultipleLetters() {
        List<DynamicValueType> listItems = new ArrayList<>();
        multipleDetails.setCaseTypeId(MANCHESTER_BULK_CASE_TYPE_ID);
        for (SubmitEvent submitEvent : submitEvents) {
            if (submitEvent != null) {
                MultipleUtil.addHearingToCaseData(submitEvent.getCaseData());
                DynamicLetters.dynamicMultipleLetters(submitEvent, multipleDetails.getCaseData(), multipleDetails.getCaseTypeId(), listItems);
            }
        }
        assertEquals(2, listItems.size());
        assertNull(multipleDetails.getCaseData().getCorrespondenceScotType());
    }

    @Test
    public void createDynamicJurisdictionCodesTest() {
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicJurisdictionCodes(caseDetails1.getCaseData());
        var totalJurisdictions = caseDetails1.getCaseData().getJurCodesCollection().size();
        var dynamicValue = DynamicListHelper.getDynamicValue(caseDetails1.getCaseData().getJurCodesCollection()
                .get(0).getValue().getJuridictionCodesList());
        assertEquals(dynamicValue, listItems.get(0));
        assertEquals(totalJurisdictions, listItems.size());
    }

    @Test
    public void findDynamicValueTest() {
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicJurisdictionCodes(caseDetails1.getCaseData());
        dynamicValueType.setCode("COM");
        dynamicValueType.setLabel("COM");
        assertEquals(dynamicValueType, DynamicListHelper.findDynamicValue(listItems, "COM"));
    }

    @Test
    public void dynamicJudgementsTest() {
        var caseData = caseDetails1.getCaseData();
        DynamicJudgements.dynamicJudgements(caseData);
        var totalHearings = caseData.getHearingCollection().size();
        JudgementType judgementType = caseData.getJudgementCollection().get(0).getValue();
        assertEquals(totalHearings, judgementType.getDynamicJudgementHearing().getListItems().size());
    }

    @Test
    public void dynamicJudgementHearing_HearingDateFilled() {
        var caseData = caseDetails1.getCaseData();
        caseData.getJudgementCollection().get(0).getValue().setJudgmentHearingDate("2019-11-01");
        DynamicJudgements.dynamicJudgements(caseData);
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 : Manchester - Single - 2019-11-01");
        assertEquals(dynamicValueType, caseData.getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().getValue());
    }

    @Test
    public void dynamicJudgementHearing_DynamicValue() {
        var caseData = caseDetails1.getCaseData();
        List<DynamicValueType> hearingListItems = DynamicListHelper.createDynamicHearingList(caseData);
        var listHearing = new DynamicFixedListType();
        listHearing.setListItems(hearingListItems);
        caseData.getJudgementCollection().get(0).getValue().setDynamicJudgementHearing(listHearing);
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 : Manchester - Single - 2019-11-01");
        caseData.getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().setValue(dynamicValueType);
        DynamicJudgements.dynamicJudgements(caseData);
        assertEquals(dynamicValueType, caseData.getJudgementCollection().get(0).getValue().getDynamicJudgementHearing().getValue());
    }

    @Test
    public void createDynamicJudgementHearing() {
        var caseData = caseDetails2.getCaseData();
        DynamicJudgements.dynamicJudgements(caseData);
        assertNotNull(caseData.getJudgementCollection());
        var totalHearings = caseData.getHearingCollection().size();
        JudgementType judgementType = caseData.getJudgementCollection().get(0).getValue();
        assertEquals(totalHearings, judgementType.getDynamicJudgementHearing().getListItems().size());
    }

    @Test
    public void createDynamicJudgementHearingNoHearing() {
        var caseData = caseDetails2.getCaseData();
        caseData.setHearingCollection(null);
        DynamicJudgements.dynamicJudgements(caseData);
        assertNotNull(caseData.getJudgementCollection());
        JudgementType judgementType = caseData.getJudgementCollection().get(0).getValue();
        assertEquals("No Hearings", judgementType.getDynamicJudgementHearing().getListItems().get(0).getCode());
    }

    @Test
    public void dynamicReconsideration_claimantReconsideration() {
        var caseData = caseDetails1.getCaseData();
        var reconsiderationType = new JudgmentReconsiderationType();
        reconsiderationType.setReconsideration(YES);
        reconsiderationType.setReconsiderationOwnInitiative(NO);
        reconsiderationType.setReconsiderationPartyInitiative(CLAIMANT_TITLE);
        caseData.getJudgementCollection().get(0).getValue().setJudgementReconsiderations(reconsiderationType);
        dynamicValueType = DynamicListHelper.getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant());
        DynamicJudgements.dynamicJudgements(caseData);
        assertEquals(dynamicValueType, caseData.getJudgementCollection().get(0).getValue().getJudgementReconsiderations().getDynamicReconsiderationPartyInitiative().getValue());
    }

    @Test
    public void dynamicReconsiderations_nullReconsideration() {
        var caseData = caseDetails1.getCaseData();
        caseData.getJudgementCollection().get(0).getValue().setJudgementReconsiderations(null);
        assertNull(caseData.getJudgementCollection().get(0).getValue().getJudgementReconsiderations());
        DynamicJudgements.dynamicJudgements(caseData);
        assertNotNull(caseData.getJudgementCollection().get(0).getValue().getJudgementReconsiderations());
        dynamicValueType = DynamicListHelper.getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant());
        assertEquals(dynamicValueType, caseData.getJudgementCollection().get(0).getValue().getJudgementReconsiderations().getDynamicReconsiderationPartyInitiative().getValue());
    }

    @Test
    public void dynamicReconsideration_existingDynamicValue() {
        var caseData = caseDetails1.getCaseData();
        var reconsiderationType = new JudgmentReconsiderationType();
        reconsiderationType.setReconsideration(YES);
        reconsiderationType.setReconsiderationOwnInitiative(NO);
        var dynamicFixedList = new DynamicFixedListType();
        dynamicFixedList.setListItems(DynamicListHelper.createDynamicHearingList(caseData));
        reconsiderationType.setDynamicReconsiderationPartyInitiative(dynamicFixedList);
        reconsiderationType.getDynamicReconsiderationPartyInitiative().setValue(dynamicFixedList.getListItems().get(0));
        caseData.getJudgementCollection().get(0).getValue().setJudgementReconsiderations(reconsiderationType);

        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseData.getJudgementCollection().get(0).getValue().getJudgementReconsiderations().getDynamicReconsiderationPartyInitiative().getValue());

        DynamicJudgements.dynamicJudgements(caseData); // This will add the claimant to the list
        assertEquals(dynamicValueType, caseData.getJudgementCollection().get(0).getValue().getJudgementReconsiderations().getDynamicReconsiderationPartyInitiative().getValue());
    }
}
