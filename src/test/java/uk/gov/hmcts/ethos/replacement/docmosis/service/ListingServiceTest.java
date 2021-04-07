package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.*;
import uk.gov.hmcts.ecm.common.model.ccd.items.*;
import uk.gov.hmcts.ecm.common.model.ccd.types.*;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BFHelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class ListingServiceTest {

    @InjectMocks
    private ListingService listingService;
    @Mock
    private TornadoService tornadoService;
    @Mock
    private CcdClient ccdClient;
    private CaseDetails caseDetails;
    private ListingDetails listingDetails;
    private ListingDetails listingDetailsRange;
    private DocumentInfo documentInfo;
    private List<SubmitEvent> submitEvents;

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

        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingStatus(null);
        dateListedType.setHearingClerk("Clerk");
        dateListedType.setHearingRoomGlasgow("Tribunal 4");
        dateListedType.setHearingAberdeen("AberdeenVenue");
        dateListedType.setHearingVenueDay("Aberdeen");
        dateListedType.setListedDate("2019-12-12T12:11:00.000");
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);

        DateListedTypeItem dateListedTypeItem1 = new DateListedTypeItem();
        DateListedType dateListedType1 = new DateListedType();
        dateListedType.setHearingStatus("Heard");
        dateListedType1.setHearingClerk("Clerk");
        dateListedType1.setHearingRoomGlasgow("Tribunal 4");
        dateListedType1.setHearingAberdeen("AberdeenVenue");
        dateListedType1.setHearingVenueDay("Aberdeen");
        dateListedType1.setListedDate("2019-12-10T12:11:00.000");
        dateListedTypeItem1.setId("124");
        dateListedTypeItem1.setValue(dateListedType1);

        DateListedTypeItem dateListedTypeItem2 = new DateListedTypeItem();
        DateListedType dateListedType2 = new DateListedType();
        dateListedType.setHearingStatus(null);
        dateListedType2.setHearingClerk("Clerk1");
        dateListedType2.setHearingCaseDisposed(YES);
        dateListedType2.setHearingRoomGlasgow("Tribunal 5");
        dateListedType2.setHearingAberdeen("AberdeenVenue2");
        dateListedType2.setHearingVenueDay("Aberdeen");
        dateListedType2.setListedDate("2019-12-12T12:11:30.000");
        dateListedTypeItem2.setId("124");
        dateListedTypeItem2.setValue(dateListedType2);

        DateListedTypeItem dateListedTypeItem3 = new DateListedTypeItem();
        DateListedType dateListedType3 = new DateListedType();
        dateListedType3.setHearingStatus(null);
        dateListedType3.setHearingClerk("Clerk3");
        dateListedType3.setHearingCaseDisposed(YES);
        dateListedType3.setHearingRoomGlasgow("Tribunal 5");
        dateListedType3.setHearingAberdeen("AberdeenVenue2");
        dateListedType3.setHearingVenueDay("Aberdeen");
        dateListedType3.setListedDate("2019-12-12T12:11:55.000");
        dateListedTypeItem3.setId("124");
        dateListedTypeItem3.setValue(dateListedType3);

        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = new HearingType();
        hearingType.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem, dateListedTypeItem1, dateListedTypeItem2)));
        hearingType.setHearingVenue("Aberdeen");
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        hearingType.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingTypeItem.setId("12345");
        hearingTypeItem.setValue(hearingType);

        BFActionTypeItem bfActionTypeItem = new BFActionTypeItem();
        BFActionType bfActionType = new BFActionType();
        bfActionType.setBfDate("2019-12-10");
        bfActionType.setCleared("020-12-30");
        bfActionType.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        bfActionTypeItem.setId("0000");
        bfActionTypeItem.setValue(bfActionType);
        HearingTypeItem hearingTypeItem1 = new HearingTypeItem();
        HearingType hearingType1 = new HearingType();
        hearingType1.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem3)));
        hearingType1.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingTypeItem1.setId("12345");
        hearingTypeItem1.setValue(hearingType1);

        BFActionTypeItem bfActionTypeItem1 = new BFActionTypeItem();
        BFActionType bfActionType1 = new BFActionType();
        bfActionType1.setBfDate("2019-12-11");
        bfActionType1.setCleared("");
        bfActionType1.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        bfActionTypeItem1.setId("111");
        bfActionTypeItem1.setValue(bfActionType1);

        BFActionTypeItem bfActionTypeItem2 = new BFActionTypeItem();
        BFActionType bfActionType2 = new BFActionType();
        bfActionType2.setBfDate("2019-12-12");
        bfActionType2.setCleared("");
        bfActionType2.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        bfActionTypeItem2.setId("222");
        bfActionTypeItem2.setValue(bfActionType2);

        BFActionTypeItem bfActionTypeItem3 = new BFActionTypeItem();
        BFActionType bfActionType3 = new BFActionType();
        bfActionType3.setBfDate("2019-12-13");
        bfActionType3.setCleared("");
        bfActionType3.setAction(BFHelperTest.getBfActionsDynamicFixedList());
        bfActionTypeItem3.setId("333");
        bfActionTypeItem3.setValue(bfActionType3);

        BFActionTypeItem bfActionTypeItem4 = new BFActionTypeItem();
        BFActionType bfActionType4 = new BFActionType();
        bfActionType4.setBfDate("2019-12-10");
        bfActionType4.setCleared("020-12-30");
        bfActionType4.setNotes("Test0");
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
        caseData.setCaseType(SINGLE_CASE_TYPE);
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
    }

    @Test
    public void listingCaseCreationWithHearingDocType() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=AberdeenVenue, venueDundee=null, venueEdinburgh=null, hearingDocType=ETL Test, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=ETL Test, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetails.getCaseData().setHearingDocType("ETL Test");
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
        listingDetails.getCaseData().setHearingDocType(null);
    }

    @Test
    public void listingCaseCreationWithReportType() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=AberdeenVenue, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=Brought Forward Report, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
    }

    @Test
    public void listingCaseCreationWithoutDocumentName() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=AberdeenVenue, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=null, documentName=Missing document name, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetails.getCaseData().setReportType(null);
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
        listingDetails.getCaseData().setReportType("Brought Forward Report");
    }

    @Test
    public void processListingHearingsRequestAberdeen() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , " +
                "claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , " +
                "hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= ))], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestGlasgow() throws IOException {
        listingDetails.getCaseData().setVenueAberdeen(null);
        listingDetails.getCaseData().setVenueGlasgow("GlasgowVenue");
        listingDetails.getCaseData().setListingVenue("Glasgow");
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Glasgow, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, " +
                "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, " +
                "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestEdinburgh() throws IOException {
        listingDetails.getCaseData().setVenueAberdeen(null);
        listingDetails.getCaseData().setVenueEdinburgh("EdinburghVenue");
        listingDetails.getCaseData().setListingVenue("Edinburgh");
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Edinburgh, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, " +
                "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, " +
                "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=null, localReportsDetail=null)";
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
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Dundee, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, " +
                "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, " +
                "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestNonScottish() throws IOException {
        listingDetails.getCaseData().setVenueAberdeen(null);
        listingDetails.getCaseData().setListingVenue("Leeds");
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Leeds, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, hearingDocType=null, hearingDocETCL=null, " +
                "roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, " +
                "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithValidHearingType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Valid Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, " +
                "claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= ))], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=ETCL - Cause List, hearingDocETCL=Public, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingType("Valid Hearing");
        listingDetails.getCaseData().setHearingDocType(HEARING_DOC_ETCL);
        listingDetails.getCaseData().setHearingDocETCL(HEARING_ETCL_PUBLIC);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithInValidHearingType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, hearingDocType=ETCL - Cause List, hearingDocETCL=Public, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingType(HEARING_TYPE_JUDICIAL_MEDIATION);
        listingDetails.getCaseData().setHearingDocType(HEARING_DOC_ETCL);
        listingDetails.getCaseData().setHearingDocETCL(HEARING_ETCL_PUBLIC);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithPrivateHearingType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=ETCL - Cause List, hearingDocETCL=Press List, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().setHearingPublicPrivate(HEARING_TYPE_PRIVATE);
        listingDetails.getCaseData().setHearingDocType(HEARING_DOC_ETCL);
        listingDetails.getCaseData().setHearingDocETCL(HEARING_ETCL_PRESS_LIST);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithALL() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[" +
                "ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= )), " +
                "ListingTypeItem(id=124, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue2, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk1, hearingDay=3 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 5, " +
                "respondentOthers= , hearingNotes= )), ListingTypeItem(id=124, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, " +
                "causeListVenue=AberdeenVenue2, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk3, hearingDay=1 of 1, claimantName=RYAN AIR LTD, claimantTown= , " +
                "claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=null null, hearingPanel= , hearingRoom=Tribunal 5, " +
                "respondentOthers= , hearingNotes= ))], " +
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        listingDetails.getCaseData().setVenueAberdeen(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestRange() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, listingDateFrom=2019-12-09, " +
                "listingDateTo=2019-12-12, listingVenue=Aberdeen, listingCollection=" +
                "[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= )), " +
                "ListingTypeItem(id=124, value=ListingType(causeListDate=10 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=2 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= ))], " +
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestRangeAndAllVenues() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, " +
                "listingDateFrom=2019-12-09, listingDateTo=2019-12-12, listingVenue=All, listingCollection=[ListingTypeItem(id=123, " +
                "value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, j" +
                "urisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= )), ListingTypeItem(id=124, value=ListingType(causeListDate=10 December 2019, causeListTime=12:11, " +
                "causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=2 of 3, " +
                "claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= )), ListingTypeItem(id=124, " +
                "value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue2, elmoCaseReference=4210000/2019, " +
                "jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk1, hearingDay=3 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 5, " +
                "respondentOthers= , hearingNotes= )), ListingTypeItem(id=124, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, " +
                "causeListVenue=AberdeenVenue2, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk3, hearingDay=1 of 1, " +
                "claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , " +
                "estHearingLength=null null, hearingPanel= , hearingRoom=Tribunal 5, respondentOthers= , hearingNotes= ))], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, hearingDocType=null, " +
                "hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, " +
                "documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        listingDetailsRange.getCaseData().setListingVenue(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test(expected = Exception.class)
    public void processListingHearingsRequestWithException() throws IOException {
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        listingService.processListingHearingsRequest(listingDetails, "authToken");
    }

    @Test
    public void processHearingDocument() throws IOException {
        when(tornadoService.listingGeneration(anyString(), any(), anyString())).thenReturn(documentInfo);
        DocumentInfo documentInfo1 = listingService.processHearingDocument(listingDetails.getCaseData(), listingDetails.getCaseTypeId(), "authToken");
        assertEquals(documentInfo, documentInfo1);
    }

    @Test(expected = Exception.class)
    public void processHearingDocumentWithException() throws IOException {
        when(tornadoService.listingGeneration(anyString(), any(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        listingService.processHearingDocument(listingDetails.getCaseData(), listingDetails.getCaseTypeId(), "authToken");
    }

    @Test
    public void processListingHearingsRequestWithAdditionalInfo() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=Juan Pedro, " +
                "claimantTown=Aberdeen, claimantRepresentative=ONG, respondent=Royal McDonal, respondentTown=Aberdeen, respondentRepresentative=ITV, " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers=Royal McDonal, hearingNotes= ))], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName= , " +
                "claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, " +
                "hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= ))], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        caseDetails.getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection().get(2).getValue().setHearingStatus("Settled");
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        assertEquals(result, caseData.getPrintHearingDetails().toString());
        caseDetails.getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection().get(2).getValue().setHearingStatus(null);
    }

    @Test
    public void setCourtAddressFromCaseData() {
        String result = "ListingData(tribunalCorrespondenceAddress=Manchester Avenue, Manchester, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=AberdeenVenue, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        ListingData listingData = listingService.setCourtAddressFromCaseData(caseDetails.getCaseData());
        assertEquals(result, listingData.toString());
    }

    @Test
    public void generateBFReportDataSingleDateMatch() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=[BFDateTypeItem(id=222, " +
                "value=BFDateType(caseReference=4210000/2019, broughtForwardAction=null, broughtForwardDate=2019-12-12, broughtForwardDateCleared=, " +
                "broughtForwardDateReason=null))], clerkResponsible=null, reportType=Brought Forward Report, documentName=null, " +
                "localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=null, localReportsDetail=null)";
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateBFReportDataSingleDateMisMatch() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-30, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=[], clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetails.getCaseData().setListingDate("2019-12-30");
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
        listingDetails.getCaseData().setListingDate("2019-12-12");
    }

    @Test
    public void generateBFReportDataRangeDatesWithMatchingClerkResponsible() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, " +
                "listingDateFrom=2019-12-09, listingDateTo=2019-12-12, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=[BFDateTypeItem(id=111, " +
                "value=BFDateType(caseReference=4210000/2019, broughtForwardAction=null, broughtForwardDate=2019-12-11, " +
                "broughtForwardDateCleared=, broughtForwardDateReason=null)), BFDateTypeItem(id=222, value=BFDateType(caseReference=4210000/2019, " +
                "broughtForwardAction=null, broughtForwardDate=2019-12-12, broughtForwardDateCleared=, broughtForwardDateReason=null))], " +
                "clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, " +
                "localReportsDetail=null)";
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateBFReportDataRangeDatesWithMisMatchedClerkResponsible() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, listingDateFrom=2019-12-09, " +
                "listingDateTo=2019-12-12, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=[], clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetailsRange.getCaseData().setClerkResponsible("not there");
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
        listingDetailsRange.getCaseData().setClerkResponsible("Steve Jones");
    }

    @Test
    public void generateClaimsAcceptedReportDataForEngland() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Claims Accepted, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=null, date=null, " +
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
                "hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, " +
                "hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, " +
                "singlesTotal=1, multiplesTotal=0, dateOfAcceptance=null, respondentET3=null, respondentET4=null, listingHistory=null), " +
                "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, " +
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
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, " +
                "hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, " +
                "leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=Single, singlesTotal=null, multiplesTotal=null, " +
                "dateOfAcceptance=2019-12-12, respondentET3=null, respondentET4=null, listingHistory=null))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CLAIMS_ACCEPTED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateClaimsAcceptedReportDataForGlasgow() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Claims Accepted, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=null, date=null, " +
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
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, " +
                "hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, " +
                "et3ReceivedDate=null, judicialMediation=null, caseType=null, singlesTotal=1, multiplesTotal=0, dateOfAcceptance=null, respondentET3=null, " +
                "respondentET4=null, listingHistory=null), " +
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
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, " +
                "hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, " +
                "leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=Single, singlesTotal=null, multiplesTotal=null, " +
                "dateOfAcceptance=2019-12-12, respondentET3=null, respondentET4=null, listingHistory=null))])";
        listingDetails.setCaseTypeId(SCOTLAND_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CLAIMS_ACCEPTED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setManagingOffice("Glasgow");
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateLiveCaseloadReportDataForEnglandWithValidPositionType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Live Caseload, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, " +
                "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=Manchester, receiptDate=null, " +
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
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, " +
                "hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, " +
                "leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, " +
                "dateOfAcceptance=2019-12-12, respondentET3=null, respondentET4=null, listingHistory=null))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(LIVE_CASELOAD_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateLiveCaseloadReportDataForGlasgowWithInvalidPositionType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Live Caseload, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=[])";
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
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Cases Completed, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=null, date=null, full=null, " +
                "half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=null, hearingSitAlone=null, hearingJudge=null, " +
                "judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=1, casesCompletedHearing=null, sessionType=null, sessionDays=null, " +
                "sessionDaysTotal=1, sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=1.0, ftSessionDays=null, ftSessionDaysTotal=null, " +
                "ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, " +
                "conciliationTrackNo=null, ConNoneCasesCompletedHearing=1, ConNoneSessionDays=1, ConNoneCompletedPerSession=1.0, ConFastCasesCompletedHearing=null, " +
                "ConFastSessionDays=null, ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, " +
                "ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, " +
                "Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, " +
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, " +
                "hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, " +
                "judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, respondentET3=null, respondentET4=null, listingHistory=null), " +
                "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=2019-12-12T12:11:55.000, " +
                "date=null, full=null, half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=4210000/2019, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=Preliminary Hearing, hearingTelConf=null, hearingDuration=null, hearingClerk=Clerk3, clerk=null, hearingSitAlone=null, hearingJudge=null, judgeType=null, " +
                "judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, " +
                "fileLocationEdinburgh=null, casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, sessionDays=1, sessionDaysTotal=null, " +
                "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, " +
                "ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=1, " +
                "ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, ConFastSessionDays=null, " +
                "ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, " +
                "ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, " +
                "Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, " +
                "hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, " +
                "reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, " +
                "respondentET3=null, respondentET4=null, listingHistory=null))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateCasesCompletedReportDataForEnglandWithConTrackFast() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Cases Completed, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=null, date=null, full=null, " +
                "half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=null, hearingSitAlone=null, hearingJudge=null, " +
                "judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=1, casesCompletedHearing=null, sessionType=null, sessionDays=null, " +
                "sessionDaysTotal=1, sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=1.0, ftSessionDays=null, ftSessionDaysTotal=null, " +
                "ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, " +
                "conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=1, " +
                "ConFastSessionDays=1, ConFastCompletedPerSession=1.0, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, " +
                "ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, " +
                "Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, " +
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, " +
                "hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, " +
                "judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, respondentET3=null, respondentET4=null, listingHistory=null), " +
                "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=2019-12-12T12:11:55.000, " +
                "date=null, full=null, half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=4210000/2019, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=Preliminary Hearing, hearingTelConf=null, hearingDuration=null, hearingClerk=Clerk3, clerk=null, hearingSitAlone=null, hearingJudge=null, judgeType=null, " +
                "judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, " +
                "fileLocationEdinburgh=null, casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, sessionDays=1, sessionDaysTotal=null, " +
                "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, " +
                "ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=2, " +
                "ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, ConFastSessionDays=null, " +
                "ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, " +
                "ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, " +
                "Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, " +
                "hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, " +
                "reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, " +
                "respondentET3=null, respondentET4=null, listingHistory=null))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_FAST_TRACK);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
    }

    @Test
    public void generateCasesCompletedReportDataForEnglandWithConTrackStandard() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Cases Completed, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=null, date=null, full=null, " +
                "half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=null, hearingSitAlone=null, hearingJudge=null, " +
                "judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=1, casesCompletedHearing=null, sessionType=null, sessionDays=null, " +
                "sessionDaysTotal=1, sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=1.0, ftSessionDays=null, ftSessionDaysTotal=null, " +
                "ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, " +
                "conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, " +
                "ConFastSessionDays=null, ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=1, ConStdSessionDays=1, ConStdCompletedPerSession=1.0, " +
                "ConOpenCasesCompletedHearing=null, ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, " +
                "Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, " +
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, " +
                "hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, " +
                "judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, respondentET3=null, respondentET4=null, listingHistory=null), " +
                "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=2019-12-12T12:11:55.000, " +
                "date=null, full=null, half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=4210000/2019, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=Preliminary Hearing, hearingTelConf=null, hearingDuration=null, hearingClerk=Clerk3, clerk=null, hearingSitAlone=null, hearingJudge=null, judgeType=null, " +
                "judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, " +
                "fileLocationEdinburgh=null, casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, sessionDays=1, sessionDaysTotal=null, " +
                "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, " +
                "ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=3, " +
                "ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, ConFastSessionDays=null, " +
                "ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, " +
                "ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, " +
                "Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, " +
                "hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, " +
                "reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, " +
                "respondentET3=null, respondentET4=null, listingHistory=null))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_STANDARD_TRACK);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
    }

    @Test
    public void generateCasesCompletedReportDataForEnglandWithConTrackOpen() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "venueGlasgow=null, venueAberdeen=null, venueDundee=null, venueEdinburgh=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Cases Completed, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, localReportsSummaryHdr2=null, " +
                "localReportsSummary2=null, " +
                "localReportsDetailHdr=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=null, date=null, full=null, " +
                "half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=null, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=null, hearingTelConf=null, hearingDuration=null, hearingClerk=null, clerk=null, hearingSitAlone=null, hearingJudge=null, " +
                "judgeType=null, judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, casesCompletedHearingTotal=1, casesCompletedHearing=null, sessionType=null, sessionDays=null, " +
                "sessionDaysTotal=1, sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=1.0, ftSessionDays=null, ftSessionDaysTotal=null, " +
                "ptSessionDays=null, ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, " +
                "conciliationTrackNo=null, ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, " +
                "ConFastSessionDays=null, ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, " +
                "ConOpenCasesCompletedHearing=1, ConOpenSessionDays=1, ConOpenCompletedPerSession=1.0, totalCases=null, Total26wk=null, Total26wkPerCent=null, " +
                "Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, " +
                "bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, " +
                "hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, " +
                "judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, respondentET3=null, respondentET4=null, listingHistory=null), " +
                "localReportsDetail=[AdhocReportTypeItem(id=null, value=AdhocReportType(reportDate=null, reportOffice=null, receiptDate=null, hearingDate=2019-12-12T12:11:55.000, " +
                "date=null, full=null, half=null, mins=null, total=null, eeMember=null, erMember=null, caseReference=4210000/2019, multipleRef=null, multSub=null, hearingNumber=null, " +
                "hearingType=Preliminary Hearing, hearingTelConf=null, hearingDuration=null, hearingClerk=Clerk3, clerk=null, hearingSitAlone=null, hearingJudge=null, judgeType=null, " +
                "judgementDateSent=null, position=null, dateToPosition=null, fileLocation=null, fileLocationGlasgow=null, fileLocationAberdeen=null, fileLocationDundee=null, " +
                "fileLocationEdinburgh=null, casesCompletedHearingTotal=null, casesCompletedHearing=null, sessionType=null, sessionDays=1, sessionDaysTotal=null, " +
                "sessionDaysTotalDetail=null, completedPerSession=null, completedPerSessionTotal=null, ftSessionDays=null, ftSessionDaysTotal=null, ptSessionDays=null, " +
                "ptSessionDaysTotal=null, ptSessionDaysPerCent=null, otherSessionDaysTotal=null, otherSessionDays=null, conciliationTrack=null, conciliationTrackNo=4, " +
                "ConNoneCasesCompletedHearing=null, ConNoneSessionDays=null, ConNoneCompletedPerSession=null, ConFastCasesCompletedHearing=null, ConFastSessionDays=null, " +
                "ConFastCompletedPerSession=null, ConStdCasesCompletedHearing=null, ConStdSessionDays=null, ConStdCompletedPerSession=null, ConOpenCasesCompletedHearing=null, " +
                "ConOpenSessionDays=null, ConOpenCompletedPerSession=null, totalCases=null, Total26wk=null, Total26wkPerCent=null, Totalx26wk=null, Totalx26wkPerCent=null, Total4wk=null, " +
                "Total4wkPerCent=null, Totalx4wk=null, Totalx4wkPerCent=null, respondentName=null, actioned=null, bfDate=null, bfDateCleared=null, reservedHearing=null, hearingCM=null, " +
                "hearingInterloc=null, hearingPH=null, hearingPrelim=null, stage=null, hearingStage1=null, hearingStage2=null, hearingFull=null, hearing=null, remedy=null, review=null, " +
                "reconsider=null, subSplit=null, leadCase=null, et3ReceivedDate=null, judicialMediation=null, caseType=null, singlesTotal=null, multiplesTotal=null, dateOfAcceptance=null, " +
                "respondentET3=null, respondentET4=null, listingHistory=null))])";
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CASES_COMPLETED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_OPEN_TRACK);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
        submitEvents.get(0).getCaseData().setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
    }

    @Test(expected = Exception.class)
    public void generateReportDataWithException() throws IOException {
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        listingService.generateReportData(listingDetails, "authToken");
    }

}