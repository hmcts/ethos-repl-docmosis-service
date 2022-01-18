package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;

@Service
@Slf4j
public class MemberDaysReport {
    private static final int MINUTES = 60;
    private static final String FULL_PANEL = "Full Panel";
    private static final String SINGLE_DATE_HEARING_REPORT = "Single";
    private static final String DATE_RANGE_HEARING_REPORT = "Range";

    public MemberDaysReportData runReport(ListingDetails listings, List<SubmitEvent> submitEventList) {
        var memberDaysReportData = initiateReport(listings);
        addReportDetails(memberDaysReportData, submitEventList, listings.getCaseData());
        addReportSummary(memberDaysReportData);
        addReportSummaryHeader(memberDaysReportData);

        return memberDaysReportData;
    }

    private MemberDaysReportData initiateReport(ListingDetails listingDetails) {
        String caseTypeId = listingDetails.getCaseTypeId();
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        var reportData = new MemberDaysReportData();
        reportData.setOffice(office);
        reportData.setDurationDescription(getDurationText(listingDetails.getCaseData()));
        reportData.setHearingDateType(listingDetails.getCaseData().getHearingDateType());
        reportData.setReportType(MEMBER_DAYS_REPORT);
        return reportData;
    }

    private String getDurationText(ListingData currentCaseData) {
        var description = "";
        if (currentCaseData.getHearingDateType().equals(SINGLE_DATE_HEARING_REPORT)) {
            description = "On " + currentCaseData.getListingDate();
        } else if (currentCaseData.getHearingDateType().equals(DATE_RANGE_HEARING_REPORT)) {
            description = "Between " + currentCaseData.getListingDateFrom()
                    + " and " + currentCaseData.getListingDateTo();
        }

        return description;
    }

    private void addReportDetails(MemberDaysReportData reportData, List<SubmitEvent> submitEvents,
                                  ListingData listingData) {
        List<MemberDaysReportDetail> interimReportDetails = new ArrayList<>();
        for (var submitEvent : submitEvents) {
            if (!isValidCase(submitEvent)) {
                continue;
            }

            var caseData = submitEvent.getCaseData();
            var hearings = getValidHearings(caseData);
            extractValidHearingsFromCurrentCase(hearings, caseData, interimReportDetails, listingData);
        }
        var sortedReportDetails = interimReportDetails.stream()
            .sorted((o1, o2) -> o1.comparedTo(o2)).collect(Collectors.toList());
        reportData.getReportDetails().clear();
        sortedReportDetails.forEach(d -> reportData.getReportDetails().add(d));
    }

    private void extractValidHearingsFromCurrentCase(List<HearingTypeItem> hearings,
                                                     CaseData caseData,
                                                     List<MemberDaysReportDetail> interimReportDetails,
                                                     ListingData listingData) {
        for (var hearingItem : hearings) {
            var dateListedTypeItems = filterHearingsWithHeardStatus(hearingItem);
            var dateListedItems = filterValidHearingDates(dateListedTypeItems, listingData);

            if (!CollectionUtils.isEmpty(dateListedItems)) {
                for (var listedItem : dateListedItems) {
                    var hearingDatePart = getDatePart(listedItem.getValue().getListedDate());
                    var hearingDate = hearingDatePart != null ?  LocalDate.parse(hearingDatePart) : null;
                    if (hearingDate != null) {
                        var reportDetail = new MemberDaysReportDetail();
                        reportDetail.setSortingHearingDate(hearingDate.toString());
                        reportDetail.setHearingDate(UtilHelper.formatCurrentDate(hearingDate));
                        reportDetail.setEmployeeMember(hearingItem.getValue().getHearingEEMember());
                        reportDetail.setEmployerMember(hearingItem.getValue().getHearingERMember());
                        reportDetail.setCaseReference(caseData.getEthosCaseReference());
                        reportDetail.setHearingNumber(hearingItem.getValue().getHearingNumber());
                        reportDetail.setHearingType(hearingItem.getValue().getHearingType());
                        reportDetail.setHearingClerk(caseData.getClerkResponsible());
                        reportDetail.setHearingDuration(getHearingDuration(listedItem));
                        reportDetail.setParentHearingId(hearingItem.getId());
                        interimReportDetails.add(reportDetail);
                    }
                }
            }
        }
    }

    private String getDatePart(String dateTimeValue) {
        return dateTimeValue.split("T")[0];
    }

