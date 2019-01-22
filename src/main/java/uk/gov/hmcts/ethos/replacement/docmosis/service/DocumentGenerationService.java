package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.DocumentRequest;

import java.io.IOException;

@Service("documentGenerationService")
public class DocumentGenerationService {

    private static final Logger log = LoggerFactory.getLogger(DocumentGenerationService.class);

    private final TornadoService tornadoService;

    @Autowired
    public DocumentGenerationService(TornadoService tornadoService) {
        this.tornadoService = tornadoService;
    }

    public boolean processDocumentRequest(DocumentRequest documentRequest) {
        log.info("Processing document request " + documentRequest.getCreatedDate());
        try {
            //tornadoService.documentGeneration(documentRequest, "WelcomeTemplate.doc");
            tornadoService.documentGeneration(documentRequest, "PostponementRequestGenericTest.odt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
