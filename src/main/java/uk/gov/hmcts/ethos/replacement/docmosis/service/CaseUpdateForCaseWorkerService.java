package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

@Slf4j
@Service("CaseUpdateForCaseWorkerService")
public class CaseUpdateForCaseWorkerService {

    private static final String MESSAGE = "Failed to update case for case id : ";
    private CcdClient ccdClient;

    @Autowired
    public CaseUpdateForCaseWorkerService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public SubmitEvent caseUpdateRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        log.info("Auth Token: " + authToken);
        log.info("Case Details: " + caseDetails);
        try {
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, caseDetails, "1554715626304100");
            log.info("------------ RETURNED REQUEST: " + returnedRequest);
            caseDetails.getCaseData().setFeeGroupReference("123456789000");
            return ccdClient.submitEventForCase(authToken, caseDetails, returnedRequest, "1554715626304100");
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }
}