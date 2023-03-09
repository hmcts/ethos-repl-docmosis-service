package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.SubmitBulkEventSubmitEventType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

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
        try {
            if (submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate() != null) {
                String bulkCaseId = String.valueOf(submitBulkEventSubmitEventType
                        .getSubmitBulkEventToUpdate().getCaseId());
                log.info("Update the bulk with case id:" + bulkCaseId);
                CCDRequest returnedRequest = ccdClient.startBulkEventForCase(authToken, bulkDetails.getCaseTypeId(),
                        bulkDetails.getJurisdiction(), bulkCaseId);
                submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate().getCaseData().setFilterCases("No");
                ccdClient.submitBulkEventForCase(authToken, submitBulkEventSubmitEventType
                                .getSubmitBulkEventToUpdate().getCaseData(), bulkDetails.getCaseTypeId(),
                        bulkDetails.getJurisdiction(), returnedRequest, bulkCaseId);
            } else {
                for (SubmitEvent submitEvent : submitBulkEventSubmitEventType.getSubmitEventList()) {
                    String caseId = String.valueOf(submitEvent.getCaseId());
                    if (!leadId.equals(caseId)) {
                        CCDRequest returnedRequest = ccdClient.startEventForCase(authToken,
                                UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                                bulkDetails.getJurisdiction(), caseId);
                        ccdClient.submitEventForCase(authToken, returnedRequest.getCaseDetails().getCaseData(),
                                UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                                bulkDetails.getJurisdiction(), returnedRequest, caseId);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error processing bulk update task threads" + e.getMessage(), e);
        }
    }
}