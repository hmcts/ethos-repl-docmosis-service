package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
public class BFHelper {

    public static void copyBFActionsCollections(CaseData caseData) {

//        List<BFActionTypeItem> bfActionTypeItemsCW = caseData.getBfActionsCW();
//        List<BFActionTypeItem> bfActionTypeItemsCWAux = new ArrayList<>();
//
//        if (bfActionTypeItemsCW != null && !bfActionTypeItemsCW.isEmpty()) {
//
//            log.info("Iterate through the bfActionTypeItemsCW");
//
//            for (BFActionTypeItem bfActionTypeItemCW : bfActionTypeItemsCW) {
//
//                BFActionType bfActionTypeCW = bfActionTypeItemCW.getValue();
//                List<BFActionTypeItem> bfActionTypeItemsAll =
//                        caseData.getBfActionsAll() == null
//                                ? new ArrayList<>()
//                                : caseData.getBfActionsAll();
//
//                if (isNullOrEmpty(bfActionTypeCW.getDateEntered())) {
//
//                    log.info("New bfAction");
//                    updateBFActionsAll(bfActionTypeCW, bfActionTypeItemsAll, CREATE_ACTION);
//
//                } else {
//
//                    log.info("Update bfAction");
//                    updateBFActionsAll(bfActionTypeCW, bfActionTypeItemsAll, AMEND_ACTION);
//
//                }
//
//                caseData.setBfActionsAll(bfActionTypeItemsAll);
//                bfActionTypeItemCW.setValue(bfActionTypeCW);
//                bfActionTypeItemsCWAux.add(bfActionTypeItemCW);
//
//            }
//
//            caseData.setBfActionsCW(bfActionTypeItemsCWAux);
//
//        }

    }

//    private static void updateBFActionsAll(BFActionType bfActionTypeCW,
//                                           List<BFActionTypeItem> bfActionTypeItemsAll,
//                                           String action) {
//
//        if (action.equals(CREATE_ACTION)) {
//
//            BFActionType bfActionTypeAll = new BFActionType();
//            BFActionTypeItem bfActionTypeItemAll = new BFActionTypeItem();
//            log.info("Generate new date time id");
//            generateNewDateTimeId(bfActionTypeCW);
//            log.info("Create new entry for bfActionAll");
//            updateBFActionTypeAll(bfActionTypeCW, bfActionTypeAll);
//            bfActionTypeItemAll.setId(UUID.randomUUID().toString());
//            bfActionTypeItemAll.setValue(bfActionTypeAll);
//            bfActionTypeItemsAll.add(bfActionTypeItemAll);
//
//        } else {
//
//            String dateEntered = bfActionTypeCW.getDateEntered();
//            log.info("Updating bfActionAll for: " + dateEntered);
//            bfActionTypeItemsAll.stream()
//                    .filter(bfActionTypeItemAll ->
//                            bfActionTypeItemAll.getValue().getDateEntered().equals(dateEntered))
//                    .findAny()
//                    .ifPresent(bfActionTypeItemAll ->
//                            updateBFActionTypeAll(bfActionTypeCW,
//                                    bfActionTypeItemAll.getValue()));
//
//        }
//
//    }
//
//    private static void generateNewDateTimeId(BFActionType bfActionTypeCW) {
//
//        log.info("Generate ID: " + bfActionTypeCW.getCwActions());
//
//        LocalDateTime dateTime = LocalDateTime.now();
//        String dateTimeID = dateTime.format(DATE_TIME_USER_FRIENDLY_PATTERN);
//        bfActionTypeCW.setDateEntered(dateTimeID);
//
//    }
//
//    private static void updateBFActionTypeAll(BFActionType bfActionTypeCW, BFActionType bfActionTypeAll) {
//
//        bfActionTypeAll.setDateEntered(bfActionTypeCW.getDateEntered());
//        bfActionTypeAll.setAllActions(bfActionTypeCW.getAllActions());
//        bfActionTypeAll.setBfDate(bfActionTypeCW.getBfDate());
//        bfActionTypeAll.setCleared(bfActionTypeCW.getCleared());
//        bfActionTypeAll.setCwActions(bfActionTypeCW.getCwActions());
//        bfActionTypeAll.setNotes(bfActionTypeCW.getNotes());
//
//    }

    private static List<DynamicValueType> populateBfListItems() {
        return new ArrayList<>(Arrays.asList(
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
                Helper.getDynamicValue(BF_ACTION_STRIKING_OUT_WARNING)));
    }

    public static void populateDynamicListBfActions(CaseData caseData) {

        if (caseData.getBfActions() != null && !caseData.getBfActions().isEmpty()){
            List<BFActionTypeItem> bfActionTypeItemList = caseData.getBfActions();

            for (BFActionTypeItem bfActionTypeItem : bfActionTypeItemList) {

                DynamicFixedListType dynamicFixedListType = bfActionTypeItem.getValue().getAction();

                if (dynamicFixedListType == null) {

                    dynamicFixedListType = new DynamicFixedListType();
                    dynamicFixedListType.setListItems(populateBfListItems());
                    dynamicFixedListType.setValue(Helper.getDynamicValue(BF_ACTION_ACAS));

                }

            }

        } else {

            DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(populateBfListItems());
            dynamicFixedListType.setValue(Helper.getDynamicValue(BF_ACTION_ACAS));

            BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
            BFActionType bfActionType = new BFActionType();
            bfActionType.setAction(dynamicFixedListType);
            bfActionTypeItem.setId(UUID.randomUUID().toString());
            bfActionTypeItem.setValue(bfActionType);

            BFActionTypeItem bfActionTypeItem1 = new BFActionTypeItem();
            BFActionType bfActionType1 = new BFActionType();
            bfActionType1.setAction(dynamicFixedListType);
            bfActionTypeItem1.setId(UUID.randomUUID().toString());
            bfActionTypeItem1.setValue(bfActionType1);

            List<BFActionTypeItem> bfActionTypeItemList = new ArrayList<>(Arrays.asList(bfActionTypeItem, bfActionTypeItem1));
            caseData.setBfActions(bfActionTypeItemList);

        }
    }

}
