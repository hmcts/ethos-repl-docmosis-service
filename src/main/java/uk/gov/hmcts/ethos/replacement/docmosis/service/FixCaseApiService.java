package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;

@RequiredArgsConstructor
@Service("fixCaseApiService")
public class FixCaseApiService {

    private final MultipleCasesReadingService multipleCasesReadingService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public void checkUpdateMultipleReference(CaseDetails caseDetails, String userToken) {
        CaseData caseData = caseDetails.getCaseData();
        if (caseData.getParentMultipleCaseId() != null) {
            caseData.setParentMultipleCaseId(null);
        }

        if (MULTIPLE_CASE_TYPE.equals(caseData.getEcmCaseType())) {
            String multipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());
            List<SubmitMultipleEvent> submitMultipleEvents = multipleCasesReadingService.retrieveMultipleCases(
                    userToken, multipleCaseTypeId, caseData.getMultipleReference());
            if (!submitMultipleEvents.isEmpty()) {
                String multipleCaseId = String.valueOf(submitMultipleEvents.get(0).getCaseId());
                caseData.setMultipleReferenceLinkMarkUp(getLinkMarkUp(multipleCaseId, caseData.getMultipleReference()));
            }
        }
    }

    private String getLinkMarkUp(String multipleCaseId, String newMultipleReference) {
        return MultiplesHelper.generateMarkUp(ccdGatewayBaseUrl, multipleCaseId, newMultipleReference);
    }
}