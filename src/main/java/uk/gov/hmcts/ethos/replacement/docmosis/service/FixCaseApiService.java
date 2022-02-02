package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;

import java.util.List;

@RequiredArgsConstructor
@Service("fixCaseApiService")
public class FixCaseApiService {

    private final MultipleCasesReadingService multipleCasesReadingService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public void checkUpdateMultipleReference(CCDRequest ccdRequest, String userToken) {
        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        if (caseData.getEcmCaseType().equals("Multiple")
                && (caseData.getMultipleReference().length() == 16
                || caseData.getMultipleReferenceLinkMarkUp() == null)) {
            String multipleCaseTypeId = ccdRequest.getCaseDetails().getCaseTypeId() + "_Multiple";
            List<SubmitMultipleEvent> submitMultipleEvents =
                    retrieveMultiCase(userToken, multipleCaseTypeId, caseData.getMultipleReference());
            if (!submitMultipleEvents.isEmpty()) {
                String multipleCaseId = String.valueOf(submitMultipleEvents.get(0).getCaseId());
                caseData.setMultipleReference(
                        String.valueOf(submitMultipleEvents.get(0).getCaseData().getMultipleReference()));
                if (caseData.getMultipleReferenceLinkMarkUp() == null) {
                    caseData.setMultipleReferenceLinkMarkUp(
                            getLinkMarkUp(multipleCaseId, caseData.getMultipleReference()));
                }
            }
        }
    }

    private List<SubmitMultipleEvent> retrieveMultiCase(String userToken, String multipleCaseTypeId,
                                                        String multipleReference) {
        if (multipleReference.length() == 16) {
            return multipleCasesReadingService.retrieveMultipleCasesCcdReference(
                    userToken,
                    multipleCaseTypeId,
                    multipleReference);
        } else {
            return multipleCasesReadingService.retrieveMultipleCases(
                    userToken,
                    multipleCaseTypeId,
                    multipleReference);
        }
    }

    private String getLinkMarkUp(String multipleCaseId, String newMultipleReference) {
        return MultiplesHelper.generateMarkUp(ccdGatewayBaseUrl, multipleCaseId, newMultipleReference);
    }

}