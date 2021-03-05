package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.*;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.bulk.types.MultipleType;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;
import uk.gov.hmcts.ethos.replacement.docmosis.tasks.BulkPreAcceptTask;
import uk.gov.hmcts.ethos.replacement.docmosis.tasks.BulkUpdateBulkTask;
import uk.gov.hmcts.ethos.replacement.docmosis.tasks.BulkUpdateTask;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("bulkUpdateService")
public class BulkUpdateService {

    private static final String MESSAGE = "Failed to update case for case id : ";
    private final CcdClient ccdClient;
    private final UserService userService;
    private final CreateUpdatesBusSender createUpdatesBusSender;

    @Autowired
    public BulkUpdateService(CcdClient ccdClient, UserService userService, CreateUpdatesBusSender createUpdatesBusSender) {
        this.ccdClient = ccdClient;
        this.userService = userService;
        this.createUpdatesBusSender = createUpdatesBusSender;
    }

    public BulkRequestPayload bulkUpdateLogic(BulkDetails bulkDetails, String userToken) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();

        // 1) Get a list with cases from the search collection
        List<SearchTypeItem> searchTypeItemList = bulkDetails.getCaseData().getSearchCollection();

        List<String> errors = new ArrayList<>();
        if (searchTypeItemList == null) {
            errors.add("There is not searchable list in the multiple case yet");
        } else {
            String multipleReferenceV2 = bulkDetails.getCaseData().getMultipleReferenceV2();
            //If exists the multiple with ref then returned to be used
            //MultRefComplexType multRefComplexType = checkMultipleReferenceExists(bulkDetails, userToken, multipleReferenceV2)
            MultRefComplexType multRefComplexType = checkMultipleReferenceExistsES(bulkDetails, userToken, multipleReferenceV2);

            // 2) Check if new multiple reference exists or it has the same as the current bulk
            if (!isNullOrEmpty(multipleReferenceV2)) {
                if (!multRefComplexType.isExist() || multipleReferenceV2.equals(bulkDetails.getCaseData().getMultipleReference())) {
                    errors.add("Multiple reference does not exist or it is the same as the current multiple case");
                }
            }
            if (errors.isEmpty()) {
                // 3) Update fields to the searched cases
                log.info("Updating fields to the searched cases");
                SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType = createEventToUpdateCasesSearched(searchTypeItemList, bulkDetails, userToken,
                        multRefComplexType.getSubmitBulkEvent(), multipleReferenceV2);
                if (submitBulkEventSubmitEventType.getErrors() != null) {
                    errors.addAll(submitBulkEventSubmitEventType.getErrors());
                }
                // 4) Refresh multiple collection for bulk
                List<MultipleTypeItem> multipleTypeItemListAux = refreshMultipleCollection(bulkDetails, submitBulkEventSubmitEventType);
                // 5) If still cases in the multiples then update with bulk update specific (flags...) No need if moving all cases
                if (!multipleTypeItemListAux.isEmpty()) {
                    multipleTypeItemListAux = performOtherMultipleUpdate(bulkDetails.getCaseData(), multipleTypeItemListAux, searchTypeItemList);
                }
                // 6) Create an event for each update single & bulk and assign lead
                BulkDetails bulkDetailsAux = BulkHelper.setMultipleCollection(bulkDetails,
                        createUpdateEventsAndAssignLead(multipleTypeItemListAux, bulkDetails, userToken, submitBulkEventSubmitEventType));
                bulkRequestPayload.setBulkDetails(bulkDetailsAux);
            }
        }
        if (bulkRequestPayload.getBulkDetails() == null) {
            bulkRequestPayload.setBulkDetails(bulkDetails);
        }
        bulkRequestPayload.setErrors(errors);
        return bulkRequestPayload;
    }

    private List<MultipleTypeItem> createUpdateEventsAndAssignLead(List<MultipleTypeItem> multipleTypeItems, BulkDetails bulkDetails, String authToken,
                                                                   SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType) {
        Instant start = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        String leadId = "";
        if (!multipleTypeItems.isEmpty()) {
            multipleTypeItems.get(0).getValue().setLeadClaimantM(YES);
            leadId = multipleTypeItems.get(0).getValue().getCaseIDM();
            log.info("Updating lead case for multipleCollection: " + leadId);
            try {
                SubmitEvent submitEvent = hasLeadCaseBeenUpdated(submitBulkEventSubmitEventType, leadId);
                if (submitEvent == null) {
                    submitEvent = ccdClient.retrieveCase(authToken, UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                            bulkDetails.getJurisdiction(), leadId);
                }
                submitEvent.getCaseData().setLeadClaimant(YES);
                executor.execute(new BulkUpdateTask(bulkDetails, submitEvent, authToken, ccdClient));
            } catch (IOException e) {
                log.error("Error processing ES retrieving lead case");
            }
        }
        if (submitBulkEventSubmitEventType.getSubmitBulkEventToUpdate() != null || submitBulkEventSubmitEventType.getSubmitEventList() != null) {
            log.info("Sending updates for single cases and bulk updated");
            executor.execute(new BulkUpdateBulkTask(bulkDetails, authToken, ccdClient, submitBulkEventSubmitEventType, leadId));
        }
        log.info("End in time: " + Duration.between(start, Instant.now()).toMillis());
        executor.shutdown();
        return multipleTypeItems;
    }

    private SubmitEvent hasLeadCaseBeenUpdated(SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType, String leadId) {
        if (submitBulkEventSubmitEventType.getSubmitEventList() != null) {
            Optional<SubmitEvent> submitEventOptional = submitBulkEventSubmitEventType.getSubmitEventList().stream()
                    .filter(submitEvent -> String.valueOf(submitEvent.getCaseId()).equals(leadId))
                    .findFirst();
            return submitEventOptional.orElse(null);
        }
        return null;
    }

    private SubmitBulkEventSubmitEventType createEventToUpdateCasesSearched(List<SearchTypeItem> searchTypeItemList, BulkDetails bulkDetails, String userToken,
                                                                            SubmitBulkEvent submitBulkEvent, String multipleReferenceV2) {
        SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType = new SubmitBulkEventSubmitEventType();
        List<SubmitEvent> submitEventList = new ArrayList<>();
        try {
            for (SearchTypeItem searchTypeItem : searchTypeItemList) {
                submitBulkEventSubmitEventType = caseUpdateFieldsRequest(bulkDetails, searchTypeItem, userToken, submitBulkEvent);
                if (submitBulkEventSubmitEventType.getSubmitEvent() != null) {
                    log.info("Case updated then add to the submitEventList");
                    submitEventList.add(submitBulkEventSubmitEventType.getSubmitEvent());
                }
                if (!isNullOrEmpty(multipleReferenceV2)) {
                    log.info("Removing case from multiples as it has been already moved to a different BULK");
                    bulkDetails.getCaseData().getMultipleCollection()
                            .removeIf(multipleTypeItem -> searchTypeItem.getValue().getEthosCaseReferenceS().equals(multipleTypeItem.getValue().getEthosCaseReferenceM()));
                    bulkDetails.getCaseData().setSearchCollection(new ArrayList<>());
                    bulkDetails.getCaseData().setSearchCollectionCount(null);
                }
            }
            //Add the cases searched and updated to a different bulk if multiple ref changed
            if (!searchTypeItemList.isEmpty() && !isNullOrEmpty(multipleReferenceV2)) {
                log.info("Adding the data to the new bulk");
                if (submitBulkEventSubmitEventType.getSubmitBulkEvent() != null) {
                    submitBulkEvent.setCaseData(submitBulkEventSubmitEventType.getSubmitBulkEvent().getCaseData());
                }
                submitBulkEventSubmitEventType.setSubmitBulkEventToUpdate(submitBulkEvent);
            }
        } catch (Exception ex) {
            log.error("Error processing bulk update threads");
        }
        submitBulkEventSubmitEventType.setBulkDetails(bulkDetails);
        submitBulkEventSubmitEventType.setSubmitEventList(submitEventList);
        return submitBulkEventSubmitEventType;
    }

    private List<MultipleTypeItem> refreshMultipleCollection(BulkDetails bulkDetails, SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType) {
        List<MultipleTypeItem> multipleTypeItemListAux = new ArrayList<>();
        for (MultipleTypeItem multipleTypeItem : bulkDetails.getCaseData().getMultipleCollection()) {
            //Check if any case has been updated
            if (submitBulkEventSubmitEventType.getSubmitEventList() != null) {
                Optional<SubmitEvent> optSubmitEvent = submitBulkEventSubmitEventType.getSubmitEventList().stream().filter(submitEvent ->
                        submitEvent.getCaseData().getEthosCaseReference().equals(multipleTypeItem.getValue().getEthosCaseReferenceM())).findFirst();
                if (optSubmitEvent.isPresent()) {
                    MultipleType multipleType = BulkHelper.getMultipleTypeFromSubmitEvent(optSubmitEvent.get());
                    multipleTypeItem.setValue(multipleType);
                }
            }
            multipleTypeItemListAux.add(multipleTypeItem);
        }
        return multipleTypeItemListAux;
    }

    public BulkRequestPayload clearUpFields(BulkRequestPayload bulkRequestPayload) {
        BulkData bulkData = bulkRequestPayload.getBulkDetails().getCaseData();
        bulkData.setClaimantRepV2(null);
        bulkData.setRespondentRepV2(null);
        bulkData.setMultipleReferenceV2(null);
        bulkData.setPositionTypeV2(null);
        bulkData.setClerkResponsibleV2(null);
        bulkData.setFileLocationV2(null);
        bulkData.setFeeGroupReferenceV2(null);
        bulkData.setClaimantSurnameV2(null);
        bulkData.setRespondentSurnameV2(null);
        bulkData.setFlag1Update(null);
        bulkData.setFlag2Update(null);
        bulkData.setEQPUpdate(null);
        bulkData.setOutcomeUpdate(null);
        bulkData.setManagingOffice(null);
        bulkData.setFileLocationAberdeen(null);
        bulkData.setFileLocationDundee(null);
        bulkData.setFileLocationEdinburgh(null);
        bulkData.setFileLocationGlasgow(null);
        bulkRequestPayload.getBulkDetails().setCaseData(bulkData);
        return bulkRequestPayload;
    }

