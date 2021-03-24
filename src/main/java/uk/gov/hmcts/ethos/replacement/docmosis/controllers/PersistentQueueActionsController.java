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
import uk.gov.hmcts.ecm.common.model.bulk.BulkCallbackResponse;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ecm.common.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.service.BulkCreationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.BulkSearchService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.BulkUpdateService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.BulkCreationService.UPDATE_SINGLES_PQ_STEP;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PersistentQueueActionsController {

    private static final String LOG_MESSAGE = "received notification request for bulk reference :    ";

    private final BulkCreationService bulkCreationService;
    private final BulkUpdateService bulkUpdateService;
    private final BulkSearchService bulkSearchService;
    private final VerifyTokenService verifyTokenService;

    @PostMapping(value = "/afterSubmittedBulkPQ", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "display the bulk info.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> afterSubmittedBulkPQ(
            @RequestBody BulkRequest bulkRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("AFTER SUBMITTED BULK PERSISTENT Q ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();

        if (bulkRequest.getCaseDetails().getCaseData().getMultipleSource() != null
                && !bulkRequest.getCaseDetails().getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)) {
            BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(
                    bulkRequest.getCaseDetails(), userToken, true, false);
            //BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequest(bulkRequest.getCaseDetails(), userToken, false);
            bulkRequestPayload = bulkCreationService.bulkCreationLogic(bulkRequest.getCaseDetails(), bulkCasesPayload, userToken, UPDATE_SINGLES_PQ_STEP);
        }

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .errors(bulkRequestPayload.getErrors())
                .data(bulkRequest.getCaseDetails().getCaseData())
                .confirmation_header("Updates are being processed. A notification will be sent once completed...")
                .build());
    }


    @PostMapping(value = "/preAcceptBulkPQ", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "accept a bulk of cases.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> preAcceptBulkPQ(
            @RequestBody BulkRequest bulkRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("PRE ACCEPT BULK PERSISTENT Q ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        List<SubmitEvent> submitEvents = bulkSearchService.retrievalCasesForPreAcceptRequest(bulkRequest.getCaseDetails(), userToken);

        BulkRequestPayload bulkRequestPayload = bulkUpdateService.bulkPreAcceptLogic(bulkRequest.getCaseDetails(),
                submitEvents, userToken, true);

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .errors(bulkRequestPayload.getErrors())
                .data(bulkRequestPayload.getBulkDetails().getCaseData())
                .build());
    }

    @PostMapping(value = "/updateBulkCasePQ", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "update a bulk case. Update the multiple collection.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accessed successfully",
                    response = CCDCallbackResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<BulkCallbackResponse> updateBulkCasePQ(
            @RequestBody BulkRequest bulkRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE BULK CASE IDS PERSISTENT Q ---> " + LOG_MESSAGE + bulkRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        BulkRequestPayload bulkRequestPayload = bulkCreationService.bulkUpdateCaseIdsLogic(bulkRequest, userToken, true);

        return ResponseEntity.ok(BulkCallbackResponse.builder()
                .errors(bulkRequestPayload.getErrors())
                .data(bulkRequestPayload.getBulkDetails().getCaseData())
                .build());
    }
}
