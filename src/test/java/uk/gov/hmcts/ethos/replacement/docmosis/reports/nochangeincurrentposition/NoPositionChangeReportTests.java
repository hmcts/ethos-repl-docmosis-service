package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TRANSFERRED_STATE;

class NoPositionChangeReportTests {

    NoPositionChangeDataSource noPositionChangeDataSource;
    NoPositionChangeReport noPositionChangeReport;
    NoPositionChangeCaseDataBuilder caseDataBuilder;
    List<NoPositionChangeSubmitEvent> submitEvents = new ArrayList<>();
    List<SubmitMultipleEvent> submitMultipleEvents = new ArrayList<>();

    static final LocalDateTime BASE_DATE = LocalDateTime.of(2021, 7, 1, 0, 0, 0);
    static final String REPORT_CREATE_DATE = BASE_DATE.plusMonths(3).format(OLD_DATE_TIME_PATTERN2);
    static final String DATE_WITHIN_3MONTHS = BASE_DATE.plusDays(2).format(OLD_DATE_TIME_PATTERN2);
    static final String DATE_BEFORE_3MONTHS = BASE_DATE.minusDays(2).format(OLD_DATE_TIME_PATTERN2);

    public NoPositionChangeReportTests() {
        caseDataBuilder = new NoPositionChangeCaseDataBuilder();
    }

    @BeforeEach
    void setup() {
        submitEvents.clear();

        noPositionChangeDataSource = mock(NoPositionChangeDataSource.class);
        when(noPositionChangeDataSource.getData(NEWCASTLE_CASE_TYPE_ID, REPORT_CREATE_DATE)).thenReturn(submitEvents);
        when(noPositionChangeDataSource.getMultiplesData(eq(NEWCASTLE_BULK_CASE_TYPE_ID), anyList()))
            .thenReturn(submitMultipleEvents);

        noPositionChangeReport = new NoPositionChangeReport(noPositionChangeDataSource, REPORT_CREATE_DATE);
    }

