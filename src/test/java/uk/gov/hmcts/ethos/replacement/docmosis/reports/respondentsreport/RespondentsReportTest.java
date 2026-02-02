package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;

public class RespondentsReportTest {

    RespondentsReportDataSource reportDataSource;
    RespondentsReport respondentsReport;
    RespondentsReportCaseDataBuilder caseDataBuilder = new RespondentsReportCaseDataBuilder();
    List<RespondentsReportSubmitEvent> submitEvents = new ArrayList<>();
    static final LocalDateTime BASE_DATE = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
    static final String DATE_FROM = BASE_DATE.minusDays(1).format(OLD_DATE_TIME_PATTERN);
    static final String DATE_TO = BASE_DATE.plusDays(15).format(OLD_DATE_TIME_PATTERN);

    @Before
    public void setup() {
        submitEvents.clear();
        caseDataBuilder = new RespondentsReportCaseDataBuilder();
        reportDataSource = mock(RespondentsReportDataSource.class);
        when(reportDataSource.getData(MANCHESTER_CASE_TYPE_ID, DATE_FROM, DATE_TO)).thenReturn(submitEvents);
        respondentsReport = new RespondentsReport(reportDataSource);
    }

    @Test
    public void shouldNotShowCaseWithNoRespondent() {
        // Given a case has no respondent
        // and report data is requested
        // the case should not be in the report data

        caseDataBuilder.withNoRespondents();
        submitEvents.add(caseDataBuilder
                .buildAsSubmitEvent());

        var reportData = respondentsReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        assertCommonValues(reportData);
        assertEquals("0", reportData.getReportSummary().getTotalCasesWithMoreThanOneRespondent());
        assertEquals(0, reportData.getReportDetails().size());
    }

    @Test
    public void shouldNotShowCaseWithOneRespondent() {
        // Given a case has 1 respondent
        // and report data is requested
        // the case should not be in the report data

        caseDataBuilder.withOneRespondent();
        submitEvents.add(caseDataBuilder
                .buildAsSubmitEvent());

        var reportData = respondentsReport.generateReport(
            new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        assertCommonValues(reportData);
        assertEquals("0", reportData.getReportSummary().getTotalCasesWithMoreThanOneRespondent());
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldShowCaseWithMoreThanOneRespondent() {
        // Given a case has more than 1 respondents
        // and report data is requested
        // the cases should be in the report data

        caseDataBuilder.withMoreThanOneRespondents();
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());

        var reportData = respondentsReport.generateReport(
            new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        assertCommonValues(reportData);
        assertEquals("1", reportData.getReportSummary().getTotalCasesWithMoreThanOneRespondent());
        assertFalse(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldShowCaseDetailsWithMoreThanOneRespondentRepresented() {
        // Given a case has more than 1 respondents and represented
        // and report data is requested
        // the cases should be in the report data details

        caseDataBuilder.withMoreThan1RespondentsRepresented();
        submitEvents.add(caseDataBuilder
                .buildAsSubmitEvent());
        var reportData = respondentsReport.generateReport(
            new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        assertCommonValues(reportData);
        assertEquals("111", reportData.getReportDetails().getFirst().getCaseNumber());
        assertEquals("Resp1", reportData.getReportDetails().getFirst().getRespondentName());
        assertEquals("Rep1", reportData.getReportDetails().getFirst().getRepresentativeName());
        assertEquals("Y", reportData.getReportDetails()
                .getFirst().getRepresentativeHasMoreThanOneRespondent());
    }

    private void assertCommonValues(RespondentsReportData reportData) {
        assertNotNull(reportData);
        assertEquals("Manchester", reportData.getReportSummary().getOffice());
    }
}
