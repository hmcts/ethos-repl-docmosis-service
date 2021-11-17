package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DynamicListHelper {

    private DynamicListHelper() {
    }

    public static void dynamicRespondentRepresentativeNames(CaseData caseData) {
        List<DynamicValueType> listItems = createDynamicRespondentName(caseData.getRespondentCollection());
        if (!listItems.isEmpty()) {
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            if (caseData.getRepCollection() != null && !caseData.getRepCollection().isEmpty()) {
                ListIterator<RepresentedTypeRItem> repItr = caseData.getRepCollection().listIterator();
                while (repItr.hasNext()) {
                    var respRepCollection = caseData.getRepCollection().get(repItr.nextIndex());
                    var dynamicValueType = new DynamicValueType();
                    if (respRepCollection.getValue().getDynamicRespRepName() == null) {
                        repItr.next().getValue().setDynamicRespRepName(dynamicFixedListType);
                        var respRepName = respRepCollection.getValue().getRespRepName();
                        dynamicValueType.setLabel(respRepName);
                        dynamicValueType.setCode(respRepName);
                    } else {
                        dynamicValueType = respRepCollection.getValue().getDynamicRespRepName().getValue();
                        repItr.next().getValue().setDynamicRespRepName(dynamicFixedListType);
                    }
                    respRepCollection.getValue().getDynamicRespRepName().setValue(dynamicValueType);
                }
            } else {
                var representedTypeR = new RepresentedTypeR();
                representedTypeR.setDynamicRespRepName(dynamicFixedListType);
                var representedTypeRItem = new RepresentedTypeRItem();
                representedTypeRItem.setValue(representedTypeR);
                var collection = List.of(representedTypeRItem);
                caseData.setRepCollection(collection);
            }
        }
    }

    private static List<DynamicValueType> createDynamicRespondentName(
            List<RespondentSumTypeItem> respondentCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (respondentCollection != null) {
            for (RespondentSumTypeItem respondentSumTypeItem : respondentCollection) {
                var dynamicValueType = new DynamicValueType();
                var respondentSumType = respondentSumTypeItem.getValue();
                dynamicValueType.setCode(respondentSumType.getRespondentName());
                dynamicValueType.setLabel(respondentSumType.getRespondentName());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }
}
