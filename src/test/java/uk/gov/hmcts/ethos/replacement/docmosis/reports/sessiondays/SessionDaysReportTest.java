package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.JudgeEmploymentStatus;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.JudgeRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice.JpaJudgeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

 class SessionDaysReportTest {

    SessionDaysReportDataSource reportDataSource;
    JpaJudgeService jpaJudgeService;
    JudgeRepository judgeRepository;
    SessionDaysReport sessionDaysReport;
    SessionDaysCaseDataBuilder caseDataBuilder = new SessionDaysCaseDataBuilder();
    List<SessionDaysSubmitEvent> submitEvents = new ArrayList<>();
    static final LocalDateTime BASE_DATE = LocalDateTime.of(2022,  1, 1,  0,  0, 0);
    static final String DATE_FROM = BASE_DATE.minusDays(1).format(OLD_DATE_TIME_PATTERN);
    static final String DATE_TO = BASE_DATE.plusDays(24).format(OLD_DATE_TIME_PATTERN);

    @BeforeEach
    @Before
    public void setup() {
        submitEvents.clear();
        caseDataBuilder = new SessionDaysCaseDataBuilder();
        reportDataSource = mock(SessionDaysReportDataSource.class);
        jpaJudgeService = mock(JpaJudgeService.class);
        judgeRepository = mock(JudgeRepository.class);
        when(reportDataSource.getData(MANCHESTER_CASE_TYPE_ID, DATE_FROM, DATE_TO)).thenReturn(submitEvents);
        List<Judge> judges  = getJudges();
        when(jpaJudgeService.getJudges("Manchester")).thenReturn(judges);
        when(jpaJudgeService.getJudges("Manchester")).thenReturn(judges);
        when(judgeRepository.findByTribunalOffice("Manchester")).thenReturn(judges);
        when(judgeRepository.findByTribunalOffice("Manchester")).thenReturn(judges);
        sessionDaysReport = new SessionDaysReport(reportDataSource, jpaJudgeService);
    }

    private List<Judge> getJudges() {
        Judge judge1 = new Judge();
        judge1.setEmploymentStatus(SALARIED);
        judge1.setName("0001_ftcJudge");
        Judge judge2 = new Judge();
        judge2.setEmploymentStatus(FEE_PAID);
        judge2.setName("ptcJudge");
        return Arrays.asList(judge1, judge2);
    }

    @Test
    public void shouldNotShowCaseWithNoHearings() {
        // Given a case has no hearing
        // and report data is requested
        // the case should not be in the report data

        caseDataBuilder.withNoHearings();
        submitEvents.add(caseDataBuilder
                    .buildAsSubmitEvent());

        var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO);
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
     void shouldNotShowCaseWithInValidHearingStatus(String hearingStatus) {
        // Given a case has invalid hearing status
        // and report data is requested
        // the case should not be in the report data
        caseDataBuilder.withHearingData(hearingStatus);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());

        var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO);
        assertCommonValues(reportData);
        Assertions.assertEquals("0", reportData.getReportSummary().getFtSessionDaysTotal());
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

        var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO);
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
     void assertReportDetailsValues(String judge, String judgeType, int index) {
        caseDataBuilder.withHearingData(HEARING_STATUS_HEARD);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());
        var reportData = sessionDaysReport.generateReport(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO);
        assertCommonValues(reportData);
        var reportDetail = reportData.getReportDetails().get(index);
        Assertions.assertEquals("111", reportDetail.getCaseReference());
        Assertions.assertEquals("Clerk A", reportDetail.getHearingClerk());
        Assertions.assertEquals("2022-01-20", reportDetail.getHearingDate());
        Assertions.assertEquals("1", reportDetail.getHearingNumber());
        Assertions.assertEquals("Y", reportDetail.getHearingSitAlone());
        Assertions.assertEquals("Y", reportDetail.getHearingTelConf());
        assertEquals(FULL_DAY, reportDetail.getSessionType());
        assertEquals(judge, reportDetail.getHearingJudge());
        assertEquals(judgeType, reportDetail.getJudgeType());

    }

    private void assertCommonValues(SessionDaysReportData reportData) {
        assertNotNull(reportData);
        assertEquals("Manchester", reportData.getReportSummary().getOffice());
    }
}
