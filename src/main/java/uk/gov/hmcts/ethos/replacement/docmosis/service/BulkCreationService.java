package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.MultipleTypeListCaseRefNumListType;
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

    public BulkRequestPayload bulkCreationLogic(BulkDetails bulkDetails, BulkCasesPayload bulkCasesPayload, String userToken) {
        List<String> errors = new ArrayList<>();
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        if (bulkCasesPayload.getErrors().isEmpty()) {
            // 1) Retrieve cases by ethos reference
            List<SubmitEvent> submitEvents = bulkCasesPayload.getSubmitEvents();
            // 2) Add list of cases to the multiple bulk case collection
            if (!submitEvents.isEmpty()) {
                List<MultipleTypeItem> multipleTypeItemList = BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents,
                        bulkDetails.getCaseData().getMultipleReference());
                bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, multipleTypeItemList));
            } else {
                bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, bulkDetails.getCaseData().getMultipleCollection()));
            }
            // 3) Create an event to update multiple reference field to all cases
            Instant start = Instant.now();
            ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
            List<Future<String>> bulkUpdateTaskList = new ArrayList<>();
            List<String> ethosCaseReferenceNumbers = new ArrayList<>();
            try {
                for (SubmitEvent submitEvent : submitEvents) {
                    Future<String> submit = executor.submit(new BulkCreationTask(bulkDetails, submitEvent, userToken,
                            bulkDetails.getCaseData().getMultipleReference(), "Multiple", ccdClient));
                    bulkUpdateTaskList.add(submit);
                }
                ethosCaseReferenceNumbers = BulkHelper.waitThreadsToFinish(bulkUpdateTaskList, executor);
                log.info("End in time: " + Duration.between(start, Instant.now()).toMillis());
            } catch (Exception e) {
                log.error("Error processing bulk update threads");
                errors.add("Cases updated: " + ethosCaseReferenceNumbers);
            }
        }
        if (bulkRequestPayload.getBulkDetails() == null) {
            bulkRequestPayload.setBulkDetails(bulkDetails);
        }
        bulkRequestPayload.setErrors(errors);
        return bulkRequestPayload;
    }

    public BulkRequestPayload bulkUpdateCaseIdsLogic(BulkRequest bulkRequest, String authToken) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        BulkCasesPayload bulkCasesPayload = updateBulkRequest(bulkRequest, authToken);
        List<String> errors = bulkCasesPayload.getErrors() != null ? bulkCasesPayload.getErrors() : new ArrayList<>();
        if (bulkCasesPayload.getAlreadyTakenIds() != null && errors.isEmpty()) {
            if (bulkCasesPayload.getAlreadyTakenIds().isEmpty()) {
                bulkRequest.setCaseDetails(BulkHelper.setMultipleCollection(bulkRequest.getCaseDetails(), bulkCasesPayload.getMultipleTypeItems()));
                bulkRequest.setCaseDetails(BulkHelper.clearSearchCollection(bulkRequest.getCaseDetails()));
            } else {
                errors.add("These cases are already assigned to a multiple case: " + bulkCasesPayload.getAlreadyTakenIds().toString());
            }
        }
        bulkRequestPayload.setErrors(errors);
        bulkRequestPayload.setBulkDetails(bulkRequest.getCaseDetails());
        return bulkRequestPayload;
    }

    BulkCasesPayload updateBulkRequest(BulkRequest bulkRequest, String authToken) {
        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
        BulkCasesPayload bulkCasesPayload = new BulkCasesPayload();
        List<String> errors = new ArrayList<>();
        MultipleTypeListCaseRefNumListType multipleTypeListCaseRefNumListType = new MultipleTypeListCaseRefNumListType();
        List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
        List<String> multipleCaseIds = BulkHelper.getMultipleCaseIds(bulkDetails);
        try {
            List<String> unionLists = Stream.concat(caseIds.stream(), multipleCaseIds.stream()).distinct().collect(Collectors.toList());
            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken,
                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction());
            bulkCasesPayload = bulkSearchService.filterSubmitEvents(submitEvents, unionLists,
                    bulkDetails.getCaseData().getMultipleReference(), false);
            if (bulkCasesPayload.getAlreadyTakenIds() != null && bulkCasesPayload.getAlreadyTakenIds().isEmpty()) {
                List<SubmitEvent> allSubmitEventsToUpdate = bulkCasesPayload.getSubmitEvents();
                if (!allSubmitEventsToUpdate.isEmpty()) {
                    List<SubmitEvent> casesToAdd = new ArrayList<>();
                    List<SubmitEvent> casesToRemove = new ArrayList<>();
                    for (SubmitEvent submitEvent : allSubmitEventsToUpdate) {
                        log.info("State SubmitEvent: " + submitEvent.getState());
                        if (!submitEvent.getState().equals(SUBMITTED_STATE)) {
                            log.info("Case submitted");
                            String ethosCaseRef = submitEvent.getCaseData().getEthosCaseReference();
                            if (caseIds.contains(ethosCaseRef) && !multipleCaseIds.contains(ethosCaseRef)) {
                                casesToAdd.add(submitEvent);
                            } else if (!caseIds.contains(ethosCaseRef) && multipleCaseIds.contains(ethosCaseRef)) {
                                casesToRemove.add(submitEvent);
                            }
                        } else {
                            errors.add("The state of case id: " + submitEvent.getCaseData().getEthosCaseReference() + " has not been accepted");
                            bulkCasesPayload.setErrors(errors);
                            return bulkCasesPayload;
                        }
                    }
                    // Delete old cases
                    if (bulkDetails.getCaseData().getMultipleCollection() != null) {
                        multipleTypeListCaseRefNumListType = getMultipleTypeListAfterDeletions(bulkDetails.getCaseData().getMultipleCollection(),
                                casesToRemove, bulkRequest.getCaseDetails(), authToken);
                    }
                    // Add new cases
                    bulkCasesPayload.setMultipleTypeItems(getMultipleTypeListAfterAdditions(multipleTypeListCaseRefNumListType.getMultipleTypeItemList(),
                            casesToAdd, bulkRequest.getCaseDetails(), authToken).getMultipleTypeItemList());
                } else {
                    bulkCasesPayload.setMultipleTypeItems(bulkDetails.getCaseData().getMultipleCollection());
                }
            }
        } catch (Exception ex) {
            log.error("Error processing bulk update threads");
            String cases = multipleTypeListCaseRefNumListType.getCaseRefNumberList() != null ? multipleTypeListCaseRefNumListType.getCaseRefNumberList().toString() : "[]";
            errors.add("Cases updated: " + cases);
        }
        bulkCasesPayload.setErrors(errors);
        return bulkCasesPayload;
    }

    private MultipleTypeListCaseRefNumListType getMultipleTypeListAfterAdditions(List<MultipleTypeItem> multipleTypeItemListFinal, List<SubmitEvent> casesToAdd,
                                                                     BulkDetails bulkDetails, String authToken) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        List<Future<String>> bulkUpdateTaskList = new ArrayList<>();
        MultipleTypeListCaseRefNumListType multipleTypeListCaseRefNumListType = new MultipleTypeListCaseRefNumListType();
        for (SubmitEvent submitEvent : casesToAdd) {
            MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
            multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
            MultipleType multipleType = BulkHelper.getMultipleTypeFromSubmitEvent(submitEvent);
            multipleType.setMultipleReferenceM(bulkDetails.getCaseData().getMultipleReference());
            multipleTypeItem.setValue(multipleType);
            multipleTypeItemListFinal.add(multipleTypeItem);

            Future<String> submit = executor.submit(new BulkCreationTask(bulkDetails, submitEvent, authToken,
                    bulkDetails.getCaseData().getMultipleReference(), "Multiple", ccdClient));
            bulkUpdateTaskList.add(submit);
        }
        multipleTypeListCaseRefNumListType.setMultipleTypeItemList(multipleTypeItemListFinal);
        multipleTypeListCaseRefNumListType.setCaseRefNumberList(BulkHelper.waitThreadsToFinish(bulkUpdateTaskList, executor));
        return multipleTypeListCaseRefNumListType;
    }

    private MultipleTypeListCaseRefNumListType getMultipleTypeListAfterDeletions(List<MultipleTypeItem> multipleTypeItemList, List<SubmitEvent> casesToRemove,
                                                                                 BulkDetails bulkDetails, String authToken) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        List<Future<String>> bulkUpdateTaskList = new ArrayList<>();
        List<MultipleTypeItem> multipleTypeItemListAux = new ArrayList<>();
        MultipleTypeListCaseRefNumListType multipleTypeListCaseRefNumListType = new MultipleTypeListCaseRefNumListType();
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
                multipleTypeItemListAux.add(multipleTypeItem);
            } else {
                Future<String> submit = executor.submit(new BulkCreationTask(bulkDetails, eventToDelete, authToken, " ", "Single", ccdClient));
                bulkUpdateTaskList.add(submit);
            }
        }
        multipleTypeListCaseRefNumListType.setMultipleTypeItemList(multipleTypeItemListAux);
        multipleTypeListCaseRefNumListType.setCaseRefNumberList(BulkHelper.waitThreadsToFinish(bulkUpdateTaskList, executor));
        return multipleTypeListCaseRefNumListType;
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
