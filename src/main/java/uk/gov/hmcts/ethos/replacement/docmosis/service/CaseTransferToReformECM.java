package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;

@Slf4j
@RequiredArgsConstructor
@Service("caseTransferToReformECM")
public class CaseTransferToReformECM {
    private final PersistentQHelperService persistentQHelperService;
    public static final String CASE_STATE_ERROR_MSG = "Case %s should be in the submitted or " +
            "accepted state to be transferred to Reform ECM";

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public List<String> createCaseTransferToReformECM(CaseDetails caseDetails, String userToken) {
        var errors = new ArrayList<String>();
        validateCase(caseDetails, errors);
        if (!errors.isEmpty()) {
            return errors;
        }
        return transferCases(caseDetails, userToken);
    }


    private boolean checkCaseState(CaseDetails caseDetails) {
        return caseDetails.getState().equals(SUBMITTED_STATE) || caseDetails.getState().equals(ACCEPTED_STATE);
    }

    public void validateCase(CaseDetails caseDetails, List<String> errors) {
        if (!checkCaseState(caseDetails)) {
            errors.add(String.format(CASE_STATE_ERROR_MSG, caseDetails.getCaseData().getEthosCaseReference()));
        }
    }

    private List<String> transferCases(CaseDetails caseDetails, String userToken) {
        var errors = new ArrayList<String>();
        createCaseTransferEvent(caseDetails, userToken, errors);
        return errors;
    }

    private void createCaseTransferEvent(CaseDetails caseDetails, String userToken,
                                         List<String> errors) {
        persistentQHelperService.sendCreationEventToSingles(
                userToken,
                caseDetails.getCaseTypeId(),
                caseDetails.getJurisdiction(),
                errors,
                List.of(caseDetails.getCaseData().getEthosCaseReference()),
                caseDetails.getCaseData().getOfficeCT().getValue().getCode(),
                caseDetails.getCaseData().getPositionTypeCT(),
                ccdGatewayBaseUrl,
                caseDetails.getCaseData().getReasonForCT(),
                SINGLE_CASE_TYPE,
                NO,
                null
        );

        caseDetails.getCaseData().setLinkedCaseCT("Transferred to " + caseDetails.getCaseData().getOfficeCT().getValue().getCode());
        caseDetails.getCaseData().setPositionType(caseDetails.getCaseData().getPositionTypeCT());
        log.info("Clearing the CT payload for case: " + caseDetails.getCaseData().getEthosCaseReference());
        caseDetails.getCaseData().setOfficeCT(null);
        caseDetails.getCaseData().setPositionTypeCT(null);
        caseDetails.getCaseData().setStateAPI(null);
    }
}
