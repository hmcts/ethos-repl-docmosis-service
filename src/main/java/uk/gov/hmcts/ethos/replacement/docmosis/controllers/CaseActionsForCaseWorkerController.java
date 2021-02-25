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
import uk.gov.hmcts.ecm.common.model.ccd.*;
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BFHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackResponseHelper.*;

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
    private final SingleCaseMultipleMidEventValidationService singleCaseMultipleMidEventValidationService;
    private final AddSingleCaseToMultipleService addSingleCaseToMultipleService;

    @Autowired
    public CaseActionsForCaseWorkerController(VerifyTokenService verifyTokenService,
                                              CaseCreationForCaseWorkerService caseCreationForCaseWorkerService,
                                              CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService,
                                              CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService,
                                              DefaultValuesReaderService defaultValuesReaderService,
                                              CaseManagementForCaseWorkerService caseManagementForCaseWorkerService,
                                              SingleReferenceService singleReferenceService,
                                              EventValidationService eventValidationService,
                                              SingleCaseMultipleMidEventValidationService singleCaseMultipleMidEventValidationService,
                                              AddSingleCaseToMultipleService addSingleCaseToMultipleService) {
        this.verifyTokenService = verifyTokenService;
        this.caseCreationForCaseWorkerService = caseCreationForCaseWorkerService;
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.caseUpdateForCaseWorkerService = caseUpdateForCaseWorkerService;
        this.defaultValuesReaderService = defaultValuesReaderService;
        this.caseManagementForCaseWorkerService = caseManagementForCaseWorkerService;
        this.singleReferenceService = singleReferenceService;
        this.eventValidationService = eventValidationService;
        this.singleCaseMultipleMidEventValidationService = singleCaseMultipleMidEventValidationService;
        this.addSingleCaseToMultipleService = addSingleCaseToMultipleService;
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

        return getCCDCallbackResponseResponseEntityWithoutErrors(ccdRequest.getCaseDetails().getCaseData());
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

        return getCCDCallbackResponseResponseEntityWithoutErrors(ccdRequest.getCaseDetails().getCaseData());
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

        return getCCDCallbackResponseResponseEntityWithoutErrors(ccdRequest.getCaseDetails().getCaseData());
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

        return getCCDCallbackResponseResponseEntityWithoutErrors(ccdRequest.getCaseDetails().getCaseData());
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
        log.info("PRE DEFAULT VALUES ---> " + LOG_MESSAGE);

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        DefaultValues defaultValues = defaultValuesReaderService.getDefaultValues(PRE_DEFAULT_XLSX_FILE_PATH, "", "");
        ccdRequest.getCaseDetails().getCaseData().setClaimantTypeOfClaimant(defaultValues.getClaimantTypeOfClaimant());

        return getCCDCallbackResponseResponseEntityWithoutErrors(ccdRequest.getCaseDetails().getCaseData());
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

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();

        List<String> errors = eventValidationService.validateReceiptDate(caseData);

        if (errors.isEmpty()) {
            DefaultValues defaultValues = getPostDefaultValues(ccdRequest.getCaseDetails());
            defaultValuesReaderService.getCaseData(caseData, defaultValues);
            caseManagementForCaseWorkerService.caseDataDefaults(caseData);
            generateEthosCaseReference(caseData, ccdRequest);
            caseData.setMultipleFlag(caseData.getCaseType() != null
                    && caseData.getCaseType().equals(MULTIPLE_CASE_TYPE) ? YES : NO);
        }

        log.info("PostDefaultValues for case: " + caseData.getEthosCaseReference());

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
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

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
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
            defaultValuesReaderService.getCaseData(caseData, defaultValues);
            caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
            FlagsImageHelper.buildFlagsImageFileName(caseData);

            addSingleCaseToMultipleService.addSingleCaseToMultipleLogic(
                    userToken, caseData, ccdRequest.getCaseDetails().getCaseTypeId(),
                    ccdRequest.getCaseDetails().getJurisdiction(),
                    ccdRequest.getCaseDetails().getCaseId(), errors);

        }

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
    }

    @PostMapping(value = "/amendClaimantDetails", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "amend the case claimant details for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> amendClaimantDetails(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND CLAIMANT DETAILS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.claimantDefaults(caseData);

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
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

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        List<String> errors = eventValidationService.validateActiveRespondents(caseData);
        if(errors.isEmpty()) {
            errors = eventValidationService.validateET3ResponseFields(caseData);
            if (errors.isEmpty()) {
                caseData = caseManagementForCaseWorkerService.struckOutRespondents(ccdRequest);
            }
        }

        log.info("Event fields validation: " + errors);

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
    }

    @PostMapping(value = "/amendRespondentRepresentative", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "amend respondent representative for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> amendRespondentRepresentative(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND RESPONDENT REPRESENTATIVE ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        List<String> errors = eventValidationService.validateRespRepNames(caseData);

        log.info("Event fields validation: " + errors);

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
    }

    @PostMapping(value = "/updateHearing", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update hearing details for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> updateHearing(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE HEARING ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        FlagsImageHelper.buildFlagsImageFileName(caseDetails.getCaseData());

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseDetails.getCaseData());
    }

    @PostMapping(value = "/restrictedCases", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "change restricted reporting for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> restrictedCases(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("RESTRICTED CASES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData);

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
    }

    @PostMapping(value = "/amendHearing", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "amend hearing details for a single case.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> amendHearing(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AMEND HEARING ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.amendHearing(caseData, ccdRequest.getCaseDetails().getCaseTypeId());

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
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

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
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

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
    }

    @PostMapping(value = "/jurisdictionValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates jurisdiction entries to prevent duplicates.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> jurisdictionValidation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("JURISDICTION VALIDATION ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        CaseData caseData =  ccdRequest.getCaseDetails().getCaseData();
        eventValidationService.validateJurisdictionCodes(caseData, errors);
        log.info("Event fields validation: " + errors);

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
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

        CaseData caseData = caseCreationForCaseWorkerService.generateCaseRefNumbers(ccdRequest);

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
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

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
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
        CaseData caseData = caseManagementForCaseWorkerService.createECC(
                ccdRequest.getCaseDetails(), userToken, errors, ABOUT_TO_SUBMIT_EVENT_CALLBACK);
        generateEthosCaseReference(caseData, ccdRequest);

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
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

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
    }

    @PostMapping(value = "/singleCaseMultipleMidEventValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates the multiple and sub multiple in the single case when moving to a multiple.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> singleCaseMultipleMidEventValidation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("SINGLE CASE MULTIPLE MID EVENT VALIDATION ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        CaseDetails caseDetails = ccdRequest.getCaseDetails();

        singleCaseMultipleMidEventValidationService.singleCaseMultipleValidationLogic(
                userToken, caseDetails, errors);

        return getCCDCallbackResponseResponseEntity(errors, caseDetails);
    }

    @PostMapping(value = "/hearingMidEventValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates the hearing number and the hearing days to prevent their creation.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> hearingMidEventValidation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("HEARING MID EVENT VALIDATION ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        List<String> errors = Helper.hearingMidEventValidation(caseDetails.getCaseData());

        return getCCDCallbackResponseResponseEntity(errors, caseDetails);
    }

    @PostMapping(value = "/bfActions", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "copy bf actions from bfActionsCW to bfActionsAll and generate a dateTime as ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> bfActions(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("BF ACTIONS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        BFHelper.copyBFActionsCollections(caseData);

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
    }

    @PostMapping(value = "/judgmentValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates jurisdiction codes within judgement collection.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> judgmentValidation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("JUDGEMENT VALIDATION ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData =  ccdRequest.getCaseDetails().getCaseData();
        List<String> errors = eventValidationService.validateJurisdictionCodesWithinJudgement(caseData);

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
    }

    @PostMapping(value = "/depositValidation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates deposit amount and deposit refunded.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> depositValidation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("DEPOSIT VALIDATION ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData =  ccdRequest.getCaseDetails().getCaseData();
        List<String> errors = eventValidationService.validateDepositRefunded(caseData);

        return getCCDCallbackResponseResponseEntityWithErrors(errors, caseData);
    }

    @PostMapping(value = "/createCaseTransfer", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "create a new Case in a different office.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> createCaseTransfer(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("CREATE CASE TRANSFER ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        caseCreationForCaseWorkerService.createCaseTransfer(caseData, ccdRequest.getCaseDetails().getJurisdiction(), userToken);

        return getCCDCallbackResponseResponseEntityWithoutErrors(caseData);
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
