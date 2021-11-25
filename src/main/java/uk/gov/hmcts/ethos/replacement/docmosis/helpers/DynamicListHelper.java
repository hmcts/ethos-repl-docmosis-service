package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENT_TITLE;

public class DynamicListHelper {

    private DynamicListHelper() {
    }

    public static List<DynamicValueType> createDynamicRespondentName(List<RespondentSumTypeItem> respondentCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (respondentCollection != null) {
            for (RespondentSumTypeItem respondentSumTypeItem : respondentCollection) {
                var dynamicValueType = new DynamicValueType();
                var respondentSumType = respondentSumTypeItem.getValue();
                dynamicValueType.setCode("R: " + respondentSumType.getRespondentName());
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

    public static DynamicValueType getDynamicValueType(CaseData caseData, List<DynamicValueType> listItems,
                                                       String party) {
        DynamicValueType dynamicValueType;
        if (party.equals(CLAIMANT_TITLE)) {
            dynamicValueType = getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant());
        } else if (party.equals(RESPONDENT_TITLE)) {
            dynamicValueType = listItems.get(0);
        } else {
            dynamicValueType = getDynamicValue(party);
        }
        return dynamicValueType;
    }
}
