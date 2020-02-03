package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.MultipleType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.tasks.BulkCreationTask;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Service("bulkCreationService")
public class BulkCreationService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private final CcdClient ccdClient;
    private final BulkSearchService bulkSearchService;

    @Autowired
    public BulkCreationService(CcdClient ccdClient, BulkSearchService bulkSearchService) {
        this.ccdClient = ccdClient;
        this.bulkSearchService = bulkSearchService;
    }

    public BulkRequestPayload bulkCreationLogic(BulkDetails bulkDetails, BulkCasesPayload bulkCasesPayload, String userToken, boolean afterSubmittedCallback) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        if (bulkCasesPayload.getErrors().isEmpty()) {
            // 1) Retrieve cases by ethos reference
            List<SubmitEvent> submitEvents = bulkCasesPayload.getSubmitEvents();
            if (!afterSubmittedCallback) {
                // 2) Create multiple ref number
                bulkDetails.getCaseData().setMultipleReference(bulkSearchService.generateMultipleRef(bulkDetails));
                // 3) Add list of cases to the multiple bulk case collection
                if (!submitEvents.isEmpty()) {
                    // 4) Add lead case
                    submitEvents.get(0).getCaseData().setLeadClaimant("Yes");
                    List<MultipleTypeItem> multipleTypeItemList = BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents,
                            bulkDetails.getCaseData().getMultipleReference());
                    bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, multipleTypeItemList));
                } else {
                    bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, bulkDetails.getCaseData().getMultipleCollection()));
                }
            } else {
                // 5) Create an event to update multiple reference field to all cases
                createCaseEventsToUpdateMultipleRef(submitEvents, bulkDetails, userToken);
                bulkRequestPayload.setBulkDetails(bulkDetails);
            }
        } else {
            bulkRequestPayload.setBulkDetails(bulkDetails);
        }
        bulkRequestPayload.setErrors(bulkCasesPayload.getErrors());
        return bulkRequestPayload;
    }

    private void createCaseEventsToUpdateMultipleRef(List<SubmitEvent> submitEvents, BulkDetails bulkDetails, String userToken) {
        Instant start = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        for (SubmitEvent submitEvent : submitEvents) {
            executor.execute(new BulkCreationTask(bulkDetails, submitEvent, userToken,
                    bulkDetails.getCaseData().getMultipleReference(), "Multiple", ccdClient));
        }
        executor.shutdown();
        log.info("End in time: " + Duration.between(start, Instant.now()).toMillis());
    }

    public BulkRequestPayload bulkUpdateCaseIdsLogic(BulkRequest bulkRequest, String authToken) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        BulkCasesPayload bulkCasesPayload = updateBulkRequest(bulkRequest, authToken);
        if (bulkCasesPayload.getErrors().isEmpty()) {
            bulkRequest.setCaseDetails(BulkHelper.setMultipleCollection(bulkRequest.getCaseDetails(), bulkCasesPayload.getMultipleTypeItems()));
            bulkRequest.setCaseDetails(BulkHelper.clearSearchCollection(bulkRequest.getCaseDetails()));
        }
        bulkRequestPayload.setErrors(bulkCasesPayload.getErrors());
        bulkRequestPayload.setBulkDetails(bulkRequest.getCaseDetails());
        return bulkRequestPayload;
    }

