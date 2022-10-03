package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_ACAS;

@Slf4j
public class BFHelper {

    private BFHelper() {
    }

    public static void updateBfActionItems(CaseData caseData) {
        List<BFActionTypeItem> bfActions = caseData.getBfActions();
        if (bfActions != null && !bfActions.isEmpty()) {
            for (BFActionTypeItem bfActionTypeItem : bfActions) {
                var bfActionType = bfActionTypeItem.getValue();
                if (isNullOrEmpty(bfActionType.getDateEntered())) {
                    bfActionType.setDateEntered(UtilHelper.formatCurrentDate2(LocalDate.now()));
                }
            }
        }

    }

    public static void populateDynamicListBfActions(CaseData caseData) {

        List<BFActionTypeItem> bfActionTypeItemListAux = new ArrayList<>();

        if (caseData.getBfActions() != null && !caseData.getBfActions().isEmpty()) {

            List<BFActionTypeItem> bfActionTypeItemList = caseData.getBfActions();

            for (BFActionTypeItem bfActionTypeItem : bfActionTypeItemList) {

                var dynamicFixedListType = bfActionTypeItem.getValue().getAction();

                if (dynamicFixedListType != null) {

                    log.info("Updating the value of bfActionDynamicLists: " + dynamicFixedListType.getValue());

                    var bfActionTypeItemAux = new BFActionTypeItem();
                    bfActionTypeItemAux.setId(bfActionTypeItem.getId());
                    bfActionTypeItemAux.setValue(bfActionTypeItem.getValue());
                    bfActionTypeItemListAux.add(bfActionTypeItemAux);
                }
            }

        } else {

            log.info("BF Actions for case reference {} is empty. "
                    + "Creating a dummy one", caseData.getEthosCaseReference());

            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(Helper.getDefaultBfListItems());
            dynamicFixedListType.setValue(DynamicListHelper.getDynamicValue(BF_ACTION_ACAS));

            var bfActionTypeItem = new BFActionTypeItem();
            var bfActionType = new BFActionType();

            bfActionType.setAction(dynamicFixedListType);
            bfActionTypeItem.setId(UUID.randomUUID().toString());
            bfActionTypeItem.setValue(bfActionType);

            bfActionTypeItemListAux = new ArrayList<>(Collections.singletonList(bfActionTypeItem));

        }

        caseData.setBfActions(bfActionTypeItemListAux);

    }

}
