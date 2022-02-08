package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;

@Service
public class BfActionReport {

    public ListingData runReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        BfActionReportData bfActionReportData = new BfActionReportData();
        if (!CollectionUtils.isEmpty(submitEvents)) {
            List<BFDateTypeItem> bfDateTypeItems = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                addBfDateTypeItems(submitEvent, listingDetails.getCaseData(), bfDateTypeItems);
            }
            var itemsSortedByBfDate = bfDateTypeItems.stream()
                .sorted((BFDateTypeItem::comparedTo)).collect(Collectors.toList());
            bfActionReportData.setBfDateCollection(itemsSortedByBfDate);
        }

        String caseTypeId = listingDetails.getCaseTypeId();
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        bfActionReportData.clearReportFields();
        bfActionReportData.setReportType(listingDetails.getCaseData().getReportType());
        bfActionReportData.setReportType(BROUGHT_FORWARD_REPORT);
        bfActionReportData.setDocumentName(BROUGHT_FORWARD_REPORT);
        bfActionReportData.setOffice(office);
        bfActionReportData.setDurationDescription(ReportHelper.getDurationText(listingDetails.getCaseData()));
        return bfActionReportData;
    }

    private static void addBfDateTypeItems(SubmitEvent submitEvent, ListingData listingData,
        List<BFDateTypeItem> bfDateTypeItems) {
        if (!CollectionUtils.isEmpty(submitEvent.getCaseData().getBfActions())) {
            for (var bfActionTypeItem : submitEvent.getCaseData().getBfActions()) {
                var bfDateTypeItem = getBFDateTypeItem(bfActionTypeItem,
                    listingData, submitEvent.getCaseData());
                if (bfDateTypeItem.getValue() != null) {
                    bfDateTypeItems.add(bfDateTypeItem);
                }
            }
        }
    }

    private static BFDateTypeItem getBFDateTypeItem(BFActionTypeItem bfActionTypeItem,
                                                    ListingData listingData, CaseData caseData) {
        var bfDateTypeItem = new BFDateTypeItem();
        var bfActionType = bfActionTypeItem.getValue();
        if (!isNullOrEmpty(bfActionType.getBfDate()) && isNullOrEmpty(bfActionType.getCleared())) {
            boolean matchingDateIsValid = ReportHelper.validateMatchingDate(listingData, bfActionType.getBfDate());

            if (matchingDateIsValid) {
                var bfDateType = new BFDateType();
                bfDateType.setCaseReference(caseData.getEthosCaseReference());

                if (!Strings.isNullOrEmpty(bfActionType.getAllActions())) {
                    bfDateType.setBroughtForwardAction(bfActionType.getAllActions());
                } else if (!Strings.isNullOrEmpty(bfActionType.getCwActions())) {
                    bfDateType.setBroughtForwardAction(bfActionType.getCwActions());
                }

                bfDateType.setBroughtForwardEnteredDate(bfActionType.getDateEntered());
                bfDateType.setBroughtForwardDate(bfActionType.getBfDate());
                bfDateType.setBroughtForwardDateReason(bfActionType.getNotes());
                bfDateTypeItem.setId(String.valueOf(bfActionTypeItem.getId()));
                bfDateTypeItem.setValue(bfDateType);
            }
        }
        return bfDateTypeItem;
    }

}