    @Test
    void shouldNotShowSingleCase_PositionsWithChangeDatesLessThan3MonthsAgo() {
        // Given a single case has a position change date within 3 month of the report date
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(createValidSingleSubmitEventWithin3Months(ACCEPTED_STATE));

        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetailsSingle().isEmpty());
        assertTrue(reportData.getReportDetailsMultiple().isEmpty());
    }

    @Test
    void shouldNotShowMultipleCase_PositionsWithChangeDatesLessThan3MonthsAgo() {
        // Given a multiple case has a position change date within 3 month of the report date
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(createValidMultipleSubmitEventWithin3Months(CLOSED_STATE));

        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertTrue(reportData.getReportDetailsSingle().isEmpty());
        assertTrue(reportData.getReportDetailsMultiple().isEmpty());
    }

    @Test
    void shouldShowSingleCase_PositionsWithChangeDatesMoreThan3MonthsAgo() {
        // Given a single case has a position change date older than the report date by 3 month
        // When I request report data
        // Then the case should be in the report data
        submitEvents.add(caseDataBuilder.withCaseType(SINGLE_CASE_TYPE)
                .withDateToPosition(DATE_BEFORE_3MONTHS)
                .withCurrentPosition("test2")
                .withFirstRespondent("resp2")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(SUBMITTED_STATE));

        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("1", reportData.getReportSummary().getTotalCases());
        assertEquals("1", reportData.getReportSummary().getTotalSingleCases());
        assertEquals("0", reportData.getReportSummary().getTotalMultipleCases());
        assertEquals(1, reportData.getReportDetailsSingle().size());
        assertTrue(reportData.getReportDetailsMultiple().isEmpty());
        var reportDetail = reportData.getReportDetailsSingle().getFirst();
        assertEquals("2500123/2021", reportDetail.getCaseReference());
        assertEquals(DATE_BEFORE_3MONTHS, reportDetail.getDateToPosition());
        assertEquals("test2", reportDetail.getCurrentPosition());
        assertEquals("resp2", reportDetail.getRespondent());
        assertEquals("2021", reportDetail.getYear());
    }

    @Test
    void shouldShowSingleCase_PositionsWithChangeDatesExactly3MonthsAgo() {
        // Given a single case has a position change date exactly older than the report date by 3 month
        // When I request report data
        // Then the case should be in the report data
        submitEvents.add(caseDataBuilder.withCaseType(SINGLE_CASE_TYPE)
                .withDateToPosition(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withCurrentPosition("test2")
                .withFirstRespondent("resp2")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(SUBMITTED_STATE));

        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("1", reportData.getReportSummary().getTotalCases());
        assertEquals("1", reportData.getReportSummary().getTotalSingleCases());
        assertEquals("0", reportData.getReportSummary().getTotalMultipleCases());
        assertEquals(1, reportData.getReportDetailsSingle().size());
        assertTrue(reportData.getReportDetailsMultiple().isEmpty());
        var reportDetail = reportData.getReportDetailsSingle().getFirst();
        assertEquals("2500123/2021", reportDetail.getCaseReference());
        assertEquals(BASE_DATE.format(OLD_DATE_TIME_PATTERN2), reportDetail.getDateToPosition());
        assertEquals("test2", reportDetail.getCurrentPosition());
        assertEquals("resp2", reportDetail.getRespondent());
        assertEquals("2021", reportDetail.getYear());
    }

    @Test
    void shouldShowMultipleCase_PositionsWithChangeDatesMoreThan3MonthsAgo() {
        // Given a multiple case has a position change date older than the report date by 3 month
        // When I request report data
        // Then the case should be in the report data

        submitEvents.add(caseDataBuilder.withCaseType(MULTIPLE_CASE_TYPE)
                .withDateToPosition(DATE_BEFORE_3MONTHS)
                .withCurrentPosition("test4")
                .withFirstRespondent("resp5")
                .withRespondent("resp6")
                .withMultipleReference("Multi2")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(ACCEPTED_STATE));

        var multipleData = new MultipleData();
        multipleData.setMultipleReference("Multi2");
        multipleData.setMultipleName("Multiple Name");
        var submitMultipleData = new SubmitMultipleEvent();
        submitMultipleData.setCaseData(multipleData);
        submitMultipleEvents.add(submitMultipleData);

        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("1", reportData.getReportSummary().getTotalCases());
        assertEquals("0", reportData.getReportSummary().getTotalSingleCases());
        assertEquals("1", reportData.getReportSummary().getTotalMultipleCases());
        assertTrue(reportData.getReportDetailsSingle().isEmpty());
        assertEquals(1, reportData.getReportDetailsMultiple().size());
        var reportDetail = reportData.getReportDetailsMultiple().getFirst();
        assertEquals("2500123/2021", reportDetail.getCaseReference());
        assertEquals(DATE_BEFORE_3MONTHS, reportDetail.getDateToPosition());
        assertEquals("test4", reportDetail.getCurrentPosition());
        assertEquals("Multiple Name", reportDetail.getMultipleName());
        assertEquals("2021", reportDetail.getYear());
    }

    @Test
    void shouldShowCorrectMultipleRespondentText_SinglePositionsWithChangeDatesMoreThan3MonthsAgo() {
        // Given a single case has a position change date older than the report date by 3 month
        // And multiple respondents
        // When I request report data
        // Then the case should be in the report data

        submitEvents.add(caseDataBuilder.withCaseType(SINGLE_CASE_TYPE)
                .withDateToPosition(DATE_BEFORE_3MONTHS)
                .withCurrentPosition("test7")
                .withFirstRespondent("resp8")
                .withRespondent("resp9")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(SUBMITTED_STATE));

        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("1", reportData.getReportSummary().getTotalCases());
        assertEquals("1", reportData.getReportSummary().getTotalSingleCases());
        assertEquals("0", reportData.getReportSummary().getTotalMultipleCases());
        assertEquals(1, reportData.getReportDetailsSingle().size());
        assertTrue(reportData.getReportDetailsMultiple().isEmpty());
        var reportDetail = reportData.getReportDetailsSingle().getFirst();
        assertEquals("2500123/2021", reportDetail.getCaseReference());
        assertEquals(DATE_BEFORE_3MONTHS, reportDetail.getDateToPosition());
        assertEquals("test7", reportDetail.getCurrentPosition());
        assertEquals("resp8 & Others", reportDetail.getRespondent());
        assertEquals("2021", reportDetail.getYear());
    }

    @Test
    void shouldShowAllCases_PositionsWithChangeDatesMoreThan3MonthsAgo() {
        // Given a mix of cases has a position change date older or newer than the report date by 3 month
        // When I request report data
        // Then the right cases should be in the report data

        submitEvents.add(createValidMultipleSubmitEventBefore3Months(ACCEPTED_STATE));
        submitEvents.add(createValidMultipleSubmitEventWithin3Months(CLOSED_STATE));
        submitEvents.add(createValidSingleSubmitEventBefore3Months(SUBMITTED_STATE));
        submitEvents.add(createValidSingleSubmitEventWithin3Months(ACCEPTED_STATE));

        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("2", reportData.getReportSummary().getTotalCases());
        assertEquals("1", reportData.getReportSummary().getTotalSingleCases());
        assertEquals("1", reportData.getReportSummary().getTotalMultipleCases());
        assertEquals(1, reportData.getReportDetailsSingle().size());
        assertEquals(1, reportData.getReportDetailsMultiple().size());
    }

    @Test
    void shouldNotIncludeClosedOrTransferredStates() {
        submitEvents.add(createValidSingleSubmitEventBefore3Months(TRANSFERRED_STATE));
        submitEvents.add(createValidMultipleSubmitEventBefore3Months(CLOSED_STATE));
        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("0", reportData.getReportSummary().getTotalCases());
    }

    @Test
    void checkAllStates() {
        submitEvents.add(createValidSingleSubmitEventBefore3Months(ACCEPTED_STATE));
        submitEvents.add(createValidSingleSubmitEventBefore3Months(CLOSED_STATE));
        submitEvents.add(createValidSingleSubmitEventBefore3Months(REJECTED_STATE));
        submitEvents.add(createValidSingleSubmitEventBefore3Months(SUBMITTED_STATE));
        submitEvents.add(createValidSingleSubmitEventBefore3Months(TRANSFERRED_STATE));
        var reportData = noPositionChangeReport.runReport(NEWCASTLE_LISTING_CASE_TYPE_ID);
        assertCommonValues(reportData);
        assertEquals("3", reportData.getReportSummary().getTotalCases());
        assertEquals("3", reportData.getReportSummary().getTotalSingleCases());
        assertEquals(3, reportData.getReportDetailsSingle().size());
    }

    private NoPositionChangeSubmitEvent createValidSingleSubmitEventWithin3Months(String state) {
        caseDataBuilder = new NoPositionChangeCaseDataBuilder();
        return caseDataBuilder.withCaseType(SINGLE_CASE_TYPE)
                .withDateToPosition(DATE_WITHIN_3MONTHS)
                .withCurrentPosition("test1")
                .withFirstRespondent("resp1")
                .withRespondent("resp3")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(state);
    }

    private NoPositionChangeSubmitEvent createValidSingleSubmitEventBefore3Months(String state) {
        caseDataBuilder = new NoPositionChangeCaseDataBuilder();
        return caseDataBuilder.withCaseType(SINGLE_CASE_TYPE)
                .withDateToPosition(DATE_BEFORE_3MONTHS)
                .withCurrentPosition("test2")
                .withFirstRespondent("resp2")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(state);
    }

    private NoPositionChangeSubmitEvent createValidMultipleSubmitEventWithin3Months(String state) {
        caseDataBuilder = new NoPositionChangeCaseDataBuilder();
        return caseDataBuilder.withCaseType(MULTIPLE_CASE_TYPE)
                .withDateToPosition(DATE_WITHIN_3MONTHS)
                .withCurrentPosition("test3")
                .withFirstRespondent("resp4")
                .withMultipleReference("Multi1")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(state);
    }

    private NoPositionChangeSubmitEvent createValidMultipleSubmitEventBefore3Months(String state) {
        caseDataBuilder = new NoPositionChangeCaseDataBuilder();
        return caseDataBuilder.withCaseType(MULTIPLE_CASE_TYPE)
                .withDateToPosition(DATE_BEFORE_3MONTHS)
                .withCurrentPosition("test4")
                .withFirstRespondent("resp5")
                .withRespondent("resp6")
                .withMultipleReference("Multi2")
                .withReceiptDate(BASE_DATE.format(OLD_DATE_TIME_PATTERN2))
                .withEthosCaseReference("2500123/2021")
                .buildAsSubmitEvent(state);
    }

    private void assertCommonValues(NoPositionChangeReportData reportData) {
        assertNotNull(reportData);
        assertEquals("Newcastle", reportData.getReportSummary().getOffice());
        assertEquals(REPORT_CREATE_DATE, reportData.getReportDate());
    }
}
