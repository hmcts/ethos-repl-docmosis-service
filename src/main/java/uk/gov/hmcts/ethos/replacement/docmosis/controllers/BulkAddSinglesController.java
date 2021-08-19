package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleCallbackResponse;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles.BulkAddSinglesService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles.BulkAddSinglesValidator;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getMultipleCallbackRespEntity;

@RestController
@Slf4j
public class BulkAddSinglesController {
    private final BulkAddSinglesValidator bulkAddSinglesValidator;
    private final BulkAddSinglesService bulkAddSinglesService;
    private final VerifyTokenService verifyTokenService;
    private static final String INVALID_TOKEN = "Invalid Token {}";

    public BulkAddSinglesController(BulkAddSinglesValidator bulkAddSinglesValidator,
                                    BulkAddSinglesService bulkAddSinglesService,
                                    VerifyTokenService verifyTokenService) {
        this.bulkAddSinglesValidator = bulkAddSinglesValidator;
        this.bulkAddSinglesService = bulkAddSinglesService;
        this.verifyTokenService = verifyTokenService;
    }

    @PostMapping(value = "/bulkAddSingleCasesImportFileMidEventValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Validate the single cases to be added to a multiple")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = MultipleCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> bulkAddSingleCasesImportFileMidEventValidation(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var multipleDetails = multipleRequest.getCaseDetails();
        var errors = bulkAddSinglesValidator.validate(multipleDetails, userToken);

        return getMultipleCallbackRespEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/bulkAddSingleCasesToMultiple", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add one or more single cases to a multiple")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = MultipleCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> bulkAddSingleCasesToMultiple(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var multipleDetails = multipleRequest.getCaseDetails();
        var errors = bulkAddSinglesService.execute(multipleDetails, userToken);

        return getMultipleCallbackRespEntity(errors, multipleDetails);
    }
}
