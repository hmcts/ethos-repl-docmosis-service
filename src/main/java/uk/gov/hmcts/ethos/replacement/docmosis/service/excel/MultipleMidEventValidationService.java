package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.SubMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;

@Slf4j
@Service("multipleDMidEventValidationService")
public class MultipleMidEventValidationService {

    private final MultipleCasesReadingService multipleCasesReadingService;

    @Autowired
    public MultipleMidEventValidationService(MultipleCasesReadingService multipleCasesReadingService) {
        this.multipleCasesReadingService = multipleCasesReadingService;
    }

    public void multipleValidationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Validating multiple and subMultiple");

        MultipleData multipleData = multipleDetails.getCaseData();

        MoveCasesType moveCasesType = multipleData.getMoveCases();

        String convertToSingle = moveCasesType.getConvertToSingle();

        if (convertToSingle.equals(NO)) {

            String updatedMultipleRef = moveCasesType.getUpdatedMultipleRef();
            String updatedSubMultipleName = moveCasesType.getUpdatedSubMultipleRef();

            if (updatedMultipleRef.equals(multipleData.getMultipleReference())) {

                validateSubMultiple(updatedSubMultipleName,
                        multipleData.getSubMultipleCollection(),
                        errors,
                        updatedMultipleRef);

            } else {

                List<SubmitMultipleEvent> submitMultipleEvents =
                        multipleCasesReadingService.retrieveMultipleCases(
                                userToken,
                                multipleDetails.getCaseTypeId(),
                                updatedMultipleRef);

                if (!submitMultipleEvents.isEmpty()) {

                    SubmitMultipleEvent submitMultipleEvent = submitMultipleEvents.get(0);

                    validateSubMultiple(updatedSubMultipleName,
                            submitMultipleEvent.getCaseData().getSubMultipleCollection(),
                            errors,
                            updatedMultipleRef);

                } else {

                    errors.add("Multiple " + updatedMultipleRef + " does not exists");

                }

            }

        }

    }

    private void validateSubMultiple(String updatedSubMultipleName,
                                     List<SubMultipleTypeItem> subMultiples,
                                     List<String> errors,
                                     String updatedMultipleRef) {

        if (updatedSubMultipleName != null && !doesSubMultipleExist(subMultiples, updatedSubMultipleName)) {

            errors.add("Sub multiple " + updatedSubMultipleName + " does not exists in " + updatedMultipleRef);

        }

    }

    private boolean doesSubMultipleExist(List<SubMultipleTypeItem> subMultiples, String updatedSubMultipleName) {

        if (subMultiples != null) {

            return subMultiples
                    .stream()
                    .anyMatch(p -> p.getValue().getSubMultipleName().equals(updatedSubMultipleName));

        } else {

            return false;

        }

    }

}
