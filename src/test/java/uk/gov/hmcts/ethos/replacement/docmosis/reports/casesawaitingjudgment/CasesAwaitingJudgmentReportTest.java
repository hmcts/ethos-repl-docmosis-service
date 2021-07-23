package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_WITHDRAWN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_COSTS_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;

public class CasesAwaitingJudgmentReportTest {

    ReportDataSource reportDataSource;

    Clock clock;

    CasesAwaitingJudgmentReport casesAwaitingJudgmentReport;

    CaseDataBuilder caseDataBuilder;

    List<SubmitEvent> submitEvents = new ArrayList<>();

    final String validPositionType;
    static final String USER = "Test User";
    static final String LISTING_DATE = "1970-01-01T00:00:00.000";

    public CasesAwaitingJudgmentReportTest() {
        caseDataBuilder = new CaseDataBuilder();
        validPositionType = CasesAwaitingJudgmentReport.VALID_POSITION_TYPES.stream().findAny().orElseThrow();
    }

    @Before
    public void setup() {
        submitEvents.clear();

        reportDataSource = mock(ReportDataSource.class);
        when(reportDataSource.getData(NEWCASTLE_LISTING_CASE_TYPE_ID)).thenReturn(submitEvents);

        var now = "2021-07-31T10:00:00Z";
        clock = Clock.fixed(Instant.parse(now), ZoneId.of("UTC"));

        casesAwaitingJudgmentReport = new CasesAwaitingJudgmentReport(reportDataSource, clock);
    }

