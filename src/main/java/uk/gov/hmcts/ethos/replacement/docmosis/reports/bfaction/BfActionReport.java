package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItemComparator;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@SuppressWarnings({"PMD.LawOfDemeter"})
@Slf4j
@Service
public class BfActionReport {

    public BfActionReportData runReport(ListingDetails listingDetails,
                                        List<SubmitEvent> submitEvents,
                                        String userName) {
        BfActionReportData bfActionReportData = new BfActionReportData();
        ListingData caseData = listingDetails.getCaseData();
        bfActionReportData.setHearingDateType(caseData.getHearingDateType());

        if (!CollectionUtils.isEmpty(submitEvents)) {
            List<BFDateTypeItem> bfDateTypeItems = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                addBfDateTypeItems(submitEvent, caseData, bfDateTypeItems);
            }
            bfDateTypeItems.sort(new BFDateTypeItemComparator());
            bfActionReportData.setBfDateCollection(bfDateTypeItems);
        }
        bfActionReportData.clearReportFields();
        bfActionReportData.setReportType(Constants.BROUGHT_FORWARD_REPORT);
        bfActionReportData.setDocumentName(Constants.BROUGHT_FORWARD_REPORT);
        bfActionReportData.setReportPrintedOnDescription(
                getReportedOnDetail(userName));
        bfActionReportData.setOffice(listingDetails.getCaseTypeId());
        setPeriodDescription(bfActionReportData, listingDetails);
        bfActionReportData.setListingDate(caseData.getListingDate());
        bfActionReportData.setListingDateFrom(caseData.getListingDateFrom());
        bfActionReportData.setListingDateTo(caseData.getListingDateTo());
        return bfActionReportData;
    }

    private void setPeriodDescription(BfActionReportData bfActionReportData, ListingDetails listingDetails) {
        ReportParams genericReportParams = ReportHelper.getListingDateRangeForSearch(listingDetails);
        bfActionReportData.setReportPeriodDescription(ReportHelper.getReportListingDate(bfActionReportData,
                genericReportParams.getDateFrom(),
                genericReportParams.getDateTo(),
                listingDetails.getCaseData().getHearingDateType(),
                UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId())));
    }

    private String getReportedOnDetail(String userName) {
        return "Reported on: " + UtilHelper.formatCurrentDate(LocalDate.now()) + "   By: " + userName;
    }

    private void addBfDateTypeItems(SubmitEvent submitEvent, ListingData listingData,
                                    List<BFDateTypeItem> bfDateTypeItems) {
        if (!CollectionUtils.isEmpty(submitEvent.getCaseData().getBfActions())) {
            for (BFActionTypeItem bfActionTypeItem : submitEvent.getCaseData().getBfActions()) {
                BFDateTypeItem bfDateTypeItem = getBFDateTypeItem(bfActionTypeItem, listingData,
                        submitEvent.getCaseData());
                if (bfDateTypeItem != null && bfDateTypeItem.getValue() != null) {
                    bfDateTypeItems.add(bfDateTypeItem);
                }
            }
        }
    }

    private BFDateTypeItem getBFDateTypeItem(BFActionTypeItem bfActionTypeItem,
                                             ListingData listingData, CaseData caseData) {
        BFActionType bfActionType = bfActionTypeItem.getValue();
        if (!isNullOrEmpty(bfActionType.getBfDate()) && isNullOrEmpty(bfActionType.getCleared())) {
            String bfDate = bfActionType.getBfDate();
            boolean isValidBfDate = ReportHelper.validateMatchingDate(listingData, bfDate);

            if (isValidBfDate) {
                return createBFDateTypeItem(bfActionTypeItem, bfDate, caseData.getEthosCaseReference());
            }
        }
        return null;
    }

    private BFDateTypeItem createBFDateTypeItem(BFActionTypeItem bfActionTypeItem, String bfDate,
                                                String ethosCaseReference) {
        BFActionType bfActionType = bfActionTypeItem.getValue();
        BFDateType bfDateType = new BFDateType();
        bfDateType.setCaseReference(ethosCaseReference);

        if (!isNullOrEmpty(bfActionType.getAllActions())) {
            bfDateType.setBroughtForwardAction(bfActionType.getAllActions());
        } else if (!isNullOrEmpty(bfActionType.getCwActions())) {
            bfDateType.setBroughtForwardAction(bfActionType.getCwActions());
        }

        bfDateType.setBroughtForwardEnteredDate(ReportHelper.getFormattedLocalDate(bfActionType.getDateEntered()));
        bfDateType.setBroughtForwardDate(ReportHelper.getFormattedLocalDate(bfDate));

        if (!isNullOrEmpty(bfActionType.getNotes())) {
            String bfReason = bfActionType.getNotes().replace("\n", ". ");
            bfDateType.setBroughtForwardDateReason(bfReason);
        }

        BFDateTypeItem bfDateTypeItem = new BFDateTypeItem();
        bfDateTypeItem.setId(String.valueOf(bfActionTypeItem.getId()));
        bfDateTypeItem.setValue(bfDateType);
        return bfDateTypeItem;
    }
}