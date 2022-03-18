package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;

class CaseCloseServiceTest {

    private CaseCloseService caseCloseService;
    private CaseData caseData;

    @BeforeEach
    public void setup() throws Exception {
        caseCloseService = new CaseCloseService();
        caseData = new CaseData();
    }

    @Test
    void shouldValidateReinstateClosedCase_IsCaseClose_ReturnOne() {
        String errorMessage = "This case cannot be reinstated with a "
                + "current position of Case closed. Please select a different current position.";
        caseData.setPositionType(CASE_CLOSED_POSITION);
        List<String> errors = caseCloseService.validateReinstateClosedCaseMidEvent(caseData);
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }

    @Test
    void shouldValidateReinstateClosedCase_NotCaseClose_ReturnZero() {
        caseData.setPositionType("Awaiting ET3");
        List<String> errors = caseCloseService.validateReinstateClosedCaseMidEvent(caseData);
        assertEquals(0, errors.size());
    }
}