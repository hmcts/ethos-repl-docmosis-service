package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.util.concurrent.Callable;

@Slf4j
public class BulkUpdateTask implements Callable<String> {

    private BulkDetails bulkDetails;
    private SubmitEvent submitEvent;
    private String authToken;
    private CcdClient ccdClient;

    public BulkUpdateTask(BulkDetails bulkDetails, SubmitEvent submitEvent, String authToken, CcdClient ccdClient) {
        this.bulkDetails = bulkDetails;
        this.submitEvent = submitEvent;
        this.authToken = authToken;
        this.ccdClient = ccdClient;
    }

    @Override
    public String call() throws Exception {

        log.info("Waiting: " + Thread.currentThread().getName());
        String caseId = String.valueOf(submitEvent.getCaseId());
        CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                bulkDetails.getJurisdiction(), caseId);
        ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                bulkDetails.getJurisdiction(), returnedRequest, caseId);
        return submitEvent.getCaseData().getEthosCaseReference();
    }
}
