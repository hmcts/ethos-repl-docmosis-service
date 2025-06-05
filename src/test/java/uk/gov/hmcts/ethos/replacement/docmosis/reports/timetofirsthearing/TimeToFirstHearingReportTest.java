package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import org.assertj.core.util.Strings;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_REMEDY;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

public class TimeToFirstHearingReportTest {

    @Test
    public void testReportHeaderTotalsAreZeroIfNoCasesExist() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();
        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        TimeToFirstHearingReport timeToFirstHearingReport = new TimeToFirstHearingReport();
        ListingData listingData = timeToFirstHearingReport.generateReportData(listingDetails, submitEvents);
        verifyReportHeaderIsZero(listingData);
    }

    private void verifyReportHeaderIsZero(ListingData listingData) {
        AdhocReportType adhocReportType = listingData.getLocalReportsDetailHdr();
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getTotal())
                ? 0 : Integer.parseInt(adhocReportType.getTotal()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getTotal26wk())
                ? 0 : Integer.parseInt(adhocReportType.getTotal26wk()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getTotalx26wk())
                ? 0 : Integer.parseInt(adhocReportType.getTotalx26wk()));
        assertEquals(0.00, Strings.isNullOrEmpty(adhocReportType.getTotal26wkPerCent())
                ? 0.00 : Float.parseFloat(adhocReportType.getTotal26wkPerCent()), .00);
        assertEquals(0.00, Strings.isNullOrEmpty(adhocReportType.getTotalx26wkPerCent())
                ? 0.00 : Float.parseFloat(adhocReportType.getTotalx26wkPerCent()),.00);
    }

    @Test
    public void testIgnoreCaseIfItContainsNoHearings() {
        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();

        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(Collections.emptyList(), CONCILIATION_TRACK_FAST_TRACK, "1970-01-01"));

        TimeToFirstHearingReport timeToFirstHearingReport = new TimeToFirstHearingReport();
        ListingData listingData = timeToFirstHearingReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(listingData);
    }

    @Test
    public void testIgnoreCaseIfHearingTypeInvalid() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        DateListedTypeItem dateListedTypeItem = createHearingDateListed("2020-01-01T00:00:00",
                HEARING_STATUS_HEARD);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_REMEDY,
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, CONCILIATION_TRACK_FAST_TRACK, "2021-01-01T00:00:00"));

        TimeToFirstHearingReport timeToFirstHearingReport = new TimeToFirstHearingReport();
        ListingData reportListingData = timeToFirstHearingReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(reportListingData);
    }

    @Test
    public void testConsiderCaseIfHearingTypeValid() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        DateListedTypeItem dateListedTypeItem = createHearingDateListed("1970-06-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING,
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings,CONCILIATION_TRACK_FAST_TRACK, "1970-04-01"));

        TimeToFirstHearingReport timeToFirstHearingReport = new TimeToFirstHearingReport();
        ListingData reportListingData = timeToFirstHearingReport.generateReportData(listingDetails, submitEvents);

        AdhocReportType adhocReportType = reportListingData.getLocalReportsDetailHdr();
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getTotalCases())
                ? 0 : Integer.parseInt(adhocReportType.getTotalCases()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getTotal26wk())
                ? 0 : Integer.parseInt(adhocReportType.getTotal26wk()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getTotalx26wk())
                ? 0 : Integer.parseInt(adhocReportType.getTotalx26wk()));
        assertEquals(100, Strings.isNullOrEmpty(adhocReportType.getTotal26wkPerCent())
                ? 0 : Float.parseFloat(adhocReportType.getTotal26wkPerCent()), .00);
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getTotalx26wkPerCent())
                ? 0 : Float.parseFloat(adhocReportType.getTotalx26wkPerCent()), .00);
    }

    @Test
    public void testFirstHearingNotWithin26Weeks() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        DateListedTypeItem dateListedTypeItem = createHearingDateListed("2021-01-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING,
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings,CONCILIATION_TRACK_FAST_TRACK, "2020-04-01"));

        TimeToFirstHearingReport timeToFirstHearingReport = new TimeToFirstHearingReport();
        ListingData reportListingData = timeToFirstHearingReport.generateReportData(listingDetails, submitEvents);

        AdhocReportType adhocReportType = reportListingData.getLocalReportsDetailHdr();
        assertEquals(1, Integer.parseInt(adhocReportType.getTotalCases()));
        assertEquals(0, Integer.parseInt(adhocReportType.getTotal26wk()));
        assertEquals(1, Integer.parseInt(adhocReportType.getTotalx26wk()));
        assertEquals(0, Float.parseFloat(adhocReportType.getTotal26wkPerCent()), .00);
        assertEquals(100, Float.parseFloat(adhocReportType.getTotalx26wkPerCent()), .00);
    }

    private SubmitEvent createSubmitEvent(List<HearingTypeItem> hearingCollection,
                                          String conciliationTrack, String receiptDate) {
        SubmitEvent submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setConciliationTrack(conciliationTrack);
        caseData.setReceiptDate(receiptDate);
        caseData.setHearingCollection(hearingCollection);
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

    private DateListedTypeItem createHearingDateListed(String listedDate, String status) {
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(status);
        dateListedType.setHearingCaseDisposed(YES);
        dateListedTypeItem.setValue(dateListedType);

        return dateListedTypeItem;
    }

    private HearingTypeItem createHearing(String type, DateListedTypeItem... dateListedTypeItems) {
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = new HearingType();
        hearingType.setHearingType(type);

        List<DateListedTypeItem> hearingDateCollection = new ArrayList<>();
        Collections.addAll(hearingDateCollection, dateListedTypeItems);

        hearingType.setHearingDateCollection(hearingDateCollection);
        hearingTypeItem.setValue(hearingType);
        return hearingTypeItem;
    }

    private List<HearingTypeItem> createHearingCollection(HearingTypeItem... hearings) {
        List<HearingTypeItem> hearingTypeItems = new ArrayList<>();
        Collections.addAll(hearingTypeItems, hearings);
        return hearingTypeItems;
    }

}