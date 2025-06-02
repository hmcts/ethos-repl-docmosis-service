package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.types.AdditionalCaseInfoType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper.buildFlagsImageFileName;

class FlagsImageHelperTest {

    private CCDRequest scotlandCcdRequest;
    private CCDRequest ccdRequest15;

    @BeforeEach
    void setUp() throws Exception {
        scotlandCcdRequest = new CCDRequest();
        CaseDetails caseDetailsScot3 = generateCaseDetails("caseDetailsScotTest3.json");
        scotlandCcdRequest.setCaseDetails(caseDetailsScot3);

        ccdRequest15 = new CCDRequest();
        CaseDetails caseDetails15 = generateCaseDetails("caseDetailsTest15.json");
        ccdRequest15.setCaseDetails(caseDetails15);

    }

    @Test
    void buildFlagsImageFileNameForNullFlagsTypes() {
        CaseData caseData = new CaseData();
        caseData.setAdditionalCaseInfoType(null);
        FlagsImageHelper.buildFlagsImageFileName(caseData, MANCHESTER_CASE_TYPE_ID);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-000000000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForEmptyFlagsTypes() {
        CaseData caseData = new CaseData();
        caseData.setAdditionalCaseInfoType(new AdditionalCaseInfoType());
        FlagsImageHelper.buildFlagsImageFileName(caseData, MANCHESTER_CASE_TYPE_ID);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-000000000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForTrueFlagsFields() {
        CaseData caseData = ccdRequest15.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData, MANCHESTER_CASE_TYPE_ID);
        String expected = "<font color='DarkRed' size='5'> DO NOT POSTPONE </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='Green' size='5'> LIVE APPEAL </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='Red' size='5'> RULE 49(3)b </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='LightBlack' size='5'> REPORTING </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='Orange' size='5'> SENSITIVE </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='Purple' size='5'> RESERVED </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='Olive' size='5'> ECC </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='SlateGray' size='5'> DIGITAL FILE </font>"
                          + "<font size='5'> - </font>"
                          + "<font color='DarkSlateBlue' size='5'> REASONABLE ADJUSTMENT </font>";
        assertEquals(expected, caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-011111111100.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForTrueFlagsFieldsScotland() {
        CaseData caseData = scotlandCcdRequest.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData, SCOTLAND_CASE_TYPE_ID);
        String expected = "<font color='DeepPink' size='5'> WITH OUTSTATION </font>";
        assertEquals(expected, caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-100000000000.jpg", caseData.getFlagsImageFileName());
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    void interventionFlag() {
        AdditionalCaseInfoType additionalCaseInfoType = new AdditionalCaseInfoType();
        additionalCaseInfoType.setInterventionRequired(YES);
        CaseData caseData = new CaseData();
        caseData.setAdditionalCaseInfoType(additionalCaseInfoType);

        buildFlagsImageFileName(caseData, MANCHESTER_CASE_TYPE_ID);
        assertEquals("EMP-TRIB-000000000001.jpg", caseData.getFlagsImageFileName());
        assertTrue(caseData.getFlagsImageAltText().contains("SPEAK TO REJ"));

        buildFlagsImageFileName(caseData, SCOTLAND_CASE_TYPE_ID);
        assertEquals("EMP-TRIB-000000000010.jpg", caseData.getFlagsImageFileName());
        assertTrue(caseData.getFlagsImageAltText().contains("SPEAK TO VP"));
    }
}
