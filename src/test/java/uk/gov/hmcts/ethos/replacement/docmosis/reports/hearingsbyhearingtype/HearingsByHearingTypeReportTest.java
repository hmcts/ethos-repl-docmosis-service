package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.JudgeRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysCaseDataBuilder;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice.JpaJudgeService;

public class HearingsByHearingTypeReportTest {

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
        submitEvents.add(caseDataBuilder.withNoHearings());
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
    public void testReportHeaderAreZeroIfNoDateCollectionExist() {

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
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_LISTED, "multiRef","subMulti"));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        verifyReportHeaderIsZeroWithNoHearings(reportData);
    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportHdr() {
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
        submitEvents.addAll(caseDataBuilder.createSubmitEvents(HEARING_STATUS_HEARD, "",""));
        HearingsByHearingTypeReportData reportData = hearingsByHearingTypeReport.generateReport(new ReportParams(NEWCASTLE_LISTING_CASE_TYPE_ID, DATE_FROM, DATE_TO));
        List<HearingsByHearingTypeReportDetail> reportDetailList = reportData.getReportDetails();
        HearingsByHearingTypeReportDetail reportDetail = reportDetailList.get(0);
        assertEquals("0 -  Not Allocated, 0 -  Not Allocated", reportDetail.getMultiSub());
    }

    @Test
    public void multipleHearingsWithOneInRangeAndOneOutOfRange() {
        // Hearing outside of range
        var dateListedTypeItem = createHearingDateListed("2021-05-30T00:00:00.000", HEARING_STATUS_HEARD);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING, "Video",
                dateListedTypeItem));

        // Hearing inside of search range
        dateListedTypeItem = createHearingDateListed("2021-06-01T00:00:00.000", HEARING_STATUS_HEARD);
        var hearingTypeItem = createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Video", dateListedTypeItem);
        hearings.add(hearingTypeItem);

        var submitEvent = createSubmitEvent(hearings, "123456", "No");
        var submitEventList = List.of(submitEvent);

        var listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        var listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        var report = new HearingsByHearingTypeReportOld();
        var reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEventList,
                DATE_FROM, DATE_TO);

        var adhocReportType = reportListingData.getLocalReportsSummaryHdr();
        assertEquals("1", adhocReportType.getTotal());
        assertEquals("1", adhocReportType.getCosts());

    }

    @Test
    public void nullStartTimeOnHearing() {
        List<HearingTypeItem> hearings = new ArrayList<>();
        var dateListedTypeItem = createHearingDateListed("2021-06-01T00:00:00.000", HEARING_STATUS_HEARD);
        dateListedTypeItem.getValue().setHearingTimingStart(null);
        var hearingTypeItem = createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Tel Con", dateListedTypeItem);
        hearings.add(hearingTypeItem);

        var submitEvent = createSubmitEvent(hearings, "123456", "No");
        var submitEventList = List.of(submitEvent);

        var listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        var listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        var report = new HearingsByHearingTypeReportOld();
        var reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEventList,
                DATE_FROM, DATE_TO);

        var adhocReportType = reportListingData.getLocalReportsDetail().get(0).getValue();
        assertEquals("0", adhocReportType.getHearingDuration());
    }

    @Test
    public void nullFinishTimeOnHearing() {
        List<HearingTypeItem> hearings = new ArrayList<>();
        var dateListedTypeItem = createHearingDateListed("2021-06-01T00:00:00.000", HEARING_STATUS_HEARD);
        dateListedTypeItem.getValue().setHearingTimingFinish(null);
        var hearingTypeItem = createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Tel Con", dateListedTypeItem);
        hearings.add(hearingTypeItem);

        var submitEvent = createSubmitEvent(hearings, "123456", "No");
        var submitEventList = List.of(submitEvent);

        var listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        var listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        var report = new HearingsByHearingTypeReportOld();
        var reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEventList,
                DATE_FROM, DATE_TO);

        var adhocReportType = reportListingData.getLocalReportsDetail().get(0).getValue();
        assertEquals("0", adhocReportType.getHearingDuration());
    }

    @Test
    public void nullBreakTimeOnHearing() {
        List<HearingTypeItem> hearings = new ArrayList<>();
        var dateListedTypeItem = createHearingDateListed("2021-06-01T00:00:00.000", HEARING_STATUS_HEARD);
        dateListedTypeItem.getValue().setHearingTimingBreak(null);
        var hearingTypeItem = createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Tel Con", dateListedTypeItem);
        hearings.add(hearingTypeItem);

        var submitEvent = createSubmitEvent(hearings, "123456", "No");
        var submitEventList = List.of(submitEvent);

        var listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        var listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        var report = new HearingsByHearingTypeReportOld();
        var reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEventList,
                DATE_FROM, DATE_TO);

        var adhocReportType = reportListingData.getLocalReportsDetail().get(0).getValue();
        assertEquals("480", adhocReportType.getHearingDuration());
    }

    @Test
    public void nullResumeTimeOnHearing() {
        List<HearingTypeItem> hearings = new ArrayList<>();
        var dateListedTypeItem = createHearingDateListed("2021-06-01T00:00:00.000", HEARING_STATUS_HEARD);
        dateListedTypeItem.getValue().setHearingTimingResume(null);
        var hearingTypeItem = createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Tel Con", dateListedTypeItem);
        hearings.add(hearingTypeItem);

        var submitEvent = createSubmitEvent(hearings, "123456", "No");
        var submitEventList = List.of(submitEvent);

        var listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        var listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        var report = new HearingsByHearingTypeReportOld();
        var reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEventList,
                DATE_FROM, DATE_TO);

        var adhocReportType = reportListingData.getLocalReportsDetail().get(0).getValue();
        assertEquals("480", adhocReportType.getHearingDuration());
    }

}