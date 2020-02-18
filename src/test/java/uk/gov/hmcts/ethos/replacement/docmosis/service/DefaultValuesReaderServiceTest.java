package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultValuesReaderServiceTest {

    @InjectMocks
    private DefaultValuesReaderService defaultValuesReaderService;

    private DefaultValues preDefaultValues;
    private DefaultValues postDefaultValuesManchester;
    private DefaultValues postDefaultValuesGlasgow;
    private DefaultValues postDefaultValuesAberdeen;
    private DefaultValues postDefaultValuesDundee;
    private DefaultValues postDefaultValuesEdinburgh;
    private DefaultValues postDefaultValuesBristol;
    private DefaultValues postDefaultValuesLeeds;
    private DefaultValues postDefaultValuesLondonCentral;
    private DefaultValues postDefaultValuesLondonEast;
    private DefaultValues postDefaultValuesLondonSouth;
    private DefaultValues postDefaultValuesMidlandsEast;
    private DefaultValues postDefaultValuesMidlandsWest;
    private DefaultValues postDefaultValuesNewcastle;
    private DefaultValues postDefaultValuesWales;
    private DefaultValues postDefaultValuesWatford;
    private CaseData caseData;
    private CaseDetails manchesterCaseDetails;
    private CaseDetails glasgowCaseDetails;
    private CaseDetails bristolCaseDetails;
    private CaseDetails leedsCaseDetails;
    private CaseDetails londonCentralCaseDetails;
    private CaseDetails londonEastCaseDetails;
    private CaseDetails londonSouthCaseDetails;
    private CaseDetails midlandsEastCaseDetails;
    private CaseDetails midlandsWestCaseDetails;
    private CaseDetails newcastleCaseDetails;
    private CaseDetails walesCaseDetails;
    private CaseDetails watfordCaseDetails;
    private ListingData listingData;

    private CaseDetails getCaseDetails(String caseTypeId) {
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(new CaseData());
        caseDetails.setCaseId("123456");
        caseDetails.setCaseTypeId(caseTypeId);
        caseDetails.setJurisdiction("TRIBUNALS");
        return caseDetails;
    }

    private ListingData getListingDataSetUp() {
        listingData = new ListingData();
        listingData.setTribunalCorrespondenceDX("DX");
        listingData.setTribunalCorrespondenceEmail("m@m.com");
        listingData.setTribunalCorrespondenceFax("100300200");
        listingData.setTribunalCorrespondenceTelephone("077123123");
        Address address = new Address();
        address.setAddressLine1("AddressLine1");
        address.setAddressLine2("AddressLine2");
        address.setAddressLine3("AddressLine3");
        address.setPostTown("Manchester");
        address.setCountry("UK");
        address.setPostCode("L1 122");
        listingData.setTribunalCorrespondenceAddress(address);
        return listingData;
    }

    @Before
    public void setUp() {
        listingData = getListingDataSetUp();
        manchesterCaseDetails = getCaseDetails(MANCHESTER_CASE_TYPE_ID);
        glasgowCaseDetails = getCaseDetails(SCOTLAND_CASE_TYPE_ID);
        bristolCaseDetails = getCaseDetails(BRISTOL_USERS_CASE_TYPE_ID);
        leedsCaseDetails = getCaseDetails(LEEDS_USERS_CASE_TYPE_ID);
        londonCentralCaseDetails = getCaseDetails(LONDON_CENTRAL_USERS_CASE_TYPE_ID);
        londonEastCaseDetails = getCaseDetails(LONDON_EAST_USERS_CASE_TYPE_ID);
        londonSouthCaseDetails = getCaseDetails(LONDON_SOUTH_USERS_CASE_TYPE_ID);
        midlandsEastCaseDetails = getCaseDetails(MIDLANDS_EAST_USERS_CASE_TYPE_ID);
        midlandsWestCaseDetails = getCaseDetails(MIDLANDS_WEST_USERS_CASE_TYPE_ID);
        newcastleCaseDetails = getCaseDetails(NEWCASTLE_USERS_CASE_TYPE_ID);
        walesCaseDetails = getCaseDetails(WALES_USERS_CASE_TYPE_ID);
        watfordCaseDetails = getCaseDetails(WATFORD_USERS_CASE_TYPE_ID);
        preDefaultValues = DefaultValues.builder().claimantTypeOfClaimant(INDIVIDUAL_TYPE_CLAIMANT).build();
        postDefaultValuesManchester = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Manchester Employment Tribunal")
                .tribunalCorrespondenceAddressLine2("Alexandra House")
                .tribunalCorrespondenceAddressLine3("14-22 The Parsonage")
                .tribunalCorrespondenceTown("Manchester")
                .tribunalCorrespondencePostCode("M3 2JA")
                .tribunalCorrespondenceTelephone("0161 833 6100")
                .tribunalCorrespondenceFax("0870 739 4433")
                .tribunalCorrespondenceDX("DX 743570")
                .tribunalCorrespondenceEmail("Manchesteret@justice.gov.uk")
                .build();
        postDefaultValuesBristol = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Bristol Civil and Family Justice Centre")
                .tribunalCorrespondenceAddressLine2("2 Redcliff Street")
                .tribunalCorrespondenceTown("Bristol")
                .tribunalCorrespondencePostCode("BS1 6GR")
                .tribunalCorrespondenceTelephone("0117 929 8261")
                .tribunalCorrespondenceFax("0870 739 4009")
                .tribunalCorrespondenceDX("DX 95903 Bristol 3")
                .tribunalCorrespondenceEmail("bristolet@justice.gov.uk")
                .build();
        postDefaultValuesLeeds = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("4th Floor")
                .tribunalCorrespondenceAddressLine2("City Exchange")
                .tribunalCorrespondenceAddressLine3("11 Albion Street")
                .tribunalCorrespondenceTown("Leeds")
                .tribunalCorrespondencePostCode("LS1 5ES")
                .tribunalCorrespondenceTelephone("0113 245 9741")
                .tribunalCorrespondenceFax("01264 785136")
                .tribunalCorrespondenceEmail("LeedsET@justice.gov.uk")
                .build();
        postDefaultValuesLondonCentral = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Ground Floor")
                .tribunalCorrespondenceAddressLine2("Victory House")
                .tribunalCorrespondenceAddressLine3("30-34 Kingsway")
                .tribunalCorrespondenceTown("London")
                .tribunalCorrespondencePostCode("WC2B 6EX")
                .tribunalCorrespondenceTelephone("0207 273 8603")
                .tribunalCorrespondenceFax("01264 785 100")
                .tribunalCorrespondenceDX("DX 141420 Bloomsbury")
                .tribunalCorrespondenceEmail("londoncentralet@hmcts.gsi.gov.uk")
                .build();
        postDefaultValuesLondonEast = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("2nd Floor")
                .tribunalCorrespondenceAddressLine2("Import Building")
                .tribunalCorrespondenceAddressLine3("2 Clove Crescent")
                .tribunalCorrespondenceTown("London")
                .tribunalCorrespondencePostCode("E14 2BE")
                .tribunalCorrespondenceTelephone("0207 538 6161")
                .tribunalCorrespondenceFax("08703 240 200")
                .tribunalCorrespondenceEmail("eastlondon@justice.gov.uk")
                .build();
        postDefaultValuesLondonSouth = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Montague Court")
                .tribunalCorrespondenceAddressLine2("101 London Road")
                .tribunalCorrespondenceAddressLine3("West Croydon")
                .tribunalCorrespondenceTown("London")
                .tribunalCorrespondencePostCode("CR0 2RF")
                .tribunalCorrespondenceTelephone("0208 667 9131")
                .tribunalCorrespondenceFax("0870 324 0174")
                .tribunalCorrespondenceDX("DX 155061 Croydon 39")
                .tribunalCorrespondenceEmail("londonsouthet@hmcts.gsi.gov.uk")
                .build();
        postDefaultValuesMidlandsEast = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Nottingham Justice Centre")
                .tribunalCorrespondenceAddressLine2("Carrington Street")
                .tribunalCorrespondenceTown("Nottingham")
                .tribunalCorrespondencePostCode("NG2 1EE")
                .tribunalCorrespondenceTelephone("0115 947 5701")
                .tribunalCorrespondenceDX("DX 719030 Nottingham 32")
                .tribunalCorrespondenceEmail("e.midlandseastet@justice.gov.uk")
                .build();
        postDefaultValuesMidlandsWest = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("13th Floor")
                .tribunalCorrespondenceAddressLine2("Centre City Tower")
                .tribunalCorrespondenceAddressLine3("5-7 Hill Street")
                .tribunalCorrespondenceTown("Birmingham")
                .tribunalCorrespondencePostCode("B5 4UU")
                .tribunalCorrespondenceTelephone("0121 600 7780")
                .tribunalCorrespondenceFax("01264 347 999")
                .tribunalCorrespondenceEmail("MidlandsWestET@justice.gov.uk")
                .build();
        postDefaultValuesNewcastle = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Kings Court")
                .tribunalCorrespondenceAddressLine2("Earl Grey Way")
                .tribunalCorrespondenceAddressLine3("Royal Quays")
                .tribunalCorrespondenceTown("North Shields")
                .tribunalCorrespondencePostCode("NE29 6AR")
                .tribunalCorrespondenceTelephone("0191 260 6900")
                .tribunalCorrespondenceFax("0870 739 4206")
                .tribunalCorrespondenceDX("DX 65137 North Shields 2")
                .tribunalCorrespondenceEmail("newcastleet@hmcts.gsi.gov.uk")
                .build();
        postDefaultValuesWales = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Fitzalan Place")
                .tribunalCorrespondenceTown("Cardiff")
                .tribunalCorrespondencePostCode("CF24 0RZ")
                .tribunalCorrespondenceTelephone("0292 067 8100")
                .tribunalCorrespondenceFax("0870 761 7635")
                .tribunalCorrespondenceDX("DX 743942 Caerdydd/Cardiff 38")
                .tribunalCorrespondenceEmail("cardiffet@justice.gov.uk")
                .build();
        postDefaultValuesWatford = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Watford Tribunal Hearing Centre")
                .tribunalCorrespondenceAddressLine2("Radius House")
                .tribunalCorrespondenceAddressLine3("51 Clarendon Road")
                .tribunalCorrespondenceTown("Watford")
                .tribunalCorrespondencePostCode("WD17 1HP")
                .tribunalCorrespondenceTelephone("0192 328 1750")
                .tribunalCorrespondenceFax("0870 324 0174")
                .tribunalCorrespondenceDX("DX 155650 Watford 3")
                .tribunalCorrespondenceEmail("watfordet@justice.gov.uk")
                .build();
        postDefaultValuesGlasgow = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Eagle Building")
                .tribunalCorrespondenceAddressLine2("215 Bothwell Street")
                .tribunalCorrespondenceTown("Glasgow")
                .tribunalCorrespondencePostCode("G2 7TS")
                .tribunalCorrespondenceTelephone("0141 204 0730")
                .tribunalCorrespondenceFax("01264 785 177")
                .tribunalCorrespondenceDX("DX 580003")
                .tribunalCorrespondenceEmail("glasgowet@justice.gov.uk")
                .managingOffice(GLASGOW_OFFICE)
                .build();
        postDefaultValuesAberdeen = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Ground Floor")
                .tribunalCorrespondenceAddressLine2("AB1, 48 Huntly Street")
                .tribunalCorrespondenceTown("Aberdeen")
                .tribunalCorrespondencePostCode("AB10 1SH")
                .tribunalCorrespondenceTelephone("01224 593 137")
                .tribunalCorrespondenceFax("0870 761 7766")
                .tribunalCorrespondenceDX("DX AB77")
                .tribunalCorrespondenceEmail("aberdeenet@justice.gov.uk")
                .build();
        postDefaultValuesDundee = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("Ground Floor")
                .tribunalCorrespondenceAddressLine2("Block C, Caledonian House")
                .tribunalCorrespondenceAddressLine3("Greenmarket")
                .tribunalCorrespondenceTown("Dundee")
                .tribunalCorrespondencePostCode("DD1 4QG")
                .tribunalCorrespondenceTelephone("01382 221 578")
                .tribunalCorrespondenceFax("01382 227 136")
                .tribunalCorrespondenceDX("DX DD51")
                .tribunalCorrespondenceEmail("dundeeet@justice.gov.uk")
                .build();
        postDefaultValuesEdinburgh = DefaultValues.builder()
                .positionType(MANUALLY_CREATED_POSITION)
                .caseType(SINGLE_CASE_TYPE)
                .tribunalCorrespondenceAddressLine1("54-56 Melville Street")
                .tribunalCorrespondenceTown("Edinburgh")
                .tribunalCorrespondencePostCode("EH3 7HF")
                .tribunalCorrespondenceTelephone("0131 226 5584")
                .tribunalCorrespondenceFax("0131 220 6847")
                .tribunalCorrespondenceDX("DX ED147")
                .tribunalCorrespondenceEmail("edinburghet@justice.gov.uk")
                .build();
        caseData = new CaseData();

    }

    @Test
    public void getPreDefaultValues() {
        DefaultValues preDefaultValues1 = defaultValuesReaderService.getDefaultValues(PRE_DEFAULT_XLSX_FILE_PATH, "", glasgowCaseDetails.getCaseTypeId());
        assertEquals(preDefaultValues, preDefaultValues1);
    }

    @Test
    public void getManchesterPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", manchesterCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesManchester, postDefaultValues1);
    }

    @Test
    public void getBristolPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", bristolCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesBristol, postDefaultValues1);
    }

    @Test
    public void getLeedsPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", leedsCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesLeeds, postDefaultValues1);
    }

    @Test
    public void getLondonCentralPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", londonCentralCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesLondonCentral, postDefaultValues1);
    }

    @Test
    public void getLondonEastPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", londonEastCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesLondonEast, postDefaultValues1);
    }

    @Test
    public void getLondonSouthPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", londonSouthCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesLondonSouth, postDefaultValues1);
    }

    @Test
    public void getMidlandsEastPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", midlandsEastCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesMidlandsEast, postDefaultValues1);
    }

    @Test
    public void getMidlandsWestPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", midlandsWestCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesMidlandsWest, postDefaultValues1);
    }

    @Test
    public void getNewcastlePostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", newcastleCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesNewcastle, postDefaultValues1);
    }

    @Test
    public void getWalesPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", walesCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesWales, postDefaultValues1);
    }

    @Test
    public void getWatfordPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", watfordCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesWatford, postDefaultValues1);
    }

    @Test
    public void getGlasgowDefaultPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, "", glasgowCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesGlasgow, postDefaultValues1);
    }

    @Test
    public void getCaseData() {
        String caseDataExpected = "CaseData(tribunalCorrespondenceAddress=Eagle Building, 215 Bothwell Street, Glasgow, G2 7TS, " +
                "tribunalCorrespondenceTelephone=0141 204 0730, tribunalCorrespondenceFax=01264 785 177, tribunalCorrespondenceDX=DX 580003, " +
                "tribunalCorrespondenceEmail=glasgowet@justice.gov.uk, ethosCaseReference=null, caseType=Single, " +
                "multipleReference=null, leadClaimant=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, feeGroupReference=null, claimantWorkAddressQuestion=null, " +
                "claimantWorkAddressQRespondent=null, representativeClaimantType=null, responseTypeCollection=null, responseType=null, " +
                "respondentCollection=null, repCollection=null, positionType=Manually Created, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, hearingType=null, hearingCollection=null, " +
                "depositType=null, judgementCollection=null, judgementDetailsCollection=null, costsCollection=null, jurCodesCollection=null, " +
                "broughtForwardCollection=null, clerkResponsible=null, userLocation=null, subMultipleReference=null, " +
                "addSubMultipleComment=null, documentCollection=null, referredToJudge=null, backFromJudge=null, additionalCaseInfoType=null, " +
                "correspondenceScotType=null, correspondenceType=null, caseNotes=null, claimantWorkAddress=null, " +
                "claimantRepresentedQuestion=null, bulkCaseReferenceNumber=null, managingOffice=Glasgow, allocatedOffice=null, caseSource=Manually Created, " +
                "state=null, stateAPI=null, et3Received=null, conciliationTrack=null, counterClaim=null, restrictedReporting=null, printHearingDetails=null, " +
                "printHearingCollection=null, targetHearingDate=null, EQP=null, flag1=null, flag2=null, docMarkUp=null)";
        assertEquals(caseDataExpected, defaultValuesReaderService.getCaseData(caseData, postDefaultValuesGlasgow, true).toString());
    }

    private CaseData getCaseDataWithClaimantWorkAddress(CaseData caseData) {
        Address address = new Address();
        address.setAddressLine1("Line1");
        address.setPostCode("PostCode");
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Andrew Smith");
        respondentSumType.setRespondentAddress(address);
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));
        caseData.setClaimantWorkAddressQuestion("Yes");
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setLabel("PostCode");
        dynamicValueType.setCode("PostCode");
        dynamicFixedListType.setValue(dynamicValueType);
        caseData.setClaimantWorkAddressQRespondent(dynamicFixedListType);
        return caseData;
    }
    @Test
    public void getCaseDataWithClaimantWorkAddress() {
        String caseDataExpected = "CaseData(tribunalCorrespondenceAddress=Eagle Building, 215 Bothwell Street, Glasgow, G2 7TS, tribunalCorrespondenceTelephone=0141 " +
                "204 0730, tribunalCorrespondenceFax=01264 785 177, tribunalCorrespondenceDX=DX 580003, tribunalCorrespondenceEmail=glasgowet@justice.gov.uk, " +
                "ethosCaseReference=null, caseType=Single, multipleReference=null, leadClaimant=null, claimantTypeOfClaimant=null, claimantCompany=null, " +
                "claimantIndType=null, claimantType=null, claimantOtherType=null, preAcceptCase=null, receiptDate=null, feeGroupReference=null, " +
                "claimantWorkAddressQuestion=Yes, claimantWorkAddressQRespondent=null, representativeClaimantType=null, responseTypeCollection=null, responseType=null," +
                " respondentCollection=[RespondentSumTypeItem(id=null, value=RespondentSumType(responseStatus=null, responseToClaim=null, rejectionReason=null, " +
                "rejectionReasonOther=null, responseOutOfTime=null, responseNotOnPrescribedForm=null, responseRequiredInfoAbsent=null, responseNotes=null, " +
                "response_ReferredToJudge=null, responseReturnedFromJudge=null, respondentName=Andrew Smith, respondentACASQuestion=null, respondentACAS=null, " +
                "respondentACASNo=null, respondentAddress=Line1, PostCode, respondentPhone1=null, respondentPhone2=null, respondentEmail=null, " +
                "respondentContactPreference=null, responseReceivedDate=null, responseRespondentNameQuestion=null, responseRespondentName=null, " +
                "responseCounterClaim=null))], repCollection=null, positionType=Manually Created, fileLocation=null, fileLocationGlasgow=null, " +
                "fileLocationAberdeen=null, fileLocationDundee=null, fileLocationEdinburgh=null, hearingType=null, hearingCollection=null, depositType=null, " +
                "judgementCollection=null, judgementDetailsCollection=null, costsCollection=null, jurCodesCollection=null, broughtForwardCollection=null, " +
                "clerkResponsible=null, userLocation=null, subMultipleReference=null, addSubMultipleComment=null, documentCollection=null, referredToJudge=null, " +
                "backFromJudge=null, additionalCaseInfoType=null, correspondenceScotType=null, correspondenceType=null, caseNotes=null, " +
                "claimantWorkAddress=ClaimantWorkAddressType(claimantWorkAddress=null, claimantWorkPhoneNumber=null), claimantRepresentedQuestion=null, " +
                "bulkCaseReferenceNumber=null, managingOffice=Glasgow, allocatedOffice=null, caseSource=Manually Created, state=null, stateAPI=null, et3Received=null, " +
                "conciliationTrack=null, counterClaim=null, restrictedReporting=null, printHearingDetails=null, printHearingCollection=null, targetHearingDate=null, " +
                "EQP=null, flag1=null, flag2=null, docMarkUp=null)";
        assertEquals(caseDataExpected, defaultValuesReaderService.getCaseData(getCaseDataWithClaimantWorkAddress(caseData), postDefaultValuesGlasgow, true).toString());
    }

    @Test
    public void getGlasgowOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(GLASGOW_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, GLASGOW_OFFICE, glasgowCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesGlasgow.toString(), postDefaultValues1.toString());
    }

    @Test
    public void getAberdeenOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(ABERDEEN_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, ABERDEEN_OFFICE, glasgowCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesAberdeen.toString(), postDefaultValues1.toString());
    }

    @Test
    public void getDundeeOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(DUNDEE_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, DUNDEE_OFFICE, glasgowCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesDundee, postDefaultValues1);
    }

    @Test
    public void getEdinburghOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(EDINBURGH_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, EDINBURGH_OFFICE, glasgowCaseDetails.getCaseTypeId());
        assertEquals(postDefaultValuesEdinburgh, postDefaultValues1);
    }

    @Test
    public void getListingData() {
        String listingDataExpected = "ListingData(tribunalCorrespondenceAddress=Eagle Building, 215 Bothwell Street, Glasgow, G2 7TS, " +
                "tribunalCorrespondenceTelephone=0141 204 0730, tribunalCorrespondenceFax=01264 785 177, tribunalCorrespondenceDX=DX 580003, " +
                "tribunalCorrespondenceEmail=glasgowet@justice.gov.uk, hearingDateType=null, listingDate=null, listingDateFrom=null, " +
                "listingDateTo=null, listingVenue=null, listingCollection=null, listingVenueOfficeGlas=null, listingVenueOfficeAber=null, " +
                "hearingDocType=null, hearingDocETCL=null, roomOrNoRoom=null, docMarkUp=null)";
        assertEquals(listingDataExpected, defaultValuesReaderService.getListingData(listingData, postDefaultValuesGlasgow).toString());
    }
}