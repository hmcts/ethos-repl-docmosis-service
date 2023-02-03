package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.List;
import java.util.ListIterator;

public class DynamicRespondentRepresentative {

    private DynamicRespondentRepresentative() {
    }

    public static void dynamicRespondentRepresentativeNames(CaseData caseData) {
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicRespondentName(
            caseData.getRespondentCollection());
        if (!listItems.isEmpty()) {
            if (CollectionUtils.isNotEmpty(caseData.getRepCollection())) {
                ListIterator<RepresentedTypeRItem> repItr = caseData.getRepCollection().listIterator();
                while (repItr.hasNext()) {
                    DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
                    dynamicFixedListType.setListItems(listItems);
                    RepresentedTypeRItem respRepCollection = caseData.getRepCollection().get(repItr.nextIndex());
                    DynamicValueType dynamicValueType = new DynamicValueType();
                    if (respRepCollection.getValue().getDynamicRespRepName() == null) {
                        repItr.next().getValue().setDynamicRespRepName(dynamicFixedListType);
                        String respRepName = respRepCollection.getValue().getRespRepName();
                        dynamicValueType.setLabel(respRepName);
                        dynamicValueType.setCode(respRepName);
                    } else {
                        dynamicValueType = respRepCollection.getValue().getDynamicRespRepName().getValue();
                        repItr.next().getValue().setDynamicRespRepName(dynamicFixedListType);
                    }
                    respRepCollection.getValue().getDynamicRespRepName().setValue(dynamicValueType);
                }
            } else {
                DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
                dynamicFixedListType.setListItems(listItems);
                RepresentedTypeR representedTypeR = RepresentedTypeR.builder()
                    .dynamicRespRepName(dynamicFixedListType).build();
                RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
                representedTypeRItem.setValue(representedTypeR);
                List<RepresentedTypeRItem> collection = List.of(representedTypeRItem);
                caseData.setRepCollection(collection);
            }
        }
    }
}
