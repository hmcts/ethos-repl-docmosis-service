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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

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

        log.info("Adding single case to multiple logic");

        if (caseData.getMultipleFlag().equals(NO) &&
            caseData.getCaseType().equals(MULTIPLE_CASE_TYPE)) {

            log.info("Case was single and now will be multiple");
            String leadClaimant = caseData.getLeadClaimant();
            String updatedMultipleReference = caseData.getMultipleReference();
            String multipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseTypeId);
            String subMultipleName;
            if(isNullOrEmpty(caseData.getSubMultipleName())){
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

            log.info("If multiple is empty the single will be always the lead");
            if (ethosCaseRefCollection.isEmpty()) {
                leadClaimant = YES;
            }

            var parentMultipleCaseId = String.valueOf(multipleEvent.getCaseId());
            addNewLeadToMultiple(userToken, multipleCaseTypeId, jurisdiction,
                    multipleData, leadClaimant, newEthosCaseReferenceToAdd, caseId, errors, parentMultipleCaseId);

            log.info("Generate and upload excel with sub multiple and send update to multiple");
            multipleHelperService.moveCasesAndSendUpdateToMultiple(userToken, caseData.getSubMultipleName(),
                    jurisdiction, multipleCaseTypeId, String.valueOf(multipleEvent.getCaseId()),
                    multipleData, new ArrayList<>(Collections.singletonList(newEthosCaseReferenceToAdd)), errors);

            log.info("Update multipleRef, multiple and lead");
            var multipleCaseId = String.valueOf(multipleEvent.getCaseId());
            updateCaseDataForMultiple(caseData, updatedMultipleReference, leadClaimant, multipleCaseId);

            log.info("Reset mid fields");
            caseData.setSubMultipleName(subMultipleName);

            log.info("Update check multiple flag");
            caseData.setMultipleFlag(YES);
        }
    }

    private void updateCaseDataForMultiple(CaseData caseData, String newMultipleReference,
                                           String leadClaimant, String multipleCaseId) {
        caseData.setMultipleReference(newMultipleReference);
        caseData.setCaseType(MULTIPLE_CASE_TYPE);

        log.info("setLeadClaimant is set to " + leadClaimant);
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

            log.info("Checking if there was a lead");
            String currentLeadCase = MultiplesHelper.getCurrentLead(multipleData.getLeadCase());

            if (!currentLeadCase.isEmpty()) {
                log.info("There is already a lead case in the multiple. Sending update to be no LEAD");
                multipleHelperService.sendCreationUpdatesToSinglesWithoutConfirmation(userToken, multipleCaseTypeId,
                        jurisdiction, multipleData, errors,
                        new ArrayList<>(Collections.singletonList(currentLeadCase)), "",
                        multipleReferenceLinkMarkUp);
            }

            log.info("Adding the new lead");
            multipleHelperService.addLeadMarkUp(userToken, multipleCaseTypeId, multipleData,
                    newEthosCaseReferenceToAdd, caseId);
        }
    }
}
