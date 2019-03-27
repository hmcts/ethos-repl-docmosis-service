package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

@Slf4j
@Service("caseCreationForCaseWorkerService")
public class CaseCreationForCaseWorkerService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private CcdClient ccdClient;

    @Autowired
    public CaseCreationForCaseWorkerService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public void caseCreationRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        log.info("Auth Token: " + authToken);
        log.info("Case Details: " + caseDetails);
        try {
            SubmitEvent submitEvent = ccdClient.submitCaseCreation(authToken, caseDetails,
                    ccdClient.startCaseCreation(authToken, caseDetails));
            log.info("Case created correctly with case Id: " + submitEvent.getCaseId());
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }
}
