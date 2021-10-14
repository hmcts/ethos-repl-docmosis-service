package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("multipleSingleDisposeEventValidationService")
public class MultipleCloseEventValidationService {

    private final SingleCasesReadingService singleCasesReadingService;
    private final MultipleHelperService multipleHelperService;
    private final EventValidationService eventValidationService;

    @Autowired
    public MultipleCloseEventValidationService(SingleCasesReadingService singleCasesReadingService,
                                               MultipleHelperService multipleHelperService,
                                               EventValidationService eventValidationService) {
        this.singleCasesReadingService = singleCasesReadingService;
        this.multipleHelperService = multipleHelperService;
        this.eventValidationService = eventValidationService;
    }

    public void validateJurisdictionCollections(String userToken, MultipleDetails multipleDetails, List<String> errors) {
        var multipleData = multipleDetails.getCaseData();

        List<String> ethosCaseRefCollection = multipleHelperService.getEthosCaseRefCollection(userToken, multipleData, errors);

        if(ethosCaseRefCollection.isEmpty()) {
            return;
        }

        List<SubmitEvent> submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(), ethosCaseRefCollection, multipleData.getMultipleSource());

        for (SubmitEvent event : submitEvents) {
            var caseData = event.getCaseData();
            eventValidationService.validateJurisdictionOutcome(caseData, event.getState().equals(REJECTED_STATE), errors, true);
        }
    }
}
