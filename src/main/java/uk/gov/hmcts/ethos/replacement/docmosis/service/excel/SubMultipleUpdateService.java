package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.items.SubMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.types.SubMultipleActionType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.SubMultipleReferenceService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.AMEND_ACTION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CREATE_ACTION;

@Slf4j
@RequiredArgsConstructor
@Service("subMultipleUpdateService")
public class SubMultipleUpdateService {

    private final ExcelReadingService excelReadingService;
    private final SubMultipleReferenceService subMultipleReferenceService;
    private final ExcelDocManagementService excelDocManagementService;

    public void subMultipleUpdateLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Managing sub multiple logic");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        log.info("Logic depending on batch update type");

        actionTypeLogic(userToken, multipleDetails, multipleObjects);

        log.info("Resetting mid fields");

        MultiplesHelper.resetMidFields(multipleDetails.getCaseData());

    }

    private void actionTypeLogic(String userToken, MultipleDetails multipleDetails, TreeMap<String, Object> multipleObjects) {

        SubMultipleActionType subMultipleActionType = multipleDetails.getCaseData().getSubMultipleAction();

        String actionType = subMultipleActionType.getActionType();

        List<MultipleObject> multipleObjectsUpdated;

        if (actionType.equals(CREATE_ACTION)) {

            log.info("Creating sub multiple");

            multipleObjectsUpdated = createAction(multipleDetails, multipleObjects);

        } else if (actionType.equals(AMEND_ACTION)) {

            log.info("Amending sub multiple");

            multipleObjectsUpdated = amendAction(multipleDetails, multipleObjects);

        } else {

            log.info("Deleting sub multiple");

            multipleObjectsUpdated = deleteAction(multipleDetails, multipleObjects);

        }

        log.info("Generate the excel with the sub multiples in the dropdown");

        excelDocManagementService.generateAndUploadExcel(
                multipleObjectsUpdated,
                userToken,
                multipleDetails.getCaseData());

    }

    private List<MultipleObject> createAction(MultipleDetails multipleDetails, TreeMap<String, Object> multipleObjects) {

        String subMultipleName = multipleDetails.getCaseData().getSubMultipleAction().getCreateSubMultipleName();

        SubMultipleTypeItem subMultipleTypeItem = createSubMultipleTypeItemWithReference(multipleDetails, subMultipleName);

        log.info("Add sub multiple to the multiple");

        MultiplesHelper.addSubMultipleTypeToCase(multipleDetails.getCaseData(), subMultipleTypeItem);

        log.info("Generate the excel with the new sub multiple names in the dropdown");

        return getMultipleObjectListFromTreeMap(multipleObjects);

    }

    private List<MultipleObject> amendAction(MultipleDetails multipleDetails, TreeMap<String, Object> multipleObjects) {

        String existingSubMultipleName = multipleDetails.getCaseData().getSubMultipleAction().getAmendSubMultipleNameExisting();

        String newSubMultipleName = multipleDetails.getCaseData().getSubMultipleAction().getAmendSubMultipleNameNew();

        log.info("Updating with new sub multiple name in the sub multiple collection");

        multipleDetails.getCaseData().setSubMultipleCollection(multipleDetails.getCaseData().getSubMultipleCollection().stream()
                .map(subMultipleTypeItem ->
                        !subMultipleTypeItem.getValue().getSubMultipleName().equals(existingSubMultipleName)
                                ? subMultipleTypeItem
                                : updateSubMultipleName(subMultipleTypeItem, newSubMultipleName))
                .collect(Collectors.toList()));

        log.info("Generating the multiple object list with the new sub multiple name updated");

        return getMultipleObjectListFromTreeMapSubMultipleUpdated(multipleObjects, existingSubMultipleName, newSubMultipleName);

    }

    private List<MultipleObject> deleteAction(MultipleDetails multipleDetails, TreeMap<String, Object> multipleObjects) {

        String deleteSubMultipleName = multipleDetails.getCaseData().getSubMultipleAction().getDeleteSubMultipleName();

        log.info("Removing sub multiple from the sub multiple collection");

        multipleDetails.getCaseData().setSubMultipleCollection(multipleDetails.getCaseData().getSubMultipleCollection().stream()
                .filter(subMultipleTypeItem ->
                        !subMultipleTypeItem.getValue().getSubMultipleName().equals(deleteSubMultipleName))
                .collect(Collectors.toList()));

        log.info("Generating the multiple object list without the sub multiple name");

        return getMultipleObjectListFromTreeMapSubMultipleUpdated(multipleObjects, deleteSubMultipleName, "");

    }

    public SubMultipleTypeItem createSubMultipleTypeItemWithReference(MultipleDetails multipleDetails, String subMultipleName) {

        String subMultipleReference = generateSubMultipleReference(multipleDetails);

        log.info("SubMultipleName: " + subMultipleName + " - SubMultipleReference: " + subMultipleReference);

        return MultiplesHelper.createSubMultipleTypeItem(subMultipleReference, subMultipleName);

    }

    private SubMultipleTypeItem updateSubMultipleName(SubMultipleTypeItem subMultipleTypeItem, String newSubMultipleName) {

        subMultipleTypeItem.getValue().setSubMultipleName(newSubMultipleName);

        return subMultipleTypeItem;

    }

    private String generateSubMultipleReference(MultipleDetails multipleDetails) {

        return subMultipleReferenceService.createReference(
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference(),
                1);
    }

    private List<MultipleObject> getMultipleObjectListFromTreeMap(TreeMap<String, Object> multipleObjects) {

        List<MultipleObject> multipleObjectsExcel = new ArrayList<>();

        multipleObjects.forEach((key, value) -> multipleObjectsExcel.add((MultipleObject) value));

        return multipleObjectsExcel;
    }

    private List<MultipleObject> getMultipleObjectListFromTreeMapSubMultipleUpdated(TreeMap<String, Object> multipleObjects,
                                                                                    String existingSubMultipleName, String newSubMultipleName) {

        List<MultipleObject> multipleObjectsExcel = new ArrayList<>();

        multipleObjects.forEach((key, value) -> {
            MultipleObject multipleObject = (MultipleObject) value;
            if (multipleObject.getSubMultiple().equals(existingSubMultipleName)) {
                multipleObject.setSubMultiple(newSubMultipleName);
            }
            multipleObjectsExcel.add(multipleObject);
        });

        return multipleObjectsExcel;
    }

}
