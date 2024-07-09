package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.reference.ReferenceData;
import uk.gov.hmcts.ecm.common.model.reference.ReferenceDetails;
import uk.gov.hmcts.ecm.common.model.reference.ReferenceSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reference.types.ClerkType;
import uk.gov.hmcts.ecm.common.model.reference.types.JudgeType;
import uk.gov.hmcts.ecm.common.model.reference.types.VenueType;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReferenceServiceTest {

    @Mock
    private CcdClient ccdClient;

    @InjectMocks
    private ReferenceService referenceService;

    private CaseDetails caseDetails;
    private ReferenceDetails referenceDetails;

    private List<ReferenceSubmitEvent> referenceSubmitEvents;
    private List<ReferenceSubmitEvent> referenceSubmitEventsNoVenues;
    private List<ReferenceSubmitEvent> referenceSubmitEventsNoClerks;
    private List<ReferenceSubmitEvent> referenceSubmitEventsNoJudges;

    @Before
    public void setUp() {

        caseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        caseData.setHearingVenue(null);
        caseDetails.setCaseData(caseData);
        caseDetails.setJurisdiction("EMPLOYMENT");

        ReferenceSubmitEvent referenceSubmitEvent1 = new ReferenceSubmitEvent();
        ReferenceData referenceData1 = new ReferenceData();
        VenueType venueType1 = new VenueType();
        venueType1.setVenueName("Venue1");
        referenceData1.setVenueType(venueType1);
        referenceSubmitEvent1.setCaseData(referenceData1);
        referenceSubmitEvent1.setCaseId(1);

        ReferenceSubmitEvent referenceSubmitEvent2 = new ReferenceSubmitEvent();
        ReferenceData referenceData2 = new ReferenceData();
        VenueType venueType2 = new VenueType();
        venueType2.setVenueName("Venue2");
        referenceData2.setVenueType(venueType2);
        referenceSubmitEvent2.setCaseData(referenceData2);
        referenceSubmitEvent2.setCaseId(2);

        ReferenceSubmitEvent referenceSubmitEvent3 = new ReferenceSubmitEvent();
        ReferenceData referenceData3 = new ReferenceData();
        VenueType venueType3 = new VenueType();
        venueType3.setVenueName("Venue3");
        referenceData3.setVenueType(venueType3);
        referenceSubmitEvent3.setCaseData(referenceData3);
        referenceSubmitEvent3.setCaseId(3);

        ReferenceData referenceData4 = new ReferenceData();
        ClerkType clerkType4 = new ClerkType();
        clerkType4.setFirstName("First Name 4");
        clerkType4.setLastName("Last Name 4");
        ReferenceSubmitEvent referenceSubmitEvent4 = new ReferenceSubmitEvent();
        referenceData4.setClerkType(clerkType4);
        referenceSubmitEvent4.setCaseData(referenceData4);
        referenceSubmitEvent4.setCaseId(4);
        ReferenceData referenceData5 = new ReferenceData();
        ClerkType clerkType5 = new ClerkType();
        clerkType5.setFirstName("First Name 5");
        clerkType5.setLastName("Last Name 5");
        referenceData5.setClerkType(clerkType5);
        ReferenceSubmitEvent referenceSubmitEvent5 = new ReferenceSubmitEvent();
        referenceSubmitEvent5.setCaseData(referenceData5);
        referenceSubmitEvent5.setCaseId(5);

        ReferenceSubmitEvent referenceSubmitEvent6 = new ReferenceSubmitEvent();
        ReferenceData referenceData6 = new ReferenceData();
        JudgeType judgeType6 = new JudgeType();
        judgeType6.setJudgeDisplayName("Judge6");
        referenceData6.setJudgeType(judgeType6);
        referenceSubmitEvent6.setCaseData(referenceData6);
        referenceSubmitEvent6.setCaseId(6);

        referenceSubmitEvents = new ArrayList<>(Arrays.asList(
                referenceSubmitEvent1, referenceSubmitEvent2, referenceSubmitEvent3));
        referenceSubmitEvents.add(referenceSubmitEvent4);
        referenceSubmitEvents.add(referenceSubmitEvent5);
        referenceSubmitEvents.add(referenceSubmitEvent6);

        referenceSubmitEventsNoVenues = new ArrayList<>(Arrays.asList(
                referenceSubmitEvent4, referenceSubmitEvent5, referenceSubmitEvent6));

        referenceSubmitEventsNoClerks = new ArrayList<>(Arrays.asList(
                referenceSubmitEvent1, referenceSubmitEvent2, referenceSubmitEvent3));
        referenceSubmitEventsNoClerks.add(referenceSubmitEvent6);

        referenceSubmitEventsNoJudges = new ArrayList<>(Arrays.asList(
                referenceSubmitEvent1, referenceSubmitEvent2, referenceSubmitEvent3));
        referenceSubmitEventsNoJudges.add(referenceSubmitEvent4);
        referenceSubmitEventsNoJudges.add(referenceSubmitEvent5);

        referenceDetails = new ReferenceDetails();
        referenceDetails.setCaseData(referenceData6);
        referenceDetails.setJurisdiction("EMPLOYMENT");
    }

    @Test
    public void fetchHearingVenueRefDataWithThreeVenuesPresent() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, "
                + "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, "
                + "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, "
                + "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, "
                + "claimantType=null, claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, "
                + "feeGroupReference=null, claimantWorkAddressQuestion=null, claimantWorkAddressQRespondent=null, "
                + "representativeClaimantType=null, respondentCollection=null, repCollection=null, positionType=null, "
                + "dateToPosition=null, currentPosition=null, fileLocation=null, fileLocationGlasgow=null, "
                + "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, "
                + "updateHearingDetails=null, hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, depositCollection=null, judgementCollection=null, jurCodesCollection=null, "
                + "bfActions=null, clerkResponsible=null, userLocation=null, documentCollection=null, "
                + "additionalCaseInfoType=null, correspondenceScotType=null, correspondenceType=null, "
                + "addressLabelsSelectionType=null, addressLabelCollection=null, addressLabelsAttributesType=null, "
                + "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, "
                + "allocatedOffice=null, caseSource=null, conciliationTrack=null, counterClaim=null, "
                + "eccCases=null, restrictedReporting=null, printHearingDetails=null, "
                + "printHearingCollection=null, targetHearingDate=null, claimant=null, respondent=null, "
                + "EQP=null, flag1=null, flag2=null, docMarkUp=null, caseRefNumberCount=null, "
                + "startCaseRefNumber=null, multipleRefNumber=null, caseRefECC=null, respondentECC=null, ccdID=null, "
                + "flagsImageFileName=null, flagsImageAltText=null, hearingNumbers=null, hearingTypes=null, "
                + "hearingPublicPrivate=null, "
                + "hearingVenue=DynamicFixedListType(value=DynamicValueType(code=Venue1, label=Venue1), "
                + "listItems=[DynamicValueType(code=Venue1, label=Venue1), "
                + "DynamicValueType(code=Venue2, label=Venue2), DynamicValueType(code=Venue3, label=Venue3)]), "
                + "hearingEstLengthNum=null, hearingEstLengthNumType=null, "
                + "hearingSitAlone=null, hearingStage=null, listedDate=null, hearingNotes=null, "
                + "hearingSelection=null, hearingActions=null, hearingERMember=null, hearingEEMember=null, "
                + "hearingDatesRequireAmending=null, hearingDateSelection=null, hearingDateActions=null, "
                + "hearingStatus=null, Postponed_by=null, hearingRoom=null, hearingClerk=null, hearingJudge=null, "
                + "hearingCaseDisposed=null, hearingPartHeard=null, "
                + "hearingReservedJudgement=null, attendeeClaimant=null, attendeeNonAttendees=null, "
                + "attendeeRespNoRep=null, attendeeRespAndRep=null, attendeeRepOnly=null, "
                + "hearingTimingStart=null, hearingTimingBreak=null, hearingTimingResume=null, "
                + "hearingTimingFinish=null, hearingTimingDuration=null, companyPremises=null, "
                + "officeCT=null, reasonForCT=null, relatedCaseCT=null, positionTypeCT=null, "
                + "linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEvents);
        CaseData caseDataResult = referenceService.fetchHearingVenueRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test
    public void fetchHearingVenueRefDataWithNoVenuesRefData() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, " +
                "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, " +
                "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, " +
                "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, "
                + "claimantType=null, claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, "
                + "feeGroupReference=null, claimantWorkAddressQuestion=null, "
                + "claimantWorkAddressQRespondent=null, representativeClaimantType=null, "
                + "respondentCollection=null, repCollection=null, positionType=null, dateToPosition=null, "
                + "currentPosition=null, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, updateHearingDetails=null, hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, " +
                "depositCollection=null, judgementCollection=null, jurCodesCollection=null, " +
                "bfActions=null, clerkResponsible=null, userLocation=null, documentCollection=null, " +
                "additionalCaseInfoType=null, correspondenceScotType=null, correspondenceType=null, " +
                "addressLabelsSelectionType=null, addressLabelCollection=null, addressLabelsAttributesType=null, " +
                "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, "
                + "allocatedOffice=null, caseSource=null, conciliationTrack=null, counterClaim=null, eccCases=null, "
                + "restrictedReporting=null, printHearingDetails=null, printHearingCollection=null, "
                + "targetHearingDate=null, claimant=null, respondent=null, EQP=null, flag1=null, flag2=null, "
                + "docMarkUp=null, caseRefNumberCount=null, startCaseRefNumber=null, multipleRefNumber=null, "
                + "caseRefECC=null, respondentECC=null, ccdID=null, flagsImageFileName=null, flagsImageAltText=null, "
                + "hearingNumbers=null, hearingTypes=null, hearingPublicPrivate=null, hearingVenue=null, "
                + "hearingEstLengthNum=null, hearingEstLengthNumType=null, hearingSitAlone=null, hearingStage=null, "
                + "listedDate=null, hearingNotes=null, hearingSelection=null, hearingActions=null, "
                + "hearingERMember=null, hearingEEMember=null, hearingDatesRequireAmending=null, "
                + "hearingDateSelection=null, hearingDateActions=null, hearingStatus=null, Postponed_by=null, "
                + "hearingRoom=null, hearingClerk=null, hearingJudge=null, hearingCaseDisposed=null, "
                + "hearingPartHeard=null, hearingReservedJudgement=null, attendeeClaimant=null, "
                + "attendeeNonAttendees=null, attendeeRespNoRep=null, attendeeRespAndRep=null, "
                + "attendeeRepOnly=null, hearingTimingStart=null, hearingTimingBreak=null, "
                + "hearingTimingResume=null, hearingTimingFinish=null, hearingTimingDuration=null, "
                + "companyPremises=null, officeCT=null, reasonForCT=null, relatedCaseCT=null, "
                + "positionTypeCT=null, linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEventsNoVenues);
        CaseData caseDataResult = referenceService.fetchHearingVenueRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test
    public void fetchHearingVenueRefDataWithNoReferenceData() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, " +
                "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, " +
                "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, " +
                "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, feeGroupReference=null, claimantWorkAddressQuestion=null, " +
                "claimantWorkAddressQRespondent=null, representativeClaimantType=null, " +
                "respondentCollection=null, repCollection=null, positionType=null, dateToPosition=null, currentPosition=null, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, updateHearingDetails=null, hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, " +
                "depositCollection=null, judgementCollection=null, jurCodesCollection=null, " +
                "bfActions=null, clerkResponsible=null, userLocation=null, " +
                "documentCollection=null, additionalCaseInfoType=null, " +
                "correspondenceScotType=null, correspondenceType=null, addressLabelsSelectionType=null, addressLabelCollection=null, addressLabelsAttributesType=null, " +
                "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, allocatedOffice=null, " +
                "caseSource=null, conciliationTrack=null, counterClaim=null, eccCases=null, restrictedReporting=null, printHearingDetails=null, " +
                "printHearingCollection=null, targetHearingDate=null, claimant=null, respondent=null, EQP=null, flag1=null, flag2=null, docMarkUp=null, caseRefNumberCount=null, " +
                "startCaseRefNumber=null, multipleRefNumber=null, caseRefECC=null, respondentECC=null, ccdID=null, flagsImageFileName=null, flagsImageAltText=null, " +
                "hearingNumbers=null, hearingTypes=null, hearingPublicPrivate=null, hearingVenue=null, hearingEstLengthNum=null, hearingEstLengthNumType=null, " +
                "hearingSitAlone=null, hearingStage=null, listedDate=null, hearingNotes=null, hearingSelection=null, hearingActions=null, " +
                "hearingERMember=null, hearingEEMember=null, hearingDatesRequireAmending=null, hearingDateSelection=null, hearingDateActions=null, hearingStatus=null, " +
                "Postponed_by=null, hearingRoom=null, hearingClerk=null, hearingJudge=null, hearingCaseDisposed=null, hearingPartHeard=null, " +
                "hearingReservedJudgement=null, attendeeClaimant=null, attendeeNonAttendees=null, attendeeRespNoRep=null, attendeeRespAndRep=null, " +
                "attendeeRepOnly=null, hearingTimingStart=null, hearingTimingBreak=null, hearingTimingResume=null, hearingTimingFinish=null, " +
                "hearingTimingDuration=null, companyPremises=null, officeCT=null, reasonForCT=null, " +
                "relatedCaseCT=null, positionTypeCT=null, linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        referenceSubmitEvents.clear();
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEvents);
        CaseData caseDataResult = referenceService.fetchHearingVenueRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test(expected = Exception.class)
    public void fetchHearingVenueRefDataException() throws IOException {
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        referenceService.fetchHearingVenueRefData(caseDetails, "authToken");
    }

    @Test
    public void fetchDateListedRefDataWithAllRefDataPresent() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, " +
                "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, " +
                "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, " +
                "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, feeGroupReference=null, claimantWorkAddressQuestion=null, " +
                "claimantWorkAddressQRespondent=null, representativeClaimantType=null, " +
                "respondentCollection=null, repCollection=null, positionType=null, dateToPosition=null, currentPosition=null, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, " +
                "updateHearingDetails=null, hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, " +
                "depositCollection=null, judgementCollection=null, jurCodesCollection=null, " +
                "bfActions=null, clerkResponsible=null, userLocation=null, " +
                "documentCollection=null, additionalCaseInfoType=null, " +
                "correspondenceScotType=null, correspondenceType=null, addressLabelsSelectionType=null, addressLabelCollection=null, addressLabelsAttributesType=null, " +
                "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, allocatedOffice=null, " +
                "caseSource=null, conciliationTrack=null, counterClaim=null, eccCases=null, restrictedReporting=null, printHearingDetails=null, " +
                "printHearingCollection=null, targetHearingDate=null, claimant=null, respondent=null, EQP=null, flag1=null, flag2=null, docMarkUp=null, caseRefNumberCount=null, " +
                "startCaseRefNumber=null, multipleRefNumber=null, caseRefECC=null, respondentECC=null, ccdID=null, flagsImageFileName=null, flagsImageAltText=null, " +
                "hearingNumbers=null, hearingTypes=null, hearingPublicPrivate=null, " +
                "hearingVenue=DynamicFixedListType(value=DynamicValueType(code=Venue1, label=Venue1), listItems=[DynamicValueType(code=Venue1, label=Venue1), " +
                "DynamicValueType(code=Venue2, label=Venue2), DynamicValueType(code=Venue3, label=Venue3)]), hearingEstLengthNum=null, hearingEstLengthNumType=null, " +
                "hearingSitAlone=null, hearingStage=null, listedDate=null, hearingNotes=null, hearingSelection=null, hearingActions=null, " +
                "hearingERMember=null, hearingEEMember=null, hearingDatesRequireAmending=null, hearingDateSelection=null, hearingDateActions=null, hearingStatus=null, Postponed_by=null, " +
                "hearingRoom=DynamicFixedListType(value=DynamicValueType(code=Venue1, label=Venue1), listItems=[DynamicValueType(code=Venue1, label=Venue1), " +
                "DynamicValueType(code=Venue2, label=Venue2), DynamicValueType(code=Venue3, label=Venue3)]), " +
                "hearingClerk=DynamicFixedListType(value=DynamicValueType(code=First Name 4 Last Name 4, label=First Name 4 Last Name 4), " +
                "listItems=[DynamicValueType(code=First Name 4 Last Name 4, label=First Name 4 Last Name 4), DynamicValueType(code=First Name 5 Last Name 5, label=First Name 5 Last Name 5)]), " +
                "hearingJudge=DynamicFixedListType(value=DynamicValueType(code=Judge6, label=Judge6), listItems=[DynamicValueType(code=Judge6, label=Judge6)]), " +
                "hearingCaseDisposed=null, hearingPartHeard=null, " +
                "hearingReservedJudgement=null, attendeeClaimant=null, attendeeNonAttendees=null, attendeeRespNoRep=null, attendeeRespAndRep=null, " +
                "attendeeRepOnly=null, hearingTimingStart=null, hearingTimingBreak=null, hearingTimingResume=null, hearingTimingFinish=null, " +
                "hearingTimingDuration=null, companyPremises=null, officeCT=null, reasonForCT=null, " +
                "relatedCaseCT=null, positionTypeCT=null, linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEvents);
        CaseData caseDataResult = referenceService.fetchDateListedRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test
    public void fetchDateListedRefDataWithNoVenuesRefData() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, " +
                "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, " +
                "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, " +
                "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, feeGroupReference=null, claimantWorkAddressQuestion=null, " +
                "claimantWorkAddressQRespondent=null, representativeClaimantType=null, " +
                "respondentCollection=null, repCollection=null, positionType=null, dateToPosition=null, currentPosition=null, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, updateHearingDetails=null, hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, " +
                "depositCollection=null, judgementCollection=null, jurCodesCollection=null, " +
                "bfActions=null, clerkResponsible=null, userLocation=null, " +
                "documentCollection=null, additionalCaseInfoType=null, " +
                "correspondenceScotType=null, correspondenceType=null, addressLabelsSelectionType=null, addressLabelCollection=null, addressLabelsAttributesType=null, " +
                "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, allocatedOffice=null, " +
                "caseSource=null, conciliationTrack=null, counterClaim=null, eccCases=null, restrictedReporting=null, printHearingDetails=null, " +
                "printHearingCollection=null, targetHearingDate=null, claimant=null, respondent=null, EQP=null, flag1=null, flag2=null, docMarkUp=null, caseRefNumberCount=null, " +
                "startCaseRefNumber=null, multipleRefNumber=null, caseRefECC=null, respondentECC=null, ccdID=null, flagsImageFileName=null, flagsImageAltText=null, " +
                "hearingNumbers=null, hearingTypes=null, hearingPublicPrivate=null, hearingVenue=null, hearingEstLengthNum=null, " +
                "hearingEstLengthNumType=null, hearingSitAlone=null, hearingStage=null, listedDate=null, hearingNotes=null, hearingSelection=null, " +
                "hearingActions=null, hearingERMember=null, hearingEEMember=null, hearingDatesRequireAmending=null, hearingDateSelection=null, " +
                "hearingDateActions=null, hearingStatus=null, Postponed_by=null, hearingRoom=null, " +
                "hearingClerk=DynamicFixedListType(value=DynamicValueType(code=First Name 4 Last Name 4, label=First Name 4 Last Name 4), " +
                "listItems=[DynamicValueType(code=First Name 4 Last Name 4, label=First Name 4 Last Name 4), DynamicValueType(code=First Name 5 Last Name 5, label=First Name 5 Last Name 5)]), " +
                "hearingJudge=DynamicFixedListType(value=DynamicValueType(code=Judge6, label=Judge6), listItems=[DynamicValueType(code=Judge6, label=Judge6)]), " +
                "hearingCaseDisposed=null, hearingPartHeard=null, " +
                "hearingReservedJudgement=null, attendeeClaimant=null, attendeeNonAttendees=null, attendeeRespNoRep=null, attendeeRespAndRep=null, " +
                "attendeeRepOnly=null, hearingTimingStart=null, hearingTimingBreak=null, hearingTimingResume=null, hearingTimingFinish=null, " +
                "hearingTimingDuration=null, companyPremises=null, officeCT=null, reasonForCT=null, " +
                "relatedCaseCT=null, positionTypeCT=null, linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEventsNoVenues);
        CaseData caseDataResult = referenceService.fetchDateListedRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test
    public void fetchDateListedRefDataWithNoClerksRefData() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, " +
                "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, "
                + "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, "
                + "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, "
                + "claimantType=null, claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, feeGroupReference=null, "
                + "claimantWorkAddressQuestion=null, claimantWorkAddressQRespondent=null, "
                + "representativeClaimantType=null, respondentCollection=null, repCollection=null, positionType=null, "
                + "dateToPosition=null, currentPosition=null, fileLocation=null, fileLocationGlasgow=null, "
                + "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, updateHearingDetails=null, "
                + "hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, depositCollection=null, judgementCollection=null, jurCodesCollection=null, "
                + "bfActions=null, clerkResponsible=null, userLocation=null, documentCollection=null, "
                + "additionalCaseInfoType=null, correspondenceScotType=null, correspondenceType=null, "
                + "addressLabelsSelectionType=null, addressLabelCollection=null, addressLabelsAttributesType=null, "
                + "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, "
                + "allocatedOffice=null, caseSource=null, conciliationTrack=null, counterClaim=null, eccCases=null, "
                + "restrictedReporting=null, printHearingDetails=null, printHearingCollection=null, "
                + "targetHearingDate=null, claimant=null, respondent=null, EQP=null, flag1=null, flag2=null, "
                + "docMarkUp=null, caseRefNumberCount=null, startCaseRefNumber=null, multipleRefNumber=null, "
                + "caseRefECC=null, respondentECC=null, ccdID=null, flagsImageFileName=null, flagsImageAltText=null, "
                + "hearingNumbers=null, hearingTypes=null, hearingPublicPrivate=null, "
                + "hearingVenue=DynamicFixedListType(value=DynamicValueType(code=Venue1, label=Venue1), "
                + "listItems=[DynamicValueType(code=Venue1, label=Venue1), DynamicValueType(code=Venue2, label=Venue2), "
                + "DynamicValueType(code=Venue3, label=Venue3)]), hearingEstLengthNum=null, hearingEstLengthNumType=null, "
                + "hearingSitAlone=null, hearingStage=null, listedDate=null, hearingNotes=null, hearingSelection=null, hearingActions=null, "
                +"hearingERMember=null, hearingEEMember=null, hearingDatesRequireAmending=null, hearingDateSelection=null, hearingDateActions=null, hearingStatus=null, Postponed_by=null, " +
                "hearingRoom=DynamicFixedListType(value=DynamicValueType(code=Venue1, label=Venue1), listItems=[DynamicValueType(code=Venue1, label=Venue1), " +
                "DynamicValueType(code=Venue2, label=Venue2), DynamicValueType(code=Venue3, label=Venue3)]), hearingClerk=null, " +
                "hearingJudge=DynamicFixedListType(value=DynamicValueType(code=Judge6, label=Judge6), listItems=[DynamicValueType(code=Judge6, label=Judge6)]), " +
                "hearingCaseDisposed=null, hearingPartHeard=null, " +
                "hearingReservedJudgement=null, attendeeClaimant=null, attendeeNonAttendees=null, attendeeRespNoRep=null, attendeeRespAndRep=null, " +
                "attendeeRepOnly=null, hearingTimingStart=null, hearingTimingBreak=null, hearingTimingResume=null, hearingTimingFinish=null, " +
                "hearingTimingDuration=null, companyPremises=null, officeCT=null, reasonForCT=null, " +
                "relatedCaseCT=null, positionTypeCT=null, linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEventsNoClerks);
        CaseData caseDataResult = referenceService.fetchDateListedRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test
    public void fetchDateListedRefDataWithNoJudgesRefData() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, " +
                "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, " +
                "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, " +
                "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, feeGroupReference=null, claimantWorkAddressQuestion=null, " +
                "claimantWorkAddressQRespondent=null, representativeClaimantType=null, " +
                "respondentCollection=null, repCollection=null, positionType=null, dateToPosition=null, "
                + "currentPosition=null, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, updateHearingDetails=null, hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, " +
                "depositCollection=null, judgementCollection=null, jurCodesCollection=null, " +
                "bfActions=null, clerkResponsible=null, userLocation=null, " +
                "documentCollection=null, additionalCaseInfoType=null, " +
                "correspondenceScotType=null, correspondenceType=null, addressLabelsSelectionType=null, "
                + "addressLabelCollection=null, addressLabelsAttributesType=null, " +
                "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, allocatedOffice=null, " +
                "caseSource=null, conciliationTrack=null, counterClaim=null, eccCases=null, restrictedReporting=null, printHearingDetails=null, " +
                "printHearingCollection=null, targetHearingDate=null, claimant=null, respondent=null, EQP=null, flag1=null, flag2=null, docMarkUp=null, caseRefNumberCount=null, " +
                "startCaseRefNumber=null, multipleRefNumber=null, caseRefECC=null, respondentECC=null, ccdID=null, flagsImageFileName=null, flagsImageAltText=null, " +
                "hearingNumbers=null, hearingTypes=null, hearingPublicPrivate=null, " +
                "hearingVenue=DynamicFixedListType(value=DynamicValueType(code=Venue1, label=Venue1), listItems=[DynamicValueType(code=Venue1, label=Venue1), " +
                "DynamicValueType(code=Venue2, label=Venue2), DynamicValueType(code=Venue3, label=Venue3)]), hearingEstLengthNum=null, hearingEstLengthNumType=null, " +
                "hearingSitAlone=null, hearingStage=null, listedDate=null, hearingNotes=null, hearingSelection=null, hearingActions=null, " +
                "hearingERMember=null, hearingEEMember=null, hearingDatesRequireAmending=null, hearingDateSelection=null, hearingDateActions=null, hearingStatus=null, Postponed_by=null, " +
                "hearingRoom=DynamicFixedListType(value=DynamicValueType(code=Venue1, label=Venue1), listItems=[DynamicValueType(code=Venue1, label=Venue1), " +
                "DynamicValueType(code=Venue2, label=Venue2), DynamicValueType(code=Venue3, label=Venue3)]), " +
                "hearingClerk=DynamicFixedListType(value=DynamicValueType(code=First Name 4 Last Name 4, label=First Name 4 Last Name 4), " +
                "listItems=[DynamicValueType(code=First Name 4 Last Name 4, label=First Name 4 Last Name 4), DynamicValueType(code=First Name 5 Last Name 5, label=First Name 5 Last Name 5)]), " +
                "hearingJudge=null, hearingCaseDisposed=null, hearingPartHeard=null, " +
                "hearingReservedJudgement=null, attendeeClaimant=null, attendeeNonAttendees=null, attendeeRespNoRep=null, attendeeRespAndRep=null, " +
                "attendeeRepOnly=null, hearingTimingStart=null, hearingTimingBreak=null, hearingTimingResume=null, hearingTimingFinish=null, " +
                "hearingTimingDuration=null, companyPremises=null, officeCT=null, reasonForCT=null, " +
                "relatedCaseCT=null, positionTypeCT=null, linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEventsNoJudges);
        CaseData caseDataResult = referenceService.fetchDateListedRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test
    public void fetchDateListedRefDataWithNoReferenceData() throws IOException {
        String result = "CaseData(tribunalCorrespondenceAddress=null, " +
                "tribunalCorrespondenceTelephone=null, tribunalCorrespondenceFax=null, tribunalCorrespondenceDX=null, " +
                "tribunalCorrespondenceEmail=null, ethosCaseReference=null, ecmCaseType=null, " +
                "multipleReference=null, multipleReferenceLinkMarkUp=null, parentMultipleCaseId=null, subMultipleName=null, leadClaimant=null, "
                + "multipleFlag=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, claimServedDate=null, feeGroupReference=null, claimantWorkAddressQuestion=null, " +
                "claimantWorkAddressQRespondent=null, representativeClaimantType=null, " +
                "respondentCollection=null, repCollection=null, positionType=null, dateToPosition=null, currentPosition=null, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, updateHearingDetails=null, hearingCollection=null, hearingsCollectionForUpdate=[], selectedHearingNumberForUpdate=null, hearingUpdateFilterType=null, " +
                "depositCollection=null, judgementCollection=null, jurCodesCollection=null, " +
                "bfActions=null, clerkResponsible=null, userLocation=null, " +
                "documentCollection=null, additionalCaseInfoType=null, " +
                "correspondenceScotType=null, correspondenceType=null, addressLabelsSelectionType=null, addressLabelCollection=null, addressLabelsAttributesType=null, " +
                "caseNotes=null, nextListedDate=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, managingOffice=null, allocatedOffice=null, " +
                "caseSource=null, conciliationTrack=null, counterClaim=null, eccCases=null, restrictedReporting=null, printHearingDetails=null, " +
                "printHearingCollection=null, targetHearingDate=null, claimant=null, respondent=null, EQP=null, flag1=null, flag2=null, docMarkUp=null, caseRefNumberCount=null, " +
                "startCaseRefNumber=null, multipleRefNumber=null, caseRefECC=null, respondentECC=null, ccdID=null, flagsImageFileName=null, flagsImageAltText=null, " +
                "hearingNumbers=null, hearingTypes=null, hearingPublicPrivate=null, " +
                "hearingVenue=null, hearingEstLengthNum=null, hearingEstLengthNumType=null, " +
                "hearingSitAlone=null, hearingStage=null, listedDate=null, hearingNotes=null, hearingSelection=null, hearingActions=null, " +
                "hearingERMember=null, hearingEEMember=null, hearingDatesRequireAmending=null, hearingDateSelection=null, hearingDateActions=null, hearingStatus=null, Postponed_by=null, " +
                "hearingRoom=null, hearingClerk=null, hearingJudge=null, hearingCaseDisposed=null, hearingPartHeard=null, " +
                "hearingReservedJudgement=null, attendeeClaimant=null, attendeeNonAttendees=null, attendeeRespNoRep=null, attendeeRespAndRep=null, " +
                "attendeeRepOnly=null, hearingTimingStart=null, hearingTimingBreak=null, hearingTimingResume=null, hearingTimingFinish=null, " +
                "hearingTimingDuration=null, companyPremises=null, officeCT=null, reasonForCT=null, " +
                "relatedCaseCT=null, positionTypeCT=null, linkedCaseCT=null, transferredCaseLink=null, transferredCaseLinkSourceCaseId=null, transferredCaseLinkSourceCaseTypeId=null, stateAPI=null, bundleConfiguration=null, caseBundles=null, digitalCaseFile=null, acasCertificate=null, adrDocumentCollection=null, piiDocumentCollection=null, appealDocumentCollection=null, addDocumentCollection=null)";
        referenceSubmitEvents.clear();
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenReturn(referenceSubmitEvents);
        CaseData caseDataResult = referenceService.fetchDateListedRefData(caseDetails, "authToken");
        assertEquals(result, caseDataResult.toString());
    }

    @Test(expected = Exception.class)
    public void fetchDateListedRefDataException() throws IOException {
        when(ccdClient.retrieveReferenceDataCases(anyString(), anyString(), anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        referenceService.fetchDateListedRefData(caseDetails, "authToken");
    }

}
