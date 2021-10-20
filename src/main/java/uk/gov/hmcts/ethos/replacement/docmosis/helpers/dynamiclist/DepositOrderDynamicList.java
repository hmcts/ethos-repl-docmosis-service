package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclist;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DepositTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DepositType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DepositOrderDynamicList {

    private DepositOrderDynamicList() {
    }

    public static void dynamicDepositOrder(CaseData caseData) {
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicRespondentName(caseData.getRespondentCollection());
        listItems.add(DynamicListHelper.getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant()));

        List<DynamicValueType> listAllItems = new ArrayList<>(listItems);
        listAllItems.add(DynamicListHelper.getDynamicValue("Tribunal"));

        populateDynamicDeposit(caseData, listItems, listAllItems);
    }

    private static void populateDynamicDeposit(CaseData caseData, List<DynamicValueType> listItems, List<DynamicValueType> listAllItems) {
        var depositOrderCollection = caseData.getDepositCollection();
        if (!listItems.isEmpty()) {
            var listClaimantRespondent = new DynamicFixedListType();
            var listAll = new DynamicFixedListType();
            listClaimantRespondent.setListItems(listItems);
            listAll.setListItems(listAllItems);
            if (depositOrderCollection != null && !depositOrderCollection.isEmpty()) {
                ListIterator<DepositTypeItem> depItr = depositOrderCollection.listIterator();
                while (depItr.hasNext()) {
                    var depositOrderItem = depositOrderCollection.get(depItr.nextIndex());
                    var dynamicValueType = new DynamicValueType();
                    if (depositOrderItem.getValue().getDynamicDepositOrderAgainst() == null) {
                        depItr.next().getValue().setDynamicDepositOrderAgainst(listClaimantRespondent);
                        dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, listItems, depositOrderItem.getValue().getDepositOrderAgainst());
                    } else {
                        dynamicValueType = depositOrderItem.getValue().getDynamicDepositOrderAgainst().getValue();
                        depItr.next().getValue().setDynamicDepositOrderAgainst(listClaimantRespondent);
                    }
                    depositOrderItem.getValue().getDynamicDepositOrderAgainst().setValue(dynamicValueType);

                    if (depositOrderItem.getValue().getDynamicDepositRequestedBy() == null) {
                        depItr.previous().getValue().setDynamicDepositRequestedBy(listAll);
                        dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, listItems, depositOrderItem.getValue().getDepositRequestedBy());
                    } else {
                        dynamicValueType = depositOrderItem.getValue().getDynamicDepositRequestedBy().getValue();
                        depItr.previous().getValue().setDynamicDepositRequestedBy(listAll);
                    }
                    depositOrderItem.getValue().getDynamicDepositRequestedBy().setValue(dynamicValueType);

                    if (depositOrderItem.getValue().getDynamicDepositRefundedTo() == null) {
                        depItr.next().getValue().setDynamicDepositRefundedTo(listClaimantRespondent);
                        dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, listItems, depositOrderItem.getValue().getDepositRefundedTo());
                    } else {
                        dynamicValueType = depositOrderItem.getValue().getDynamicDepositRefundedTo().getValue();
                        depItr.next().getValue().setDynamicDepositRefundedTo(listClaimantRespondent);
                    }
                    depositOrderItem.getValue().getDynamicDepositRefundedTo().setValue(dynamicValueType);

                }
            } else {
                var depositType = new DepositType();
                depositType.setDynamicDepositOrderAgainst(listClaimantRespondent);
                depositType.setDepositOrderAgainst(null);
                depositType.setDynamicDepositRefundedTo(listClaimantRespondent);
                depositType.setDepositRefundedTo(null);
                depositType.setDynamicDepositRequestedBy(listAll);
                depositType.setDepositRequestedBy(null);
                var depositTypeItem = new DepositTypeItem();
                depositTypeItem.setValue(depositType);
                caseData.setDepositCollection(List.of(depositTypeItem));

            }
        }
    }
}