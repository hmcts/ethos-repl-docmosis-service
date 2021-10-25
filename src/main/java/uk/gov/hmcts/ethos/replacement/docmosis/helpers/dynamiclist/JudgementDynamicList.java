package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclist;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.List;
import java.util.ListIterator;

public class JudgementDynamicList {

    private JudgementDynamicList() {
    }

    public static void createDynamicJudgementList(CaseData caseData) {
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicHearingList(caseData);
        if (!listItems.isEmpty()) {
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            if (caseData.getJudgementCollection() != null && !caseData.getJudgementCollection().isEmpty()) {
                ListIterator<JudgementTypeItem> judgementItr = caseData.getJudgementCollection().listIterator();
                while (judgementItr.hasNext()) {
                    var judgementCollection = caseData.getJudgementCollection().get(judgementItr.nextIndex());
                    var dynamicValueType = new DynamicValueType();
                    if (judgementCollection.getValue().getDynamicJudgementHearing() == null) {
                        if (judgementCollection.getValue().getJudgmentHearingDate() != null && !judgementCollection.getValue().getJudgmentHearingDate().isEmpty()) {
                            var hearingDate = judgementCollection.getValue().getJudgmentHearingDate();
                            judgementItr.next().getValue().setDynamicJudgementHearing(dynamicFixedListType);
                            var hearingNumber = findDateOfHearing(caseData, hearingDate);
                            dynamicValueType = DynamicListHelper.findDynamicValue(listItems, hearingNumber);

                        } else {
                            judgementItr.next().getValue().setDynamicJudgementHearing(dynamicFixedListType);
                            dynamicValueType = listItems.get(0);
                        }
                    }
                    else {
                        dynamicValueType = judgementCollection.getValue().getDynamicJudgementHearing().getValue();
                        judgementItr.next().getValue().setDynamicJudgementHearing(dynamicFixedListType);
                    }
                    judgementCollection.getValue().getDynamicJudgementHearing().setValue(dynamicValueType);
                }
            } else {
                var judgementType = new JudgementType();
                judgementType.setDynamicJudgementHearing(dynamicFixedListType);
                var judgementTypeItem = new JudgementTypeItem();
                judgementTypeItem.setValue(judgementType);
                var collection = List.of(judgementTypeItem);
                caseData.setJudgementCollection(collection);
            }
        }
    }

    public static String findDateOfHearing(CaseData caseData, String hearingDate) {
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                var listedDate = dateListedTypeItem.getValue().getListedDate().substring(0, 10);
                if (listedDate.equals(hearingDate)) {
                    return hearingTypeItem.getValue().getHearingNumber();
                }
            }
        }
        return null;
    }
}
