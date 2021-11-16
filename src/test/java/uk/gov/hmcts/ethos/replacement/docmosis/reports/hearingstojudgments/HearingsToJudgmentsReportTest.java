package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.hmcts.ecm.common.model.reports.hearingstojudgments.HearingsToJudgmentsSubmitEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_COSTS_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

class HearingsToJudgmentsReportTest {

    HearingsToJudgmentsReportDataSource hearingsToJudgmentsReportDataSource;
    HearingsToJudgmentsReport hearingsToJudgmentsReport;
    HearingsToJudgmentsCaseDataBuilder caseDataBuilder;
    List<HearingsToJudgmentsSubmitEvent> submitEvents = new ArrayList<>();

    static final LocalDateTime BASE_DATE = LocalDateTime.of(2021, 7, 1, 0, 0,0);
    static final String HEARING_LISTING_DATE = BASE_DATE.format(OLD_DATE_TIME_PATTERN);
    static final String DATE_WITHIN_4WKS = BASE_DATE.plusWeeks(1).format(OLD_DATE_TIME_PATTERN2);
    static final String DATE_NOT_WITHIN_4WKS = BASE_DATE.plusWeeks(5).format(OLD_DATE_TIME_PATTERN2);
    static final String JUDGMENT_HEARING_DATE = BASE_DATE.format(OLD_DATE_TIME_PATTERN2);
    static final String DATE_FROM = BASE_DATE.minusDays(1).format(OLD_DATE_TIME_PATTERN);
    static final String DATE_TO = BASE_DATE.plusDays(29).format(OLD_DATE_TIME_PATTERN);

    public HearingsToJudgmentsReportTest() {
        caseDataBuilder = new HearingsToJudgmentsCaseDataBuilder();
    }

    @BeforeEach
    public void setup() {
        submitEvents.clear();

        hearingsToJudgmentsReportDataSource = mock(HearingsToJudgmentsReportDataSource.class);
        when(hearingsToJudgmentsReportDataSource.getData(NEWCASTLE_CASE_TYPE_ID, DATE_FROM, DATE_TO)).thenReturn(submitEvents);

        hearingsToJudgmentsReport = new HearingsToJudgmentsReport(hearingsToJudgmentsReportDataSource, DATE_FROM, DATE_TO);
    }