    @Test
    public void shouldNotShowClosedCase() {
        // Given a case is closed
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.buildAsSubmitEvent(CLOSED_STATE));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseWithInvalidPositionType() {
        // Given a case is not closed
        // And a case has an invalid position type
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.withPositionType("An invalid position type").buildAsSubmitEvent(ACCEPTED_STATE));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
    }

    @Test
    public void shouldNotShowCaseIfNoHearingsExist() {
        // Given a case is not closed
        // And has a valid position type
        // And has no hearings
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.withPositionType(validPositionType).buildAsSubmitEvent(ACCEPTED_STATE));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseIfNoHearingHasBeenHeard() {
        // Given a case is not closed
        // And has a valid position type
        // And has no hearing that has been heard
        // When I request report data
        // Then the case should not be in the report data

        var submitEvent = caseDataBuilder.withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_LISTED)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseIfHeardButJudgmentMade() {
        // Given a case is not closed
        // And has a valid position type
        // And has been heard
        // And has a judgment
        // When I request report data
        // Then the case should not be in the report data

        var submitEvent = caseDataBuilder.withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .withJudgment()
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldShowValidCase() {
        // Given a case is not closed
        // And has been heard
        // And is awaiting judgment
        // When I request report data
        // Then the case is in the report data

        var submitEvent = caseDataBuilder.withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());
    }

    @Test
    public void shouldShowTotalPositionValuesInSummary() {
        // Given I have 3 valid cases with position type Draft with Members
        // When I request report data
        // Then the report summary shows 3 Draft with Members
        var positionType = "Draft with members";
        submitEvents.add(createValidSubmitEvent(positionType));
        submitEvents.add(createValidSubmitEvent(positionType));
        submitEvents.add(createValidSubmitEvent(positionType));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertEquals(3, reportData.getReportDetails().size());
        assertEquals(1, reportData.getReportSummary().getPositionTypes().size());
        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType));
        assertEquals(3, reportData.getReportSummary().getPositionTypes().get(positionType).intValue());
    }

    @Test
    public void shouldShowMultiplePositionValuesInSummary() {
        // Given I have 3 valid cases with position type Draft with Members
        // And I have 2 valid cases with position type Awaiting written reasons
        // And I have 1 valid case with position type Fair copy, to chairman for signature
        // When I request report data
        // Then the report summary shows 3 rows:
        //    | Draft with members                   | 3 |
        //    | Awaiting written reasons             | 2 |
        //    | Fair copy, to chairman for signature | 1 |
        var positionType1 = "Draft with members";
        submitEvents.add(createValidSubmitEvent(positionType1));
        submitEvents.add(createValidSubmitEvent(positionType1));
        submitEvents.add(createValidSubmitEvent(positionType1));
        var positionType2 = "Awaiting written reasons";
        submitEvents.add(createValidSubmitEvent(positionType2));
        submitEvents.add(createValidSubmitEvent(positionType2));
        var positionType3 = "Fair copy, to chairman for signature";
        submitEvents.add(createValidSubmitEvent(positionType3));

        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertEquals(6, reportData.getReportDetails().size());
        assertEquals(3, reportData.getReportSummary().getPositionTypes().size());

        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType1));
        assertEquals(3, reportData.getReportSummary().getPositionTypes().get(positionType1).intValue());
        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType2));
        assertEquals(2, reportData.getReportSummary().getPositionTypes().get(positionType2).intValue());
        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType3));
        assertEquals(1, reportData.getReportSummary().getPositionTypes().get(positionType3).intValue());
    }

    @Test
    public void shouldContainCorrectDetailValuesForCaseWithOneHearing() {
        // Given I have a valid case
        // When I request report data
        // Then I have correct report detail values for the case
        var listedDate = "2021-07-16T10:00:00.000";
        var expectedWeeksSinceHearing = 2;
        var expectedDaysSinceHearing = 15;
        var caseReference = "2500123/2021";
        var currentPosition = MANUALLY_CREATED_POSITION;
        var dateToPosition = "2021-07-10";
        var conciliationTrack = CONCILIATION_TRACK_FAST_TRACK;
        var hearingNumber = "1";
        var hearingType = HEARING_TYPE_JUDICIAL_COSTS_HEARING;
        var judge = "Hugh Parkfield";

        var submitEvent = caseDataBuilder.withEthosCaseReference(caseReference)
                .withPositionType(validPositionType)
                .withSingleCaseType()
                .withCurrentPosition(currentPosition)
                .withDateToPosition(dateToPosition)
                .withConciliationTrack(conciliationTrack)
                .withHearing(listedDate, HEARING_STATUS_HEARD, hearingNumber, hearingType, judge)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var caseData = submitEvents.get(0).getCaseData();
        caseData.setEthosCaseReference(caseReference);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(validPositionType, reportDetail.getPositionType());

        assertEquals(expectedWeeksSinceHearing, reportDetail.getWeeksSinceHearing());
        assertEquals(expectedDaysSinceHearing, reportDetail.getDaysSinceHearing());
        assertEquals(caseReference, reportDetail.getCaseNumber());
        assertEquals(ReportDetail.NO_MULTIPLE_REFERENCE, reportDetail.getMultipleReference());
        assertEquals(listedDate, reportDetail.getLastHeardHearingDate());
        assertEquals(hearingNumber, reportDetail.getHearingNumber());
        assertEquals(hearingType, reportDetail.getHearingType());
        assertEquals(judge, reportDetail.getJudge());
        assertEquals(currentPosition, reportDetail.getCurrentPosition());
        assertEquals(dateToPosition, reportDetail.getDateToPosition());
        assertEquals(conciliationTrack, reportDetail.getConciliationTrack());
    }

    @Test
    public void shouldContainCorrectMultipleReferenceIfInMultiple() {
        // Given I have a valid case
        // And the case is a Multiple case type
        // When I request report data
        // Then I have correct Multiple Reference value in the report data
        var listedDate = "2021-07-16T10:00:00.000";
        var caseReference = "2500123/2021";
        var multipleReference = "250999/2021";

        var submitEvent = caseDataBuilder.withEthosCaseReference(caseReference)
                .withPositionType(validPositionType)
                .withMultipleCaseType(multipleReference)
                .withHearing(listedDate, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var caseData = submitEvents.get(0).getCaseData();
        caseData.setEthosCaseReference(caseReference);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(multipleReference, reportDetail.getMultipleReference());
    }

    @Test
    public void shouldContainCorrectDetailValuesForCaseWithMultipleHearings() {
        // Given I have a valid case
        // And the case has the following hearings:
        // | Listed Date | Hearing Number | Status |
        // | 2021-07-01 | 1 | Heard |
        // | 2021-07-02 | 2 | Postponed |
        // | 2021-07-05 | 3 | Heard |
        // | 2021-07-06 | 4 | Withdrawn |
        // When I request report data
        // Then I have correct hearing values for hearing #3
        var expectedWeeksSinceHearing = 3;
        var expectedDaysSinceHearing = 26;
        var caseReference = "2500123/2021";
        var judge = "Hugh Parkfield";

        var submitEvent = caseDataBuilder.withEthosCaseReference(caseReference)
                .withPositionType(validPositionType)
                .withSingleCaseType()
                .withHearing("2021-07-01T10:00:00.000", HEARING_STATUS_HEARD, "1", HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
                .withHearing("2021-07-02T10:00:00.000", HEARING_STATUS_POSTPONED, "2", HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
                .withHearing("2021-07-05T10:00:00.000", HEARING_STATUS_HEARD, "3", HEARING_TYPE_JUDICIAL_MEDIATION, judge)
                .withHearing("2021-07-06T10:00:00.000", HEARING_STATUS_WITHDRAWN, "4", HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var caseData = submitEvents.get(0).getCaseData();
        caseData.setEthosCaseReference(caseReference);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                NEWCASTLE_LISTING_CASE_TYPE_ID, USER);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(validPositionType, reportDetail.getPositionType());

        assertEquals(expectedWeeksSinceHearing, reportDetail.getWeeksSinceHearing());
        assertEquals(expectedDaysSinceHearing, reportDetail.getDaysSinceHearing());
        assertEquals(caseReference, reportDetail.getCaseNumber());
        assertEquals("2021-07-05T10:00:00.000", reportDetail.getLastHeardHearingDate());
        assertEquals("3", reportDetail.getHearingNumber());
        assertEquals(HEARING_TYPE_JUDICIAL_MEDIATION, reportDetail.getHearingType());
        assertEquals(judge, reportDetail.getJudge());
    }

    private SubmitEvent createValidSubmitEvent(String positionType) {
        caseDataBuilder = new CaseDataBuilder();
        return caseDataBuilder.withPositionType(positionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE);
    }

    private void assertCommonValues(CasesAwaitingJudgmentReportData reportData) {
        assertNotNull(reportData);
        assertNotNull(reportData.getListingData());
        assertEquals("Newcastle", reportData.getReportSummary().getOffice());
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertEquals("2021-07-31", reportData.getReportSummary().getReportRunDate().format(OLD_DATE_TIME_PATTERN2));
    }
}
