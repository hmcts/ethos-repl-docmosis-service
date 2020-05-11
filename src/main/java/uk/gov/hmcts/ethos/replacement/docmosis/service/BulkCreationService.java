package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ecm.common.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.tasks.BulkCreationTask;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("bulkCreationService")
public class BulkCreationService {

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
                    List<MultipleTypeItem> multipleTypeItemList = BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents,
                            bulkDetails.getCaseData().getMultipleReference());
                    bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, multipleTypeItemList));
                } else {
                    bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, bulkDetails.getCaseData().getMultipleCollection()));
                }
            } else {
                // 4) Create an event to update multiple reference field to all cases
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
                    bulkDetails.getCaseData().getMultipleReference(), MULTIPLE_CASE_TYPE, ccdClient));
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
        bulkRequest.getCaseDetails().getCaseData().setFilterCases(null);
        bulkRequestPayload.setBulkDetails(bulkRequest.getCaseDetails());
        return bulkRequestPayload;
    }

    BulkCasesPayload updateBulkRequest(BulkRequest bulkRequest, String authToken) {
        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
        BulkCasesPayload bulkCasesPayload = new BulkCasesPayload();
        List<String> errors = new ArrayList<>();
        List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
        List<String> multipleCaseIds = BulkHelper.getMultipleCaseIds(bulkDetails);
        try {
            List<String> unionLists = Stream.concat(caseIds.stream(), multipleCaseIds.stream())
                    .distinct().collect(Collectors.toList());
            bulkCasesPayload = bulkSearchService.filterSubmitEventsElasticSearch(
                    ccdClient.retrieveCasesElasticSearch(authToken, UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), unionLists),
                    bulkDetails.getCaseData().getMultipleReference(), false, bulkDetails);
//            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken,
//                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction());
//            bulkCasesPayload = bulkSearchService.filterSubmitEvents(submitEvents, unionLists,
//                    bulkDetails.getCaseData().getMultipleReference(), false, true);
            if (bulkCasesPayload.getErrors().isEmpty()) {
                List<SubmitEvent> allSubmitEventsToUpdate = bulkCasesPayload.getSubmitEvents();
                if (!allSubmitEventsToUpdate.isEmpty()) {
                    List<SubmitEvent> submitEventsWithLead = BulkHelper.calculateLeadCase(allSubmitEventsToUpdate, caseIds);
                    bulkCasesPayload.setMultipleTypeItems(addRemoveNewCases(submitEventsWithLead,
                            caseIds, multipleCaseIds, bulkDetails, authToken));
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

    private List<MultipleTypeItem> addRemoveNewCases(List<SubmitEvent> allSubmitEventsWithLead, List<String> caseIds, List<String> multipleCaseIds,
                                                     BulkDetails bulkDetails, String authToken) {
        List<MultipleTypeItem> multipleTypeItemList = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        String leadId = allSubmitEventsWithLead.get(0).getCaseData().getEthosCaseReference();
        for (SubmitEvent submitEvent : allSubmitEventsWithLead) {
            String ethosCaseRef = submitEvent.getCaseData().getEthosCaseReference();
            if (!submitEvent.getCaseData().getEthosCaseReference().equals(leadId)) {
                submitEvent.getCaseData().setLeadClaimant(NO);
            }
            log.info("State SubmitEvent: " + submitEvent.getState());
            if (caseIds.contains(ethosCaseRef) && !multipleCaseIds.contains(ethosCaseRef)) {
                multipleTypeItemList.add(BulkHelper.getMultipleTypeItemFromSubmitEvent(submitEvent, bulkDetails.getCaseData().getMultipleReference()));
                executor.execute(new BulkCreationTask(bulkDetails, submitEvent, authToken,
                        bulkDetails.getCaseData().getMultipleReference(), MULTIPLE_CASE_TYPE, ccdClient));
            } else if (!caseIds.contains(ethosCaseRef) && multipleCaseIds.contains(ethosCaseRef)) {
                executor.execute(new BulkCreationTask(bulkDetails, submitEvent, authToken, " ", SINGLE_CASE_TYPE, ccdClient));
            } else {
                multipleTypeItemList.add(BulkHelper.getMultipleTypeItemFromSubmitEvent(submitEvent, bulkDetails.getCaseData().getMultipleReference()));
                executor.execute(new BulkCreationTask(bulkDetails, submitEvent, authToken,
                        bulkDetails.getCaseData().getMultipleReference(), MULTIPLE_CASE_TYPE, ccdClient));
            }
        }
        executor.shutdown();
        return multipleTypeItemList;
    }

}
