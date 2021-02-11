package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BroughtForwardDatesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BroughtForwardDatesType;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_VENUES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_COMPLETED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_NO_CONCILIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_OPEN_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_STANDARD_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_OUTCOME_SUCCESSFUL_AT_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIVE_CASELOAD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.POSITION_TYPE_CASE_CLOSED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

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
        listingData.setListingVenueOfficeAber("AberdeenVenue");
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
        listingData1.setListingVenueOfficeAber("AberdeenVenue");
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

        HearingTypeItem hearingTypeItem1 = new HearingTypeItem();
        HearingType hearingType1 = new HearingType();
        hearingType1.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem3)));
        hearingType1.setHearingType(HEARING_TYPE_PERLIMINARY_HEARING);
        hearingTypeItem1.setId("12345");
        hearingTypeItem1.setValue(hearingType1);

        BroughtForwardDatesTypeItem broughtForwardDatesTypeItem = new BroughtForwardDatesTypeItem();
        BroughtForwardDatesType broughtForwardDatesType = new BroughtForwardDatesType();
        broughtForwardDatesType.setBroughtForwardDate("2019-12-10");
        broughtForwardDatesType.setBroughtForwardDateCleared("020-12-30");
        broughtForwardDatesType.setBroughtForwardDateReason("Test0");
        broughtForwardDatesTypeItem.setId("0000");
        broughtForwardDatesTypeItem.setValue(broughtForwardDatesType);

        BroughtForwardDatesTypeItem broughtForwardDatesTypeItem1 = new BroughtForwardDatesTypeItem();
        BroughtForwardDatesType broughtForwardDatesType1 = new BroughtForwardDatesType();
        broughtForwardDatesType1.setBroughtForwardDate("2019-12-11");
        broughtForwardDatesType1.setBroughtForwardDateCleared("");
        broughtForwardDatesType1.setBroughtForwardDateReason("Test1");
        broughtForwardDatesTypeItem1.setId("111");
        broughtForwardDatesTypeItem1.setValue(broughtForwardDatesType1);

        BroughtForwardDatesTypeItem broughtForwardDatesTypeItem2 = new BroughtForwardDatesTypeItem();
        BroughtForwardDatesType broughtForwardDatesType2 = new BroughtForwardDatesType();
        broughtForwardDatesType2.setBroughtForwardDate("2019-12-12");
        broughtForwardDatesType2.setBroughtForwardDateCleared("");
        broughtForwardDatesType2.setBroughtForwardDateReason("Test2");
        broughtForwardDatesTypeItem2.setId("222");
        broughtForwardDatesTypeItem2.setValue(broughtForwardDatesType2);

        BroughtForwardDatesTypeItem broughtForwardDatesTypeItem3 = new BroughtForwardDatesTypeItem();
        BroughtForwardDatesType broughtForwardDatesType3 = new BroughtForwardDatesType();
        broughtForwardDatesType3.setBroughtForwardDate("2019-12-13");
        broughtForwardDatesType3.setBroughtForwardDateCleared("");
        broughtForwardDatesType3.setBroughtForwardDateReason("Test3");
        broughtForwardDatesTypeItem3.setId("333");
        broughtForwardDatesTypeItem3.setValue(broughtForwardDatesType3);

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
        caseData.setHearingCollection(new ArrayList<>(Arrays.asList(hearingTypeItem, hearingTypeItem1)));
        caseData.setBroughtForwardCollection(new ArrayList<>(Arrays.asList(broughtForwardDatesTypeItem,
                broughtForwardDatesTypeItem1, broughtForwardDatesTypeItem2, broughtForwardDatesTypeItem3)));
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
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=AberdeenVenue, " +
                "hearingDocType=ETL Test, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=ETL Test, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetails.getCaseData().setHearingDocType("ETL Test");
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
        listingDetails.getCaseData().setHearingDocType(null);
    }

    @Test
    public void listingCaseCreationWithReportType() {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=AberdeenVenue, " +
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
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=AberdeenVenue, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=null, documentName=Missing document name, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        listingDetails.getCaseData().setReportType(null);
        ListingData listingData = listingService.listingCaseCreation(listingDetails);
        assertEquals(result, listingData.toString());
        listingDetails.getCaseData().setReportType("Brought Forward Report");
    }

    @Test
    public void processListingHearingsRequestAberdeen() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , " +
                "claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , " +
                "hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= ))], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestAberdeenWithALL() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
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
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        listingDetails.getCaseData().setListingVenueOfficeAber(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void processListingHearingsRequestRange() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=" +
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
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=All, listingCollection=" +
                "[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= )), " +
                "ListingTypeItem(id=124, value=ListingType(causeListDate=10 December 2019, causeListTime=12:11, causeListVenue=AberdeenVenue, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , hearingClerk=Clerk, hearingDay=2 of 3, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
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
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=null, hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=null, clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, " +
                "localReportsSummary=null, localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        listingDetailsRange.getCaseData().setListingVenue(ALL_VENUES);
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test(expected = Exception.class)
    public void processListingHearingsRequestWithException() throws IOException {
        when(ccdClient.retrieveCasesVenueAndDateElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(new RuntimeException());
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
        when(tornadoService.listingGeneration(anyString(), any(), anyString())).thenThrow(new RuntimeException());
        listingService.processHearingDocument(listingDetails.getCaseData(), listingDetails.getCaseTypeId(), "authToken");
    }

    @Test
    public void processListingHearingsRequestWithAdditionalInfo() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, positionType=Awaiting ET3, " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName=Juan Pedro, " +
                "claimantTown=Aberdeen, claimantRepresentative=ONG, respondent=Royal McDonal, respondentTown=Aberdeen, respondentRepresentative=ITV, " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers=Royal McDonal, hearingNotes= ))], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=null, hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=AberdeenVenue, elmoCaseReference=4210000/2019, jurisdictionCodesList=ABC, hearingType=Preliminary Hearing, " +
                "positionType=Awaiting ET3, hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=1 of 3, claimantName= , " +
                "claimantTown= , claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, " +
                "hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= ))], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        caseDetails.getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection().get(2).getValue().setHearingStatus("Settled");
        CaseData caseData = listingService.processListingSingleCasesRequest(caseDetails);
        assertEquals(result, caseData.getPrintHearingDetails().toString());
        caseDetails.getCaseData().getHearingCollection().get(0).getValue().getHearingDateCollection().get(2).getValue().setHearingStatus(null);
    }

    @Test
    public void setCourtAddressFromCaseData() {
        String result = "ListingData(tribunalCorrespondenceAddress=Manchester Avenue, Manchester, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=AberdeenVenue, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, clerkResponsible=null, " +
                "reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        ListingData listingData = listingService.setCourtAddressFromCaseData(caseDetails.getCaseData());
        assertEquals(result, listingData.toString());
    }

    @Test
    public void generateBFReportDataSingleDateMatch() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=[BFDateTypeItem(id=222, value=BFDateType(caseReference=4210000/2019, " +
                "broughtForwardDate=2019-12-12, broughtForwardDateReason=Test2, broughtForwardDateCleared=))], " +
                "clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateBFReportDataSingleDateMisMatch() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, " +
                "bfDateCollection=[BFDateTypeItem(id=111, value=BFDateType(caseReference=4210000/2019, broughtForwardDate=2019-12-11, " +
                "broughtForwardDateReason=Test1, broughtForwardDateCleared=)), BFDateTypeItem(id=222, value=BFDateType(caseReference=4210000/2019, " +
                "broughtForwardDate=2019-12-12, broughtForwardDateReason=Test2, broughtForwardDateCleared=))], " +
                "clerkResponsible=null, reportType=Brought Forward Report, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=null)";
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        ListingData listingDataResult = listingService.generateReportData(listingDetailsRange, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateBFReportDataRangeDatesWithMisMatchedClerkResponsible() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
        listingDetails.setCaseTypeId(SCOTLAND_CASE_TYPE_ID);
        listingDetails.getCaseData().setReportType(CLAIMS_ACCEPTED_REPORT);
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(submitEvents);
        submitEvents.get(0).getCaseData().setManagingOffice("Glasgow");
        ListingData listingDataResult = listingService.generateReportData(listingDetails, "authToken");
        assertEquals(result, listingDataResult.toString());
    }

    @Test
    public void generateLiveCaseloadReportDataForEnglandWithValidPositionType() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null, bfDateCollection=null, " +
                "clerkResponsible=null, reportType=Live Caseload, documentName=null, localReportsSummaryHdr=null, localReportsSummary=null, " +
                "localReportsSummaryHdr2=null, localReportsSummary2=null, localReportsDetailHdr=null, localReportsDetail=[])";
        listingDetails.setCaseTypeId(SCOTLAND_CASE_TYPE_ID);
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[], listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
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
        when(ccdClient.retrieveCasesGenericReportElasticSearch(anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(new RuntimeException());
        listingService.generateReportData(listingDetails, "authToken");
    }

}