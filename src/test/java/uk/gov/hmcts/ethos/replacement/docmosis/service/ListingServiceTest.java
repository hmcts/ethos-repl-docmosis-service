package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringJUnit4ClassRunner.class)
public class ListingServiceTest {

    @InjectMocks
    private ListingService listingService;
    @Mock
    private TornadoService tornadoService;
    @Mock
    private CcdClient ccdClient;
    private ListingDetails listingDetails;
    private ListingDetails listingDetailsRange;
    private DocumentInfo documentInfo;
    private List<SubmitEvent> submitEvents;

    @Before
    public void setUp() {
        documentInfo = new DocumentInfo();
        listingDetails = new ListingDetails();
        ListingData listingData = new ListingData();
        listingData.setListingDate("2019-12-12");
        listingData.setListingVenue("Aberdeen");
        listingData.setListingVenueOfficeAber("Aberdeen");
        listingData.setListingCollection(new ArrayList<>());
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingDetails.setCaseData(listingData);
        listingDetails.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetails.setJurisdiction("EMPLOYMENT");

        listingDetailsRange = new ListingDetails();
        ListingData listingData1 = new ListingData();
        listingData1.setListingDateFrom("2019-12-09");
        listingData1.setListingDateTo("2019-12-12");
        listingData1.setListingVenue("Aberdeen");
        listingData1.setListingVenueOfficeAber("Aberdeen");
        listingData1.setListingCollection(new ArrayList<>());
        listingData1.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetailsRange.setCaseData(listingData1);
        listingDetailsRange.setCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID);
        listingDetailsRange.setJurisdiction("EMPLOYMENT");

        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = new HearingType();

        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingClerk("Clerk");
        dateListedType.setHearingRoomGlasgow("Tribunal 4");
        dateListedType.setHearingAberdeen("Aberdeen");
        dateListedType.setHearingVenueDay("Aberdeen");
        dateListedType.setListedDate("2019-12-12T12:11:00.000");
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);

        DateListedTypeItem dateListedTypeItem1 = new DateListedTypeItem();
        DateListedType dateListedType1 = new DateListedType();
        dateListedType1.setHearingClerk("Clerk");
        dateListedType1.setHearingRoomGlasgow("Tribunal 4");
        dateListedType1.setHearingAberdeen("Aberdeen");
        dateListedType1.setHearingVenueDay("Aberdeen");
        dateListedType1.setListedDate("2019-12-10T12:11:00.000");
        dateListedTypeItem1.setId("124");
        dateListedTypeItem1.setValue(dateListedType1);

        hearingType.setHearingDateCollection(new ArrayList<>(Arrays.asList(dateListedTypeItem, dateListedTypeItem1)));
        hearingType.setHearingVenue("Aberdeen");
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        hearingTypeItem.setId("12345");
        hearingTypeItem.setValue(hearingType);
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseId(1);
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("4210000/2019");
        caseData.setHearingCollection(new ArrayList<>(Collections.singleton(hearingTypeItem)));
        submitEvent1.setCaseData(caseData);
        submitEvents = new ArrayList<>(Collections.singleton(submitEvent1));
    }

    @Test
    public void processListingHearingsRequest() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=Aberdeen, elmoCaseReference=4210000/2019, jurisdictionCodesList= , hearingType= , positionType= , " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , clerkResponsible= , hearingDay=1 of 1, claimantName=RYAN AIR LTD, claimantTown= , " +
                "claimantRepresentative= , respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , " +
                "hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= ))], listingVenueOfficeGlas=null, listingVenueOfficeAber=Aberdeen, " +
                "hearingDocType=null, hearingDocETCL=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenReturn(submitEvents);
        ListingDetails listingDetailsResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDetailsResult.getCaseData().toString());
    }

    @Test
    public void processListingHearingsRequestRange() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Range, listingDate=null, listingDateFrom=2019-12-09, " +
                "listingDateTo=2019-12-12, listingVenue=Aberdeen, listingCollection=" +
                "[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Aberdeen, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , clerkResponsible= , hearingDay=1 of 1, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= )), " +
                "ListingTypeItem(id=124, value=ListingType(causeListDate=10 December 2019, causeListTime=12:11, causeListVenue=Aberdeen, " +
                "elmoCaseReference=4210000/2019, jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , " +
                "hearingERMember= , clerkResponsible= , hearingDay=1 of 1, claimantName=RYAN AIR LTD, claimantTown= , claimantRepresentative= , " +
                "respondent= , respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, " +
                "respondentOthers= , hearingNotes= ))], " +
                "listingVenueOfficeGlas=null, listingVenueOfficeAber=Aberdeen, hearingDocType=null, hearingDocETCL=null)";
        submitEvents.get(0).getCaseData().setClaimantCompany("RYAN AIR LTD");
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenReturn(submitEvents);
        ListingDetails listingDetailsResult = listingService.processListingHearingsRequest(listingDetailsRange, "authToken");
        assertEquals(result, listingDetailsResult.getCaseData().toString());
    }

    @Test(expected = Exception.class)
    public void processListingHearingsRequestWithException() throws IOException {
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenThrow(feignError());
        listingService.processListingHearingsRequest(listingDetails, "authToken");
    }

    @Test
    public void processHearingDocument() throws IOException {
        when(tornadoService.listingGeneration(anyString(), any(), anyString())).thenReturn(documentInfo);
        DocumentInfo documentInfo1 = listingService.processHearingDocument(listingDetails, "authToken");
        assertEquals(documentInfo, documentInfo1);
    }

    @Test(expected = Exception.class)
    public void processHearingDocumentWithException() throws IOException {
        when(tornadoService.listingGeneration(anyString(), any(), anyString())).thenThrow(feignError());
        listingService.processHearingDocument(listingDetails, "authToken");
    }

    @Test
    public void processListingHearingsRequestWithAdditionalInfo() throws IOException {
        String result = "ListingData(tribunalCorrespondenceAddress=null, tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, " +
                "tribunalCorrespondenceDX=null, tribunalCorrespondenceEmail=null, hearingDateType=Single, listingDate=2019-12-12, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=Aberdeen, listingCollection=[ListingTypeItem(id=123, value=ListingType(causeListDate=12 December 2019, " +
                "causeListTime=12:11, causeListVenue=Aberdeen, elmoCaseReference=4210000/2019, jurisdictionCodesList= , hearingType= , positionType= , " +
                "hearingJudgeName= , hearingEEMember= , hearingERMember= , clerkResponsible= , hearingDay=1 of 1, claimantName=Juan Pedro, " +
                "claimantTown=Aberdeen, claimantRepresentative=ONG, respondent=Royal McDonal, respondentTown=Aberdeen, respondentRepresentative=ITV, " +
                "estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers=Royal McDonal, hearingNotes= ))], listingVenueOfficeGlas=null, " +
                "listingVenueOfficeAber=Aberdeen, hearingDocType=null, hearingDocETCL=null)";
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
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenReturn(submitEvents);
        ListingDetails listingDetailsResult = listingService.processListingHearingsRequest(listingDetails, "authToken");
        assertEquals(result, listingDetailsResult.getCaseData().toString());
    }
}