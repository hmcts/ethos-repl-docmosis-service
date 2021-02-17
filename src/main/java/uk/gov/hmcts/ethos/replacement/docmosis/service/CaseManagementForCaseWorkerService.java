package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ECCHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
@Service("caseManagementForCaseWorkerService")
public class CaseManagementForCaseWorkerService {

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    private final CcdClient ccdClient;

    private static final String MISSING_CLAIMANT = "Missing claimant";
    private static final String MISSING_RESPONDENT = "Missing respondent";
    private static final String EDIT_HEARING = "Edit hearing";
    private static final String DELETE_HEARING = "Delete hearing";
    private static final String ADD_NEW_HEARING_DATE = "Add a new hearing date";
    private static final String MESSAGE = "Failed to link ECC case for case id : ";
    private static final String CASE_NOT_FOUND_MESSAGE = "Case Reference Number not found.";

    @Autowired
    public CaseManagementForCaseWorkerService(CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService, CcdClient ccdClient) {
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.ccdClient = ccdClient;
    }

    public void caseDataDefaults(CaseData caseData) {
        claimantDefaults(caseData);
        respondentDefaults(caseData);
        struckOutDefaults(caseData);
        dateToCurrentPosition(caseData);
        flagsImageFileNameDefaults(caseData);
    }

    public void claimantDefaults(CaseData caseData) {
        String claimantTypeOfClaimant = caseData.getClaimantTypeOfClaimant();
        if (!isNullOrEmpty(claimantTypeOfClaimant)) {
            if(claimantTypeOfClaimant.equals(INDIVIDUAL_TYPE_CLAIMANT)) {
                String claimantFirstNames = nullCheck(caseData.getClaimantIndType().getClaimantFirstNames());
                String claimantLastName = nullCheck(caseData.getClaimantIndType().getClaimantLastName());
                caseData.setClaimant(claimantFirstNames + " " + claimantLastName);
            } else {
                caseData.setClaimant(nullCheck(caseData.getClaimantCompany()));
            }
        } else {
            caseData.setClaimant(MISSING_CLAIMANT);
        }
    }

