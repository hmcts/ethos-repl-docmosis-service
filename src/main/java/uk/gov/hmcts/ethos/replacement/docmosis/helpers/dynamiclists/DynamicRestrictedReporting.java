package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.RestrictedReportingType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DynamicRestrictedReporting {
    private DynamicRestrictedReporting() {
    }

    public static void dynamicRestrictedReporting(CaseData caseData) {
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicRespondentName(
                caseData.getRespondentCollection());
        listItems.add(DynamicListHelper.getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant()));
        listItems.add(DynamicListHelper.getDynamicValue("Judge"));
        listItems.add(DynamicListHelper.getDynamicValue("Both Parties"));
        listItems.add(DynamicListHelper.getDynamicValue("Other"));
        listItems.add(DynamicListHelper.getDynamicValue("None"));

        if (!listItems.isEmpty()) {
            var restrictedReporting = caseData.getRestrictedReporting();
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            if (restrictedReporting != null) {
                DynamicValueType dynamicValueType;
                if (restrictedReporting.getDynamicRequestedBy() == null) {
                    restrictedReporting.setDynamicRequestedBy(dynamicFixedListType);
                    if (!isNullOrEmpty(restrictedReporting.getRequestedBy())) {
                        dynamicValueType = DynamicListHelper.getDynamicValueType(caseData, listItems,
                                restrictedReporting.getRequestedBy());
                    } else {
                        dynamicValueType = listItems.get(listItems.size() - 1);
                    }
                } else {
                    dynamicValueType = restrictedReporting.getDynamicRequestedBy().getValue();
                    restrictedReporting.getDynamicRequestedBy().setListItems(listItems);
                }
                restrictedReporting.getDynamicRequestedBy().setValue(dynamicValueType);
            } else {
                var restrictedReportingType = new RestrictedReportingType();
                restrictedReportingType.setDynamicRequestedBy(dynamicFixedListType);
                caseData.setRestrictedReporting(restrictedReportingType);
            }
        }
    }

}
