package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.MultRefComplexType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.SubmitBulkEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.CaseType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.PENDING_STATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.SUBMITTED_STATE;

@Slf4j
@Service("bulkUpdateService")
public class BulkUpdateService {

    private static final String MESSAGE = "Failed to update case for case id : ";
    private final CcdClient ccdClient;
    private final BulkSearchService bulkSearchService;

    @Autowired
    public BulkUpdateService(CcdClient ccdClient, BulkSearchService bulkSearchService) {
        this.ccdClient = ccdClient;
        this.bulkSearchService = bulkSearchService;
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
            MultRefComplexType multRefComplexType = checkMultipleReferenceExists(bulkDetails, userToken, multipleReferenceV2);
            // 2) Check if new multiple reference exists or it has the same as the current bulk
            if (!isNullOrEmpty(multipleReferenceV2)) {
                if (!multRefComplexType.isExist() || multipleReferenceV2.equals(bulkDetails.getCaseData().getMultipleReference())) {
                    errors.add("Multiple reference does not exist or it is the same as the current multiple case");
                }
            }
            log.info("multipleReferenceV2: " + multipleReferenceV2);
            if (errors.isEmpty()) {
                SubmitBulkEvent submitBulkEvent = multRefComplexType.getSubmitBulkEvent();
                // 3) Create an event to update fields to the searched cases
                for (SearchTypeItem searchTypeItem : searchTypeItemList) {
                    submitBulkEvent = caseUpdateFieldsRequest(bulkDetails, searchTypeItem, userToken, submitBulkEvent);
                    log.info("SUBMITBULKEVENT -----------" + submitBulkEvent);
                    if (!isNullOrEmpty(multipleReferenceV2)) {
                        // If multipleReference changed then from this bulk remove all cases
                        log.info("Removing case id collection");
                        bulkDetails.getCaseData().getCaseIdCollection()
                                .removeIf(id -> searchTypeItem.getValue().getEthosCaseReferenceS().equals(id.getValue().getEthosCaseReference()));
                    }
                }
                try {
                    if (!searchTypeItemList.isEmpty() && !isNullOrEmpty(multipleReferenceV2)) {
                        // And add them to the new bulk case
                        log.info("Adding TO THE NEW BULK");
                        String bulkCaseId = String.valueOf(submitBulkEvent.getCaseId());
                        CCDRequest returnedRequest = ccdClient.startBulkEventForCase(userToken, bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction(), bulkCaseId);
                        ccdClient.submitBulkEventForCase(userToken, submitBulkEvent.getCaseData(), bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction(), returnedRequest, bulkCaseId);
                    }
                } catch (IOException ex) {
                    throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
                }

                // 4) Refresh multiple collection for bulk getting elements from caseIdCollection
                log.info("Refreshing multiple type list");
                BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequest(bulkDetails, userToken);
                List<MultipleTypeItem> multipleTypeItemList = BulkHelper.getMultipleTypeListBySubmitEventList(
                        bulkCasesPayload.getSubmitEvents(),
                        bulkDetails.getCaseData().getMultipleReference());
                bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, multipleTypeItemList));

