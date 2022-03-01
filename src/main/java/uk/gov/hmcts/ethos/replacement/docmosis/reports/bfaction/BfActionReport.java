package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

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
        bfActionReportData.setHearingDateType(caseData.getHearingDateType());

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

    private void addBfDateTypeItems(SubmitEvent submitEvent, ListingData listingData,
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

    private BFDateTypeItem getBFDateTypeItem(BFActionTypeItem bfActionTypeItem,
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

    private BFDateTypeItem createBFDateTypeItem(BFActionTypeItem bfActionTypeItem, String bfDate,
                                             String ethosCaseReference) {
        var bfActionType = bfActionTypeItem.getValue();
        var bfDateType = new BFDateType();
        bfDateType.setCaseReference(ethosCaseReference);

        if (!isNullOrEmpty(bfActionType.getAllActions())) {
            bfDateType.setBroughtForwardAction(bfActionType.getAllActions());
        } else if (!isNullOrEmpty(bfActionType.getCwActions())) {
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
