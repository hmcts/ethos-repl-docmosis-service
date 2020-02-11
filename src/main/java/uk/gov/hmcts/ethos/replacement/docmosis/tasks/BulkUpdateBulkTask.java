package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.SubmitBulkEventSubmitEventType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.io.IOException;

@Slf4j
public class BulkUpdateBulkTask implements Runnable {

    private BulkDetails bulkDetails;
    private String authToken;
    private CcdClient ccdClient;
    private SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType;
    private String leadId;

    public BulkUpdateBulkTask(BulkDetails bulkDetails, String authToken, CcdClient ccdClient,
                              SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType, String leadId) {
        this.bulkDetails = bulkDetails;
        this.authToken = authToken;
        this.ccdClient = ccdClient;
        this.submitBulkEventSubmitEventType = submitBulkEventSubmitEventType;
        this.leadId = leadId;
    }

    @Override
    public void run() {

        log.info("Waiting: " + Thread.currentThread().getName());
        String bulkCaseId = String.valueOf(submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate().getCaseId());
        try {
            log.info("Update the single cases");
            for (SubmitEvent submitEvent : submitBulkEventSubmitEventType.getSubmitEventList()) {
                String caseId = String.valueOf(submitEvent.getCaseId());
                if (!leadId.equals(caseId)) {
                    log.info("Sending update NO LEAD");
                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                            bulkDetails.getJurisdiction(), caseId);
                    ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                            bulkDetails.getJurisdiction(), returnedRequest, caseId);
                } else {
                    log.info("Already updated the leadId");
                }
            }
            if (submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate() != null) {
                log.info("Update the bulk");
                CCDRequest returnedRequest = ccdClient.startBulkEventForCase(authToken, bulkDetails.getCaseTypeId(),
                        bulkDetails.getJurisdiction(), bulkCaseId);
                ccdClient.submitBulkEventForCase(authToken, submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate().getCaseData(), bulkDetails.getCaseTypeId(),
                        bulkDetails.getJurisdiction(), returnedRequest, bulkCaseId);
            }
        } catch (IOException e) {
            log.error("Error processing bulk update task threads");
        }
    }
}
