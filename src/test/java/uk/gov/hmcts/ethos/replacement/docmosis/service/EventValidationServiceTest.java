package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@RunWith(SpringRunner.class)
public class EventValidationServiceTest {

    private static final String PAST_DATE = LocalDate.now().minusDays(1).toString();
    private static final String CURRENT_DATE = LocalDate.now().toString();
    private static final String FUTURE_DATE = LocalDate.now().plusDays (1).toString();

    private EventValidationService eventValidationService;

    @Before
    public void setup() {
        eventValidationService = new EventValidationService();
    }

    @Test
    public void shouldValidatePastReceiptDate() {

        List<String> errors = eventValidationService.validateReceiptDate(PAST_DATE);

        assertEquals(0, errors.size());

    }

    @Test
    public void shouldValidateCurrentReceiptDate() {

        List<String> errors = eventValidationService.validateReceiptDate(CURRENT_DATE);

        assertEquals(0, errors.size());

    }

    @Test
    public void shouldValidateFutureReceiptDate() {

        List<String> errors = eventValidationService.validateReceiptDate(FUTURE_DATE);

        assertEquals(1, errors.size());
        assertEquals(FUTURE_RECEIPT_DATE_ERROR_MESSAGE, errors.get(0));

    }

}
