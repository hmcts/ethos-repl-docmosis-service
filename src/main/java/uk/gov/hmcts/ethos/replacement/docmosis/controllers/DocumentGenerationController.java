package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentGenerationService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class DocumentGenerationController {

    private static final Logger log = LoggerFactory.getLogger(DocumentGenerationController.class);

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
        log.info(LOG_MESSAGE + ccdRequest.getCaseDetails().getCaseId());
        String filePath = documentGenerationService.processDocumentRequest(ccdRequest, userToken);
        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        return ResponseEntity.ok(CCDCallbackResponse.builder()
                .data(caseData)
                .confirmation_header(GENERATED_DOCUMENT_URL + filePath)
                .build());
    }

}
