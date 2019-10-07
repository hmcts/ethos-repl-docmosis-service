package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.POST_DEFAULT_XLSX_FILE_PATH;

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
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        try {
            String caseId = ccdRequest.getCaseDetails().getCaseId();
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), caseId);
            DefaultValues defaultValues = defaultValuesReaderService.getDefaultValues(POST_DEFAULT_XLSX_FILE_PATH, caseDetails);
            ccdRequest.getCaseDetails().getCaseData().setPositionType(defaultValues.getPositionType());
            log.info("Post Default values added to the case: " + defaultValues);
            return ccdClient.submitEventForCase(authToken, caseDetails.getCaseData(), caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), returnedRequest, caseId);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }
}