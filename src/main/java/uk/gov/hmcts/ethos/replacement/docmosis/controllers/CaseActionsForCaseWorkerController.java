package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.service.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
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

    private final SingleReferenceService singleReferenceService;

    private final VerifyTokenService verifyTokenService;

    private final EventValidationService eventValidationService;

    @Autowired
    public CaseActionsForCaseWorkerController(VerifyTokenService verifyTokenService,
                                              CaseCreationForCaseWorkerService caseCreationForCaseWorkerService,
                                              CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService,
                                              CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService,
                                              DefaultValuesReaderService defaultValuesReaderService,
                                              CaseManagementForCaseWorkerService caseManagementForCaseWorkerService,
                                              SingleReferenceService singleReferenceService,
                                              EventValidationService eventValidationService) {
        this.verifyTokenService = verifyTokenService;
        this.caseCreationForCaseWorkerService = caseCreationForCaseWorkerService;
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.caseUpdateForCaseWorkerService = caseUpdateForCaseWorkerService;
        this.defaultValuesReaderService = defaultValuesReaderService;
        this.caseManagementForCaseWorkerService = caseManagementForCaseWorkerService;
        this.singleReferenceService = singleReferenceService;
        this.eventValidationService = eventValidationService;
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

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

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
    @Deprecated public ResponseEntity<CCDCallbackResponse> retrieveCase(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("RETRIEVE CASE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        SubmitEvent submitEvent = caseRetrievalForCaseWorkerService.caseRetrievalRequest(userToken, ccdRequest.getCaseDetails().getCaseTypeId(),
                ccdRequest.getCaseDetails().getJurisdiction(),"1550576532211563");
        log.info("Case received correctly with id: " + submitEvent.getCaseId());
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

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

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

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        SubmitEvent submitEvent = caseUpdateForCaseWorkerService.caseUpdateRequest(ccdRequest, userToken);
        log.info("Case updated correctly with id: " + submitEvent.getCaseId());
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
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRE DEFAULT VALUES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        DefaultValues defaultValues = defaultValuesReaderService.getDefaultValues(PRE_DEFAULT_XLSX_FILE_PATH, "", "");
        log.info("Pre Default values loaded: " + defaultValues);
        ccdRequest.getCaseDetails().getCaseData().setClaimantTypeOfClaimant(defaultValues.getClaimantTypeOfClaimant());
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
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("POST DEFAULT VALUES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        CaseData caseData = new CaseData();
        if (ccdRequest.getCaseDetails() != null && ccdRequest.getCaseDetails().getCaseId() != null) {
            errors = eventValidationService.validateReceiptDate(ccdRequest.getCaseDetails().getCaseData());
            log.info("Event fields validation:: " + errors);
            if (errors.isEmpty()) {
                caseManagementForCaseWorkerService.struckOutDefaults(ccdRequest.getCaseDetails().getCaseData());
                log.info("POST DEFAULT VALUES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
                DefaultValues defaultValues = getPostDefaultValues(ccdRequest.getCaseDetails());
                log.info("Post Default values loaded: " + defaultValues);
                caseData = defaultValuesReaderService.getCaseData(ccdRequest.getCaseDetails().getCaseData(), defaultValues, true);
                generateEthosCaseReference(caseData, ccdRequest);
            }
        } else {
            errors.add("The payload is empty. Please make sure you have some data on your case");
        }
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .errors(errors)
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
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRE ACCEPT CASE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

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
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND CASE DETAILS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        List<String> errors = eventValidationService.validateReceiptDate(caseData);
        log.info("Event fields validation: " + errors);
        if (errors.isEmpty()) {
            DefaultValues defaultValues = getPostDefaultValues(ccdRequest.getCaseDetails());
            log.info("Post Default values loaded: " + defaultValues);
            caseData = defaultValuesReaderService.getCaseData(ccdRequest.getCaseDetails().getCaseData(), defaultValues, false);
        }

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .errors(errors)
                .build());
    }

    @PostMapping(value = "/amendRespondentDetails", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "amend respondent details for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> amendRespondentDetails(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND RESPONDENT DETAILS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = caseManagementForCaseWorkerService.struckOutRespondents(ccdRequest);

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .build());
    }

    @PostMapping(value = "/addAmendET3", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add or amend ET3 respondent details for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> addAmendET3(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("ADD AMEND ET3 ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors;
        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();

        errors = eventValidationService.validateActiveRespondents(caseData);

        if(errors.isEmpty()) {
            errors = eventValidationService.validateReturnedFromJudgeDate(caseData);

            if (errors.isEmpty()) {
                caseData = caseManagementForCaseWorkerService.struckOutRespondents(ccdRequest);
            }
        }

        log.info("Event fields validation: " + errors);

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .errors(errors)
                .build());
    }

    @PostMapping(value = "/amendCaseState", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "amend the case state for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> amendCaseState(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND CASE STATE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();

        if(ccdRequest.getCaseDetails().getState().equals(CLOSED_STATE)) {
            errors = eventValidationService.validateJurisdictionOutcome(caseData);
            log.info("Event fields validation: " + errors);
        }

        if (errors.isEmpty()) {
            caseData.setState(ccdRequest.getCaseDetails().getState());
        }

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .errors(errors)
                .build());
    }

    @PostMapping(value = "/midRespondentAddress", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "populates the mid dynamic fixed list with the respondent addresses.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> midRespondentAddress(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MID RESPONDENT ADDRESS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = Helper.midRespondentAddress(ccdRequest.getCaseDetails().getCaseData());
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .build());
    }

    @PostMapping(value = "/generateCaseRefNumbers", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generates ethos case numbers according to caseRefNumberCount field.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> generateCaseRefNumbers(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE CASE REF NUMBERS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseCreationForCaseWorkerService.generateCaseRefNumbers(ccdRequest))
                .build());
    }

    @PostMapping(value = "/midRespondentECC", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "populates the mid dynamic list with the respondent names.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> midRespondentECC(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MID RESPONDENT ECC ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }
        List<String> errors = new ArrayList<>();
        CaseData caseData = caseManagementForCaseWorkerService.createECC(ccdRequest.getCaseDetails(), userToken, errors, MID_EVENT_CALLBACK);
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .errors(errors)
                .build());
    }

    @PostMapping(value = "/createECC", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "create a new Employer Contract Claim.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> createECC(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("CREATE ECC ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        CaseData caseData = caseManagementForCaseWorkerService.createECC(ccdRequest.getCaseDetails(), userToken, errors, ABOUT_TO_SUBMIT_EVENT_CALLBACK);
        generateEthosCaseReference(caseData, ccdRequest);

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .errors(errors)
                .build());
    }

    @PostMapping(value = "/linkOriginalCaseECC", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "send an update to the original case with the new ECC reference created to link it.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> linkOriginalCaseECC(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("LINK ORIGINAL CASE ECC ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }
        List<String> errors = new ArrayList<>();
        CaseData caseData = caseManagementForCaseWorkerService.createECC(ccdRequest.getCaseDetails(), userToken, errors, SUBMITTED_CALLBACK);
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .errors(errors)
                .build());
    }

    private DefaultValues getPostDefaultValues(CaseDetails caseDetails) {
        String caseTypeId = caseDetails.getCaseTypeId();
        String managingOffice = caseDetails.getCaseData().getManagingOffice() != null ? caseDetails.getCaseData().getManagingOffice() : "";
        return defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, managingOffice, caseTypeId);
    }

    private void generateEthosCaseReference(CaseData caseData, CCDRequest ccdRequest) {
        if (caseData.getEthosCaseReference() == null || caseData.getEthosCaseReference().trim().equals("")) {
            log.info("Case Type Id: " + ccdRequest.getCaseDetails().getCaseTypeId());
            String reference = singleReferenceService.createReference(ccdRequest.getCaseDetails().getCaseTypeId(), 1);
            log.info("Reference generated: " + reference);
            caseData.setEthosCaseReference(reference);
        }
    }

}
