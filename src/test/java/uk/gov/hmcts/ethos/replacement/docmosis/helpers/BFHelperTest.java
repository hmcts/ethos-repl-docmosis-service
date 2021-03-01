package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;

import java.util.*;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

public class BFHelperTest {

    private CaseData caseData;

    @Before
    public void setUp() {
        caseData = MultipleUtil.getCaseData("245000/2021");
        //caseData.setBfActionsCW(generateBFActionTypeItemsCW());
    }

//    @Test
//    public void copyBFActionsCollectionsNewAction() {
//        caseData.getBfActionsCW().get(0).getValue().setDateEntered(null);
//        BFHelper.copyBFActionsCollections(caseData);
//        assertEquals(1, caseData.getBfActionsCW().size());
//        assertEquals(1, caseData.getBfActionsAll().size());
//        assertEquals("24-08-2020", caseData.getBfActionsAll().get(0).getValue().getBfDate());
//        assertNotEquals("01-01-2020 23:00:00", caseData.getBfActionsAll().get(0).getValue().getDateEntered());
//    }

//    @Test
//    public void copyBFActionsCollectionsUpdateAction() {
//        caseData.setBfActionsAll(generateBFActionTypeItemsAll());
//        BFHelper.copyBFActionsCollections(caseData);
//        assertEquals(1, caseData.getBfActionsCW().size());
//        assertEquals(1, caseData.getBfActionsAll().size());
//        assertEquals("24-08-2020", caseData.getBfActionsAll().get(0).getValue().getBfDate());
//        assertEquals("01-01-2020 23:00:00", caseData.getBfActionsAll().get(0).getValue().getDateEntered());
//    }

//    private List<BFActionTypeItem> generateBFActionTypeItemsCW() {
//        BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
//        BFActionType bfActionType = new BFActionType();
//        bfActionType.setCwActions("Actions");
//        bfActionType.setCleared("Date Cleared");
//        bfActionType.setBfDate("24-08-2020");
//        bfActionType.setNotes("Notes");
//        bfActionType.setAllActions("All actions");
//        bfActionType.setDateEntered("01-01-2020 23:00:00");
//        bfActionTypeItem.setId(UUID.randomUUID().toString());
//        bfActionTypeItem.setValue(bfActionType);
//        return new ArrayList<>(Collections.singletonList(bfActionTypeItem));
//    }

//    private List<BFActionTypeItem> generateBFActionTypeItemsAll() {
//        BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
//        BFActionType bfActionType = new BFActionType();
//        bfActionType.setCwActions("Actions2");
//        bfActionType.setCleared("Date Cleared2");
//        bfActionType.setBfDate("");
//        bfActionType.setNotes("Notes");
//        bfActionType.setAllActions("All actions");
//        bfActionType.setDateEntered("01-01-2020 23:00:00");
//        bfActionTypeItem.setId(UUID.randomUUID().toString());
//        bfActionTypeItem.setValue(bfActionType);
//        return new ArrayList<>(Collections.singletonList(bfActionTypeItem));
//    }

    @Test
    public void populateDynamicListBfActions() {
        BFHelper.populateDynamicListBfActions(caseData);
        assertEquals(2, caseData.getBfActions().size());
     }

    private static DynamicFixedListType getDynamicFixedList() {
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(new ArrayList<>(Arrays.asList(
                Helper.getDynamicValue(BF_ACTION_ACAS),
                Helper.getDynamicValue(BF_ACTION_CASE_LISTED),
                Helper.getDynamicValue(BF_ACTION_CASE_PAPERS),
                Helper.getDynamicValue(BF_ACTION_CASE_TRANSFERRED),
                Helper.getDynamicValue(BF_ACTION_DRAFT),
                Helper.getDynamicValue(BF_ACTION_ENQUIRY_ISSUED),
                Helper.getDynamicValue(BF_ACTION_ENQUIRY_RECEIVED),
                Helper.getDynamicValue(BF_ACTION_EXHIBITS),
                Helper.getDynamicValue(BF_ACTION_INTERLOCUTORY),
                Helper.getDynamicValue(BF_ACTION_IT3_RECEIVED),
                Helper.getDynamicValue(BF_ACTION_OTHER_ACTION),
                Helper.getDynamicValue(BF_ACTION_POSTPONEMENT_REQUESTED),
                Helper.getDynamicValue(BF_ACTION_REFER_CHAIRMAN),
                Helper.getDynamicValue(BF_ACTION_REPLY_TO_ENQUIRY),
                Helper.getDynamicValue(BF_ACTION_STRIKING_OUT_WARNING))));
        dynamicFixedListType.setValue(Helper.getDynamicValue(BF_ACTION_ACAS));
        return dynamicFixedListType;
    }

    private List<BFActionTypeItem> generateBFActionTypeItems() {
        BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
        BFActionType bfActionType = new BFActionType();
        bfActionType.setAction(getDynamicFixedList());
        bfActionType.setCleared("Date Cleared");
        bfActionType.setBfDate("24-08-2020");
        bfActionType.setNotes("Notes");
        bfActionType.setAction(getDynamicFixedList());
        bfActionType.setDateEntered("01-01-2020 23:00:00");
        bfActionTypeItem.setId(UUID.randomUUID().toString());
        bfActionTypeItem.setValue(bfActionType);
        return new ArrayList<>(Collections.singletonList(bfActionTypeItem));
    }


}
