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
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("bulkCreationService")
public class BulkCreationService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private final CcdClient ccdClient;
    private final BulkUpdateService bulkUpdateService;
    private final BulkSearchService bulkSearchService;

    @Autowired
    public BulkCreationService(CcdClient ccdClient, BulkUpdateService bulkUpdateService,
                               BulkSearchService bulkSearchService) {
        this.ccdClient = ccdClient;
        this.bulkUpdateService = bulkUpdateService;
        this.bulkSearchService = bulkSearchService;
    }

    public BulkRequestPayload bulkCreationLogic(BulkDetails bulkDetails, BulkCasesPayload bulkCasesPayload, String userToken) {
        List<String> errors = new ArrayList<>();
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        if (bulkCasesPayload.getDuplicateIds() != null) {
            if (bulkCasesPayload.getDuplicateIds().isEmpty()) {
                // 1) Retrieve cases by ethos reference
                List<SubmitEvent> submitEvents = bulkCasesPayload.getSubmitEvents();
                submitEvents.forEach(submitEvent -> log.info("CASE RECEIVED FOR CREATION: " + submitEvent));

                // 2) Add list of cases to the multiple bulk case collection
                String leadId = BulkHelper.getLeadId(bulkDetails);
                if (!submitEvents.isEmpty()) {
                    String multipleReference = bulkDetails.getCaseData().getMultipleReference();
                    List<MultipleTypeItem> multipleTypeItemList = BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents,
                            multipleReference, leadId);
                    bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, multipleTypeItemList));
                }

                // 3) Create an event to update multiple reference field to all cases
                for (SubmitEvent submitEvent : submitEvents) {
                    bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkDetails, submitEvent, userToken,
                            bulkDetails.getCaseData().getMultipleReference(), "Multiple", leadId);
                }
            } else {
                errors.add("These cases are already assigned to a multiple bulk: " + bulkCasesPayload.getDuplicateIds().toString());
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
        List<String> errors = new ArrayList<>();
        if (bulkCasesPayload.getDuplicateIds() != null) {
            if (bulkCasesPayload.getDuplicateIds().isEmpty()) {
                //Check lead here
                bulkRequest.setCaseDetails(BulkHelper.setMultipleCollection(bulkRequest.getCaseDetails(), bulkCasesPayload.getMultipleTypeItems()));
                bulkRequest.setCaseDetails(BulkHelper.clearSearchCollection(bulkRequest.getCaseDetails()));
            } else {
                errors.add("These cases are already assigned to a multiple bulk: " + bulkCasesPayload.getDuplicateIds().toString());
            }
        }
        bulkRequestPayload.setErrors(errors);
        bulkRequestPayload.setBulkDetails(bulkRequest.getCaseDetails());
        return bulkRequestPayload;
    }

    BulkCasesPayload updateBulkRequest(BulkRequest bulkRequest, String authToken) {
        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
        log.info("Bulk EventId: " + bulkRequest.getEventId());
        log.info("Bulk Details: " + bulkDetails);

        List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
        List<String> multipleCaseIds = BulkHelper.getMultipleCaseIds(bulkDetails);
        try {
            List<String> unionLists = Stream.concat(caseIds.stream(), multipleCaseIds.stream()).distinct().collect(Collectors.toList());
            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken,
                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction());
            BulkCasesPayload bulkCasesPayload = bulkSearchService.filterSubmitEvents(submitEvents, unionLists,
                    bulkDetails.getCaseData().getMultipleReference());
            if (bulkCasesPayload.getDuplicateIds() != null && bulkCasesPayload.getDuplicateIds().isEmpty()) {
                List<SubmitEvent> allSubmitEventsToUpdate = bulkCasesPayload.getSubmitEvents();
                if (!allSubmitEventsToUpdate.isEmpty()) {
                    List<SubmitEvent> casesToAdd = new ArrayList<>();
                    List<SubmitEvent> casesToRemove = new ArrayList<>();
                    for (SubmitEvent submitEvent : allSubmitEventsToUpdate) {
                        String ethosCaseRef = submitEvent.getCaseData().getEthosCaseReference();
                        if (caseIds.contains(ethosCaseRef) && !multipleCaseIds.contains(ethosCaseRef)) {
                            casesToAdd.add(submitEvent);
                        } else if (!caseIds.contains(ethosCaseRef) && multipleCaseIds.contains(ethosCaseRef)) {
                            casesToRemove.add(submitEvent);
                        }
                    }
                    List<MultipleTypeItem> multipleTypeItemListFinal = new ArrayList<>();
                    // Delete old cases
                    if (bulkDetails.getCaseData().getMultipleCollection() != null) {
                        multipleTypeItemListFinal = getMultipleTypeListAfterDeletions(bulkDetails.getCaseData().getMultipleCollection(),
                                casesToRemove, bulkRequest.getCaseDetails(), authToken);
                    }
                    // Add new cases
                    bulkCasesPayload.setMultipleTypeItems(getMultipleTypeListAfterAdditions(multipleTypeItemListFinal, casesToAdd, bulkRequest.getCaseDetails(), authToken));
                } else {
                    bulkCasesPayload.setMultipleTypeItems(bulkDetails.getCaseData().getMultipleCollection());
                }
            }
            return bulkCasesPayload;
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
        }
    }

    private List<MultipleTypeItem> getMultipleTypeListAfterAdditions(List<MultipleTypeItem> multipleTypeItemListFinal, List<SubmitEvent> casesToAdd,
                                                                     BulkDetails bulkDetails, String authToken) {
        String leadId = BulkHelper.getLeadId(bulkDetails);
        for (SubmitEvent submitEvent : casesToAdd) {
            MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
            multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
            MultipleType multipleType = BulkHelper.getMultipleTypeFromSubmitEvent(submitEvent, leadId);
            multipleType.setMultipleReferenceM(bulkDetails.getCaseData().getMultipleReference());
            multipleTypeItem.setValue(multipleType);
            if (submitEvent.getCaseData().getEthosCaseReference().equals(leadId) && !multipleTypeItem.getValue().getLeadClaimantM().trim().isEmpty()) {
                multipleTypeItemListFinal.add(0, multipleTypeItem);
            } else {
                multipleTypeItemListFinal.add(multipleTypeItem);
            }
            bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkDetails, submitEvent, authToken, bulkDetails.getCaseData().getMultipleReference(), "Multiple", leadId);
        }
        return multipleTypeItemListFinal;
    }

    private List<MultipleTypeItem> getMultipleTypeListAfterDeletions(List<MultipleTypeItem> multipleTypeItemList, List<SubmitEvent> casesToRemove,
                                                                     BulkDetails bulkDetails, String authToken) {
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
                if (multipleTypeItem.getValue().getLeadClaimantM() != null && !multipleTypeItem.getValue().getLeadClaimantM().trim().isEmpty()) {
                    multipleTypeItemListAux.add(0, multipleTypeItem);
                } else {
                    multipleTypeItemListAux.add(multipleTypeItem);
                }
            } else {
                bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkDetails, eventToDelete, authToken, " ", "Single", "No Lead");
            }
        }
        return multipleTypeItemListAux;
    }

}
