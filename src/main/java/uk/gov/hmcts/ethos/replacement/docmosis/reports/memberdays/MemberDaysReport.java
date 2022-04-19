package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.groupingBy;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportCommonMethods.getHearingDurationInMinutes;

@Slf4j
public class MemberDaysReport {
    private static final int MINUTES = 60;
    private static final String FULL_PANEL = "Full Panel";
    public static final DateTimeFormatter OLD_DATE_TIME_PATTERN3 =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public MemberDaysReportData runReport(ListingDetails listings, List<SubmitEvent> submitEventList) {

        var memberDaysReportData = initiateReport(listings);

        if (!CollectionUtils.isEmpty(submitEventList)) {
            addReportDetails(memberDaysReportData, submitEventList, listings.getCaseData());
            addReportSummary(memberDaysReportData);
            addReportSummaryHeader(memberDaysReportData);
        }

        return memberDaysReportData;
    }

    private MemberDaysReportData initiateReport(ListingDetails listingDetails) {
        var office = UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId());
        var reportData = new MemberDaysReportData();
        var caseData = listingDetails.getCaseData();
        reportData.setOffice(office);
        reportData.setHearingDateType(caseData.getHearingDateType());
        reportData.setReportType(MEMBER_DAYS_REPORT);
        reportData.setDocumentName(MEMBER_DAYS_REPORT);
        reportData.setListingDate(caseData.getListingDate());
        reportData.setListingDateFrom(caseData.getListingDateFrom());
        reportData.setListingDateTo(caseData.getListingDateTo());
        return reportData;
    }

    private void addReportDetails(MemberDaysReportData reportData, List<SubmitEvent> submitEvents,
                                  ListingData listingData) {

        List<MemberDaysReportDetail> interimReportDetails = new ArrayList<>();

        for (var submitEvent : submitEvents) {
            if (CollectionUtils.isEmpty(submitEvent.getCaseData().getHearingCollection())) {
                continue;
            }
            addValidHearingsFromCurrentCase(submitEvent.getCaseData(), interimReportDetails, listingData);
        }

        var sortedReportDetails = interimReportDetails.stream()
            .sorted(MemberDaysReportDetail::comparedTo).collect(Collectors.toList());
        reportData.getReportDetails().clear();
        sortedReportDetails.forEach(d -> reportData.getReportDetails().add(d));
    }

    private void addValidHearingsFromCurrentCase(CaseData caseData,
                                                 List<MemberDaysReportDetail> reportDetails,
                                                 ListingData listingData) {
        var fullPanelHearings = caseData.getHearingCollection().stream()
            .filter(this::isFullPanelHearing).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(fullPanelHearings)) {
            for (var hearing : fullPanelHearings) {
                extractValidHearingDates(hearing, reportDetails, listingData, caseData.getEthosCaseReference());
            }
        }
    }

    private boolean isFullPanelHearing(HearingTypeItem hearing) {
        return FULL_PANEL.equals(hearing.getValue().getHearingSitAlone());
    }

    private boolean isHeardHearingDate(DateListedTypeItem hearingDate) {
        return HEARING_STATUS_HEARD.equals(hearingDate.getValue().getHearingStatus());
    }

    private void extractValidHearingDates(HearingTypeItem hearing,
                                                 List<MemberDaysReportDetail> interimReportDetails,
                                                 ListingData listingData, String ethosCaseReference) {
        var hearingDatesWithHeardStatus = hearing.getValue().getHearingDateCollection().stream()
            .filter(this::isHeardHearingDate).collect(Collectors.toList());

        for (var hearingDate : hearingDatesWithHeardStatus) {
            var currentHearingDate = ReportHelper.getFormattedLocalDate(hearingDate.getValue().getListedDate());
            if (currentHearingDate != null && isValidHearingDate(currentHearingDate, listingData)) {
                var reportDetail = new MemberDaysReportDetail();
                reportDetail.setSortingHearingDate(currentHearingDate);
                reportDetail.setHearingDate(UtilHelper.formatCurrentDate(LocalDate.parse(currentHearingDate)));
                reportDetail.setEmployeeMember(hearing.getValue().getHearingEEMember());
                reportDetail.setEmployerMember(hearing.getValue().getHearingERMember());
                reportDetail.setCaseReference(ethosCaseReference);
                reportDetail.setHearingNumber(hearing.getValue().getHearingNumber());
                reportDetail.setHearingType(hearing.getValue().getHearingType());
                reportDetail.setHearingClerk(hearingDate.getValue().getHearingClerk());
                reportDetail.setHearingDuration(getHearingDurationInMinutes(hearingDate));
                reportDetail.setParentHearingId(hearing.getId());
                interimReportDetails.add(reportDetail);
            }
        }
    }

    private boolean isValidHearingDate(String dateListed, ListingData listingData) {
        if (SINGLE_HEARING_DATE_TYPE.equals(listingData.getHearingDateType())) {
            return isDateInRange(dateListed, listingData.getListingDate(),
                listingData.getListingDate());
        } else {
            return isDateInRange(dateListed, listingData.getListingDateFrom(),
                listingData.getListingDateTo());
        }
    }

    private boolean isDateInRange(String listedDate, String dateFrom, String dateTo) {
        var hearingListedDate = LocalDate.parse(listedDate);
        var hearingDatesFrom = LocalDate.parse(dateFrom);
        var hearingDatesTo = LocalDate.parse(dateTo);

        return  (hearingListedDate.isEqual(hearingDatesFrom) ||  hearingListedDate.isAfter(hearingDatesFrom))
            && (hearingListedDate.isEqual(hearingDatesTo) || hearingListedDate.isBefore(hearingDatesTo));
    }

    private void addReportSummary(MemberDaysReportData reportData) {
        var groupedByDate = reportData.getReportDetails()
                .stream().distinct().collect(groupingBy(MemberDaysReportDetail::getSortingHearingDate));
        var uniqueDatesList = groupedByDate.keySet().stream().sorted()
            .collect(Collectors.toList());

        for (var listingDate : uniqueDatesList) {
            var memberDaySummaryItem = new MemberDaySummaryItem();
            memberDaySummaryItem.setHearingDate(UtilHelper.formatCurrentDate(LocalDate.parse(listingDate)));
            setDayCounts(groupedByDate.get(listingDate), memberDaySummaryItem);
            reportData.getMemberDaySummaryItems().add(memberDaySummaryItem);
        }
    }

    private void setDayCounts(List<MemberDaysReportDetail> reportDetails, MemberDaySummaryItem summaryItem) {
        var dayCounts = getFullMembersDayCount(reportDetails);
        var fullDaysTotal = dayCounts.get(0);
        summaryItem.setFullDays(String.valueOf(fullDaysTotal));
        var halfDaysTotal = dayCounts.get(1);
        summaryItem.setHalfDays(String.valueOf(halfDaysTotal));
        var totalDays = (double)fullDaysTotal + ((double)(halfDaysTotal) / 2.0);
        summaryItem.setTotalDays(String.valueOf(totalDays));
    }

    private List<Integer> getFullMembersDayCount(List<MemberDaysReportDetail> reportDetails) {
        int fullDayTotal = 0;
        int halfDayTotal = 0;
        for (var detail: reportDetails) {
            if ((Integer.parseInt(detail.getHearingDuration()) / MINUTES) >= 3) {
                fullDayTotal = fullDayTotal + getPanelMembersTotalDuration(detail);
            } else {
                halfDayTotal = halfDayTotal + getPanelMembersTotalDuration(detail);
            }
        }

        return List.of(fullDayTotal, halfDayTotal);
    }

    private int getPanelMembersTotalDuration(MemberDaysReportDetail currentDetail) {
        var employeeValue = getPanelMemberValue(currentDetail.getEmployeeMember());
        var employerValue = getPanelMemberValue(currentDetail.getEmployerMember());
        return employeeValue + employerValue;
    }

    private int getPanelMemberValue(String currentMember) {
        return isNullOrEmpty(currentMember) ? 0 : 1;
    }

    private void addReportSummaryHeader(MemberDaysReportData reportData) {
        var summaryItems = reportData.getMemberDaySummaryItems();

        var fullDaysTotal = String.valueOf(summaryItems.stream()
                .map(item -> Integer.parseInt(item.getFullDays()))
                .reduce(0, Integer::sum));
        reportData.setFullDaysTotal(fullDaysTotal);

        var halfDaysTotal = String.valueOf(summaryItems.stream()
            .map(item -> Integer.parseInt(item.getHalfDays()))
            .reduce(0, Integer::sum));
        reportData.setHalfDaysTotal(halfDaysTotal);

        var totalDays = String.valueOf(summaryItems.stream()
            .map(item -> Double.parseDouble(item.getTotalDays()))
            .reduce(0.0, Double::sum));
        reportData.setTotalDays(totalDays);
    }
}
