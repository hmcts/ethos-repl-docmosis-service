package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicDepositOrder;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicLetters;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicRespondentRepresentative;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicRestrictedReporting;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.createDynamicHearingList;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.createDynamicJurisdictionCodes;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.createDynamicRespondentName;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.findDynamicValue;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.getDynamicCodeLabel;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.getDynamicValue;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.getDynamicValueParty;

public class DynamicListHelperTest {

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails2;
    private CaseDetails caseDetails4;
    private CaseDetails caseDetails6;
    private CaseDetails caseDetailsScotTest1;
    private DynamicValueType dynamicValueType;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetails6 = generateCaseDetails("caseDetailsTest6.json");
        caseDetailsScotTest1 = generateCaseDetails("caseDetailsScotTest1.json");
        dynamicValueType = new DynamicValueType();
    }

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
        dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRepCollection().getFirst()
                .getValue().getDynamicRespRepName().getListItems().getFirst());
    }

    @Test
    public void populateDynamicRespondentRepList() {
        DynamicRespondentRepresentative.dynamicRespondentRepresentativeNames(caseDetails6.getCaseData());
        assertNotNull(caseDetails6.getCaseData().getRepCollection().getFirst().getValue().getDynamicRespRepName());
        dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails6.getCaseData().getRepCollection().getFirst()
                .getValue().getDynamicRespRepName().getListItems().getFirst());
    }

    @Test
    public void createDynamicListForRestrictedReporting() {
        DynamicRestrictedReporting.dynamicRestrictedReporting(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getRestrictedReporting());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRestrictedReporting()
                .getDynamicRequestedBy().getListItems().getFirst());
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
        List<DynamicValueType> listItems = 
            createDynamicRespondentName(caseDetails1.getCaseData().getRespondentCollection());
        listItems.add(getDynamicCodeLabel("C: " + caseDetails1.getCaseData().getClaimant(), 
            caseDetails1.getCaseData().getClaimant()));
        var dynamicValue = getDynamicValueParty(caseDetails1.getCaseData(), listItems, "Respondent");
        assertEquals(dynamicValue, dynamicValueRespondent);
    }

    @Test
    public void dynamicDepositOrder() {
        DynamicDepositOrder.dynamicDepositOrder(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getDepositCollection());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().getFirst()
                .getValue().getDynamicDepositOrderAgainst().getValue());
        dynamicValueType.setCode("Tribunal");
        dynamicValueType.setLabel("Tribunal");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().getFirst()
                .getValue().getDynamicDepositRequestedBy().getValue());
    }

    @Test
    public void dynamicDepositRefund() {
        caseDetails1.getCaseData().getDepositCollection().getFirst().getValue().setDepositRefund("Yes");
        DynamicDepositOrder.dynamicDepositOrder(caseDetails1.getCaseData());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().getFirst()
                .getValue().getDynamicDepositRefundedTo().getValue());
    }

    @Test
    public void dynamicHearingList() {
        List<DynamicValueType> dynamicHearingList = createDynamicHearingList(caseDetails1.getCaseData());
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 - Single - Manchester - 01 Nov 2019");
        assertEquals(dynamicValueType, dynamicHearingList.getFirst());
        dynamicValueType.setCode("2");
        dynamicValueType.setLabel("2 - Single - Manchester - 25 Nov 2019");
        assertEquals(dynamicValueType, dynamicHearingList.get(1));
    }

    @Test
    public void dynamicLettersEngWales() {
        DynamicLetters.dynamicLetters(caseDetails1.getCaseData(), "Manchester");
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 - Single - Manchester - 01 Nov 2019");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getCorrespondenceType().getDynamicHearingNumber()
            .getListItems().getFirst());
        assertNull(caseDetails1.getCaseData().getCorrespondenceScotType());
    }

    @Test
    public void dynamicLettersScotland() {
        DynamicLetters.dynamicLetters(caseDetailsScotTest1.getCaseData(), "Scotland");
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 - Single - Glasgow - 25 Nov 2019");
        assertEquals(dynamicValueType, caseDetailsScotTest1.getCaseData().getCorrespondenceScotType()
            .getDynamicHearingNumber().getListItems().getFirst());
        assertNull(caseDetailsScotTest1.getCaseData().getCorrespondenceType());
    }

    @Test
    public void createDynamicJurisdictionCodesTest() {
        List<DynamicValueType> listItems = createDynamicJurisdictionCodes(caseDetails1.getCaseData());
        var totalJurisdictions = caseDetails1.getCaseData().getJurCodesCollection().size();
        var dynamicValue = getDynamicValue(caseDetails1.getCaseData().getJurCodesCollection()
                .getFirst().getValue().getJuridictionCodesList());
        assertEquals(dynamicValue, listItems.getFirst());
        assertEquals(totalJurisdictions, listItems.size());
    }

    @Test
    public void findDynamicValueTest() {
        List<DynamicValueType> listItems = createDynamicJurisdictionCodes(caseDetails1.getCaseData());
        dynamicValueType.setCode("COM");
        dynamicValueType.setLabel("COM");
        assertEquals(dynamicValueType, findDynamicValue(listItems, "COM"));
    }

    @Test
    public void dynamicJudgementsTest() {
        var caseData = caseDetails1.getCaseData();
        DynamicJudgements.dynamicJudgements(caseData);
        var totalHearings = caseData.getHearingCollection().size();
        JudgementType judgementType = caseData.getJudgementCollection().getFirst().getValue();
        assertEquals(totalHearings, judgementType.getDynamicJudgementHearing().getListItems().size());
    }

    @Test
    public void dynamicJudgementHearing_HearingDateFilled() {
        var caseData = caseDetails1.getCaseData();
        caseData.getJudgementCollection().getFirst().getValue().setJudgmentHearingDate("2019-11-01");
        DynamicJudgements.dynamicJudgements(caseData);
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 : Manchester - Single - 2019-11-01");
        assertEquals(dynamicValueType, caseData.getJudgementCollection().getFirst().getValue()
            .getDynamicJudgementHearing().getValue());
    }

    @Test
    public void dynamicJudgementHearing_DynamicValue() {
        var caseData = caseDetails1.getCaseData();
        List<DynamicValueType> hearingListItems = createDynamicHearingList(caseData);
        var listHearing = new DynamicFixedListType();
        listHearing.setListItems(hearingListItems);
        caseData.getJudgementCollection().getFirst().getValue().setDynamicJudgementHearing(listHearing);
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 : Manchester - Single - 2019-11-01");
        caseData.getJudgementCollection().getFirst().getValue().getDynamicJudgementHearing().setValue(dynamicValueType);
        DynamicJudgements.dynamicJudgements(caseData);
        assertEquals(dynamicValueType, caseData.getJudgementCollection().getFirst().getValue()
            .getDynamicJudgementHearing().getValue());
    }

    @Test
    public void createDynamicJudgementHearing() {
        var caseData = caseDetails2.getCaseData();
        DynamicJudgements.dynamicJudgements(caseData);
        assertNotNull(caseData.getJudgementCollection());
        var totalHearings = caseData.getHearingCollection().size();
        JudgementType judgementType = caseData.getJudgementCollection().getFirst().getValue();
        assertEquals(totalHearings, judgementType.getDynamicJudgementHearing().getListItems().size());
    }

    @Test
    public void createDynamicJudgementHearingNoHearing() {
        var caseData = caseDetails2.getCaseData();
        caseData.setHearingCollection(null);
        DynamicJudgements.dynamicJudgements(caseData);
        assertNotNull(caseData.getJudgementCollection());
        JudgementType judgementType = caseData.getJudgementCollection().getFirst().getValue();
        assertEquals("No Hearings", judgementType.getDynamicJudgementHearing().getListItems().getFirst().getCode());
    }

    @Test
    public void dynamicJudgment_ifHearingDateIsInvalid() {
        var casedata = caseDetails2.getCaseData();
        var judgmentType = new JudgementType();
        judgmentType.setJudgmentHearingDate("2022-02-02");
        var judgmentTypeItem = new JudgementTypeItem();
        judgmentTypeItem.setValue(judgmentType);
        casedata.setJudgementCollection(List.of(judgmentTypeItem));

        DynamicJudgements.dynamicJudgements(casedata);

        assertNotNull(casedata.getJudgementCollection());
        var judgementType = casedata.getJudgementCollection().getFirst().getValue();
        assertNull(judgementType.getJudgmentHearingDate());
    }
}
