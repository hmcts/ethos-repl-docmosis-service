package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicDepositOrder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.UNABLE_TO_FIND_PARTY;

class DepositOrderValidationServiceTest {

    private DepositOrderValidationService depositOrderValidationService;
    private JudgmentValidationService judgmentValidationService;

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails2;
    private CaseDetails caseDetails3;
    private CaseDetails caseDetails5;

    @BeforeEach
    void setup() throws Exception {
        depositOrderValidationService = new DepositOrderValidationService();
        judgmentValidationService = new JudgmentValidationService();
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails3 = generateCaseDetails("caseDetailsTest3.json");
        caseDetails5 = generateCaseDetails("caseDetailsTest5.json");

    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    void shouldValidateDepositRefunded() {
        List<String> errors = depositOrderValidationService.validateDepositOrder(caseDetails3.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR, errors.getFirst());
    }

    @Test
    void shouldValidateNullDepositRefunded() {
        List<String> errors = depositOrderValidationService.validateDepositOrder(caseDetails2.getCaseData());

        assertEquals(0, errors.size());
    }

    @Test
    void shouldValidateDepositRefundedWithNullAmount() {
        List<String> errors = depositOrderValidationService.validateDepositOrder(caseDetails1.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR, errors.getFirst());
    }

    @Test
    void shouldReturnNoErrorsForDepositValidation() {
        var caseData = caseDetails1.getCaseData();
        caseData.getDepositCollection().getFirst().getValue().setDepositAmount("300");
        DynamicDepositOrder.dynamicDepositOrder(caseData);
        List<String> errors = depositOrderValidationService.validateDepositOrder(caseData);
        assertEquals(0, errors.size());
        assertEquals("Tribunal", caseDetails1.getCaseData().getDepositCollection().getFirst()
            .getValue().getDepositRequestedBy());
    }

    @Test
    void shouldReturnErrorForDepositValidation() {
        var caseData = caseDetails5.getCaseData();
        List<String> errors = depositOrderValidationService.validateDepositOrder(caseData);
        assertEquals(1, errors.size());
        assertEquals(UNABLE_TO_FIND_PARTY, errors.getFirst());
    }
}
