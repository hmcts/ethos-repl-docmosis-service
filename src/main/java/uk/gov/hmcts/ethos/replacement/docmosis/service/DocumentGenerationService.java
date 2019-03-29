package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.DocumentManagementException;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;

@Slf4j
@Service("documentGenerationService")
public class DocumentGenerationService {

    private final TornadoService tornadoService;
    private static final String MESSAGE = "Failed to generate document for case id : ";

    @Value("${azure.app_insights_key}")
    private String appInsightsKey;

    @Autowired
    public DocumentGenerationService(TornadoService tornadoService) {
        this.tornadoService = tornadoService;
    }

    public DocumentInfo processDocumentRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("Auth Token: " + authToken);
        log.info("Case Details: " + caseDetails);
        log.info("AppInsightsKey: " + "162CD82F0363F36E81218AD218C3C19F862CD479");
        log.info("AppInsightsKey: " + appInsightsKey);
        try {
            return tornadoService.documentGeneration(authToken, ccdRequest.getCaseDetails());
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

}
