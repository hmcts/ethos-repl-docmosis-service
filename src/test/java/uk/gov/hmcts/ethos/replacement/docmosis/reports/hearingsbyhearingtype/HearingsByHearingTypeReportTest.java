package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_LISTING_CASE_TYPE_ID;
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
        when(reportDataSource.getData(new ReportParams(
                MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO))).thenReturn(submitEvents);
        hearingsByHearingTypeReport = new HearingsByHearingTypeReport(reportDataSource);
    }

    @Test
     void testReportHeaderAreZeroIfNoCasesExist() {
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
     void testReportHeaderAreZeroIfNoHearingCollectionExist() {
        submitEvents.clear();
        submitEvents.add(caseDataBuilder.withNoHearings());
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
     void testReportHeaderAreZeroIfNoDateCollectionExist() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEventsWithoutDates());
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    private void verifyReportHeaderIsZeroWithNoHearings(HearingsByHearingTypeReportData reportData) {
        HearingsByHearingTypeReportSummaryHdr reportHdr = reportData.getReportSummaryHdr();
        assertEquals("0", reportHdr.getFields().getTotal());
        assertEquals("0", reportHdr.getFields().getHearingCount());
        assertEquals("0", reportHdr.getFields().getCmCount());
        assertEquals("0", reportHdr.getFields().getCostsCount());
        assertEquals("0", reportHdr.getFields().getHearingPrelimCount());
        assertEquals("0", reportHdr.getFields().getReconsiderCount());
        assertEquals("0", reportHdr.getFields().getRemedyCount());

    }

    @Test
     void testIgnoreCaseIfHearingStatusIsNotHeard() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(
                HEARING_STATUS_LISTED, "multiRef", "subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
     void testConsiderCaseIfValidHearingStatusReportHdr() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(
                HEARING_STATUS_HEARD, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        HearingsByHearingTypeReportSummaryHdr reportSummaryHdr = reportData.getReportSummaryHdr();
        assertEquals("6", reportSummaryHdr.getFields().getTotal());
        assertEquals("1", reportSummaryHdr.getFields().getHearingCount());
        assertEquals("1", reportSummaryHdr.getFields().getCmCount());
        assertEquals("1", reportSummaryHdr.getFields().getCostsCount());
        assertEquals("1", reportSummaryHdr.getFields().getHearingPrelimCount());
        assertEquals("1", reportSummaryHdr.getFields().getReconsiderCount());
        assertEquals("1", reportSummaryHdr.getFields().getRemedyCount());

    }

    @Test
     void testConsiderCaseIfValidHearingStatusReportSummary() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef", "subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary> reportSummaryList = reportData.getReportSummaryList();
        HearingsByHearingTypeReportSummary reportSummary = reportSummaryList.get(0);
        assertEquals("1", reportSummary.getFields().getTotal());
        assertEquals("1", reportSummary.getFields().getHearingCount());
        assertEquals("0", reportSummary.getFields().getCmCount());
        assertEquals("0", reportSummary.getFields().getCostsCount());
        assertEquals("0", reportSummary.getFields().getHearingPrelimCount());
        assertEquals("0", reportSummary.getFields().getReconsiderCount());
        assertEquals("0", reportSummary.getFields().getRemedyCount());
        assertEquals("2022-01-01", reportSummary.getFields().getDate());
    }

    @Test
     void testConsiderCaseIfValidHearingStatusReportSummaryHdr2() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef", "subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList = reportData.getReportSummary2HdrList();
        HearingsByHearingTypeReportSummary2Hdr reportSummary2Hdr = reportSummary2HdrList.get(0);
        assertEquals("1", reportSummary2Hdr.getFields().getTotal());
        assertEquals("0", reportSummary2Hdr.getFields().getHearingCount());
        assertEquals("1", reportSummary2Hdr.getFields().getCmCount());
        assertEquals("0", reportSummary2Hdr.getFields().getCostsCount());
        assertEquals("0", reportSummary2Hdr.getFields().getHearingPrelimCount());
        assertEquals("0", reportSummary2Hdr.getFields().getReconsiderCount());
        assertEquals("0", reportSummary2Hdr.getFields().getRemedyCount());
        assertEquals("Full Panel", reportSummary2Hdr.getFields().getSubSplit());
    }

    @Test
     void testConsiderCaseIfValidHearingStatusReportSummary2() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(
                HEARING_STATUS_HEARD, "multiRef", "subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary2> reportSummary2List = reportData.getReportSummary2List();
        HearingsByHearingTypeReportSummary2 reportSummary2 = reportSummary2List.get(0);
        assertEquals("1", reportSummary2.getFields().getTotal());
        assertEquals("1", reportSummary2.getFields().getHearingCount());
        assertEquals("0", reportSummary2.getFields().getCmCount());
        assertEquals("0", reportSummary2.getFields().getCostsCount());
        assertEquals("0", reportSummary2.getFields().getHearingPrelimCount());
        assertEquals("0", reportSummary2.getFields().getReconsiderCount());
        assertEquals("0", reportSummary2.getFields().getRemedyCount());
        assertEquals("JM", reportSummary2.getFields().getSubSplit());
        assertEquals("2022-01-01", reportSummary2.getFields().getDate());

    }

    @Test
    void testIfDatesAreInOrder() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "", ""));
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setListedDate("2022-01-02T00:00:00.000");
        submitEvents.get(1).getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setListedDate("2022-01-01T00:00:00.000");
        submitEvents.get(2).getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setListedDate("2022-01-03T00:00:00.000");
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary> reportSummaryList = reportData.getReportSummaryList();
        assertEquals("2022-01-01", reportSummaryList.get(0).getFields().getDate());
        assertEquals("2022-01-02", reportSummaryList.get(1).getFields().getDate());
        assertEquals("2022-01-03", reportSummaryList.get(2).getFields().getDate());

    }

    @Test
    void testShowSubSplits() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef", "subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportSummary2> reportSummary2List = reportData.getReportSummary2List();
        assertEquals("JM", reportSummary2List.get(0).getFields().getSubSplit());
        assertEquals("Hybrid", reportSummary2List.get(1).getFields().getSubSplit());
        assertEquals("Video", reportSummary2List.get(2).getFields().getSubSplit());
        assertEquals("Stage 1", reportSummary2List.get(3).getFields().getSubSplit());
        assertEquals("Full Panel", reportSummary2List.get(4).getFields().getSubSplit());
    }

    @Test
     void testConsiderCaseIfValidHearingStatusReportDetail() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "multiRef", "subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportDetail> reportDetailList = reportData.getReportDetails();
        HearingsByHearingTypeReportDetail reportDetail = reportDetailList.get(0);
        assertEquals("111", reportDetail.getCaseReference());
        assertEquals("Y", reportDetail.getLead());
        assertEquals("330", reportDetail.getDuration());
        assertEquals("multiRef, subMulti", reportDetail.getMultiSub());
        assertEquals("Hearing", reportDetail.getHearingType());
        assertEquals("", reportDetail.getTel());
        assertEquals("clerk1", reportDetail.getHearingClerk());
        assertEquals("2022-01-01", reportDetail.getDetailDate());
    }

    @Test
     void testConsiderCaseIfNullMultiSubInReportDetail() {
        submitEvents.clear();
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, null, null));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportDetail> reportDetailList = reportData.getReportDetails();
        HearingsByHearingTypeReportDetail reportDetail = reportDetailList.get(0);
        assertEquals("0 -  Not Allocated, 0 -  Not Allocated", reportDetail.getMultiSub());
    }

    @Test
     void multipleHearingsWithOneInRangeAndOneOutOfRange() {
        submitEvents.clear();
        submitEvents.add(caseDataBuilder.createSubmitEventDateInOutRange());
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        var reportSummaryHdr = reportData.getReportSummaryHdr();
        assertEquals("1", reportSummaryHdr.getFields().getTotal());
        assertEquals("1", reportSummaryHdr.getFields().getCostsCount());
    }

    @ParameterizedTest
    @CsvSource({"Start, 0", "Finish, 0", "Break, 360", "Resume, 360"})
     void nullTimeOnHearing(String time, String result) {
        submitEvents.clear();
        submitEvents.add(caseDataBuilder.createSubmitEventNullTime(time));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(
                new ReportParams(MANCHESTER_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(result, reportDetail.getDuration());
    }

}