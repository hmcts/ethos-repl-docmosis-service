package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

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
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleCallbackResponse;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
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
    private static final String GENERATED_DOCUMENT_URL = "Please download the document from : ";

    private final VerifyTokenService verifyTokenService;
    private final MultipleCreationService multipleCreationService;
    private final MultiplePreAcceptService multiplePreAcceptService;
    private final MultipleAmendCaseIdsService multipleAmendCaseIdsService;
    private final MultipleUpdateService multipleUpdateService;
    private final MultipleScheduleService multipleScheduleService;
    private final MultipleLetterService multipleLetterService;
    private final MultipleUploadService multipleUploadService;
    private final MultipleDynamicListFlagsService multipleDynamicListFlagsService;
    private final MultipleMidEventValidationService multipleMidEventValidationService;
    private final SubMultipleUpdateService subMultipleUpdateService;
    private final SubMultipleMidEventValidationService subMultipleMidEventValidationService;
    private final MultipleCreationMidEventValidationService multipleCreationMidEventValidationService;
    private final MultipleSingleMidEventValidationService multipleSingleMidEventValidationService;

    @Autowired
    public ExcelActionsController(VerifyTokenService verifyTokenService,
                                  MultipleCreationService multipleCreationService,
                                  MultiplePreAcceptService multiplePreAcceptService,
                                  MultipleAmendCaseIdsService multipleAmendCaseIdsService,
                                  MultipleUpdateService multipleUpdateService,
                                  MultipleLetterService multipleLetterService,
                                  MultipleScheduleService multipleScheduleService,
                                  MultipleUploadService multipleUploadService,
                                  MultipleDynamicListFlagsService multipleDynamicListFlagsService,
                                  MultipleMidEventValidationService multipleMidEventValidationService,
                                  SubMultipleUpdateService subMultipleUpdateService,
                                  SubMultipleMidEventValidationService subMultipleMidEventValidationService,
                                  MultipleCreationMidEventValidationService multipleCreationMidEventValidationService,
                                  MultipleSingleMidEventValidationService multipleSingleMidEventValidationService) {
        this.verifyTokenService = verifyTokenService;
        this.multipleCreationService = multipleCreationService;
        this.multiplePreAcceptService = multiplePreAcceptService;
        this.multipleAmendCaseIdsService = multipleAmendCaseIdsService;
        this.multipleUpdateService = multipleUpdateService;
        this.multipleScheduleService = multipleScheduleService;
        this.multipleLetterService = multipleLetterService;
        this.multipleUploadService = multipleUploadService;
        this.multipleDynamicListFlagsService = multipleDynamicListFlagsService;
        this.multipleMidEventValidationService = multipleMidEventValidationService;
        this.subMultipleUpdateService = subMultipleUpdateService;
        this.subMultipleMidEventValidationService = subMultipleMidEventValidationService;
        this.multipleCreationMidEventValidationService = multipleCreationMidEventValidationService;
        this.multipleSingleMidEventValidationService = multipleSingleMidEventValidationService;
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
    }

    @PostMapping(value = "/printSchedule", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a multiple schedule.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> printSchedule(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRINT SCHEDULE ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        DocumentInfo documentInfo = multipleScheduleService.bulkScheduleLogic(userToken, multipleDetails, errors);

        multipleDetails.getCaseData().setDocMarkUp(documentInfo.getMarkUp());

        if (errors.isEmpty()) {
            return ResponseEntity.ok(MultipleCallbackResponse.builder()
                    .data(multipleDetails.getCaseData())
                    .significant_item(Helper.generateSignificantItem(documentInfo))
                    .build());
        } else {
            return ResponseEntity.ok(MultipleCallbackResponse.builder()
                    .errors(errors)
                    .data(multipleDetails.getCaseData())
                    .build());
        }

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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
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

        log.info("Returning callback");

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .build());
    }

    @PostMapping(value = "/printLetter", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a letter for the first case in the filtered collection.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> printLetter(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRINT LETTER ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        DocumentInfo documentInfo = multipleLetterService.bulkLetterLogic(userToken, multipleDetails, errors);

        if (errors.isEmpty()) {

            multipleDetails.getCaseData().setDocMarkUp(documentInfo.getMarkUp());

            return ResponseEntity.ok(MultipleCallbackResponse.builder()
                    .data(multipleDetails.getCaseData())
                    .significant_item(Helper.generateSignificantItem(documentInfo))
                    .build());
        } else {
            return ResponseEntity.ok(MultipleCallbackResponse.builder()
                    .errors(errors)
                    .data(multipleDetails.getCaseData())
                    .build());
        }
    }

    @PostMapping(value = "/printDocumentConfirmation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a confirmation with a link to the document generated.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> printDocumentConfirmation(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRINT DOCUMENT CONFIRMATION ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        MultipleData multipleData = multipleRequest.getCaseDetails().getCaseData();

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .data(multipleData)
                .confirmation_header(GENERATED_DOCUMENT_URL + multipleData.getDocMarkUp())
                .build());
    }

}
