package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("multipleCreationMidEventValidationService")
public class MultipleCreationMidEventValidationService {

    public static final String CASE_STATE_ERROR = " cases have not been Accepted.";
    public static final String CASE_BELONG_MULTIPLE_ERROR = " cases already belong to a different multiple";
    public static final String CASE_EXIST_ERROR = " cases do not exist.";
    public static final String LEAD_STATE_ERROR = " lead case has not been Accepted.";
    public static final String LEAD_BELONG_MULTIPLE_ERROR = " lead case already belongs to a different multiple";
    public static final String LEAD_EXIST_ERROR = " lead case does not exist.";
    public static final int MULTIPLE_MAX_SIZE = 50;

    private final SingleCasesReadingService singleCasesReadingService;

    @Autowired
    public MultipleCreationMidEventValidationService(SingleCasesReadingService singleCasesReadingService) {
        this.singleCasesReadingService = singleCasesReadingService;
    }

    public void multipleCreationValidationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        if (multipleDetails.getCaseData().getMultipleSource() != null
                &&
                ( multipleDetails.getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)
                        || multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)
                )) {

            log.info("Skipping validation as ET1 Online Case");

        } else {

            log.info("Validating multiple creation");

            MultipleData multipleData = multipleDetails.getCaseData();

            log.info("Checking lead case");

            if (!isNullOrEmpty(multipleData.getLeadCase())) {

                log.info("Validating lead case introduced by user: " + multipleData.getLeadCase());

                validateCases(userToken, multipleDetails.getCaseTypeId(), multipleData,
                        new ArrayList<>(Collections.singletonList(multipleData.getLeadCase())), errors, true);

            }

            List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIdsForMidEvent(multipleData);

            log.info("Validating case id collection size: " + ethosCaseRefCollection.size());

            validateCaseReferenceCollectionSize(ethosCaseRefCollection, errors);

            validateCases(userToken, multipleDetails.getCaseTypeId(), multipleData, ethosCaseRefCollection, errors, false);

        }

    }

    private void validateCaseReferenceCollectionSize(List<String> ethosCaseRefCollection, List<String> errors) {

        if (ethosCaseRefCollection.size() > MULTIPLE_MAX_SIZE) {

            log.info("Case id collection reached the max size");

            errors.add("There are " + ethosCaseRefCollection.size() + " cases in the multiple. The limit is " + MULTIPLE_MAX_SIZE + ".");

        }

    }

    private void validateCases(String userToken, String multipleCaseTypeId, MultipleData multipleData,
                               List<String> caseRefCollection, List<String> errors, boolean isLead) {

        if (errors.isEmpty() && !caseRefCollection.isEmpty()) {

            List<SubmitEvent> submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                    multipleCaseTypeId, caseRefCollection, multipleData.getMultipleSource());

            log.info("Validate number of cases returned");

            validateNumberCasesReturned(submitEvents, errors, isLead, caseRefCollection);

            log.info("Validating cases");

            validateSingleCasesState(submitEvents, errors, isLead);

        }

    }

    private void validateNumberCasesReturned(List<SubmitEvent> submitEvents, List<String> errors, boolean isLead,
                                             List<String> caseRefCollection) {

        if (caseRefCollection.size() != submitEvents.size()) {

            log.info("List returned is different");

            List<String> listCasesDoNotExistError = caseRefCollection.stream()
                    .filter(caseRef ->
                            submitEvents.stream()
                                    .noneMatch(submitEvent ->
                                            submitEvent.getCaseData().getEthosCaseReference().equals(caseRef)))
                    .collect(Collectors.toList());

            if (!listCasesDoNotExistError.isEmpty()) {

                String errorMessage = isLead ? LEAD_EXIST_ERROR : CASE_EXIST_ERROR;

                errors.add(listCasesDoNotExistError + errorMessage);

            }

        }

    }

    private void validateSingleCasesState(List<SubmitEvent> submitEvents, List<String> errors, boolean isLead) {

        List<String> listCasesStateError = new ArrayList<>();

        List<String> listCasesMultipleError = new ArrayList<>();

        for (SubmitEvent submitEvent : submitEvents) {

            if (!submitEvent.getState().equals(ACCEPTED_STATE)) {

                log.info("VALIDATION ERROR: state of single case not Accepted");

                listCasesStateError.add(submitEvent.getCaseData().getEthosCaseReference());

            }

            if (submitEvent.getCaseData().getMultipleReference() != null
                    && !submitEvent.getCaseData().getMultipleReference().trim().isEmpty()) {

                log.info("VALIDATION ERROR: already another multiple");

                listCasesMultipleError.add(submitEvent.getCaseData().getEthosCaseReference());

            }

        }

        if (!listCasesStateError.isEmpty()) {

            String errorMessage = isLead ? LEAD_STATE_ERROR : CASE_STATE_ERROR;

            errors.add(listCasesStateError + errorMessage);

        }

        if (!listCasesMultipleError.isEmpty()) {

            String errorMessage = isLead ? LEAD_BELONG_MULTIPLE_ERROR : CASE_BELONG_MULTIPLE_ERROR;

            errors.add(listCasesMultipleError + errorMessage);

        }

    }

}
