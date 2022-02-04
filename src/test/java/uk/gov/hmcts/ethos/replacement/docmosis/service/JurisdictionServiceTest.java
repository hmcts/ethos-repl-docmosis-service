package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_DELETED_ERROR;

class JurisdictionServiceTest {

    private JurisdictionService jurisdictionService;
    private CaseDetails caseDetails1;
    private CaseDetails caseDetails2;
    private CaseDetails caseDetails3;
    private CaseDetails caseDetails6;

    @BeforeEach
    public void setup() throws Exception {
        jurisdictionService = new JurisdictionService();
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails3 = generateCaseDetails("caseDetailsTest3.json");
        caseDetails6 = generateCaseDetails("caseDetailsTest6.json");
    }

    @Test
    void shouldValidateJurisdictionCodesWithDuplicatesCodesAndExistenceJudgement() {
        List<String> errors = new ArrayList<>();
        jurisdictionService.validateJurisdictionCodes(caseDetails1.getCaseData(), errors);

        assertEquals(2, errors.size());
        assertEquals(DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE + " \"COM\" in Jurisdiction 3 "
                + "- \"DOD\" in Jurisdiction 5 ", errors.get(0));
        assertEquals(JURISDICTION_CODES_DELETED_ERROR + "[CCP, ADG]", errors.get(1));
    }

    @Test
    void shouldValidateJurisdictionCodesWithUniqueCodes() {
        List<String> errors = new ArrayList<>();
        jurisdictionService.validateJurisdictionCodes(caseDetails2.getCaseData(), errors);

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateJurisdictionCodesWithEmptyCodes() {
        List<String> errors = new ArrayList<>();
        caseDetails3.getCaseData().setJudgementCollection(new ArrayList<>());
        jurisdictionService.validateJurisdictionCodes(caseDetails3.getCaseData(), errors);

        assertEquals(0, errors.size());
    }

    @Test
    void populateJurisdictionCodeTest() {
        var caseData = caseDetails6.getCaseData();
        jurisdictionService.populateJurisdictionCode(caseData);
        assertEquals(caseData.getJurCodesCollection().get(0).getValue().getJuridictionCodesList(),
                caseData.getJurCodesCollection().get(0).getValue().getJurisdictionCode());
        assertEquals(caseData.getJurCodesCollection().get(1).getValue().getJuridictionCodesList(),
                caseData.getJurCodesCollection().get(1).getValue().getJurisdictionCode());
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }
}
