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
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.SignificantItemType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DefaultValuesReaderService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.ListingService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.POST_DEFAULT_XLSX_FILE_PATH;

@Slf4j
@RestController
public class ListingGenerationController {

    private static final String LOG_MESSAGE = "received notification request for case reference :    ";

    private static final String GENERATED_DOCUMENT_URL = "Please download the document from : ";

    private final ListingService listingService;

    private final DefaultValuesReaderService defaultValuesReaderService;

    @Autowired
    public ListingGenerationController(ListingService listingService, DefaultValuesReaderService defaultValuesReaderService) {
        this.listingService = listingService;
        this.defaultValuesReaderService = defaultValuesReaderService;
    }

    @PostMapping(value = "/listingHearings", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "search hearings by venue and date.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<ListingCallbackResponse> listingHearings(
            @RequestBody ListingRequest listingRequest,
            @RequestHeader(value = "Authorization") String userToken) {

        log.info("LISTING HEARINGS ---> " + LOG_MESSAGE + listingRequest.getCaseDetails().getCaseId());
        ListingDetails listingDetails = listingService.processListingHearingsRequest(listingRequest.getCaseDetails(), userToken);

        String managingOffice = listingRequest.getCaseDetails().getCaseData().getListingVenue() != null ?
                listingRequest.getCaseDetails().getCaseData().getListingVenue() : "";
        DefaultValues defaultValues = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, managingOffice,
                ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()));
        log.info("Post Default values loaded: " + defaultValues);
        ListingData listingData = defaultValuesReaderService.getListingData(listingDetails.getCaseData(), defaultValues);

        return ResponseEntity.ok(ListingCallbackResponse.builder()
                .data(listingData)
                .build());
    }

    @PostMapping(value = "/generateHearingDocument", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a listing document.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<ListingCallbackResponse> generateHearingDocument(
            @RequestBody ListingRequest listingRequest,
            @RequestHeader(value = "Authorization") String userToken) {

        log.info("GENERATE HEARING DOCUMENT ---> " + LOG_MESSAGE + listingRequest.getCaseDetails().getCaseId());

        List<String> errors = new ArrayList<>();
        if (listingRequest.getCaseDetails().getCaseData().getListingCollection() != null && !listingRequest.getCaseDetails().getCaseData().getListingCollection().isEmpty()) {
            DocumentInfo documentInfo = listingService.processHearingDocument(listingRequest.getCaseDetails(), userToken);
            return ResponseEntity.ok(ListingCallbackResponse.builder()
                    .data(listingRequest.getCaseDetails().getCaseData())
                    .confirmation_header(GENERATED_DOCUMENT_URL + documentInfo.getMarkUp())
                    .significant_item(generateSignificantItem(documentInfo))
                    .build());
        } else {
            errors.add("No hearings have been found for your search criteria");
            return ResponseEntity.ok(ListingCallbackResponse.builder()
                    .errors(errors)
                    .data(listingRequest.getCaseDetails().getCaseData())
                    .build());
        }
    }

    private SignificantItem generateSignificantItem(DocumentInfo documentInfo) {
        log.info("generateSignificantItem for document: " + documentInfo);
        return SignificantItem.builder()
                .url(documentInfo.getUrl())
                .description(documentInfo.getDescription())
                .type(SignificantItemType.DOCUMENT.name())
                .build();
    }

}
