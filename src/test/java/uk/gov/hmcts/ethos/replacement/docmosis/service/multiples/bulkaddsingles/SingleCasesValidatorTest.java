package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OPEN_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PENDING_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TRANSFERRED_STATE;

class SingleCasesValidatorTest {
    private final String authToken = "some-token";
    private List<SubmitEvent> submitEvents;
    private List<String> caseIds;
    private SingleCasesValidator singleCasesValidator;

    @BeforeEach
    public void setup() throws IOException {
        var ccdClient = mock(CcdClient.class);
        caseIds = new ArrayList<>();
        submitEvents = new ArrayList<>();
        when(ccdClient.retrieveCasesElasticSearchForCreation(authToken, NEWCASTLE_CASE_TYPE_ID, caseIds,
                MANUALLY_CREATED_POSITION)).thenReturn(submitEvents);

        singleCasesValidator = new SingleCasesValidator(ccdClient);
    }

    @ParameterizedTest
    @CsvSource({ACCEPTED_STATE, SUBMITTED_STATE, CLOSED_STATE, REJECTED_STATE})
    void shouldSetValidStateCaseAsValid(String caseState) throws IOException {
        var ethosReference = "case1";
        caseIds.add(ethosReference);
        submitEvents.add(createSubmitEvent(ethosReference, SINGLE_CASE_TYPE, caseState, null));

        List<ValidatedSingleCase> validatedCases = singleCasesValidator.getValidatedCases(caseIds,
                NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        verify(validatedCases, ethosReference, true, null);
    }

    @ParameterizedTest
    @CsvSource({PENDING_STATE, OPEN_STATE, TRANSFERRED_STATE})
    void shouldSetInvalidCaseStateAsInvalid(String caseState) throws IOException {
        String ethosReference = "case1";
        caseIds.add(ethosReference);
        submitEvents.add(createSubmitEvent(ethosReference, SINGLE_CASE_TYPE, caseState, null));
        List<ValidatedSingleCase> validatedCases = singleCasesValidator.getValidatedCases(caseIds,
                NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        assertEquals(1, validatedCases.size());
        assertFalse(validatedCases.getFirst().isValid());
        assertEquals(ethosReference, validatedCases.getFirst().getEthosReference());
        assertEquals("Case is in state " + caseState, validatedCases.getFirst().getInvalidReason());
    }

    @Test
    public void shouldSetCaseInOtherMultipleAsValid() throws IOException {
        var ethosReference = "case1";
        var otherMultipleReference = "other-multiple";
        caseIds.add(ethosReference);
        submitEvents.add(createSubmitEvent(ethosReference, MULTIPLE_CASE_TYPE, ACCEPTED_STATE, otherMultipleReference));

        var validatedCases = singleCasesValidator.getValidatedCases(caseIds, NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        verify(validatedCases, ethosReference, true, null);
    }

    @Test
    public void shouldSetCaseAlreadyInMultipleAsValid() throws IOException {
        var ethosReference = "case1";
        var multipleReference = "multiple1";
        caseIds.add(ethosReference);
        submitEvents.add(createSubmitEvent(ethosReference, MULTIPLE_CASE_TYPE, ACCEPTED_STATE, multipleReference));

        var validatedCases = singleCasesValidator.getValidatedCases(caseIds,
                NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        verify(validatedCases, ethosReference, true, null);
    }

    @Test
    public void shouldSetUnknownCaseAsInvalid() throws IOException {
        var ethosReference = "case1";
        caseIds.add(ethosReference);

        var validatedCases = singleCasesValidator.getValidatedCases(caseIds,
                NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        verify(validatedCases, ethosReference, false, "Case not found");
    }

    @Test
    public void shouldSetSingleAcceptedCaseAsValid() throws IOException {
        var ethosReference = "case1";
        caseIds.add(ethosReference);
        submitEvents.add(createSubmitEvent(ethosReference, SINGLE_CASE_TYPE, ACCEPTED_STATE, null));

        var validatedCases = singleCasesValidator.getValidatedCases(caseIds,
                NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        verify(validatedCases, ethosReference, true, null);
    }

    /**
     * This unit test is to check the scenario where a single case still has a multiple reference assigned.
     * (which is actually a bug)
     * @throws IOException an exception
     */
    @Test
    public void shouldSetSingleAcceptedCaseWithMultipleReferenceAsValid() throws IOException {
        var ethosReference = "case1";
        var otherMultipleReference = "multiple2";
        caseIds.add(ethosReference);
        submitEvents.add(createSubmitEvent(ethosReference, SINGLE_CASE_TYPE, ACCEPTED_STATE, otherMultipleReference));

        var validatedCases = singleCasesValidator.getValidatedCases(caseIds,
                NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        verify(validatedCases, ethosReference, true, null);
    }

    @Test
    public void shouldHandleAllCases() throws IOException {
        caseIds.addAll(List.of("case1", "case2", "case3", "case4"));
        var multipleReference = "multiple1";
        submitEvents.add(createSubmitEvent("case1", SINGLE_CASE_TYPE, SUBMITTED_STATE, null));
        submitEvents.add(createSubmitEvent("case2", SINGLE_CASE_TYPE, ACCEPTED_STATE, null));
        submitEvents.add(createSubmitEvent("case3", SINGLE_CASE_TYPE, ACCEPTED_STATE, multipleReference));

        var validatedCases = singleCasesValidator.getValidatedCases(caseIds,
                NEWCASTLE_BULK_CASE_TYPE_ID, authToken);
        assertEquals(4, validatedCases.size());
        verify(validatedCases.getFirst(), "case1", true, null);
        verify(validatedCases.get(1), "case2", true, null);
        verify(validatedCases.get(2), "case3", true, null);
        verify(validatedCases.get(3), "case4", false, "Case not found");
    }

    private SubmitEvent createSubmitEvent(String ethosReference, String caseType, String state,
                                          String multipleReference) {
        var submitEvent = new SubmitEvent();
        submitEvent.setState(state);
        var caseData = new CaseData();
        caseData.setEthosCaseReference(ethosReference);
        caseData.setEcmCaseType(caseType);
        caseData.setMultipleReference(multipleReference);
        submitEvent.setCaseData(caseData);

        return submitEvent;
    }

    private void verify(List<ValidatedSingleCase> validatedCases, String expectedEthosReference,
                        boolean expectedValid, String expectedInvalidReason) {
        assertEquals(1, validatedCases.size());
        verify(validatedCases.getFirst(), expectedEthosReference, expectedValid, expectedInvalidReason);
    }

    private void verify(ValidatedSingleCase validatedCase, String expectedEthosReference,
                        boolean expectedValid, String expectedInvalidReason) {
        assertEquals(expectedValid, validatedCase.isValid());
        assertEquals(expectedEthosReference, validatedCase.getEthosReference());
        assertEquals(expectedInvalidReason, validatedCase.getInvalidReason());
    }

}
