package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleAmendService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADD_CASES_TO_MULTIPLE_AMENDMENT;

@Service
@Slf4j
public class BulkAddSinglesService {
    private final SingleCasesImporter singleCasesImporter;
    private final MultipleAmendService multipleAmendService;

    public BulkAddSinglesService(SingleCasesImporter singleCasesImporter, MultipleAmendService multipleAmendService) {
        this.singleCasesImporter = singleCasesImporter;
        this.multipleAmendService = multipleAmendService;
    }

    public List<String> execute(MultipleDetails multipleDetails, String authToken) {
        try {
            var ethosCaseReferences = singleCasesImporter.importCases(multipleDetails.getCaseData(), authToken);
            return submitSingleCases(multipleDetails, ethosCaseReferences, authToken);
        } catch (ImportException e) {
            log.error("Unexpected error when importing single cases for "
                    + multipleDetails.getCaseData().getMultipleReference(), e);
            return List.of("Unexpected error when importing single cases");
        }

    }

    private List<String> submitSingleCases(MultipleDetails multipleDetails, List<String> ethosCaseReferences,
                                           String authToken) {
        var caseIds = convert(ethosCaseReferences);
        multipleDetails.getCaseData().setTypeOfAmendmentMSL(List.of(ADD_CASES_TO_MULTIPLE_AMENDMENT));
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
