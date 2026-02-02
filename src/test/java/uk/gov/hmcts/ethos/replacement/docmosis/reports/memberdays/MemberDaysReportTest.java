package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;

public class MemberDaysReportTest {
    private static final String SIT_ALONE_PANEL = "Sit Alone";
    private static final String FULL_PANEL = "Full Panel";
    private List<SubmitEvent> submitEvents;
    private ListingDetails listingDetails;

    @Before
    public void setUp() {
        listingDetails = new ListingDetails();
        var listingData = new ListingData();
        listingData.setListingDateFrom("2019-12-08");
        listingData.setListingDateTo("2019-12-20");
        listingData.setListingVenue("Leeds");
        listingData.setReportType(MEMBER_DAYS_REPORT);
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);

        listingDetails.setCaseData(listingData);
        listingDetails.setCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID);
        listingDetails.setJurisdiction("EMPLOYMENT");

        var submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseId(1);
        submitEvent1.setState(ACCEPTED_STATE);

        var caseData = new CaseData();
        caseData.setEthosCaseReference("1800522/2020");
        caseData.setReceiptDate("2018-08-10");
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setDateAccepted("2018-08-10");
        caseData.setPreAcceptCase(casePreAcceptType);

        var dateListedType = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType.setHearingClerk("Clerk A");
        dateListedType.setHearingRoomGlasgow("Tribunal 4");
        dateListedType.setHearingAberdeen("AberdeenVenue");
        dateListedType.setHearingVenueDay("Aberdeen");
        dateListedType.setListedDate("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingStart("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingBreak("2019-12-11T12:11:00");
        dateListedType.setHearingTimingResume("2019-12-11T12:11:00");
        dateListedType.setHearingTimingFinish("2019-12-11T14:11:00.000");
        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setId("12300");
        dateListedTypeItem.setValue(dateListedType);

        var dateListedType1 = new DateListedType();
        dateListedType1.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType1.setHearingClerk("Clerk Man");
        dateListedType1.setHearingRoomGlasgow("Tribunal 55");
        dateListedType1.setHearingAberdeen("Test Venue");
        dateListedType1.setHearingVenueDay("Test");
        dateListedType1.setListedDate("2019-12-10T12:11:00.000");
        dateListedType1.setHearingTimingStart("2019-12-10T11:00:00.000");
        dateListedType1.setHearingTimingBreak("2019-12-10T12:00:00");
        dateListedType1.setHearingTimingResume("2019-12-10T13:00:00");
        dateListedType1.setHearingTimingFinish("2019-12-10T14:00:00.000");
        var dateListedTypeItem1 = new DateListedTypeItem();
        dateListedTypeItem1.setId("12400");
        dateListedTypeItem1.setValue(dateListedType1);

        var dateListedType2 = new DateListedType();
        dateListedType2.setHearingStatus(HEARING_STATUS_LISTED);
        dateListedType2.setHearingClerk("Clerk Space");
        dateListedType2.setHearingRoomGlasgow("Tribunal 22");
        dateListedType2.setHearingAberdeen("Venue");
        dateListedType2.setHearingVenueDay("Aberdeen");
        dateListedType2.setListedDate("2019-12-12T12:11:30.000");
        dateListedType2.setHearingTimingStart("2019-12-12T12:30:00.000");
        dateListedType2.setHearingTimingBreak("2019-12-12T12:30:00");
        dateListedType2.setHearingTimingResume("2019-12-12T12:30:00");
        dateListedType2.setHearingTimingFinish("2019-12-12T14:30:00.000");
        var dateListedTypeItem2 = new DateListedTypeItem();
        dateListedTypeItem2.setId("12500");
        dateListedTypeItem2.setValue(dateListedType2);

        var dateListedType3 = new DateListedType();
        dateListedType3.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType3.setHearingClerk("Clerk3");
        dateListedType3.setHearingRoomGlasgow("Tribunal 6");
        dateListedType3.setHearingAberdeen("AberdeenVenue2");
        dateListedType3.setHearingVenueDay("Aberdeen");
        dateListedType3.setListedDate("2019-12-13T12:11:55.000");
        dateListedType3.setHearingTimingStart("2019-12-13T14:11:55.000");
        dateListedType3.setHearingTimingBreak("2019-12-13T15:11:55");
        dateListedType3.setHearingTimingResume("2019-12-13T15:30:55");
        dateListedType3.setHearingTimingFinish("2019-12-13T16:30:55.000");
        var dateListedTypeItem3 = new DateListedTypeItem();
        dateListedTypeItem3.setId("12600");
        dateListedTypeItem3.setValue(dateListedType3);
        
        HearingType hearingType = new HearingType();
        hearingType.setHearingNumber("33");
        hearingType.setHearingSitAlone(FULL_PANEL);
        hearingType.setHearingVenue("Aberdeen");
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        hearingType.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingType.setHearingERMember("er member 0");
        hearingType.setHearingEEMember("ee member 0");
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setId("12345");
        hearingTypeItem.setValue(hearingType);
        hearingType.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem,
            dateListedTypeItem1, dateListedTypeItem2, dateListedTypeItem3)));

        var hearingTypeItems = new ArrayList<HearingTypeItem>();
        hearingTypeItems.add(hearingTypeItem);
        caseData.setHearingCollection(hearingTypeItems);
        submitEvent1.setCaseData(caseData);

        var caseData2 = new CaseData();
        caseData2.setEthosCaseReference("1800522/2020");
        caseData2.setReceiptDate("2018-08-10");
        var casePreAcceptType2 = new CasePreAcceptType();
        casePreAcceptType2.setDateAccepted("2018-08-10");
        caseData2.setPreAcceptCase(casePreAcceptType2);
        caseData2.setEcmCaseType(SINGLE_CASE_TYPE);

        HearingType hearingType2 = new HearingType();
        hearingType2.setHearingNumber("53");
        hearingType2.setHearingSitAlone(SIT_ALONE_PANEL);
        hearingType2.setHearingVenue("Aberdeen");
        hearingType2.setHearingEstLengthNum("3");
        hearingType2.setHearingEstLengthNumType("hours");
        hearingType2.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        HearingTypeItem hearingTypeItem2 = new HearingTypeItem();
        hearingTypeItem2.setId("12345000");
        hearingType2.setHearingEEMember("ee member 2");
        hearingType2.setHearingERMember("er member 2");
        hearingType2.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem,
            dateListedTypeItem1, dateListedTypeItem2, dateListedTypeItem3)));
        hearingTypeItem2.setValue(hearingType2);

        var hearingTypeItems2 = new ArrayList<HearingTypeItem>();
        hearingTypeItems2.add(hearingTypeItem2);
        caseData2.setHearingCollection(hearingTypeItems2);

        var caseData3 = new CaseData();
        caseData3.setEthosCaseReference("1800522/2020");
        caseData3.setReceiptDate("2018-08-12");
        
        var submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseId(2);
        submitEvent2.setState(ACCEPTED_STATE);
        submitEvent2.setCaseData(caseData2);

        var casePreAcceptType3 = new CasePreAcceptType();
        casePreAcceptType3.setDateAccepted("2018-08-12");
        caseData3.setPreAcceptCase(casePreAcceptType3);
        caseData3.setEcmCaseType(SINGLE_CASE_TYPE);
        HearingType hearingType3 = new HearingType();
        hearingType3.setHearingNumber("56");
        hearingType3.setHearingSitAlone(FULL_PANEL);
        hearingType3.setHearingVenue("Aberdeen");
        hearingType3.setHearingEstLengthNum("1");
        hearingType3.setHearingEstLengthNumType("hours");
        hearingType3.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingType3.setHearingEEMember("ee member 1");
        hearingType3.setHearingERMember("er member 1");
        HearingTypeItem hearingTypeItem3 = new HearingTypeItem();
        hearingTypeItem3.setId("1234500033");

        var hearingTypeItems3 = new ArrayList<HearingTypeItem>();
        hearingTypeItems3.add(hearingTypeItem3);
        var dateListedType6 = new DateListedType();
        dateListedType6.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType6.setHearingClerk("Clerk3");
        dateListedType6.setHearingRoomGlasgow("Tribunal 6");
        dateListedType6.setHearingAberdeen("AberdeenVenue2");
        dateListedType6.setHearingVenueDay("Aberdeen");
        dateListedType6.setListedDate("2019-12-14T12:11:55.000");
        dateListedType6.setHearingTimingStart("2019-12-14T13:11:55.000");
        dateListedType6.setHearingTimingBreak("2019-12-14T15:11:55");
        dateListedType6.setHearingTimingResume("2019-12-14T15:30:55");
        dateListedType6.setHearingTimingFinish("2019-12-14T19:30:55.000");
        var dateListedTypeItem6 = new DateListedTypeItem();
        dateListedTypeItem6.setId("12600334");
        dateListedTypeItem6.setValue(dateListedType6);

        hearingType3.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem2, dateListedTypeItem3,
            dateListedTypeItem6)));
        hearingTypeItem3.setValue(hearingType3);
        caseData3.setHearingCollection(hearingTypeItems3);

        var submitEvent3 = new SubmitEvent();
        submitEvent3.setCaseId(3);
        submitEvent3.setState(ACCEPTED_STATE);
        submitEvent3.setCaseData(caseData3);
        submitEvents = new ArrayList<>();
        submitEvents.add(submitEvent1);
        submitEvents.add(submitEvent2);
        submitEvents.add(submitEvent3);
    }

    @Test
    public void shouldReturnMembersDayReportType() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        assertEquals(MEMBER_DAYS_REPORT, resultListingData.getReportType());
    }

    @Test
    public void shouldReturnZeroReportDetailsEntriesForEmptySubmitEvents() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, null);
        var actualHeardHearingsCount = resultListingData.getReportDetails().size();
        var expectedHeardHearingsCount = 0;
        assertEquals(expectedHeardHearingsCount, actualHeardHearingsCount);
    }

    @Test
    public void shouldIncludeOnlyCasesWithHeardHearingStatus() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualHeardHearingsCount = resultListingData.getReportDetails().size();
        var expectedHeardHearingsCount = 5;
        assertEquals(expectedHeardHearingsCount, actualHeardHearingsCount);
    }

    @Test
    public void shouldIncludeOnlyCasesWithFullPanelHearing() {
        var memberDaysReport = new MemberDaysReport();
        List<Long> validHearingsCountList = new ArrayList<>();
        submitEvents.forEach(s -> validHearingsCountList.add(getValidHearingsInCurrentSubmitEvent(s)));
        var expectedFullPanelHearingsCount = validHearingsCountList.stream().filter(x -> x > 0).count();
        var expectedReportDateType = "Range";
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualFullPanelHearingsCount = resultListingData.getReportDetails()
            .stream().map(MemberDaysReportDetail::getParentHearingId)
            .toList().stream().distinct().count();
        var actualReportDateType = resultListingData.getHearingDateType();
        assertEquals(expectedFullPanelHearingsCount, actualFullPanelHearingsCount);
        assertEquals(expectedReportDateType, actualReportDateType);
    }

    @Test
    public void shouldReturnZeroCasesWhenForNoHearingsWithFullPanelHearing() {
        var memberDaysReport = new MemberDaysReport();
        
        submitEvents.forEach(s -> s.getCaseData().getHearingCollection()
            .forEach(h -> h.getValue().setHearingSitAlone(SIT_ALONE_PANEL)));
        var expectedReportDateType = "Range";
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualFullPanelHearingsCount = resultListingData.getReportDetails()
            .stream().map(MemberDaysReportDetail::getParentHearingId)
            .toList().stream().distinct().count();
        var actualReportDateType = resultListingData.getHearingDateType();
        assertEquals(0, actualFullPanelHearingsCount);
        assertEquals(expectedReportDateType, actualReportDateType);
    }

    @Test
    public void shouldIncludeOnlyCasesWithValidHearingDates() {
        var memberDaysReport = new MemberDaysReport();
        var thirdSubmitEvent = submitEvents.get(2);
        var caseData3FirstHearing = thirdSubmitEvent.getCaseData().getHearingCollection().getFirst();
        var dateListedTypeToSetToInvalidRange = caseData3FirstHearing.getValue()
            .getHearingDateCollection().get(2).getValue();
        dateListedTypeToSetToInvalidRange.setListedDate("2019-12-29T12:11:55.000");
        var dateListedTypeItems = extractDateListedTypeItems(submitEvents);
        var expectedValidHearingDatesCount = dateListedTypeItems.stream().distinct().count();
        var expectedReportDateType = "Range";
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualValidHearingDatesCount = resultListingData.getReportDetails().size();
        var actualReportDateType = resultListingData.getHearingDateType();
        assertEquals(expectedValidHearingDatesCount, actualValidHearingDatesCount);
        assertEquals(expectedReportDateType, actualReportDateType);
    }

    private Long getValidHearingsInCurrentSubmitEvent(SubmitEvent submitEvent) {
        return submitEvent.getCaseData().getHearingCollection().stream()
            .filter(h -> FULL_PANEL.equals(h.getValue().getHearingSitAlone())).count();
    }

    @Test
    public void shouldReturnCorrectHearingDateCountForListedDateWithSpaceAndMilliseconds() {
        var memberDaysReport = new MemberDaysReport();
        var thirdSubmitEvent = submitEvents.get(2);
        var caseData3FirstHearing = thirdSubmitEvent.getCaseData().getHearingCollection().getFirst();
        caseData3FirstHearing.getValue().getHearingDateCollection().getFirst()
            .getValue().setListedDate("2019-12-16 12:11:55.000");
        var dateListedTypeItems = extractDateListedTypeItems(submitEvents);
        var expectedValidHearingDatesCount = dateListedTypeItems.stream().distinct().count();
        var expectedReportDateType = "Range";
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualValidHearingDatesCount = resultListingData.getReportDetails().size();
        var actualReportDateType = resultListingData.getHearingDateType();
        assertEquals(expectedValidHearingDatesCount, actualValidHearingDatesCount);
        assertEquals(expectedReportDateType, actualReportDateType);
    }

    @Test
    public void shouldReturnZeroHearingDurationForNullHearingTimingStart() {
        submitEvents.remove(2);
        submitEvents.remove(1);
        var caseData = submitEvents.getFirst().getCaseData();
        // submitEvent3 has 4 hearingDates with 3 "Heard" status and one "Listed". Hence, the returned result
        // has to have three hearingDate entries.
        // and setting the first hearingDate HearingTimingStart time to null
        // should exclude it from the returned result
        var caseData3FirstHearingType = caseData.getHearingCollection().getFirst().getValue();
        var firstHearingDate = caseData3FirstHearingType.getHearingDateCollection().getFirst();
        firstHearingDate.getValue().setHearingTimingStart(null);
        long expectedHearingDatesCount = 3;
        var expectedHearingDateEntry1Duration = 0;
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualHearingDatesCount = resultListingData.getReportDetails().stream().distinct().count();
        // As the result valid hearing dates get chronologically sorted in ascending order, the 1st element should
        // be the entry with null HearingTimingStart and, hence, zero hearing duration
        var actualHearingDateEntry1Duration = resultListingData.getReportDetails().get(1).getHearingDuration();
        assertEquals(expectedHearingDatesCount, actualHearingDatesCount);
        assertEquals(String.valueOf(expectedHearingDateEntry1Duration), actualHearingDateEntry1Duration);
    }

    @Test
    public void shouldReturnZeroHearingDurationForNullHearingTimingFinish() {
        submitEvents.remove(2);
        submitEvents.remove(1);
        var caseData = submitEvents.getFirst().getCaseData();
        var caseData3FirstHearingType = caseData.getHearingCollection().getFirst().getValue();
        var firstHearingDate = caseData3FirstHearingType.getHearingDateCollection().getFirst();
        firstHearingDate.getValue().setHearingTimingFinish(null);
        long expectedHearingDatesCount = 3;
        var expectedHearingDateEntry1Duration = 0;
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualHearingDatesCount = resultListingData.getReportDetails().stream().distinct().count();
        var actualHearingDateEntry1Duration = resultListingData.getReportDetails().get(1).getHearingDuration();
        assertEquals(expectedHearingDatesCount, actualHearingDatesCount);
        assertEquals(String.valueOf(expectedHearingDateEntry1Duration), actualHearingDateEntry1Duration);
    }

    @Test
    public void shouldReturnCorrectHearingDurationDaysCount() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        assertEquals("8", resultListingData.getHalfDaysTotal());
        assertEquals("2", resultListingData.getFullDaysTotal());
        assertEquals("6.0", resultListingData.getTotalDays());
        var uniqueDaysCount = resultListingData.getMemberDaySummaryItems().size();
        assertEquals("4", String.valueOf(uniqueDaysCount));
        var totalDetailEntriesCount = resultListingData.getReportDetails().size();
        assertEquals("5", String.valueOf(totalDetailEntriesCount));
    }

    @Test
    public void shouldReturnSortedSummaryItemsList() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var memberDaySummaryItems = resultListingData.getMemberDaySummaryItems();
        assertEquals("10 December 2019", memberDaySummaryItems.getFirst().getHearingDate());
        assertEquals("11 December 2019", memberDaySummaryItems.get(1).getHearingDate());
        assertEquals("13 December 2019", memberDaySummaryItems.get(2).getHearingDate());
        assertEquals("14 December 2019", memberDaySummaryItems.get(3).getHearingDate());
    }

    @Test
    public void shouldReturnSortedDetailedItemsList() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var reportDetails = resultListingData.getReportDetails();
        assertEquals("10 December 2019", reportDetails.getFirst().getHearingDate());
        assertEquals("1800522/2020", reportDetails.getFirst().getCaseReference());
        assertEquals("33", reportDetails.getFirst().getHearingNumber());
        assertEquals("120", reportDetails.getFirst().getHearingDuration());
        assertEquals("14 December 2019", reportDetails.get(4).getHearingDate());
        assertEquals("1800522/2020", reportDetails.get(4).getCaseReference());
        assertEquals("56", reportDetails.get(4).getHearingNumber());
        assertEquals("360", reportDetails.get(4).getHearingDuration());
    }

    @Test
    public void shouldReturnOnlySelectedSingleDateDetailedItemsList() {
        listingDetails.getCaseData().setListingDate("2019-12-11");
        listingDetails.getCaseData().setListingDateFrom(null);
        listingDetails.getCaseData().setListingDateTo(null);
        listingDetails.getCaseData().setListingVenue("Leeds");
        listingDetails.getCaseData().setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var reportDetails = resultListingData.getReportDetails();
        assertEquals("11 December 2019", reportDetails.getFirst().getHearingDate());
        assertEquals("1800522/2020", reportDetails.getFirst().getCaseReference());
        assertEquals("33", reportDetails.getFirst().getHearingNumber());
        assertEquals("120", reportDetails.getFirst().getHearingDuration());
    }

    private List<DateListedTypeItem> extractDateListedTypeItems(List<SubmitEvent> submitEvents) {
        List<DateListedTypeItem> dateListedTypeItems = new ArrayList<>();
        for (var submitEvent : submitEvents) {
            for (var hearing : submitEvent.getCaseData().getHearingCollection()) {
                var validDates = hearing.getValue().getHearingDateCollection()
                    .stream().filter(h -> isValidDate(h.getValue().getListedDate(),
                        listingDetails.getCaseData().getListingDateFrom(),
                        listingDetails.getCaseData().getListingDateTo()))
                    .toList();
                dateListedTypeItems.addAll(validDates);
            }
        }
        return dateListedTypeItems;
    }

    private boolean isValidDate(String dateListed, String dateFrom, String dateTo) {
        if (dateListed == null) {
            return false;
        }
        String datePart = null;
        if (dateListed.contains("T")) {
            datePart = dateListed.split("T")[0];
        }
        if (dateListed.contains(" ")) {
            datePart = dateListed.split(" ")[0];
        }

        Assert.assertNotNull(datePart);
        var hearingListedDate = LocalDate.parse(datePart);
        var hearingDatesFrom = LocalDate.parse(dateFrom);
        var hearingDatesTo = LocalDate.parse(dateTo);
        return (hearingListedDate.isEqual(hearingDatesFrom) || hearingListedDate.isAfter(hearingDatesFrom))
            && (hearingListedDate.isEqual(hearingDatesTo) || hearingListedDate.isBefore(hearingDatesTo));
    }
}