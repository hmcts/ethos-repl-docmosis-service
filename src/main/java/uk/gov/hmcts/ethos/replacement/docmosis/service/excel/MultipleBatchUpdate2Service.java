package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OPEN_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("multipleBatchUpdate2Service")
public class MultipleBatchUpdate2Service {

    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleCasesReadingService multipleCasesReadingService;
    private final ExcelReadingService excelReadingService;
    private final MultipleHelperService multipleHelperService;
    private final CcdClient ccdClient;

    @Autowired
    public MultipleBatchUpdate2Service(ExcelDocManagementService excelDocManagementService,
                                       MultipleCasesReadingService multipleCasesReadingService,
                                       ExcelReadingService excelReadingService,
                                       MultipleHelperService multipleHelperService, CcdClient ccdClient) {
        this.excelDocManagementService = excelDocManagementService;
        this.multipleCasesReadingService = multipleCasesReadingService;
        this.excelReadingService = excelReadingService;
        this.multipleHelperService = multipleHelperService;
        this.ccdClient = ccdClient;
    }

    public void batchUpdate2Logic(String userToken, MultipleDetails multipleDetails,
                                  List<String> errors, SortedMap<String, Object> multipleObjects) {

        var multipleData = multipleDetails.getCaseData();

        log.info("Batch update type = 2");

        var convertToSingle = multipleData.getMoveCases().getConvertToSingle();

        log.info("Convert to singles " + convertToSingle);

        List<String> multipleObjectsFiltered = new ArrayList<>(multipleObjects.keySet());

        log.info("Update multiple state to open when batching update2 as there will be No Confirmation when updates");

        multipleDetails.getCaseData().setState(OPEN_STATE);

        if (convertToSingle.equals(YES)) {

            removeCasesFromCurrentMultiple(userToken, multipleDetails, errors, multipleObjectsFiltered);

            log.info("Sending detach updates to singles");

            multipleHelperService.sendDetachUpdatesToSinglesWithoutConfirmation(userToken, multipleDetails,
                    errors, multipleObjects);

        } else {

            var moveCasesType = multipleData.getMoveCases();
            String updatedMultipleRef = moveCasesType.getUpdatedMultipleRef();
            String updatedSubMultipleRef = moveCasesType.getUpdatedSubMultipleRef();
            String currentMultipleRef = multipleData.getMultipleReference();

            if (currentMultipleRef.equals(updatedMultipleRef)) {

                log.info("Updates to the same multiple");

                if (isNullOrEmpty(updatedSubMultipleRef)) {

                    log.info("Keep cases in the same sub-multiple");

                } else {

                    log.info("Reading excel and add sub multiple references");
                    readExcelAndAddSubMultipleRef(userToken, multipleDetails, errors,
                            multipleObjectsFiltered, updatedSubMultipleRef);
                }

            } else {

                log.info("Updates to different multiple");

                updateToDifferentMultiple(userToken, multipleDetails, errors, multipleObjectsFiltered,
                        multipleObjects, updatedMultipleRef, updatedSubMultipleRef);

            }

        }

    }

    private void performActionsWithNewLeadCase(String userToken, MultipleDetails multipleDetails, List<String> errors,
                                               String oldLeadCase, List<String> multipleObjectsFiltered) {

        String newLeadCase = multipleHelperService.getLeadCaseFromExcel(userToken,
                multipleDetails.getCaseData(), errors);

        if (newLeadCase.isEmpty()) {

            log.info("Removing lead as it has been already taken out");

            multipleDetails.getCaseData().setLeadCase(null);

        } else {

            if (multipleObjectsFiltered.contains(oldLeadCase)) {

                log.info("Changing the lead case to: " + newLeadCase + " as old lead case: "
                        + oldLeadCase + " has been taken out");

                multipleHelperService.addLeadMarkUp(userToken, multipleDetails.getCaseTypeId(),
                        multipleDetails.getCaseData(), newLeadCase, "");

                log.info("Sending single update with the lead flag");

                multipleHelperService.sendCreationUpdatesToSinglesWithoutConfirmation(
                        userToken, multipleDetails.getCaseTypeId(),
                        multipleDetails.getJurisdiction(), multipleDetails.getCaseData(), errors,
                        new ArrayList<>(Collections.singletonList(newLeadCase)), newLeadCase,
                        multipleDetails.getCaseId());

            }

        }

    }

    private void removeCasesFromCurrentMultiple(String userToken, MultipleDetails multipleDetails, List<String> errors,
                                                List<String> multipleObjectsFiltered) {

        log.info("Read current excel and remove cases in multiple");

        readCurrentExcelAndRemoveCasesInMultiple(userToken, multipleDetails, errors,
                multipleObjectsFiltered);

        log.info("Perform actions with the new lead if exists");

        String oldLeadCase = MultiplesHelper.getCurrentLead(multipleDetails.getCaseData().getLeadCase());

        performActionsWithNewLeadCase(userToken, multipleDetails, errors, oldLeadCase, multipleObjectsFiltered);

    }

