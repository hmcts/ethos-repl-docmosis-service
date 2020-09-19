package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;

@Slf4j
@Service("multipleCreationMidEventValidationService")
public class MultipleCreationMidEventValidationService {

    public static final String CASE_STATE_ERROR = " cases have not been Accepted.";
    public static final String CASE_BELONG_MULTIPLE_ERROR = " cases belong already to a different multiple";
    public static final int MULTIPLE_MAX_SIZE = 50;

    private final SingleCasesReadingService singleCasesReadingService;

    @Autowired
    public MultipleCreationMidEventValidationService(SingleCasesReadingService singleCasesReadingService) {
        this.singleCasesReadingService = singleCasesReadingService;
    }

    public void multipleCreationValidationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        if (multipleDetails.getCaseData().getMultipleSource() != null
                && multipleDetails.getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)) {

            log.info("Skipping validation as ET1 Online Case");

        } else {

            log.info("Validating multiple creation");

            MultipleData multipleData = multipleDetails.getCaseData();

            log.info("Checking lead case");

            if (!isNullOrEmpty(multipleData.getLeadCase())) {

                log.info("Adding lead case introduced by user: " + multipleData.getLeadCase());

                MultiplesHelper.addLeadToCaseIds(multipleData, multipleData.getLeadCase());

            }

            List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleData);

            log.info("Validating case id collection size: " + ethosCaseRefCollection.size());

            validateCaseReferenceCollectionSize(ethosCaseRefCollection, errors);

            if (errors.isEmpty() && !ethosCaseRefCollection.isEmpty()) {

                List<SubmitEvent> submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                        multipleDetails.getCaseTypeId(), ethosCaseRefCollection);

                log.info("Validating cases: " + submitEvents);

                validateSingleCasesState(submitEvents, errors);

            }

        }

    }

    private void validateCaseReferenceCollectionSize(List<String> ethosCaseRefCollection, List<String> errors) {

        if (ethosCaseRefCollection.size() > MULTIPLE_MAX_SIZE) {

            log.info("Case id collection reached the max size");

            errors.add("Three are " + ethosCaseRefCollection.size() + " cases in the multiple. The limit is " + MULTIPLE_MAX_SIZE + ".");

        }

    }

    private void validateSingleCasesState(List<SubmitEvent> submitEvents, List<String> errors) {

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

            errors.add(listCasesMultipleError + CASE_STATE_ERROR);

        }

        if (!listCasesMultipleError.isEmpty()) {

            errors.add(listCasesMultipleError + CASE_BELONG_MULTIPLE_ERROR);

        }

    }

}
