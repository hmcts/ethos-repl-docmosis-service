package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JurisdictionHelperTest {

    private List<JurCodesTypeItem> getJurCodesTypeItems(String... codes) {
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
        assertTrue(JurisdictionHelper.containsAllJurCodes(jurCodesTypeItemsInput, jurCodesTypeItems2));
        jurCodesTypeItemsInput = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        assertFalse(JurisdictionHelper.containsAllJurCodes(jurCodesTypeItemsInput, jurCodesTypeItems2));
        assertFalse(JurisdictionHelper.containsAllJurCodes(null, jurCodesTypeItems2));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), jurCodesTypeItems2));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), new ArrayList<>()));
        assertFalse(JurisdictionHelper.containsAllJurCodes(null, null));
    }

    @Test
    public void getJurCodesListFromString() {
        List<JurCodesTypeItem> jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        String jurCodes = JurisdictionHelper.getJurCodesCollection(jurCodesTypeItems2);
        assertTrue(JurisdictionHelper.containsAllJurCodes(jurCodesTypeItems1, JurisdictionHelper.getJurCodesListFromString(jurCodes)));

        jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        jurCodes = JurisdictionHelper.getJurCodesCollection(jurCodesTypeItems2);
        assertFalse(JurisdictionHelper.containsAllJurCodes(jurCodesTypeItems1, JurisdictionHelper.getJurCodesListFromString(jurCodes)));

        assertFalse(JurisdictionHelper.containsAllJurCodes(null, JurisdictionHelper.getJurCodesListFromString(jurCodes)));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString(jurCodes)));

        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString(null)));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString("")));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString(" ")));
    }

    @Test
    public void getJurCodesCollectionWithHide_ListFromString() {
        List<JurCodesTypeItem> jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        String jurCodes = JurisdictionHelper.getJurCodesCollectionWithHide(jurCodesTypeItems2);
        assertTrue(JurisdictionHelper.containsAllJurCodes(jurCodesTypeItems1, JurisdictionHelper.getJurCodesListFromString(jurCodes)));

        jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        jurCodes = JurisdictionHelper.getJurCodesCollectionWithHide(jurCodesTypeItems2);
        assertFalse(JurisdictionHelper.containsAllJurCodes(jurCodesTypeItems1, JurisdictionHelper.getJurCodesListFromString(jurCodes)));

        assertFalse(JurisdictionHelper.containsAllJurCodes(null, JurisdictionHelper.getJurCodesListFromString(jurCodes)));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString(jurCodes)));

        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString(null)));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString("")));
        assertFalse(JurisdictionHelper.containsAllJurCodes(new ArrayList<>(), JurisdictionHelper.getJurCodesListFromString(" ")));
    }

    @Test
    public void getJurCodesCollectionWithHide_AllJurCodesAreFiltered_ReturnsEmptyString() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Acas conciliated settlement");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Withdrawn or private settlement");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Input in error");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1, jurCodesTypeItem2,
                jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals(" ", JurisdictionHelper.getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

    @Test
    public void getJurCodesCollectionWithHide_NoJurCodeFiltered_ReturnsAllOutputs() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Successful at hearing");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Unsuccessful at hearing");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Dismissed at hearing - out of scope");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1,
                jurCodesTypeItem2, jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals("A, B, C", JurisdictionHelper.getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

    @Test
    public void getJurCodesCollectionWithHide_PartJurCodesFiltered_ReturnsSomeOutputs() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Acas conciliated settlement");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Successful at hearing");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Withdrawn or private settlement");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1,
                jurCodesTypeItem2, jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals("B", JurisdictionHelper.getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

}