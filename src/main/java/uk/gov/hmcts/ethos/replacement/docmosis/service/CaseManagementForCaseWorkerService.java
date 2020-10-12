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
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
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

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABOUT_TO_SUBMIT_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.COMPANY_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEFAULT_FLAGS_IMAGE_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_DO_NOT_POSTPONE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_ECC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_LIVE_APPEAL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_REPORTING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_RESERVED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_RULE_503B;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_SENSITIVE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IMAGE_FILE_EXTENSION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IMAGE_FILE_PRECEDING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INDIVIDUAL_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MID_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ONE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ZERO;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
@Service("caseManagementForCaseWorkerService")
public class CaseManagementForCaseWorkerService {

    private static final String MISSING_CLAIMANT = "Missing claimant";
    private static final String MISSING_RESPONDENT = "Missing respondent";

    private static final String COLOR_ORANGE = "Orange";
    private static final String COLOR_TURQUOISE = "Turquoise";
    private static final String COLOR_RED = "Red";
    private static final String COLOR_PURPLE = "Purple";
    private static final String COLOR_BLUE = "Blue";
    private static final String COLOR_GREEN = "Green";
    private static final String COLOR_BLACK = "Black";
    private static final String COLOR_WHITE = "White";

    private static final String EDIT_HEARING = "Edit hearing";
    private static final String DELETE_HEARING = "Delete hearing";
    private static final String ADD_NEW_HEARING_DATE = "Add a new hearing date";

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    private final CcdClient ccdClient;
    private static final String JURISDICTION_CODE_ECC = "BOC";
    private static final String EMPLOYER_CONTRACT_CLAIM_CODE = "ECC";
    private static final String MESSAGE = "Failed to link ECC case for case id : ";
    private static final String CASE_NOT_FOUND_MESSAGE = "Case Reference Number not found.";
    private static final String WRONG_CASE_STATE_MESSAGE = "An Employment Counterclaim Case can only be raised against a case that has a state of Accepted.";
    private static final String ET3_RESPONSE_NOT_FOUND_MESSAGE = "An Employment Counterclaim Case can only be raised against a case that has an ET3 response.";

    @Autowired
    public CaseManagementForCaseWorkerService(CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService, CcdClient ccdClient) {
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.ccdClient = ccdClient;
    }

    public void caseDataDefaults(CaseData caseData) {

        claimantDefaults(caseData);
        respondentDefaults(caseData);
        struckOutDefaults(caseData);
        flagsImageFileNameDefaults(caseData);
    }

    private void claimantDefaults(CaseData caseData) {
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

    private void respondentDefaults (CaseData caseData) {
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
            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();
            while (itr.hasNext()) {
                itr.next().getValue().setResponseStruckOut(NO);
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

    public void buildFlagsImageFileName(CaseData caseData) {
        StringBuilder flagsImageFileName = new StringBuilder();
        StringBuilder flagsImageAltText = new StringBuilder();

        flagsImageFileName.append(IMAGE_FILE_PRECEDING);
        setFlagImageFor(FLAG_DO_NOT_POSTPONE, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_LIVE_APPEAL, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_RULE_503B, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_REPORTING, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_SENSITIVE, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_RESERVED, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_ECC, flagsImageFileName, flagsImageAltText, caseData);
        flagsImageFileName.append(IMAGE_FILE_EXTENSION);

        caseData.setFlagsImageAltText(flagsImageAltText.toString());
        caseData.setFlagsImageFileName(flagsImageFileName.toString());
    }

    private void setFlagImageFor(String flagName, StringBuilder flagsImageFileName, StringBuilder flagsImageAltText, CaseData caseData) {
        boolean flagRequired;
        String flagColor;

        switch (flagName) {
            case FLAG_DO_NOT_POSTPONE:
                flagRequired = doNotPostpone(caseData);
                flagColor = COLOR_BLACK;
                break;
            case FLAG_LIVE_APPEAL:
                flagRequired = liveAppeal(caseData);
                flagColor = COLOR_GREEN;
                break;
            case FLAG_RULE_503B:
                flagRequired = rule503bApplies(caseData);
                flagColor = COLOR_RED;
                break;
            case FLAG_REPORTING:
                flagRequired = rule503dApplies(caseData);
                flagColor = COLOR_TURQUOISE;
                break;
            case FLAG_SENSITIVE:
                flagRequired = sensitiveCase(caseData);
                flagColor = COLOR_ORANGE;
                break;
            case FLAG_RESERVED:
                flagRequired = reservedJudgement(caseData);
                flagColor = COLOR_PURPLE;
                break;
            case FLAG_ECC:
                flagRequired = counterClaimMade(caseData);
                flagColor = COLOR_BLUE;
                break;
            default:
                flagRequired = false;
                flagColor = COLOR_WHITE;
        }

        flagsImageFileName.append(flagRequired ? ONE : ZERO);
        flagsImageAltText.append(flagRequired && flagsImageAltText.length() > 0 ? " - " : "");
        flagsImageAltText.append(flagRequired ? "<font color='" + flagColor + "'>" + flagName + "</font>" : "");
    }

    private boolean sensitiveCase(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getAdditionalSensitive())) {
                return caseData.getAdditionalCaseInfoType().getAdditionalSensitive().equals(YES);
            } else { return  false; }
        } else { return  false; }
    }

