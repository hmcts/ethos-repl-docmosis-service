package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BFHelperTest {

    private CaseData caseData;

    @Before
    public void setUp() {
        caseData = MultipleUtil.getCaseData("245000/2021");
        caseData.setBfActionsCW(generateBFActionTypeItemsCW());
    }

    @Test
    public void copyBFActionsCollectionsNewAction() {
        caseData.getBfActionsCW().get(0).getValue().setDateEntered(null);
        BFHelper.copyBFActionsCollections(caseData);
        assertEquals(1, caseData.getBfActionsCW().size());
        assertEquals(1, caseData.getBfActionsAll().size());
        assertEquals("24-08-2020", caseData.getBfActionsAll().get(0).getValue().getBfDate());
        assertNotEquals("01-01-2020 23:00:00", caseData.getBfActionsAll().get(0).getValue().getDateEntered());
    }

    @Test
    public void copyBFActionsCollectionsUpdateAction() {
        caseData.setBfActionsAll(generateBFActionTypeItemsAll());
        BFHelper.copyBFActionsCollections(caseData);
        assertEquals(1, caseData.getBfActionsCW().size());
        assertEquals(1, caseData.getBfActionsAll().size());
        assertEquals("24-08-2020", caseData.getBfActionsAll().get(0).getValue().getBfDate());
        assertEquals("01-01-2020 23:00:00", caseData.getBfActionsAll().get(0).getValue().getDateEntered());
    }

    private List<BFActionTypeItem> generateBFActionTypeItemsCW() {
        BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
        BFActionType bfActionType = new BFActionType();
        bfActionType.setCwActions("Actions");
        bfActionType.setCleared("Date Cleared");
        bfActionType.setBfDate("24-08-2020");
        bfActionType.setNotes("Notes");
        bfActionType.setAllActions("All actions");
        bfActionType.setDateEntered("01-01-2020 23:00:00");
        bfActionTypeItem.setId(UUID.randomUUID().toString());
        bfActionTypeItem.setValue(bfActionType);
        return new ArrayList<>(Collections.singletonList(bfActionTypeItem));
    }

    private List<BFActionTypeItem> generateBFActionTypeItemsAll() {
        BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
        BFActionType bfActionType = new BFActionType();
        bfActionType.setCwActions("Actions2");
        bfActionType.setCleared("Date Cleared2");
        bfActionType.setBfDate("");
        bfActionType.setNotes("Notes");
        bfActionType.setAllActions("All actions");
        bfActionType.setDateEntered("01-01-2020 23:00:00");
        bfActionTypeItem.setId(UUID.randomUUID().toString());
        bfActionTypeItem.setValue(bfActionType);
        return new ArrayList<>(Collections.singletonList(bfActionTypeItem));
    }

}
