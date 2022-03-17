package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;

class CaseCloseServiceTest {

    private CaseCloseService caseCloseService;

    @BeforeEach
    public void setup() throws Exception {
        caseCloseService = new CaseCloseService();
    }

    @Test
    void shouldValidateReinstateClosedCase_IsCaseClose_ReturnOne() {
        String errorMessage = "This case cannot be reinstated with a "
                + "current position of Case closed. Please select a different current position.";
        CaseData caseData = new CaseData();
        caseData.setPositionType(CASE_CLOSED_POSITION);
        List<String> errors = caseCloseService.validateReinstateClosedCaseMidEvent(caseData);
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals(errorMessage, errors.get(0));
    }

    @Test
    void shouldValidateReinstateClosedCase_NotCaseClose_ReturnZero() {
        CaseData caseData = new CaseData();
        caseData.setPositionType("Awaiting ET3");
        List<String> errors = caseCloseService.validateReinstateClosedCaseMidEvent(caseData);
        Assertions.assertEquals(0, errors.size());
    }
}