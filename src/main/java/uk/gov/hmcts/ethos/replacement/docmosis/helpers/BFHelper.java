package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
public class BFHelper {

    public static void copyBFActionsCollections(CaseData caseData) {

        List<BFActionTypeItem> bfActionTypeItemsCW = caseData.getBfActionsCW();
        List<BFActionTypeItem> bfActionTypeItemsCWAux = new ArrayList<>();

        if (bfActionTypeItemsCW != null && !bfActionTypeItemsCW.isEmpty()) {

            log.info("Iterate through the bfActionTypeItemsCW");

            for (BFActionTypeItem bfActionTypeItemCW : bfActionTypeItemsCW) {

                BFActionType bfActionTypeCW = bfActionTypeItemCW.getValue();
                List<BFActionTypeItem> bfActionTypeItemsAll =
                        caseData.getBfActionsAll() == null
                                ? new ArrayList<>()
                                : caseData.getBfActionsAll();

                if (isNullOrEmpty(bfActionTypeCW.getDateEntered())) {

                    log.info("New bfAction");
                    updateBFActionsAll(bfActionTypeCW, bfActionTypeItemsAll, CREATE_ACTION);

                } else {

                    log.info("Update bfAction");
                    updateBFActionsAll(bfActionTypeCW, bfActionTypeItemsAll, AMEND_ACTION);

                }

                caseData.setBfActionsAll(bfActionTypeItemsAll);
                bfActionTypeItemCW.setValue(bfActionTypeCW);
                bfActionTypeItemsCWAux.add(bfActionTypeItemCW);

            }

            caseData.setBfActionsCW(bfActionTypeItemsCWAux);

        }

    }

    private static void updateBFActionsAll(BFActionType bfActionTypeCW,
                                           List<BFActionTypeItem> bfActionTypeItemsAll,
                                           String action) {

        if (action.equals(CREATE_ACTION)) {

            BFActionType bfActionTypeAll = new BFActionType();
            BFActionTypeItem bfActionTypeItemAll = new BFActionTypeItem();
            log.info("Generate new date time id");
            generateNewDateTimeId(bfActionTypeCW);
            log.info("Create new entry for bfActionAll");
            updateBFActionTypeAll(bfActionTypeCW, bfActionTypeAll);
            bfActionTypeItemAll.setId(UUID.randomUUID().toString());
            bfActionTypeItemAll.setValue(bfActionTypeAll);
            bfActionTypeItemsAll.add(bfActionTypeItemAll);

        } else {

            String dateEntered = bfActionTypeCW.getDateEntered();
            log.info("Updating bfActionAll for: " + dateEntered);
            bfActionTypeItemsAll.stream()
                    .filter(bfActionTypeItemAll ->
                            bfActionTypeItemAll.getValue().getDateEntered().equals(dateEntered))
                    .findAny()
                    .ifPresent(bfActionTypeItemAll ->
                            updateBFActionTypeAll(bfActionTypeCW,
                                    bfActionTypeItemAll.getValue()));

        }

    }

    private static void generateNewDateTimeId(BFActionType bfActionTypeCW) {

        log.info("Generate ID: " + bfActionTypeCW.getCwActions());

        LocalDateTime dateTime = LocalDateTime.now();
        String dateTimeID = dateTime.format(DATE_TIME_USER_FRIENDLY_PATTERN);
        bfActionTypeCW.setDateEntered(dateTimeID);

    }

    private static void updateBFActionTypeAll(BFActionType bfActionTypeCW, BFActionType bfActionTypeAll) {

        bfActionTypeAll.setDateEntered(bfActionTypeCW.getDateEntered());
        bfActionTypeAll.setAllActions(bfActionTypeCW.getAllActions());
        bfActionTypeAll.setBfDate(bfActionTypeCW.getBfDate());
        bfActionTypeAll.setCleared(bfActionTypeCW.getCleared());
        bfActionTypeAll.setCwActions(bfActionTypeCW.getCwActions());
        bfActionTypeAll.setNotes(bfActionTypeCW.getNotes());

    }

}
