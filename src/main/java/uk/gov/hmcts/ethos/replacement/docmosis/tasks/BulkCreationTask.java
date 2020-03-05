package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.io.IOException;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.PENDING_STATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.SUBMITTED_STATE;

@Slf4j
public class BulkCreationTask implements Runnable {

    private BulkDetails bulkDetails;
    private SubmitEvent submitEvent;
    private String authToken;
    private String multipleRef;
    private String caseType;
    private CcdClient ccdClient;

    public BulkCreationTask(BulkDetails bulkDetails, SubmitEvent submitEvent, String authToken, String multipleRef, String caseType, CcdClient ccdClient) {
        this.bulkDetails = bulkDetails;
        this.submitEvent = submitEvent;
        this.authToken = authToken;
        this.multipleRef = multipleRef;
        this.caseType = caseType;
        this.ccdClient = ccdClient;
    }

    @Override
    public void run() {
        log.info("Waiting: " + Thread.currentThread().getName());
        String caseId = String.valueOf(submitEvent.getCaseId());
        CCDRequest returnedRequest;
        log.info("Current state ---> " + submitEvent.getState());
        try {
            if (submitEvent.getState().equals(PENDING_STATE)) {
                // Moving to submitted_state
                log.info("Moving from pending to submitted");
                    returnedRequest = ccdClient.startEventForCaseBulkSingle(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
                submitEvent.getCaseData().setState(SUBMITTED_STATE);
            } else {
                // Moving to accepted_state
                log.info("Moving to accepted state");
                returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            }
            submitEvent.getCaseData().setMultipleReference(multipleRef);
            submitEvent.getCaseData().setCaseType(caseType);
            ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), returnedRequest, caseId);
        } catch (IOException e) {
            log.error("Error processing bulk update threads");
        }
    }
}
