package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.io.IOException;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.PENDING_STATE;

@Slf4j
public class BulkCreationTask implements Runnable {

    private final BulkDetails bulkDetails;
    private final SubmitEvent submitEvent;
    private final String authToken;
    private final String multipleRef;
    private final String caseType;
    private final CcdClient ccdClient;

    public BulkCreationTask(BulkDetails bulkDetails, SubmitEvent submitEvent, String authToken,
                            String multipleRef, String caseType, CcdClient ccdClient) {
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
        var caseId = String.valueOf(submitEvent.getCaseId());
        CCDRequest returnedRequest;
        log.info("Current state ---> " + submitEvent.getState());
        try {
            if (submitEvent.getState().equals(PENDING_STATE)) {
                // Moving to submitted_state
                log.info("Moving case {} from pending to submitted",
                        submitEvent.getCaseData().getEthosCaseReference());
                returnedRequest = ccdClient.startEventForCaseBulkSingle(authToken,
                        UtilHelper.getCaseTypeId(
                                bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            } else {
                // Moving to accepted_state
                log.info("Moving case {} to accepted state",
                        submitEvent.getCaseData().getEthosCaseReference());
                returnedRequest = ccdClient.startEventForCase(authToken,
                        UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            }
            returnedRequest.getCaseDetails().getCaseData().setMultipleReference(multipleRef);
            returnedRequest.getCaseDetails().getCaseData().setEcmCaseType(caseType);
            ccdClient.submitEventForCase(authToken, returnedRequest.getCaseDetails().getCaseData(),
                    UtilHelper.getCaseTypeId(
                            bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), returnedRequest, caseId);
        } catch (IOException e) {
            log.error("Error processing bulk update threads:" + e.getMessage(), e);
        }
    }
}