    private boolean rule503dApplies(CaseData caseData) {
        if (caseData.getRestrictedReporting() != null) {
            if (!isNullOrEmpty(caseData.getRestrictedReporting().getImposed())) {
                return caseData.getRestrictedReporting().getImposed().equals(YES);
            } else { return false; }
        }
        else { return false; }
    }

    private boolean rule503bApplies(CaseData caseData) {
        if (caseData.getRestrictedReporting() != null) {
            if (!isNullOrEmpty(caseData.getRestrictedReporting().getRule503b())) {
                return caseData.getRestrictedReporting().getRule503b().equals(YES);
            } else { return false; }
        } else { return false; }
    }

    private boolean reservedJudgement(CaseData caseData) {
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingDateCollection() != null && !hearingTypeItem.getValue().getHearingDateCollection().isEmpty()) {
                    for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                        String hearingReservedJudgement = dateListedTypeItem.getValue().getHearingReservedJudgement();
                        if (!isNullOrEmpty(hearingReservedJudgement) && hearingReservedJudgement.equals(YES))  {
                            return true;
                        }
                    }
                }
            }
        } else { return false; }
        return false;
    }

    private boolean counterClaimMade(CaseData caseData) {
        return !isNullOrEmpty(caseData.getCounterClaim());
    }

    private boolean liveAppeal(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getAdditionalLiveAppeal())) {
                return caseData.getAdditionalCaseInfoType().getAdditionalLiveAppeal().equals(YES);
            } else { return false; }
        } else { return false; }
    }

    private boolean doNotPostpone(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getDoNotPostpone() )) {
                return caseData.getAdditionalCaseInfoType().getDoNotPostpone().equals(YES);
            } else { return false; }
        } else { return false; }
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
        log.info("EventId: " + ccdRequest.getEventId());
        return caseDetails.getCaseData();
    }

    public CaseData createECC(CaseDetails caseDetails, String authToken, List<String> errors, String callback) {
        CaseData currentCaseData = caseDetails.getCaseData();
        List<SubmitEvent> submitEvents = getCasesES(caseDetails, authToken);
        if (submitEvents != null && !submitEvents.isEmpty()) {
            SubmitEvent submitEvent = submitEvents.get(0);
            if(validCaseForECC(submitEvent, errors)) {
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
            }
        } else {
            errors.add(CASE_NOT_FOUND_MESSAGE);
        }
        return currentCaseData;
    }

    private List<SubmitEvent> getCasesES(CaseDetails caseDetails, String authToken) {
//        return new ArrayList<>(Collections.singleton(caseRetrievalForCaseWorkerService.caseRetrievalRequest(authToken,
//                caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), "1584620660814572")));
        return caseRetrievalForCaseWorkerService.casesRetrievalESRequest(caseDetails.getCaseId(), authToken,
                caseDetails.getCaseTypeId(), new ArrayList<>(Collections.singleton(caseDetails.getCaseData().getCaseRefECC())));
    }

    private boolean validCaseForECC(SubmitEvent submitEvent, List<String> errors) {
        boolean validCaseForECC = true;
        if(!submitEvent.getState().equals(ACCEPTED_STATE)) {
            errors.add(WRONG_CASE_STATE_MESSAGE);
            validCaseForECC = false;
        }
        if (!et3Received(submitEvent)) {
            errors.add(ET3_RESPONSE_NOT_FOUND_MESSAGE);
            validCaseForECC = false;
        }
        return validCaseForECC;
    }

    private boolean et3Received(SubmitEvent submitEvent) {
        CaseData caseData = submitEvent.getCaseData();
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();
            while (itr.hasNext()) {
                RespondentSumType respondentSumType = itr.next().getValue();
                if (respondentSumType.getResponseReceived() != null && respondentSumType.getResponseReceived().equals(YES)) {
                    return true;
                }
            }
        }
        return false;
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
        populateRepresentativeClaimantDetails(caseData, originalCaseData);
        populateRepCollectionDetails(caseData, originalCaseData);
        buildFlagsImageFileName(caseData);
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

    private void populateRepresentativeClaimantDetails (CaseData caseData, CaseData originalCaseData) {
        if (originalCaseData.getRepCollection() != null && !originalCaseData.getRepCollection().isEmpty()) {
            ListIterator<RepresentedTypeRItem> itr = originalCaseData.getRepCollection().listIterator();
            while (itr.hasNext()) {
                RepresentedTypeR representedTypeR = itr.next().getValue();
                if (representedTypeR.getRespRepName() != null && representedTypeR.getRespRepName().equals(caseData.getClaimantCompany())) {
                    RepresentedTypeC representedTypeC = new RepresentedTypeC();
                    representedTypeC.setNameOfRepresentative(nullCheck(representedTypeR.getNameOfRepresentative()));
                    representedTypeC.setNameOfOrganisation(nullCheck(representedTypeR.getNameOfOrganisation()));
                    representedTypeC.setRepresentativeReference(nullCheck(representedTypeR.getRepresentativeReference()));
                    representedTypeC.setRepresentativeOccupation(nullCheck(representedTypeR.getRepresentativeOccupation()));
                    representedTypeC.setRepresentativeOccupationOther(nullCheck(representedTypeR.getRepresentativeOccupationOther()));
                    representedTypeC.setRepresentativeAddress(representedTypeR.getRepresentativeAddress());
                    representedTypeC.setRepresentativePhoneNumber(nullCheck(representedTypeR.getRepresentativePhoneNumber()));
                    representedTypeC.setRepresentativeMobileNumber(nullCheck(representedTypeR.getRepresentativeMobileNumber()));
                    representedTypeC.setRepresentativeEmailAddress(nullCheck(representedTypeR.getRepresentativeEmailAddress()));
                    representedTypeC.setRepresentativePreference(nullCheck(representedTypeR.getRepresentativePreference()));
                    caseData.setRepresentativeClaimantType(representedTypeC);
                    caseData.setClaimantRepresentedQuestion(YES);
                    break;
                }
            }
        }
    }

    private void populateRepCollectionDetails(CaseData caseData, CaseData originalCaseData) {
        RepresentedTypeC representativeClaimantType = originalCaseData.getRepresentativeClaimantType();
        if (representativeClaimantType != null && originalCaseData.getClaimantRepresentedQuestion().equals(YES)) {
            RepresentedTypeR representedTypeR = new RepresentedTypeR();
            representedTypeR.setRespRepName(caseData.getRespondentCollection().get(0).getValue().getRespondentName());
            representedTypeR.setNameOfRepresentative(nullCheck(representativeClaimantType.getNameOfRepresentative()));
            representedTypeR.setNameOfOrganisation(nullCheck(representativeClaimantType.getNameOfOrganisation()));
            representedTypeR.setRepresentativeReference(nullCheck(representativeClaimantType.getRepresentativeReference()));
            representedTypeR.setRepresentativeOccupation(nullCheck(representativeClaimantType.getRepresentativeOccupation()));
            representedTypeR.setRepresentativeOccupationOther(nullCheck(representativeClaimantType.getRepresentativeOccupationOther()));
            representedTypeR.setRepresentativeAddress(representativeClaimantType.getRepresentativeAddress());
            representedTypeR.setRepresentativePhoneNumber(nullCheck(representativeClaimantType.getRepresentativePhoneNumber()));
            representedTypeR.setRepresentativeMobileNumber(nullCheck(representativeClaimantType.getRepresentativeMobileNumber()));
            representedTypeR.setRepresentativeEmailAddress(nullCheck(representativeClaimantType.getRepresentativeEmailAddress()));
            representedTypeR.setRepresentativePreference(nullCheck(representativeClaimantType.getRepresentativePreference()));
            RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
            representedTypeRItem.setValue(representedTypeR);
            caseData.setRepCollection(new ArrayList<>(Collections.singleton(representedTypeRItem)));
        }
    }

    private void sendUpdateSingleCaseECC(String authToken, CaseDetails currentCaseDetails, CaseData originalCaseData, String caseIdToLink) {
        try {
            originalCaseData.setCcdID(currentCaseDetails.getCaseId());
            originalCaseData.setCounterClaim(currentCaseDetails.getCaseData().getEthosCaseReference());
            buildFlagsImageFileName(originalCaseData);
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), caseIdToLink);
            ccdClient.submitEventForCase(authToken, originalCaseData, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), returnedRequest, caseIdToLink);
        } catch (Exception e) {
            throw new CaseCreationException(MESSAGE + caseIdToLink + e.getMessage());
        }
    }

}
