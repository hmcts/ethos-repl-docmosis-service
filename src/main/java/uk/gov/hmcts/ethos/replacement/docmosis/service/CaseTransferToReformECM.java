package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.POSITION_TYPE_CASE_TRANSFERRED_REFORM_ECM;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

@Slf4j
@RequiredArgsConstructor
@Service("caseTransferToReformECM")
public class CaseTransferToReformECM {
    private static final String ET_SCOTLAND = "ET_Scotland";
    private static final String ET_ENGLAND_AND_WALES = "ET_EnglandWales";
    private static final String SCOTLAND = "Scotland";

    private final PersistentQHelperService persistentQHelperService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public List<String> createCaseTransferToReformECM(CaseDetails caseDetails, String userToken) {
        var errors = new ArrayList<String>();
        var targetCaseType = getTargetOffice(caseDetails);
        return createCaseTransferEvent(caseDetails, targetCaseType, userToken, errors);
    }

    private String getTargetOffice(CaseDetails caseDetails) {
        if (SCOTLAND.equals(caseDetails.getCaseTypeId())) {
            return ET_SCOTLAND;
        } else {
            return ET_ENGLAND_AND_WALES;
        }
    }

    private List<String> createCaseTransferEvent(CaseDetails caseDetails, String targetCaseType, String userToken,
                                                 List<String> errors) {
        String ethosCaseReference = caseDetails.getCaseData().getEthosCaseReference();
        String positionType = caseDetails.getCaseData().getPositionType();
        persistentQHelperService.sendCreationEventToSinglesReformECM(
            userToken,
            caseDetails.getCaseTypeId(),
            caseDetails.getJurisdiction(),
            errors,
            List.of(ethosCaseReference),
            targetCaseType,
            positionType,
            ccdGatewayBaseUrl,
            caseDetails.getCaseData().getReasonForCT(),
            SINGLE_CASE_TYPE,
            NO,
            null
        );

        caseDetails.getCaseData().setCurrentPosition(POSITION_TYPE_CASE_TRANSFERRED_REFORM_ECM);
        caseDetails.getCaseData().setLinkedCaseCT(POSITION_TYPE_CASE_TRANSFERRED_REFORM_ECM);
        caseDetails.getCaseData().setPositionType(POSITION_TYPE_CASE_TRANSFERRED_REFORM_ECM);
        log.info("Clearing the CT payload for case: " +  ethosCaseReference);
        caseDetails.getCaseData().setOfficeCT(null);
        caseDetails.getCaseData().setPositionTypeCT(null);
        caseDetails.getCaseData().setStateAPI(null);

        return errors;
    }

}
