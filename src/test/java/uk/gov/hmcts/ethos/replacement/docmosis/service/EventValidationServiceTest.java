package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@RunWith(SpringRunner.class)
public class EventValidationServiceTest {

    private static final LocalDate PAST_RECEIPT_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate CURRENT_RECEIPT_DATE = LocalDate.now();
    private static final LocalDate FUTURE_RECEIPT_DATE = LocalDate.now().plusDays(1);

    private static final LocalDate PAST_HEARING_DATE = PAST_RECEIPT_DATE.plusDays(TARGET_HEARING_DATE_INCREMENT);
    private static final LocalDate CURRENT_HEARING_DATE = CURRENT_RECEIPT_DATE.plusDays(TARGET_HEARING_DATE_INCREMENT);

    private EventValidationService eventValidationService;
    private CaseData caseData;

    @Before
    public void setup() {
        eventValidationService = new EventValidationService();
        caseData = new CaseData();
    }

    @Test
    public void shouldValidatePastReceiptDate() {

        caseData.setReceiptDate(PAST_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        assertEquals(0, errors.size());
        assertEquals(caseData.getTargetHearingDate(), PAST_HEARING_DATE.toString());

    }

    @Test
    public void shouldValidateCurrentReceiptDate() {

        caseData.setReceiptDate(CURRENT_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        assertEquals(0, errors.size());
        assertEquals(caseData.getTargetHearingDate(), CURRENT_HEARING_DATE.toString());

    }

    @Test
    public void shouldValidateFutureReceiptDate() {

        caseData.setReceiptDate(FUTURE_RECEIPT_DATE.toString());

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        assertEquals(1, errors.size());
        assertEquals(FUTURE_RECEIPT_DATE_ERROR_MESSAGE, errors.get(0));

    }

}