    private List<DateListedTypeItem> filterValidHearingDates(List<DateListedTypeItem> dateListedTypeItems,
                                                             ListingData listingData) {
        //if search is not date range, exclude other dates from current case
        if (SINGLE_HEARING_DATE_TYPE.equals(listingData.getHearingDateType())) {
            return dateListedTypeItems.stream()
                .filter(x -> x.getValue().getListedDate().split("T")[0]
                    .equals(listingData.getListingDate()))
                .collect(Collectors.toList());
        }

        return dateListedTypeItems;
    }

    private List<DateListedTypeItem> filterHearingsWithHeardStatus(HearingTypeItem hearingItem) {
        return hearingItem.getValue().getHearingDateCollection()
            .stream().filter(x -> HEARING_STATUS_HEARD.equals(x.getValue().getHearingStatus()))
            .collect(Collectors.toList());
    }

    private String getHearingDuration(DateListedTypeItem dateListedTypeItem) {
        var hearingStart = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingStart()
            .replace(".000", ""));
        var hearingFinish = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingFinish()
            .replace(".000", ""));
        var hearingBreak = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingBreak()
            .replace(".000", ""));
        var hearingResume = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingResume()
            .replace(".000", ""));

        var startFinishDuration = Duration.between(hearingStart, hearingFinish).toMinutes();
        var breakResumeDuration = Duration.between(hearingBreak, hearingResume).toMinutes();
        var duration = startFinishDuration - breakResumeDuration;

        return String.valueOf(duration);
    }

    private List<HearingTypeItem> getValidHearings(CaseData caseData) {
        List<HearingTypeItem> validHearingTypeItems = new ArrayList<>();
        var hearingTypeItems = caseData.getHearingCollection();
        // Filter valid hearing - i.e. hearings with "Heard" status and
        // "Full Panel" for the 'Sit Alone or Full Panel' property
        for (var hearingTypeItem : hearingTypeItems) {
            var validDateListedTypeItems = hearingTypeItem.getValue()
                .getHearingDateCollection().stream()
                    .filter(x -> HEARING_STATUS_HEARD.equals(x.getValue().getHearingStatus())
                        && FULL_PANEL.equals(hearingTypeItem.getValue().getHearingSitAlone()))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(validDateListedTypeItems)) {
                validHearingTypeItems.add(hearingTypeItem);
            }
        }

        return validHearingTypeItems;
    }

    private boolean isValidCase(SubmitEvent submitEvent) {
        if (CollectionUtils.isEmpty(submitEvent.getCaseData().getHearingCollection())) {
            return false;
        }

        var hearings = submitEvent.getCaseData().getHearingCollection();

        for (var hearingItem : hearings) {
            var validDates = hearingItem.getValue().getHearingDateCollection().stream()
                    .filter(x -> HEARING_STATUS_HEARD.equals(x.getValue().getHearingStatus()))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(validDates)) {
                return true;
            }
        }

        return false;
    }

    private void addReportSummary(MemberDaysReportData reportData) {
        var groupedByDate = reportData.getReportDetails()
                .stream().distinct().collect(groupingBy(MemberDaysReportDetail::getHearingDate));
        var uniqueDatesList = groupedByDate.keySet().stream().sorted()
            .collect(Collectors.toList());

        for (var listingDate : uniqueDatesList) {
            var memberDaySummaryItem = new MemberDaySummaryItem();
            memberDaySummaryItem.setHearingDate(listingDate);
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
        var totalDays = fullDaysTotal + (halfDaysTotal / 2);
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
        return StringUtils.isEmpty(currentMember) ? 0 : 1;
    }

    private void addReportSummaryHeader(MemberDaysReportData reportData) {
        var summaryItems = reportData.getMemberDaySummaryItems();

        var fullDaysTotal = String.valueOf(summaryItems.stream()
                .map(item -> Integer.parseInt(item.fullDays))
                .reduce(0, Integer::sum));
        reportData.setFullDaysTotal(fullDaysTotal);

        var halfDaysTotal = String.valueOf(summaryItems.stream()
            .map(item -> Integer.parseInt(item.halfDays))
            .reduce(0, Integer::sum));
        reportData.setHalfDaysTotal(halfDaysTotal);

        var totalDays = String.valueOf(summaryItems.stream()
            .map(item -> Double.parseDouble(item.totalDays))
            .reduce(0.0, Double::sum));
        reportData.setTotalDays(totalDays);
    }

}
