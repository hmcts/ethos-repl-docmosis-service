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
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentGenerationService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class DocumentGenerationController {

    private static final String LOG_MESSAGE = "received notification request for case reference :    ";

    private static final String GENERATED_DOCUMENT_URL = "Please download the document from : ";

    private final DocumentGenerationService documentGenerationService;

    @Autowired
    public DocumentGenerationController(DocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
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

        DocumentInfo documentInfo = new DocumentInfo();
        List<String> errors = new ArrayList<>();
        CaseData caseData = new CaseData();
        if (ccdRequest != null && ccdRequest.getCaseDetails() != null && ccdRequest.getCaseDetails().getCaseId() != null) {
            log.info(LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
            documentInfo = documentGenerationService.processDocumentRequest(ccdRequest, userToken);
            caseData = ccdRequest.getCaseDetails().getCaseData();
        } else {
            errors.add("The payload is empty. Please make sure you have some data on your case");
        }
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .errors(errors)
                .data(caseData)
                .confirmation_header(GENERATED_DOCUMENT_URL + documentInfo.getMarkUp())
                .significant_item(Helper.generateSignificantItem(documentInfo))
                .build());
    }

}
