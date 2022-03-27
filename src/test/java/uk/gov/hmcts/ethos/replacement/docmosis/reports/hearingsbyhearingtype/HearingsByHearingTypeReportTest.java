package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;


import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;

 class HearingsByHearingTypeReportTest {

    HearingsByHearingTypeReportDataSource reportDataSource;
    HearingsByHearingTypeReport hearingsByHearingTypeReport;
    HearingsByHearingTypeCaseDataBuilder caseDataBuilder = new HearingsByHearingTypeCaseDataBuilder();
    List<HearingsByHearingTypeSubmitEvent> submitEvents = new ArrayList<>();
    static final LocalDateTime BASE_DATE = LocalDateTime.of(2022,  1, 1,  0,  0, 0);
    static final String DATE_FROM = BASE_DATE.minusDays(1).format(OLD_DATE_TIME_PATTERN);
    static final String DATE_TO = BASE_DATE.plusDays(24).format(OLD_DATE_TIME_PATTERN);

    @BeforeEach
    public void setup() {
        submitEvents.clear();
        caseDataBuilder = new HearingsByHearingTypeCaseDataBuilder();
        reportDataSource = mock(HearingsByHearingTypeReportDataSource.class);
        when(reportDataSource.getData(MANCHESTER_CASE_TYPE_ID, DATE_FROM, DATE_TO)).thenReturn(submitEvents);
        hearingsByHearingTypeReport = new HearingsByHearingTypeReport(reportDataSource);
    }

    @Test
    public void testReportHeaderAreZeroIfNoCasesExist() {
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
    public void testReportHeaderAreZeroIfNoHearingCollectionExist() {
        submitEvents.clear();
        submitEvents.add(caseDataBuilder.withNoHearings());
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
    public void testReportHeaderAreZeroIfNoDateCollectionExist() {
        submitEvents.clear();
       submitEvents.addAll(caseDataBuilder.createSubmitEventsWithoutDates());
       HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
       verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    private void verifyReportHeaderIsZeroWithNoHearings(HearingsByHearingTypeReportData reportData) {
        HearingsByHearingTypeReportSummaryHdr reportHdr = reportData.getReportSummaryHdr();
        assertEquals("0", reportHdr.getFields().getTotal());
        assertEquals("0", reportHdr.getFields().getHearingCount());
        assertEquals("0",reportHdr.getFields().getCmCount());
        assertEquals("0",reportHdr.getFields().getCostsCount());
        assertEquals("0",reportHdr.getFields().getHearingPrelimCount());
        assertEquals("0",reportHdr.getFields().getReconsiderCount());
        assertEquals("0",reportHdr.getFields().getRemedyCount());

    }

    @Test
    public void testIgnoreCaseIfHearingStatusIsNotHeard() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_LISTED, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportHdr() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        HearingsByHearingTypeReportSummaryHdr reportSummaryHdr = reportData.getReportSummaryHdr();
        assertEquals("1", reportSummaryHdr.getFields().getTotal());
        assertEquals("0", reportSummaryHdr.getFields().getHearingCount());
        assertEquals("0", reportSummaryHdr.getFields().getCmCount());
        assertEquals("1", reportSummaryHdr.getFields().getCostsCount());
        assertEquals("0", reportSummaryHdr.getFields().getHearingPrelimCount());
        assertEquals("0", reportSummaryHdr.getFields().getReconsiderCount());
        assertEquals("0", reportSummaryHdr.getFields().getRemedyCount());

    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportSummary() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary> reportSummaryList = reportData.getReportSummaryList();
        HearingsByHearingTypeReportSummary reportSummary = reportSummaryList.get(0);
        assertEquals("1", reportSummary.getFields().getTotal());
        assertEquals("0", reportSummary.getFields().getHearingCount());
        assertEquals("0", reportSummary.getFields().getCmCount());
        assertEquals("1", reportSummary.getFields().getCostsCount());
        assertEquals("0", reportSummary.getFields().getHearingPrelimCount());
        assertEquals("0", reportSummary.getFields().getReconsiderCount());
        assertEquals("0", reportSummary.getFields().getRemedyCount());
        assertEquals("2021-06-01 00:00:00.000", reportSummary.getDate());

    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportSummaryHdr2() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList = reportData.getReportSummary2HdrList();
        HearingsByHearingTypeReportSummary2Hdr reportSummary2Hdr = reportSummary2HdrList.get(0);
        assertEquals("1", reportSummary2Hdr.getFields().getTotal());
        assertEquals("0", reportSummary2Hdr.getFields().getHearingCount());
        assertEquals("0", reportSummary2Hdr.getFields().getCmCount());
        assertEquals("1", reportSummary2Hdr.getFields().getCostsCount());
        assertEquals("0", reportSummary2Hdr.getFields().getHearingPrelimCount());
        assertEquals("0", reportSummary2Hdr.getFields().getReconsiderCount());
        assertEquals("0", reportSummary2Hdr.getFields().getRemedyCount());
        assertEquals("Stage 1", reportSummary2Hdr.getSubSplit());
    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportSummary2() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary2> reportSummary2List = reportData.getReportSummary2List();
        HearingsByHearingTypeReportSummary2 reportSummary2 = reportSummary2List.get(0);
        assertEquals("1", reportSummary2.getFields().getTotal());
        assertEquals("0", reportSummary2.getFields().getHearingCount());
        assertEquals("0", reportSummary2.getFields().getCmCount());
        assertEquals("1", reportSummary2.getFields().getCostsCount());
        assertEquals("0", reportSummary2.getFields().getHearingPrelimCount());
        assertEquals("0", reportSummary2.getFields().getReconsiderCount());
        assertEquals("0", reportSummary2.getFields().getRemedyCount());
        assertEquals("Stage 1", reportSummary2.getSubSplit());
        assertEquals("2021-06-01 00:00:00.000", reportSummary2.getDate());

    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportDetail() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportDetail> reportDetailList = reportData.getReportDetails();
        HearingsByHearingTypeReportDetail reportDetail = reportDetailList.get(0);
        assertEquals("3", reportDetail.getCaseReference());
        assertEquals("Y", reportDetail.getLead());
        assertEquals("471", reportDetail.getDuration());
        assertEquals("multiRef, subMulti", reportDetail.getMultiSub());
        assertEquals("Costs Hearing", reportDetail.getHearingType());
        assertEquals("", reportDetail.getTel());
        assertEquals("clerk1", reportDetail.getHearingClerk());
        assertEquals("2021-06-01 00:00:00.000", reportDetail.getDate());
    }

    @Test
    public void testConsiderCaseIfNullMultiSubInReportDetail() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "",""));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportDetail> reportDetailList = reportData.getReportDetails();
        HearingsByHearingTypeReportDetail reportDetail = reportDetailList.get(0);
        assertEquals("0 -  Not Allocated, 0 -  Not Allocated", reportDetail.getMultiSub());
    }

    @Test
    public void multipleHearingsWithOneInRangeAndOneOutOfRange() {
       submitEvents.clear();
       submitEvents.add(caseDataBuilder.createSubmitEventDateInOutRange());
       HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
       var reportSummaryHdr = reportData.getReportSummaryHdr();
       assertEquals("1", reportSummaryHdr.getFields().getTotal());
       assertEquals("1", reportSummaryHdr.getFields().getCostsCount());
    }

    @ParameterizedTest
    @CsvSource({"Start, 0", "Finish, 0", "Break, 480", "Resume, 480"})
     void nullTimeOnHearing(String time, String result) {
        submitEvents.clear();
        submitEvents.add(caseDataBuilder.createSubmitEventNullTime(time));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(result, reportDetail.getDuration());
    }

}