    private void updateToDifferentMultiple(String userToken,
                                           MultipleDetails multipleDetails,
                                           List<String> errors,
                                           List<String> multipleObjectsFiltered,
                                           SortedMap<String, Object> multipleObjects,
                                           String updatedMultipleRef,
                                           String updatedSubMultipleRef) {

        removeCasesFromCurrentMultiple(userToken, multipleDetails, errors, multipleObjectsFiltered);

        SubmitMultipleEvent updatedMultiple = getUpdatedMultiple(userToken,
                multipleDetails.getCaseTypeId(), updatedMultipleRef);

        var updatedMultipleData = updatedMultiple.getCaseData();

        String updatedCaseTypeId = multipleDetails.getCaseTypeId();

        String updatedJurisdiction = multipleDetails.getJurisdiction();

        log.info("Add the lead case markUp");

        String getCurrentNewMultipleLeadCase =
                multipleHelperService.getLeadCaseFromExcel(userToken, updatedMultipleData, errors);

        String updatedLeadCase = checkIfNewMultipleWasEmpty(getCurrentNewMultipleLeadCase,
                new ArrayList<>(multipleObjects.keySet()));

        multipleHelperService.addLeadMarkUp(userToken, updatedCaseTypeId, updatedMultipleData,
                updatedLeadCase, "");

        multipleHelperService.moveCasesAndSendUpdateToMultiple(userToken, updatedSubMultipleRef,
                updatedJurisdiction, updatedCaseTypeId, String.valueOf(updatedMultiple.getCaseId()),
                updatedMultipleData, multipleObjectsFiltered, errors);

        log.info("Sending creation updates to singles");

        multipleHelperService.sendCreationUpdatesToSinglesWithoutConfirmation(userToken, updatedCaseTypeId,
                updatedJurisdiction, updatedMultipleData, errors,
                new ArrayList<>(multipleObjects.keySet()), updatedLeadCase,
                String.valueOf(updatedMultiple.getCaseId()));

    }

    private String checkIfNewMultipleWasEmpty(String updatedLeadCase, List<String> multipleObjectsFiltered) {

        if (updatedLeadCase.isEmpty() && !multipleObjectsFiltered.isEmpty()) {
            return multipleObjectsFiltered.getFirst();
        }

        return updatedLeadCase;

    }

    private void readExcelAndAddSubMultipleRef(String userToken, MultipleDetails multipleDetails,
                                               List<String> errors, List<String> multipleObjectsFiltered,
                                               String updatedSubMultipleRef) {

        SortedMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        List<MultipleObject> newMultipleObjectsUpdated = addSubMultipleRefToMultipleObjects(multipleObjectsFiltered,
                multipleObjects, updatedSubMultipleRef, userToken, multipleDetails);
        excelDocManagementService.generateAndUploadExcel(newMultipleObjectsUpdated, userToken, multipleDetails);

    }

    private List<MultipleObject> addSubMultipleRefToMultipleObjects(List<String> multipleObjectsFiltered,
                                                                    SortedMap<String, Object> multipleObjects,
                                                                    String updatedSubMultipleRef,
                                                                    String userToken,
                                                                    MultipleDetails multipleDetails) {

        List<MultipleObject> newMultipleObjectsUpdated = new ArrayList<>();
        multipleObjects.forEach((key, value) -> {
            var multipleObject = (MultipleObject) value;
            if (multipleObjectsFiltered.contains(key)) {
                multipleObject.setSubMultiple(updatedSubMultipleRef);
                try {
                    MultiplesHelper.setSubMultipleFieldInSingleCaseData(userToken,
                            multipleDetails,
                            multipleObject.getEthosCaseRef(),
                            updatedSubMultipleRef,
                            ccdClient);
                } catch (IOException e) {
                    log.error(String.format("Error in setting subMultiple for case %s:",
                            multipleObject.getEthosCaseRef()) + e);
                }
            }
            newMultipleObjectsUpdated.add(multipleObject);
        });

        return newMultipleObjectsUpdated;

    }

    private void readCurrentExcelAndRemoveCasesInMultiple(String userToken, MultipleDetails multipleDetails,
                                                          List<String> errors, List<String> multipleObjectsFiltered) {

        SortedMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        List<MultipleObject> newMultipleObjectsUpdated = removeCasesInMultiple(multipleObjectsFiltered,
                multipleObjects);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjectsUpdated, userToken, multipleDetails);

    }

    private List<MultipleObject> removeCasesInMultiple(List<String> multipleObjectsFiltered,
                                                       SortedMap<String, Object> multipleObjects) {

        List<MultipleObject> newMultipleObjectsUpdated = new ArrayList<>();

        multipleObjects.forEach((key, value) -> {
            var multipleObject = (MultipleObject) value;
            if (!multipleObjectsFiltered.contains(key)) {
                newMultipleObjectsUpdated.add(multipleObject);
            }
        });

        return newMultipleObjectsUpdated;

    }

    private SubmitMultipleEvent getUpdatedMultiple(String userToken, String caseTypeId, String updatedMultipleRef) {

        return multipleCasesReadingService.retrieveMultipleCasesWithRetries(
                userToken,
                caseTypeId,
                updatedMultipleRef).getFirst();

    }

}
