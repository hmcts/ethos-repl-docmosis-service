package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Service("caseManagementForCaseWorkerService")
public class CaseManagementForCaseWorkerService {

    public CaseData preAcceptCase(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);
        if (caseData.getPreAcceptCase() != null) {
            if (caseData.getPreAcceptCase().getCaseAccepted().equals("Yes")) {
                caseData.setState(ACCEPTED_STATE);
            } else {
                caseData.setState(REJECTED_STATE);
            }
        }
        return caseData;
    }

    private CaseData getCaseData(CCDRequest ccdRequest) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        return caseDetails.getCaseData();
    }

}
