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
    private List<SubmitEvent> submitEvents;
    private ListingDetails listingDetails;
    private static final String FULL_PANEL = "Full Panel";

    public MemberDaysReportData runReport(ListingDetails listings, List<SubmitEvent> submitEventList) {
        submitEvents = submitEventList;
        listingDetails = listings;

        var memberDaysReportData = initiateReport(listingDetails.getCaseTypeId());
        addReportDetails(memberDaysReportData);
        addReportSummary(memberDaysReportData);
        addReportSummaryHeader(memberDaysReportData);

        return memberDaysReportData;
    }

    private MemberDaysReportData initiateReport(String caseTypeId) {
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        var reportData = new MemberDaysReportData();
        reportData.setOffice(office);
        reportData.setDurationDescription(getDurationText(reportData));
        reportData.setReportType(MEMBER_DAYS_REPORT);
        return reportData;
    }

    private String getDurationText(MemberDaysReportData reportData) {
        var description = "";
        var currentCaseData = listingDetails.getCaseData();
        if (currentCaseData.getHearingDateType().equals("Single")) {
            description = "On " + currentCaseData.getListingDate();
        } else if (currentCaseData.getHearingDateType().equals("Range")) {
            description = "Between " + currentCaseData.getListingDateFrom()
                    + " and " + currentCaseData.getListingDateTo();
        }
        reportData.setHearingDateType(currentCaseData.getHearingDateType());
        return description;
    }

    private void addReportDetails(MemberDaysReportData reportData) {
        List<MemberDaysReportDetail> interimReportDetails = new ArrayList<>();
        for (var submitEvent : submitEvents) {

            if (!isValidCase(submitEvent)) {
                continue;
            }

            var caseData = submitEvent.getCaseData();
            log.info("Adding case {} to Member Days report", caseData.getEthosCaseReference());

            var hearings = getValidHearings(caseData);
            extractValidHearingsFromCurrentCase(hearings, caseData, interimReportDetails);
        }
        var sortedReportDetails = interimReportDetails.stream()
            .sorted((o1, o2) -> o1.comparedTo(o2)).collect(Collectors.toList());
        reportData.getReportDetails().clear();
        sortedReportDetails.forEach(d -> reportData.getReportDetails().add(d));
    }

    private void extractValidHearingsFromCurrentCase(List<HearingTypeItem> hearings,
                                                     CaseData caseData,
                                                     List<MemberDaysReportDetail> interimReportDetails) {
        log.info("Adding hearings from the current case {} to the valid hearings list",
            caseData.getEthosCaseReference());

        for (var hearingItem : hearings) {
            var dateListedTypeItems = filterHearingsWithHeardStatus(hearingItem);
            var dateListedItems = filterValidHearingDates(dateListedTypeItems);

            if (!CollectionUtils.isEmpty(dateListedItems)) {
                for (var listedItem : dateListedItems) {
                    var hearingDate = getLocalDate(listedItem.getValue().getListedDate());
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

    private LocalDate getLocalDate(String listedDate) {
        var dateParts = splitDateTimeString(removeMilliSeconds(listedDate));
        var formattedDate = generateLocalDateTime(dateParts);
        if (formattedDate == null) {
            return null;
        }
        return formattedDate.toLocalDate();
    }

    private List<DateListedTypeItem> filterValidHearingDates(List<DateListedTypeItem> dateListedTypeItems) {
        //if search is not date range, exclude other dates from current case
        if (SINGLE_HEARING_DATE_TYPE.equals(listingDetails.getCaseData().getHearingDateType())) {
            return dateListedTypeItems.stream()
                .filter(x -> x.getValue().getListedDate().split("T")[0]
                    .equals(listingDetails.getCaseData().getListingDate()))
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
        var dateListedType = dateListedTypeItem.getValue();
        var startTimeComponents = splitDateTimeString(removeMilliSeconds(
            dateListedType.getHearingTimingStart()));
        var hearingTimingFinish = splitDateTimeString(removeMilliSeconds(
            dateListedType.getHearingTimingFinish()));
        var hearingTimingBreak = splitDateTimeString(removeMilliSeconds(
            dateListedType.getHearingTimingBreak()));
        var hearingTimingResume = splitDateTimeString(removeMilliSeconds(
            dateListedType.getHearingTimingResume()));

        LocalDateTime start = generateLocalDateTime(startTimeComponents);
        LocalDateTime finish = generateLocalDateTime(hearingTimingFinish);
        LocalDateTime hearingBreak = generateLocalDateTime(hearingTimingBreak);
        LocalDateTime hearingResume = generateLocalDateTime(hearingTimingResume);

        var startFinishDuration = Duration.between(start, finish).toMinutes();
        var breakResumeDuration = Duration.between(hearingBreak, hearingResume).toMinutes();
        var duration = startFinishDuration - breakResumeDuration;

        return String.valueOf(duration);
    }

    private LocalDateTime generateLocalDateTime(String[] dateParts) {
        LocalDateTime localDateTime = null;
        if (dateParts != null) {
            localDateTime = LocalDateTime.of(Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]),
                Integer.parseInt(dateParts[3]), Integer.parseInt(dateParts[4]),
                Integer.parseInt(dateParts[5]));
        }
        return localDateTime;
    }

    private String removeMilliSeconds(String hearingTime) {
        String hearingTimeWithNoMill = null;
        if (hearingTime.contains(".000")) {
            hearingTimeWithNoMill = hearingTime.replace(".000", "");
        } else {
            hearingTimeWithNoMill = hearingTime;
        }

        return hearingTimeWithNoMill;
    }

    private String[] splitDateTimeString(String hearingTime) {
        String[] dateTimeParts = null;
        if (hearingTime != null) {
            String formattedHearingTime = "";
            if (hearingTime.contains("T")) {
                formattedHearingTime = hearingTime.replace("T", "-");
            }
            if (formattedHearingTime.contains(":")) {
                formattedHearingTime = formattedHearingTime.replace(":", "-");
            }
            dateTimeParts = formattedHearingTime.split("-");
        }
        return dateTimeParts;
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
