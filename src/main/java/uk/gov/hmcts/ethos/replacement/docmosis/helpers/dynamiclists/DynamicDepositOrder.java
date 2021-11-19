package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DepositTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DepositType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DynamicDepositOrder {
    private DynamicDepositOrder() {
    }

    public static void dynamicDepositOrder(CaseData caseData) {
        List<DynamicValueType> listItems = DynamicListHelper
                .createDynamicRespondentName(caseData.getRespondentCollection());
        listItems.add(DynamicListHelper.getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant()));
        List<DynamicValueType> listAllParties = new ArrayList<>(listItems);
        listAllParties.add(DynamicListHelper.getDynamicValue("Tribunal"));
        populateDynamicDepositOrder(caseData, listItems, listAllParties);
    }

    private static void populateDynamicDepositOrder(CaseData caseData, List<DynamicValueType> listItems,
                                                    List<DynamicValueType> listAllParties) {
        if (!listItems.isEmpty()) {
            var listClaimantRespondent = new DynamicFixedListType();
            listClaimantRespondent.setListItems(listItems);
            var listAll = new DynamicFixedListType();
            listAll.setListItems(listAllParties);

            if (!CollectionUtils.isEmpty(caseData.getDepositCollection())) {
                var depositCollection = caseData.getDepositCollection();
                for (DepositTypeItem depositTypeItem : depositCollection) {
                    dynamicOrderAgainst(caseData, depositTypeItem.getValue(), listClaimantRespondent);
                    dynamicRequestedBy(caseData, depositTypeItem.getValue(), listAll);
                    dynamicRefundedTo(caseData, depositTypeItem.getValue(), listClaimantRespondent);
                }
            } else {
                var depositType = new DepositType();
                depositType.setDynamicDepositOrderAgainst(listClaimantRespondent);
                depositType.setDynamicDepositRequestedBy(listAll);
                depositType.setDynamicDepositRefundedTo(listClaimantRespondent);
                var depositTypeItem = new DepositTypeItem();
                depositTypeItem.setValue(depositType);
                List<DepositTypeItem> depositTypeItems = new ArrayList<>();
                depositTypeItems.add(depositTypeItem);
                caseData.setDepositCollection(depositTypeItems);
            }

        }
    }

    private static void dynamicOrderAgainst(CaseData caseData, DepositType depositType,
                                            DynamicFixedListType dynamicFixedListType) {
        var dynamicValueType = new DynamicValueType();
        if (depositType.getDynamicDepositOrderAgainst() == null) {
            depositType.setDynamicDepositOrderAgainst(dynamicFixedListType);
            if (!isNullOrEmpty(depositType.getDepositOrderAgainst())) {
                dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, dynamicFixedListType.getListItems(),
                        depositType.getDepositOrderAgainst());
            }
        } else {
            dynamicValueType = depositType.getDynamicDepositOrderAgainst().getValue();
            depositType.setDynamicDepositOrderAgainst(dynamicFixedListType);
        }
        depositType.getDynamicDepositOrderAgainst().setValue(dynamicValueType);
    }

    private static void dynamicRequestedBy(CaseData caseData, DepositType depositType,
                                            DynamicFixedListType dynamicFixedListType) {
        var dynamicValueType = new DynamicValueType();
        if (depositType.getDynamicDepositRequestedBy() == null) {
            depositType.setDynamicDepositRequestedBy(dynamicFixedListType);
            if (!isNullOrEmpty(depositType.getDepositRequestedBy())) {
                dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, dynamicFixedListType.getListItems(),
                        depositType.getDepositRequestedBy());
            }
        } else {
            dynamicValueType = depositType.getDynamicDepositRequestedBy().getValue();
            depositType.setDynamicDepositRequestedBy(dynamicFixedListType);
        }
        depositType.getDynamicDepositRequestedBy().setValue(dynamicValueType);
    }

    private static void dynamicRefundedTo(CaseData caseData, DepositType depositType,
                                           DynamicFixedListType dynamicFixedListType) {
        var dynamicValueType = new DynamicValueType();
        if (depositType.getDynamicDepositRefundedTo() == null) {
            depositType.setDynamicDepositRefundedTo(dynamicFixedListType);
            if (!isNullOrEmpty(depositType.getDepositRefundedTo())) {
                dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, dynamicFixedListType.getListItems(),
                        depositType.getDepositRefundedTo());
            }
        } else {
            dynamicValueType = depositType.getDynamicDepositRefundedTo().getValue();
            depositType.setDynamicDepositRefundedTo(dynamicFixedListType);
        }
        depositType.getDynamicDepositRefundedTo().setValue(dynamicValueType);
    }
}
