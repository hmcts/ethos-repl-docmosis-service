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

        String updatedMultipleRef = moveCasesType.getUpdatedMultipleRef();
        String updatedSubMultipleRef = moveCasesType.getUpdatedSubMultipleRef();

        if (updatedMultipleRef.equals(multipleData.getMultipleReference())) {

            validateSubMultiple(updatedSubMultipleRef,
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

                validateSubMultiple(updatedSubMultipleRef,
                        submitMultipleEvent.getCaseData().getSubMultipleCollection(),
                        errors,
                        updatedMultipleRef);

            } else {

                errors.add("Multiple " + updatedMultipleRef + " does not exists");

            }

        }

    }

    private void validateSubMultiple(String updatedSubMultipleRef,
                                     List<SubMultipleTypeItem> subMultiples,
                                     List<String> errors,
                                     String updatedMultipleRef) {

        if (updatedSubMultipleRef != null && !doesSubMultipleExist(subMultiples, updatedSubMultipleRef)) {

            errors.add("Sub multiple " + updatedSubMultipleRef + " does not exists in " + updatedMultipleRef);

        }

    }

    private boolean doesSubMultipleExist(List<SubMultipleTypeItem> subMultiples, String updatedSubMultipleRef) {

        if (subMultiples != null) {

            return subMultiples
                    .stream()
                    .anyMatch(p -> p.getValue().getSubMultipleRef().equals(updatedSubMultipleRef));

        } else {

            return false;

        }

    }

}
