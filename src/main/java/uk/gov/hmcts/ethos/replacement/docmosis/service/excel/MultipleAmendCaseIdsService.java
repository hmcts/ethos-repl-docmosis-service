package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("multipleAmendCaseIdsService")
public class MultipleAmendCaseIdsService {

    private final ExcelReadingService excelReadingService;
    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleAmendCaseIdsService(ExcelReadingService excelReadingService,
                                       ExcelDocManagementService excelDocManagementService,
                                       MultipleHelperService multipleHelperService) {
        this.excelReadingService = excelReadingService;
        this.excelDocManagementService = excelDocManagementService;
        this.multipleHelperService = multipleHelperService;
    }

    public void bulkAmendCaseIdsLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel to amend caseIds");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        log.info("MultipleObjectsKeySet: " + multipleObjects.keySet());
        log.info("MultipleObjectsValues: " + multipleObjects.values());

        List<String> newEthosCaseRefCollection = MultiplesHelper.getCaseIds(multipleDetails.getCaseData());

        log.info("Calculate union new and old cases");

        List<String> unionLists = concatNewAndOldCases(multipleObjects, newEthosCaseRefCollection);

        if (!newEthosCaseRefCollection.isEmpty()) {

            log.info("Send updates to single cases");

            multipleHelperService.sendUpdatesToSinglesLogic(userToken, multipleDetails, errors, unionLists.get(0),
                    multipleObjects, newEthosCaseRefCollection);

        }

        log.info("Create a new Excel");

        List<MultipleObject> newMultipleObjects = generateMultipleObjects(unionLists, multipleObjects);

        log.info("New Multiple Objects: " + newMultipleObjects);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjects, userToken, multipleDetails.getCaseData());

        log.info("Clearing the payload");

        multipleDetails.getCaseData().setCaseIdCollection(null);

    }

    private List<String> concatNewAndOldCases(TreeMap<String, Object> multipleObjects, List<String> newEthosCaseRefCollection) {

        log.info("EthosCaseRefCollection: " + newEthosCaseRefCollection);

        return Stream.concat(newEthosCaseRefCollection.stream(), multipleObjects.keySet().stream())
                .distinct().collect(Collectors.toList());

    }

    private List<MultipleObject> generateMultipleObjects(List<String> unionLists, TreeMap<String, Object> multipleObjects) {

        List<MultipleObject> multipleObjectList = new ArrayList<>();

        for (String ethosCaseRef : unionLists) {

                MultipleObject multipleObject;

                if (multipleObjects.containsKey(ethosCaseRef)) {

                    multipleObject = (MultipleObject)multipleObjects.get(ethosCaseRef);

                } else {

                    multipleObject = MultiplesHelper.createMultipleObject(ethosCaseRef, "");

                }

                multipleObjectList.add(multipleObject);

        }

        return multipleObjectList;
    }

}
