package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.JudgeEmploymentStatus;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice.JpaJudgeService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_SETTLED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_WITHDRAWN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.JudgeEmploymentStatus.FEE_PAID;
import static uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.JudgeEmploymentStatus.SALARIED;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReport.FULL_DAY;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDaysReportTest {

    SessionDaysReportDataSource reportDataSource;
    JpaJudgeService jpaJudgeService;
    SessionDaysReport sessionDaysReport;
    SessionDaysCaseDataBuilder caseDataBuilder = new SessionDaysCaseDataBuilder();
    List<SessionDaysSubmitEvent> submitEvents = new ArrayList<>();
    static final LocalDateTime BASE_DATE = LocalDateTime.of(2022, 1, 1, 0, 0,0);
    static final String DATE_FROM = BASE_DATE.minusDays(1).format(OLD_DATE_TIME_PATTERN);
    static final String DATE_TO = BASE_DATE.plusDays(15).format(OLD_DATE_TIME_PATTERN);

    @BeforeEach
    @Before
    public void setup() {
        submitEvents.clear();
        caseDataBuilder = new SessionDaysCaseDataBuilder();
        reportDataSource = mock(SessionDaysReportDataSource.class);
        jpaJudgeService = mock(JpaJudgeService.class);
        when(reportDataSource.getData(MANCHESTER_CASE_TYPE_ID, DATE_FROM, DATE_TO)).thenReturn(submitEvents);
        when(jpaJudgeService.getJudge("Manchester", "ftcJudge")).thenReturn(getJudge("ftcJudge", SALARIED));
        when(jpaJudgeService.getJudge("Manchester", "ptcJudge")).thenReturn(getJudge("ptcJudge", FEE_PAID));
        sessionDaysReport = new SessionDaysReport(reportDataSource,jpaJudgeService);
    }

    private Judge getJudge(String name, JudgeEmploymentStatus status) {
        Judge judge = new Judge();
        judge.setEmploymentStatus(status);
        judge.setName(name);
        return judge;
    }

    @Test
    public void shouldNotShowCaseWithNoHearings() {
        // Given a case has no hearing
        // and report data is requested
        // the case should not be in the report data

            caseDataBuilder.withNoHearings();
            submitEvents.add(caseDataBuilder
                    .buildAsSubmitEvent());

            var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID,DATE_FROM, DATE_TO);
            assertCommonValues(reportData);
            assertEquals("0", reportData.getReportSummary().getFtSessionDaysTotal());
            assertEquals("0", reportData.getReportSummary().getPtSessionDaysTotal());
            assertEquals("0", reportData.getReportSummary().getOtherSessionDaysTotal());
            assertEquals("0", reportData.getReportSummary().getSessionDaysTotal());
            assertEquals("0", reportData.getReportSummary().getPtSessionDaysPerCent());
            assertEquals(0, reportData.getReportSummary2List().size());
            assertEquals(0, reportData.getReportDetails().size());
    }

    @ParameterizedTest
    @CsvSource({HEARING_STATUS_LISTED, HEARING_STATUS_SETTLED, HEARING_STATUS_WITHDRAWN, HEARING_STATUS_POSTPONED})
    public void shouldNotShowCaseWithInValidHearingStatus(String hearingStatus) {
        // Given a case has invalid hearing status
        // and report data is requested
        // the case should not be in the report data
        caseDataBuilder.withHearingData(hearingStatus);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());

        var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID,DATE_FROM, DATE_TO);
        assertCommonValues(reportData);
        assertEquals("0", reportData.getReportSummary().getFtSessionDaysTotal());
        assertEquals("0", reportData.getReportSummary().getPtSessionDaysTotal());
        assertEquals("0", reportData.getReportSummary().getOtherSessionDaysTotal());
        assertEquals("0", reportData.getReportSummary().getSessionDaysTotal());
        assertEquals("0", reportData.getReportSummary().getPtSessionDaysPerCent());
        assertEquals(0, reportData.getReportSummary2List().size());
        assertEquals(0, reportData.getReportDetails().size());

    }

    @Test
    public void shouldShowCaseWithValidHearingStatus() {
        // Given a case has valid hearing status i.e "Heard"
        // and report data is requested
        // the case should be in the report data

        caseDataBuilder.withHearingData(HEARING_STATUS_HEARD);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());

        var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID,DATE_FROM, DATE_TO);
        assertCommonValues(reportData);
        assertEquals("1", reportData.getReportSummary().getFtSessionDaysTotal());
        assertEquals("1", reportData.getReportSummary().getPtSessionDaysTotal());
        assertEquals("1", reportData.getReportSummary().getOtherSessionDaysTotal());
        assertEquals("3", reportData.getReportSummary().getSessionDaysTotal());
        assertEquals("33", reportData.getReportSummary().getPtSessionDaysPerCent());
        assertEquals(1, reportData.getReportSummary2List().size());
        assertEquals(3, reportData.getReportDetails().size());
        assertReportSummary2Values(reportData);
    }

    private void assertReportSummary2Values(SessionDaysReportData reportData) {
        var reportSummary2 = reportData.getReportSummary2List().get(0);
        assertEquals("1", reportSummary2.getFtSessionDays());
        assertEquals("1", reportSummary2.getPtSessionDays());
        assertEquals("1", reportSummary2.getOtherSessionDays());
        assertEquals("3", reportSummary2.getSessionDaysTotalDetail());
        assertEquals("2022-01-20", reportSummary2.getDate());
    }

    @ParameterizedTest
    @CsvSource({"ftcJudge, FTC, 0 ", "ptcJudge, PTC, 1 ", "* Not Allocated,*, 2"})
    public void assertReportDetailsValues(String judge, String judgeType, int index) {
        caseDataBuilder.withHearingData(HEARING_STATUS_HEARD);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());
        var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID,DATE_FROM, DATE_TO);
        assertCommonValues(reportData);
        var reportDetail = reportData.getReportDetails().get(index);
        assertEquals("111", reportDetail.getCaseReference());
        assertEquals("Clerk A", reportDetail.getHearingClerk());
        assertEquals("2022-01-20", reportDetail.getHearingDate());
        assertEquals("1", reportDetail.getHearingNumber());
        assertEquals("Y", reportDetail.getHearingSitAlone());
        assertEquals("Y", reportDetail.getHearingTelConf());
        assertEquals(FULL_DAY, reportDetail.getSessionType());
        assertEquals(judge, reportDetail.getHearingJudge());
        assertEquals(judgeType, reportDetail.getJudgeType());


    }

    private void assertCommonValues(SessionDaysReportData reportData) {
        assertNotNull(reportData);
        assertEquals("Manchester", reportData.getReportSummary().getOffice());
    }
}
