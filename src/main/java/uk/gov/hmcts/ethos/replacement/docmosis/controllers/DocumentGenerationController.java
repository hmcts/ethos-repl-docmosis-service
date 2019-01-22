package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ethos.replacement.docmosis.model.DocumentRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentGenerationService;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class DocumentGenerationController {

    private static final Logger log = LoggerFactory.getLogger(DocumentGenerationController.class);

    private final DocumentGenerationService documentGenerationService;

    @Autowired
    public DocumentGenerationController(DocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    @GetMapping(value = "/generateDocument")
    public ResponseEntity<Object> generateDocument() {
        log.info("Generation Success: ");
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @PostMapping(value = "/generateDocument", consumes = APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> generateDocument(@RequestBody DocumentRequest documentRequest) {
//        boolean generationSuccess = documentGenerationService.processDocumentRequest(documentRequest);
//        log.info("Generation Success: " + generationSuccess);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


//    @PostMapping(value = "/case-orchestration/notify/hwf-successful", consumes = APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "send e-mail for HWF Successful.")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "HWFSuccessful e-mail sent successfully",
//                    response = CCDCallbackResponse.class)})
//    public ResponseEntity<CCDCallbackResponse> sendHwfSuccessfulConfirmationEmail(
//            @RequestBody CCDRequest ccdRequest,
//            @RequestHeader(value = "Authorization") String userToken) {
//        log.info(LOG_MESSAGE, ccdRequest.getCaseDetails().getCaseId());
//        notificationService.sendHWFSuccessfulConfirmationEmail(ccdRequest, userToken);
//        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
//        return ResponseEntity.ok(CCDCallbackResponse.builder().data(caseData).build());
//    }
}
