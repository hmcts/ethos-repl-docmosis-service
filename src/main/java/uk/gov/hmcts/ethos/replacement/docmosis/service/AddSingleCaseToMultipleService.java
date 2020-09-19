package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
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

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

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

        //Should come from caseData
        String leadClaimant = YES;
        String newMultipleReference = "246000";
        String newSubMultipleReference = "246000/2";

        String oldMultipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());
        String multipleCaseTypeId = oldMultipleCaseTypeId.substring(0, oldMultipleCaseTypeId.length() - 1);

        log.info("Pulling the multiple case");

        List<SubmitMultipleEvent> multipleEvents =
                multipleCasesReadingService.retrieveMultipleCases(
                        userToken,
                        multipleCaseTypeId,
                        newMultipleReference);

        SubmitMultipleEvent multipleEvent = multipleEvents.get(0);

        MultipleData multipleData = multipleEvent.getCaseData();

        String ethosCaseReference = caseData.getEthosCaseReference();

        addSingleCaseToCaseId(userToken, multipleCaseTypeId, multipleData, leadClaimant, ethosCaseReference);

        log.info("Generate and upload excel with sub multiple and send update to multiple");

        multipleHelperService.moveCasesAndSendUpdateToMultiple(userToken, newSubMultipleReference,
                caseDetails.getJurisdiction(), multipleCaseTypeId, String.valueOf(multipleEvent.getCaseId()),
                multipleData, new ArrayList<>(Collections.singletonList(ethosCaseReference)), errors);

        log.info("Update multipleRef, multiple and lead");

        updateCaseDataForMultiple(caseData, newMultipleReference, leadClaimant);

    }

    private void updateCaseDataForMultiple(CaseData caseData, String newMultipleReference, String leadClaimant) {

        caseData.setMultipleReference(newMultipleReference);
        caseData.setCaseType(MULTIPLE_CASE_TYPE);
        caseData.setLeadClaimant(leadClaimant);

    }

    private void addSingleCaseToCaseId(String userToken, String multipleCaseTypeId, MultipleData multipleData,
                                       String leadClaimant, String ethosCaseReference) {

        if (leadClaimant.equals(YES)) {

            log.info("Lead: Adding the single case id to the TOP of case ids collection in multiple");

            multipleHelperService.addLeadMarkUp(
                    userToken, multipleCaseTypeId, multipleData, ethosCaseReference);

        } else {

            log.info("No Lead: Adding the single case id to the case ids collection in multiple");

            MultiplesHelper.addCaseIds(
                    multipleData, new ArrayList<>(Collections.singletonList(ethosCaseReference)));

        }

    }

}
