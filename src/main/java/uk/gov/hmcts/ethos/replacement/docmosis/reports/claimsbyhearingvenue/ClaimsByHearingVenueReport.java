package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueCaseData;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_BY_HEARING_VENUE_REPORT;

@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
public final class ClaimsByHearingVenueReport {
    private static final String NULL_STRING_VALUE = "Null";
    private final ClaimsByHearingVenueReportDataSource dataSource;

    public ClaimsByHearingVenueReport(ClaimsByHearingVenueReportDataSource dataSource) {

        this.dataSource = dataSource;
    }

    public ClaimsByHearingVenueReportData generateReport(ClaimsByHearingVenueReportParams reportParams,
                                                         String office) {
        String caseTypeId = reportParams.getCaseTypeId();
        var reportOffice = UtilHelper.getListingCaseTypeId(caseTypeId);
        ClaimsByHearingVenueReportData claimsByHearingVenueReportData = initReport(reportOffice);
        claimsByHearingVenueReportData.setReportPrintedOnDescription(
                getReportedOnDetail(reportParams.getUserFullName()));
        claimsByHearingVenueReportData.setOffice(reportParams.getCaseTypeId());
        List<ClaimsByHearingVenueSubmitEvent> submitEvents = dataSource.getData(
                UtilHelper.getListingCaseTypeId(reportParams.getCaseTypeId()),
                reportParams.getDateFrom(), reportParams.getDateTo());
        claimsByHearingVenueReportData.setReportPeriodDescription(ReportHelper.getReportListingDate(
                claimsByHearingVenueReportData, reportParams.getDateFrom(),
                reportParams.getDateTo(), reportParams.getHearingDateType(), office));
        if (CollectionUtils.isNotEmpty(submitEvents)) {
            setReportData(submitEvents, claimsByHearingVenueReportData);
        }

        return claimsByHearingVenueReportData;
    }

    private ClaimsByHearingVenueReportData initReport(String office) {
        ClaimsByHearingVenueReportData reportData =  new ClaimsByHearingVenueReportData();
        reportData.setOffice(office);
        reportData.setReportType(CLAIMS_BY_HEARING_VENUE_REPORT);
        reportData.setDocumentName(CLAIMS_BY_HEARING_VENUE_REPORT);
        return reportData;
    }

    private String getReportedOnDetail(String userName) {
        return "Reported on: " + UtilHelper.formatCurrentDate(LocalDate.now()) + "   By: " + userName;
    }

    private void setReportData(List<ClaimsByHearingVenueSubmitEvent> submitEvents,
                               ClaimsByHearingVenueReportData reportData) {

        for (ClaimsByHearingVenueSubmitEvent submitEvent : submitEvents) {
            ClaimsByHearingVenueCaseData caseData = submitEvent.getCaseData();
            ClaimsByHearingVenueReportDetail currentReportDetail = new ClaimsByHearingVenueReportDetail();
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
        return claimantType != null && claimantType.getClaimantAddressUK() != null
                && StringUtils.isNotBlank(claimantType.getClaimantAddressUK().getPostCode())
                ? claimantType.getClaimantAddressUK().getPostCode() : NULL_STRING_VALUE;
    }

    private String getClaimantWorkPostcode(ClaimantWorkAddressType claimantWorkAddressType) {
        return claimantWorkAddressType != null && claimantWorkAddressType.getClaimantWorkAddress() != null
                && StringUtils.isNotBlank(claimantWorkAddressType.getClaimantWorkAddress().getPostCode())
                ? claimantWorkAddressType.getClaimantWorkAddress().getPostCode() : NULL_STRING_VALUE;
    }

    private String getRespondentPostcode(List<RespondentSumTypeItem> respondentItems) {
        return CollectionUtils.isNotEmpty(respondentItems)
                && respondentItems.get(0).getValue().getRespondentAddress() != null
                && StringUtils.isNotBlank(respondentItems.get(0).getValue()
                .getRespondentAddress().getPostCode())
                ? respondentItems.get(0).getValue().getRespondentAddress().getPostCode() : NULL_STRING_VALUE;
    }

    private String getRespondentET3Postcode(List<RespondentSumTypeItem> respondentItems) {

        return CollectionUtils.isNotEmpty(respondentItems)
                && respondentItems.get(0).getValue().getResponseRespondentAddress() != null
                && StringUtils.isNotBlank(respondentItems.get(0).getValue()
                .getResponseRespondentAddress().getPostCode())
                ? respondentItems.get(0).getValue().getResponseRespondentAddress().getPostCode() : NULL_STRING_VALUE;
    }
}

