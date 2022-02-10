package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service("addSingleCaseToMultipleService")
public class AddSingleCaseToMultipleService {

    private final MultipleHelperService multipleHelperService;
    private final MultipleCasesReadingService multipleCasesReadingService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public void addSingleCaseToMultipleLogic(String userToken, CaseData caseData, String caseTypeId,
                                             String jurisdiction, String caseId, List<String> errors) {

        if (caseData.getMultipleFlag().equals(NO)
                && caseData.getEcmCaseType().equals(MULTIPLE_CASE_TYPE)) {

            String leadClaimant = caseData.getLeadClaimant();
            String updatedMultipleReference = caseData.getMultipleReference();
            String multipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseTypeId);
            String subMultipleName;
            if (isNullOrEmpty(caseData.getSubMultipleName())) {
                subMultipleName = "";
            } else {
                subMultipleName = caseData.getSubMultipleName();
            }

            log.info("Pulling the multiple case: " + updatedMultipleReference);
            List<SubmitMultipleEvent> multipleEvents =
                    multipleCasesReadingService.retrieveMultipleCases(
                            userToken,
                            multipleCaseTypeId,
                            updatedMultipleReference);

            SubmitMultipleEvent multipleEvent = multipleEvents.get(0);
            var multipleData = multipleEvent.getCaseData();

            List<String> ethosCaseRefCollection = multipleHelperService.getEthosCaseRefCollection(userToken,
                    multipleData, errors);

            String newEthosCaseReferenceToAdd = caseData.getEthosCaseReference();

            if (ethosCaseRefCollection.isEmpty()) {
                leadClaimant = YES;
            }

            var parentMultipleCaseId = String.valueOf(multipleEvent.getCaseId());
            addNewLeadToMultiple(userToken, multipleCaseTypeId, jurisdiction,
                    multipleData, leadClaimant, newEthosCaseReferenceToAdd, caseId, errors, parentMultipleCaseId);

            multipleHelperService.moveCasesAndSendUpdateToMultiple(userToken, caseData.getSubMultipleName(),
                    jurisdiction, multipleCaseTypeId, String.valueOf(multipleEvent.getCaseId()),
                    multipleData, new ArrayList<>(Collections.singletonList(newEthosCaseReferenceToAdd)), errors);

            var multipleCaseId = String.valueOf(multipleEvent.getCaseId());
            updateCaseDataForMultiple(caseData, updatedMultipleReference, leadClaimant, multipleCaseId);

            caseData.setSubMultipleName(subMultipleName);

            caseData.setMultipleFlag(YES);
        }
    }

    private void updateCaseDataForMultiple(CaseData caseData, String newMultipleReference,
                                           String leadClaimant, String multipleCaseId) {
        caseData.setMultipleReference(newMultipleReference);
        caseData.setEcmCaseType(MULTIPLE_CASE_TYPE);

        caseData.setLeadClaimant(leadClaimant);
        caseData.setMultipleReferenceLinkMarkUp(getLinkMarkUp(multipleCaseId, newMultipleReference));
    }

    private String getLinkMarkUp(String multipleCaseId, String newMultipleReference) {
        return MultiplesHelper.generateMarkUp(ccdGatewayBaseUrl, multipleCaseId, newMultipleReference);
    }

    private void addNewLeadToMultiple(String userToken, String multipleCaseTypeId, String jurisdiction,
                                      MultipleData multipleData, String leadClaimant,
                                      String newEthosCaseReferenceToAdd, String caseId, List<String> errors,
                                      String multipleReferenceLinkMarkUp) {
        if (leadClaimant.equals(YES)) {

            String currentLeadCase = MultiplesHelper.getCurrentLead(multipleData.getLeadCase());

            if (!currentLeadCase.isEmpty()) {
                multipleHelperService.sendCreationUpdatesToSinglesWithoutConfirmation(userToken, multipleCaseTypeId,
                        jurisdiction, multipleData, errors,
                        new ArrayList<>(Collections.singletonList(currentLeadCase)), "",
                        multipleReferenceLinkMarkUp);
            }

            multipleHelperService.addLeadMarkUp(userToken, multipleCaseTypeId, multipleData,
                    newEthosCaseReferenceToAdd, caseId);
        }
    }
}
