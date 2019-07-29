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
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class BulkActionsController {

    private static final String LOG_MESSAGE = "received notification request for bulk reference :    ";
    private static final String GENERATED_DOCUMENTS_URL = "Please download the documents from : ";

    private final BulkCreationService bulkCreationService;
    private final BulkUpdateService bulkUpdateService;
    private final BulkSearchService bulkSearchService;
    private final DocumentGenerationService documentGenerationService;

    @Autowired
    public BulkActionsController(BulkCreationService bulkCreationService, BulkUpdateService bulkUpdateService,
                                 BulkSearchService bulkSearchService, DocumentGenerationService documentGenerationService) {
        this.bulkCreationService = bulkCreationService;
        this.bulkUpdateService = bulkUpdateService;
        this.bulkSearchService = bulkSearchService;
        this.documentGenerationService = documentGenerationService;
    }

    @PostMapping(value = "/createBulk", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "creates a bulk case. Retrieves cases by ethos case reference.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> createBulk(
            @RequestBody BulkRequest bulkRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("CREATE BULK ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        log.info("BEFORE RETRIEVAL");
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequest(bulkRequest.getCaseDetails(), userToken);
        log.info("AFTER RETRIEVAL");
        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkCreationLogic(bulkRequest.getCaseDetails(), bulkCasesPayload, userToken);
        log.info("AFTER BULK CREATION LOGIC");
        bulkRequestPayload = bulkCreationService.updateLeadCase(bulkRequestPayload, userToken);
        log.info("ISSUE ON THE LEAD CASE");

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .errors(bulkRequestPayload.getErrors())
                .data(bulkRequestPayload.getBulkDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/searchBulk", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "searches cases in a bulk case. Look for cases in multipleCollection by fields.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> searchBulk(
            @RequestBody BulkRequest bulkRequest) {
        log.info("SEARCH BULK ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        bulkRequest.setCaseDetails(bulkSearchService.bulkSearchLogic(bulkRequest.getCaseDetails()));

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .data(bulkRequest.getCaseDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/updateBulk", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "updates cases in a bulk case. Update cases in searchCollection by given fields.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> updateBulk(
            @RequestBody BulkRequest bulkRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE BULK ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        BulkRequestPayload bulkRequestPayload = bulkUpdateService.bulkUpdateLogic(bulkRequest.getCaseDetails(), userToken);

        bulkRequestPayload = bulkCreationService.updateLeadCase(bulkRequestPayload, userToken);

        bulkRequestPayload = bulkUpdateService.clearUpFields(bulkRequestPayload);

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .errors(bulkRequestPayload.getErrors())
                .data(bulkRequestPayload.getBulkDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/updateBulkCase", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update a bulk case. Update the multiple collection.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> updateBulkCase(
            @RequestBody BulkRequest bulkRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE BULK CASE IDS ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkUpdateCaseIdsLogic(bulkRequest, userToken);

        bulkRequestPayload = bulkCreationService.updateLeadCase(bulkRequestPayload, userToken);

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .errors(bulkRequestPayload.getErrors())
                .data(bulkRequestPayload.getBulkDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/generateBulkLetter", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "generate a bulk of letters.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> generateBulkLetter(
            @RequestBody BulkRequest bulkRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("GENERATE BULK LETTER ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        List<DocumentInfo> documentInfoList = documentGenerationService.processBulkDocumentRequest(bulkRequest, userToken);
        List<String> errors = new ArrayList<>();
        String markUps = "";
        if (documentInfoList.isEmpty()) {
            errors.add("There are not cases searched to generate letters");
        } else {
            markUps = documentInfoList.stream().map(DocumentInfo::getMarkUp).collect(Collectors.joining(", "));
            log.info("Markups: " + markUps);
        }

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .errors(errors)
                .data(bulkRequest.getCaseDetails().getCaseData())
                .confirmation_header(GENERATED_DOCUMENTS_URL + markUps)
//                .significant_item(SignificantItem.builder()
//                        .url(documentInfo.getUrl())
//                        .description(documentInfo.getDescription())
//                        .type(SignificantItemType.DOCUMENT.name())
//                        .build())
                .build());
    }

}
