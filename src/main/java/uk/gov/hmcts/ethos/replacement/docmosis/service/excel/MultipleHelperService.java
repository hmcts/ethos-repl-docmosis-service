package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.SubMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.DataModelParent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("multipleHelperService")
public class MultipleHelperService {

    private final SingleCasesReadingService singleCasesReadingService;
    private final MultipleCasesReadingService multipleCasesReadingService;
    private final ExcelReadingService excelReadingService;
    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleCasesSendingService multipleCasesSendingService;
    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    @Autowired
    public MultipleHelperService(SingleCasesReadingService singleCasesReadingService,
                                 MultipleCasesReadingService multipleCasesReadingService,
                                 ExcelReadingService excelReadingService,
                                 ExcelDocManagementService excelDocManagementService,
                                 MultipleCasesSendingService multipleCasesSendingService,
                                 CreateUpdatesBusSender createUpdatesBusSender,
                                 UserService userService) {
        this.singleCasesReadingService = singleCasesReadingService;
        this.multipleCasesReadingService = multipleCasesReadingService;
        this.excelReadingService = excelReadingService;
        this.excelDocManagementService = excelDocManagementService;
        this.multipleCasesSendingService = multipleCasesSendingService;
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
    }

    public void addLeadMarkUp(String userToken, String multipleCaseTypeId, MultipleData multipleData,
                              String newLeadCase, String caseId) {

        if (caseId.equals("")) {

            SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(
                    userToken,
                    multipleCaseTypeId,
                    newLeadCase,
                    multipleData.getMultipleSource());

            if (submitEvent != null) {

                multipleData.setLeadCase(MultiplesHelper.generateLeadMarkUp(
                        ccdGatewayBaseUrl,
                        String.valueOf(submitEvent.getCaseId()),
                        newLeadCase));

            } else {

                log.info("No lead case found for: " + newLeadCase);

            }

        } else {

            multipleData.setLeadCase(MultiplesHelper.generateLeadMarkUp(
                    ccdGatewayBaseUrl,
                    caseId,
                    newLeadCase));

        }

    }

    public void validateExternalMultipleAndSubMultiple(String userToken, String caseTypeId, String multipleRef,
                                                       String subMultipleName, List<String> errors) {

        List<SubmitMultipleEvent> multipleEvents =
                multipleCasesReadingService.retrieveMultipleCases(
                        userToken,
                        caseTypeId,
                        multipleRef);

        if (!multipleEvents.isEmpty()) {

            SubmitMultipleEvent multipleEvent = multipleEvents.get(0);

            validateSubMultiple(subMultipleName,
                    multipleEvent.getCaseData().getSubMultipleCollection(),
                    errors,
                    multipleRef);

        } else {

            errors.add("Multiple " + multipleRef + " does not exist");

        }

    }

    public void validateSubMultiple(String subMultipleName,
                                     List<SubMultipleTypeItem> subMultiples,
                                     List<String> errors,
                                     String multipleReference) {

        if (!isNullOrEmpty(subMultipleName) && !doesSubMultipleExist(subMultiples, subMultipleName)) {

            errors.add("Sub multiple " + subMultipleName + " does not exist in " + multipleReference);

        }

    }

    private boolean doesSubMultipleExist(List<SubMultipleTypeItem> subMultiples, String subMultipleName) {

        if (subMultiples != null) {

            return subMultiples
                    .stream()
                    .anyMatch(p -> p.getValue().getSubMultipleName().equals(subMultipleName));

        } else {

            return false;

        }

    }

    public void moveCasesAndSendUpdateToMultiple(String userToken, String newSubMultipleName,
                                                 String jurisdiction, String caseTypeId,
                                                 String newMultipleCaseId, MultipleData newMultipleData,
                                                 List<String> casesFiltered, List<String> errors) {

        if (isNullOrEmpty(newSubMultipleName)) {

            log.info("Moving single cases without sub multiples");

            readUpdatedExcelAndAddCasesInMultiple(userToken, newMultipleData, errors,
                    casesFiltered, "");

        } else {

            log.info("Moving single cases with sub multiples");

            readUpdatedExcelAndAddCasesInMultiple(userToken, newMultipleData, errors,
                    casesFiltered, newSubMultipleName);

        }

        log.info("Send update to the multiple with new excel");

        multipleCasesSendingService.sendUpdateToMultiple(userToken, caseTypeId, jurisdiction,
                newMultipleData, newMultipleCaseId);

    }

