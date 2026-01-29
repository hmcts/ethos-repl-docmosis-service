package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.listing.ListingCallbackResponse;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.letters.InvalidCharacterCheck;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DefaultValuesReaderService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.ListingService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SERVING_CLAIMS_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityErrors;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getListingCallbackRespEntityErrors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ListingGenerationController {

    private static final String LOG_MESSAGE = "received notification request for case reference : ";
    private static final String GENERATED_DOCUMENT_URL = "Please download the document from : ";
    private static final String INVALID_TOKEN = "Invalid Token {}";

    private final ListingService listingService;
    private final DefaultValuesReaderService defaultValuesReaderService;
    private final VerifyTokenService verifyTokenService;

    @PostMapping(value = "/listingCaseCreation", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "handles logic related to the creation of listing cases.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<ListingCallbackResponse> listingCaseCreation(
            @RequestBody ListingRequest listingRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("LISTING CASE CREATION ---> " + LOG_MESSAGE + listingRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var listingData = listingService.listingCaseCreation(listingRequest.getCaseDetails());

        return ResponseEntity.ok(ListingCallbackResponse.builder()
                .data(listingData)
                .build());
    }

    @PostMapping(value = "/listingSingleCases", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "search hearings by venue and date in a specific case.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> listingSingleCases(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("LISTING SINGLE CASES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        var caseData = ccdRequest.getCaseDetails().getCaseData();

        if (ListingHelper.isListingRangeValid(ccdRequest.getCaseDetails().getCaseData()
                .getPrintHearingDetails(), errors)) {

            listingService.processListingSingleCasesRequest(ccdRequest.getCaseDetails());

        }

        return getCallbackRespEntityErrors(errors, caseData);
    }

    @PostMapping(value = "/listingHearings", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "search hearings by venue and date.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<ListingCallbackResponse> listingHearings(
            @RequestBody ListingRequest listingRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("LISTING HEARINGS ---> " + LOG_MESSAGE + listingRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        var listingData = listingRequest.getCaseDetails().getCaseData();

        if (ListingHelper.isListingRangeValid(listingData, errors)) {
            listingData = listingService.processListingHearingsRequest(
                    listingRequest.getCaseDetails(), userToken);

            String managingOffice = listingRequest.getCaseDetails().getCaseData().getListingVenue() != null
                    ? listingRequest.getCaseDetails().getCaseData().getListingVenue() : "";
            var defaultValues = defaultValuesReaderService.getDefaultValues(
                    managingOffice,
                    UtilHelper.getListingCaseTypeId(listingRequest.getCaseDetails().getCaseTypeId()));
            log.info("Post Default values loaded: " + defaultValues);
            listingData = defaultValuesReaderService.getListingData(listingData, defaultValues);

        }

        return getListingCallbackRespEntityErrors(errors, listingData);
    }

    @PostMapping(value = "/generateListingsDocSingleCases", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "generate a listing document.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> generateListingsDocSingleCases(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE LISTINGS DOC SINGLE CASES ---> " + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<String> errors = new ArrayList<>();
        var listingData = ccdRequest.getCaseDetails().getCaseData().getPrintHearingCollection();
        if (listingData.getListingCollection() != null && !listingData.getListingCollection().isEmpty()) {
            listingData = listingService.setCourtAddressFromCaseData(ccdRequest.getCaseDetails().getCaseData());
            var documentInfo = listingService.processHearingDocument(
                    listingData, ccdRequest.getCaseDetails().getCaseTypeId(), userToken);
            ccdRequest.getCaseDetails().getCaseData().setDocMarkUp(documentInfo.getMarkUp());
            return ResponseEntity.ok(CCDCallbackResponse.builder()
                    .data(ccdRequest.getCaseDetails().getCaseData())
                    .significant_item(Helper.generateSignificantItem(documentInfo, errors))
                    .build());
        } else {
            errors.add("No hearings have been found for your search criteria");
            return ResponseEntity.ok(CCDCallbackResponse.builder()
                    .errors(errors)
                    .data(ccdRequest.getCaseDetails().getCaseData())
                    .build());
        }
    }

    @PostMapping(value = "/generateListingsDocSingleCasesConfirmation", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "generate a listing document confirmation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> generateListingsDocSingleCasesConfirmation(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE LISTINGS DOC SINGLE CASES CONFIRMATION ---> "
                + LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(ccdRequest.getCaseDetails().getCaseData())
                .confirmation_header(GENERATED_DOCUMENT_URL + ccdRequest.getCaseDetails().getCaseData().getDocMarkUp())
                .build());
    }

    @PostMapping(value = "/generateReport", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "generate data for selected report.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<ListingCallbackResponse> generateReport(
            @RequestBody ListingRequest listingRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE REPORT ---> " + LOG_MESSAGE + listingRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var listingData = listingService.generateReportData(listingRequest.getCaseDetails(), userToken);

        return getResponseEntity(listingData, listingRequest.getCaseDetails().getCaseTypeId(), userToken);

    }

    private ResponseEntity<ListingCallbackResponse> getResponseEntity(ListingData listingData,
                                                                      String caseTypeId,
                                                                      String userToken) {
        List<String> errorsList = new ArrayList<>();

        if (hasListings(listingData)
                || (isAllowedReportType(listingData)
                    && (hasServedClaims(listingData) || hasSummaryAndDetails(listingData)))) {
            var documentInfo = getDocumentInfo(listingData, caseTypeId, userToken);
            updateListingDocMarkUp(listingData, documentInfo);
            return ResponseEntity.ok(ListingCallbackResponse.builder()
                    .data(listingData)
                    .significant_item(Helper.generateSignificantItem(documentInfo, errorsList))
                    .build());
        } else {
            errorsList.add("No cases (with hearings / claims served) have been found for your search criteria");
            return ResponseEntity.ok(ListingCallbackResponse.builder()
                    .errors(errorsList)
                    .data(listingData)
                    .build());
        }
    }

    private boolean hasServedClaims(ListingData listingData) {
        if (SERVING_CLAIMS_REPORT.equals(listingData.getReportType())) {
            return CollectionUtils.isNotEmpty(listingData.getLocalReportsDetail());
        } else {
            return true;
        }
    }

    private boolean isAllowedReportType(ListingData listingData) {
        return ListingHelper.isReportType(listingData.getReportType());
    }

    private boolean hasListings(ListingData listingData) {
        return CollectionUtils.isNotEmpty(listingData.getListingCollection());
    }

    private boolean hasSummaryAndDetails(ListingData listingData) {
        return listingData.getClass() == HearingsToJudgmentsReportData.class
                || listingData.getClass() == CasesAwaitingJudgmentReportData.class
                || listingData.getClass() == RespondentsReportData.class
                || listingData.getClass() == NoPositionChangeReportData.class;
    }

    private void updateListingDocMarkUp(ListingData listingData, DocumentInfo documentInfo) {
        listingData.setDocMarkUp(documentInfo.getMarkUp());
    }

    private DocumentInfo getDocumentInfo(ListingData listingData, String caseTypeId, String userToken) {
        return listingService.processHearingDocument(listingData, caseTypeId, userToken);
    }

    @PostMapping(value = "/generateHearingDocument", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "generate a listing document.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<ListingCallbackResponse> generateHearingDocument(
            @RequestBody ListingRequest listingRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE HEARING DOCUMENT ---> " + LOG_MESSAGE + listingRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var listingData = listingRequest.getCaseDetails().getCaseData();
        var caseTypeId = listingRequest.getCaseDetails().getCaseTypeId();

        List<String> errorsList = new ArrayList<>();
        boolean invalidCharsExist = InvalidCharacterCheck.invalidCharactersExistAllListingTypes(
                listingRequest.getCaseDetails(), errorsList);
        if (!invalidCharsExist && !hasListings(listingData)) {
            errorsList.add("No cases with hearings have been found for your search criteria");
        }
        if (errorsList.isEmpty()) {
            var documentInfo = getDocumentInfo(listingData, caseTypeId, userToken);
            updateListingDocMarkUp(listingData, documentInfo);
            return ResponseEntity.ok(ListingCallbackResponse.builder()
                    .data(listingData)
                    .significant_item(Helper.generateSignificantItem(documentInfo, errorsList))
                    .build());
        } else {
            return ResponseEntity.ok(ListingCallbackResponse.builder()
                    .errors(errorsList)
                    .data(listingData)
                    .build());
        }

    }

    @PostMapping(value = "/generateHearingDocumentConfirmation", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "generate a listing document confirmation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<ListingCallbackResponse> generateHearingDocumentConfirmation(
            @RequestBody ListingRequest listingRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE HEARING DOCUMENT CONFIRMATION ---> "
                + LOG_MESSAGE + listingRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        return ResponseEntity.ok(ListingCallbackResponse.builder()
                .data(listingRequest.getCaseDetails().getCaseData())
                .confirmation_header(GENERATED_DOCUMENT_URL
                        + listingRequest.getCaseDetails().getCaseData().getDocMarkUp())
                .build());
    }

}
