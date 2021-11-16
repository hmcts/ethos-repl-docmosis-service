package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;

@Slf4j
@Service("multipleCloseEventValidationService")
public class MultipleCloseEventValidationService {

    private final SingleCasesReadingService singleCasesReadingService;
    private final MultipleHelperService multipleHelperService;
    private final EventValidationService eventValidationService;
    private static final String JURISDICTION_OUTCOME = "Jurisdiction Outcome";
    private static final String HEARING_STATUS = "HearingStatus";
    private static final String JUDGE_ALLOCATION = "JudgeAllocation";
    private static final String JUDGMENT_JURISDICTION = "Judgment Jurisdiction";
    private static final List<String> validationConditions = List.of(JUDGMENT_JURISDICTION, JURISDICTION_OUTCOME,
            HEARING_STATUS, JUDGE_ALLOCATION);

    @Autowired
    public MultipleCloseEventValidationService(SingleCasesReadingService singleCasesReadingService,
                                               MultipleHelperService multipleHelperService,
                                               EventValidationService eventValidationService) {
        this.singleCasesReadingService = singleCasesReadingService;
        this.multipleHelperService = multipleHelperService;
        this.eventValidationService = eventValidationService;
    }

    public List<String> validateCasesBeforeCloseEvent(String userToken, MultipleDetails multipleDetails) {
        List<String> errors = new ArrayList<>();
        var ethosCaseRefCollection = multipleHelperService.getEthosCaseRefCollection(userToken,
                multipleDetails.getCaseData(), errors);

        if (ethosCaseRefCollection.isEmpty()) {
            return errors;
        } else {
            var submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                    multipleDetails.getCaseTypeId(), ethosCaseRefCollection,
                    multipleDetails.getCaseData().getMultipleSource());

            for (var submitEvent : submitEvents) {
                validateCase(submitEvent, errors);
                if (!errors.isEmpty()) {
                    break;
                }
            }
        }

        return errors;
    }

    private void validateCase(SubmitEvent submitEvent, List<String> errors) {
        for (var conditionToValidate : validationConditions) {
            if (JURISDICTION_OUTCOME.equals(conditionToValidate)) {
                eventValidationService.validateJurisdictionOutcome(submitEvent.getCaseData(),
                        submitEvent.getState().equals(REJECTED_STATE), true, errors);
            } else if (JUDGMENT_JURISDICTION.equals(conditionToValidate)) {
                eventValidationService.validateJudgementsHasJurisdiction(submitEvent.getCaseData(),
                        submitEvent.getState().equals(REJECTED_STATE), errors);
            } else if (HEARING_STATUS.equals(conditionToValidate)) {
                eventValidationService.validateHearingStatusForCaseCloseEvent(submitEvent.getCaseData(), errors);
            } else if (JUDGE_ALLOCATION.equals(conditionToValidate)) {
                eventValidationService.validateHearingJudgeAllocationForCaseCloseEvent(submitEvent.getCaseData(),
                        errors);
            }

            if (!errors.isEmpty()) {
                return;
            }
        }
    }

}
