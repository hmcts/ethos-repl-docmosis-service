package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
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
    private CaseData caseData;

    @Before
    public void setUp() {
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
        caseData = new CaseData();

    }

    @Test
    public void getPreDefaultValues() {
        DefaultValues preDefaultValues1 = defaultValuesReaderService.getDefaultValues(PRE_DEFAULT_XLSX_FILE_PATH, GLASGOW_CASE_TYPE_ID);
        assertEquals(preDefaultValues, preDefaultValues1);
    }

    @Test
    public void getManchesterPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, MANCHESTER_CASE_TYPE_ID);
        assertEquals(postDefaultValuesManchester, postDefaultValues1);
    }

    @Test
    public void getGlasgowPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, GLASGOW_CASE_TYPE_ID);
        assertEquals(postDefaultValuesGlasgow, postDefaultValues1);
    }

    @Test
    public void getCaseData() {
        String caseDataExpected = "CaseData(tribunalCorrespondenceAddress=Eagle Building, 215 Bothwell Street, Glasgow, G2 7TS, " +
                "tribunalCorrespondenceTelephone=0141 204 0730, tribunalCorrespondenceFax=01264 785 177, " +
                "tribunalCorrespondenceDX=DX 580003, tribunalCorrespondenceEmail=glasgowet@justice.gov.uk, " +
                "ethosCaseReference=null, caseType=null, multipleType=null, multipleOthers=null, multipleReference=null, " +
                "leadClaimant=null, claimantTypeOfClaimant=null, claimantCompany=null, claimantIndType=null, claimantType=null, " +
                "claimantOtherType=null, preAcceptCase=null, receiptDate=null, feeGroupReference=null, respondentSumType=null, " +
                "representativeClaimantType=null, respondentCollection=null, repCollection=null, positionType=Manually Created, " +
                "fileLocation=null, hearingType=null, hearingCollection=null, depositType=null, judgementCollection=null, " +
                "judgementDetailsCollection=null, costsCollection=null, disposeType=null, NH_JudgementType=null, jurCodesCollection=null, " +
                "acasOffice=null, clerkResponsible=null, userLocation=null, subMultipleReference=null, addSubMultipleComment=null, " +
                "panelCollection=null, documentCollection=null, referToETJ=null, responseType=null, responseTypeCollection=null, " +
                "withdrawType=null, archiveType=null, referredToJudge=null, backFromJudge=null, additionalType=null, " +
                "reconsiderationType=null, reconsiderationCollection=null, correspondenceType=null, correspondenceScotType=null, " +
                "caseNotes=null, caseDocument=null, claimantWorkAddress=null, claimantRepresentedQuestion=null, state=null, " +
                "stateAPI=null, managingOffice=Glasgow)";
        assertEquals(caseDataExpected, defaultValuesReaderService.getCaseData(caseData, postDefaultValuesGlasgow).toString());
    }
}