    @Test
    void shouldNotShowSubmittedCase() {
        // Given a case is submitted
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.buildAsSubmitEvent(SUBMITTED_STATE));

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    void shouldNotShowCaseIfNoHearingsExist() {
        // Given a case is not submitted
        // And has no hearings
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    void shouldNotShowCaseIfNoHearingHasNotBeenHeard() {
        // Given a case is not submitted
        // And has no hearing that has been heard
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder
                .withHearing(HEARING_LISTING_DATE, HEARING_STATUS_LISTED, HEARING_TYPE_JUDICIAL_HEARING, YES)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    void shouldNotShowCaseIfHeardButNoJudgmentsMade() {
        // Given a case is not submitted
        // And has been heard
        // And has no judgments
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder
                .withHearing(HEARING_LISTING_DATE, HEARING_STATUS_HEARD, HEARING_TYPE_JUDICIAL_COSTS_HEARING, YES)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    void shouldShowValidCase() {
        // Given a case is not submitted
        // And has been heard
        // And has a judgment made
        // When I request report data
        // Then the case is in the report data

        submitEvents.add(caseDataBuilder
                .withHearing(HEARING_LISTING_DATE, HEARING_STATUS_HEARD, HEARING_TYPE_JUDICIAL_HEARING, YES)
                .withJudgment(JUDGMENT_HEARING_DATE, DATE_NOT_WITHIN_4WKS, DATE_NOT_WITHIN_4WKS)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());
    }

    @Test
    void shouldShowCorrectScotlandOfficeValidCase() {
        // Given a case with a scottish office is not submitted
        // And has been heard
        // And has a judgment made
        // When I request report data
        // Then the case is in the report data
        when(hearingsToJudgmentsReportDataSource.getData(SCOTLAND_CASE_TYPE_ID, DATE_FROM, DATE_TO)).thenReturn(submitEvents);
        var managingOffice = "Test Office";

        submitEvents.add(caseDataBuilder
                .withManagingOffice(managingOffice)
                .withHearing(HEARING_LISTING_DATE, HEARING_STATUS_HEARD, HEARING_TYPE_JUDICIAL_HEARING, YES)
                .withJudgment(JUDGMENT_HEARING_DATE, DATE_NOT_WITHIN_4WKS, DATE_NOT_WITHIN_4WKS)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = hearingsToJudgmentsReport.runReport(SCOTLAND_LISTING_CASE_TYPE_ID);
        assertNotNull(reportData);
        assertEquals(SCOTLAND_CASE_TYPE_ID, reportData.getHearingsToJudgmentsReportSummary().getOffice());
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(managingOffice, reportDetail.getReportOffice());
    }

    @Test
    void shouldShowTotalHearingsInSummary() {
        // Given I have 2 valid cases with hearings with judgements within 4 weeks
        // And 1 valid case with hearings with judgement not within 4 weeks
        // When I request report data
        // Then the report summary shows 3 total hearings
        // And 2 total hearings with judgements within 4 weeks
        // And 1 total hearings with judgement not within 4 weeks

        submitEvents.add(createValidSubmitEventNotWithin4Wks());
        submitEvents.add(createValidSubmitEventWithin4Wks());
        submitEvents.add(createValidSubmitEventWithin4Wks());
        submitEvents.add(createValidSubmitEventWithin4Wks());

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("4", reportData.getHearingsToJudgmentsReportSummary().getTotalCases());
        assertEquals(1, reportData.getReportDetails().size());
        assertEquals("3", reportData.getHearingsToJudgmentsReportSummary().getTotal4Wk());
        assertEquals("75.00", reportData.getHearingsToJudgmentsReportSummary().getTotal4WkPercent());
        assertEquals("1", reportData.getHearingsToJudgmentsReportSummary().getTotalX4Wk());
        assertEquals("25.00", reportData.getHearingsToJudgmentsReportSummary().getTotalX4WkPercent());
    }

    @ParameterizedTest
    @CsvSource({
            "2021-07-16T10:00:00.000,2021-07-16,2021-08-26,2021-08-26,41,2500121/2021,1,One Test,"
                    + HEARING_TYPE_JUDICIAL_HEARING + "," + YES + "," + ACCEPTED_STATE,
            "2021-07-17T10:00:00.000,2021-07-17,2021-08-26,2021-08-26,40,2500122/2021,2,Two Test,"
                    + HEARING_TYPE_PERLIMINARY_HEARING + "," + NO + "," + ACCEPTED_STATE,
            "2021-07-18T10:00:00.000,2021-07-18,2021-08-26,2021-08-26,39,2500123/2021,3,Three Test,"
                    + HEARING_TYPE_PERLIMINARY_HEARING_CM + ",," + CLOSED_STATE,
            "2021-07-19T10:00:00.000,2021-07-19,2021-08-26,2021-08-26,38,2500124/2021,4,Four Test,"
                    + HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC + "," + YES + "," + CLOSED_STATE})
    void shouldContainCorrectDetailValuesForHearingsWithValidJudgment(String hearingListedDate, String judgmentHearingDate,
                                                                      String dateJudgmentMade, String dateJudgmentSent,
                                                                      String expectedTotalDays, String caseReference,
                                                                      String hearingNumber, String hearingJudge,
                                                                      String hearingType, String hearingReserved,
                                                                      String caseState) {
        // Given I have a valid case
        // And the case has a valid hearing and judgment
        // When I request report data
        // Then I have correct report detail values for the case

        submitEvents.add(caseDataBuilder
                .withEthosCaseReference(caseReference)
                .withHearing(hearingListedDate, HEARING_STATUS_HEARD, hearingType, YES,
                        hearingNumber, hearingJudge, hearingReserved)
                .withJudgment(judgmentHearingDate, dateJudgmentMade, dateJudgmentSent)
                .buildAsSubmitEvent(caseState));

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(hearingJudge, reportDetail.getHearingJudge());
        assertEquals(NEWCASTLE_CASE_TYPE_ID, reportDetail.getReportOffice());
        assertEquals(caseReference, reportDetail.getCaseReference());
        assertEquals(hearingReserved, reportDetail.getReservedHearing());
        assertEquals(expectedTotalDays, reportDetail.getTotalDays());
        assertEquals(judgmentHearingDate, reportDetail.getHearingDate());
        assertEquals(dateJudgmentSent, reportDetail.getJudgementDateSent());
    }

    @Test
    void shouldContainCorrectDetailValuesForMultipleHearingsWithJudgments() {
        // Given I have a valid case
        // And the case has the following hearings:
        // | Listed Date | Hearing Number | Date Judgment Made |
        // | 2021-07-06 | 1 | 2021-08-03
        // | 2021-07-05 | 2 | 2021-08-03
        // When I request report data
        // Then I have correct hearing values for hearing #2
        var expectedTotalDays = "30";
        var caseReference = "2500123/2021";
        var judge = "Hugh Parkfield";
        var judgmentHearingDate = "2021-07-05";
        var dateJudgmentSent = "2021-08-04";

        submitEvents.add(caseDataBuilder
                .withEthosCaseReference(caseReference)
                .withManagingOffice(NEWCASTLE_CASE_TYPE_ID)
                .withHearing("2021-07-06T10:00:00.000", HEARING_STATUS_HEARD, HEARING_TYPE_JUDICIAL_HEARING,
                        YES, "1", "A.N. Other", YES)
                .withJudgment("2021-07-06", "2021-08-03", "2021-08-04")
                .withHearing("2021-07-05T10:00:00.000", HEARING_STATUS_HEARD, HEARING_TYPE_JUDICIAL_HEARING,
                        YES, "2", judge, NO)
                .withJudgment(judgmentHearingDate, "2021-08-03", dateJudgmentSent)
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var reportData = hearingsToJudgmentsReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals(1, reportData.getReportDetails().size());

        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(judge, reportDetail.getHearingJudge());
        assertEquals(NEWCASTLE_CASE_TYPE_ID, reportDetail.getReportOffice());
        assertEquals(caseReference, reportDetail.getCaseReference());
        assertEquals(NO, reportDetail.getReservedHearing());
        assertEquals(expectedTotalDays, reportDetail.getTotalDays());
        assertEquals(judgmentHearingDate, reportDetail.getHearingDate());
        assertEquals(dateJudgmentSent, reportDetail.getJudgementDateSent());
    }

    private HearingsToJudgmentsSubmitEvent createValidSubmitEventWithin4Wks() {
        caseDataBuilder = new HearingsToJudgmentsCaseDataBuilder();
        return caseDataBuilder.withHearing(HEARING_LISTING_DATE, HEARING_STATUS_HEARD, HEARING_TYPE_JUDICIAL_HEARING, YES)
                .withJudgment(JUDGMENT_HEARING_DATE, DATE_WITHIN_4WKS, DATE_WITHIN_4WKS)
                .buildAsSubmitEvent(ACCEPTED_STATE);
    }

    private HearingsToJudgmentsSubmitEvent createValidSubmitEventNotWithin4Wks() {
        caseDataBuilder = new HearingsToJudgmentsCaseDataBuilder();
        return caseDataBuilder.withHearing(HEARING_LISTING_DATE, HEARING_STATUS_HEARD, HEARING_TYPE_JUDICIAL_HEARING, YES)
                .withJudgment(JUDGMENT_HEARING_DATE, DATE_NOT_WITHIN_4WKS, DATE_NOT_WITHIN_4WKS)
                .buildAsSubmitEvent(ACCEPTED_STATE);
    }

    private void assertCommonValues(HearingsToJudgmentsReportData reportData) {
        assertNotNull(reportData);
        assertEquals("Newcastle", reportData.getHearingsToJudgmentsReportSummary().getOffice());
    }
}