//    BulkCasesPayload updateBulkRequest(BulkRequest bulkRequest, String authToken) {
//        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
//        BulkCasesPayload bulkCasesPayload = new BulkCasesPayload();
//        List<String> errors = new ArrayList<>();
//        List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
//        List<String> multipleCaseIds = BulkHelper.getMultipleCaseIds(bulkDetails);
//        try {
//            //Get all caseIds user introduced and current ones
//            List<String> unionLists = Stream.concat(caseIds.stream(), multipleCaseIds.stream())
//                    .distinct().collect(Collectors.toList());
//            bulkCasesPayload = bulkSearchService.filterSubmitEventsElasticSearch(
//                    ccdClient.retrieveCasesElasticSearch(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), unionLists),
//                    bulkDetails.getCaseData().getMultipleReference(), false);
//            if (bulkCasesPayload.getErrors().isEmpty()) {
//                List<SubmitEvent> allSubmitEventsToUpdate = bulkCasesPayload.getSubmitEvents();
//                if (!allSubmitEventsToUpdate.isEmpty()) {
//                    List<SubmitEvent> casesToAdd = new ArrayList<>();
//                    List<SubmitEvent> casesToRemove = new ArrayList<>();
//                    for (SubmitEvent submitEvent : allSubmitEventsToUpdate) {
//                        log.info("State SubmitEvent: " + submitEvent.getState());
//                        String ethosCaseRef = submitEvent.getCaseData().getEthosCaseReference();
//                        if (caseIds.contains(ethosCaseRef) && !multipleCaseIds.contains(ethosCaseRef)) {
//                            casesToAdd.add(submitEvent);
//                        } else if (!caseIds.contains(ethosCaseRef) && multipleCaseIds.contains(ethosCaseRef)) {
//                            casesToRemove.add(submitEvent);
//                        }
//                    }
//                    // Delete old cases
//                    List<MultipleTypeItem> multipleTypeItems = new ArrayList<>();
//                    if (bulkDetails.getCaseData().getMultipleCollection() != null) {
//                        multipleTypeItems = getMultipleTypeListAfterDeletions(bulkDetails.getCaseData().getMultipleCollection(),
//                                casesToRemove, bulkRequest.getCaseDetails(), authToken);
//                    }
//                    // Add new cases
//                    bulkCasesPayload.setMultipleTypeItems(getMultipleTypeListAfterAdditions(multipleTypeItems, casesToAdd,
//                            bulkRequest.getCaseDetails(), authToken));
//                } else {
//                    bulkCasesPayload.setMultipleTypeItems(bulkDetails.getCaseData().getMultipleCollection());
//                }
//            } else {
//                errors.addAll(bulkCasesPayload.getErrors());
//            }
//        } catch (Exception ex) {
//            log.error("Error processing bulk update threads");
//        }
//        bulkCasesPayload.setErrors(errors);
//        return bulkCasesPayload;
//    }

    BulkCasesPayload updateBulkRequest(BulkRequest bulkRequest, String authToken) {
        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
        BulkCasesPayload bulkCasesPayload = new BulkCasesPayload();
        List<String> errors = new ArrayList<>();
        List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
        List<String> multipleCaseIds = BulkHelper.getMultipleCaseIds(bulkDetails);
        try {
            //Get all caseIds user introduced and current ones
            List<String> unionLists = Stream.concat(caseIds.stream(), multipleCaseIds.stream())
                    .distinct().collect(Collectors.toList());
            log.info("UNION LIST:" + unionLists);
            log.info("multipleCaseIds: " + multipleCaseIds);
            log.info("caseIds: " + caseIds);
            bulkCasesPayload = bulkSearchService.filterSubmitEventsElasticSearch(
                    ccdClient.retrieveCasesElasticSearch(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), unionLists),
                    bulkDetails.getCaseData().getMultipleReference(), false);
//            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken,
//                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction());
//            bulkCasesPayload = bulkSearchService.filterSubmitEvents(submitEvents, unionLists,
//                    bulkDetails.getCaseData().getMultipleReference(), false);
            log.info("SugmittedEvents: " + bulkCasesPayload.getSubmitEvents());
            if (bulkCasesPayload.getErrors().isEmpty()) {
                List<SubmitEvent> allSubmitEventsToUpdate = bulkCasesPayload.getSubmitEvents();
                if (!allSubmitEventsToUpdate.isEmpty()) {
                    bulkCasesPayload.setMultipleTypeItems(addRemoveNewCases(allSubmitEventsToUpdate, caseIds, multipleCaseIds, bulkDetails, authToken));
                } else {
                    bulkCasesPayload.setMultipleTypeItems(bulkDetails.getCaseData().getMultipleCollection());
                }
            } else {
                errors.addAll(bulkCasesPayload.getErrors());
            }
        } catch (Exception ex) {
            log.error("Error processing bulk update threads");
        }
        bulkCasesPayload.setErrors(errors);
        return bulkCasesPayload;
    }

    private List<MultipleTypeItem> addRemoveNewCases(List<SubmitEvent> allSubmitEventsToUpdate, List<String> caseIds, List<String> multipleCaseIds,
                                                     BulkDetails bulkDetails, String authToken) {
        List<SubmitEvent> casesToAdd = new ArrayList<>();
        List<SubmitEvent> casesToRemove = new ArrayList<>();
        boolean lead = false;
        String leadId = "";
        for (SubmitEvent submitEvent : allSubmitEventsToUpdate) {
            String ethosCaseRef = submitEvent.getCaseData().getEthosCaseReference();
            if (caseIds.contains(ethosCaseRef) && !lead) {
                submitEvent.getCaseData().setLeadClaimant("Yes");
                leadId = submitEvent.getCaseData().getEthosCaseReference();
                log.info("LeadId: " + leadId);
                lead = true;
            } else {
                submitEvent.getCaseData().setLeadClaimant("No");
            }
            log.info("State SubmitEvent: " + submitEvent.getState());
            if (caseIds.contains(ethosCaseRef) && !multipleCaseIds.contains(ethosCaseRef)) {
                casesToAdd.add(submitEvent);
            } else if (!caseIds.contains(ethosCaseRef) && multipleCaseIds.contains(ethosCaseRef)) {
                casesToRemove.add(submitEvent);
            }
        }
        // Delete old cases
        List<MultipleTypeItem> multipleTypeItems = new ArrayList<>();
        if (bulkDetails.getCaseData().getMultipleCollection() != null) {
            multipleTypeItems = getMultipleTypeListAfterDeletions(bulkDetails.getCaseData().getMultipleCollection(), casesToRemove, bulkDetails, authToken, leadId);
        }
        // Add new cases
        return getMultipleTypeListAfterAdditions(multipleTypeItems, casesToAdd, bulkDetails, authToken);
    }

    //    BulkCasesPayload updateBulkRequest(BulkRequest bulkRequest, String authToken) {
