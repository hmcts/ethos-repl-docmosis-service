package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;

@Service("documentGenerationService")
@Slf4j
public class DocumentGenerationService {

    private final TornadoService tornadoService;
    private static final String MESSAGE = "Failed to generate document for case id : ";
    private static final String EXCEPTION = " Exception :";

    @Autowired
    public DocumentGenerationService(TornadoService tornadoService) {
        this.tornadoService = tornadoService;
    }

    public String processDocumentRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("Auth Token: " + authToken);
        log.info("Case Details: " + caseDetails);
        String filePath = "";
        try {
            filePath = tornadoService.documentGeneration(authToken, ccdRequest.getCaseDetails());
        } catch (Exception ex) {
            log.error(MESSAGE + caseDetails.getCaseId() + EXCEPTION + ex.toString());
        }
        return filePath;
    }

}
