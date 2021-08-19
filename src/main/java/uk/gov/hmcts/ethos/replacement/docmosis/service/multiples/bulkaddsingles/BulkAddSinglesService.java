package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleAmendService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.TYPE_AMENDMENT_ADDITION;

@Service
@Slf4j
public class BulkAddSinglesService {
    private final SingleCasesImporter singleCasesImporter;
    private final SingleCasesValidator singleCasesValidator;
    private final MultipleAmendService multipleAmendService;

    public BulkAddSinglesService(SingleCasesImporter singleCasesImporter, SingleCasesValidator singleCasesValidator,
                                 MultipleAmendService multipleAmendService) {
        this.singleCasesImporter = singleCasesImporter;
        this.singleCasesValidator = singleCasesValidator;
        this.multipleAmendService = multipleAmendService;
    }

    public List<String> validate(MultipleDetails multipleDetails, String authToken)  {
        var multipleEthosReference = multipleDetails.getCaseData().getMultipleReference();
        List<String> ethosCaseReferences;
        try {
            ethosCaseReferences = singleCasesImporter.importCases(multipleDetails.getCaseData(), authToken);
        } catch (ImportException e) {
            log.error("Unexpected error when importing single cases to add to "
                    + multipleEthosReference, e);
            return List.of("Unexpected error when importing single cases to add");
        }

        log.info(String.format("Multiple %s import file contains %d cases", multipleEthosReference,
                ethosCaseReferences.size()));
        try {
            var validatedSingleCases = singleCasesValidator.getValidatedCases(ethosCaseReferences,
                    multipleDetails.getCaseTypeId(), multipleEthosReference, authToken);

            return validatedSingleCases.stream()
                    .filter(Predicate.not(ValidatedSingleCase::isValid))
                    .map(s -> s.getEthosReference() + ": " + s.getInvalidReason())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Unexpected error when validating single cases to add to " + multipleEthosReference, e);
            return List.of("Unexpected error when validating single cases to add");
        }
    }

    public List<String> execute(MultipleDetails multipleDetails, String authToken) {
        try {
            var ethosCaseReferences = singleCasesImporter.importCases(multipleDetails.getCaseData(), authToken);
            return submitSingleCases(multipleDetails, ethosCaseReferences, authToken);
        } catch (ImportException e) {
            log.error("Unexpected error when importing single cases to add to "
                    + multipleDetails.getCaseData().getMultipleReference(), e);
            return List.of("Unexpected error when importing single cases to add");
        }

    }

    private List<String> submitSingleCases(MultipleDetails multipleDetails, List<String> ethosCaseReferences,
                                           String authToken) {
        var caseIds = convert(ethosCaseReferences);
        multipleDetails.getCaseData().setTypeOfAmendmentMSL(List.of(TYPE_AMENDMENT_ADDITION));
        multipleDetails.getCaseData().setCaseIdCollection(caseIds);

        var errors = new ArrayList<String>();
        multipleAmendService.bulkAmendMultipleLogic(authToken, multipleDetails, errors);

        return errors;
    }

    private List<CaseIdTypeItem> convert(List<String> validatedSingleCases) {
        return validatedSingleCases.stream()
                .map(CaseIdTypeItem::from)
                .collect(Collectors.toList());
    }
}
