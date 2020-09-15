package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OPEN_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("multipleBatchUpdate2Service")
public class MultipleBatchUpdate2Service {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;
    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleCasesReadingService multipleCasesReadingService;
    private final ExcelReadingService excelReadingService;
    private final MultipleCasesSendingService multipleCasesSendingService;

    @Autowired
    public MultipleBatchUpdate2Service(CreateUpdatesBusSender createUpdatesBusSender,
                                       UserService userService,
                                       ExcelDocManagementService excelDocManagementService,
                                       MultipleCasesReadingService multipleCasesReadingService,
                                       ExcelReadingService excelReadingService,
                                       MultipleCasesSendingService multipleCasesSendingService) {
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
        this.excelDocManagementService = excelDocManagementService;
        this.multipleCasesReadingService = multipleCasesReadingService;
        this.excelReadingService = excelReadingService;
        this.multipleCasesSendingService = multipleCasesSendingService;
    }

    public void batchUpdate2Logic(String userToken, MultipleDetails multipleDetails,
                                  List<String> errors, TreeMap<String, Object> multipleObjects) {

        MultipleData multipleData = multipleDetails.getCaseData();

        log.info("Batch update type = 2");

        String convertToSingle = multipleData.getMoveCases().getConvertToSingle();

        log.info("Convert to singles " + convertToSingle);

        List<String> multipleObjectsFiltered = new ArrayList<>(multipleObjects.keySet());

        if (convertToSingle.equals(YES)) {

            removeCasesFromCurrentMultiple(userToken, multipleDetails, errors, multipleObjectsFiltered);

            log.info("Sending detach updates to singles");

            sendDetachUpdatesToSingles(userToken, multipleDetails, errors, multipleObjects);

        } else {

            MoveCasesType moveCasesType = multipleData.getMoveCases();
            String updatedMultipleRef = moveCasesType.getUpdatedMultipleRef();
            String updatedSubMultipleRef = moveCasesType.getUpdatedSubMultipleRef();
            String currentMultipleRef = multipleData.getMultipleReference();

            if (currentMultipleRef.equals(updatedMultipleRef)) {

                log.info("Updates to the same multiple");

                if (isNullOrEmpty(updatedSubMultipleRef)) {

                    log.info("Keep cases in the same multiple");

                } else {

                    log.info("Reading excel and add sub multiple references");

                    readExcelAndAddSubMultipleRef(userToken, multipleDetails.getCaseData(), errors,
                            multipleObjectsFiltered, updatedSubMultipleRef);

                }

                log.info("Update multiple state to open");

                multipleDetails.getCaseData().setState(OPEN_STATE);

            } else {

                log.info("Updates to different multiple");

                updateToDifferentMultiple(userToken, multipleDetails, errors, multipleObjectsFiltered,
                        multipleObjects, updatedMultipleRef, updatedSubMultipleRef);

            }

        }

    }

    private void removeCasesFromCurrentMultiple(String userToken, MultipleDetails multipleDetails, List<String> errors,
                                                List<String> multipleObjectsFiltered) {

        log.info("Remove case ids in current multiple");

        MultiplesHelper.removeCaseIds(multipleDetails.getCaseData(), multipleObjectsFiltered);

        log.info("Read current excel and remove cases in multiple");

        readCurrentExcelAndRemoveCasesInMultiple(userToken, multipleDetails.getCaseData(), errors,
                multipleObjectsFiltered);

    }

    private void updateToDifferentMultiple(String userToken, MultipleDetails multipleDetails, List<String> errors,
                                           List<String> multipleObjectsFiltered, TreeMap<String, Object> multipleObjects,
                                           String updatedMultipleRef, String updatedSubMultipleRef) {

        removeCasesFromCurrentMultiple(userToken, multipleDetails, errors, multipleObjectsFiltered);

        SubmitMultipleEvent updatedMultiple = getUpdatedMultiple(userToken,
                multipleDetails.getCaseTypeId(), updatedMultipleRef);

        log.info("Add new cases to case ids collection");

        MultiplesHelper.addCaseIds(updatedMultiple.getCaseData(), multipleObjectsFiltered);

        if (isNullOrEmpty(updatedSubMultipleRef)) {

            log.info("Moving single cases without sub multiples");

            readUpdatedExcelAndAddCasesInMultiple(userToken, updatedMultiple.getCaseData(), errors,
                    multipleObjectsFiltered, "");

        } else {

            log.info("Moving single cases with sub multiples");

            readUpdatedExcelAndAddCasesInMultiple(userToken, updatedMultiple.getCaseData(), errors,
                    multipleObjectsFiltered, updatedSubMultipleRef);

        }

        log.info("Send update to the multiple with new excel");

        multipleCasesSendingService.sendUpdateToMultiple(userToken, multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(), updatedMultiple.getCaseData(), String.valueOf(updatedMultiple.getCaseId()));

        log.info("Sending creation updates to singles");

        sendCreationUpdatesToSingles(userToken, multipleDetails.getCaseTypeId(), multipleDetails.getJurisdiction(),
                updatedMultiple.getCaseData(), errors, multipleObjects,
                MultiplesHelper.getLeadFromCaseIds(updatedMultiple.getCaseData()));

    }


