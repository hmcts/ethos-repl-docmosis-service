package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.JudgeRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice.JpaJudgeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import static uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.JudgeEmploymentStatus.UNKNOWN;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.TWO_JUDGES;
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
    void setup() {
        submitEvents.clear();
        caseDataBuilder = new SessionDaysCaseDataBuilder();
        reportDataSource = mock(SessionDaysReportDataSource.class);
        judgeRepository = mock(JudgeRepository.class);
        jpaJudgeService = new JpaJudgeService(judgeRepository);
        when(reportDataSource.getData(MANCHESTER_CASE_TYPE_ID, DATE_FROM, DATE_TO)).thenReturn(submitEvents);
        List<Judge> judges  = getJudges();
        when(judgeRepository.findByTribunalOffice("Manchester")).thenReturn(judges);
        sessionDaysReport = new SessionDaysReport(reportDataSource, jpaJudgeService);
    }

    private List<Judge> getJudges() {
        Judge judge1 = new Judge();
        judge1.setEmploymentStatus(SALARIED);
        judge1.setName("ftcJudge");
        Judge judge2 = new Judge();
        judge2.setEmploymentStatus(FEE_PAID);
        judge2.setName("ptcJudge");
        Judge judge3 = new Judge();
        judge3.setEmploymentStatus(UNKNOWN);
        judge3.setName("unknownJudge");
        return Arrays.asList(judge1, judge2, judge3);
    }

    @Test
     void shouldNotShowCaseWithNoHearings() {
        // Given a case has no hearing
        // and report data is requested
        // the case should not be in the report data

        caseDataBuilder.withNoHearings();
        submitEvents.add(caseDataBuilder
                    .buildAsSubmitEvent());

        var reportData = sessionDaysReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
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

        var reportData = sessionDaysReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
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
     void shouldShowCaseWithValidHearingStatus() {
        // Given a case has valid hearing status i.e "Heard"
        // and report data is requested
        // the case should be in the report data

        caseDataBuilder.withHearingData(HEARING_STATUS_HEARD);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());

        var reportData = sessionDaysReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        assertCommonValues(reportData);
        assertEquals("1", reportData.getReportSummary().getFtSessionDaysTotal());
        assertEquals("1", reportData.getReportSummary().getPtSessionDaysTotal());
        assertEquals("2", reportData.getReportSummary().getOtherSessionDaysTotal());
        assertEquals("4", reportData.getReportSummary().getSessionDaysTotal());
        assertEquals("25", reportData.getReportSummary().getPtSessionDaysPerCent());
        assertEquals(1, reportData.getReportSummary2List().size());
        assertEquals(4, reportData.getReportDetails().size());
        assertReportSummary2Values(reportData);
    }

    private void assertReportSummary2Values(SessionDaysReportData reportData) {
        var reportSummary2 = reportData.getReportSummary2List().getFirst();
        assertEquals("1", reportSummary2.getFtSessionDays());
        assertEquals("1", reportSummary2.getPtSessionDays());
        assertEquals("2", reportSummary2.getOtherSessionDays());
        assertEquals("4", reportSummary2.getSessionDaysTotalDetail());
        assertEquals("2022-01-20", reportSummary2.getDate());
    }

    @ParameterizedTest
    @CsvSource({"ftcJudge, FTC, 0 ", "ptcJudge, PTC, 1 ", "* Not Allocated,, 2", "unknownJudge, UNKNOWN, 3"})
     void assertReportDetailsValues(String judge, String judgeType, int index) {
        caseDataBuilder.withHearingData(HEARING_STATUS_HEARD);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());
        var reportData = sessionDaysReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
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

    @Test
    void shouldShowCasesWithAdditionalJudges() {
        // Create 4 hearings with the first hearing having 2 judges
        caseDataBuilder.withHearingData(HEARING_STATUS_HEARD);
        submitEvents.add(caseDataBuilder.buildAsSubmitEvent());
        submitEvents.getFirst().getCaseData().getHearingCollection().getFirst().getValue()
            .setHearingSitAlone(TWO_JUDGES);
        submitEvents.getFirst().getCaseData().getHearingCollection().getFirst().getValue()
             .setAdditionalJudge("ptcJudge");

        SessionDaysReportData reportData = sessionDaysReport.generateReport(
             new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));

        assertCommonValues(reportData);
        // 4 hearings plus 1 Two Judges hearing
        assertEquals(5, reportData.getReportDetails().size());
    }

    private void assertCommonValues(SessionDaysReportData reportData) {
        assertNotNull(reportData);
        assertEquals("Manchester", reportData.getReportSummary().getOffice());
    }
}
