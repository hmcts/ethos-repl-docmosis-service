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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.PENDING_STATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.SUBMITTED_STATE;

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
        if (bulkCasesPayload.getAlreadyTakenIds() != null) {
            if (bulkCasesPayload.getAlreadyTakenIds().isEmpty()) {
                // 1) Retrieve cases by ethos reference
                List<SubmitEvent> submitEvents = bulkCasesPayload.getSubmitEvents();
                //submitEvents.forEach(submitEvent -> log.info("CASE RECEIVED FOR CREATION: " + submitEvent));

                // 2) Add list of cases to the multiple bulk case collection
                if (!submitEvents.isEmpty()) {
                    String multipleReference = bulkDetails.getCaseData().getMultipleReference();
                    List<MultipleTypeItem> multipleTypeItemList = BulkHelper.getMultipleTypeListBySubmitEventList(submitEvents,
                            multipleReference);
                    bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, multipleTypeItemList));
                } else {
                    bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, bulkDetails.getCaseData().getMultipleCollection()));
                }

                // 3) Create an event to update multiple reference field to all cases
                for (SubmitEvent submitEvent : submitEvents) {
                    if (!submitEvent.getState().equals(SUBMITTED_STATE)) {
                        bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkDetails, submitEvent, userToken,
                                bulkDetails.getCaseData().getMultipleReference(), "Multiple");
                    } else {
                        errors.add("The state of case id: " + submitEvent.getCaseData().getEthosCaseReference() + " has not been accepted");
                        bulkRequestPayload.setErrors(errors);
                        bulkRequestPayload.setBulkDetails(bulkDetails);
                        return bulkRequestPayload;
                    }
                }
            } else {
                errors.add("These cases are already assigned to a multiple case: " + bulkCasesPayload.getAlreadyTakenIds().toString());
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

        List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
        List<String> multipleCaseIds = BulkHelper.getMultipleCaseIds(bulkDetails);
        try {
            List<String> unionLists = Stream.concat(caseIds.stream(), multipleCaseIds.stream()).distinct().collect(Collectors.toList());
            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken,
                    BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction());
            BulkCasesPayload bulkCasesPayload = bulkSearchService.filterSubmitEvents(submitEvents, unionLists,
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
                            List<String> errors = new ArrayList<>();
                            errors.add("The state of case id: " + submitEvent.getCaseData().getEthosCaseReference() + " has not been accepted");
                            bulkCasesPayload.setErrors(errors);
                            return bulkCasesPayload;
                        }
                    }
                    List<MultipleTypeItem> multipleTypeItemListFinal = new ArrayList<>();
                    // Delete old cases
                    if (bulkDetails.getCaseData().getMultipleCollection() != null) {
                        multipleTypeItemListFinal = getMultipleTypeListAfterDeletions(bulkDetails.getCaseData().getMultipleCollection(),
                                casesToRemove, bulkRequest.getCaseDetails(), authToken);
                    }
                    // Add new cases
                    bulkCasesPayload.setMultipleTypeItems(getMultipleTypeListAfterAdditions(multipleTypeItemListFinal, casesToAdd,
                            bulkRequest.getCaseDetails(), authToken));
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
        for (SubmitEvent submitEvent : casesToAdd) {
            MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
            multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
            MultipleType multipleType = BulkHelper.getMultipleTypeFromSubmitEvent(submitEvent);
            multipleType.setMultipleReferenceM(bulkDetails.getCaseData().getMultipleReference());
            multipleTypeItem.setValue(multipleType);
            multipleTypeItemListFinal.add(multipleTypeItem);
            bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkDetails, submitEvent, authToken,
                    bulkDetails.getCaseData().getMultipleReference(), "Multiple");
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
                multipleTypeItemListAux.add(multipleTypeItem);
            } else {
                bulkUpdateService.caseUpdateMultipleReferenceRequest(bulkDetails, eventToDelete, authToken, " ", "Single");
            }
        }
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