    private void readExcelAndAddSubMultipleRef(String userToken, MultipleData multipleData,
                                               List<String> errors, List<String> multipleObjectsFiltered,
                                               String updatedSubMultipleRef) {

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleData),
                        errors,
                        multipleData,
                        FilterExcelType.ALL);

        List<MultipleObject> newMultipleObjectsUpdated = addSubMultipleRefToMultipleObjects(multipleObjectsFiltered,
                multipleObjects, updatedSubMultipleRef);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjectsUpdated, userToken, multipleData);

    }

    private List<MultipleObject> addSubMultipleRefToMultipleObjects(List<String> multipleObjectsFiltered,
                                                                    TreeMap<String, Object> multipleObjects,
                                                                    String updatedSubMultipleRef) {

        List<MultipleObject> newMultipleObjectsUpdated = new ArrayList<>();

        multipleObjects.forEach((key, value) -> {
            MultipleObject multipleObject = (MultipleObject) value;
            if (multipleObjectsFiltered.contains(key)) {
                multipleObject.setSubMultiple(updatedSubMultipleRef);
            }
            newMultipleObjectsUpdated.add(multipleObject);
        });

        return newMultipleObjectsUpdated;

    }

    private void readCurrentExcelAndRemoveCasesInMultiple(String userToken, MultipleData multipleData,
                                                          List<String> errors, List<String> multipleObjectsFiltered) {

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleData),
                        errors,
                        multipleData,
                        FilterExcelType.ALL);

        List<MultipleObject> newMultipleObjectsUpdated = removeCasesInMultiple(multipleObjectsFiltered,
                multipleObjects);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjectsUpdated, userToken, multipleData);

    }

    private List<MultipleObject> removeCasesInMultiple(List<String> multipleObjectsFiltered,
                                                       TreeMap<String, Object> multipleObjects) {

        List<MultipleObject> newMultipleObjectsUpdated = new ArrayList<>();

        multipleObjects.forEach((key, value) -> {
            MultipleObject multipleObject = (MultipleObject) value;
            if (!multipleObjectsFiltered.contains(key)) {
                newMultipleObjectsUpdated.add(multipleObject);
            }
        });

        return newMultipleObjectsUpdated;

    }

    private void readUpdatedExcelAndAddCasesInMultiple(String userToken, MultipleData updatedMultipleData,
                                                       List<String> errors, List<String> multipleObjectsFiltered,
                                                       String updatedSubMultipleRef) {

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(updatedMultipleData),
                        errors,
                        updatedMultipleData,
                        FilterExcelType.ALL);

        List<MultipleObject> newMultipleObjectsUpdated = addCasesInMultiple(multipleObjectsFiltered,
                multipleObjects, updatedSubMultipleRef);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjectsUpdated, userToken, updatedMultipleData);

    }

    private List<MultipleObject> addCasesInMultiple(List<String> multipleObjectsFiltered,
                                                    TreeMap<String, Object> multipleObjects,
                                                    String updatedSubMultipleRef) {

        List<MultipleObject> multipleObjectsToBeAdded = new ArrayList<>();
        List<MultipleObject> newMultipleObjectsUpdated = new ArrayList<>();

        for (String ethosCaseReference : multipleObjectsFiltered) {

            multipleObjectsToBeAdded.add(MultiplesHelper.createMultipleObject(ethosCaseReference, updatedSubMultipleRef));
        }

        multipleObjects.forEach((key, value) -> newMultipleObjectsUpdated.add((MultipleObject) value));

        newMultipleObjectsUpdated.addAll(multipleObjectsToBeAdded);

        return newMultipleObjectsUpdated;

    }

    private void sendDetachUpdatesToSingles(String userToken, MultipleDetails multipleDetails,
                                              List<String> errors, TreeMap<String, Object> multipleObjects) {

        List<String> multipleObjectsFiltered = new ArrayList<>(multipleObjects.keySet());
        MultipleData multipleData = multipleDetails.getCaseData();
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                username,
                multipleObjectsFiltered,
                PersistentQHelper.getDetachDataModel(),
                errors,
                multipleData.getMultipleReference(),
                createUpdatesBusSender,
                String.valueOf(multipleObjectsFiltered.size()));

    }

    private void sendCreationUpdatesToSingles(String userToken, String caseTypeId, String jurisdiction, MultipleData updatedMultipleData,
                                              List<String> errors, TreeMap<String, Object> multipleObjects, String leadId) {

        List<String> multipleObjectsFiltered = new ArrayList<>(multipleObjects.keySet());
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(caseTypeId,
                jurisdiction,
                username,
                multipleObjectsFiltered,
                PersistentQHelper.getCreationDataModel(leadId,
                        updatedMultipleData.getMultipleReference()),
                errors,
                updatedMultipleData.getMultipleReference(),
                createUpdatesBusSender,
                String.valueOf(multipleObjectsFiltered.size()));

    }

    private SubmitMultipleEvent getUpdatedMultiple(String userToken, String caseTypeId, String updatedMultipleRef) {

        return multipleCasesReadingService.retrieveMultipleCasesWithRetries(
                        userToken,
                        caseTypeId,
                        updatedMultipleRef).get(0);

    }

}
