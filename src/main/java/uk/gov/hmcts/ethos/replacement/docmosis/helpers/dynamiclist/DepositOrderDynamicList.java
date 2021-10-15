package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclist;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DepositTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DepositType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.List;
import java.util.ListIterator;

public class DepositOrderDynamicList {

    private DepositOrderDynamicList() {
    }

    public static void dynamicDepositOrder (CaseData caseData) {
        List<DynamicValueType> listClaimantRespondent = DynamicListHelper.createDynamicRespondentName(caseData.getRespondentCollection());
        listClaimantRespondent.add(DynamicListHelper.getDynamicCodeLabel("C: "+ caseData.getClaimant(), caseData.getClaimant()));

        var listAllItems = listClaimantRespondent;
        listAllItems.add(DynamicListHelper.getDynamicValue("Tribunal"));

        populateDynamicDeposit(caseData, listClaimantRespondent);
    }

    private static void populateDynamicDeposit(CaseData caseData, List<DynamicValueType> listItems) {
        var depositOrderCollection = caseData.getDepositCollection();
        if (!listItems.isEmpty()) {
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            if (depositOrderCollection != null && !depositOrderCollection.isEmpty()) {
                ListIterator<DepositTypeItem> depItr = depositOrderCollection.listIterator();
                while (depItr.hasNext()) {
                    var depositOrderItem = depositOrderCollection.get(depItr.nextIndex());
                    var dynamicValueType = new DynamicValueType();
                    if (depositOrderItem.getValue().getDynamicDepositOrderAgainst() == null) {
                        depItr.next().getValue().setDynamicDepositOrderAgainst(dynamicFixedListType);
                        dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, listItems, depositOrderItem.getValue().getDepositOrderAgainst());
                    } else {
                        dynamicValueType = depositOrderItem.getValue().getDynamicDepositOrderAgainst().getValue();
                        depItr.next().getValue().setDynamicDepositOrderAgainst(dynamicFixedListType);
                    }
                    depositOrderItem.getValue().getDynamicDepositOrderAgainst().setValue(dynamicValueType);

                    if (depositOrderItem.getValue().getDynamicDepositRequestedBy() == null) {
                        depItr.next().getValue().setDynamicDepositRequestedBy(dynamicFixedListType);
                        dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, listItems, depositOrderItem.getValue().getDepositRequestedBy());
                    } else {
                        dynamicValueType = depositOrderItem.getValue().getDynamicDepositRequestedBy().getValue();
                        depItr.next().getValue().setDynamicDepositRequestedBy(dynamicFixedListType);
                    }
                    depositOrderItem.getValue().getDynamicDepositRequestedBy().setValue(dynamicValueType);

                    if (depositOrderItem.getValue().getDynamicDepositRefundedTo() == null) {
                        depItr.next().getValue().setDynamicDepositRefundedTo(dynamicFixedListType);
                        dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, listItems, depositOrderItem.getValue().getDepositRequestedBy());
                    } else {
                        dynamicValueType = depositOrderItem.getValue().getDynamicDepositRefundedTo().getValue();
                        depItr.next().getValue().setDynamicDepositRefundedTo(dynamicFixedListType);
                    }
                    depositOrderItem.getValue().getDynamicDepositRefundedTo().setValue(dynamicValueType);

                }
            } else {
                var depositType = new DepositType();
                depositType.setDynamicDepositOrderAgainst(dynamicFixedListType);
                var depositTypeItem = new DepositTypeItem();
                depositTypeItem.setValue(depositType);
                caseData.setDepositCollection(List.of(depositTypeItem));

            }
        }
    }

}
