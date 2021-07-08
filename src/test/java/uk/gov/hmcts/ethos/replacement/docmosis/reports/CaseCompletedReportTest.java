package uk.gov.hmcts.ethos.replacement.docmosis.reports;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

public class CaseCompletedReportTest {

    @Test
    public void testReportHeaderTotalsAreZeroIfNoCasesExist() {
        // given no cases exist
        // when we generate report data
        // then totals are all zero

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();

        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData listingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(listingData);
    }

    @Test
    public void testIgnoreCaseIfNotClosed() {
        // given case is not closed
        // when we generate report data
        // then no data returned

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();

        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(SUBMITTED_STATE));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData listingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(listingData);
    }

    @Test
    public void testIgnoreCaseIfPositionTypeInvalid() {
        // given case is closed
        // given position type is invalid
        // when we generate report data
        // then no data returned
        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();

        listingDetails.setCaseData(listingData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(CLOSED_STATE));

        List<String> invalidPositionTypes = Arrays.asList(POSITION_TYPE_CASE_INPUT_IN_ERROR,
                POSITION_TYPE_CASE_TRANSFERRED_SAME_COUNTRY,
                POSITION_TYPE_CASE_TRANSFERRED_OTHER_COUNTRY);

        CaseData caseData = submitEvents.get(0).getCaseData();
        for (String positionType : invalidPositionTypes) {
            caseData.setPositionType(positionType);
            CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
            ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

            verifyReportHeaderIsZero(reportListingData);
        }
    }

    @Test
    public void testIgnoreCaseIfJurisdictionOutcomeInvalid() {
        // given case is closed
        // given position type is valid
        // given jurisdiction outcome is invalid
        // when we generate report data
        // then no data returned
        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();

        listingDetails.setCaseData(listingData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(CLOSED_STATE));

        List<String> invalidOutcomes = Arrays.asList("This is not a valid outcome", null);

        CaseData caseData = submitEvents.get(0).getCaseData();
        caseData.setJurCodesCollection(new ArrayList<>());
        for (String outcome : invalidOutcomes) {
            caseData.getJurCodesCollection().add(createJurisdiction(outcome));
            CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
            ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

            verifyReportHeaderIsZero(reportListingData);
            caseData.getJurCodesCollection().clear();
        }
    }

    @Test
    public void testIgnoreCaseIfItContainsNoHearings() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has no hearings
        // when we generate report data
        // then no data returned

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();

        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, Collections.emptyList()));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData listingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(listingData);
    }

    @Test
    public void testIgnoreCaseIfHearingTypeInvalid() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has a hearing with a type that is invalid
        // when we generate report data
        // then no data returned

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_REMEDY, null, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(reportListingData);
    }

    @Test
    public void testIgnoreCaseIfHearingListingDateNotInSearchRange() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has a hearing that was disposed
        // given case has a hearing listing date that is different to report search date
        // when we generate report data
        // then no data returned

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-02T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(reportListingData);
    }

    @Test
    public void testIgnoreCaseIfHearingNotDisposed() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has a hearing with a valid type
        // given case has a hearing that was not disposed
        // when we generate report data
        // then no data returned

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-01T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, NO));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(reportListingData);
    }

    @Test
    public void testValidNoneConciliationTrackCaseIsAddedToReport() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has a hearing with a valid type
        // given case has a hearing that was disposed
        // given case has a hearing listed date that is within report search range
        // given case is for none conciliation track
        // when we generate report data
        // then we have some data

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-01T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_NO_CONCILIATION));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        ReportHeaderValues reportHeaderValues = new ReportHeaderValues(
                1, 1, 1.0, "Newcastle",
                1, 1, 1.0,
                0, 0, 0,
                0, 0, 0,
                0, 0, 0);
        verifyReportHeader(reportListingData, reportHeaderValues);
        verifyReportDetails(reportListingData, 1);
    }

    @Test
    public void testValidFastConciliationTrackCaseIsAddedToReport() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has a hearing with a valid type
        // given case has a hearing that was disposed
        // given case has a hearing listed date that is within report search range
        // given case is for fast conciliation track
        // when we generate report data
        // then we have some data

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-01T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_FAST_TRACK));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        ReportHeaderValues reportHeaderValues = new ReportHeaderValues(
                1, 1, 1.0, "Newcastle",
                0, 0, 0,
                1, 1, 1.0,
                0, 0, 0,
                0, 0, 0);
        verifyReportHeader(reportListingData, reportHeaderValues);
        verifyReportDetails(reportListingData, 1);
    }

    @Test
    public void testValidStdConciliationTrackCaseIsAddedToReport() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has a hearing with a valid type
        // given case has a hearing that was disposed
        // given case has a hearing listed date that is within report search range
        // given case is for standard conciliation track
        // when we generate report data
        // then we have some data

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-01T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_STANDARD_TRACK));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        ReportHeaderValues reportHeaderValues = new ReportHeaderValues(
                1, 1, 1.0, "Newcastle",
                0, 0, 0,
                0, 0, 0,
                1, 1, 1.0,
                0, 0, 0);
        verifyReportHeader(reportListingData, reportHeaderValues);
        verifyReportDetails(reportListingData, 1);
    }

    @Test
    public void testValidOpenConciliationTrackCaseIsAddedToReport() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has a hearing with a valid type
        // given case has a hearing that was disposed
        // given case has a hearing listed date that is within report search range
        // given case is for open conciliation track
        // when we generate report data
        // then we have some data

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-01T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_OPEN_TRACK));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        ReportHeaderValues reportHeaderValues = new ReportHeaderValues(
                1, 1, 1.0, "Newcastle",
                0, 0, 0,
                0, 0, 0,
                0, 0, 0,
                1, 1, 1.0);
        verifyReportHeader(reportListingData, reportHeaderValues);
        verifyReportDetails(reportListingData, 1);
    }

    @Test
    public void testMultipleCasesAreSummedUpInTotals() {
        // given we have multiple cases that are valid
        // when we generate report data
        // then we have data for all cases

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-01T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_NO_CONCILIATION));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_FAST_TRACK));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_STANDARD_TRACK));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_OPEN_TRACK));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        ReportHeaderValues reportHeaderValues = new ReportHeaderValues(
                4, 4, 1.0, "Newcastle",
                1, 1, 1.0,
                1, 1, 1.0,
                1, 1, 1.0,
                1, 1, 1.0);
        verifyReportHeader(reportListingData, reportHeaderValues);
        verifyReportDetails(reportListingData, 4);
    }

    @Test
    public void testMultipleCasesOnlyValidAreSummedUpInTotals() {
        // given we have two cases that are valid
        // given we have two cases that are not valid
        // when we generate report data
        // then we have data for only valid cases

        String searchDate = "1970-01-01";
        String listingDate = "1970-01-01T00:00:00";

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingData.setListingDate(searchDate);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, listingDate, YES));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_NO_CONCILIATION));
        submitEvents.add(createSubmitEvent(SUBMITTED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_FAST_TRACK));
        submitEvents.add(createSubmitEvent(CLOSED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_STANDARD_TRACK));
        submitEvents.add(createSubmitEvent(SUBMITTED_STATE, JURISDICTION_OUTCOME_DISMISSED_AT_HEARING, hearings, CONCILIATION_TRACK_OPEN_TRACK));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        ReportHeaderValues reportHeaderValues = new ReportHeaderValues(
                2, 2, 1.0, "Newcastle",
                1, 1, 1.0,
                0, 0, 0,
                1, 1, 1.0,
                0, 0, 0);
        verifyReportHeader(reportListingData, reportHeaderValues);
        verifyReportDetails(reportListingData, 2);
    }

    private SubmitEvent createSubmitEvent(String state) {
        return createSubmitEvent(state, null, null);
    }

    private SubmitEvent createSubmitEvent(String state, String jurisdictionOutcome, List<HearingTypeItem> hearingCollection) {
        return createSubmitEvent(state, jurisdictionOutcome, hearingCollection, null);
    }

    private SubmitEvent createSubmitEvent(String state, String jurisdictionOutcome, List<HearingTypeItem> hearingCollection, String conciliationTrack) {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setState(state);

        CaseData caseData = new CaseData();
        caseData.setConciliationTrack(conciliationTrack);
        if (jurisdictionOutcome != null) {
            caseData.setJurCodesCollection(new ArrayList<>());
            caseData.getJurCodesCollection().add(createJurisdiction(jurisdictionOutcome));
        }

        caseData.setHearingCollection(hearingCollection);

        submitEvent.setCaseData(caseData);

        return submitEvent;
    }

    private JurCodesTypeItem createJurisdiction(String outcome) {
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJudgmentOutcome(outcome);
        jurCodesTypeItem.setValue(jurCodesType);
        return jurCodesTypeItem;
    }

    private HearingTypeItem createHearing(String type, String listedDate, String disposed) {
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = new HearingType();
        hearingType.setHearingType(type);

        List<DateListedTypeItem> dateListedTypeItems = new ArrayList<>();
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingCaseDisposed(disposed);
        dateListedTypeItem.setValue(dateListedType);
        dateListedTypeItems.add(dateListedTypeItem);

        hearingType.setHearingDateCollection(dateListedTypeItems);
        hearingTypeItem.setValue(hearingType);
        return hearingTypeItem;
    }

    private List<HearingTypeItem> createHearingCollection(HearingTypeItem... hearings) {
        List<HearingTypeItem> hearingTypeItems = new ArrayList<>();
        Collections.addAll(hearingTypeItems, hearings);
        return hearingTypeItems;
    }

    private void verifyReportHeaderIsZero(ListingData listingData) {
        ReportHeaderValues reportHeaderValues = new ReportHeaderValues(
                0,0,0,"Newcastle",
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0);
        verifyReportHeader(listingData, reportHeaderValues);
        verifyReportDetails(listingData, 0);
    }

    private void verifyReportHeader(ListingData listingData, ReportHeaderValues reportHeaderValues) {
        AdhocReportType adhocReportType = listingData.getLocalReportsDetailHdr();

        // Report header
        assertEquals(String.valueOf(reportHeaderValues.casesCompletedHearingTotal), adhocReportType.getCasesCompletedHearingTotal());
        assertEquals(String.valueOf(reportHeaderValues.sessionDaysTotal), adhocReportType.getSessionDaysTotal());
        assertEquals(String.valueOf(reportHeaderValues.completedPerSessionTotal), adhocReportType.getCompletedPerSessionTotal());
        assertEquals(reportHeaderValues.reportOffice, adhocReportType.getReportOffice());

        // Conciliation - No Conciliation
        assertEquals(String.valueOf(reportHeaderValues.conNoneCasesCompletedHearing), adhocReportType.getConNoneCasesCompletedHearing());
        assertEquals(String.valueOf(reportHeaderValues.conNoneSessionDays), adhocReportType.getConNoneSessionDays());
        assertEquals(String.valueOf(reportHeaderValues.conNoneCompletedPerSession), adhocReportType.getConNoneCompletedPerSession());

        // Conciliation - Fast Track
        assertEquals(String.valueOf(reportHeaderValues.conFastCasesCompletedHearing), adhocReportType.getConFastCasesCompletedHearing());
        assertEquals(String.valueOf(reportHeaderValues.conFastSessionDays), adhocReportType.getConFastSessionDays());
        assertEquals(String.valueOf(reportHeaderValues.conFastCompletedPerSession), adhocReportType.getConFastCompletedPerSession());

        // Conciliation - Standard Track
        assertEquals(String.valueOf(reportHeaderValues.conStdCasesCompletedHearing), adhocReportType.getConStdCasesCompletedHearing());
        assertEquals(String.valueOf(reportHeaderValues.conStdSessionDays), adhocReportType.getConStdSessionDays());
        assertEquals(String.valueOf(reportHeaderValues.conStdCompletedPerSession), adhocReportType.getConStdCompletedPerSession());

        // Conciliation - Open Track
        assertEquals(String.valueOf(reportHeaderValues.conOpenCasesCompletedHearing), adhocReportType.getConOpenCasesCompletedHearing());
        assertEquals(String.valueOf(reportHeaderValues.conOpenSessionDays), adhocReportType.getConOpenSessionDays());
        assertEquals(String.valueOf(reportHeaderValues.conOpenCompletedPerSession), adhocReportType.getConOpenCompletedPerSession());
    }

    private void verifyReportDetails(ListingData listingData, int size) {
        assertEquals(size, listingData.getLocalReportsDetail().size());
    }

}