    private void respondentDefaults(CaseData caseData) {
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            RespondentSumType respondentSumType = caseData.getRespondentCollection().get(0).getValue();
            caseData.setRespondent(nullCheck(respondentSumType.getRespondentName()));
        }
        else {
            caseData.setRespondent(MISSING_RESPONDENT);
        }
    }

    private void struckOutDefaults(CaseData caseData) {
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                respondentSumTypeItem.getValue().setResponseStruckOut(NO);
            }
        }
    }

    private void flagsImageFileNameDefaults(CaseData caseData) {
        if(isNullOrEmpty(caseData.getFlagsImageFileName())) {
            caseData.setFlagsImageFileName(DEFAULT_FLAGS_IMAGE_FILE_NAME);
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

    public void dateToCurrentPosition(CaseData caseData) {
        if (!isNullOrEmpty(caseData.getPositionType()) && positionChanged(caseData)) {
            caseData.setDateToPosition(LocalDate.now().toString());
            caseData.setCurrentPosition(caseData.getPositionType());
        }
    }

    public CaseData struckOutRespondents(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            List<RespondentSumTypeItem> activeRespondent = new ArrayList<>();
            List<RespondentSumTypeItem> struckRespondent = new ArrayList<>();
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                RespondentSumType respondentSumType = respondentSumTypeItem.getValue();
                if (respondentSumType.getResponseStruckOut() != null) {
                    if (respondentSumType.getResponseStruckOut().equals(YES)) {
                        struckRespondent.add(respondentSumTypeItem);
                    } else {
                        activeRespondent.add(respondentSumTypeItem);
                    }
                } else {
                    respondentSumType.setResponseStruckOut(NO);
                    activeRespondent.add(respondentSumTypeItem);
                }
            }
            caseData.setRespondentCollection(Stream.concat(activeRespondent.stream(), struckRespondent.stream()).collect(Collectors.toList()));
            respondentDefaults(caseData);
        }
        return caseData;
    }

    private boolean positionChanged(CaseData caseData) {
        return (isNullOrEmpty(caseData.getCurrentPosition()) || !caseData.getPositionType().equals(caseData.getCurrentPosition()));
    }

    public CaseData addNewHearingItem(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);

        List<HearingTypeItem> hearingCollection = (caseData.getHearingCollection() != null ? caseData.getHearingCollection() : new ArrayList<>());
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = createNewHearingItem(caseData);
        hearingTypeItem.setValue(hearingType);
        hearingCollection.add(hearingTypeItem);
        caseData.setHearingCollection(hearingCollection);
        clearAddHearingFields(caseData);
        populateHearingSelectionItems(caseData);

        return caseData;
    }

    public CaseData fetchHearingItemData(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);
        if(caseData.getHearingActions().equals(EDIT_HEARING) && validHearingCollection(caseData.getHearingCollection())) {
            String hearingSelectionChoice = caseData.getHearingSelection().getValue().getCode();
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                HearingType hearingType = hearingTypeItem.getValue();
                if (hearingType.getHearingNumber() != null && hearingType.getHearingNumber().equals(hearingSelectionChoice)) {
                    populateTempHearingFields(caseData, hearingType);
                    break;
                }
            }
        }

        return caseData;
    }

    public CaseData amendHearingItemDetails(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);

        switch (caseData.getHearingActions()) {
            case EDIT_HEARING:
                editHearingItem(caseData);
                break;
            case DELETE_HEARING:
                deleteHearingItem(caseData);
                break;
            default:
                log.info("Missing action!");
        }
        clearAmendHearingFields(caseData);
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

    private void clearAddHearingFields(CaseData caseData) {
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

    private void populateHearingSelectionItems(CaseData caseData) {
        if (validHearingCollection(caseData.getHearingCollection())) {
            List<DynamicValueType> hearingsListItems = createDynamicHearingSelectionFixedList(caseData.getHearingCollection());

            if (!hearingsListItems.isEmpty()) {
                caseData.setHearingSelection(bindDynamicSelectionFixedList(hearingsListItems));
            }
        }
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

    private void editHearingItem(CaseData caseData) {
        HearingTypeItem matchingHearingItem = getMatchingHearingItem(caseData);

        if (matchingHearingItem.getValue() != null) {
            matchingHearingItem.getValue().setHearingSitAlone(caseData.getHearingSitAlone());
            matchingHearingItem.getValue().setHearingERMember(caseData.getHearingERMember());
            matchingHearingItem.getValue().setHearingEEMember(caseData.getHearingEEMember());
        }

        caseData.getHearingSelection().setValue(caseData.getHearingSelection().getListItems().get(0));
    }

    private void deleteHearingItem(CaseData caseData) {
        HearingTypeItem matchingHearingItem = getMatchingHearingItem(caseData);

        if (matchingHearingItem.getValue() != null) {
            caseData.getHearingCollection().remove(matchingHearingItem);
        }

        populateHearingSelectionItems(caseData);
    }

    private HearingTypeItem getMatchingHearingItem(CaseData caseData) {
        HearingTypeItem matchingHearingItem = new HearingTypeItem();
        if (validHearingCollection(caseData.getHearingCollection())) {
            String hearingSelectionChoice = caseData.getHearingSelection().getValue().getCode();
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                HearingType hearingType = hearingTypeItem.getValue();
                if (hearingType.getHearingNumber() != null && hearingType.getHearingNumber().equals(hearingSelectionChoice)) {
                    return hearingTypeItem;
                }
            }
        }
        return matchingHearingItem;
    }

    private void clearAmendHearingFields(CaseData caseData) {
        caseData.setHearingActions(null);

        caseData.setHearingTypes(null);
        caseData.setHearingSitAlone(null);
        caseData.setHearingERMember(null);
        caseData.setHearingEEMember(null);
        caseData.setHearingDatesRequireAmending(null);
        caseData.setHearingDateSelection(null);

        caseData.setHearingDateActions(null);

        caseData.setListedDate(null);
        caseData.setHearingStatus(null);
        caseData.setPostponed_by(null);
        caseData.setHearingVenue(null);
        caseData.setHearingRoom(null);
        caseData.setHearingClerk(null);
        caseData.setHearingJudge(null);

        caseData.setHearingCaseDisposed(null);
        caseData.setHearingPartHeard(null);
        caseData.setHearingReservedJudgement(null);
        caseData.setAttendeeClaimant(null);
        caseData.setAttendeeNonAttendees(null);
        caseData.setAttendeeRespNoRep(null);
        caseData.setAttendeeRespAndRep(null);
        caseData.setAttendeeRepOnly(null);
        caseData.setHearingTimingStart(null);
        caseData.setHearingTimingBreak(null);
        caseData.setHearingTimingResume(null);
        caseData.setHearingTimingFinish(null);
        caseData.setHearingTimingDuration(null);
        caseData.setCaseNotes(null);
    }

    private boolean validHearingCollection(List<HearingTypeItem> hearingCollection) {
        return hearingCollection != null && !hearingCollection.isEmpty();
    }

    private CaseData getCaseData(CCDRequest ccdRequest) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        return caseDetails.getCaseData();
    }

    public CaseData createECC(CaseDetails caseDetails, String authToken, List<String> errors, String callback) {
        CaseData currentCaseData = caseDetails.getCaseData();
        List<SubmitEvent> submitEvents = getCasesES(caseDetails, authToken);
        if (submitEvents != null && !submitEvents.isEmpty()) {
            SubmitEvent submitEvent = submitEvents.get(0);
            if(ECCHelper.validCaseForECC(submitEvent, errors)) {
                switch (callback) {
                    case MID_EVENT_CALLBACK:
                        Helper.midRespondentECC(currentCaseData, submitEvent.getCaseData());
                        break;
                    case ABOUT_TO_SUBMIT_EVENT_CALLBACK:
                        ECCHelper.createECCLogic(currentCaseData, submitEvent.getCaseData(), String.valueOf(submitEvent.getCaseId()));
                        currentCaseData.setRespondentECC(null);
                        break;
                    default:
                        sendUpdateSingleCaseECC(authToken, caseDetails, submitEvent.getCaseData(), String.valueOf(submitEvent.getCaseId()));
                }
            }
        } else {
            errors.add(CASE_NOT_FOUND_MESSAGE);
        }
        log.info("Add claimant and respondent defaults");
        claimantDefaults(currentCaseData);
        respondentDefaults(currentCaseData);
        return currentCaseData;
    }

    private List<SubmitEvent> getCasesES(CaseDetails caseDetails, String authToken) {
        return caseRetrievalForCaseWorkerService.casesRetrievalESRequest(caseDetails.getCaseId(), authToken,
                caseDetails.getCaseTypeId(), new ArrayList<>(Collections.singleton(caseDetails.getCaseData().getCaseRefECC())));
    }

    private void sendUpdateSingleCaseECC(String authToken, CaseDetails currentCaseDetails, CaseData originalCaseData, String caseIdToLink) {
        try {
            originalCaseData.setCcdID(currentCaseDetails.getCaseId());
            originalCaseData.setCounterClaim(currentCaseDetails.getCaseData().getEthosCaseReference());
            FlagsImageHelper.buildFlagsImageFileName(originalCaseData);
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), caseIdToLink);
            ccdClient.submitEventForCase(authToken, originalCaseData, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), returnedRequest, caseIdToLink);
        } catch (Exception e) {
            throw new CaseCreationException(MESSAGE + caseIdToLink + e.getMessage());
        }
    }

}