    private void readUpdatedExcelAndAddCasesInMultiple(String userToken, MultipleData newMultipleData,
                                                       List<String> errors, List<String> multipleObjectsFiltered,
                                                       String newSubMultipleName) {

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(newMultipleData),
                        errors,
                        newMultipleData,
                        FilterExcelType.ALL);

        List<MultipleObject> newMultipleObjectsUpdated = addCasesInMultiple(multipleObjectsFiltered,
                multipleObjects, newSubMultipleName);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjectsUpdated, userToken, newMultipleData);

    }

    private List<MultipleObject> addCasesInMultiple(List<String> multipleObjectsFiltered,
                                                    TreeMap<String, Object> multipleObjects,
                                                    String newSubMultipleName) {

        List<MultipleObject> multipleObjectsToBeAdded = new ArrayList<>();
        List<MultipleObject> newMultipleObjectsUpdated = new ArrayList<>();

        for (String ethosCaseReference : multipleObjectsFiltered) {

            multipleObjectsToBeAdded.add(MultiplesHelper.createMultipleObject(ethosCaseReference, newSubMultipleName));

        }

        multipleObjects.forEach((key, value) -> newMultipleObjectsUpdated.add((MultipleObject) value));

        newMultipleObjectsUpdated.addAll(multipleObjectsToBeAdded);

        return newMultipleObjectsUpdated;

    }


    public void sendCreationUpdatesToSinglesWithoutConfirmation(String userToken, String caseTypeId, String jurisdiction,
                                                           MultipleData updatedMultipleData, List<String> errors,
                                                           List<String> multipleObjectsFiltered, String leadId) {

        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(caseTypeId,
                jurisdiction,
                username,
                multipleObjectsFiltered,
                PersistentQHelper.getCreationDataModel(leadId,
                        updatedMultipleData.getMultipleReference()),
                errors,
                updatedMultipleData.getMultipleReference(),
                NO,
                createUpdatesBusSender,
                String.valueOf(multipleObjectsFiltered.size()));

    }

    public void sendDetachUpdatesToSinglesWithoutConfirmation(String userToken, MultipleDetails multipleDetails,
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
                NO,
                createUpdatesBusSender,
                String.valueOf(multipleObjectsFiltered.size()));

    }

    public void sendUpdatesToSinglesWithConfirmation(String userToken, MultipleDetails multipleDetails,
                                                     List<String> errors, TreeMap<String, Object> multipleObjects,
                                                     CaseData caseData) {

        List<String> multipleObjectsFiltered = new ArrayList<>(multipleObjects.keySet());
        MultipleData multipleData = multipleDetails.getCaseData();
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                username,
                multipleObjectsFiltered,
                PersistentQHelper.getUpdateDataModel(multipleDetails.getCaseData(), caseData),
                errors,
                multipleData.getMultipleReference(),
                YES,
                createUpdatesBusSender,
                String.valueOf(multipleObjectsFiltered.size()));

    }

    public void sendPreAcceptToSinglesWithConfirmation(String userToken, MultipleDetails multipleDetails,
                                                       List<String> errors) {

        MultipleData multipleData = multipleDetails.getCaseData();
        CasePreAcceptType casePreAcceptType = multipleData.getPreAcceptCase();

        sendTaskToSinglesWithConfirmation(userToken, multipleDetails,
                PersistentQHelper.getPreAcceptDataModel(casePreAcceptType.getDateAccepted()),
                errors,
                YES);

    }

    public void sendRejectToSinglesWithConfirmation(String userToken, MultipleDetails multipleDetails,
                                                    List<String> errors) {

        MultipleData multipleData = multipleDetails.getCaseData();
        CasePreAcceptType casePreAcceptType = multipleData.getPreAcceptCase();

        sendTaskToSinglesWithConfirmation(userToken, multipleDetails,
                PersistentQHelper.getRejectDataModel(casePreAcceptType.getDateRejected(), casePreAcceptType.getRejectReason()),
                errors,
                YES);

    }

    public void sendCloseToSinglesWithoutConfirmation(String userToken, MultipleDetails multipleDetails,
                                                   List<String> errors) {

        MultipleData multipleData = multipleDetails.getCaseData();

        sendTaskToSinglesWithConfirmation(userToken, multipleDetails,
                PersistentQHelper.getCloseDataModel(multipleData.getClerkResponsible(), multipleData.getFileLocation(), multipleData.getNotes()),
                errors,
                NO);

    }

    private void sendTaskToSinglesWithConfirmation(String userToken, MultipleDetails multipleDetails, DataModelParent dataModelParent,
                                                   List<String> errors, String confirmation) {

        MultipleData multipleData = multipleDetails.getCaseData();
        List<String> ethosCaseRefCollection = getEthosCaseRefCollection(userToken, multipleData, errors);
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                username,
                ethosCaseRefCollection,
                dataModelParent,
                errors,
                multipleData.getMultipleReference(),
                confirmation,
                createUpdatesBusSender,
                String.valueOf(ethosCaseRefCollection.size()));

    }

    public void sendResetMultipleStateWithoutConfirmation(String userToken, String caseTypeId, String jurisdiction,
                                                          MultipleData multipleData, List<String> errors) {

        String caseToSend = MultiplesHelper.getCurrentLead(multipleData.getLeadCase());
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(caseTypeId,
                jurisdiction,
                username,
                new ArrayList<>(Collections.singletonList(caseToSend)),
                PersistentQHelper.getResetStateModel(),
                errors,
                multipleData.getMultipleReference(),
                NO,
                createUpdatesBusSender,
                "1");

    }

    public List<String> getEthosCaseRefCollection(String userToken, MultipleData newMultipleData, List<String> errors) {

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(newMultipleData),
                        errors,
                        newMultipleData,
                        FilterExcelType.ALL);

        return new ArrayList<>(multipleObjects.keySet());

    }

    public String getLeadCaseFromExcel(String userToken, MultipleData newMultipleData, List<String> errors) {

        List<String> ethosCaseRefCollection = getEthosCaseRefCollection(userToken, newMultipleData, errors);

        return ethosCaseRefCollection.isEmpty() ? "" : ethosCaseRefCollection.get(0);

    }

    private void sendUpdatesToSinglesLogicCheckingLead(String userToken, MultipleDetails multipleDetails, List<String> errors,
                                                      String newLeadCase, TreeMap<String, Object> multipleObjects) {

        String oldLeadCase = MultiplesHelper.getCurrentLead(multipleDetails.getCaseData().getLeadCase());

        if (!oldLeadCase.equals(newLeadCase)) {

            log.info("Sending update to old lead case as not lead any more: " + oldLeadCase);

            sendCreationUpdatesToSinglesWithoutConfirmation(userToken,
                    multipleDetails.getCaseTypeId(),
                    multipleDetails.getJurisdiction(),
                    multipleDetails.getCaseData(),
                    errors,
                    new ArrayList<>(Collections.singletonList(oldLeadCase)),
                    newLeadCase);

        }

        if (multipleObjects.keySet().isEmpty() || !oldLeadCase.equals(newLeadCase)) {

            log.info("Adding new lead: " + newLeadCase);

            addLeadMarkUp(userToken, multipleDetails.getCaseTypeId(),
                    multipleDetails.getCaseData(), newLeadCase, "");

        }

    }

    public void sendUpdatesToSinglesLogic(String userToken, MultipleDetails multipleDetails, List<String> errors,
                                           String newLeadCase, TreeMap<String, Object> multipleObjects,
                                           List<String> newEthosCaseRefCollection) {

        sendUpdatesToSinglesLogicCheckingLead(userToken, multipleDetails, errors, newLeadCase, multipleObjects);

        sendCreationUpdatesToSinglesWithoutConfirmation(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseData(),
                errors,
                newEthosCaseRefCollection,
                newLeadCase);

    }

}
