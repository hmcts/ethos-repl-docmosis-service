package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_BY_HEARING_VENUE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;

public class ClaimsByHearingVenueReport {
    private static final String NULL_STRING_VALUE = "Null";
    private final ClaimsByHearingVenueReportDataSource dataSource;

    public ClaimsByHearingVenueReport(ClaimsByHearingVenueReportDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ClaimsByHearingVenueReportData generateReport(ClaimsByHearingVenueReportParams reportParams) {
        var submitEvents = dataSource.getData(
            UtilHelper.getListingCaseTypeId(reportParams.getCaseTypeId()),
                reportParams.getDateFrom(), reportParams.getDateTo());
        var claimsByHearingVenueReportData = initReport(reportParams.getCaseTypeId());

        setReportListingDate(claimsByHearingVenueReportData, reportParams.getDateFrom(),
                reportParams.getDateTo(), reportParams.getHearingType());
        claimsByHearingVenueReportData.setReportPrintedOnDescription(
                getReportedOnDetail(reportParams.getUserFullName()));

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            setReportData(submitEvents, claimsByHearingVenueReportData);
        }

        return claimsByHearingVenueReportData;
    }

    private ClaimsByHearingVenueReportData initReport(String caseTypeId) {
        var reportData =  new ClaimsByHearingVenueReportData();
        reportData.setOffice(UtilHelper.getListingCaseTypeId(caseTypeId));
        reportData.setReportType(CLAIMS_BY_HEARING_VENUE_REPORT);
        reportData.setDocumentName(CLAIMS_BY_HEARING_VENUE_REPORT);
        return reportData;
    }

    private String getReportedOnDetail(String userName) {
        return "Reported on: " + UtilHelper.formatCurrentDate(LocalDate.now()) + "   By: " + userName;
    }

    private String getReportTitle(String reportPeriod, String officeName) {
        return "   Period: " + reportPeriod + "       Office: " + officeName;
    }

    private void setReportListingDate(ClaimsByHearingVenueReportData reportData,
                                      String listingDateFrom, String listingDateTo, String hearingDateType) {
        if (SINGLE_HEARING_DATE_TYPE.equals(hearingDateType)) {
            reportData.setListingDate(ReportHelper.getFormattedLocalDate(listingDateFrom));
            reportData.setListingDateFrom(null);
            reportData.setListingDateTo(null);
            reportData.setHearingDateType(hearingDateType);
            var reportedOn = "On " + UtilHelper.listingFormatLocalDate(
                    ReportHelper.getFormattedLocalDate(listingDateFrom));
            reportData.setReportPeriodDescription(getReportTitle(reportedOn, reportData.getOffice()));
        } else {
            reportData.setListingDate(null);
            reportData.setListingDateFrom(ReportHelper.getFormattedLocalDate(listingDateFrom));
            reportData.setListingDateTo(ReportHelper.getFormattedLocalDate(listingDateTo));
            reportData.setHearingDateType(hearingDateType);
            var reportedBetween = "Between " + UtilHelper.listingFormatLocalDate(reportData.getListingDateFrom())
                    + " and " + UtilHelper.listingFormatLocalDate(reportData.getListingDateTo());
            reportData.setReportPeriodDescription(getReportTitle(reportedBetween, reportData.getOffice()));
        }
    }

    private void setReportData(List<ClaimsByHearingVenueSubmitEvent> submitEvents,
                               ClaimsByHearingVenueReportData reportData) {

        for (var submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            var currentReportDetail = new ClaimsByHearingVenueReportDetail();
            currentReportDetail.setCaseReference(caseData.getEthosCaseReference());
            currentReportDetail.setDateOfReceipt(caseData.getReceiptDate());
            currentReportDetail.setClaimantPostcode(getClaimantPostcode(caseData.getClaimantType()));
            currentReportDetail.setClaimantWorkPostcode(getClaimantWorkPostcode(caseData.getClaimantWorkAddressType()));
            currentReportDetail.setRespondentPostcode(getRespondentPostcode(caseData.getRespondentCollection()));
            currentReportDetail.setRespondentET3Postcode(getRespondentET3Postcode(caseData.getRespondentCollection()));
            currentReportDetail.setManagingOffice(caseData.getManagingOffice());
            reportData.getReportDetails().add(currentReportDetail);
        }
        reportData.getReportDetails().sort(Comparator.comparing(ClaimsByHearingVenueReportDetail::getCaseReference));
    }

    private String getClaimantPostcode(ClaimantType claimantType) {
        return (claimantType != null && claimantType.getClaimantAddressUK() != null
            && StringUtils.isNotBlank(claimantType.getClaimantAddressUK().getPostCode()))
            ? claimantType.getClaimantAddressUK().getPostCode() : NULL_STRING_VALUE;
    }

    private String getClaimantWorkPostcode(ClaimantWorkAddressType claimantWorkAddressType) {
        return (claimantWorkAddressType != null && claimantWorkAddressType.getClaimantWorkAddress() != null
            && StringUtils.isNotBlank(claimantWorkAddressType.getClaimantWorkAddress().getPostCode()))
            ? claimantWorkAddressType.getClaimantWorkAddress().getPostCode() : NULL_STRING_VALUE;
    }

    private String getRespondentPostcode(List<RespondentSumTypeItem> respondentItems) {
        return (CollectionUtils.isNotEmpty(respondentItems)
            && respondentItems.get(0).getValue().getRespondentAddress() != null
            && StringUtils.isNotBlank(respondentItems.get(0).getValue()
            .getRespondentAddress().getPostCode()))
            ? respondentItems.get(0).getValue().getRespondentAddress().getPostCode() : NULL_STRING_VALUE;
    }

    private String getRespondentET3Postcode(List<RespondentSumTypeItem> respondentItems) {

        return (CollectionUtils.isNotEmpty(respondentItems)
            && respondentItems.get(0).getValue().getResponseRespondentAddress() != null
            && StringUtils.isNotBlank(respondentItems.get(0).getValue()
            .getResponseRespondentAddress().getPostCode()))
            ? respondentItems.get(0).getValue().getResponseRespondentAddress().getPostCode() : NULL_STRING_VALUE;
    }
}

