package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails2;
    private CaseDetails caseDetails3;

    private CaseData caseData;

    @Before
    public void setup() throws Exception {
        eventValidationService = new EventValidationService();

        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails3 = generateCaseDetails("caseDetailsTest3.json");

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

    @Test
    public void shouldValidateHearingNumberMatching() {

        List<String> errors = eventValidationService.validateHearingNumber(caseDetails1.getCaseData());

        assertEquals(0, errors.size());

    }

    @Test
    public void shouldValidateHearingNumberMismatch() {

        List<String> errors = eventValidationService.validateHearingNumber(caseDetails2.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(HEARING_NUMBER_MISMATCH_ERROR_MESSAGE, errors.get(0));

    }

    @Test
    public void shouldValidateHearingNumberForEmptyHearings() {

        List<String> errors = eventValidationService.validateHearingNumber(caseDetails3.getCaseData());

        assertEquals(1, errors.size());
        assertEquals(EMPTY_HEARING_COLLECTION_ERROR_MESSAGE, errors.get(0));

    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }
}
