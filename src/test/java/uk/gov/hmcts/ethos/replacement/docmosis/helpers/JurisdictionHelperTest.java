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
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.JurisdictionHelper.containsAllJurCodes;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.JurisdictionHelper.getJurCodesCollection;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.JurisdictionHelper.getJurCodesCollectionWithHide;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.JurisdictionHelper.getJurCodesListFromString;

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
    public void containsAllJurCodesTest() {
        List<JurCodesTypeItem> jurCodesTypeItemsInput = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        assertTrue(containsAllJurCodes(jurCodesTypeItemsInput, jurCodesTypeItems2));
        jurCodesTypeItemsInput = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        assertFalse(containsAllJurCodes(jurCodesTypeItemsInput, jurCodesTypeItems2));
        assertFalse(containsAllJurCodes(null, jurCodesTypeItems2));
        assertFalse(containsAllJurCodes(new ArrayList<>(), jurCodesTypeItems2));
        assertFalse(containsAllJurCodes(new ArrayList<>(), new ArrayList<>()));
        assertFalse(containsAllJurCodes(null, null));
    }

    @Test
    public void getJurCodesListFromStringTest() {
        List<JurCodesTypeItem> jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        String jurCodes = getJurCodesCollection(jurCodesTypeItems2);
        assertTrue(containsAllJurCodes(jurCodesTypeItems1, getJurCodesListFromString(jurCodes)));

        jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        jurCodes = getJurCodesCollection(jurCodesTypeItems2);
        assertFalse(containsAllJurCodes(jurCodesTypeItems1, getJurCodesListFromString(jurCodes)));

        assertFalse(containsAllJurCodes(null, getJurCodesListFromString(jurCodes)));
        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString(jurCodes)));

        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString(null)));
        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString("")));
        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString(" ")));
    }

    @Test
    public void getJurCodesCollectionWithHide_ListFromString() {
        List<JurCodesTypeItem> jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "C");
        List<JurCodesTypeItem> jurCodesTypeItems2 = getJurCodesTypeItems("A", "B", "C");
        String jurCodes = getJurCodesCollectionWithHide(jurCodesTypeItems2);
        assertTrue(containsAllJurCodes(jurCodesTypeItems1, getJurCodesListFromString(jurCodes)));

        jurCodesTypeItems1 = getJurCodesTypeItems("A", "B", "D");
        jurCodesTypeItems2 = getJurCodesTypeItems("A", "C", "B");
        jurCodes = getJurCodesCollectionWithHide(jurCodesTypeItems2);
        assertFalse(containsAllJurCodes(jurCodesTypeItems1, getJurCodesListFromString(jurCodes)));

        assertFalse(containsAllJurCodes(null, getJurCodesListFromString(jurCodes)));
        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString(jurCodes)));

        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString(null)));
        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString("")));
        assertFalse(containsAllJurCodes(new ArrayList<>(), getJurCodesListFromString(" ")));
    }

    @Test
    public void getJurCodesCollectionWithHide_AllJurCodesAreFiltered_ReturnsEmptyString() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Acas conciliated settlement");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Withdrawn or private settlement");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Input in error");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1, jurCodesTypeItem2,
                jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals(" ", getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

    @Test
    public void getJurCodesCollectionWithHide_NoJurCodeFiltered_ReturnsAllOutputs() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Successful at hearing");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Unsuccessful at hearing");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Dismissed at hearing - out of scope");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1,
                jurCodesTypeItem2, jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals("A, B, C", getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

    @Test
    public void getJurCodesCollectionWithHide_PartJurCodesFiltered_ReturnsSomeOutputs() {
        JurCodesTypeItem jurCodesTypeItem1 = getJurCodesWithOutcome("A", "Acas conciliated settlement");
        JurCodesTypeItem jurCodesTypeItem2 = getJurCodesWithOutcome("B", "Successful at hearing");
        JurCodesTypeItem jurCodesTypeItem3 = getJurCodesWithOutcome("C", "Withdrawn or private settlement");
        JurCodesTypeItem jurCodesTypeItem4 = getJurCodesWithOutcome("D", "Dismissed on withdrawal");
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>(Arrays.asList(jurCodesTypeItem1,
                jurCodesTypeItem2, jurCodesTypeItem3, jurCodesTypeItem4));
        assertEquals("B", getJurCodesCollectionWithHide(jurCodesTypeItems));
    }

}