//    private MultRefComplexType checkMultipleReferenceExists(BulkDetails bulkDetails, String authToken, String multipleReference) {
//        try {
//            MultRefComplexType multRefComplexType = new MultRefComplexType();
//            if (!isNullOrEmpty(multipleReference)) {
//                List<SubmitBulkEvent> submitBulkEvents = ccdClient.retrieveBulkCases(authToken,
//                        bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction());
//                Optional<SubmitBulkEvent> optSubmitBulkEvent = submitBulkEvents.stream().filter(submitBulkEvent -> submitBulkEvent.getCaseData().getMultipleReference().equals(multipleReference)).findFirst();
//                if (optSubmitBulkEvent.isPresent()) {
//                    log.info("SubmitBulkEvent exists!!");
//                    multRefComplexType.setExist(true);
//                    multRefComplexType.setSubmitBulkEvent(optSubmitBulkEvent.get());
//                } else {
//                    multRefComplexType.setExist(false);
//                }
//            }
//            return multRefComplexType;
//        } catch (Exception ex) {
//            throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
//        }
//    }

    private MultRefComplexType checkMultipleReferenceExistsES(BulkDetails bulkDetails, String authToken, String multipleReference) {
        try {
            MultRefComplexType multRefComplexType = new MultRefComplexType();
            if (!isNullOrEmpty(multipleReference)) {
                List<SubmitBulkEvent> submitBulkEvents = ccdClient.retrieveBulkCasesElasticSearch(authToken,
                        bulkDetails.getCaseTypeId(), multipleReference);
                //Optional<SubmitBulkEvent> optSubmitBulkEvent = submitBulkEvents.stream().filter(submitBulkEvent -> submitBulkEvent.getCaseData().getMultipleReference().equals(multipleReference)).findFirst();
                //if (optSubmitBulkEvent.isPresent()) {
                if (!submitBulkEvents.isEmpty()) {
                    multRefComplexType.setExist(true);
                    multRefComplexType.setSubmitBulkEvent(submitBulkEvents.get(0));
                } else {
                    multRefComplexType.setExist(false);
                }
            }
            return multRefComplexType;
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
        }
    }

    SubmitBulkEventSubmitEventType caseUpdateFieldsRequest(BulkDetails bulkDetails, SearchTypeItem searchTypeItem, String authToken,
                                                           SubmitBulkEvent submitBulkEvent) {
        try {
            SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType = new SubmitBulkEventSubmitEventType();
            String caseId = searchTypeItem.getId();
            BulkData bulkData = bulkDetails.getCaseData();
            String respondentNameNewValue = bulkData.getRespondentSurnameV2();
            String claimantRepNewValue = bulkData.getClaimantRepV2();
            String respondentRepNewValue = bulkData.getRespondentRepV2();
            String managingOfficeNewValue = bulkData.getManagingOffice();
            String fileLocationNewValue = bulkData.getFileLocationV2();
            String fileLocationGlasgowNewValue = bulkData.getFileLocationGlasgow();
            String fileLocationAberdeenNewValue = bulkData.getFileLocationAberdeen();
            String fileLocationDundeeNewValue = bulkData.getFileLocationDundee();
            String fileLocationEdinburghNewValue = bulkData.getFileLocationEdinburgh();
            String multipleRefNewValue = bulkData.getMultipleReferenceV2();
            String clerkNewValue = bulkData.getClerkResponsibleV2();
            String positionTypeNewValue = bulkData.getPositionTypeV2();
            String flag1NewValue = bulkData.getFlag1Update();
            String flag2NewValue = bulkData.getFlag2Update();
            String EQPNewValue = bulkData.getEQPUpdate();
            String jurCodeSelected = bulkData.getJurCodesDynamicList().getValue().getCode();
            String outcomeNewValue = bulkData.getOutcomeUpdate();
            SubmitEvent submitEvent = ccdClient.retrieveCase(authToken, UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            boolean updated = false;
            boolean multipleReferenceUpdated = false;
            if (!isNullOrEmpty(respondentNameNewValue)) {
                updated = true;
                if (submitEvent.getCaseData().getRespondentCollection()!=null && !submitEvent.getCaseData().getRespondentCollection().isEmpty()) {
                    RespondentSumTypeItem respondentSumTypeItem = submitEvent.getCaseData().getRespondentCollection().get(0);
                    respondentSumTypeItem.getValue().setRespondentName(respondentNameNewValue);
                    submitEvent.getCaseData().getRespondentCollection().set(0, respondentSumTypeItem);
                } else {
                    List<RespondentSumTypeItem> respondentSumTypeItems = new ArrayList<>();
                    RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
                    RespondentSumType respondentSumType = new RespondentSumType();
                    respondentSumType.setRespondentName(respondentNameNewValue);
                    respondentSumTypeItem.setValue(respondentSumType);
                    respondentSumTypeItems.add(respondentSumTypeItem);
                    submitEvent.getCaseData().setRespondentCollection(respondentSumTypeItems);
                }
            }
            if (!isNullOrEmpty(jurCodeSelected) && !jurCodeSelected.equals(SELECT_NONE_VALUE) && !isNullOrEmpty(outcomeNewValue)) {
                List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>();
                if (submitEvent.getCaseData().getJurCodesCollection() != null && !submitEvent.getCaseData().getJurCodesCollection().isEmpty()) {
                    for (JurCodesTypeItem jurCodesTypeItem : submitEvent.getCaseData().getJurCodesCollection()) {
                        if (jurCodesTypeItem.getValue().getJuridictionCodesList().equals(jurCodeSelected)) {
                            JurCodesType jurCodesType = jurCodesTypeItem.getValue();
                            jurCodesType.setJudgmentOutcome(outcomeNewValue);
                            jurCodesTypeItem.setValue(jurCodesType);
                            updated = true;
                        }
                        jurCodesTypeItems.add(jurCodesTypeItem);
                    }
                }
                submitEvent.getCaseData().setJurCodesCollection(jurCodesTypeItems);
            }
            if (!isNullOrEmpty(fileLocationNewValue)) {
                updated = true;
                submitEvent.getCaseData().setFileLocation(fileLocationNewValue);
            }
            if (!isNullOrEmpty(fileLocationGlasgowNewValue)) {
                updated = true;
                submitEvent.getCaseData().setFileLocationGlasgow(fileLocationGlasgowNewValue);
            }
            if (!isNullOrEmpty(fileLocationAberdeenNewValue)) {
                updated = true;
                submitEvent.getCaseData().setFileLocationAberdeen(fileLocationAberdeenNewValue);
            }
            if (!isNullOrEmpty(fileLocationDundeeNewValue)) {
                updated = true;
                submitEvent.getCaseData().setFileLocationDundee(fileLocationDundeeNewValue);
            }
            if (!isNullOrEmpty(fileLocationEdinburghNewValue)) {
                updated = true;
                submitEvent.getCaseData().setFileLocationEdinburgh(fileLocationEdinburghNewValue);
            }
            if (!isNullOrEmpty(managingOfficeNewValue)) {
                updated = true;
                submitEvent.getCaseData().setManagingOffice(managingOfficeNewValue);
            }
            if (!isNullOrEmpty(claimantRepNewValue)) {
                updated = true;
                RepresentedTypeC representedTypeC;
                if (submitEvent.getCaseData().getRepresentativeClaimantType() != null) {
                    representedTypeC = submitEvent.getCaseData().getRepresentativeClaimantType();
                    representedTypeC.setNameOfRepresentative(claimantRepNewValue);
                    submitEvent.getCaseData().setRepresentativeClaimantType(representedTypeC);
                } else {
                    representedTypeC = new RepresentedTypeC();
                    representedTypeC.setNameOfRepresentative(claimantRepNewValue);
                    submitEvent.getCaseData().setRepresentativeClaimantType(representedTypeC);
                    submitEvent.getCaseData().setClaimantRepresentedQuestion(YES);
                }
                submitEvent.getCaseData().setRepresentativeClaimantType(representedTypeC);
            }
            if (!isNullOrEmpty(respondentRepNewValue)) {
                updated = true;
                if (submitEvent.getCaseData().getRepCollection() != null && !submitEvent.getCaseData().getRepCollection().isEmpty()) {
                    RepresentedTypeRItem representedTypeRItem = submitEvent.getCaseData().getRepCollection().get(0);
                    RepresentedTypeR representedTypeR;
                    if (representedTypeRItem != null) {
                        representedTypeR = representedTypeRItem.getValue();
                        representedTypeR.setNameOfRepresentative(respondentRepNewValue);
                    } else {
                        representedTypeRItem = new RepresentedTypeRItem();
                        representedTypeR = new RepresentedTypeR();
                        representedTypeR.setNameOfRepresentative(respondentRepNewValue);
                    }
                    representedTypeRItem.setValue(representedTypeR);
                    submitEvent.getCaseData().getRepCollection().set(0, representedTypeRItem);
                } else {
                    RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
                    RepresentedTypeR representedTypeR = new RepresentedTypeR();
                    representedTypeR.setNameOfRepresentative(respondentRepNewValue);
                    representedTypeRItem.setValue(representedTypeR);
                    List<RepresentedTypeRItem> repCollection = new ArrayList<>(Collections.singletonList(representedTypeRItem));
                    submitEvent.getCaseData().setRepCollection(repCollection);
                }
            }
            if (!isNullOrEmpty(multipleRefNewValue)) {
                updated = true;
                multipleReferenceUpdated = true;
                submitEvent.getCaseData().setMultipleReference(multipleRefNewValue);
            }
            if (!isNullOrEmpty(clerkNewValue)) {
                updated = true;
                submitEvent.getCaseData().setClerkResponsible(clerkNewValue);
            }
            if (!isNullOrEmpty(positionTypeNewValue)) {
                updated = true;
                submitEvent.getCaseData().setPositionType(positionTypeNewValue);
            }
            if (!isNullOrEmpty(flag1NewValue)) {
                updated = true;
                submitEvent.getCaseData().setFlag1(flag1NewValue);
            }
            if (!isNullOrEmpty(flag2NewValue)) {
                updated = true;
                submitEvent.getCaseData().setFlag2(flag2NewValue);
            }
            if (!isNullOrEmpty(EQPNewValue)) {
                updated = true;
                submitEvent.getCaseData().setEQP(EQPNewValue);
            }
            return updateOldSubmitBulk(updated, multipleReferenceUpdated, submitBulkEvent,
                    submitEvent, submitBulkEventSubmitEventType);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + searchTypeItem.getId() + ex.getMessage());
        }
    }

    private SubmitBulkEventSubmitEventType updateOldSubmitBulk(boolean updated, boolean multipleReferenceUpdated, SubmitBulkEvent submitBulkEvent,
                                                               SubmitEvent submitEvent, SubmitBulkEventSubmitEventType submitBulkEventSubmitEventType) {
        if (updated) {
            boolean isThisCaseLead = false;
            // If multipleReference was updated then add the new values to the bulk case
            if (multipleReferenceUpdated) {
                BulkData bulkData1 = submitBulkEvent.getCaseData();
                MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
                multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
                multipleTypeItem.setValue(BulkHelper.getMultipleTypeFromSubmitEvent(submitEvent));
                if (bulkData1.getMultipleCollection() != null) {
                    multipleTypeItem.getValue().setLeadClaimantM(NO);
                    bulkData1.getMultipleCollection().add(multipleTypeItem);
                } else {
                    isThisCaseLead = true;
                    multipleTypeItem.getValue().setLeadClaimantM(YES);
                    bulkData1.setMultipleCollection(new ArrayList<>(Collections.singletonList(multipleTypeItem)));
                }
                //Updating the caseIdCollection for the new Multiple
                CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
                caseIdTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
                CaseType caseType = new CaseType();
                caseType.setEthosCaseReference(submitEvent.getCaseData().getEthosCaseReference());
                caseIdTypeItem.setValue(caseType);
                log.info("Case Id Collection: " + bulkData1.getCaseIdCollection());
                if (bulkData1.getCaseIdCollection() != null) {
                    bulkData1.getCaseIdCollection().add(caseIdTypeItem);
                } else {
                    bulkData1.setCaseIdCollection(new ArrayList<>(Collections.singletonList(caseIdTypeItem)));
                }
                bulkData1.setMultipleCollectionCount(String.valueOf(bulkData1.getMultipleCollection().size() + 1));
                bulkData1.setSearchCollection(new ArrayList<>());
                bulkData1.setSearchCollectionCount(null);
                submitBulkEvent.setCaseData(bulkData1);
            }
            if (isThisCaseLead) {
                submitEvent.getCaseData().setLeadClaimant(YES);
            } else {
                submitEvent.getCaseData().setLeadClaimant(NO);
            }
            //Update the value to return
            submitBulkEventSubmitEventType.setSubmitBulkEvent(submitBulkEvent);
            submitBulkEventSubmitEventType.setSubmitEvent(submitEvent);
        }
        return submitBulkEventSubmitEventType;
    }

    private List<MultipleTypeItem> performOtherMultipleUpdate(BulkData bulkData, List<MultipleTypeItem> multipleTypeItemList, List<SearchTypeItem> searchTypeItemList) {
        List<MultipleTypeItem> multipleTypeItemsAux = new ArrayList<>();
        List<String> refNumbersFromSearchList = searchTypeItemList.stream()
                .map(searchTypeItem -> searchTypeItem.getValue().getEthosCaseReferenceS())
                .collect(Collectors.toList());
        String subMultipleRefNewValue = bulkData.getSubMultipleDynamicList() != null ? bulkData.getSubMultipleDynamicList().getValue().getCode() : "";
        String subMultipleTitleNewValue = bulkData.getSubMultipleDynamicList() != null ? bulkData.getSubMultipleDynamicList().getValue().getLabel() : "";
        for (MultipleTypeItem multipleTypeItem : multipleTypeItemList) {
            boolean updated = false;
            if (!isNullOrEmpty(subMultipleRefNewValue) &&
                    !subMultipleRefNewValue.equals(DEFAULT_SELECT_ALL_VALUE) &&
                    refNumbersFromSearchList.contains(multipleTypeItem.getValue().getEthosCaseReferenceM())) {
                multipleTypeItem.getValue().setSubMultipleM(subMultipleRefNewValue);
                multipleTypeItem.getValue().setSubMultipleTitleM(subMultipleTitleNewValue);
                updated = true;
            }
            //Keep the old info for flags and subMultiple ref
            if (!updated) {
                Optional<MultipleTypeItem> previousMultipleTypeItem = bulkData.getMultipleCollection().stream()
                        .filter(multipleTypeItem1 -> multipleTypeItem.getValue().getEthosCaseReferenceM().equals(multipleTypeItem1.getValue().getEthosCaseReferenceM()))
                        .findFirst();
                multipleTypeItem.getValue().setSubMultipleM(previousMultipleTypeItem.isPresent() ? previousMultipleTypeItem.get().getValue().getSubMultipleM() : " ");
                multipleTypeItem.getValue().setSubMultipleTitleM(previousMultipleTypeItem.isPresent() ? previousMultipleTypeItem.get().getValue().getSubMultipleTitleM() : " ");
            }
            multipleTypeItemsAux.add(multipleTypeItem);
        }
        return multipleTypeItemsAux;
    }

    public BulkRequestPayload bulkPreAcceptLogic(BulkDetails bulkDetails, List<SubmitEvent> submitEventList,
                                                 String userToken, boolean isPersistentQ) {
        List<String> errors = new ArrayList<>();
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        if (!submitEventList.isEmpty()) {

            // 1) Update list of cases to the multiple bulk case collection
            List<MultipleTypeItem> multipleTypeItemList = new ArrayList<>();
            for (MultipleTypeItem multipleTypeItem : bulkDetails.getCaseData().getMultipleCollection()) {
                MultipleType multipleType = multipleTypeItem.getValue();
                if (multipleType.getStateM().equals(SUBMITTED_STATE)) {
                    multipleType.setStateM(ACCEPTED_STATE);
                    multipleTypeItem.setValue(multipleType);
                }
                multipleTypeItemList.add(multipleTypeItem);
            }
            bulkDetails.getCaseData().setMultipleCollection(multipleTypeItemList);

            // 2) Create an event to update state of the cases
            if (!isPersistentQ) {
                sendPreAcceptUpdates(bulkDetails, submitEventList, userToken);
            } else {
                List<String> ethosCaseRefCollection = BulkHelper.getCaseIds(bulkDetails);
                PersistentQHelper.sendUpdatesPersistentQ(bulkDetails,
                        userService.getUserDetails(userToken).getEmail(),
                        ethosCaseRefCollection,
                        PersistentQHelper.getPreAcceptDataModel("dateAccepted"),
                        errors,
                        bulkDetails.getCaseData().getMultipleReference(),
                        createUpdatesBusSender,
                        String.valueOf(ethosCaseRefCollection.size()));
            }

            bulkRequestPayload.setBulkDetails(bulkDetails);
        } else {
            errors.add("No cases on the multiple case: " + bulkDetails.getCaseId());
        }
        if (bulkRequestPayload.getBulkDetails() == null) {
            bulkRequestPayload.setBulkDetails(bulkDetails);
        }
        bulkRequestPayload.setErrors(errors);
        return bulkRequestPayload;
    }

    private void sendPreAcceptUpdates(BulkDetails bulkDetails, List<SubmitEvent> submitEventList, String userToken) {
        for (SubmitEvent submitEvent : submitEventList) {
            if (submitEvent.getState().equals(SUBMITTED_STATE)) {
                caseUpdatePreAcceptRequest(bulkDetails, submitEvent, userToken);
            } else {
                log.info("The case is already accepted");
            }
        }
    }

    private void caseUpdatePreAcceptRequest(BulkDetails bulkDetails, SubmitEvent submitEvent, String userToken) {
        log.info("Current state ---> " + submitEvent.getState());
        Instant start = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        executor.execute(new BulkPreAcceptTask(bulkDetails, submitEvent, userToken, ccdClient));
        log.info("End in time: " + Duration.between(start, Instant.now()).toMillis());
        executor.shutdown();
    }

}
