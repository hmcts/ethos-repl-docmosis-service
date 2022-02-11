package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import com.google.common.base.Strings;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItemComparator;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;

public class BfActionReport {
    public ListingData runReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        BfActionReportData bfActionReportData = new BfActionReportData();
        var caseData = listingDetails.getCaseData();
        if (!CollectionUtils.isEmpty(submitEvents)) {
            List<BFDateTypeItem> bfDateTypeItems = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                addBfDateTypeItems(submitEvent, caseData, bfDateTypeItems);
            }
            bfDateTypeItems.sort(new BFDateTypeItemComparator());
            bfActionReportData.setBfDateCollection(bfDateTypeItems);
        }

        String caseTypeId = listingDetails.getCaseTypeId();
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        bfActionReportData.clearReportFields();
        bfActionReportData.setReportType(BROUGHT_FORWARD_REPORT);
        bfActionReportData.setDocumentName(BROUGHT_FORWARD_REPORT);
        bfActionReportData.setOffice(office);
        bfActionReportData.setListingDate(caseData.getListingDate());
        bfActionReportData.setListingDateFrom(caseData.getListingDateFrom());
        bfActionReportData.setListingDateTo(caseData.getListingDateTo());
        return bfActionReportData;
    }

    private static void addBfDateTypeItems(SubmitEvent submitEvent, ListingData listingData,
        List<BFDateTypeItem> bfDateTypeItems) {
        if (!CollectionUtils.isEmpty(submitEvent.getCaseData().getBfActions())) {
            for (var bfActionTypeItem : submitEvent.getCaseData().getBfActions()) {
                var bfDateTypeItem = getBFDateTypeItem(bfActionTypeItem, listingData,
                    submitEvent.getCaseData());
                if (bfDateTypeItem != null && bfDateTypeItem.getValue() != null) {
                    bfDateTypeItems.add(bfDateTypeItem);
                }
            }
        }
    }

    private static BFDateTypeItem getBFDateTypeItem(BFActionTypeItem bfActionTypeItem,
                                                    ListingData listingData, CaseData caseData) {
        var bfActionType = bfActionTypeItem.getValue();
        if (!isNullOrEmpty(bfActionType.getBfDate()) && isNullOrEmpty(bfActionType.getCleared())) {
            var bfDate = ReportHelper.getFormattedLocalDate(bfActionType.getBfDate());
            boolean isValidBfDate = ReportHelper.validateMatchingDate(listingData, bfDate);

            if (isValidBfDate) {
                return createBFDateTypeItem(bfActionTypeItem, bfDate, caseData.getEthosCaseReference());
            }
        }
        return null;
    }

    private static BFDateTypeItem createBFDateTypeItem(BFActionTypeItem bfActionTypeItem, String bfDate,
                                             String ethosCaseReference) {
        var bfActionType = bfActionTypeItem.getValue();
        var bfDateType = new BFDateType();
        bfDateType.setCaseReference(ethosCaseReference);

        if (!Strings.isNullOrEmpty(bfActionType.getAllActions())) {
            bfDateType.setBroughtForwardAction(bfActionType.getAllActions());
        } else if (!Strings.isNullOrEmpty(bfActionType.getCwActions())) {
            bfDateType.setBroughtForwardAction(bfActionType.getCwActions());
        }

        bfDateType.setBroughtForwardEnteredDate(bfActionType.getDateEntered());
        bfDateType.setBroughtForwardDate(bfDate);
        bfDateType.setBroughtForwardDateReason(bfActionType.getNotes());
        var bfDateTypeItem = new BFDateTypeItem();
        bfDateTypeItem.setId(String.valueOf(bfActionTypeItem.getId()));
        bfDateTypeItem.setValue(bfDateType);
        return bfDateTypeItem;
    }
}
