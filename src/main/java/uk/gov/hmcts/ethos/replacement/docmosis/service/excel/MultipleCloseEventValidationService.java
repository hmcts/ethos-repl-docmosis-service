package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;

@Slf4j
@RequiredArgsConstructor
@Service("multipleCloseEventValidationService")
public class MultipleCloseEventValidationService {

    private final SingleCasesReadingService singleCasesReadingService;
    private final MultipleHelperService multipleHelperService;
    private final EventValidationService eventValidationService;

    public List<String> validateCasesBeforeCloseEvent(String userToken, MultipleDetails multipleDetails) {
        List<String> errors = new ArrayList<>();
        var ethosCaseRefCollection = multipleHelperService.getEthosCaseRefCollection(userToken,
                multipleDetails.getCaseData(), errors);

        if (ethosCaseRefCollection.isEmpty()) {
            return errors;
        }

        var submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(), ethosCaseRefCollection,
                multipleDetails.getCaseData().getMultipleSource());

        for (var submitEvent : submitEvents) {
            eventValidationService.validateCaseBeforeCloseEvent(submitEvent.getCaseData(),
                    submitEvent.getState().equals(REJECTED_STATE), true, errors);
            if (!errors.isEmpty()) {
                break;
            }
        }

        return errors;
    }
}
