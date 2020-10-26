package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.types.SubMultipleActionType;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.AMEND_ACTION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CREATE_ACTION;

@Slf4j
@Service("subMultipleMidEventValidationService")
public class SubMultipleMidEventValidationService {

    public void subMultipleValidationLogic(MultipleDetails multipleDetails, List<String> errors) {

        log.info("Validating subMultiple");

        MultipleData multipleData = multipleDetails.getCaseData();

        SubMultipleActionType subMultipleActionType = multipleData.getSubMultipleAction();

        String actionType = subMultipleActionType.getActionType();

        if (actionType.equals(CREATE_ACTION)) {

            log.info("Create validation");

            validateSubMultipleExist(multipleData, subMultipleActionType.getCreateSubMultipleName(), errors);

        } else if (actionType.equals(AMEND_ACTION)) {

            log.info("Amend validation");

            validateSubMultipleDoesNotExist(multipleData, subMultipleActionType.getAmendSubMultipleNameExisting(), errors);

            validateSubMultipleExist(multipleData, subMultipleActionType.getAmendSubMultipleNameNew(), errors);

        } else {

            log.info("Delete validation");

            validateSubMultipleDoesNotExist(multipleData, subMultipleActionType.getDeleteSubMultipleName(), errors);

        }

    }

    private void validateSubMultipleDoesNotExist(MultipleData multipleData, String subMultipleName, List<String> errors) {

        if (!doesSubMultipleExist(multipleData, subMultipleName)) {

            errors.add("Sub Multiple " + subMultipleName + " does not exist");

        }

    }

    private void validateSubMultipleExist(MultipleData multipleData, String subMultipleName, List<String> errors) {

        if (doesSubMultipleExist(multipleData, subMultipleName)) {

            errors.add("Sub Multiple " + subMultipleName + " already exists");

        }

    }

    private boolean doesSubMultipleExist(MultipleData multipleData, String subMultipleName) {

        log.info("Checking if sub multiple name exists");

        if (multipleData.getSubMultipleCollection() == null) return false;

        return multipleData.getSubMultipleCollection().stream().anyMatch(
                subMultipleTypeItem ->
                        subMultipleTypeItem.getValue().getSubMultipleName().equals(subMultipleName));

    }

}
