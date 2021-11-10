package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;

@Slf4j
@Service("multipleCloseEventValidationService")
public class MultipleCloseEventValidationService {

    private final SingleCasesReadingService singleCasesReadingService;
    private final MultipleHelperService multipleHelperService;
    private final EventValidationService eventValidationService;
    private static final String JURISDICTION = "Jurisdiction";
    private static final String HEARING_STATUS = "HearingStatus";
    private static final String JUDGE_ALLOCATION = "JudgeAllocation";
    private final static List<String> validationConditions = Arrays.asList("Jurisdiction", "HearingStatus",
            "JudgeAllocation");

    @Autowired
    public MultipleCloseEventValidationService(SingleCasesReadingService singleCasesReadingService,
                                               MultipleHelperService multipleHelperService,
                                               EventValidationService eventValidationService) {
        this.singleCasesReadingService = singleCasesReadingService;
        this.multipleHelperService = multipleHelperService;
        this.eventValidationService = eventValidationService;
    }

    public List<String> validateCaseClosingConditions(String userToken, MultipleDetails multipleDetails) {
        List<String> errors = new ArrayList<>();

        for (var condition : validationConditions) {
            validateCondition(userToken, multipleDetails, errors, condition);
        }

        return errors;
    }

    private void validateCondition(String userToken, MultipleDetails multipleDetails,
                                   List<String> errors, String conditionToValidate) {

        var multipleData = multipleDetails.getCaseData();
        var ethosCaseRefCollection = multipleHelperService.getEthosCaseRefCollection(userToken,
                multipleData, errors);

        if (!ethosCaseRefCollection.isEmpty()) {
            var submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                    multipleDetails.getCaseTypeId(), ethosCaseRefCollection, multipleData.getMultipleSource());

            for (SubmitEvent event : submitEvents) {

                if(JURISDICTION.equals(conditionToValidate)) {
                    eventValidationService.validateJurisdictionOutcome(event.getCaseData(),
                            event.getState().equals(REJECTED_STATE), true, errors);
                } else if(HEARING_STATUS.equals(conditionToValidate)) {
                    eventValidationService.validateHearingStatusForCaseCloseEvent(event.getCaseData(), errors);
                } else if(JUDGE_ALLOCATION.equals(conditionToValidate)) {
                    eventValidationService.validateHearingJudgeAllocationForCaseCloseEvent(event.getCaseData(),
                            errors);
                }

                if(!errors.isEmpty()) {
                    break;
                }
            }
        }
    }
}
