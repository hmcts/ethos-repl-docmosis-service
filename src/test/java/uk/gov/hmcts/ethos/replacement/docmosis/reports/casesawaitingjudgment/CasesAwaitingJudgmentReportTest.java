package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.reports.casesawaitingjudgment.CasesAwaitingJudgmentSubmitEvent;

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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;

public class CasesAwaitingJudgmentReportTest {

    ReportDataSource reportDataSource;
    CasesAwaitingJudgmentReport casesAwaitingJudgmentReport;
    CaseDataBuilder caseDataBuilder;
    List<CasesAwaitingJudgmentSubmitEvent> submitEvents = new ArrayList<>();

    final String validPositionType;
    static final String LISTING_DATE = "1970-01-01T00:00:00.000";

    public CasesAwaitingJudgmentReportTest() {
        caseDataBuilder = new CaseDataBuilder();
        validPositionType = CasesAwaitingJudgmentReport.VALID_POSITION_TYPES.stream().findAny().orElseThrow();
    }

    @Before
    public void setup() {
        submitEvents.clear();

        reportDataSource = mock(ReportDataSource.class);
        when(reportDataSource.getData(NEWCASTLE_CASE_TYPE_ID)).thenReturn(submitEvents);

        var now = "2021-07-31T10:00:00.Z";
        var clock = Clock.fixed(Instant.parse(now), ZoneId.of("UTC"));

        casesAwaitingJudgmentReport = new CasesAwaitingJudgmentReport(reportDataSource, clock);
    }

