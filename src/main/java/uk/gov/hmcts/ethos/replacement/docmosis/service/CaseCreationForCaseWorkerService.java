package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("caseCreationForCaseWorkerService")
public class CaseCreationForCaseWorkerService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private final CcdClient ccdClient;
    private final SingleReferenceService singleReferenceService;
    private final MultipleReferenceService multipleReferenceService;
    private final PersistentQHelperService persistentQHelperService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public SubmitEvent caseCreationRequest(CCDRequest ccdRequest, String userToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        try {
            return ccdClient.submitCaseCreation(userToken, caseDetails,
                    ccdClient.startCaseCreation(userToken, caseDetails));
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    public CaseData generateCaseRefNumbers(CCDRequest ccdRequest) {
        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        if (caseData.getCaseRefNumberCount() != null && Integer.parseInt(caseData.getCaseRefNumberCount()) > 0) {
            log.info("Case Type: " + ccdRequest.getCaseDetails().getCaseTypeId());
            log.info("Count: " + Integer.parseInt(caseData.getCaseRefNumberCount()));
            caseData.setStartCaseRefNumber(singleReferenceService.createReference(
                    ccdRequest.getCaseDetails().getCaseTypeId(),
                    Integer.parseInt(caseData.getCaseRefNumberCount())));
            caseData.setMultipleRefNumber(multipleReferenceService.createReference(
                    UtilHelper.getBulkCaseTypeId(ccdRequest.getCaseDetails().getCaseTypeId()), 1));
        }
        return caseData;
    }

    public void createCaseTransfer(CaseDetails caseDetails, List<String> errors, String userToken) {

        CaseData caseData = caseDetails.getCaseData();

        persistentQHelperService.sendCreationEventToSinglesWithoutConfirmation(
                userToken,
                caseDetails.getCaseTypeId(),
                caseDetails.getJurisdiction(),
                errors,
                caseData.getEthosCaseReference(),
                caseData.getOfficeCT().getValue().getCode(),
                caseData.getPositionTypeCT(),
                ccdGatewayBaseUrl
        );

        caseData.setLinkedCaseCT("Transferred to " + caseData.getOfficeCT().getValue().getCode());
        caseData.setPositionType(caseData.getPositionTypeCT());

        log.info("Clearing the CT payload");

        caseData.setOfficeCT(null);
        caseData.setPositionTypeCT(null);

    }

}