package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;

@Service("documentGenerationService")
public class DocumentGenerationService {

    private static final Logger log = LoggerFactory.getLogger(DocumentGenerationService.class);
    private final TornadoService tornadoService;
    private static final String MESSAGE = "Failed to generate document for case id : ";
    private static final String EXCEPTION = " Exception :";

    @Autowired
    public DocumentGenerationService(TornadoService tornadoService) {
        this.tornadoService = tornadoService;
    }

    public void processDocumentRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        //log.info("Auth Token: " + authToken);
        log.info("Case Details: " + caseDetails);
        try {
//            tornadoService.documentGeneration(ccdRequest.getCaseDetails(), "PostponementRequestGenericTest.docx");
            tornadoService.documentGeneration(ccdRequest.getCaseDetails(), "1.14.docx");
        } catch (Exception ex) {
            log.error(MESSAGE + caseDetails.getCaseId() + EXCEPTION + ex.toString());
        }
    }

}
