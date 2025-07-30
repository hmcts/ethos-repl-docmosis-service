package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

class PreAcceptanceCaseServiceTest {
    private final PreAcceptanceCaseService preAcceptanceCaseService = new PreAcceptanceCaseService();

    @Test
    void shouldReturnErrorWhenDateAcceptedBeforeReceiptDate() {
        CaseData caseData = buildCaseData("2024-01-20", YES, "2024-01-15", null);
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Accepted date should not be earlier than the case received date");
    }

    @Test
    void shouldNotReturnErrorWhenDateAcceptedAfterReceiptDate() {
        CaseData caseData = buildCaseData("2024-01-20", YES, "2024-01-25", null);
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnErrorWhenDateRejectedBeforeReceiptDate() {
        CaseData caseData = buildCaseData("2024-01-20", NO, null, "2024-01-15");
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Rejected date should not be earlier than the case received date");
    }

    @Test
    void shouldNotReturnErrorWhenDateRejectedAfterReceiptDate() {
        CaseData caseData = buildCaseData("2024-01-20", NO, null, "2024-01-25");
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).isEmpty();
    }

    @Test
    void shouldReturnErrorWhenCaseDataIsNull() {
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(null);
        assertThat(errors).containsExactly("Case data is missing");
    }

    @Test
    void shouldReturnErrorWhenPreAcceptCaseIsNull() {
        CaseData caseData = new CaseData();
        caseData.setReceiptDate("2024-01-10");
        caseData.setPreAcceptCase(null);
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Pre-acceptance case data is missing");
    }

    @Test
    void shouldReturnErrorWhenReceiptDateIsNull() {
        CaseData caseData = buildCaseData(null, NO, null, "2024-01-25");
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Receipt date is missing or invalid");
    }

    @Test
    void shouldReturnErrorWhenReceiptDateIsInvalid() {
        CaseData caseData = buildCaseData("2024-13-20", NO, null, "2024-01-25");
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Receipt date is missing or invalid");
    }

    @Test
    void shouldReturnErrorWhenCaseAcceptedIsNull() {
        CaseData caseData = buildCaseData("2024-01-20", null, null, "2024-01-25");
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Case acceptance status is missing");
    }

    @Test
    void shouldReturnErrorWhenDateAcceptedIsNull() {
        CaseData caseData = buildCaseData("2024-01-20", YES, null, null);
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Accepted date is missing or invalid");
    }

    @Test
    void shouldReturnErrorWhenDateAcceptedIsInvalid() {
        CaseData caseData = buildCaseData("2024-01-20", YES, "2024-13-25", null);
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Accepted date is missing or invalid");
    }

    @Test
    void shouldReturnErrorWhenDateRejectedIsNull() {
        CaseData caseData = buildCaseData("2024-01-20", NO, null, null);
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Rejected date is missing or invalid");
    }

    @Test
    void shouldReturnErrorWhenDateRejectedIsInvalid() {
        CaseData caseData = buildCaseData("2024-01-20", NO, null, "2024-13-25");
        List<String> errors = preAcceptanceCaseService.validateAcceptanceDate(caseData);
        assertThat(errors).containsExactly("Rejected date is missing or invalid");
    }

    private CaseData buildCaseData(String receiptDate, String acceptedFlag, String dateAccepted, String dateRejected) {
        CasePreAcceptType preAccept = new CasePreAcceptType();
        preAccept.setCaseAccepted(acceptedFlag);
        preAccept.setDateAccepted(dateAccepted);
        preAccept.setDateRejected(dateRejected);

        CaseData caseData = new CaseData();
        caseData.setReceiptDate(receiptDate);
        caseData.setPreAcceptCase(preAccept);

        return caseData;
    }
}