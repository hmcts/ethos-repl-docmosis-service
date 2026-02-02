package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BFHelperTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.CaseCloseValidator.CLOSING_CASE_WITH_BF_OPEN_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.CaseCloseValidator.REINSTATE_CANNOT_CASE_CLOSED_ERROR_MESSAGE;

class CaseCloseValidatorTest {

    private CaseCloseValidator caseCloseValidator;
    private CaseData caseData;

    @BeforeEach
    void setup() throws Exception {
        caseCloseValidator = new CaseCloseValidator();
        CaseDetails caseDetails = generateCaseDetails();
        caseData = caseDetails.getCaseData();
    }

    @Test
    void shouldValidateReinstateClosedCase_IsCaseClose_ReturnOne() {
        caseData.setPositionType(CASE_CLOSED_POSITION);
        List<String> errors = caseCloseValidator.validateReinstateClosedCaseMidEvent(caseData);
        assertEquals(1, errors.size());
        assertEquals(REINSTATE_CANNOT_CASE_CLOSED_ERROR_MESSAGE, errors.getFirst());
    }

    @Test
    void shouldValidateReinstateClosedCase_NotCaseClose_ReturnZero() {
        caseData.setPositionType("Awaiting ET3");
        List<String> errors = caseCloseValidator.validateReinstateClosedCaseMidEvent(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateBfActionsForCaseCloseEvent_AllCleared_Pass() {
        caseData.setBfActions(BFHelperTest.generateBFActionTypeItems());
        caseData.getBfActions().getFirst().getValue().setCleared("2022-02-22");
        List<String> errors = CaseCloseValidator.validateBfActionsForCaseCloseEvent(caseData);
        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateBfActionsForCaseCloseEvent_NotCleared_Error() {
        caseData.setBfActions(BFHelperTest.generateBFActionTypeItems());
        caseData.getBfActions().getFirst().getValue().setCleared(null);
        List<String> errors = CaseCloseValidator.validateBfActionsForCaseCloseEvent(caseData);
        assertEquals(1, errors.size());
        assertEquals(String.format(CLOSING_CASE_WITH_BF_OPEN_ERROR, caseData.getEthosCaseReference()),
            errors.getFirst());
    }

    @Test
    void shouldValidateBfActionsForCaseCloseEvent_NoBF_Pass() {
        caseData.setBfActions(null);
        List<String> errors = CaseCloseValidator.validateBfActionsForCaseCloseEvent(caseData);
        assertEquals(0, errors.size());
    }

    private CaseDetails generateCaseDetails() throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("caseDetailsTest1.json")).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }
}