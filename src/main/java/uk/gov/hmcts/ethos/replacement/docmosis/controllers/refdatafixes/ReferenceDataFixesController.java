package uk.gov.hmcts.ethos.replacement.docmosis.controllers.refdatafixes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityNoErrors;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.ReferenceDataFixesService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReferenceDataFixesController {

    private static final String LOG_MESSAGE = "received notification request for case reference :    ";

    private final VerifyTokenService verifyTokenService;
    private final ReferenceDataFixesService referenceDataFixesService;

    @PostMapping(value = "/updateJudgesItcoReferences", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "update the judges' ITCO references")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accessed successfully",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<RefDataFixesCallbackResponse> updateJudgesItcoReferences(
            @RequestBody RefDataFixesRequest refDataFixesRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE JUDGES ITCO REFERENCES ---> " + LOG_MESSAGE + refDataFixesRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

       RefDataFixesData caseData = referenceDataFixesService.updateJudgesItcoReferences(refDataFixesRequest.getCaseDetails(), userToken);

        return getCallbackRespEntityNoErrors(caseData);
    }


    @PostMapping(value = "/insertClaimServedDate", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Insert the claim served date for existing cases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accessed successfully",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<RefDataFixesCallbackResponse> insertClaimServedDate(
            @RequestBody RefDataFixesRequest refDataFixesRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("INSERT CLAIM SEERVED DATE ---> " + LOG_MESSAGE + refDataFixesRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        RefDataFixesData caseData = referenceDataFixesService.insertClaimServedDate(refDataFixesRequest.getCaseDetails(), userToken);

        return getCallbackRespEntityNoErrors(caseData);
    }
}
