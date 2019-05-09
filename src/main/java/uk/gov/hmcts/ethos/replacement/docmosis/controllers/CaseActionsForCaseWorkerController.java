package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseCreationForCaseWorkerService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseRetrievalForCaseWorkerService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseUpdateForCaseWorkerService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class CaseActionsForCaseWorkerController {

    private static final String LOG_MESSAGE = "received notification request for case reference :    ";

    private final CaseCreationForCaseWorkerService caseCreationForCaseWorkerService;

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;

    private final CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService;

    @Autowired
    public CaseActionsForCaseWorkerController(CaseCreationForCaseWorkerService caseCreationForCaseWorkerService,
                                              CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService,
                                              CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService) {
        this.caseCreationForCaseWorkerService = caseCreationForCaseWorkerService;
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.caseUpdateForCaseWorkerService = caseUpdateForCaseWorkerService;
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
        log.info(LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
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
        log.info(LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
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
        log.info(LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
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
        log.info(LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        SubmitEvent submitEvent = caseUpdateForCaseWorkerService.caseUpdateRequest(ccdRequest, userToken);
        log.info("Case updated correctly: " + submitEvent);
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/defaultFixedList", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update a fixed list with a default value.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> defaultFixedList(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info(LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        ccdRequest.getCaseDetails().getCaseData().setFeeGroupReference("123456789123");
        //ccdRequest.getCaseDetails().getCaseData().getClaimantType().setClaimantContactPreference("Email");
        log.info("Default value added to fixed lists");
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .build());
    }

}
