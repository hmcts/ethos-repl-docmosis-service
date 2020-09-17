package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("multipleAmendCaseIdsService")
public class MultipleAmendCaseIdsService {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;
    private final ExcelReadingService excelReadingService;
    private final ExcelDocManagementService excelDocManagementService;

    @Autowired
    public MultipleAmendCaseIdsService(CreateUpdatesBusSender createUpdatesBusSender,
                                       UserService userService,
                                       ExcelReadingService excelReadingService,
                                       ExcelDocManagementService excelDocManagementService) {
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
        this.excelReadingService = excelReadingService;
        this.excelDocManagementService = excelDocManagementService;
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

        log.info("Calculate attached and detached cases");

        List<String> attachCasesList = new ArrayList<>();
        List<String> detachCasesList = new ArrayList<>();

        List<String> unionLists = calculateAttachAndDetachCases(multipleObjects, multipleDetails.getCaseData(), detachCasesList, attachCasesList);

        log.info("Send updates to single cases");

        if (!unionLists.isEmpty()) {
            sendUpdatesToSingles(userToken, multipleDetails, errors, unionLists.get(0), detachCasesList, attachCasesList);
        }

        log.info("Create a new Excel");

        List<MultipleObject> newMultipleObjects = generateMultipleObjects(unionLists, detachCasesList, multipleObjects);

        log.info("New Multiple Objects: " + newMultipleObjects);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjects, userToken, multipleDetails.getCaseData());

    }

    private List<String> calculateAttachAndDetachCases(TreeMap<String, Object> multipleObjects, MultipleData multipleData,
                                                 List<String> detachCasesList, List<String> attachCasesList) {

        List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleData);
        log.info("EthosCaseRefCollection: " + ethosCaseRefCollection);

        List<String> unionLists = Stream.concat(ethosCaseRefCollection.stream(), multipleObjects.keySet().stream())
                .distinct().collect(Collectors.toList());

        for (String ethosCaseRef : unionLists) {

            log.info("EthosCaseRef: " + ethosCaseRef);

            if (!ethosCaseRefCollection.contains(ethosCaseRef) && multipleObjects.containsKey(ethosCaseRef)) {
                detachCasesList.add(ethosCaseRef);
            } else {
                attachCasesList.add(ethosCaseRef);
            }
        }

        log.info("DetachCaseList: " + detachCasesList);
        log.info("AttachCaseList: " + attachCasesList);

        return unionLists;
    }

    private void sendUpdatesToSingles(String userToken, MultipleDetails multipleDetails,
                                      List<String> errors, String leadId,
                                      List<String> detachCasesList, List<String> attachCasesList) {

        String updateSize = String.valueOf(detachCasesList.size() + attachCasesList.size());

        log.info("UpdateSize: " + updateSize);

        MultipleData multipleData = multipleDetails.getCaseData();
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                username,
                detachCasesList,
                PersistentQHelper.getDetachDataModel(),
                errors,
                multipleData.getMultipleReference(),
                YES,
                createUpdatesBusSender,
                updateSize);

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                username,
                attachCasesList,
                PersistentQHelper.getCreationDataModel(leadId,
                        multipleData.getMultipleReference()),
                errors,
                multipleData.getMultipleReference(),
                YES,
                createUpdatesBusSender,
                updateSize);

    }

    private List<MultipleObject> generateMultipleObjects(List<String> unionLists, List<String> detachCasesList,
                                                         TreeMap<String, Object> multipleObjects) {

        List<MultipleObject> multipleObjectList = new ArrayList<>();

        for (String ethosCaseRef : unionLists) {

            if (!detachCasesList.contains(ethosCaseRef)) {

                MultipleObject multipleObject;

                if (multipleObjects.containsKey(ethosCaseRef)) {

                    multipleObject = (MultipleObject)multipleObjects.get(ethosCaseRef);

                } else {

                    multipleObject = MultiplesHelper.createMultipleObject(ethosCaseRef, "");

                }

                multipleObjectList.add(multipleObject);

            } else {

                log.info("Case Id detached: " + ethosCaseRef);

            }
        }

        return multipleObjectList;
    }

}
