package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleCallbackResponse;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles.BulkAddSinglesService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles.BulkAddSinglesValidator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BulkAddSinglesControllerTest {
    private BulkAddSinglesController bulkAddSinglesController;
    private VerifyTokenService verifyTokenService;
    private BulkAddSinglesValidator bulkAddSinglesValidator;
    private BulkAddSinglesService bulkAddSinglesService;
    private MultipleRequest multipleRequest;
    private String authToken;

    @Before
    public void setup() {
        bulkAddSinglesValidator = mock(BulkAddSinglesValidator.class);
        bulkAddSinglesService = mock(BulkAddSinglesService.class);
        authToken = "some-token";
        verifyTokenService = mock(VerifyTokenService.class);
        multipleRequest = mock(MultipleRequest.class);
        bulkAddSinglesController = new BulkAddSinglesController(bulkAddSinglesValidator, bulkAddSinglesService,
                verifyTokenService);
    }

    @Test
    public void shouldHandleInvalidTokenForValidation() {
        when(verifyTokenService.verifyTokenSignature(authToken)).thenReturn(false);
        var response = bulkAddSinglesController.bulkAddSingleCasesImportFileMidEventValidation(multipleRequest,
                authToken);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void shouldHandleInvalidTokenForCallback() {
        when(verifyTokenService.verifyTokenSignature(authToken)).thenReturn(false);
        var response = bulkAddSinglesController.bulkAddSingleCasesToMultiple(multipleRequest, authToken);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void shouldReturnValidationResponse() {
        when(verifyTokenService.verifyTokenSignature(authToken)).thenReturn(true);
        var multipleDetails = mock(MultipleDetails.class);
        when(multipleRequest.getCaseDetails()).thenReturn(multipleDetails);
        var errors = List.of("Error 1", "Error 2", "Error 3");
        when(bulkAddSinglesValidator.validate(multipleDetails, authToken)).thenReturn(errors);

        var response = bulkAddSinglesController
            .bulkAddSingleCasesImportFileMidEventValidation(multipleRequest, authToken);
        verifyResponse(response);
    }

    @Test
    public void shouldReturnCallbackResponse() {
        when(verifyTokenService.verifyTokenSignature(authToken)).thenReturn(true);
        var multipleDetails = mock(MultipleDetails.class);
        when(multipleRequest.getCaseDetails()).thenReturn(multipleDetails);
        var errors = List.of("Error 1", "Error 2", "Error 3");
        when(bulkAddSinglesService.execute(multipleDetails, authToken)).thenReturn(errors);

        var response = bulkAddSinglesController.bulkAddSingleCasesToMultiple(multipleRequest, authToken);
        verifyResponse(response);
    }

    private void verifyResponse(ResponseEntity<MultipleCallbackResponse> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getErrors().size());
        assertEquals("Error 1", response.getBody().getErrors().get(0));
        assertEquals("Error 2", response.getBody().getErrors().get(1));
        assertEquals("Error 3", response.getBody().getErrors().get(2));
    }
}
