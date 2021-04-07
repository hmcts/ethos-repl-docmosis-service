package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;

import java.io.IOException;
import java.time.LocalDate;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
public class BulkPreAcceptTask implements Runnable {

    private BulkDetails bulkDetails;
    private SubmitEvent submitEvent;
    private String authToken;
    private CcdClient ccdClient;

    public BulkPreAcceptTask(BulkDetails bulkDetails, SubmitEvent submitEvent, String authToken, CcdClient ccdClient) {
        this.bulkDetails = bulkDetails;
        this.submitEvent = submitEvent;
        this.authToken = authToken;
        this.ccdClient = ccdClient;
    }

    @Override
    public void run() {

        log.info("Waiting: " + Thread.currentThread().getName());
        String caseId = String.valueOf(submitEvent.getCaseId());
        try {
            log.info("Moving to accepted state");
            CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
            casePreAcceptType.setCaseAccepted(YES);
            casePreAcceptType.setDateAccepted(UtilHelper.formatCurrentDate2(LocalDate.now()));
            submitEvent.getCaseData().setPreAcceptCase(casePreAcceptType);
            CCDRequest returnedRequest = ccdClient.startEventForCasePreAcceptBulkSingle(authToken,
                    UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                    bulkDetails.getJurisdiction(), caseId);
            ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(),
                    UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                    bulkDetails.getJurisdiction(), returnedRequest, caseId);
        } catch (IOException e) {
            log.error("Error processing bulk pre accept threads");
        }
    }
}
