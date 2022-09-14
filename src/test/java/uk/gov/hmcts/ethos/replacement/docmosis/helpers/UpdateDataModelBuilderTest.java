package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.*;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.UpdateDataModel;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SELECT_NONE_VALUE;

public class UpdateDataModelBuilderTest {
    CaseData caseData;

    final String CLAIMANT_REPRESENTATIVE_NAME = "Ruth Powers";
    final String JURISDICTION = "DSO";
    final String RESPONDENT_NAME = "Mindy Simmons";
    final String JUDGEMENT_ID = "1234-5678-9012";
    final String JUDGEMENT_TYPE = "Case Management";
    final String RESPONDENT_REPRESENTATIVE_ID = "5555-6666-7777-8888";
    final String RESPONDENT_REPRESENTATIVE_NAME = "Lionel Hutz";
    final String UNKNOWN_VALUE = "Unknown";

    @Before
    public void setup() {
        caseData = new CaseData();

        // Claimant Representative Name
        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        representedTypeC.setNameOfRepresentative(CLAIMANT_REPRESENTATIVE_NAME);
        caseData.setRepresentativeClaimantType(representedTypeC);

        // Jurisdiction
        caseData.setJurCodesCollection(new ArrayList<>());
        JurCodesTypeItem item = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList(JURISDICTION);
        item.setValue(jurCodesType);
        caseData.getJurCodesCollection().add(item);

        // Respondent
        caseData.setRespondentCollection(new ArrayList<>());
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName(RESPONDENT_NAME);
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.getRespondentCollection().add(respondentSumTypeItem);

        // Judgement
        caseData.setJudgementCollection(new ArrayList<>());
        JudgementType judgementType = new JudgementType();
        judgementType.setJudgementType(JUDGEMENT_TYPE);
        JudgementTypeItem judgementTypeItem = new JudgementTypeItem();
        judgementTypeItem.setId(JUDGEMENT_ID);
        judgementTypeItem.setValue(judgementType);
        caseData.getJudgementCollection().add(judgementTypeItem);

        // Respondent Representative
        caseData.setRepCollection(new ArrayList<>());
        RepresentedTypeR representedType = new RepresentedTypeR();
        representedType.setNameOfRepresentative(RESPONDENT_REPRESENTATIVE_NAME);
        RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setId(RESPONDENT_REPRESENTATIVE_ID);
        representedTypeRItem.setValue(representedType);
        caseData.getRepCollection().add(representedTypeRItem);
    }

    @Test
    public void testNoClaimantRepresentativeSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getRepresentativeClaimantType());
    }


    @Test
    public void testClaimantRepresentativeSelected() {
        MultipleData multipleData = createMultipleData(CLAIMANT_REPRESENTATIVE_NAME, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertEquals(CLAIMANT_REPRESENTATIVE_NAME, updateDataModel.getRepresentativeClaimantType().getNameOfRepresentative());
    }

    @Test
    public void testUnknownSelectedClaimantRepresentativeIsIgnored() {
        MultipleData multipleData = createMultipleData(UNKNOWN_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getRepresentativeClaimantType());
    }

    @Test
    public void testNoJurisdictionSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getJurCodesType());
    }

    @Test
    public void testJurisdictionSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, JURISDICTION, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertEquals(JURISDICTION, updateDataModel.getJurCodesType().getJuridictionCodesList());
    }

    @Test
    public void testUnknownSelectedJurisdictionIsIgnored() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, UNKNOWN_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getJurCodesType());
    }

    @Test
    public void testNoRespondentSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getRespondentSumType());
    }

    @Test
    public void testRespondentSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, RESPONDENT_NAME, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertEquals(RESPONDENT_NAME, updateDataModel.getRespondentSumType().getRespondentName());
    }

    @Test
    public void testUnknownSelectedRespondentIsIgnored() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE,UNKNOWN_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getRespondentSumType());
    }

    @Test
    public void testNoJudgementSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getJudgementType());
    }

    @Test
    public void testJudgementSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, JUDGEMENT_ID, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertEquals(JUDGEMENT_TYPE, updateDataModel.getJudgementType().getJudgementType());
    }

    @Test
    public void testUnknownSelectedJudgementIsIgnored() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE,UNKNOWN_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getJudgementType());
    }

    @Test
    public void testNoRespondentRepresentativeSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getRepresentedType());
    }

    @Test
    public void testRespondentRepresentativeSelected() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, RESPONDENT_REPRESENTATIVE_ID);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertEquals(RESPONDENT_REPRESENTATIVE_NAME, updateDataModel.getRepresentedType().getNameOfRepresentative());
    }

    @Test
    public void testUnknownSelectedRespondentRepresentativeIsIgnored() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, UNKNOWN_VALUE);
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertNull(updateDataModel.getRepresentedType());
    }

    private MultipleData createMultipleData(String claimantRepresentative, String jurisdiction, String respondent, String judgement, String respondentRepresentative) {
        MultipleData multipleData = new MultipleData();
        multipleData.setBatchUpdateClaimantRep(new DynamicFixedListType(claimantRepresentative));
        multipleData.setBatchUpdateJurisdiction(new DynamicFixedListType((jurisdiction)));
        multipleData.setBatchUpdateRespondent(new DynamicFixedListType(respondent));
        multipleData.setBatchUpdateJudgment(new DynamicFixedListType(judgement));
        multipleData.setBatchUpdateRespondentRep(new DynamicFixedListType(respondentRepresentative));

        return multipleData;
    }

    @Test
    public void testSubMultiple() {
        MultipleData multipleData = createMultipleData(SELECT_NONE_VALUE, SELECT_NONE_VALUE, SELECT_NONE_VALUE, JUDGEMENT_ID, SELECT_NONE_VALUE);
        caseData.setSubMultipleName("SubMultiple");
        UpdateDataModel updateDataModel = UpdateDataModelBuilder.build(multipleData, caseData);
        assertEquals("SubMultiple", updateDataModel.getSubMultiple());
    }
}
