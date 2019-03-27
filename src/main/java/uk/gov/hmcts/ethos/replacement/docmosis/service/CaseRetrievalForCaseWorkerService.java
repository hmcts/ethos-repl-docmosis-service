package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

@Slf4j
@Service("CaseRetrievalForCaseWorkerService")
public class CaseRetrievalForCaseWorkerService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private CcdClient ccdClient;

    @Autowired
    public CaseRetrievalForCaseWorkerService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public SubmitEvent caseRetrievalRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        log.info("Auth Token: " + authToken);
        log.info("Case Details: " + caseDetails);
        try {
            return ccdClient.retrieveCase(authToken, caseDetails, "1550576532211563");
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }
}