package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;

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
    private CaseData caseData;
    private CaseDetails manchesterCaseDetails;
    private CaseDetails glasgowCaseDetails;

    @Before
    public void setUp() {
        manchesterCaseDetails = new CaseDetails();
        manchesterCaseDetails.setCaseData(new CaseData());
        manchesterCaseDetails.setCaseId("123456");
        manchesterCaseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        manchesterCaseDetails.setJurisdiction("TRIBUNALS");
        glasgowCaseDetails = new CaseDetails();
        glasgowCaseDetails.setCaseData(new CaseData());
        glasgowCaseDetails.setCaseId("123456");
        glasgowCaseDetails.setCaseTypeId(SCOTLAND_CASE_TYPE_ID);
        glasgowCaseDetails.setJurisdiction("TRIBUNALS");
        preDefaultValues = DefaultValues.builder().claimantTypeOfClaimant("Individual").build();
        postDefaultValuesManchester = DefaultValues.builder()
                .positionType("Manually Created")
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
        postDefaultValuesGlasgow = DefaultValues.builder()
                .positionType("Manually Created")
                .tribunalCorrespondenceAddressLine1("Eagle Building")
                .tribunalCorrespondenceAddressLine2("215 Bothwell Street")
                .tribunalCorrespondenceTown("Glasgow")
                .tribunalCorrespondencePostCode("G2 7TS")
                .tribunalCorrespondenceTelephone("0141 204 0730")
                .tribunalCorrespondenceFax("01264 785 177")
                .tribunalCorrespondenceDX("DX 580003")
                .tribunalCorrespondenceEmail("glasgowet@justice.gov.uk")
                .managingOffice("Glasgow")
                .build();
        postDefaultValuesAberdeen = DefaultValues.builder()
                .positionType("Manually Created")
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
                .positionType("Manually Created")
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
                .positionType("Manually Created")
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
        DefaultValues preDefaultValues1 = defaultValuesReaderService.getDefaultValues(PRE_DEFAULT_XLSX_FILE_PATH, glasgowCaseDetails);
        assertEquals(preDefaultValues, preDefaultValues1);
    }

    @Test
    public void getManchesterPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, manchesterCaseDetails);
        assertEquals(postDefaultValuesManchester, postDefaultValues1);
    }

    @Test
    public void getGlasgowDefaultPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, glasgowCaseDetails);
        assertEquals(postDefaultValuesGlasgow, postDefaultValues1);
    }

    @Test
    public void getCaseData() {
        String caseDataExpected = "CaseData(tribunalCorrespondenceAddress=Eagle Building, 215 Bothwell Street, Glasgow, G2 7TS, " +
                "tribunalCorrespondenceTelephone=0141 204 0730, tribunalCorrespondenceFax=01264 785 177, tribunalCorrespondenceDX=DX 580003, " +
                "tribunalCorrespondenceEmail=glasgowet@justice.gov.uk, ethosCaseReference=null, caseType=null, multipleType=null, multipleOthers=null, " +
                "multipleReference=null, leadClaimant=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, feeGroupReference=null, claimantWorkAddressQuestion=null, " +
                "representativeClaimantType=null, responseTypeCollection=null, responseType=null, respondentCollection=null, repCollection=null, " +
                "positionType=Manually Created, fileLocation=null, hearingType=null, hearingCollection=null, depositType=null, judgementCollection=null, " +
                "judgementDetailsCollection=null, costsCollection=null, disposeType=null, NH_JudgementType=null, jurCodesCollection=null, " +
                "broughtForwardCollection=null, acasOffice=null, clerkResponsible=null, userLocation=null, subMultipleReference=null, " +
                "addSubMultipleComment=null, panelCollection=null, documentCollection=null, referToETJ=null, withdrawType=null, archiveType=null, " +
                "referredToJudge=null, backFromJudge=null, additionalType=null, reconsiderationType=null, reconsiderationCollection=null, correspondenceType=null, " +
                "correspondenceScotType=null, caseNotes=null, caseDocument=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, " +
                "state=null, stateAPI=null, managingOffice=Glasgow, conciliationTrack=null)";
        assertEquals(caseDataExpected, defaultValuesReaderService.getCaseData(caseData, postDefaultValuesGlasgow).toString());
    }

    @Test
    public void getGlasgowOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(GLASGOW_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, glasgowCaseDetails);
        assertEquals(postDefaultValuesGlasgow.toString(), postDefaultValues1.toString());
    }

    @Test
    public void getAberdeenOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(ABERDEEN_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, glasgowCaseDetails);
        assertEquals(postDefaultValuesAberdeen.toString(), postDefaultValues1.toString());
    }

    @Test
    public void getDundeeOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(DUNDEE_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, glasgowCaseDetails);
        assertEquals(postDefaultValuesDundee, postDefaultValues1);
    }

    @Test
    public void getEdinburghOfficePostDefaultValues() {
        glasgowCaseDetails.getCaseData().setManagingOffice(EDINBURGH_OFFICE);
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, glasgowCaseDetails);
        assertEquals(postDefaultValuesEdinburgh, postDefaultValues1);
    }
}