    @Test
    public void shouldNotShowClosedCase() {
        // Given a case is closed
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.buildAsSubmitEvent(CLOSED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseWithInvalidPositionType() {
        // Given a case is not closed
        // And a case has an invalid position type
        // Examples
        // | An invalid position type |
        // | null |
        // When I request report data
        // Then the case should not be in the report data

        var invalidPositionTypes = new String[] { "An invalid position type", null};
        for (var invalidPositionType : invalidPositionTypes) {
            submitEvents.clear();
            caseDataBuilder = new CaseDataBuilder();
            submitEvents.add(caseDataBuilder
                    .withPositionType(invalidPositionType)
                    .buildAsSubmitEvent(ACCEPTED_STATE));

            var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
            assertCommonValues(reportData);
            assertTrue(reportData.getReportDetails().isEmpty());
        }
    }

    @Test
    public void shouldNotShowCaseIfNoHearingsExist() {
        // Given a case is not closed
        // And has a valid position type
        // And has no hearings
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
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

        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_LISTED)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
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

        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .withJudgment()
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
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

        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
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

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(3, reportData.getReportDetails().size());
        assertEquals(1, reportData.getReportSummary().getPositionTypes().size());
        assertEquals(positionType, reportData.getReportSummary().getPositionTypes().getFirst().getPositionTypeName());
        assertEquals(3, reportData.getReportSummary().getPositionTypes().getFirst().getPositionTypeCount());
    }

    @Test
    public void shouldShowMultiplePositionValuesInSummaryInOrder() {
        // Given I have 3 valid cases with position type Draft with Members
        // And I have 2 valid cases with position type Awaiting written reasons
        // And I have 1 valid case with position type Fair copy, to chairman for signature
        // When I request report data
        // Then the report summary shows 3 rows in total count order:
        //    | Fair copy, to chairman for signature | 1 |
        //    | Awaiting written reasons             | 2 |
        //    | Draft with members                   | 3 |

        var positionType1 = "Draft with members";
        var positionType2 = "Awaiting written reasons";
        var positionType3 = "Fair copy, to chairman for signature";
        submitEvents.add(createValidSubmitEvent(positionType2));
        submitEvents.add(createValidSubmitEvent(positionType1));
        submitEvents.add(createValidSubmitEvent(positionType1));
        submitEvents.add(createValidSubmitEvent(positionType1));
        submitEvents.add(createValidSubmitEvent(positionType3));
        submitEvents.add(createValidSubmitEvent(positionType2));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(6, reportData.getReportDetails().size());
        assertEquals(3, reportData.getReportSummary().getPositionTypes().size());

        assertEquals(positionType3, reportData.getReportSummary().getPositionTypes().getFirst().getPositionTypeName());
        assertEquals(1, reportData.getReportSummary().getPositionTypes().getFirst().getPositionTypeCount());
        assertEquals(positionType2, reportData.getReportSummary().getPositionTypes().get(1).getPositionTypeName());
        assertEquals(2, reportData.getReportSummary().getPositionTypes().get(1).getPositionTypeCount());
        assertEquals(positionType1, reportData.getReportSummary().getPositionTypes().get(2).getPositionTypeName());
        assertEquals(3, reportData.getReportSummary().getPositionTypes().get(2).getPositionTypeCount());
    }

    @Test
    public void shouldContainCorrectDetailValuesForCaseWithOneHearing() {
        // Given I have a valid case
        // When I request report data
        // Then I have correct report detail values for the case
        var listedDate = "2021-07-16T10:00:00.000";
        var caseReference = "2500123/2021";
        var currentPosition = MANUALLY_CREATED_POSITION;
        var dateToPosition = "2021-07-10";
        var conciliationTrack = CONCILIATION_TRACK_FAST_TRACK;
        var hearingNumber = "1";
        var hearingType = HEARING_TYPE_JUDICIAL_COSTS_HEARING;
        var judge = "Hugh Parkfield";

        submitEvents.add(caseDataBuilder
                .withEthosCaseReference(caseReference)
                .withPositionType(validPositionType)
                .withSingleCaseType()
                .withCurrentPosition(currentPosition)
                .withDateToPosition(dateToPosition)
                .withConciliationTrack(conciliationTrack)
                .withHearing(listedDate, HEARING_STATUS_HEARD, hearingNumber, hearingType, judge)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().getFirst();
        assertEquals(validPositionType, reportDetail.getPositionType());

        assertEquals(2, reportDetail.getWeeksSinceHearing());
        assertEquals(15, reportDetail.getDaysSinceHearing());
        assertEquals(caseReference, reportDetail.getCaseNumber());
        assertEquals(ReportDetail.NO_MULTIPLE_REFERENCE, reportDetail.getMultipleReference());
        assertEquals("16/07/2021", reportDetail.getLastHeardHearingDate());
        assertEquals(hearingNumber, reportDetail.getHearingNumber());
        assertEquals(hearingType, reportDetail.getHearingType());
        assertEquals(judge, reportDetail.getJudge());
        assertEquals(currentPosition, reportDetail.getCurrentPosition());
        assertEquals("10/07/2021", reportDetail.getDateToPosition());
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

        submitEvents.add(caseDataBuilder
                .withEthosCaseReference(caseReference)
                .withPositionType(validPositionType)
                .withMultipleCaseType(multipleReference)
                .withHearing(listedDate, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().getFirst();
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
        var caseReference = "2500123/2021";
        var judge = "Hugh Parkfield";

        submitEvents.add(caseDataBuilder
            .withEthosCaseReference(caseReference)
            .withPositionType(validPositionType)
            .withSingleCaseType()
            .withHearing("2021-07-01T10:00:00.000", HEARING_STATUS_HEARD, "1",
                HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
            .withHearing("2021-07-02T10:00:00.000", HEARING_STATUS_POSTPONED, "2",
                HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
            .withHearing("2021-07-05T10:00:00.000", HEARING_STATUS_HEARD, "3",
                HEARING_TYPE_JUDICIAL_MEDIATION, judge)
            .withHearing("2021-07-06T10:00:00.000", HEARING_STATUS_WITHDRAWN, "4",
                HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
            .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().getFirst();
        assertEquals(validPositionType, reportDetail.getPositionType());

        assertEquals(3, reportDetail.getWeeksSinceHearing());
        assertEquals(26, reportDetail.getDaysSinceHearing());
        assertEquals(caseReference, reportDetail.getCaseNumber());
        assertEquals("05/07/2021", reportDetail.getLastHeardHearingDate());
        assertEquals("3", reportDetail.getHearingNumber());
        assertEquals(HEARING_TYPE_JUDICIAL_MEDIATION, reportDetail.getHearingType());
        assertEquals(judge, reportDetail.getJudge());
    }

    @Test
    public void shouldOrderReportDetailsInDaysSinceHeardDescOrder() {
        // Given I have valid cases
        // And the cases have the following listed dates
        // | Case Number | Listed Date |
        // | Case 1 | 2021-07-10 |
        // | Case 2 | 2021-07-02 |
        // | Case 3 | 2021-07-05 |
        // | Case 4 | 2021-07-01 |
        // When I request report data
        // Then I have report details in the following order:
        // | Case Number |
        // | Case 4 |
        // | Case 2 |
        // | Case 3 |
        // | Case 1 |

        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .withEthosCaseReference("Case 1")
                .withSingleCaseType()
                .withHearing("2021-07-10T10:00:00.000", HEARING_STATUS_HEARD, "1",
                    HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
                .buildAsSubmitEvent(ACCEPTED_STATE));
        caseDataBuilder = new CaseDataBuilder();
        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .withEthosCaseReference("Case 2")
                .withSingleCaseType()
                .withHearing("2021-07-02T10:00:00.000", HEARING_STATUS_HEARD, "1",
                    HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
                .buildAsSubmitEvent(ACCEPTED_STATE));
        caseDataBuilder = new CaseDataBuilder();
        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .withEthosCaseReference("Case 3")
                .withSingleCaseType()
                .withHearing("2021-07-05T10:00:00.000", HEARING_STATUS_HEARD, "1",
                    HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
                .buildAsSubmitEvent(ACCEPTED_STATE));
        caseDataBuilder = new CaseDataBuilder();
        submitEvents.add(caseDataBuilder
                .withPositionType(validPositionType)
                .withEthosCaseReference("Case 4")
                .withSingleCaseType()
                .withHearing("2021-07-01T10:00:00.000", HEARING_STATUS_HEARD, "1",
                    HEARING_TYPE_JUDICIAL_COSTS_HEARING, "A.N. Other")
                .buildAsSubmitEvent(ACCEPTED_STATE));
        caseDataBuilder = new CaseDataBuilder();

        var reportData = casesAwaitingJudgmentReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(4, reportData.getReportDetails().size());

        assertEquals("Case 4", reportData.getReportDetails().getFirst().getCaseNumber());
        assertEquals("Case 2", reportData.getReportDetails().get(1).getCaseNumber());
        assertEquals("Case 3", reportData.getReportDetails().get(2).getCaseNumber());
        assertEquals("Case 1", reportData.getReportDetails().get(3).getCaseNumber());
    }

    private CasesAwaitingJudgmentSubmitEvent createValidSubmitEvent(String positionType) {
        caseDataBuilder = new CaseDataBuilder();
        return caseDataBuilder.withPositionType(positionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE);
    }

    private void assertCommonValues(CasesAwaitingJudgmentReportData reportData) {
        assertNotNull(reportData);
        assertEquals("Newcastle", reportData.getReportSummary().getOffice());
    }
}
