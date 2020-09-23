package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("addSingleCaseToMultipleService")
public class AddSingleCaseToMultipleService {

    private final MultipleHelperService multipleHelperService;
    private final MultipleCasesReadingService multipleCasesReadingService;

    @Autowired
    public AddSingleCaseToMultipleService(MultipleHelperService multipleHelperService,
                                          MultipleCasesReadingService multipleCasesReadingService) {
        this.multipleHelperService = multipleHelperService;
        this.multipleCasesReadingService = multipleCasesReadingService;
    }

    public void addSingleCaseToMultipleLogic(String userToken, CaseDetails caseDetails, List<String> errors) {

        CaseData caseData = caseDetails.getCaseData();

        String leadClaimant = caseData.getLeadClaimant();
        String updatedMultipleReference = caseData.getMultipleReference();

        String multipleCaseTypeId = MultiplesHelper.getMultipleCaseTypeIdFromSingle(caseDetails.getCaseTypeId());

        log.info("Pulling the multiple case");

        List<SubmitMultipleEvent> multipleEvents =
                multipleCasesReadingService.retrieveMultipleCases(
                        userToken,
                        multipleCaseTypeId,
                        updatedMultipleReference);

        SubmitMultipleEvent multipleEvent = multipleEvents.get(0);

        MultipleData multipleData = multipleEvent.getCaseData();

        String ethosCaseReference = caseData.getEthosCaseReference();

        log.info("If multiple is empty the single will be always the lead");

        if (multipleData.getCaseIdCollection() == null || multipleData.getCaseIdCollection().isEmpty()) {

            leadClaimant = YES;

        }

        addSingleCaseToCaseIds(userToken, multipleCaseTypeId, caseDetails.getJurisdiction(),
                multipleData, leadClaimant, ethosCaseReference, caseDetails.getCaseId(), errors);

        log.info("Generate and upload excel with sub multiple and send update to multiple");

        multipleHelperService.moveCasesAndSendUpdateToMultiple(userToken, caseData.getSubMultipleReference(),
                caseDetails.getJurisdiction(), multipleCaseTypeId, String.valueOf(multipleEvent.getCaseId()),
                multipleData, new ArrayList<>(Collections.singletonList(ethosCaseReference)), errors);

        log.info("Update multipleRef, multiple and lead");

        updateCaseDataForMultiple(caseData, updatedMultipleReference, leadClaimant);

        log.info("Reset mid fields");

        caseData.setSubMultipleReference(null);

    }

    private void updateCaseDataForMultiple(CaseData caseData, String newMultipleReference, String leadClaimant) {

        caseData.setMultipleReference(newMultipleReference);
        caseData.setCaseType(MULTIPLE_CASE_TYPE);
        caseData.setLeadClaimant(leadClaimant);

    }

    private void addSingleCaseToCaseIds(String userToken, String multipleCaseTypeId, String jurisdiction, MultipleData multipleData,
                                        String leadClaimant, String ethosCaseReference, String caseId, List<String> errors) {

        if (leadClaimant.equals(YES)) {

            log.info("Lead: Adding the single case id to the TOP of case ids collection in multiple");

            String currentLeadCase = MultiplesHelper.getLeadFromCaseIds(multipleData);

            if (!currentLeadCase.isEmpty()) {

                log.info("There is already a lead case in the multiple. Sending update to be no LEAD");

                multipleHelperService.sendCreationUpdatesToSinglesNoConfirmation(userToken, multipleCaseTypeId,
                        jurisdiction, multipleData, errors,
                        new ArrayList<>(Collections.singletonList(currentLeadCase)), "");

            }

            multipleHelperService.addLeadMarkUp(
                    userToken, multipleCaseTypeId, multipleData, ethosCaseReference, caseId);

            MultiplesHelper.addLeadToCaseIds(multipleData, ethosCaseReference);

        } else {

            log.info("No Lead: Adding the single case id to the case ids collection in multiple");

            MultiplesHelper.addCaseIds(
                    multipleData, new ArrayList<>(Collections.singletonList(ethosCaseReference)));

        }

    }

}
