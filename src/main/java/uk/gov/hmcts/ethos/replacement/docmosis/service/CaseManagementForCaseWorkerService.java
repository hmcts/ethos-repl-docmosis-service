package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABOUT_TO_SUBMIT_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.COMPANY_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MID_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("caseManagementForCaseWorkerService")
public class CaseManagementForCaseWorkerService {

    private static final String EDIT_HEARING = "Edit hearing";
    private static final String DELETE_HEARING = "Delete hearing";
    private static final String EDIT_HEARING_DATE = "Edit hearing date";
    private static final String DELETE_HEARING_DATE = "Delete hearing date";
    private static final String ADD_NEW_HEARING_DATE = "Add a new hearing date";

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    private final CcdClient ccdClient;
    private static final String MESSAGE = "Failed to link ECC case for case id : ";
    private static final String JURISDICTION_CODE_ECC = "BOC";
    private static final String EMPLOYER_CONTRACT_CLAIM_CODE = "ECC";

    @Autowired
    public CaseManagementForCaseWorkerService(CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService, CcdClient ccdClient) {
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.ccdClient = ccdClient;
    }

    public void struckOutDefaults(CaseData caseData) {
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();
            while (itr.hasNext()) {
                itr.next().getValue().setResponseStruckOut(NO);
            }
        }
    }

    public CaseData preAcceptCase(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);
        if (caseData.getPreAcceptCase() != null) {
            if (caseData.getPreAcceptCase().getCaseAccepted().equals(YES)) {
                log.info("Accepting preAcceptCase");
                caseData.setState(ACCEPTED_STATE);
            } else {
                caseData.setState(REJECTED_STATE);
            }
        }
        return caseData;
    }

    public CaseData addNewHearingItem(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);

        List<HearingTypeItem> hearingCollection = (caseData.getHearingCollection() != null ? caseData.getHearingCollection() : new ArrayList<>());
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = createNewHearingItem(caseData);
        hearingTypeItem.setValue(hearingType);
        hearingCollection.add(hearingTypeItem);
        caseData.setHearingCollection(hearingCollection);
        clearTempHearingItem(caseData);
        populateHearingSelectionItems(caseData);

        return caseData;
    }

    public CaseData fetchHearingItemData(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);
        if(caseData.getHearingActions().equals(EDIT_HEARING)) {
            if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
                String hearingSelectionChoice = caseData.getHearingSelection().getValue().getCode();
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    HearingType hearingType = hearingTypeItem.getValue();
                    if (hearingType.getHearingNumber() != null && hearingType.getHearingNumber().equals(hearingSelectionChoice)) {
                        populateTempHearingFields(caseData, hearingType);
                        break;
                    }
                }
            }
        }

        return caseData;
    }

    public CaseData amendHearingItemDetails(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);

        // working progress ...

        return caseData;
    }

    public CaseData struckOutRespondents(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);

        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {

            List<RespondentSumTypeItem> activeRespondent = new ArrayList<>();
            List<RespondentSumTypeItem> struckRespondent = new ArrayList<>();

            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();

            while (itr.hasNext()) {

                RespondentSumTypeItem respondentSumTypeItem = itr.next();
                RespondentSumType respondentSumType = respondentSumTypeItem.getValue();

                if (respondentSumType.getResponseStruckOut() != null) {
                    if (respondentSumType.getResponseStruckOut().equals(YES)) {
                        struckRespondent.add(respondentSumTypeItem);
                    }
                    else {
                        activeRespondent.add(respondentSumTypeItem);
                    }
                }
                else{
                    respondentSumType.setResponseStruckOut(NO);
                    activeRespondent.add(respondentSumTypeItem);
                }
            }

            caseData.setRespondentCollection(Stream.concat(activeRespondent.stream(), struckRespondent.stream()).collect(Collectors.toList()));
        }

        return caseData;
    }

    private HearingType createNewHearingItem(CaseData caseData) {
        HearingType hearingType = new HearingType();

        List<DateListedTypeItem> hearingDateCollection = new ArrayList<>() ;
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(caseData.getListedDate());
        dateListedTypeItem.setValue(dateListedType);
        hearingDateCollection.add(dateListedTypeItem);

        hearingType.setHearingNumber(caseData.getHearingNumbers());
        hearingType.setHearingType(caseData.getHearingTypes());
        hearingType.setHearingPublicPrivate(caseData.getHearingPublicPrivate());
        hearingType.setHearingVenue(caseData.getHearingVenue().getValue().getLabel());
        hearingType.setHearingEstLengthNum(caseData.getHearingEstLengthNum());
        hearingType.setHearingEstLengthNumType(caseData.getHearingEstLengthNumType());
        hearingType.setHearingSitAlone(caseData.getHearingSitAlone());
        hearingType.setHearingStage(caseData.getHearingStage());
        hearingType.setHearingDateCollection(hearingDateCollection);
        hearingType.setHearingNotes(caseData.getHearingNotes());

        return  hearingType;
    }

    private void clearTempHearingItem(CaseData caseData) {
        caseData.setHearingNumbers(null);
        caseData.setHearingTypes(null);
        caseData.setHearingPublicPrivate(null);
        caseData.setHearingVenue(null);
        caseData.setHearingEstLengthNum(null);
        caseData.setHearingEstLengthNumType(null);
        caseData.setHearingSitAlone(null);
        caseData.setHearingStage(null);
        caseData.setListedDate(null);
        caseData.setHearingNotes(null);
    }

    private void populateTempHearingFields(CaseData caseData, HearingType hearingType) {

        caseData.setHearingTypes(hearingType.getHearingType());
        caseData.setHearingSitAlone(hearingType.getHearingSitAlone());
        caseData.setHearingEEMember(hearingType.getHearingEEMember());
        caseData.setHearingERMember(hearingType.getHearingERMember());

        if (hearingType.getHearingDateCollection() != null && !hearingType.getHearingDateCollection().isEmpty()) {
            List<DynamicValueType> hearingDatesListItems = createDynamicHearingDateSelectionFixedList(hearingType.getHearingDateCollection());

            if (!hearingDatesListItems.isEmpty()) {
                caseData.setHearingDateSelection(bindDynamicSelectionFixedList(hearingDatesListItems));
            }
        }
    }

    private void populateHearingSelectionItems(CaseData caseData) {
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            List<DynamicValueType> hearingsListItems = createDynamicHearingSelectionFixedList(caseData.getHearingCollection());

            if (!hearingsListItems.isEmpty()) {
                caseData.setHearingSelection(bindDynamicSelectionFixedList(hearingsListItems));
            }
        }
    }

    private List<DynamicValueType> createDynamicHearingSelectionFixedList(List<HearingTypeItem> hearingCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();

        for (HearingTypeItem hearingTypeItem : hearingCollection) {
            HearingType hearingType = hearingTypeItem.getValue();
            if (hearingType.getHearingNumber() != null) {
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(hearingType.getHearingNumber());
                dynamicValueType.setLabel(hearingType.getHearingNumber());
                listItems.add(dynamicValueType);
            }
        }

        return listItems;
    }

    private List<DynamicValueType> createDynamicHearingDateSelectionFixedList(List<DateListedTypeItem> hearingDateCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();

        DynamicValueType firstDynamicValueType = new DynamicValueType();
        firstDynamicValueType.setCode(ADD_NEW_HEARING_DATE);
        firstDynamicValueType.setLabel(ADD_NEW_HEARING_DATE);
        listItems.add(firstDynamicValueType);

        for (DateListedTypeItem dateListedTypeItem : hearingDateCollection) {
            DateListedType dateListedType = dateListedTypeItem.getValue();
            if (dateListedType.getListedDate() != null) {
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(dateListedType.getListedDate());
                dynamicValueType.setLabel(dateListedType.getListedDate());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }

    private DynamicFixedListType bindDynamicSelectionFixedList(List<DynamicValueType> listItems) {
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setValue(listItems.get(0));
        dynamicFixedListType.setListItems(listItems);
        return dynamicFixedListType;
    }

    private CaseData getCaseData(CCDRequest ccdRequest) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        return caseDetails.getCaseData();
    }

    public CaseData createECC(CaseDetails caseDetails, String authToken, List<String> errors, String callback) {
        CaseData currentCaseData = caseDetails.getCaseData();
        List<SubmitEvent> submitEvents = getCasesES(caseDetails, authToken);
        if (submitEvents != null && !submitEvents.isEmpty()) {
            SubmitEvent submitEvent = submitEvents.get(0);
            switch (callback) {
                case MID_EVENT_CALLBACK:
                    Helper.midRespondentECC(currentCaseData, submitEvent.getCaseData());
                    break;
                case ABOUT_TO_SUBMIT_EVENT_CALLBACK:
                    createECCLogic(currentCaseData, submitEvent.getCaseData(), String.valueOf(submitEvent.getCaseId()));
                    currentCaseData.setRespondentECC(null);
                    break;
                default:
                    sendUpdateSingleCaseECC(authToken, caseDetails, submitEvent.getCaseData(), String.valueOf(submitEvent.getCaseId()));
            }
        } else {
            errors.add("Case Reference Number not found");
        }
        return currentCaseData;
    }

    private List<SubmitEvent> getCasesES(CaseDetails caseDetails, String authToken) {
//        return new ArrayList<>(Collections.singleton(caseRetrievalForCaseWorkerService.caseRetrievalRequest(authToken,
//                caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), "1584620660814572")));
        return caseRetrievalForCaseWorkerService.casesRetrievalESRequest(caseDetails.getCaseId(), authToken,
                caseDetails.getCaseTypeId(), new ArrayList<>(Collections.singleton(caseDetails.getCaseData().getCaseRefECC())));
    }

    private void createECCLogic(CaseData caseData, CaseData originalCaseData, String originalId) {
        if (originalCaseData.getRespondentCollection() != null) {
            Optional<RespondentSumTypeItem> respondentChosen = originalCaseData.getRespondentCollection()
                    .stream()
                    .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getRespondentName()
                            .equals(caseData.getRespondentECC().getValue().getCode()))
                    .findFirst();
            respondentChosen.ifPresent(respondentSumTypeItem ->
                    populateClaimantDetails(caseData, respondentSumTypeItem.getValue()));
        }
        populatePreAcceptCaseDetails(caseData);
        populateJurCodesCollection(caseData);
        populateRespondentCollectionDetails(caseData, originalCaseData.getClaimantIndType(), originalCaseData.getClaimantType());
        populateTribunalCorrespondenceDetails(caseData, originalCaseData);
        populateCaseDataDetails(caseData, originalCaseData, originalId);
    }

    private void populateClaimantDetails(CaseData caseData, RespondentSumType respondentSumType) {
        ClaimantType claimantType = new ClaimantType();
        claimantType.setClaimantAddressUK(respondentSumType.getRespondentAddress());
        caseData.setClaimantType(claimantType);

        ClaimantWorkAddressType claimantWorkAddressType = new ClaimantWorkAddressType();
        claimantWorkAddressType.setClaimantWorkAddress(respondentSumType.getRespondentAddress());
        caseData.setClaimantWorkAddress(claimantWorkAddressType);

        caseData.setClaimantTypeOfClaimant(COMPANY_TYPE_CLAIMANT);
        caseData.setClaimantCompany(respondentSumType.getRespondentName());
        caseData.setClaimantWorkAddressQuestion(YES);
        caseData.setReceiptDate(respondentSumType.getResponseReceivedDate());
    }

    private void populateRespondentCollectionDetails(CaseData caseData, ClaimantIndType originalClaimantIndType, ClaimantType originalClaimantType) {
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName(originalClaimantIndType.claimantFullName());
        respondentSumType.setRespondentACASNo(EMPLOYER_CONTRACT_CLAIM_CODE);
        respondentSumType.setRespondentACASQuestion(NO);
        respondentSumType.setRespondentAddress(originalClaimantType.getClaimantAddressUK());

        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singleton(respondentSumTypeItem)));
    }

    private void populateJurCodesCollection(CaseData caseData) {
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList(JURISDICTION_CODE_ECC);
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setId(JURISDICTION_CODE_ECC);
        jurCodesTypeItem.setValue(jurCodesType);
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singleton(jurCodesTypeItem)));
    }

    private void populatePreAcceptCaseDetails(CaseData caseData) {
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        casePreAcceptType.setDateAccepted(UtilHelper.formatCurrentDate2(LocalDate.now()));
        caseData.setPreAcceptCase(casePreAcceptType);
    }

    private void populateTribunalCorrespondenceDetails(CaseData caseData, CaseData originalCaseData) {
        caseData.setTribunalCorrespondenceAddress(originalCaseData.getTribunalCorrespondenceAddress());
        caseData.setTribunalCorrespondenceDX(originalCaseData.getTribunalCorrespondenceDX());
        caseData.setTribunalCorrespondenceEmail(originalCaseData.getTribunalCorrespondenceEmail());
        caseData.setTribunalCorrespondenceFax(originalCaseData.getTribunalCorrespondenceFax());
        caseData.setTribunalCorrespondenceTelephone(originalCaseData.getTribunalCorrespondenceTelephone());
    }

    private void populateCaseDataDetails(CaseData caseData, CaseData originalCaseData, String originalId) {
        caseData.setFeeGroupReference(originalCaseData.getFeeGroupReference());
        caseData.setCaseType(SINGLE_CASE_TYPE);
        caseData.setCaseSource(originalCaseData.getCaseSource());
        caseData.setCounterClaim(originalCaseData.getEthosCaseReference());
        caseData.setCcdID(originalId);
        caseData.setManagingOffice(originalCaseData.getManagingOffice() != null ? originalCaseData.getManagingOffice() : "");
        caseData.setAllocatedOffice(originalCaseData.getAllocatedOffice() != null ? originalCaseData.getAllocatedOffice() : "");
        caseData.setState(ACCEPTED_STATE);
    }

    private void sendUpdateSingleCaseECC(String authToken, CaseDetails currentCaseDetails, CaseData originalCaseData, String caseIdToLink) {
        try {
            originalCaseData.setCcdID(currentCaseDetails.getCaseId());
            originalCaseData.setCounterClaim(currentCaseDetails.getCaseData().getEthosCaseReference());
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), caseIdToLink);
            ccdClient.submitEventForCase(authToken, originalCaseData, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), returnedRequest, caseIdToLink);
        } catch (Exception e) {
            throw new CaseCreationException(MESSAGE + caseIdToLink + e.getMessage());
        }
    }

}
