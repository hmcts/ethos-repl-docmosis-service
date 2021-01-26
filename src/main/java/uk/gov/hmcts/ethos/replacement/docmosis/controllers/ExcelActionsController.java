package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleCallbackResponse;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class ExcelActionsController {

    private static final String LOG_MESSAGE = "received notification request for multiple reference : ";

    private final VerifyTokenService verifyTokenService;
    private final MultipleCreationService multipleCreationService;
    private final MultiplePreAcceptService multiplePreAcceptService;
    private final MultipleAmendCaseIdsService multipleAmendCaseIdsService;
    private final MultipleUpdateService multipleUpdateService;
    private final MultipleUploadService multipleUploadService;
    private final MultipleDynamicListFlagsService multipleDynamicListFlagsService;
    private final MultipleMidEventValidationService multipleMidEventValidationService;
    private final SubMultipleUpdateService subMultipleUpdateService;
    private final SubMultipleMidEventValidationService subMultipleMidEventValidationService;
    private final MultipleCreationMidEventValidationService multipleCreationMidEventValidationService;
    private final MultipleSingleMidEventValidationService multipleSingleMidEventValidationService;
    private final EventValidationService eventValidationService;

    @Autowired
    public ExcelActionsController(VerifyTokenService verifyTokenService,
                                  MultipleCreationService multipleCreationService,
                                  MultiplePreAcceptService multiplePreAcceptService,
                                  MultipleAmendCaseIdsService multipleAmendCaseIdsService,
                                  MultipleUpdateService multipleUpdateService,
                                  MultipleUploadService multipleUploadService,
                                  MultipleDynamicListFlagsService multipleDynamicListFlagsService,
                                  MultipleMidEventValidationService multipleMidEventValidationService,
                                  SubMultipleUpdateService subMultipleUpdateService,
                                  SubMultipleMidEventValidationService subMultipleMidEventValidationService,
                                  MultipleCreationMidEventValidationService multipleCreationMidEventValidationService,
                                  MultipleSingleMidEventValidationService multipleSingleMidEventValidationService,
                                  EventValidationService eventValidationService) {
        this.verifyTokenService = verifyTokenService;
        this.multipleCreationService = multipleCreationService;
        this.multiplePreAcceptService = multiplePreAcceptService;
        this.multipleAmendCaseIdsService = multipleAmendCaseIdsService;
        this.multipleUpdateService = multipleUpdateService;
        this.multipleUploadService = multipleUploadService;
        this.multipleDynamicListFlagsService = multipleDynamicListFlagsService;
        this.multipleMidEventValidationService = multipleMidEventValidationService;
        this.subMultipleUpdateService = subMultipleUpdateService;
        this.subMultipleMidEventValidationService = subMultipleMidEventValidationService;
        this.multipleCreationMidEventValidationService = multipleCreationMidEventValidationService;
        this.multipleSingleMidEventValidationService = multipleSingleMidEventValidationService;
        this.eventValidationService = eventValidationService;
    }

    @PostMapping(value = "/createMultiple", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates a multiple case. Retrieves cases by ethos case reference. Creates an Excel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> createMultiple(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("CREATE MULTIPLE ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleCreationService.bulkCreationLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/amendMultiple", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update the state of the multiple")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> amendMultiple(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND MULTIPLE ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .data(multipleDetails.getCaseData())
                .build());
    }

    @PostMapping(value = "/importMultiple", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Check errors uploading an excel to the multiple")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> importMultiple(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("IMPORT MULTIPLE ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleUploadService.bulkUploadLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/preAcceptMultiple", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Accept a bulk of cases.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> preAcceptMultiple(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRE ACCEPT MULTIPLE ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multiplePreAcceptService.bulkPreAcceptLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/amendCaseIDs", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update the collection of caseIds in a multiple.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Deprecated public ResponseEntity<MultipleCallbackResponse> amendCaseIDs(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND CASE IDS ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleAmendCaseIdsService.bulkAmendCaseIdsLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/batchUpdate", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "updates cases in a bulk case. Update cases by given fields.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> batchUpdate(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("BATCH UPDATE ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleUpdateService.bulkUpdateLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/dynamicListFlags", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "populate flags in dynamic lists with all flags values are in the excel.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> dynamicListFlags(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("DYNAMIC LIST FLAGS ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleDynamicListFlagsService.populateDynamicListFlagsLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/multipleMidEventValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates if multiple and sub multiples are correct.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> multipleMidEventValidation(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MULTIPLE MID EVENT VALIDATION ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleMidEventValidationService.multipleValidationLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/subMultipleMidEventValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates if sub multiple is correct.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> subMultipleMidEventValidation(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("SUB MULTIPLE MID EVENT VALIDATION ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        subMultipleMidEventValidationService.subMultipleValidationLogic(multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/updateSubMultiple", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "manage create/amend/delete actions for sub multiples.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> updateSubMultiple(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE SUB MULTIPLE ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        subMultipleUpdateService.subMultipleUpdateLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/multipleCreationMidEventValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates if single cases are right on the multiple creation.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> multipleCreationMidEventValidation(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MULTIPLE CREATION MID EVENT VALIDATION ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleCreationMidEventValidationService.multipleCreationValidationLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/multipleSingleMidEventValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates whether case exists in the multiple.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> multipleSingleMidEventValidation(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MULTIPLE SINGLE MID EVENT VALIDATION ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        multipleSingleMidEventValidationService.multipleSingleValidationLogic(userToken, multipleDetails, errors);

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @PostMapping(value = "/multipleMidBatch1Validation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates the receipts date introduce by the user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> multipleMidBatch1Validation(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MULTIPLE MID BATCH 1 VALIDATION ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();
        List<String> errors = eventValidationService.validateReceiptDateMultiple(multipleDetails.getCaseData());

        return getMultipleCallbackResponseResponseEntity(errors, multipleDetails);
    }

    @NotNull
    private ResponseEntity<MultipleCallbackResponse> getMultipleCallbackResponseResponseEntity(List<String> errors,
                                                                                               MultipleDetails multipleDetails) {
        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
    }

}
