package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_OUTCOME_SUCCESSFUL_AT_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

public class MemberDaysReportTest {

    private List<SubmitEvent> submitEvents;
    private ListingDetails listingDetails;
    private static String SIT_ALONE_PANEL = "Sit Alone";
    private static String FULL_PANEL = "Full Panel";
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

        var dateListedTypeItem = new DateListedTypeItem();

        var dateListedType = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType.setHearingClerk("Clerk A");
        dateListedType.setHearingRoomGlasgow("Tribunal 4");
        dateListedType.setHearingAberdeen("AberdeenVenue");
        dateListedType.setHearingVenueDay("Aberdeen");
        dateListedType.setListedDate("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingStart("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingBreak("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingResume("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingFinish("2019-12-11T14:11:00.000");
        dateListedTypeItem.setId("12300");
        dateListedTypeItem.setValue(dateListedType);

        var dateListedTypeItem1 = new DateListedTypeItem();

        var dateListedType1 = new DateListedType();
        dateListedType1.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType1.setHearingClerk("Clerk B");
        dateListedType1.setHearingRoomGlasgow("Tribunal 4");
        dateListedType1.setHearingAberdeen("AberdeenVenue");
        dateListedType1.setHearingVenueDay("Aberdeen");
        dateListedType1.setListedDate("2019-12-10T12:11:00.000");
        dateListedType1.setHearingTimingStart("2019-12-10T11:00:00.000");
        dateListedType1.setHearingTimingBreak("2019-12-10T12:00:00.000");
        dateListedType1.setHearingTimingResume("2019-12-10T13:00:00.000");
        dateListedType1.setHearingTimingFinish("2019-12-10T14:00:00.000");
        dateListedTypeItem1.setId("12400");
        dateListedTypeItem1.setValue(dateListedType1);

        var dateListedTypeItem2 = new DateListedTypeItem();

        var dateListedType2 = new DateListedType();
        dateListedType2.setHearingStatus(HEARING_STATUS_LISTED);
        dateListedType2.setHearingClerk("Clerk1");
        dateListedType2.setHearingRoomGlasgow("Tribunal 5");
        dateListedType2.setHearingAberdeen("AberdeenVenue2");
        dateListedType2.setHearingVenueDay("Aberdeen");
        dateListedType2.setListedDate("2019-12-12T12:11:30.000");
        dateListedType2.setHearingTimingStart("2019-12-12T12:30:00.000");
        dateListedType2.setHearingTimingBreak("2019-12-12T12:30:00.000");
        dateListedType2.setHearingTimingResume("2019-12-12T12:30:00.000");
        dateListedType2.setHearingTimingFinish("2019-12-12T14:30:00.000");
        dateListedTypeItem2.setId("12500");
        dateListedTypeItem2.setValue(dateListedType2);

        var dateListedTypeItem3 = new DateListedTypeItem();
        var dateListedType3 = new DateListedType();
        dateListedType3.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType3.setHearingClerk("Clerk3");
        dateListedType3.setHearingRoomGlasgow("Tribunal 6");
        dateListedType3.setHearingAberdeen("AberdeenVenue2");
        dateListedType3.setHearingVenueDay("Aberdeen");
        dateListedType3.setListedDate("2019-12-13T12:11:55.000");
        dateListedType3.setHearingTimingStart("2019-12-13T14:11:55.000");
        dateListedType3.setHearingTimingBreak("2019-12-13T15:11:55.000");
        dateListedType3.setHearingTimingResume("2019-12-13T15:30:55.000");
        dateListedType3.setHearingTimingFinish("2019-12-13T16:30:55.000");
        dateListedTypeItem3.setId("12600");
        dateListedTypeItem3.setValue(dateListedType3);

        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = new HearingType();
        hearingType.setHearingNumber("33");
        hearingType.setHearingSitAlone(FULL_PANEL);
        hearingType.setHearingVenue("Aberdeen");
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        hearingType.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingType.setHearingERMember("er memb 0");
        hearingType.setHearingEEMember("ee memb 0");
        hearingTypeItem.setId("12345");
        hearingTypeItem.setValue(hearingType);
        hearingType.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem,
            dateListedTypeItem1, dateListedTypeItem2, dateListedTypeItem3)));

        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("ABC");
        jurCodesType.setJudgmentOutcome(JURISDICTION_OUTCOME_SUCCESSFUL_AT_HEARING);
        jurCodesTypeItem.setId("000");
        jurCodesTypeItem.setValue(jurCodesType);

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

        HearingTypeItem hearingTypeItem2 = new HearingTypeItem();
        HearingType hearingType2 = new HearingType();
        hearingType2.setHearingNumber("53");
        hearingType2.setHearingSitAlone(SIT_ALONE_PANEL);
        hearingType2.setHearingVenue("Aberdeen");
        hearingType2.setHearingEstLengthNum("3");
        hearingType2.setHearingEstLengthNumType("hours");
        hearingType2.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingTypeItem2.setId("12345000");
        hearingType2.setHearingEEMember("ee memb 2");
        hearingType2.setHearingERMember("er memb 2");
        hearingType2.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem,
            dateListedTypeItem1, dateListedTypeItem2, dateListedTypeItem3)));
        hearingTypeItem2.setValue(hearingType2);

        var hearingTypeItems2 = new ArrayList<HearingTypeItem>();
        hearingTypeItems2.add(hearingTypeItem2);
        caseData2.setHearingCollection(hearingTypeItems2);

        var dateListedTypeItem5 = new DateListedTypeItem();
        var dateListedType5 = new DateListedType();
        dateListedType5.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType5.setHearingClerk("Clerk3");
        dateListedType5.setHearingRoomGlasgow("Tribunal 6");
        dateListedType5.setHearingAberdeen("AberdeenVenue2");
        dateListedType5.setHearingVenueDay("Aberdeen");
        dateListedType5.setListedDate("2019-12-14T12:11:55.000");
        dateListedType5.setHearingTimingStart("2019-12-14T14:11:55.000");
        dateListedType5.setHearingTimingBreak("2019-12-14T15:11:55.000");
        dateListedType5.setHearingTimingResume("2019-12-14T15:30:55.000");
        dateListedType5.setHearingTimingFinish("2019-12-14T16:30:55.000");
        dateListedTypeItem5.setId("12600");
        dateListedTypeItem5.setValue(dateListedType5);

        var submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseId(2);
        submitEvent2.setState(ACCEPTED_STATE);
        submitEvent2.setCaseData(caseData2);

        var caseData3 = new CaseData();
        caseData3.setEthosCaseReference("1800522/2020");
        caseData3.setReceiptDate("2018-08-12");
        var casePreAcceptType3 = new CasePreAcceptType();
        casePreAcceptType3.setDateAccepted("2018-08-12");
        caseData3.setPreAcceptCase(casePreAcceptType3);
        caseData3.setEcmCaseType(SINGLE_CASE_TYPE);

        HearingTypeItem hearingTypeItem3 = new HearingTypeItem();
        HearingType hearingType3 = new HearingType();
        hearingType3.setHearingNumber("56");
        hearingType3.setHearingSitAlone(FULL_PANEL);
        hearingType3.setHearingVenue("Aberdeen");
        hearingType3.setHearingEstLengthNum("1");
        hearingType3.setHearingEstLengthNumType("hours");
        hearingType3.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingType3.setHearingEEMember("ee memb 1");
        hearingType3.setHearingERMember("er memb 1");
        hearingTypeItem3.setId("1234500033");

        var hearingTypeItems3 = new ArrayList<HearingTypeItem>();
        hearingTypeItems3.add(hearingTypeItem3);
        caseData3.setHearingCollection(hearingTypeItems3);

        var dateListedTypeItem6 = new DateListedTypeItem();
        var dateListedType6 = new DateListedType();
        dateListedType6.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType6.setHearingClerk("Clerk3");
        dateListedType6.setHearingRoomGlasgow("Tribunal 6");
        dateListedType6.setHearingAberdeen("AberdeenVenue2");
        dateListedType6.setHearingVenueDay("Aberdeen");
        dateListedType6.setListedDate("2019-12-14T12:11:55.000");
        dateListedType6.setHearingTimingStart("2019-12-14T13:11:55.000");
        dateListedType6.setHearingTimingBreak("2019-12-14T15:11:55.000");
        dateListedType6.setHearingTimingResume("2019-12-14T15:30:55.000");
        dateListedType6.setHearingTimingFinish("2019-12-14T19:30:55.000");
        dateListedTypeItem6.setId("12600334");
        dateListedTypeItem6.setValue(dateListedType6);

        hearingType3.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem2, dateListedTypeItem3,
            dateListedTypeItem6)));
        hearingTypeItem3.setValue(hearingType3);

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
    public void shouldIncludeOnlyCasesWithHeardHearingStatus() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var actualHeardHearingsCount  = resultListingData.getReportDetails().stream().count();
        var expectedHeardHearingsCount = 5;
        assertEquals(expectedHeardHearingsCount, actualHeardHearingsCount);
    }

    @Test
    public void shouldIncludeOnlyCasesWithFullPanelHearing() {
        var memberDaysReport = new MemberDaysReport();
        List<Integer> validHearingsCountList = new ArrayList<>();

        //filter listed hearings with full panel
        submitEvents.stream().forEach(s ->
            validHearingsCountList.add(s.getCaseData().getHearingCollection().stream()
                .filter(h -> FULL_PANEL.equals(h.getValue().getHearingSitAlone()))
                .collect(Collectors.toList()).size()));

        var expectedFullPanelHearingsCount = validHearingsCountList.stream().filter(x->x.intValue() > 0).count();
        var expectedReportDateType = "Range";
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);

        var actualFullPanelHearingsCount  = resultListingData.getReportDetails()
            .stream().map(MemberDaysReportDetail::getParentHearingId)
            .collect(Collectors.toList()).stream().distinct().count();
        var actualReportDateType = resultListingData.getHearingDateType();

        assertEquals(expectedFullPanelHearingsCount, actualFullPanelHearingsCount);
        assertEquals(expectedReportDateType, actualReportDateType);
    }

    @Test
    public void shouldIncludeOnlyCasesWithValidHearingDates() {
        var memberDaysReport = new MemberDaysReport();
        var thridCase = submitEvents.get(2);
        var hearingWithInvalidDate = thridCase.getCaseData().getHearingCollection().get(0);

        var dateListedTypeItem6 = new DateListedTypeItem();
        var dateListedType6 = new DateListedType();
        dateListedType6.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType6.setHearingClerk("Clerk55");
        dateListedType6.setHearingRoomGlasgow("Tribunal 66");
        dateListedType6.setHearingAberdeen("AberdeenVenue26");
        dateListedType6.setHearingVenueDay("Aberdeen6");
        dateListedType6.setListedDate("2019-12-29T12:11:55.000");
        dateListedType6.setHearingTimingStart("2019-12-29T13:11:55.000");
        dateListedType6.setHearingTimingBreak("2019-12-29T15:11:55.000");
        dateListedType6.setHearingTimingResume("2019-12-29T15:30:55.000");
        dateListedType6.setHearingTimingFinish("2019-12-29T19:30:55.000");
        dateListedTypeItem6.setId("1268899999");
        dateListedTypeItem6.setValue(dateListedType6);

        hearingWithInvalidDate.getValue().getHearingDateCollection().add(dateListedTypeItem6);

        List<Integer> validHearingsCountList = new ArrayList<>();
        submitEvents.stream().forEach(caseData ->
            validHearingsCountList.add(caseData.getCaseData().getHearingCollection().stream()
                .filter(h -> FULL_PANEL.equals(h.getValue().getHearingSitAlone()))
                .collect(Collectors.toList()).size()));

        List<DateListedTypeItem> dateListedTypeItems = new ArrayList<>();
        //filter listed hearings with full panel
        for(var submitEvent :submitEvents) {
            for(var hearing : submitEvent.getCaseData().getHearingCollection()) {
                var validDates = hearing.getValue().getHearingDateCollection()
                    .stream().filter(h -> isValidDate(h.getValue().getListedDate(),
                        listingDetails.getCaseData().getListingDateFrom(),
                        listingDetails.getCaseData().getListingDateTo()))
                    .collect(Collectors.toList());
                validDates.forEach(vd -> dateListedTypeItems.add(vd));
            }
        }

        var expectedValidHearingDatesCount = dateListedTypeItems.stream().distinct().count();
        var expectedReportDateType = "Range";
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);

        var actualValidHearingDatesCount  = resultListingData.getReportDetails().size();
        var actualReportDateType = resultListingData.getHearingDateType();

        assertEquals(expectedValidHearingDatesCount, actualValidHearingDatesCount);
        assertEquals(expectedReportDateType, actualReportDateType);
    }

    private boolean isValidDate(String dateListed, String dateFrom, String dateTo){
        var listed = LocalDate.parse(dateListed.split("T")[0]);
        var from = LocalDate.parse(dateFrom);
        var to = LocalDate.parse(dateTo);

        return listed.compareTo(from) >= 0 && listed.compareTo(to) <= 0;
    }

    @Test
    public void shouldReturnZeroHearingDurationForNullHearingTimingStart() {
        var memberDaysReport = new MemberDaysReport();
        submitEvents.remove(2);
        submitEvents.remove(1);

        var dateListedType = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType.setHearingClerk("Clerk A");
        dateListedType.setHearingRoomGlasgow("Tribunal 4");
        dateListedType.setHearingAberdeen("AberdeenVenue");
        dateListedType.setHearingVenueDay("Aberdeen");
        dateListedType.setListedDate("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingStart(null);
        dateListedType.setHearingTimingFinish("2019-12-11T14:11:00.000");

        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setId("12300");
        dateListedTypeItem.setValue(dateListedType);
        var caseData = submitEvents.get(0).getCaseData();
       var hearingType = caseData.getHearingCollection().get(0).getValue();
        hearingType.getHearingDateCollection().clear();
        hearingType.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem)));

        long expectedHearingDatesCount = 1;
        var expectedHearingDateEntry1Duration = 0;
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);

        var actualHearingDatesCount  = resultListingData.getReportDetails().stream().distinct().count();
        var actualHearingDateEntry1Duration = resultListingData.getReportDetails().get(0).getHearingDuration();

        assertEquals(expectedHearingDatesCount, actualHearingDatesCount);
        assertEquals(String.valueOf(expectedHearingDateEntry1Duration), actualHearingDateEntry1Duration);
    }

    @Test
    public void shouldReturnZeroHearingDurationForNullHearingTimingFinish() {
        var memberDaysReport = new MemberDaysReport();
        submitEvents.remove(2);
        submitEvents.remove(1);

        var dateListedType = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType.setHearingClerk("Clerk A");
        dateListedType.setHearingRoomGlasgow("Tribunal 4");
        dateListedType.setHearingAberdeen("AberdeenVenue");
        dateListedType.setHearingVenueDay("Glasgow");
        dateListedType.setListedDate("2019-12-11T12:11:00.000");
        dateListedType.setHearingTimingStart("2019-12-11T14:11:00.000");
        dateListedType.setHearingTimingFinish(null);

        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setId("12456600");
        dateListedTypeItem.setValue(dateListedType);
        var caseData = submitEvents.get(0).getCaseData();
        var hearingType = caseData.getHearingCollection().get(0).getValue();
        hearingType.getHearingDateCollection().clear();
        hearingType.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem)));

        long expectedHearingDatesCount = 1;
        var expectedHearingDateEntry1Duration = 0;
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);

        var actualHearingDatesCount  = resultListingData.getReportDetails().stream().distinct().count();
        var actualHearingDateEntry1Duration = resultListingData.getReportDetails().get(0).getHearingDuration();

        assertEquals(expectedHearingDatesCount, actualHearingDatesCount);
        assertEquals(String.valueOf(expectedHearingDateEntry1Duration), actualHearingDateEntry1Duration);
    }

    @Test
    public void shouldReturnCorrectHearingDurationDaysCount() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);

        var halfDaysCount = resultListingData.getHalfDaysTotal();
        assertEquals("8", halfDaysCount);

        var fullDaysCount = resultListingData.getFullDaysTotal();
        assertEquals("2", fullDaysCount);

        var totalDaysCount = resultListingData.getTotalDays();
        assertEquals("6.0", totalDaysCount);

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

        assertEquals("10 December 2019", memberDaySummaryItems.get(0).getHearingDate());
        assertEquals("11 December 2019", memberDaySummaryItems.get(1).getHearingDate());
        assertEquals("13 December 2019", memberDaySummaryItems.get(2).getHearingDate());
        assertEquals("14 December 2019", memberDaySummaryItems.get(3).getHearingDate());
    }

    @Test
    public void shouldReturnSortedDetailedItemsList() {
        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var reportDetails = resultListingData.getReportDetails();

        assertEquals("10 December 2019", reportDetails.get(0).getHearingDate());
        assertEquals("1800522/2020", reportDetails.get(0).getCaseReference());
        assertEquals("33", reportDetails.get(0).getHearingNumber());
        assertEquals("120", reportDetails.get(0).getHearingDuration());

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
        listingDetails.getCaseData().setReportType(MEMBER_DAYS_REPORT);
        listingDetails.getCaseData().setHearingDateType(SINGLE_HEARING_DATE_TYPE);

        var memberDaysReport = new MemberDaysReport();
        var resultListingData = memberDaysReport.runReport(listingDetails, submitEvents);
        var reportDetails = resultListingData.getReportDetails();

        assertEquals("11 December 2019", reportDetails.get(0).getHearingDate());
        assertEquals("1800522/2020", reportDetails.get(0).getCaseReference());
        assertEquals("33", reportDetails.get(0).getHearingNumber());
        assertEquals("120", reportDetails.get(0).getHearingDuration());
    }
}