//        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
//        BulkCasesPayload bulkCasesPayload = new BulkCasesPayload();
//        List<String> errors = new ArrayList<>();
//        MultipleTypeListCaseRefNumListType multipleTypeListCaseRefNumListType = new MultipleTypeListCaseRefNumListType();
//        List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
//        List<String> multipleCaseIds = BulkHelper.getMultipleCaseIds(bulkDetails);
//        try {
//            List<String> unionLists = Stream.concat(caseIds.stream(), multipleCaseIds.stream())
//                    .distinct().collect(Collectors.toList());
//            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken,
//                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction());
//            bulkCasesPayload = bulkSearchService.filterSubmitEvents(submitEvents, unionLists,
//                    bulkDetails.getCaseData().getMultipleReference(), false);
//            if (bulkCasesPayload.getAlreadyTakenIds() != null && bulkCasesPayload.getAlreadyTakenIds().isEmpty()) {
//                List<SubmitEvent> allSubmitEventsToUpdate = bulkCasesPayload.getSubmitEvents();
//                if (!allSubmitEventsToUpdate.isEmpty()) {
//                    List<SubmitEvent> casesToAdd = new ArrayList<>();
//                    List<SubmitEvent> casesToRemove = new ArrayList<>();
//                    for (SubmitEvent submitEvent : allSubmitEventsToUpdate) {
//                        log.info("State SubmitEvent: " + submitEvent.getState());
//                        if (!submitEvent.getState().equals(SUBMITTED_STATE)) {
//                            log.info("Case submitted");
//                            String ethosCaseRef = submitEvent.getCaseData().getEthosCaseReference();
//                            if (caseIds.contains(ethosCaseRef) && !multipleCaseIds.contains(ethosCaseRef)) {
//                                casesToAdd.add(submitEvent);
//                            } else if (!caseIds.contains(ethosCaseRef) && multipleCaseIds.contains(ethosCaseRef)) {
//                                casesToRemove.add(submitEvent);
//                            }
//                        } else {
//                            errors.add("The state of case id: " + submitEvent.getCaseData().getEthosCaseReference() + " has not been accepted");
//                            bulkCasesPayload.setErrors(errors);
//                            return bulkCasesPayload;
//                        }
//                    }
//                    // Delete old cases
//                    if (bulkDetails.getCaseData().getMultipleCollection() != null) {
//                        multipleTypeListCaseRefNumListType = getMultipleTypeListAfterDeletions(bulkDetails.getCaseData().getMultipleCollection(),
//                                casesToRemove, bulkRequest.getCaseDetails(), authToken);
//                    }
//                    // Add new cases
//                    bulkCasesPayload.setMultipleTypeItems(getMultipleTypeListAfterAdditions(multipleTypeListCaseRefNumListType.getMultipleTypeItemList(),
//                            casesToAdd, bulkRequest.getCaseDetails(), authToken).getMultipleTypeItemList());
//                } else {
//                    bulkCasesPayload.setMultipleTypeItems(bulkDetails.getCaseData().getMultipleCollection());
//                }
//            }
//        } catch (Exception ex) {
//            log.error("Error processing bulk update threads");
//            String cases = multipleTypeListCaseRefNumListType.getCaseRefNumberList() != null ? multipleTypeListCaseRefNumListType.getCaseRefNumberList().toString() : "[]";
//            errors.add("Cases updated: " + cases);
//        }
//        bulkCasesPayload.setErrors(errors);
//        return bulkCasesPayload;
//    }

    private List<MultipleTypeItem> getMultipleTypeListAfterAdditions(List<MultipleTypeItem> multipleTypeItemListFinal, List<SubmitEvent> casesToAdd,
                                                                     BulkDetails bulkDetails, String authToken) {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        for (SubmitEvent submitEvent : casesToAdd) {
            MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
            multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
            MultipleType multipleType = BulkHelper.getMultipleTypeFromSubmitEvent(submitEvent);
            multipleType.setMultipleReferenceM(bulkDetails.getCaseData().getMultipleReference());
            multipleTypeItem.setValue(multipleType);
            multipleTypeItemListFinal.add(multipleTypeItem);
            executor.execute(new BulkCreationTask(bulkDetails, submitEvent, authToken,
                    bulkDetails.getCaseData().getMultipleReference(), "Multiple", ccdClient));
        }
        executor.shutdown();
        return multipleTypeItemListFinal;
    }

    private List<MultipleTypeItem> getMultipleTypeListAfterDeletions(List<MultipleTypeItem> multipleTypeItemList, List<SubmitEvent> casesToRemove,
                                                                                 BulkDetails bulkDetails, String authToken, String leadId) {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        List<MultipleTypeItem> multipleTypeItemListAux = new ArrayList<>();
        for (MultipleTypeItem multipleTypeItem : multipleTypeItemList) {
            boolean found = false;
            SubmitEvent eventToDelete = new SubmitEvent();
            for (SubmitEvent submitEvent : casesToRemove) {
                if (multipleTypeItem.getValue().getEthosCaseReferenceM().equals(submitEvent.getCaseData().getEthosCaseReference())) {
                    found = true;
                    eventToDelete = submitEvent;
                    break;
                }
            }
            if (!found) {
                if (multipleTypeItem.getValue().getEthosCaseReferenceM().equals(leadId)) {
                    multipleTypeItem.getValue().setLeadClaimantM("Yes");
                }
                multipleTypeItemListAux.add(multipleTypeItem);
            } else {
                executor.execute(new BulkCreationTask(bulkDetails, eventToDelete, authToken, " ", "Single", ccdClient));
            }
        }
        executor.shutdown();
        return multipleTypeItemListAux;
    }

    public BulkRequestPayload updateLeadCase(BulkRequestPayload bulkRequestPayload, String authToken) {
        if (bulkRequestPayload.getErrors().isEmpty()) {
            List<MultipleTypeItem> multipleTypeItemList = bulkRequestPayload.getBulkDetails().getCaseData().getMultipleCollection();
            List<MultipleTypeItem> multipleTypeItemListAux = new ArrayList<>();
            String leadId = BulkHelper.getLeadId(bulkRequestPayload.getBulkDetails());
            if (multipleTypeItemList != null && !multipleTypeItemList.isEmpty() && !leadId.equals("")) {
                for (MultipleTypeItem multipleTypeItem : multipleTypeItemList) {
                    if (multipleTypeItem.getValue().getEthosCaseReferenceM().equals(leadId)) {
                        multipleTypeItem.getValue().setLeadClaimantM("Yes");
                        multipleTypeItemListAux.add(0, multipleTypeItem);
                        BulkDetails bulkDetails = bulkRequestPayload.getBulkDetails();
                        try {
                            String caseId = multipleTypeItem.getValue().getCaseIDM();
                            log.info("Assigning lead: " + caseId);
                            SubmitEvent submitEvent = ccdClient.retrieveCase(authToken,
                                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(),
                                    multipleTypeItem.getValue().getCaseIDM());
                            submitEvent.getCaseData().setLeadClaimant("Yes");
                            CCDRequest returnedRequest = getReturnedRequestCheckingStateForLead(bulkDetails, submitEvent, authToken, caseId);
                            ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(),
                                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), returnedRequest, caseId);
                        } catch (IOException ex) {
                            throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
                        }

                    } else {
                        multipleTypeItem.getValue().setLeadClaimantM("No");
                        multipleTypeItemListAux.add(multipleTypeItem);
                    }
                }
                bulkRequestPayload.getBulkDetails().getCaseData().setMultipleCollection(multipleTypeItemListAux);
            }
        }
        return bulkRequestPayload;
    }

    private CCDRequest getReturnedRequestCheckingStateForLead(BulkDetails bulkDetails, SubmitEvent submitEvent, String authToken, String caseId) throws IOException {
        if (submitEvent.getState().equals(PENDING_STATE) ||
                (submitEvent.getCaseData().getStateAPI() != null && submitEvent.getCaseData().getStateAPI().equals(PENDING_STATE)) ) {
            return ccdClient.startEventForCasePreAcceptBulkSingle(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                    bulkDetails.getJurisdiction(), caseId);
        } else {
            return ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
        }
    }

}