                // 5) Clear the search collection from the bulk
                bulkRequestPayload.setBulkDetails(BulkHelper.clearSearchCollection(bulkDetails));
            }
        }
        if (bulkRequestPayload.getBulkDetails() == null) {
            bulkRequestPayload.setBulkDetails(bulkDetails);
        }
        bulkRequestPayload.setErrors(errors);
        return bulkRequestPayload;
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
        bulkRequestPayload.getBulkDetails().setCaseData(bulkData);
        return bulkRequestPayload;
    }

    private MultRefComplexType checkMultipleReferenceExists(BulkDetails bulkDetails, String authToken, String multipleReference) {
        try {
            MultRefComplexType multRefComplexType = new MultRefComplexType();
            if (!isNullOrEmpty(multipleReference)) {
                List<SubmitBulkEvent> submitBulkEvents = ccdClient.retrieveBulkCases(authToken,
                        bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction());
                Optional<SubmitBulkEvent> optSubmitBulkEvent = submitBulkEvents.stream().filter(submitBulkEvent -> submitBulkEvent.getCaseData().getMultipleReference().equals(multipleReference)).findFirst();
                if (optSubmitBulkEvent.isPresent()) {
                    log.info("SubmitBulkEvent exists!!");
                    multRefComplexType.setExist(true);
                    multRefComplexType.setSubmitBulkEvent(optSubmitBulkEvent.get());
                } else {
                    multRefComplexType.setExist(false);
                }
            }
            return multRefComplexType;
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
        }
    }

    void caseUpdateMultipleReferenceRequest(BulkDetails bulkDetails, SubmitEvent submitEvent, String authToken, String multipleRef, String caseType) {
        log.info("Bulk Details: " + bulkDetails);
        try {
            String caseId = String.valueOf(submitEvent.getCaseId());
            CCDRequest returnedRequest;
            log.info("Current state ---> " + submitEvent.getState());
            if (submitEvent.getState().equals(PENDING_STATE)) {
                // Moving to submitted_state
                log.info("MOVING FROM PENDING TO SUBMITTED");
                returnedRequest = ccdClient.startEventForCaseBulkSingle(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
                submitEvent.getCaseData().setState(SUBMITTED_STATE);
            } else {
                // Moving to accepted_state
                log.info("MOVING TO ACCEPTED STATE");
                returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            }
            submitEvent.getCaseData().setLeadClaimant("No");
            submitEvent.getCaseData().setMultipleReference(multipleRef);
            submitEvent.getCaseData().setCaseType(caseType);

            ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), returnedRequest, caseId);
            log.info("------------ UPDATED REQUEST: " + submitEvent.getCaseData());
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + submitEvent.getCaseId() + ex.getMessage());
        }
    }

    SubmitBulkEvent caseUpdateFieldsRequest(BulkDetails bulkDetails, SearchTypeItem searchTypeItem, String authToken, SubmitBulkEvent submitBulkEvent) {
        try {
            String caseId = searchTypeItem.getId();
            BulkData bulkData = bulkDetails.getCaseData();
            String claimantSurnameNewValue = bulkData.getClaimantSurnameV2();
            String respondentSurnameNewValue = bulkData.getRespondentSurnameV2();
            String fileLocationNewValue = bulkData.getFileLocationV2();
            String claimantRepNewValue = bulkData.getClaimantRepV2();
            String respondentRepNewValue = bulkData.getRespondentRepV2();
            String multipleRefNewValue = bulkData.getMultipleReferenceV2();
            String clerkNewValue = bulkData.getClerkResponsibleV2();
            String positionTypeNewValue = bulkData.getPositionTypeV2();
            SubmitEvent submitEvent = ccdClient.retrieveCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            boolean updated = false;
            boolean multipleReferenceUpdated = false;
            if (!isNullOrEmpty(claimantSurnameNewValue) ) {
                updated = true;
                ClaimantIndType claimantIndType;
                if (submitEvent.getCaseData().getClaimantIndType() != null) {
                    claimantIndType = submitEvent.getCaseData().getClaimantIndType();
                    claimantIndType.setClaimantLastName(claimantSurnameNewValue);
                } else {
                    claimantIndType = new ClaimantIndType();
                    claimantIndType.setClaimantLastName(claimantSurnameNewValue);
                }
                submitEvent.getCaseData().setClaimantIndType(claimantIndType);
            }
            if (!isNullOrEmpty(respondentSurnameNewValue)) {
                updated = true;
                if (submitEvent.getCaseData().getRespondentCollection()!=null && !submitEvent.getCaseData().getRespondentCollection().isEmpty()) {
                    RespondentSumTypeItem respondentSumTypeItem = submitEvent.getCaseData().getRespondentCollection().get(0);
                    respondentSumTypeItem.getValue().setRespondentName(respondentSurnameNewValue);
                    submitEvent.getCaseData().getRespondentCollection().add(respondentSumTypeItem);
                } else {
                    List<RespondentSumTypeItem> respondentSumTypeItems = new ArrayList<>();
                    RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
                    RespondentSumType respondentSumType = new RespondentSumType();
                    respondentSumType.setRespondentName(respondentSurnameNewValue);
                    respondentSumTypeItem.setValue(respondentSumType);
                    respondentSumTypeItems.add(respondentSumTypeItem);
                    submitEvent.getCaseData().setRespondentCollection(respondentSumTypeItems);
                }
            }
            if (!isNullOrEmpty(fileLocationNewValue)) {
                updated = true;
                submitEvent.getCaseData().setFileLocation(fileLocationNewValue);
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
                    submitEvent.getCaseData().setClaimantRepresentedQuestion("Yes");
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
                    submitEvent.getCaseData().getRepCollection().add(0, representedTypeRItem);
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
            if (updated) {
                boolean isThisCaseLead = false;
                // If multipleReference was updated then add the new values to the bulk case
                if (multipleReferenceUpdated) {
                    BulkData bulkData1 = submitBulkEvent.getCaseData();
                    MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
                    multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
                    multipleTypeItem.setValue(BulkHelper.getMultipleTypeFromSubmitEvent(submitEvent));
                    log.info("MULTIPLE COLLECTION: " + bulkData1.getMultipleCollection());
                    if (bulkData1.getMultipleCollection() != null) {
                        multipleTypeItem.getValue().setLeadClaimantM("No");
                        bulkData1.getMultipleCollection().add(multipleTypeItem);
                    } else {
                        log.info("REALLY???");
                        isThisCaseLead = true;
                        multipleTypeItem.getValue().setLeadClaimantM("Yes");
                        bulkData1.setMultipleCollection(new ArrayList<>(Collections.singletonList(multipleTypeItem)));
                    }
                    CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
                    caseIdTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
                    CaseType caseType = new CaseType();
                    caseType.setEthosCaseReference(submitEvent.getCaseData().getEthosCaseReference());
                    caseIdTypeItem.setValue(caseType);
                    log.info("CASEID COLLECTION: " + bulkData1.getCaseIdCollection());
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
                log.info("Update the case with the new event details");
                if (isThisCaseLead) {
                    submitEvent.getCaseData().setLeadClaimant("Yes");
                } else {
                    submitEvent.getCaseData().setLeadClaimant("No");
                }
                CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
                ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), returnedRequest, caseId);
            }
            return submitBulkEvent;
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + searchTypeItem.getId() + ex.getMessage());
        }
    }
}
