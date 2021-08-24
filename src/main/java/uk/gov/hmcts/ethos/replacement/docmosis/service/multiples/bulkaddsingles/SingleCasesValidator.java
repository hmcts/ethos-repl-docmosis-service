package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

@Service
@Slf4j
class SingleCasesValidator {

    static final int ELASTICSEARCH_TERMS_SIZE = 1024;

    private final CcdClient ccdClient;

    SingleCasesValidator(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    List<ValidatedSingleCase> getValidatedCases(List<String> ethosCaseReferences, String multipleCaseTypeId,
                                                String multipleEthosReference,
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

                validatedSingleCases.add(create(ethosCaseReference, multipleEthosReference, searchResult));
            }
        }
        return validatedSingleCases;
    }

    private ValidatedSingleCase create(String ethosCaseReference, String multipleEthosReference,
                                       Optional<SubmitEvent> submitEventOptional) {
        if (submitEventOptional.isPresent()) {
            var submitEvent = submitEventOptional.get();

            if (!isAccepted(submitEvent)) {
                return ValidatedSingleCase.createInvalidCase(ethosCaseReference,
                        "Case is in state " + submitEvent.getState());
            }

            if (isSingleCaseType(submitEvent)) {
                return ValidatedSingleCase.createValidCase(ethosCaseReference);
            }

            var existingMultipleReference = getExistingMultipleReference(submitEvent, multipleEthosReference);
            if (existingMultipleReference != null) {
                return ValidatedSingleCase.createInvalidCase(ethosCaseReference,
                            "Case already assigned to " + existingMultipleReference);
            }
        } else {
            return ValidatedSingleCase.createInvalidCase(ethosCaseReference, "Case not found");
        }

        return ValidatedSingleCase.createValidCase(ethosCaseReference);
    }

    private boolean isAccepted(SubmitEvent submitEvent) {
        return ACCEPTED_STATE.equals(submitEvent.getState());
    }

    private boolean isSingleCaseType(SubmitEvent submitEvent) {
        return SINGLE_CASE_TYPE.equals(submitEvent.getCaseData().getCaseType());
    }

    private String getExistingMultipleReference(SubmitEvent submitEvent, String multipleEthosReference) {
        var existingMultipleReference = submitEvent.getCaseData().getMultipleReference();
        if (StringUtils.isNotBlank(existingMultipleReference)
                && !multipleEthosReference.equals(existingMultipleReference)) {
            return existingMultipleReference;
        } else {
            return null;
        }
    }
}
