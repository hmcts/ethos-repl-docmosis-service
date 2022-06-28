package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.generic.RETURN;
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
    private static final String ET_SCOTLAND = "ET_Scotland";
    private static final String ET_ENGLAND_AND_WALES = "ET_EnglandWales";
    private static final String SCOTLAND = "Scotland";

    public List<String> createCaseTransferToReformECM(CaseDetails caseDetails, String userToken) {
        var errors = new ArrayList<String>();
        var targetCaseType = getTargetOffice(caseDetails);
        return createCaseTransferEvent(caseDetails, targetCaseType, userToken, errors);
    }

    private String getTargetOffice(CaseDetails caseDetails) {
        if(SCOTLAND.equals(caseDetails.getCaseTypeId())){
           return ET_SCOTLAND;
        } else {
            return ET_ENGLAND_AND_WALES;
        }
    }

    private List<String> createCaseTransferEvent(CaseDetails caseDetails, String targetCaseType, String userToken,
                                                 List<String> errors) {
        var positionTypeCT = "Case transferred - same country";
        persistentQHelperService.sendCreationEventToSinglesReformECM(
            userToken,
            caseDetails.getCaseTypeId(),
            caseDetails.getJurisdiction(),
            errors,
            List.of(caseDetails.getCaseData().getEthosCaseReference()),
            targetCaseType,
            positionTypeCT,
            ccdGatewayBaseUrl,
            caseDetails.getCaseData().getReasonForCT(),
            SINGLE_CASE_TYPE,
            NO,
            null
        );

        caseDetails.getCaseData().setLinkedCaseCT("Transferred to " + targetCaseType);
        caseDetails.getCaseData().setPositionType(caseDetails.getCaseData().getPositionTypeCT());
        log.info("Clearing the CT payload for case: " + caseDetails.getCaseData().getEthosCaseReference());
        caseDetails.getCaseData().setOfficeCT(null);
        caseDetails.getCaseData().setPositionTypeCT(null);
        caseDetails.getCaseData().setStateAPI(null);

        return errors;
    }
}
