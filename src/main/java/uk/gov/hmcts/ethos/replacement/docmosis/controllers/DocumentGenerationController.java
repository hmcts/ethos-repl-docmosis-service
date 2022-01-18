package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicLetters;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DefaultValuesReaderService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentGenerationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_EMPTY_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityErrors;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityNoErrors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DocumentGenerationController {

    private static final String LOG_MESSAGE = "received notification request for case reference : ";
    private static final String GENERATED_DOCUMENT_URL = "Please download the document from : ";
    private static final String INVALID_TOKEN = "Invalid Token {}";
    private final DocumentGenerationService documentGenerationService;
    private final DefaultValuesReaderService defaultValuesReaderService;
    private final VerifyTokenService verifyTokenService;
    private final EventValidationService eventValidationService;

    @PostMapping(value = "/midAddressLabels", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "populates the address labels list with the user selected addresses.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> midAddressLabels(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MID ADDRESS LABELS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        var caseData = ccdRequest.getCaseDetails().getCaseData();
        caseData = documentGenerationService.midAddressLabels(caseData);

        if (caseData.getAddressLabelCollection() != null && caseData.getAddressLabelCollection().isEmpty()) {
            errors.add(ADDRESS_LABELS_EMPTY_ERROR);
            log.info("Event fields validation: " + errors);
        }

        return getCallbackRespEntityErrors(errors, caseData);
    }

    @PostMapping(value = "/midSelectedAddressLabels", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "populates the address labels list with the user selected addresses to be printed.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> midSelectedAddressLabels(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MID SELECTED ADDRESS LABELS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var caseData = ccdRequest.getCaseDetails().getCaseData();
        caseData = documentGenerationService.midSelectedAddressLabels(caseData);

        return getCallbackRespEntityNoErrors(caseData);
    }

    @PostMapping(value = "/midValidateAddressLabels", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "validates the address labels collection and print attributes before printing.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> midValidateAddressLabels(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("MID VALIDATE ADDRESS LABELS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors;
        var caseData = ccdRequest.getCaseDetails().getCaseData();
        errors = documentGenerationService.midValidateAddressLabels(caseData);
        log.info("Event fields validation: " + errors);

        return getCallbackRespEntityErrors(errors, caseData);
    }

    @PostMapping(value = "/generateDocument", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a document.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> generateDocument(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE LETTER ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var caseDetails = ccdRequest.getCaseDetails();

        List<String> errors = eventValidationService.validateHearingNumber(caseDetails.getCaseData(),
                caseDetails.getCaseData().getCorrespondenceType(), caseDetails.getCaseData()
                        .getCorrespondenceScotType());

        if (errors.isEmpty()) {

            var defaultValues = getPostDefaultValues(caseDetails);
            defaultValuesReaderService.getCaseData(caseDetails.getCaseData(), defaultValues);
            var documentInfo = documentGenerationService.processDocumentRequest(ccdRequest, userToken);
            documentGenerationService.updateBfActions(documentInfo, caseDetails.getCaseData());
            caseDetails.getCaseData().setDocMarkUp(documentInfo.getMarkUp());
            documentGenerationService.clearUserChoices(caseDetails);

            var significantItem = Helper.generateSignificantItem(documentInfo, errors);

            if (errors.isEmpty()) {
                return ResponseEntity.ok(CCDCallbackResponse.builder()
                        .data(caseDetails.getCaseData())
                        .errors(errors)
                        .significant_item(significantItem)
                        .build());
            } else {
                return getCallbackRespEntityErrors(errors, caseDetails.getCaseData());
            }
        } else {
            return getCallbackRespEntityErrors(errors, caseDetails.getCaseData());
        }
    }

    @PostMapping(value = "/generateDocumentConfirmation", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a document confirmation.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> generateDocumentConfirmation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE LETTER CONFIRMATION ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .confirmation_header(GENERATED_DOCUMENT_URL + ccdRequest.getCaseDetails().getCaseData().getDocMarkUp())
                .build());
    }

    private DefaultValues getPostDefaultValues(CaseDetails caseDetails) {
        String caseTypeId = caseDetails.getCaseTypeId();
        String managingOffice = caseDetails.getCaseData().getManagingOffice() != null
                ? caseDetails.getCaseData().getManagingOffice() : "";

        return defaultValuesReaderService.getDefaultValues(managingOffice, caseTypeId);
    }

    @PostMapping(value = "/dynamicLetters", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "populates a dynamic list for hearing numbers for letter")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> dynamicLetters(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("DYNAMIC LETTERS ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var caseData = ccdRequest.getCaseDetails().getCaseData();
        DynamicLetters.dynamicLetters(caseData, ccdRequest.getCaseDetails().getCaseTypeId());

        return getCallbackRespEntityNoErrors(caseData);
    }
}
