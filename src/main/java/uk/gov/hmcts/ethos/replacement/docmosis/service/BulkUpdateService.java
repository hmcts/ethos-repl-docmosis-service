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
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.JurCodesType;
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
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

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
                        log.info("Adding to the new bulk");
                        String bulkCaseId = String.valueOf(submitBulkEvent.getCaseId());
                        CCDRequest returnedRequest = ccdClient.startBulkEventForCase(userToken, bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction(), bulkCaseId);
                        ccdClient.submitBulkEventForCase(userToken, submitBulkEvent.getCaseData(), bulkDetails.getCaseTypeId(), bulkDetails.getJurisdiction(), returnedRequest, bulkCaseId);
                    }
                } catch (IOException ex) {
                    throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
                }

                // 4) Refresh multiple collection for bulk getting elements from caseIdCollection
                log.info("Refreshing multiple type list");
                BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(bulkDetails, userToken);
                List<MultipleTypeItem> multipleTypeItemList = BulkHelper.getMultipleTypeListBySubmitEventList(
                        bulkCasesPayload.getSubmitEvents(),
                        bulkDetails.getCaseData().getMultipleReference());

                // 5) If still cases in the multiples then update with bulk update specific (flags...)
                if (!multipleTypeItemList.isEmpty()) {
                    multipleTypeItemList = performOtherMultipleUpdate(bulkDetails.getCaseData(), multipleTypeItemList, searchTypeItemList);
                }
                bulkRequestPayload.setBulkDetails(BulkHelper.setMultipleCollection(bulkDetails, multipleTypeItemList));

                // 6) Clear the search collection from the bulk
                //bulkRequestPayload.setBulkDetails(BulkHelper.clearSearchCollection(bulkDetails));
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
        bulkData.setFlag1Update(null);
        bulkData.setFlag2Update(null);
        bulkData.setEQPUpdate(null);
        bulkData.setOutcomeUpdate(null);
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
        try {
            String caseId = String.valueOf(submitEvent.getCaseId());
            CCDRequest returnedRequest;
            log.info("Current state ---> " + submitEvent.getState());
            if (submitEvent.getState().equals(PENDING_STATE)) {
                // Moving to submitted_state
                log.info("Moving from pending to submitted");
                returnedRequest = ccdClient.startEventForCaseBulkSingle(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
                submitEvent.getCaseData().setState(SUBMITTED_STATE);
            } else {
                // Moving to accepted_state
                log.info("Moving to accepted state");
                returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            }
            submitEvent.getCaseData().setLeadClaimant("No");
            submitEvent.getCaseData().setMultipleReference(multipleRef);
            submitEvent.getCaseData().setCaseType(caseType);

            ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), returnedRequest, caseId);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + submitEvent.getCaseId() + ex.getMessage());
        }
    }

    SubmitBulkEvent caseUpdateFieldsRequest(BulkDetails bulkDetails, SearchTypeItem searchTypeItem, String authToken, SubmitBulkEvent submitBulkEvent) {
        try {
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
            log.info("Empty JurCodes???");
            String jurCodeSelected = bulkData.getJurCodesDynamicList().getValue().getCode();
            String outcomeNewValue = bulkData.getOutcomeUpdate();
            log.info("JurCodes fine");
            log.info("AuthToken: " + authToken);
            log.info("bulkDetails.getCaseTypeId(): " + bulkDetails.getCaseTypeId());
            log.info("CaseTypeId: " + BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()));
            log.info("JUR: " + bulkDetails.getJurisdiction());
            log.info("CaseID: " + caseId);
            SubmitEvent submitEvent = ccdClient.retrieveCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
            log.info("Retrieve case passed");
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
            if (updated) {
                boolean isThisCaseLead = false;
                // If multipleReference was updated then add the new values to the bulk case
                log.info("Coming to update");
                if (multipleReferenceUpdated) {
                    BulkData bulkData1 = submitBulkEvent.getCaseData();
                    MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
                    multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
                    multipleTypeItem.setValue(BulkHelper.getMultipleTypeFromSubmitEvent(submitEvent));
                    if (bulkData1.getMultipleCollection() != null) {
                        multipleTypeItem.getValue().setLeadClaimantM("No");
                        bulkData1.getMultipleCollection().add(multipleTypeItem);
                    } else {
                        isThisCaseLead = true;
                        multipleTypeItem.getValue().setLeadClaimantM("Yes");
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
                    submitEvent.getCaseData().setLeadClaimant("Yes");
                } else {
                    submitEvent.getCaseData().setLeadClaimant("No");
                }
                log.info("Ready to startEvent");
                CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), caseId);
                ccdClient.submitEventForCase(authToken, submitEvent.getCaseData(), BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), bulkDetails.getJurisdiction(), returnedRequest, caseId);
            } else {
                log.info("No updated");
            }
            log.info("End fine");
            return submitBulkEvent;
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + searchTypeItem.getId() + ex.getMessage());
        }
    }

    private List<MultipleTypeItem> performOtherMultipleUpdate(BulkData bulkData, List<MultipleTypeItem> multipleTypeItemList, List<SearchTypeItem> searchTypeItemList) {
        List<MultipleTypeItem> multipleTypeItemsAux = new ArrayList<>();
        List<String> refNumbersFromSearchList = searchTypeItemList.stream()
                .map(searchTypeItem -> searchTypeItem.getValue().getEthosCaseReferenceS())
                .collect(Collectors.toList());
        String subMultipleRefNewValue = bulkData.getSubMultipleDynamicList() != null ? bulkData.getSubMultipleDynamicList().getValue().getCode() : "";
        String subMultipleTitleNewValue = bulkData.getSubMultipleDynamicList() != null ? bulkData.getSubMultipleDynamicList().getValue().getLabel() : "";
        for (MultipleTypeItem multipleTypeItem : multipleTypeItemList) {
            log.info("Adding FLAGS to case");
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
}
