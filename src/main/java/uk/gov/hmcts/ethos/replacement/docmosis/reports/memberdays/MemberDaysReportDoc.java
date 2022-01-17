package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.text.DecimalFormat;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_OFFICE;

public class MemberDaysReportDoc {

    public StringBuilder getReportDocPart(ListingData data) {
        return getMemberDaysReport(data);
    }

    private StringBuilder getMemberDaysReport(ListingData listingData) {
        if (!(listingData instanceof MemberDaysReportData)) {
            throw new IllegalStateException(("ListingData is not instance of MemberDaysReportData"));
        }

        var reportData = (MemberDaysReportData) listingData;
        var sb = new StringBuilder();
        sb.append("\"Duration_Description\":\"").append(
            nullCheck(reportData.durationDescription)).append(NEW_LINE);
        sb.append(REPORT_OFFICE).append(nullCheck(reportData.office)).append(NEW_LINE);

        sb.append(addMemberDaysReportSummaryHeader(reportData));

        sb.append("\"memberDaySummaryItems\":[\n");
        sb.append(addMemberDaysReportSummary(reportData.getMemberDaySummaryItems()));
        sb.append("],\n");

        sb.append("\"reportDetails\":[\n");
        sb.append(addMemberDaysReportDetails(reportData.getReportDetails()));
        sb.append("],\n");

        return sb;
    }

    private static StringBuilder addMemberDaysReportSummaryHeader(MemberDaysReportData reportData) {
        var summaryHeaderContent = new StringBuilder();

        if (reportData == null) {
            return summaryHeaderContent;
        }

        summaryHeaderContent.append("\"Total_Full_Days\":\"")
            .append(nullCheck(reportData.getFullDaysTotal())).append(NEW_LINE);
        summaryHeaderContent.append("\"Total_Half_Days\":\"")
            .append(nullCheck(reportData.getHalfDaysTotal())).append(NEW_LINE);
        summaryHeaderContent.append("\"Total_Days\":\"")
            .append(nullCheck(reportData.getTotalDays())).append(NEW_LINE);

        return summaryHeaderContent;
    }

    private static StringBuilder addMemberDaysReportSummary(List<MemberDaySummaryItem> memberDaySummaryItems) {
        var reportSummaryContent = new StringBuilder();
        if (CollectionUtils.isEmpty(memberDaySummaryItems)) {
            return reportSummaryContent;
        }

        var itemsCount = memberDaySummaryItems.size();
        for (var i = 0; i < itemsCount; i++) {
            reportSummaryContent.append(getMemberDaySummaryRow(memberDaySummaryItems.get(i)));
            if ((itemsCount - i) > 1) {
                reportSummaryContent.append(",\n");
            }
        }

        return reportSummaryContent;
    }

    private static StringBuilder getMemberDaySummaryRow(MemberDaySummaryItem summaryItem) {
        var summaryRowContent = new StringBuilder();
        summaryRowContent.append("{\n\"Hearing_Date\":\"").append(
            nullCheck(summaryItem.getHearingDate())).append(NEW_LINE);
        summaryRowContent.append("\"Full_Days\":\"").append(
            nullCheck(summaryItem.fullDays)).append(NEW_LINE);
        summaryRowContent.append("\"Half_Days\":\"").append(
            nullCheck(summaryItem.halfDays)).append(NEW_LINE);
        summaryRowContent.append("\"Total_Days\":\"").append(nullCheck(summaryItem.totalDays))
            .append("\"\n}");

        return summaryRowContent;
    }

    private static StringBuilder addMemberDaysReportDetails(List<MemberDaysReportDetail> reportDetails) {
        var reportDetailsContent = new StringBuilder();

        if (CollectionUtils.isEmpty(reportDetails)) {
            return reportDetailsContent;
        }

        var detailEntriesCount = reportDetails.size();
        for (var i = 0; i < detailEntriesCount; i++) {
            reportDetailsContent.append(getMemberDayReportDetailRow(reportDetails.get(i)));
            if ((detailEntriesCount - i) > 1) {
                reportDetailsContent.append(",\n");
            }
        }

        return reportDetailsContent;
    }

    private static StringBuilder getMemberDayReportDetailRow(MemberDaysReportDetail reportDetail) {
        var detailRowContent = new StringBuilder();
        detailRowContent.append("{\n\"Detail_Hearing_Date\":\"")
            .append(nullCheck(reportDetail.getHearingDate()))
            .append(NEW_LINE);
        detailRowContent.append("\"Employee_Member\":\"")
            .append(nullCheck(reportDetail.getEmployeeMember()))
            .append(NEW_LINE);
        detailRowContent.append("\"Employer_Member\":\"")
            .append(nullCheck(reportDetail.getEmployerMember()))
            .append(NEW_LINE);
        detailRowContent.append("\"Case_Reference\":\"")
            .append(nullCheck(reportDetail.getCaseReference()))
            .append(NEW_LINE);
        detailRowContent.append("\"Hearing_Number\":\"")
            .append(nullCheck(reportDetail.getHearingNumber()))
            .append(NEW_LINE);
        detailRowContent.append("\"Hearing_Type\":\"")
            .append(nullCheck(reportDetail.getHearingType()))
            .append(NEW_LINE);
        detailRowContent.append("\"Hearing_Clerk\":\"")
            .append(nullCheck(reportDetail.getHearingClerk()))
            .append(NEW_LINE);
        var durationInMinutes = Double.parseDouble(reportDetail.getHearingDuration());
        detailRowContent.append("\"Hearing_Duration\":\"")
            .append(nullCheck(String.valueOf(new DecimalFormat("#").format(durationInMinutes))))
            .append("\"\n}");
        return detailRowContent;
    }

}
