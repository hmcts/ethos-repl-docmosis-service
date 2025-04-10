package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;

@Service
@Slf4j
class SingleCasesValidator {

    static final int ELASTICSEARCH_TERMS_SIZE = 1024;

    private final CcdClient ccdClient;

    SingleCasesValidator(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    List<ValidatedSingleCase> getValidatedCases(List<String> ethosCaseReferences, String multipleCaseTypeId,
                  String authToken) throws IOException {
        var partitionedCaseReferences = Lists.partition(ethosCaseReferences, ELASTICSEARCH_TERMS_SIZE);

        var validatedSingleCases = new ArrayList<ValidatedSingleCase>();
        for (var caseReferences : partitionedCaseReferences) {
            var submitEvents = ccdClient.retrieveCasesElasticSearchForCreation(authToken,
                    UtilHelper.getCaseTypeId(multipleCaseTypeId),
                    caseReferences,
                    MANUALLY_CREATED_POSITION);
            log.info("Search returned {} results", submitEvents.size());

            for (var ethosCaseReference : caseReferences) {
                log.info(ethosCaseReference);
                var searchResult = submitEvents.stream()
                        .filter(se -> se.getCaseData().getEthosCaseReference().equals(ethosCaseReference))
                        .findFirst();

                validatedSingleCases.add(create(ethosCaseReference, searchResult));
            }
        }
        return validatedSingleCases;
    }

    private ValidatedSingleCase create(String ethosCaseReference,
                                       Optional<SubmitEvent> submitEventOptional) {
        if (submitEventOptional.isPresent()) {
            var submitEvent = submitEventOptional.get();

            if (!validateStates(submitEvent)) {
                return ValidatedSingleCase.createInvalidCase(ethosCaseReference,
                        "Case is in state " + submitEvent.getState());
            }

        } else {
            return ValidatedSingleCase.createInvalidCase(ethosCaseReference, "Case not found");
        }

        return ValidatedSingleCase.createValidCase(ethosCaseReference);
    }

    private boolean validateStates(SubmitEvent submitEvent) {
        return ACCEPTED_STATE.equals(submitEvent.getState()) || SUBMITTED_STATE.equals(submitEvent.getState())
            || CLOSED_STATE.equals(submitEvent.getState()) || REJECTED_STATE.equals(submitEvent.getState());
    }
}
