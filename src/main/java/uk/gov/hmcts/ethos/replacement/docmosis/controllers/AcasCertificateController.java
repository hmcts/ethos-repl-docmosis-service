package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.AcasService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityErrors;

@RestController
@RequestMapping("/acasCertificate")
@Slf4j
public class AcasCertificateController {
    private static final String LOG_MESSAGE = "{} received notification request for case reference : {}";

    private static final String INVALID_TOKEN = "Invalid Token {}";
    private static final String GENERATED_DOCUMENT_URL = "Please download the ACAS Certificate from : ";
    private final VerifyTokenService verifyTokenService;
    private final AcasService acasService;

    public AcasCertificateController(VerifyTokenService verifyTokenService, AcasService acasService) {
        this.verifyTokenService = verifyTokenService;
        this.acasService = acasService;
    }

    @PostMapping(value = "/retrieveCertificate", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve ACAS Certificate from ACAS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> retrieveCertificate(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader("Authorization") String userToken) throws JsonProcessingException {
        log.info(LOG_MESSAGE, "RETRIEVE ACAS CERTIFICATES FROM ACAS ---> ", ccdRequest.getCaseDetails().getCaseId());
        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        List<String> errors = acasService.getAcasCertificate(ccdRequest.getCaseDetails(), userToken);

        return getCallbackRespEntityErrors(errors, caseData);
    }

    @PostMapping(value = "/confirmation", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Show ACAS Certificate Download link.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> confirmation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader("Authorization") String userToken) {
        log.info(LOG_MESSAGE, "SHOW CERTIFICATE CONFIRMATION ---> ", ccdRequest.getCaseDetails().getCaseId());
        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .confirmation_body(GENERATED_DOCUMENT_URL + ccdRequest.getCaseDetails().getCaseData().getDocMarkUp())
                .build());
    }
}
