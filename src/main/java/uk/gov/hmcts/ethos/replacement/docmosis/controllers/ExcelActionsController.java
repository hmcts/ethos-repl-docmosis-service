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
import uk.gov.hmcts.ethos.replacement.docmosis.service.*;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class ExcelActionsController {

    private static final String LOG_MESSAGE = "received notification request for multiple reference : ";
    private static final String GENERATED_DOCUMENTS_URL = "Please download the documents from : ";

    private final VerifyTokenService verifyTokenService;
    private final MultipleCreationService multipleCreationService;
    private final MultiplePreAcceptService multiplePreAcceptService;
    private final MultipleAmendCaseIdsService multipleAmendCaseIdsService;
    private final MultipleUpdateService multipleUpdateService;
    private final MultipleScheduleService multipleScheduleService;
    private final MultipleUploadService multipleUploadService;

    @Autowired
    public ExcelActionsController(VerifyTokenService verifyTokenService,
                                  MultipleCreationService multipleCreationService,
                                  MultiplePreAcceptService multiplePreAcceptService,
                                  MultipleAmendCaseIdsService multipleAmendCaseIdsService,
                                  MultipleUpdateService multipleUpdateService,
                                  MultipleScheduleService multipleScheduleService,
                                  MultipleUploadService multipleUploadService) {
        this.verifyTokenService = verifyTokenService;
        this.multipleCreationService = multipleCreationService;
        this.multiplePreAcceptService = multiplePreAcceptService;
        this.multipleAmendCaseIdsService = multipleAmendCaseIdsService;
        this.multipleUpdateService = multipleUpdateService;
        this.multipleScheduleService = multipleScheduleService;
        this.multipleUploadService = multipleUploadService;
    }

    @PostMapping(value = "/createBulkExcel", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates a multiple case. Retrieves cases by ethos case reference. Create an Excel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> createBulkExcel(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("CREATE MULTIPLE EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

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
        log.info("AMEND MULTIPLE EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .data(multipleDetails.getCaseData())
                .build());
    }

    @PostMapping(value = "/uploadMultipleExcel", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Check errors uploading an excel to the multiple")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> uploadMultipleExcel(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPLOAD MULTIPLE EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

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

    @PostMapping(value = "/preAcceptBulkExcel", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Accept a bulk of cases.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> preAcceptBulkExcel(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRE ACCEPT MULTIPLE EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

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

    @PostMapping(value = "/updateBulkCaseExcel", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update a bulk case. Update the collection of caseIds.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> updateBulkCaseExcel(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE MULTIPLE CASE IDS EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

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

    @PostMapping(value = "/updateBulkExcel", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "updates cases in a bulk case. Update cases by given fields.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> updateBulkExcel(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE MULTIPLE EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

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

    @PostMapping(value = "/generateBulkScheduleExcel", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a multiple schedule.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> generateBulkScheduleExcel(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE MULTIPLE SCHEDULE EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        MultipleDetails multipleDetails = multipleRequest.getCaseDetails();

        DocumentInfo documentInfo = multipleScheduleService.bulkScheduleLogic(userToken, multipleDetails, errors);

        multipleDetails.getCaseData().setDocMarkUp(documentInfo.getMarkUp());

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .errors(errors)
                .data(multipleDetails.getCaseData())
                .significant_item(Helper.generateSignificantItem(documentInfo))
                .build());
    }

    @PostMapping(value = "/generateBulkScheduleConfirmationExcel", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a multiple schedule confirmation.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = MultipleCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<MultipleCallbackResponse> generateBulkScheduleConfirmationExcel(
            @RequestBody MultipleRequest multipleRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE MULTIPLE SCHEDULE CONFIRMATION EXCEL ---> " + LOG_MESSAGE + multipleRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        MultipleData multipleData = multipleRequest.getCaseDetails().getCaseData();

        return ResponseEntity.ok(MultipleCallbackResponse.builder()
                .data(multipleData)
                .confirmation_header(GENERATED_DOCUMENTS_URL + multipleData.getDocMarkUp())
                .build());
    }
}
