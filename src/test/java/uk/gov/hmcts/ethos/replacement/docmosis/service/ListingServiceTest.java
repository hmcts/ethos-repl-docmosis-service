package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueCaseData;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reports.eccreport.EccReportCaseData;
import uk.gov.hmcts.ecm.common.model.reports.eccreport.EccReportSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeCaseData;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reports.hearingstojudgments.HearingsToJudgmentsSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportCaseData;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysCaseData;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BFHelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CaseDataBuilder;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted.CasesCompletedReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport.EccReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeCaseDataBuilder;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeSearchResult;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_VENUES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_AWAITING_JUDGMENT_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_COMPLETED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_BY_HEARING_VENUE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_NO_CONCILIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_OPEN_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_STANDARD_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_BY_HEARING_TYPE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_TO_JUDGEMENTS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_ETCL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_PRESS_LIST;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_PUBLIC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_STAFF;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PRIVATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_OUTCOME_SUCCESSFUL_AT_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIVE_CASELOAD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFCTC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.POSITION_TYPE_CASE_CLOSED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SESSION_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_JUSTICE_CENTRE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_MAGS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper.CAUSE_LIST_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.ECC_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.NO_CHANGE_IN_CURRENT_POSITION_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.RESPONDENTS_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class ListingServiceTest {

    @InjectMocks
    private ListingService listingService;
    @Mock
    private TornadoService tornadoService;
    @Mock
    private UserService userService;
    @Mock
    private CcdClient ccdClient;
    @Spy
    private CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
    private CaseDetails caseDetails;
    private ListingDetails listingDetails;
    private ListingDetails listingDetailsRange;
    private DocumentInfo documentInfo;
    private List<SubmitEvent> submitEvents;
    private UserDetails userDetails;

    @Before
    public void setUp() {
        documentInfo = new DocumentInfo();
        caseDetails = new CaseDetails();
        listingDetails = new ListingDetails();
        ListingData listingData = new ListingData();
        listingData.setListingDate("2019-12-12");
        listingData.setListingVenue("Aberdeen");
        listingData.setVenueAberdeen("AberdeenVenue");
        listingData.setListingCollection(new ArrayList<>());
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setReportType(BROUGHT_FORWARD_REPORT);
        listingDetails.setCaseData(listingData);
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.setJurisdiction("EMPLOYMENT");

        listingDetailsRange = new ListingDetails();
        ListingData listingData1 = new ListingData();
        listingData1.setListingDateFrom("2019-12-09");
        listingData1.setListingDateTo("2019-12-12");
        listingData1.setListingVenue("Aberdeen");
        listingData1.setVenueAberdeen("AberdeenVenue");
        listingData1.setListingCollection(new ArrayList<>());
        listingData1.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingData1.setReportType("Brought Forward Report");
        listingData1.setClerkResponsible("Steve Jones");
        listingDetailsRange.setCaseData(listingData1);
        listingDetailsRange.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetailsRange.setJurisdiction("EMPLOYMENT");

        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType.setHearingClerk("Clerk");
        dateListedType.setHearingRoomGlasgow("Tribunal 4");
        dateListedType.setHearingAberdeen("AberdeenVenue");
        dateListedType.setHearingVenueDay("Aberdeen");
        dateListedType.setListedDate("2019-12-12T12:11:00.000");
        dateListedType.setHearingTimingStart("2019-12-12T12:11:00.000");
        dateListedType.setHearingTimingBreak("2019-12-12T12:11:00.000");
        dateListedType.setHearingTimingResume("2019-12-12T12:11:00.000");
        dateListedType.setHearingTimingFinish("2019-12-12T12:11:00.000");
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);

        DateListedType dateListedType1 = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType1.setHearingClerk("Clerk");
        dateListedType1.setHearingRoomGlasgow("Tribunal 4");
        dateListedType1.setHearingAberdeen("AberdeenVenue");
        dateListedType1.setHearingVenueDay("Aberdeen");
        dateListedType1.setListedDate("2019-12-10T12:11:00.000");
        dateListedType1.setHearingTimingStart("2019-12-10T11:00:00.000");
        dateListedType1.setHearingTimingBreak("2019-12-10T12:00:00.000");
        dateListedType1.setHearingTimingResume("2019-12-10T13:00:00.000");
        dateListedType1.setHearingTimingFinish("2019-12-10T14:00:00.000");
        DateListedTypeItem dateListedTypeItem1 = new DateListedTypeItem();
        dateListedTypeItem1.setId("124");
        dateListedTypeItem1.setValue(dateListedType1);

        DateListedType dateListedType2 = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType2.setHearingClerk("Clerk1");
        dateListedType2.setHearingCaseDisposed(YES);
        dateListedType2.setHearingRoomGlasgow("Tribunal 5");
        dateListedType2.setHearingAberdeen("AberdeenVenue2");
        dateListedType2.setHearingVenueDay("Aberdeen");
        dateListedType2.setListedDate("2019-12-12T12:11:30.000");
        DateListedTypeItem dateListedTypeItem2 = new DateListedTypeItem();
        dateListedTypeItem2.setId("124");
        dateListedTypeItem2.setValue(dateListedType2);

        DateListedType dateListedType3 = new DateListedType();
        dateListedType3.setHearingStatus(HEARING_STATUS_HEARD);
        dateListedType3.setHearingClerk("Clerk3");
        dateListedType3.setHearingCaseDisposed(YES);
        dateListedType3.setHearingRoomGlasgow("Tribunal 5");
        dateListedType3.setHearingAberdeen("AberdeenVenue2");
        dateListedType3.setHearingVenueDay("Aberdeen");
        dateListedType3.setListedDate("2019-12-12T12:11:55.000");
        dateListedType3.setHearingTimingStart("2019-12-12T14:11:55.000");
        dateListedType3.setHearingTimingBreak("2019-12-12T15:11:55.000");
        dateListedType3.setHearingTimingResume("2019-12-12T15:30:55.000");
        dateListedType3.setHearingTimingFinish("2019-12-12T16:30:55.000");
        DateListedTypeItem dateListedTypeItem3 = new DateListedTypeItem();
        dateListedTypeItem3.setId("124");
        dateListedTypeItem3.setValue(dateListedType3);

        HearingType hearingType = new HearingType();
        hearingType.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem,
                dateListedTypeItem1, dateListedTypeItem2)));
        hearingType.setHearingVenue("Aberdeen");
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        hearingType.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setId("12345");
        hearingTypeItem.setValue(hearingType);

        BFActionType bfActionType = new BFActionType();
        bfActionType.setBfDate("2019-12-10");
        bfActionType.setCleared("020-12-30");
        bfActionType.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
        bfActionTypeItem.setId("0000");
        bfActionTypeItem.setValue(bfActionType);
        HearingTypeItem hearingTypeItem1 = new HearingTypeItem();
        HearingType hearingType1 = new HearingType();
        hearingType1.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem3)));
        hearingType1.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingTypeItem1.setId("12345");
        hearingTypeItem1.setValue(hearingType1);

        BFActionType bfActionType1 = new BFActionType();
        bfActionType1.setBfDate("2019-12-11");
        bfActionType1.setCleared("");
        bfActionType1.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        BFActionTypeItem bfActionTypeItem1 = new BFActionTypeItem();
        bfActionTypeItem1.setId("111");
        bfActionTypeItem1.setValue(bfActionType1);

        BFActionType bfActionType2 = new BFActionType();
        bfActionType2.setBfDate("2019-12-12");
        bfActionType2.setCleared("");
        bfActionType2.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        BFActionTypeItem bfActionTypeItem2 = new BFActionTypeItem();
        bfActionTypeItem2.setId("222");
        bfActionTypeItem2.setValue(bfActionType2);

        BFActionType bfActionType3 = new BFActionType();
        bfActionType3.setBfDate("2019-12-13");
        bfActionType3.setCleared("");
        bfActionType3.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        BFActionTypeItem bfActionTypeItem3 = new BFActionTypeItem();
        bfActionTypeItem3.setId("333");
        bfActionTypeItem3.setValue(bfActionType3);

        BFActionType bfActionType4 = new BFActionType();
        bfActionType4.setBfDate("2019-12-10");
        bfActionType4.setCleared("020-12-30");
        bfActionType4.setNotes("Test0");
        BFActionTypeItem bfActionTypeItem4 = new BFActionTypeItem();
        bfActionTypeItem4.setId("0000");
        bfActionTypeItem4.setValue(bfActionType4);

        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("ABC");
        jurCodesType.setJudgmentOutcome(JURISDICTION_OUTCOME_SUCCESSFUL_AT_HEARING);
        jurCodesTypeItem.setId("000");
        jurCodesTypeItem.setValue(jurCodesType);

        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseId(1);
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("4210000/2019");
        caseData.setHearingCollection(new ArrayList<>(Collections.singleton(hearingTypeItem)));
        caseData.setBfActions(new ArrayList<>(Arrays.asList(bfActionTypeItem,
                bfActionTypeItem1, bfActionTypeItem2, bfActionTypeItem3, bfActionTypeItem4)));
        caseData.setHearingCollection(new ArrayList<>(Arrays.asList(hearingTypeItem, hearingTypeItem1)));
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singleton(jurCodesTypeItem)));
        caseData.setClerkResponsible("Steve Jones");
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setDateAccepted("2019-12-12");
        caseData.setPreAcceptCase(casePreAcceptType);
        caseData.setEcmCaseType(SINGLE_CASE_TYPE);
        caseData.setPositionType("Awaiting ET3");
        caseData.setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
        submitEvent1.setCaseData(caseData);
        submitEvent1.setState(CLOSED_STATE);
        submitEvents = new ArrayList<>(Collections.singleton(submitEvent1));

        caseData.setPrintHearingDetails(listingData);
        caseData.setPrintHearingCollection(listingData);
        Address address = new Address();
        address.setAddressLine1("Manchester Avenue");
        address.setPostTown("Manchester");
        caseData.setTribunalCorrespondenceAddress(address);
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        caseDetails.setJurisdiction("EMPLOYMENT");
        userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
    }

    @Test
    public void listingCaseCreationWithHearingDocType() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null,"
                + " tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, "
                + "tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=AberdeenVenue, venueDundee=null,"
                + " venueEdinburgh=null, hearingDocType=ETL Test, hearingDocETCL=null, roomOrNoRoom=null,"
                + " docMarkUp=null, bfDateCollection=null, clerkResponsible=null, "
                + "reportType=Brought Forward Report, documentName=ETL Test, showAll=null, "
                + "localReportsSummaryHdr=null, "
                + "localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetails.getCaseData().setHearingDocType("ETL Test");
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
        listingDetails.getCaseData().setHearingDocType(null);
    }

    @Test
    public void listingCaseCreationWithReportType() {
        String result = "ListingData(tribunalCorrespondenceAddress=null,"
                + " tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, "
                + "tribunalCorrespondenceEmail=null,"
                + " reportDate=null, hearingDateType=Single," + " listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], "
                + "listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=AberdeenVenue, "
                + "venueDundee=null, "
                + "venueEdinburgh=null, " + "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, "
                + "docMarkUp=null," + " bfDateCollection=null, clerkResponsible=null, "
                + "reportType=Brought Forward Report,"
                + " documentName=Brought Forward Report, showAll=null, localReportsSummaryHdr=null,"
                + " localReportsSummary=null, " + "localReportsSummaryHdr2=null, localReportsSummary2=null, "
                + "localReportsDetailHdr=null, localReportsDetail=null)";
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
    }

    @Test
    public void listingCaseCreationWithoutDocumentName() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null,"
                + " tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, "
                + "tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], "
                + "listingVenueOfficeGlas=null," + " listingVenueOfficeAber=null, " + "venueGlasgow=null,"
                + " venueAberdeen=AberdeenVenue, " + "venueDundee=null, "
                + "venueEdinburgh=null, " + "hearingDocType=null, hearingDocETCL=null, "
                + "roomOrNoRoom=null, docMarkUp=null," + " bfDateCollection=null, clerkResponsible=null, "
                + "reportType=null, documentName=Missing document name, "
                + "showAll=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetails.getCaseData().setReportType(null);
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
        listingDetails.getCaseData().setReportType("Brought Forward Report");
    }

    @Test
    public void processListingHearingsRequestAberdeen() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, "
                + "tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, "
                + "tribunalCorrespondenceEmail=null," + " reportDate=null, hearingDateType=Single, "
                + "listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123,"
                + " value=ListingType(causeListDate=12 December 2019, " + "causeListTime=12:11,"
                + " causeListVenue=AberdeenVenue, "
                + "elmoCaseReference=4210000/2019, " + "jurisdictionCodesList=ABC, hearingType=Preliminary Hearing,"
                + " positionType=Awaiting ET3, " + "hearingJudgeName= , hearingEEMember= , hearingERMember= , "
                + "hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , "
                + "claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , "
                + "estHearingLength=2 hours, hearingPanel= , " + "hearingRoom=Tribunal 4, respondentOthers= , "
                + "hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= ))], "
                + "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, "
                + "venueAberdeen=null, venueDundee=null, venueEdinburgh=null, "
                + "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, "
                + "bfDateCollection=null, clerkResponsible=null, "
                + "reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, "
                + "localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestGlasgow() throws IOException {
        listingDetails.getCaseData().setVenueAberdeen(null);
        listingDetails.getCaseData().setVenueGlasgow("GlasgowVenue");
        listingDetails.getCaseData().setListingVenue("Glasgow");
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null,"
                + " tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null,"
                + " reportDate=null, " + "hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Glasgow, listingCollection=[], listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=null, venueDundee=null, "
                + "venueEdinburgh=null, " + "hearingDocType=null, hearingDocETCL=null, "
                + "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, "
                + "reportType=Brought Forward Report, documentName=null, showAll=null, "
                + "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, " + "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestEdinburgh() throws IOException {
        listingDetails.getCaseData().setVenueAberdeen(null);
        listingDetails.getCaseData().setVenueEdinburgh("EdinburghVenue");
        listingDetails.getCaseData().setListingVenue("Edinburgh");
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, " +
                "tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null," +
                " reportDate=null, hearingDateType=Single, " + "listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Edinburgh, listingCollection=[], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=null, venueDundee=null, " +
                "venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, " +
                "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, showAll=null, " +
                "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, " + "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestDundee() throws IOException {
        listingDetails.getCaseData().setVenueAberdeen(null);
        listingDetails.getCaseData().setVenueDundee("DundeeVenue");
        listingDetails.getCaseData().setListingVenue("Dundee");
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single," + " listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Dundee, listingCollection=[], listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=null, venueDundee=null, "
                + "venueEdinburgh=null, hearingDocType=null, " + "hearingDocETCL=null, "
                + "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report,"
                + " documentName=null, showAll=null, " + "localReportsSummaryHdr=null, localReportsSummary=null, "
                + "localReportsSummaryHdr2=null, localReportsSummary2=null, " + "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequest_SetCauseListVenueForNewcastleCft_NonNullHearingVenueName() throws IOException {
        String expectedHearingVenueName = NEWCASTLE_CFCTC;
        String expectedHearingVenue = NEWCASTLE_CFT;

        setNewcastleTestCaseDetails(NEWCASTLE_CFT, expectedHearingVenueName);

        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString())).thenReturn(submitEvents);
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        String actualHearingVenueNameForNewcastleCFT = caseData.getHearingCollection().get(0).getValue()
            .getHearingDateCollection().get(0).getValue().getHearingVenueNameForNewcastleCFT();
        String actualHearingVenue = caseData.getPrintHearingCollection().getListingVenue();
        assertNotNull(actualHearingVenueNameForNewcastleCFT);
        assertEquals(expectedHearingVenueName, actualHearingVenueNameForNewcastleCFT);
        assertEquals(expectedHearingVenue, actualHearingVenue);
    }

    @Test
    public void processListingHearingsRequest_SetCauseListVenueForNewcastleCft_NullHearingVenueName() throws IOException {
        String expectedHearingVenueName = null;
        String expectedHearingVenue = "Kings Court";

        setNewcastleTestCaseDetails(expectedHearingVenue, expectedHearingVenueName);

        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString())).thenReturn(submitEvents);
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        String actualHearingVenueNameForNewcastleCFT = caseData.getHearingCollection().get(0).getValue()
            .getHearingDateCollection().get(0).getValue().getHearingVenueNameForNewcastleCFT();
        String actualHearingVenue = caseData.getPrintHearingCollection().getListingVenue();
        assertNull(actualHearingVenueNameForNewcastleCFT);
        assertEquals(expectedHearingVenue, actualHearingVenue);
    }

    @Test
    public void processListingHearingsRequest_SetCauseListVenueForTeessideMags_NonNullHearingVenueName() throws IOException {
        String expectedHearingVenueName = TEESSIDE_JUSTICE_CENTRE;
        String expectedHearingVenue = TEESSIDE_MAGS;

        setNewcastleTestCaseDetails(expectedHearingVenue, expectedHearingVenueName);

        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString())).thenReturn(submitEvents);
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        String actualHearingVenueName = caseData.getHearingCollection().get(0).getValue()
            .getHearingDateCollection().get(0).getValue().getHearingVenueNameForTeessideMags();
        String actualHearingVenue = caseData.getPrintHearingCollection().getListingVenue();
        assertNotNull(actualHearingVenueName);
        assertEquals(expectedHearingVenueName, actualHearingVenueName);
        assertEquals(expectedHearingVenue, actualHearingVenue);
    }

    @Test
    public void processListingHearingsRequest_SetCauseListVenueForTeessideMags__NullHearingVenueName() throws IOException {
        String expectedHearingVenueName = null;
        String expectedHearingVenue = TEESSIDE_MAGS;
        setNewcastleTestCaseDetails(expectedHearingVenue, expectedHearingVenueName);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString())).thenReturn(submitEvents);
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        String actualHearingVenueNameForTeessideMags = caseData.getHearingCollection().get(0).getValue()
            .getHearingDateCollection().get(0).getValue().getHearingVenueNameForTeessideMags();
        String actualHearingVenue = caseData.getPrintHearingCollection().getListingVenue();
        assertNull(actualHearingVenueNameForTeessideMags);
        assertEquals(expectedHearingVenue, actualHearingVenue);
    }

    @Test
    public void processListingHearingsRequest_SetCauseListVenue_NonNewcastle() throws IOException {
        String expectedHearingVenueName = "Harrowgate CJC";
        String expectedHearingVenue = "Leeds";
        setNewcastleTestCaseDetails(expectedHearingVenue, expectedHearingVenueName);

        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString())).thenReturn(submitEvents);
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        String actualHearingVenueNameForNewcastleCFT = caseData.getHearingCollection().get(0).getValue()
            .getHearingDateCollection().get(0).getValue().getHearingVenueNameForNewcastleCFT();
        String actualHearingVenueNameForTeessideMags = caseData.getHearingCollection().get(0).getValue()
            .getHearingDateCollection().get(0).getValue().getHearingVenueNameForTeessideMags();
        String actualHearingVenue = caseData.getPrintHearingCollection().getListingVenue();
        assertNull(actualHearingVenueNameForNewcastleCFT);
        assertNull(actualHearingVenueNameForTeessideMags);
        assertEquals(expectedHearingVenue, actualHearingVenue);
    }

    private void setNewcastleTestCaseDetails(String hearingVenue, String hearingVenueName) {
        listingDetails.getCaseData().setListingVenue(hearingVenue);
        listingDetails.getCaseData().setListingDateFrom("2022-12-1");
        listingDetails.getCaseData().setListingDateTo("2022-12-28");
        listingDetails.getCaseData().setListingDate("2022-12-15");

        submitEvents.get(0).getCaseData().getHearingCollection()
            .subList(0, submitEvents.get(0).getCaseData().getHearingCollection().size()).clear();
        HearingType hearingTypeOne = new HearingType();
        hearingTypeOne.setHearingVenue(hearingVenue);
        hearingTypeOne.setHearingType("Hearing");
        hearingTypeOne.setHearingFormat(List.of("In person"));
        hearingTypeOne.setHearingNumber("1223");
        hearingTypeOne.setHearingSitAlone("Full Panel");
        hearingTypeOne.setHearingEstLengthNum("2");
        hearingTypeOne.setHearingEstLengthNumType("Hours");
        DateListedTypeItem dateListedTypeItemOne = new DateListedTypeItem();
        dateListedTypeItemOne.setId("c409336e-8bf3-405f-b29d-074c59196c8d");

        DateListedType dateListedTypeOne = new DateListedType();
        dateListedTypeOne.setListedDate("2022-12-15T11:00:00.000");
        dateListedTypeOne.setHearingStatus("Listed");
        dateListedTypeOne.setHearingVenueDay(hearingVenue);

        if (hearingVenue.equals(TEESSIDE_MAGS)) {
            dateListedTypeOne.setHearingVenueDay(hearingVenue);
            dateListedTypeOne.setHearingVenueNameForTeessideMags(hearingVenueName);
            listingDetails.getCaseData().setVenueAberdeen(null);
        }

        if (hearingVenue.equals(NEWCASTLE_CFT)) {
            dateListedTypeOne.setHearingVenueDay(hearingVenue);
            dateListedTypeOne.setHearingVenueNameForNewcastleCFT(hearingVenueName);
            listingDetails.getCaseData().setVenueAberdeen(null);
        }

        dateListedTypeOne.setHearingTimingStart("2022-12-15T11:00:00.000");
        dateListedTypeOne.setHearingTimingFinish("2022-12-15T11:00:00.000");
        dateListedTypeItemOne.setValue(dateListedTypeOne);

        hearingTypeOne.setHearingDateCollection(List.of(dateListedTypeItemOne));

        HearingTypeItem hearingTypeItemOne = new HearingTypeItem();
        hearingTypeItemOne.setId("3912807b-e862-43f2-8436-0eeeccb74220");
        hearingTypeItemOne.setValue(hearingTypeOne);
        List<HearingTypeItem> hearingTypeItems = new ArrayList<>();
        hearingTypeItems.add(hearingTypeItemOne);
        submitEvents.get(0).getCaseData().setHearingCollection(hearingTypeItems);
    }

    @Test
    public void processListingHearingsRequestNonScottish() throws IOException {
        listingDetails.getCaseData().setVenueAberdeen(null);
        listingDetails.getCaseData().setListingVenue("Leeds");
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, " +
                "tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, " +
                "reportDate=null," + " hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Leeds, listingCollection=[], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=null, venueDundee=null, " +
                "venueEdinburgh=null," + " hearingDocType=null, hearingDocETCL=null, " +
                "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, showAll=null, " +
                "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null," +
                " localReportsSummary2=null, " + "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithValidHearingType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, " +
                "tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, " +
                "reportDate=null, hearingDateType=Single, " + "listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, " +
                "value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, " +
                "jurisdictionCodesList=ABC, hearingType=Valid Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , " +
                "hearingClerk=Clerk, hearingDay=1 of 3, " + "claimantName=RYAN AIR LTD, claimantTown= , " +
                "claimantRepresentative= , respondent= , respondentTown= , " +
                "respondentRepresentative= , " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= , " +
                "judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= ))], " +
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, " +
                "venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=ETCL - Cause List, hearingDocETCL=Public, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, " + "clerkResponsible=null, reportType=Brought Forward Report, " +
                "documentName=null, showAll=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingType("Valid Hearing");
        listingDetails.getCaseData().setHearingDocType(HEARING_DOC_ETCL);
        listingDetails.getCaseData().setHearingDocETCL(HEARING_ETCL_PUBLIC);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithInValidHearingType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, "
                + "hearingDocType=ETCL - Cause List, hearingDocETCL=Public, roomOrNoRoom=null, docMarkUp=null, "
                + "bfDateCollection=null, clerkResponsible=null, "
                + "reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, "
                + "localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingType(HEARING_TYPE_JUDICIAL_MEDIATION);
        listingDetails.getCaseData().setHearingDocType(HEARING_DOC_ETCL);
        listingDetails.getCaseData().setHearingDocETCL(HEARING_ETCL_PUBLIC);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithPrivateHearingType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null,"
                + " tribunalCorrespondenceFax=null, " + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, " + "venueGlasgow=null, venueAberdeen=null, venueDundee=null, "
                + "venueEdinburgh=null, " + "hearingDocType=ETCL - Cause List, hearingDocETCL=Press List, "
                + "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, "
                + "reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, "
                + "localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingType(
                HEARING_TYPE_PERLIMINARY_HEARING);
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingPublicPrivate(
                HEARING_TYPE_PRIVATE);
        listingDetails.getCaseData().setHearingDocType(HEARING_DOC_ETCL);
        listingDetails.getCaseData().setHearingDocETCL(HEARING_ETCL_PRESS_LIST);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithALL() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, "
                + "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Single,"
                + " listingDate=2019-12-12, listingDateFrom=null, "
                + "listingDateTo=null, listingVenue=Aberdeen, listingCollection=["
                + "ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11,"
                + " causeListVenue=AberdeenVenue, " + "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, "
                + "hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , "
                + "hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , "
                + "claimantRepresentative= , " + "respondent= , respondentTown= , respondentRepresentative= , "
                + "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, "
                + "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , "
                + "hearingReadingDeliberationMembersChambers= )), " + "ListingTypeItem(id=124, "
                + "value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue2, "
                + "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, "
                + "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , "
                + "hearingERMember= , hearingClerk=Clerk1, hearingDay=3 of 3, claimantName=RYAN AIR LTD, "
                + "claimantTown= , claimantRepresentative= , "
                + "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, "
                + "hearingPanel= , hearingRoom=Tribunal 5, "
                + "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , "
                + "hearingReadingDeliberationMembersChambers= )), "
                + "ListingTypeItem(id=124, value=ListingType(causeListDate=12 December 2019, "
                + "causeListTime=12:11, "
                + "causeListVenue=AberdeenVenue2, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, "
                + "hearingType=Preliminary Hearing, positionType=Awaiting ET3, "
                + "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk3, "
                + "hearingDay=1 of 1, claimantName=RYAN AIR LTD, claimantTown= , "
                + "claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , "
                +  "estHearingLength=null null, hearingPanel= , hearingRoom=Tribunal 5, "
                + "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , "
                + "hearingReadingDeliberationMembersChambers= ))], " + "listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, venueGlasgow=null," + " venueAberdeen=null, venueDundee=null, "
                + "venueEdinburgh=null, " + "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, "
                + "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null,"
                + " showAll=null, localReportsSummaryHdr=null, " + "localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        listingDetails.getCaseData().setVenueAberdeen(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestDateRange() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Range, listingDate=null, listingDateFrom=2019-12-09, " +
                "listingDateTo=2019-12-12, listingVenue=Aberdeen, listingCollection=" +
                "[ListingTypeItem(id=124, value=ListingType(causeListDate=10 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=2 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= )), " +
                "ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= ))], " +
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch("authToken", "Manchester", listingDetailsRange.getCaseData().getListingDateFrom(),
                listingDetailsRange.getCaseData().getListingDateTo(), "AberdeenVenue",
                "data.hearingCollection.value.hearingDateCollection.value.Hearing_Aberdeen.keyword")).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestSingleDate() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[" +
                "ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= )), " +
                "ListingTypeItem(id=124, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue2, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk1, hearingDay=3 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 5, " +
                "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= )), ListingTypeItem(id=124, " +
                "value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, " +
                "causeListVenue=AberdeenVenue2, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk3, hearingDay=1 of 1, claimantName=RYAN AIR LTD, claimantTown= , " +
                "claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=null null, hearingPanel= , hearingRoom=Tribunal 5, " +
                "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= ))], " +
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        listingDetails.getCaseData().setVenueAberdeen(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch("authToken", "Manchester", listingDetails.getCaseData().getListingDate(),
                listingDetails.getCaseData().getListingDate(), "Aberdeen",
                "data.hearingCollection.value.hearingDateCollection.value.hearingVenueDay.keyword")).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestRangeAndAllVenues() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Range, listingDate=null, " +
                "listingDateFrom=2019-12-09, listingDateTo=2019-12-12, listingVenue=All, listingCollection=[ListingTypeItem(id=124, " +
                "value=ListingType(causeListDate=10 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, j" +
                "urisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=2 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= )), " +
                "ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, " +
                "causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, " +
                "claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , " +
                "hearingReadingDeliberationMembersChambers= )), ListingTypeItem(id=124, " +
                "value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue2, elmoCaseReference=4210000/2019, " +
                "jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk1, hearingDay=3 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 5, " +
                "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= )), ListingTypeItem(id=124, " +
                "value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, " +
                "causeListVenue=AberdeenVenue2, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk3, hearingDay=1 of 1, " +
                "claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , " +
                "estHearingLength=null null, hearingPanel= , hearingRoom=Tribunal 5, respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , " +
                "hearingReadingDeliberationMembersChambers= ))], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, hearingDocType=null, " +
                "hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, " +
                "documentName=null, showAll=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        listingDetailsRange.getCaseData().setListingVenue(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test(expected = Exception.class)
    public void processListingHearings_listedDateNullOrEmpty() throws IOException {
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(1).getValue().setListedDate(null);
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(1).getValue().setListedDate("");
        listingDetailsRange.getCaseData().setListingVenue(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(submitEvents);
        listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
    }

    @Test(expected = Exception.class)
    public void processListingHearings_causeListDateNull() throws IOException {
        listingDetailsRange.getCaseData().setListingVenue(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(submitEvents);
        var listingData = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        var listingCollection = listingData.getListingCollection();
        listingCollection.get(0).getValue().setCauseListDate(null);
        listingCollection.sort(Comparator.comparing(o -> LocalDate.parse(o.getValue().getCauseListDate(),
                CAUSE_LIST_DATE_TIME_PATTERN)));
    }

    @Test
    public void processListingHearings_SameDayAndTimeDifferentMonth() throws IOException {
        var result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, " +
                "tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, " +
                "reportDate=null, hearingDateType=Range, listingDate=null, listingDateFrom=2021-01-01, " +
                "listingDateTo=2021-12-01, listingVenue=All, listingCollection=[ListingTypeItem(id=124, " +
                "value=ListingType(causeListDate=01 January 2021, causeListTime=12:00, causeListVenue=AberdeenVenue," +
                " elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , " +
                "hearingClerk=Clerk, hearingDay=2 of 3, claimantName= , claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, " +
                "hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= , judicialMediation= ," +
                " hearingFormat= , hearingReadingDeliberationMembersChambers= )), ListingTypeItem(id=123, " +
                "value=ListingType(causeListDate=01 December 2021, causeListTime=12:00, causeListVenue=AberdeenVenue," +
                " elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing," +
                " positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , " +
                "hearingClerk=Clerk, hearingDay=1 of 3, claimantName= , claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, " +
                "hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= , judicialMediation= , " +
                "hearingFormat= , hearingReadingDeliberationMembersChambers= ))], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, " +
                "venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, " +
                "showAll=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";

        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().setListedDate("2021-12-01T12:00:00.000");
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(1).getValue().setListedDate("2021-01-01T12:00:00.000");
        listingDetailsRange.getCaseData().setListingVenue(ALL_VENUES);
        listingDetailsRange.getCaseData().setListingDateFrom("2021-01-01");
        listingDetailsRange.getCaseData().setListingDateTo("2021-12-01");

        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(submitEvents);
        var listingDataResult = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test(expected = Exception.class)
    public void processListingHearingsRequestWithException() throws IOException {
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        listingService.processListingHearingsRequest(listingDetails, "authToken");
    }

    @Test
    public void processHearingDocument() throws IOException {
        listingDetails.getCaseData().setReportType(CLAIMS_ACCEPTED_REPORT);
        when(tornadoService.listingGeneration(anyString(), ArgumentMatchers.any(), anyString())).thenReturn(documentInfo);
        DocumentInfo documentInfo1 = listingService.processHearingDocument(listingDetails.getCaseData(), listingDetails.getCaseTypeId(), "authToken");
        assertEquals(documentInfo, documentInfo1);
    }

    @Test(expected = Exception.class)
    public void processHearingDocumentWithException() throws IOException {
        when(tornadoService.listingGeneration(anyString(), ArgumentMatchers.any(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        listingService.processHearingDocument(listingDetails.getCaseData(), listingDetails.getCaseTypeId(), "authToken");
    }

    @Test
    public void processListingHearingsRequestWithAdditionalInfo() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=Juan Pedro, " +
                "claimantTown=Aberdeen, claimantRepresentative=ONG, respondent=Royal McDonal, respondentTown=Aberdeen, respondentRepresentative=ITV, " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers=Royal McDonal, hearingNotes= , judicialMediation= , hearingFormat= , " +
                "hearingReadingDeliberationMembersChambers= ))], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        ClaimantType claimantType = new ClaimantType();
        Address address = new Address();
        address.setPostTown("Aberdeen");
        claimantType.setClaimantAddressUK(address);
        submitEvents.get(0).getCaseData().setClaimantType(claimantType);
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Juan Pedro");
        submitEvents.get(0).getCaseData().setClaimantIndType(claimantIndType);
        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        representedTypeC.setNameOfOrganisation("ONG");
        submitEvents.get(0).getCaseData().setRepresentativeClaimantType(representedTypeC);
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentAddress(address);
        respondentSumType.setRespondentName("Royal McDonal");
        respondentSumType.setResponseStruckOut(NO);
        respondentSumTypeItem.setId("111");
        respondentSumTypeItem.setValue(respondentSumType);
        RespondentSumTypeItem respondentSumTypeItem1 = new RespondentSumTypeItem();
        RespondentSumType respondentSumType1 = new RespondentSumType();
        respondentSumType1.setRespondentAddress(address);
        respondentSumType1.setRespondentName("Burger King");
        respondentSumTypeItem1.setId("112");
        respondentSumTypeItem1.setValue(respondentSumType);
        submitEvents.get(0).getCaseData().setRespondentCollection(new ArrayList<>(Arrays.asList(respondentSumTypeItem, respondentSumTypeItem1)));
        RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
        RepresentedTypeR representedTypeR = new RepresentedTypeR();
        representedTypeR.setNameOfOrganisation("ITV");
        representedTypeRItem.setId("222");
        representedTypeRItem.setValue(representedTypeR);
        submitEvents.get(0).getCaseData().setRepCollection(new ArrayList<>(Collections.singleton(representedTypeRItem)));
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingSingleCasesRequest() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName= , " +
                "claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, " +
                "hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= ))], " +
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        caseDetails.getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection().get(2).getValue().setHearingStatus("Settled");
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        assertEquals(result, caseData.getPrintHearingDetails().toString());
        caseDetails.getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection().get(2).getValue().setHearingStatus(null);
    }

    @Test
    public void processListingSingleCasesRequestNotShowAll() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, listingDateTo=null, "
                + "listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 "
                + "December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, "
                + "jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, "
                + "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, "
                + "claimantName= , claimantTown= , claimantRepresentative= , respondent= , respondentTown= , "
                + "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, "
                + "respondentOthers= , hearingNotes= , judicialMediation= , hearingFormat= , hearingReadingDeliberationMembersChambers= ))], "
                + "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, "
                + "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, "
                + "hearingDocType=ETCL - Cause List, hearingDocETCL=Staff, roomOrNoRoom=null, docMarkUp=null, "
                + "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, "
                + "showAll=No, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        caseDetails.getCaseData().getPrintHearingDetails().setShowAll(NO);
        caseDetails.getCaseData().getPrintHearingDetails().setHearingDocType(HEARING_DOC_ETCL);
        caseDetails.getCaseData().getPrintHearingDetails().setHearingDocETCL(HEARING_ETCL_STAFF);
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        assertEquals(result, caseData.getPrintHearingDetails().toString());
    }

    @Test
    public void setCourtAddressFromCaseData() {
        String result = "ListingData(tribunalCorrespondenceAddress=Manchester Avenue, Manchester, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=AberdeenVenue, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, showAll=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        ListingData listingData = listingService.setCourtAddressFromCaseData(caseDetails.getCaseData());
        assertEquals(result, listingData.toString());
    }

    @Test
    public void generateClaimsAcceptedReportDataForEngland() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Claims Accepted, documentName=null, showAll=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=Manchester, receiptDate=null, hearingDate=null, date=null, " +
                "full=null, half=null, mins=null, total=1, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, " +
                "hearingNumber=null, hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=null, hearingSitAlone=null, " +
                "hearingJudge=null, judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, " +
                "fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=null, " +
                "casesCompletedHearing=null, sessionType=null, sessionDays=null, sessionDaysTotal=null, sessionDaysTotalDetail=null, completedPerSession=null, " +
                "completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, " +
                "otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, " +
                "ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, ConFastSessionDays=null, ConFastCompletedPerSession=null, " +
                "ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, " +
                "ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, " +
                "Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, " +
                "hearingCM=null, costs=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, " +
                "hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, " +
                "singlesTotal=1, multiplesTotal=0, dateOfAcceptance=null, respondentET3=null, respondentET4=null, listingHistory=null, ConNoneTotal=null, ConStdTotal=null, " +
                "ConFastTotal=null, ConOpenTotal=null, ConNone26wkTotal=null, ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, ConNone26wkTotalPerCent=null, " +
                "ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, ConOpen26wkTotalPerCent=null, xConNone26wkTotal=null, xConStd26wkTotal=null, xConFast26wkTotal=null, xConOpen26wkTotal=null, " +
                "xConNone26wkTotalPerCent=null, xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
                + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, "
                + "et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, "
                + "manuallyCreatedTotalCasesPercent=null, et1OnlineTotalCasesPercent=null, "
                + "eccTotalCasesPercent=null, migratedTotalCasesPercent=null"
                + "), "
                + "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, " +
                "hearingDate=null, date=null, full=null, half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=4210000/2019, " +
                "multipleRef=null, multSub=null, hearingNumber=null, hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, " +
                "clerk=Steve Jones, hearingSitAlone=null, hearingJudge=null, judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, " +
                "fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, " +
                "casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, sessionDays=null, sessionDaysTotal=null, " +
                "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, " +
                "ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, " +
                "conciliationTrack=null, conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, " +
                "ConFastCasesCompletedHearing=null, ConFastSessionDays=null, ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, " +
                "ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, " +
                "Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, " +
                "Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, " +
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, costs=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, " +
                "hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, " +
                "leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=Single, singlesTotal=null, multiplesTotal=null, " +
                "dateOfAcceptance=2019-12-12, respondentET3=null, respondentET4=null, listingHistory=null, ConNoneTotal=null, ConStdTotal=null, ConFastTotal=null, " +
                "ConOpenTotal=null, ConNone26wkTotal=null, ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, ConNone26wkTotalPerCent=null, " +
                "ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, ConOpen26wkTotalPerCent=null, xConNone26wkTotal=null, xConStd26wkTotal=null, xConFast26wkTotal=null, " +
                "xConOpen26wkTotal=null, xConNone26wkTotalPerCent=null, xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, "
                + "xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
                + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, " +
                "et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, " +
                "manuallyCreatedTotalCasesPercent=null, et1OnlineTotalCasesPercent=null, " +
                "eccTotalCasesPercent=null, migratedTotalCasesPercent=null"
                +"))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CLAIMS_ACCEPTED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateClaimsAcceptedReportDataForGlasgow() throws IOException {
         String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Claims Accepted, documentName=null, showAll=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=Scotland, receiptDate=null, hearingDate=null, date=null, " +
                "full=null, half=null, mins=null, total=1, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, " +
                "hearingNumber=null, hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=null, hearingSitAlone=null, " +
                "hearingJudge=null, judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=null, casesCompletedHearing=null, " +
                "sessionType=null, sessionDays=null, sessionDaysTotal=null, sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, " +
                "ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, " +
                "otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, " +
                "ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, ConFastSessionDays=null, ConFastCompletedPerSession=null, " +
                "ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, " +
                "ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, " +
                "Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, " +
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, costs=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, " +
                "hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, " +
                "et3ReceivedDate=null, judicialMediation=null, caseType=null, singlesTotal=1, multiplesTotal=0, dateOfAcceptance=null, respondentET3=null, " +
                "respondentET4=null, listingHistory=null, ConNoneTotal=null, ConStdTotal=null, ConFastTotal=null, ConOpenTotal=null, ConNone26wkTotal=null, " +
                "ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, ConNone26wkTotalPerCent=null, ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, " +
                "ConOpen26wkTotalPerCent=null, xConNone26wkTotal=null, xConStd26wkTotal=null, xConFast26wkTotal=null, xConOpen26wkTotal=null, xConNone26wkTotalPerCent=null, " +
                "xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
                 + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                 + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                 + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                 + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                 + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, " +
                 "et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, " +
                 "manuallyCreatedTotalCasesPercent=null, et1OnlineTotalCasesPercent=null," +
                 " eccTotalCasesPercent=null, migratedTotalCasesPercent=null"
                 + "), " +
                "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, " +
                "hearingDate=null, date=null, full=null, half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=4210000/2019, " +
                "multipleRef=null, multSub=null, hearingNumber=null, hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, " +
                "clerk=Steve Jones, hearingSitAlone=null, hearingJudge=null, judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, " +
                "fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, " +
                "casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, sessionDays=null, sessionDaysTotal=null, " +
                "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, " +
                "ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, " +
                "conciliationTrack=null, conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, " +
                "ConFastCasesCompletedHearing=null, ConFastSessionDays=null, ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, " +
                "ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, " +
                "totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, " +
                "Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, " +
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, costs=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, " +
                "hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, " +
                "leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=Single, singlesTotal=null, multiplesTotal=null, " +
                "dateOfAcceptance=2019-12-12, respondentET3=null, respondentET4=null, listingHistory=null, ConNoneTotal=null, ConStdTotal=null, " +
                "ConFastTotal=null, ConOpenTotal=null, ConNone26wkTotal=null, ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, " +
                "ConNone26wkTotalPerCent=null, ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, ConOpen26wkTotalPerCent=null, xConNone26wkTotal=null, xConStd26wkTotal=null, " +
                "xConFast26wkTotal=null, xConOpen26wkTotal=null, xConNone26wkTotalPerCent=null, xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
                 + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                 + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                 + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                 + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                 + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, " +
                 "et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, " +
                 "manuallyCreatedTotalCasesPercent=null, et1OnlineTotalCasesPercent=null, " +
                 "eccTotalCasesPercent=null, migratedTotalCasesPercent=null"
                 +"))])";
        listingDetails.setCaseTypeId(SCOTLAND_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CLAIMS_ACCEPTED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setManagingOffice("Glasgow");
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void checkExistingDataInReport() throws IOException {
        var adhocReportType = new AdhocReportType();
        adhocReportType.setSinglesTotal("6");
        adhocReportType.setMultiplesTotal("10");
        listingDetails.getCaseData().setLocalReportsSummaryHdr(adhocReportType);
        listingDetails.setCaseId("1234567");
        var adhocReportType2 = new AdhocReportType();
        adhocReportType2.setCaseReference("1800001/2021");
        adhocReportType2.setDateOfAcceptance("2021-01-01");
        var adhocReportTypeItem = new AdhocReportTypeItem();
        adhocReportTypeItem.setValue(adhocReportType2);
        List<AdhocReportTypeItem> localReportsSummary = List.of(adhocReportTypeItem);
        listingDetails.getCaseData().setLocalReportsSummary(localReportsSummary);
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CLAIMS_ACCEPTED_REPORT);
        submitEvents.remove(0);
        
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");

        assertTrue(CollectionUtils.isEmpty(listingDataResult.getLocalReportsDetail()));
        assertTrue(CollectionUtils.isEmpty(listingDataResult.getLocalReportsSummary2()));
        assertTrue(CollectionUtils.isEmpty(listingDataResult.getLocalReportsSummary()));
        assertNull(listingDataResult.getLocalReportsDetailHdr());
        assertNull(listingDataResult.getLocalReportsSummaryHdr());
        assertNull(listingDataResult.getLocalReportsSummaryHdr2());

    }

    @Test
    public void generateLiveCaseloadReportDataForEnglandWithValidPositionType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, listingDateTo=null, "
                + "listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, "
                + "venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, "
                + "bfDateCollection=null, clerkResponsible=null, reportType=Live Caseload, documentName=null, "
                + "showAll=null, localReportsSummaryHdr=AdhocReportType(reportDate=null, "
                + "reportOffice=null, receiptDate=null, hearingDate=null, date=null, full=null, half=null, mins=null, "
                + "total=1, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, "
                + "hearingNumber=null, hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, "
                + "clerk=null, hearingSitAlone=null, hearingJudge=null, judgeType=null, judgementDateSent=null, "
                + "position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, "
                + "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, "
                + "casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, "
                + "sessionDays=null, sessionDaysTotal=null, sessionDaysTotalDetail=null, completedPerSession=null, "
                + "completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, "
                + "ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, "
                + "otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=null, "
                + "ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, "
                + "ConFastCasesCompletedHearing=null, ConFastSessionDays=null, ConFastCompletedPerSession=null, "
                + "ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, "
                + "ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, "
                + "totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, "
                + "Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, "
                + "actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, costs=null, "
                + "hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, "
                + "hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, "
                + "subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, "
                + "singlesTotal=1, multiplesTotal=0, dateOfAcceptance=null, respondentET3=null, respondentET4=null, "
                + "listingHistory=null, ConNoneTotal=null, ConStdTotal=null, ConFastTotal=null, ConOpenTotal=null, "
                + "ConNone26wkTotal=null, ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, "
                + "ConNone26wkTotalPerCent=null, "
                + "ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, ConOpen26wkTotalPerCent=null, "
                + "xConNone26wkTotal=null, xConStd26wkTotal=null,"
                + " xConFast26wkTotal=null, xConOpen26wkTotal=null, xConNone26wkTotalPerCent=null, "
                + "xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, "
                + "xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
                + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null,"
                + " et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, "
                + "manuallyCreatedTotalCasesPercent=null, "
                + "et1OnlineTotalCasesPercent=null, eccTotalCasesPercent=null, "
                + "migratedTotalCasesPercent=null"
                + "), localReportsSummary=null, "
                + "localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=AdhocReportType(reportDate=null, "
                + "reportOffice=Manchester, receiptDate=null, hearingDate=null, date=null, full=null, half=null, "
                + "mins=null, total=null, eeMember=null, erMember=null, caseReference=null, multipleRef=null, "
                + "multSub=null, hearingNumber=null, hearingType=null, hearingTelConf=null, hearingDuration=null, "
                + "hearingClerk=null, clerk=null, hearingSitAlone=null, hearingJudge=null, judgeType=null, "
                + "judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, "
                + "fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, "
                + "fileLocationEdinburgh=null, casesCompletedHearingTotal=null, casesCompletedHearing=null, "
                + "sessionType=null, sessionDays=null, sessionDaysTotal=null, sessionDaysTotalDetail=null, "
                + "completedPerSession=null, completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, "
                + "ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, "
                + "otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=null, "
                + "ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, "
                + "ConFastCasesCompletedHearing=null, ConFastSessionDays=null, ConFastCompletedPerSession=null, "
                + "ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, "
                + "ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, "
                + "totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, "
                + "Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, "
                + "actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, costs=null, "
                + "hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, "
                + "hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, "
                + "subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, "
                + "singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, respondentET3=null, "
                + "respondentET4=null, listingHistory=null, ConNoneTotal=null, ConStdTotal=null, ConFastTotal=null, "
                + "ConOpenTotal=null, ConNone26wkTotal=null, ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, "
                + "ConNone26wkTotalPerCent=null, ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, ConOpen26wkTotalPerCent=null,"
                + " xConNone26wkTotal=null, xConStd26wkTotal=null, xConFast26wkTotal=null, xConOpen26wkTotal=null, " +
                "xConNone26wkTotalPerCent=null, "
                + "xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, xConOpen26wkTotalPerCent=null, " +
                "delayedDaysForFirstHearing=null, "
                + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, "
                + "et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, "
                + "manuallyCreatedTotalCasesPercent=null, "
                + "et1OnlineTotalCasesPercent=null, eccTotalCasesPercent=null, migratedTotalCasesPercent=null"
                + "), "
                + "localReportsDetail=[AdhocReportTypeItem(id=null, "
                + "value=AdhocReportType(reportDate=null, reportOffice=Manchester, receiptDate=null, hearingDate=null, "
                + "date=null, full=null, half=null, mins=null, total=null, eeMember=null, erMember=null, "
                + "caseReference=4210000/2019, multipleRef=null, multSub=null, hearingNumber=null, hearingType=null, "
                + "hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=Steve Jones, "
                + "hearingSitAlone=null, hearingJudge=null, judgeType=null, judgementDateSent=null, position=null, "
                + "dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, "
                + "fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=null, "
                + "casesCompletedHearing=null, sessionType=null, sessionDays=null, sessionDaysTotal=null, "
                + "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, "
                + "ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, ptSessionDaysTotal=null, "
                + "ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, "
                + "conciliationTrack=null, conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, "
                + "ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, "
                + "ConFastSessionDays=null, ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, "
                + "ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, "
                + "ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, "
                + "Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, "
                + "Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, bfDate=null, "
                + "bfDateCleared=null, reservedHearing=null, hearingCM=null, costs=null, hearingInterloc=null, hearingPH=null, "
                + "hearingPrelim=null, stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, "
                + "hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, "
                + "et3ReceivedDate=null, judicialMediation=null, caseType=Single, singlesTotal=null, multiplesTotal=null, "
                + "dateOfAcceptance=2019-12-12, respondentET3=null, respondentET4=null, listingHistory=null, "
                + "ConNoneTotal=null, "
                + "ConStdTotal=null, ConFastTotal=null, ConOpenTotal=null, ConNone26wkTotal=null, ConStd26wkTotal=null, "
                + "ConFast26wkTotal=null," + " ConOpen26wkTotal=null, ConNone26wkTotalPerCent=null,"
                + " ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, "
                + "ConOpen26wkTotalPerCent=null, xConNone26wkTotal=null, xConStd26wkTotal=null, "
                + "xConFast26wkTotal=null, xConOpen26wkTotal=null, "
                + "xConNone26wkTotalPerCent=null, xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
                + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, "
                + "et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, "
                + "manuallyCreatedTotalCasesPercent=null, et1OnlineTotalCasesPercent=null, "
                + "eccTotalCasesPercent=null, migratedTotalCasesPercent=null"
                +"))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(LIVE_CASELOAD_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateLiveCaseloadReportDataForGlasgowWithInvalidPositionType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, "
                + "tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, "
                + "reportDate=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, listingDateTo=null, "
                + "listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, "
                + "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, "
                + "venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, "
                + "docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Live Caseload, "
                + "documentName=null, showAll=null, localReportsSummaryHdr=AdhocReportType(reportDate=null, "
                + "reportOffice=null, receiptDate=null, hearingDate=null, date=null, full=null, half=null, "
                + "mins=null, total=0, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, "
                + "hearingNumber=null, hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, "
                + "clerk=null, hearingSitAlone=null, hearingJudge=null, judgeType=null, judgementDateSent=null, "
                + "position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, "
                + "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, "
                + "casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, "
                + "sessionDays=null, sessionDaysTotal=null, sessionDaysTotalDetail=null, completedPerSession=null, "
                + "completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, "
                + "ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, "
                + "otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=null, "
                + "ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, "
                + "ConFastCasesCompletedHearing=null, ConFastSessionDays=null, ConFastCompletedPerSession=null, "
                + "ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, "
                + "ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, "
                + "totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, "
                + "Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, "
                + "actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, costs=null, "
                + "hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, "
                + "hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, "
                + "subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, "
                + "singlesTotal=0, multiplesTotal=0, dateOfAcceptance=null, respondentET3=null, respondentET4=null, "
                + "listingHistory=null, ConNoneTotal=null, ConStdTotal=null, ConFastTotal=null, ConOpenTotal=null, "
                + "ConNone26wkTotal=null, ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, "
                + "ConNone26wkTotalPerCent=null, ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, "
                + "ConOpen26wkTotalPerCent=null, xConNone26wkTotal=null, xConStd26wkTotal=null, xConFast26wkTotal=null, "
                + "xConOpen26wkTotal=null, xConNone26wkTotalPerCent=null, xConStd26wkTotalPerCent=null, "
                + "xConFast26wkTotalPerCent=null, xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
                + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
                + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
                + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
                + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
                + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, et1OnlineTotalCases=null," +
                " eccTotalCases=null, migratedTotalCases=null, manuallyCreatedTotalCasesPercent=null," +
                " et1OnlineTotalCasesPercent=null, eccTotalCasesPercent=null, migratedTotalCasesPercent=null"
                + "), localReportsSummary=null, "
                + "localReportsSummaryHdr2=null, "
                + "localReportsSummary2=null, localReportsDetailHdr=AdhocReportType(reportDate=null, "
                + "reportOffice=Scotland, receiptDate=null, "
                + "hearingDate=null, date=null, full=null, half=null, mins=null, total=null, eeMember=null, "
                + "erMember=null, caseReference=null, multipleRef=null, multSub=null, hearingNumber=null, "
                + "hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=null, "
                + "hearingSitAlone=null, hearingJudge=null, judgeType=null, judgementDateSent=null, position=null, "
                + "dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, "
                + "fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=null, "
                + "casesCompletedHearing=null, sessionType=null, sessionDays=null, sessionDaysTotal=null, "
                + "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, f"
                + "tSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, ptSessionDaysTotal=null, "
                + "ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, "
                + "conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, "
                + "ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, ConFastSessionDays=null, "
                + "ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, "
                + "ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, "
                + "ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, "
                + "Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, "
                + "Totalx4wkPerCent=null, respondentName=null, actioned=null, bfDate=null, bfDateCleared=null, "
                + "reservedHearing=null, hearingCM=null, costs=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, "
                + "stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, "
                + "review=null, reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, "
                + "judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, "
                + "dateOfAcceptance=null, respondentET3=null, respondentET4=null, listingHistory=null, " +
                "ConNoneTotal=null, ConStdTotal=null, ConFastTotal=null, ConOpenTotal=null, ConNone26wkTotal=null," +
                " ConStd26wkTotal=null, ConFast26wkTotal=null, ConOpen26wkTotal=null, ConNone26wkTotalPerCent=null, " +
                "ConStd26wkTotalPerCent=null, ConFast26wkTotalPerCent=null, ConOpen26wkTotalPerCent=null, " +
                "xConNone26wkTotal=null, xConStd26wkTotal=null, xConFast26wkTotal=null, xConOpen26wkTotal=null, " +
                "xConNone26wkTotalPerCent=null, xConStd26wkTotalPerCent=null, xConFast26wkTotalPerCent=null, " +
                "xConOpen26wkTotalPerCent=null, delayedDaysForFirstHearing=null, "
        + "claimServedDay1Total=null, claimServedDay1Percent=null, claimServedDay2Total=null, "
        + "claimServedDay2Percent=null, claimServedDay3Total=null, claimServedDay3Percent=null, "
        + "claimServedDay4Total=null, claimServedDay4Percent=null, claimServedDay5Total=null, "
        + "claimServedDay5Percent=null, claimServed6PlusDaysTotal=null, claimServed6PlusDaysPercent=null, "
        + "claimServedTotal=null, claimServedItems=null, manuallyCreatedTotalCases=null, " +
                "et1OnlineTotalCases=null, eccTotalCases=null, migratedTotalCases=null, " +
                "manuallyCreatedTotalCasesPercent=null, et1OnlineTotalCasesPercent=null, " +
                "eccTotalCasesPercent=null, migratedTotalCasesPercent=null), localReportsDetail=[])";
        listingDetails.setCaseTypeId(SCOTLAND_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(LIVE_CASELOAD_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setManagingOffice("Aberdeen");
        submitEvents.get(0).getCaseData().setPositionType(POSITION_TYPE_CASE_CLOSED);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
        submitEvents.get(0).getCaseData().setPositionType("Awaiting ET3");
    }

    @Test
    public void generateCasesCompletedReportDataForEnglandWithConTrackNone() throws IOException {
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertNotNull(listingDataResult.getLocalReportsDetailHdr());
        assertEquals(1, listingDataResult.getLocalReportsDetail().size());
    }

    @Test
    public void generateCasesCompletedReportDataForEnglandWithConTrackFast() throws IOException {
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_FAST_TRACK);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertNotNull(listingDataResult.getLocalReportsDetailHdr());
        assertEquals(1, listingDataResult.getLocalReportsDetail().size());
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
    }

    @Test
    public void generateCasesCompletedReportDataForEnglandWithConTrackStandard() throws IOException {
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_STANDARD_TRACK);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertNotNull(listingDataResult.getLocalReportsDetailHdr());
        assertEquals(1, listingDataResult.getLocalReportsDetail().size());
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
    }

    @Test
    public void generateCasesCompletedReportDataForEnglandWithConTrackOpen() throws IOException {
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_OPEN_TRACK);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertNotNull(listingDataResult.getLocalReportsDetailHdr());
        assertEquals(1, listingDataResult.getLocalReportsDetail().size());
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
    }

    @Test
    public void generateHearingToJudgmentsReportData() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(HEARINGS_TO_JUDGEMENTS_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        listingDetails.getCaseData().setHearingDateType("Ranged");
        listingDetails.getCaseData().setListingDate("2021-07-13");
        listingDetails.getCaseData().setListingDateFrom("2021-07-12");
        listingDetails.getCaseData().setListingDateTo("2021-07-14");
        when(ccdClient.hearingsToJudgementsSearch(anyString(), anyString(), anyString())).thenReturn(List.of(new HearingsToJudgmentsSubmitEvent()));
        var listingDataResult = (HearingsToJudgmentsReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(HEARINGS_TO_JUDGEMENTS_REPORT, listingDataResult.getReportType());
        assertEquals("Ranged", listingDataResult.getHearingDateType());
        assertEquals("2021-07-13", listingDataResult.getListingDate());
        assertEquals("2021-07-12", listingDataResult.getListingDateFrom());
        assertEquals("2021-07-14", listingDataResult.getListingDateTo());
    }

    @Test
    public void generateRespondentsReportData() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(RESPONDENTS_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        listingDetails.getCaseData().setHearingDateType("Ranged");
        listingDetails.getCaseData().setListingDate("2022-01-13");
        listingDetails.getCaseData().setListingDateFrom("2022-01-31");
        listingDetails.getCaseData().setListingDateTo("2022-02-08");
        var submitEvent = new RespondentsReportSubmitEvent();
        submitEvent.setCaseData(new RespondentsReportCaseData());
        when(ccdClient.respondentsReportSearch(anyString(), anyString(), anyString())).thenReturn(List.of(submitEvent));
        var listingDataResult = (RespondentsReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(RESPONDENTS_REPORT, listingDataResult.getReportType());
        assertEquals("Ranged", listingDataResult.getHearingDateType());
        assertEquals("2022-01-13", listingDataResult.getListingDate());
        assertEquals("2022-01-31", listingDataResult.getListingDateFrom());
        assertEquals("2022-02-08", listingDataResult.getListingDateTo());
    }

    @Test
    public void generateSessionDaysReportData() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.setCaseId("caseId");
        listingDetails.getCaseData().setReportType(SESSION_DAYS_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        listingDetails.getCaseData().setHearingDateType("Ranged");
        listingDetails.getCaseData().setListingDate("2021-07-13");
        listingDetails.getCaseData().setListingDateFrom("2021-07-12");
        listingDetails.getCaseData().setListingDateTo("2021-07-14");
        var submitEvent = new SessionDaysSubmitEvent();
        submitEvent.setCaseData(new SessionDaysCaseData());
        when(ccdClient.sessionDaysSearch(anyString(), anyString(), anyString())).thenReturn(List.of(submitEvent));
        var listingDataResult = (SessionDaysReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(SESSION_DAYS_REPORT, listingDataResult.getReportType());
        assertEquals("Ranged", listingDataResult.getHearingDateType());
        assertEquals("2021-07-13", listingDataResult.getListingDate());
        assertEquals("2021-07-12", listingDataResult.getListingDateFrom());
        assertEquals("2021-07-14", listingDataResult.getListingDateTo());
    }

    @Test
    public void generateEccReportData() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.setCaseId("caseId");
        listingDetails.getCaseData().setReportType(ECC_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        listingDetails.getCaseData().setHearingDateType("Ranged");
        listingDetails.getCaseData().setListingDate("2021-07-13");
        listingDetails.getCaseData().setListingDateFrom("2021-07-12");
        listingDetails.getCaseData().setListingDateTo("2021-07-14");
        var submitEvent = new EccReportSubmitEvent();
        submitEvent.setCaseData(new EccReportCaseData());
        when(ccdClient.eccReportSearch(anyString(), anyString(), anyString())).thenReturn(List.of(submitEvent));
        var listingDataResult = (EccReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(ECC_REPORT, listingDataResult.getReportType());
        assertEquals("Ranged", listingDataResult.getHearingDateType());
        assertEquals("2021-07-13", listingDataResult.getListingDate());
        assertEquals("2021-07-12", listingDataResult.getListingDateFrom());
        assertEquals("2021-07-14", listingDataResult.getListingDateTo());
    }

    @Test
    public void generateClaimsByHearingVenueReport() throws IOException
    {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.setCaseId("caseId");
        listingDetails.getCaseData().setReportType(CLAIMS_BY_HEARING_VENUE_REPORT);
        listingDetails.getCaseData().setDocumentName(CLAIMS_BY_HEARING_VENUE_REPORT);
        listingDetails.getCaseData().setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.getCaseData().setListingDate("2021-12-15");
        listingDetails.getCaseData().setListingDateFrom("2021-12-03");
        listingDetails.getCaseData().setListingDateTo("2021-12-18");

        var submitEvent = new ClaimsByHearingVenueSubmitEvent();
        submitEvent.setCaseData(new ClaimsByHearingVenueCaseData());

        when(ccdClient.claimsByHearingVenueSearch(anyString(), anyString(), anyString()))
                .thenReturn(List.of(submitEvent));
        var userDetails = new UserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);

        var listingDataResult = (ClaimsByHearingVenueReportData) listingService.generateReportData(listingDetails,
                "authToken");
        assertEquals(CLAIMS_BY_HEARING_VENUE_REPORT, listingDataResult.getDocumentName());
        assertEquals(CLAIMS_BY_HEARING_VENUE_REPORT, listingDataResult.getReportType());
        assertEquals(RANGE_HEARING_DATE_TYPE, listingDataResult.getHearingDateType());
        assertNull(listingDataResult.getListingDate());
        assertEquals("2021-12-03", listingDataResult.getListingDateFrom());
        assertEquals("2021-12-18", listingDataResult.getListingDateTo());
    }

    @Test
    public void generateHearingsByHearingTypeReportData() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.setCaseId("caseId");
        listingDetails.getCaseData().setReportType(HEARINGS_BY_HEARING_TYPE_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        listingDetails.getCaseData().setHearingDateType("Ranged");
        listingDetails.getCaseData().setListingDate("2021-07-13");
        listingDetails.getCaseData().setListingDateFrom("2021-07-12");
        listingDetails.getCaseData().setListingDateTo("2021-07-14");
        var submitEvent = new HearingsByHearingTypeSubmitEvent();
        submitEvent.setCaseData(new HearingsByHearingTypeCaseData());
        when(ccdClient.hearingsByHearingTypeSearch(anyString(), anyString(), anyString())).thenReturn(List.of(submitEvent));
        var listingDataResult = (HearingsByHearingTypeReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(HEARINGS_BY_HEARING_TYPE_REPORT, listingDataResult.getReportType());
        assertEquals("Ranged", listingDataResult.getHearingDateType());
        assertEquals("2021-07-13", listingDataResult.getListingDate());
        assertEquals("2021-07-12", listingDataResult.getListingDateFrom());
        assertEquals("2021-07-14", listingDataResult.getListingDateTo());
    }

    @Test
    public void generateMemberDaysReportData() throws IOException {
        var localSubmitEvents = submitEvents;
        String docName = "Member Days Report - Test";
        listingDetailsRange.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetailsRange.getCaseData().setReportType(MEMBER_DAYS_REPORT);
        listingDetailsRange.getCaseData().setDocumentName(docName);
        listingDetailsRange.getCaseData().setHearingDateType("Range");
        listingDetailsRange.getCaseData().setListingDate("2021-09-12");
        listingDetailsRange.getCaseData().setListingDateFrom("2021-09-08");
        listingDetailsRange.getCaseData().setListingDateTo("2021-09-18");

        var memberDaysReportData = new MemberDaysReportData();
        memberDaysReportData.setFullDaysTotal("0");
        memberDaysReportData.setHalfDaysTotal("4");
        memberDaysReportData.setTotalDays("2");
        memberDaysReportData.setOffice("Manchester");
        memberDaysReportData.setDocumentName(docName);

        var memberDaysReport = Mockito.mock(MemberDaysReport.class);

        doReturn(localSubmitEvents).when(ccdClient).retrieveCasesGenericReportElasticSearch(anyString(),
            anyString(), anyString(), anyString(), anyString());

        doReturn(memberDaysReportData).when(memberDaysReport).runReport(ArgumentMatchers.any(ListingDetails.class),
            Mockito.anyList());

        var listingDataResult = (MemberDaysReportData) listingService.generateReportData(listingDetailsRange,
            "authToken");

        assertEquals(MEMBER_DAYS_REPORT, listingDataResult.getReportType());
    }

    @Test
    public void generateCasesAwaitingJudgmentsReportData() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_AWAITING_JUDGMENT_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        var caseDataBuilder = new CaseDataBuilder();
        when(ccdClient.casesAwaitingJudgmentSearch(anyString(), anyString(), anyString())).thenReturn(
                List.of(caseDataBuilder.withPositionType("Draft with members")
                        .withHearing("1970-01-01T00:00:00.000", HEARING_STATUS_HEARD)
                        .buildAsSubmitEvent(ACCEPTED_STATE)));
        var listingDataResult = (CasesAwaitingJudgmentReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(CASES_AWAITING_JUDGMENT_REPORT, listingDataResult.getReportType());
    }

    @Test
    public void generateNoPositionChangeReportData() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(NO_CHANGE_IN_CURRENT_POSITION_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        listingDetails.getCaseData().setReportDate("2021-12-12");
        var caseDataBuilder = new NoPositionChangeCaseDataBuilder();
        var result =  new NoPositionChangeSearchResult();
        result.setCases(List.of(caseDataBuilder.withCaseType("SINGLE")
                                    .withCurrentPosition("Position")
                                    .withDateToPosition("2021-04-03")
                                    .withReceiptDate("2021-03-03")
                                    .buildAsSubmitEvent(ACCEPTED_STATE)));
        when(ccdClient.runElasticSearch(anyString(), anyString(), anyString(), ArgumentMatchers.eq(NoPositionChangeSearchResult.class)))
                .thenReturn(result);
        when(ccdClient.buildAndGetElasticSearchRequestWithRetriesMultiples(anyString(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        var listingDataResult = (NoPositionChangeReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(NO_CHANGE_IN_CURRENT_POSITION_REPORT, listingDataResult.getReportType());
        assertEquals("2021-12-12", listingDataResult.getReportDate());
    }

    @Test
    public void generateNoPositionChangeReportDataWithMultiple() throws IOException {
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(NO_CHANGE_IN_CURRENT_POSITION_REPORT);
        listingDetails.getCaseData().setDocumentName("name");
        listingDetails.getCaseData().setReportDate("2021-12-12");
        var caseDataBuilder = new NoPositionChangeCaseDataBuilder();
        var result =  new NoPositionChangeSearchResult();
        result.setCases(List.of(caseDataBuilder.withCaseType(MULTIPLE_CASE_TYPE)
                .withCurrentPosition("Position")
                .withDateToPosition("2021-04-03")
                .withMultipleReference("multipleRef")
                .withReceiptDate("2021-03-03")
                .buildAsSubmitEvent(ACCEPTED_STATE)));
        var multipleData = new MultipleData();
        multipleData.setMultipleReference("multipleRef");
        multipleData.setMultipleName("Multiple Name");
        var submitMultipleData = new SubmitMultipleEvent();
        submitMultipleData.setCaseData(multipleData);
        when(ccdClient.runElasticSearch(anyString(), anyString(), anyString(), ArgumentMatchers.eq(NoPositionChangeSearchResult.class)))
                .thenReturn(result);
        when(ccdClient.buildAndGetElasticSearchRequestWithRetriesMultiples(anyString(), anyString(), anyString()))
                .thenReturn(List.of(submitMultipleData));
        var listingDataResult = (NoPositionChangeReportData) listingService.generateReportData(listingDetails, "authToken");
        assertEquals("name", listingDataResult.getDocumentName());
        assertEquals(NO_CHANGE_IN_CURRENT_POSITION_REPORT, listingDataResult.getReportType());
        assertEquals("2021-12-12", listingDataResult.getReportDate());
    }

    @Test(expected = Exception.class)
    public void generateReportDataWithException() throws IOException {
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        listingService.generateReportData(listingDetails, "authToken");
    }
}