package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.util.Strings;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
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

public class HearingsByHearingTypeReportTest {
    List<SubmitEvent> submitEvents = new ArrayList<>();
    List<SubmitEvent> submitEventsWithoutHearings = new ArrayList<>();
    List<SubmitEvent> submitEventsWithoutDates = new ArrayList<>();

    @Before
    public void setup() {

        DateListedTypeItem dateListedTypeItem = createHearingDateListed("1970-06-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING, "JM",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "1", "lead1" ));
        dateListedTypeItem = createHearingDateListed("2021-07-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_REMEDY, "Hybrid",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "2", "lead2"));
        dateListedTypeItem = createHearingDateListed("2021-06-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Stage 1",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "3", "lead3"));
        dateListedTypeItem = createHearingDateListed("2020-06-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, "Video",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "4", "lead4"));
        dateListedTypeItem = createHearingDateListed("2020-11-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING_CM, "Full Panel",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "5", "lead5"));
        dateListedTypeItem = createHearingDateListed("2021-10-01T00:00:00.000",
                HEARING_STATUS_HEARD);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_RECONSIDERATION, "Yes",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "6", "lead6"));
        createSubmitEventsWithoutHearings();
        createSubmitEventsWithoutDates();
    }

    private void createSubmitEventsWithoutHearings() {
        List<HearingTypeItem> hearings = new ArrayList<>();
        submitEventsWithoutHearings.add(createSubmitEvent(hearings, "1", "lead1" ));
        hearings = new ArrayList<>();
        submitEventsWithoutHearings.add(createSubmitEvent(hearings, "2", "lead2"));
    }

    private void createSubmitEventsWithoutDates() {
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType type = new DateListedType();
        dateListedTypeItem.setValue(type);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING, "JM",
                dateListedTypeItem));
        submitEventsWithoutDates.add(createSubmitEvent(hearings, "1", "lead1" ));
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_REMEDY, "Hybrid",
                dateListedTypeItem));
        submitEventsWithoutDates.add(createSubmitEvent(hearings, "2", "lead2"));
    }

    @Test
    public void testReportHeaderAreZeroIfNoCasesExist() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();
        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData listingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEvents);
        verifyReportHeaderIsZeroWhenNoCasesExist(listingData);
    }

    @Test
    public void testReportHeaderAreZeroIfNoHearingCollectionExist() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();
        listingDetails.setCaseData(caseData);
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData listingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEventsWithoutHearings);
        verifyReportHeaderIsZeroWithNoHearings(listingData);
    }

    @Test
    public void testReportHeaderAreZeroIfNoDateCollectionExist() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();
        listingDetails.setCaseData(caseData);
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData listingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEventsWithoutDates);
        verifyReportHeaderIsZeroWithNoHearings(listingData);
    }

    private void verifyReportHeaderIsZeroWhenNoCasesExist(ListingData listingData) {
        AdhocReportType adhocReportType = listingData.getLocalReportsSummaryHdr();
        assertEquals(0, adhocReportType == null ? 0:Integer.parseInt(adhocReportType.getTotal()));
        assertEquals(0, adhocReportType == null ? 0:Integer.parseInt(adhocReportType.getHearing()));
        assertEquals(0, adhocReportType == null ? 0:Integer.parseInt(adhocReportType.getHearingCM()));
        assertEquals(0, adhocReportType == null ? 0:Integer.parseInt(adhocReportType.getCosts()));
        assertEquals(0, adhocReportType == null ? 0:Integer.parseInt(adhocReportType.getHearingPrelim()));
        assertEquals(0, adhocReportType == null ? 0:Integer.parseInt(adhocReportType.getReconsider()));
        assertEquals(0, adhocReportType == null ? 0:Integer.parseInt(adhocReportType.getRemedy()));

    }

    private void verifyReportHeaderIsZeroWithNoHearings(ListingData listingData) {
        AdhocReportType adhocReportType = listingData.getLocalReportsSummaryHdr();
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getTotal())?0:Integer.parseInt(adhocReportType.getTotal()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearing())?0:Integer.parseInt(adhocReportType.getHearing()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingCM())?0:Integer.parseInt(adhocReportType.getHearingCM()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getCosts())?0:Integer.parseInt(adhocReportType.getCosts()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingPrelim())?0:Integer.parseInt(adhocReportType.getHearingPrelim()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getReconsider())?0:Integer.parseInt(adhocReportType.getReconsider()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getRemedy())?0:Integer.parseInt(adhocReportType.getRemedy()));

    }

    @Test
    public void testIgnoreCaseIfHearingStatusIsNotHeard() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);

        List<SubmitEvent> submitEvents = new ArrayList<>();
        DateListedTypeItem dateListedTypeItem = createHearingDateListed("2020-01-01T00:00:00",
                HEARING_STATUS_LISTED);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING, "Telephone",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "1", "lead1"));

        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEvents);

        verifyReportHeaderIsZeroWithNoHearings(reportListingData);
    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportHdr() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEvents);

        AdhocReportType adhocReportType = reportListingData.getLocalReportsSummaryHdr();
        assertEquals(6, Strings.isNullOrEmpty(adhocReportType.getTotal())?0:Integer.parseInt(adhocReportType.getTotal()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getHearing())?0:Integer.parseInt(adhocReportType.getHearing()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getHearingCM())?0:Integer.parseInt(adhocReportType.getHearingCM()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getCosts())?0:Integer.parseInt(adhocReportType.getCosts()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getHearingPrelim())?0:Integer.parseInt(adhocReportType.getHearingPrelim()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getReconsider())?0:Integer.parseInt(adhocReportType.getReconsider()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getRemedy())?0:Integer.parseInt(adhocReportType.getRemedy()));

    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportSummary() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEvents);

        List<AdhocReportTypeItem> adhocReportTypeItemList = reportListingData.getLocalReportsSummary();
        AdhocReportType adhocReportType = adhocReportTypeItemList.get(0).getValue();
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getTotal())?0:Integer.parseInt(adhocReportType.getTotal()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getHearing())?0:Integer.parseInt(adhocReportType.getHearing()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingCM())?0:Integer.parseInt(adhocReportType.getHearingCM()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getCosts())?0:Integer.parseInt(adhocReportType.getCosts()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingPrelim())?0:Integer.parseInt(adhocReportType.getHearingPrelim()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getReconsider())?0:Integer.parseInt(adhocReportType.getReconsider()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getRemedy())?0:Integer.parseInt(adhocReportType.getRemedy()));
        assertEquals("1970-06-01T00:00:00.000", Strings.isNullOrEmpty(adhocReportType.getDate())?0:adhocReportType.getDate());

    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportSummaryHdr2() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEvents);

        AdhocReportType adhocReportType = reportListingData.getLocalReportsSummaryHdr2();
        var listingHistory = adhocReportType.getListingHistory();
        var number = listingHistory.get(2).getValue().getHearingNumber();
        var numbers = number.split("[|]");
        assertEquals(2, Strings.isNullOrEmpty(numbers[6])?0:Integer.parseInt(numbers[6]));
        assertEquals(1, Strings.isNullOrEmpty(numbers[0])?0:Integer.parseInt(numbers[0]));
        assertEquals(1, Strings.isNullOrEmpty(numbers[2])?0:Integer.parseInt(numbers[2]));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getCosts())?0:Integer.parseInt(adhocReportType.getCosts()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingPrelim())?0:Integer.parseInt(adhocReportType.getHearingPrelim()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getReconsider())?0:Integer.parseInt(adhocReportType.getReconsider()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getRemedy())?0:Integer.parseInt(adhocReportType.getRemedy()));
        assertEquals("JM", Strings.isNullOrEmpty(numbers[7])?"":numbers[7]);

    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportSummary2() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEvents);

        List<AdhocReportTypeItem> adhocReportTypeItemList = reportListingData.getLocalReportsSummary2();

        AdhocReportType adhocReportType = adhocReportTypeItemList.get(2).getValue();
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getTotal())?0:Integer.parseInt(adhocReportType.getTotal()));
        assertEquals(1, Strings.isNullOrEmpty(adhocReportType.getHearing())?0:Integer.parseInt(adhocReportType.getHearing()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingCM())?0:Integer.parseInt(adhocReportType.getHearingCM()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getCosts())?0:Integer.parseInt(adhocReportType.getCosts()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingPrelim())?0:Integer.parseInt(adhocReportType.getHearingPrelim()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getReconsider())?0:Integer.parseInt(adhocReportType.getReconsider()));
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getRemedy())?0:Integer.parseInt(adhocReportType.getRemedy()));
        assertEquals("1970-06-01T00:00:00.000", Strings.isNullOrEmpty(adhocReportType.getDate())?0:adhocReportType.getDate());
        assertEquals("JM", Strings.isNullOrEmpty(adhocReportType.getSubSplit())?0:adhocReportType.getSubSplit());

    }

    @Test
    public void testConsiderCaseIfValidHearingStatusReportDetail() {

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();
        listingDetails.setCaseData(listingData);
        HearingsByHearingTypeReport report = new HearingsByHearingTypeReport();
        ListingData reportListingData = report.processHearingsByHearingTypeRequest(listingDetails, submitEvents);

        List<AdhocReportTypeItem> adhocReportTypeItemList = reportListingData.getLocalReportsDetail();

        AdhocReportType adhocReportType = adhocReportTypeItemList.get(0).getValue();
        assertEquals("1", Strings.isNullOrEmpty(adhocReportType.getCaseReference())?0:adhocReportType.getCaseReference());
        assertEquals("Y", Strings.isNullOrEmpty(adhocReportType.getLeadCase())?0:adhocReportType.getLeadCase());
        assertEquals(471, Strings.isNullOrEmpty(adhocReportType.getHearingDuration())
                ? 0:Integer.parseInt(adhocReportType.getHearingDuration()));
        assertEquals("multiRef, subMulti", Strings.isNullOrEmpty(adhocReportType.getMultSub())?0:adhocReportType.getMultSub());
        assertEquals("Hearing", Strings.isNullOrEmpty(adhocReportType.getHearingType())?0:adhocReportType.getHearingType());
        assertEquals(0, Strings.isNullOrEmpty(adhocReportType.getHearingTelConf())?0:adhocReportType.getHearingTelConf());
        assertEquals("Y", Strings.isNullOrEmpty(adhocReportType.getJudicialMediation())?0:adhocReportType.getJudicialMediation());
        assertEquals("clerk1", Strings.isNullOrEmpty(adhocReportType.getHearingClerk())?0:adhocReportType.getHearingClerk());

        assertEquals("1970-06-01T00:00:00.000", Strings.isNullOrEmpty(adhocReportType.getDate())?0:adhocReportType.getDate());

    }

    private SubmitEvent createSubmitEvent(List<HearingTypeItem> hearingCollection, String caseNo,String lead) {
        SubmitEvent submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setHearingCollection(hearingCollection);
        caseData.setEthosCaseReference(caseNo);
        caseData.setLeadClaimant(lead);
        caseData.setMultipleReference("multiRef");
        caseData.setSubMultipleName("subMulti");
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

    private DateListedTypeItem createHearingDateListed(String listedDate, String status) {
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(status);
        dateListedType.setHearingClerk("clerk1");
        dateListedTypeItem.setValue(dateListedType);
        dateListedType.setHearingTimingStart(LocalDateTime.now().minusMinutes(480).toString());
        dateListedType.setHearingTimingFinish(LocalDateTime.now().toString());
        dateListedType.setHearingTimingResume(LocalDateTime.now().minusMinutes(230).toString());
        dateListedType.setHearingTimingBreak(LocalDateTime.now().minusMinutes(240).toString());

        return dateListedTypeItem;
    }

    private HearingTypeItem createHearing(String type, String subSplitHeader, DateListedTypeItem... dateListedTypeItems) {
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = new HearingType();
        hearingType.setHearingType(type);
        hearingType.setHearingNumber("1");
        switch (subSplitHeader) {
            case "Full Panel":
                hearingType.setHearingSitAlone("Full");
            case "EJ Sit Alone":
                hearingType.setHearingSitAlone("Yes");
            case "JM":
                hearingType.setJudicialMediation("JM");
            case "Tel Con":
                hearingType.setHearingFormat(List.of ("Telephone"));
            case "Video":
                hearingType.setHearingFormat(List.of ("Video"));
            case "Hybrid":
                hearingType.setHearingFormat(List.of ("Hybrid"));
            case "In Person":
                hearingType.setHearingFormat(List.of ("In Person"));
            case "Stage 1":
                hearingType.setHearingStage("Stage 1");
            case "Stage 2":
                hearingType.setHearingStage("Stage 2");
            case "Stage 3":
                hearingType.setHearingStage("Stage 3");
                default:
        }

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
