package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BulkAddSinglesValidator {

    private final SingleCasesImporter singleCasesImporter;
    private final SingleCasesValidator singleCasesValidator;

    public BulkAddSinglesValidator(SingleCasesImporter singleCasesImporter, SingleCasesValidator singleCasesValidator) {
        this.singleCasesImporter = singleCasesImporter;
        this.singleCasesValidator = singleCasesValidator;
    }

    public List<String> validate(MultipleDetails multipleDetails, String authToken)  {
        var multipleEthosReference = multipleDetails.getCaseData().getMultipleReference();
        List<String> ethosCaseReferences;
        try {
            ethosCaseReferences = singleCasesImporter.importCases(multipleDetails.getCaseData(), authToken);
        } catch (ImportException e) {
            log.error("Unexpected error when importing single cases for " + multipleEthosReference, e);
            return List.of("Unexpected error when importing single cases");
        }

        log.info(String.format("Multiple %s import file contains %d cases", multipleEthosReference,
                ethosCaseReferences.size()));

        if (ethosCaseReferences.isEmpty()) {
            return List.of("No cases found");
        }

        try {
            var validatedSingleCases = singleCasesValidator.getValidatedCases(ethosCaseReferences,
                    multipleDetails.getCaseTypeId(), authToken);

            return validatedSingleCases.stream()
                    .filter(Predicate.not(ValidatedSingleCase::isValid))
                    .map(s -> s.getEthosReference() + ": " + s.getInvalidReason())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Unexpected error when validating single cases for " + multipleEthosReference, e);
            return List.of("Unexpected error when validating single cases");
        }
    }
}
