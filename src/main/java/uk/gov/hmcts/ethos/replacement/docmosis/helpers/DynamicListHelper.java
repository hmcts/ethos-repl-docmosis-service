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

    public static List<DynamicValueType> createDynamicRespondentName(List<RespondentSumTypeItem> respondentCollection) {
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

    public static DynamicValueType getDynamicValue(String value) {
        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(value);
        dynamicValueType.setLabel(value);
        return dynamicValueType;
    }

    public static DynamicValueType getDynamicCodeLabel(String code, String label) {
        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(code);
        dynamicValueType.setLabel(label);
        return dynamicValueType;
    }
}
