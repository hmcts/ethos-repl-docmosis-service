package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;

public class DynamicLetters {
    private DynamicLetters() {
    }

    public static void dynamicLetters(CaseData caseData, String caseTypeId) {
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicHearingList(caseData);
        if (!listItems.isEmpty()) {
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            if (!caseTypeId.equals(SCOTLAND_CASE_TYPE_ID)) {
                CorrespondenceType correspondenceType = new CorrespondenceType();
                correspondenceType.setDynamicHearingNumber(dynamicFixedListType);
                caseData.setCorrespondenceType(correspondenceType);
            } else {
                CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
                correspondenceScotType.setDynamicHearingNumber(dynamicFixedListType);
                caseData.setCorrespondenceScotType(correspondenceScotType);
            }
        }
    }
}
