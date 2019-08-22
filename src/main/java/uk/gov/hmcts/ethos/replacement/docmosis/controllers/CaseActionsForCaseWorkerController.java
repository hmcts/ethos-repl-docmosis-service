package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.service.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@RestController
public class CaseActionsForCaseWorkerController {

    private static final String LOG_MESSAGE = "received notification request for case reference :    ";

    private final CaseCreationForCaseWorkerService caseCreationForCaseWorkerService;

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;

    private final CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService;

    private final CaseManagementForCaseWorkerService caseManagementForCaseWorkerService;

    private final DefaultValuesReaderService defaultValuesReaderService;

    @Autowired
    public CaseActionsForCaseWorkerController(CaseCreationForCaseWorkerService caseCreationForCaseWorkerService,
                                              CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService,
                                              CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService,
                                              DefaultValuesReaderService defaultValuesReaderService,
                                              CaseManagementForCaseWorkerService caseManagementForCaseWorkerService) {
        this.caseCreationForCaseWorkerService = caseCreationForCaseWorkerService;
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.caseUpdateForCaseWorkerService = caseUpdateForCaseWorkerService;
        this.defaultValuesReaderService = defaultValuesReaderService;
        this.caseManagementForCaseWorkerService = caseManagementForCaseWorkerService;
    }

    @PostMapping(value = "/createCase", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "create a case for a caseWorker.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> createCase(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("CREATE CASE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        SubmitEvent submitEvent = caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, userToken);
        log.info("Case created correctly with case Id: " + submitEvent.getCaseId());
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/retrieveCase", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "retrieve a case for a caseWorker.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> retrieveCase(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("RETRIEVE CASE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        SubmitEvent submitEvent = caseRetrievalForCaseWorkerService.caseRetrievalRequest(ccdRequest, userToken);
        log.info("Case received correctly: " + submitEvent);
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/retrieveCases", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "retrieve cases for a caseWorker.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> retrieveCases(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("RETRIEVE CASES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        List<SubmitEvent> submitEvents = caseRetrievalForCaseWorkerService.casesRetrievalRequest(ccdRequest, userToken);
        log.info("Cases received: " + submitEvents.size());
        submitEvents.forEach(submitEvent -> System.out.println(submitEvent.getCaseId()));
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/updateCase", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update a case for a caseWorker.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> updateCase(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE CASE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        SubmitEvent submitEvent = caseUpdateForCaseWorkerService.caseUpdateRequest(ccdRequest, userToken);
        log.info("Case updated correctly: " + submitEvent);
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/preDefaultValues", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update pre default values in a case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> preDefaultValues(
            @RequestBody CCDRequest ccdRequest) {
        log.info("PRE DEFAULT VALUES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        DefaultValues defaultValues = defaultValuesReaderService.getDefaultValues(PRE_DEFAULT_XLSX_FILE_PATH, ccdRequest.getCaseDetails());
        log.info("Pre Default values loaded: " + defaultValues);
        ccdRequest.getCaseDetails().getCaseData().setClaimantTypeOfClaimant(defaultValues.getClaimantTypeOfClaimant());
        log.info("Pre Default caseDetails: " + ccdRequest.getCaseDetails());
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/postDefaultValues", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update the case with some default values after submitted.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> postDefaultValues(
            @RequestBody CCDRequest ccdRequest) {
        List<String> errors = new ArrayList<>();
        CaseData caseData = new CaseData();
        if (ccdRequest != null && ccdRequest.getCaseDetails() != null && ccdRequest.getCaseDetails().getCaseId() != null) {
            log.info("POST DEFAULT VALUES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
            DefaultValues defaultValues = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, ccdRequest.getCaseDetails());
            log.info("Post Default values loaded: " + defaultValues);
            caseData = defaultValuesReaderService.getCaseData(ccdRequest.getCaseDetails().getCaseData(), defaultValues);
            log.info("Post Default caseData: " + caseData);
        } else {
            log.info("Error in PostDefaultValues");
            errors.add("The payload is empty. Please make sure you have some data on your case");
        }
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .errors(errors)
                .data(caseData)
                .build());
    }

    @PostMapping(value = "/preAcceptCase", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update the case state to Accepted or Rejected.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> preAcceptCase(
            @RequestBody CCDRequest ccdRequest) {
        log.info("PRE ACCEPT CASE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        CaseData caseData = caseManagementForCaseWorkerService.preAcceptCase(ccdRequest);
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .build());
    }

    @PostMapping(value = "/amendCaseDetails", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "amend the case details for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> amendCaseDetails(
            @RequestBody CCDRequest ccdRequest) {
        log.info("AMEND CASE DETAILS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        DefaultValues defaultValues = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, ccdRequest.getCaseDetails());
        log.info("Post Default values loaded: " + defaultValues);
        CaseData caseData = defaultValuesReaderService.getCaseData(ccdRequest.getCaseDetails().getCaseData(), defaultValues);
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .build());
    }

}
