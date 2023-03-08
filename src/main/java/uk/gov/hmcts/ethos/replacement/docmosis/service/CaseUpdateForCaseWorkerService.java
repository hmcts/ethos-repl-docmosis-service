package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

@Slf4j
@Service("CaseUpdateForCaseWorkerService")
public class CaseUpdateForCaseWorkerService {

    private static final String MESSAGE = "Failed to update case for case id : ";
    private final CcdClient ccdClient;
    private final DefaultValuesReaderService defaultValuesReaderService;

    @Autowired
    public CaseUpdateForCaseWorkerService(CcdClient ccdClient,
                                          DefaultValuesReaderService defaultValuesReaderService) {
        this.ccdClient = ccdClient;
        this.defaultValuesReaderService = defaultValuesReaderService;
    }

    public SubmitEvent caseUpdateRequest(CCDRequest ccdRequest, String authToken) {
        var caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());

        try {
            String caseId = ccdRequest.getCaseDetails().getCaseId();
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken,
                    caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), caseId);
            String managingOffice = returnedRequest.getCaseDetails().getCaseData().getManagingOffice() != null
                    ? returnedRequest.getCaseDetails().getCaseData().getManagingOffice()
                    : "";
            var defaultValues = defaultValuesReaderService.getDefaultValues(
                    managingOffice, caseDetails.getCaseTypeId());
            returnedRequest.getCaseDetails().getCaseData().setPositionType(defaultValues.getPositionType());

            log.info("Post Default values added to the case: " + defaultValues);
            return ccdClient.submitEventForCase(authToken, returnedRequest.getCaseDetails().getCaseData(),
                    returnedRequest.getCaseDetails().getCaseTypeId(),
                    returnedRequest.getCaseDetails().getJurisdiction(),
                    returnedRequest, caseId);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }
}