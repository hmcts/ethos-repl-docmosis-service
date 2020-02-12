package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.SubmitBulkEventSubmitEventType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.io.IOException;
import java.util.Optional;

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
            log.info("Update the single cases");
            for (SubmitEvent submitEvent : submitBulkEventSubmitEventType.getSubmitEventList()) {
                String caseId = String.valueOf(submitEvent.getCaseId());
                //if (!willBeUpdatedByBulkEvent(submitEvent.getCaseData().getEthosCaseReference())) {
                    log.info("Updating single cases");
                    if (leadId.equals(caseId)) {
                        submitEvent.getCaseData().setLeadClaimant("Yes");
                        log.info("LEAD");
                    } else {
                        log.info("NO LEAD");
                    }
                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                            bulkDetails.getJurisdiction(), caseId);
                    SubmitEvent submitEvent1 = ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                            bulkDetails.getJurisdiction(), returnedRequest, caseId);
//                } else {
//                    log.info("It will be updated by bulk event");
//                }
                log.info("SubmitEvent1 Response: " + submitEvent1);
            }
            if (submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate() != null) {
                String bulkCaseId = String.valueOf(submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate().getCaseId());
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

//    private boolean willBeUpdatedByBulkEvent(String ethosCaseReference) {
//        if (submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate() != null) {
//            Optional<CaseIdTypeItem> optionalCaseIdTypeItem = submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate().getCaseData().getCaseIdCollection()
//                    .stream()
//                    .filter(submitBulkEvent -> submitBulkEvent.getValue().getEthosCaseReference().equals(ethosCaseReference))
//                    .findFirst();
//            return optionalCaseIdTypeItem.isPresent();
//        }
//        return false;
//    }
}
