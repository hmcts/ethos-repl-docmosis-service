package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.google.common.base.Strings;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.*;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.EccCounterClaimType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ECCHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABOUT_TO_SUBMIT_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEFAULT_FLAGS_IMAGE_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_ECC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INDIVIDUAL_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MID_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFCTC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_JUSTICE_CENTRE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_MAGS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
@Service("caseManagementForCaseWorkerService")
public class CaseManagementForCaseWorkerService {

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    private final CcdClient ccdClient;

    private static final String MISSING_CLAIMANT = "Missing claimant";
    private static final String MISSING_RESPONDENT = "Missing respondent";
    private static final String MESSAGE = "Failed to link ECC case for case id : ";
    private static final String CASE_NOT_FOUND_MESSAGE = "Case Reference Number not found.";
    public static final String LISTED_DATE_ON_WEEKEND_MESSAGE = "A hearing date you have entered "
            + "falls on a weekend. You cannot list this case on a weekend. Please amend the date of Hearing ";

    @Autowired
    public CaseManagementForCaseWorkerService(CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService,
                                              CcdClient ccdClient) {
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
            if (claimantTypeOfClaimant.equals(INDIVIDUAL_TYPE_CLAIMANT)) {
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
            var respondentSumType = caseData.getRespondentCollection().get(0).getValue();
            caseData.setRespondent(nullCheck(respondentSumType.getRespondentName()));
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                if (respondentSumTypeItem.getValue().getResponseReceived() == null) {
                    respondentSumTypeItem.getValue().setResponseReceived(NO);
                }
                if (respondentSumTypeItem.getValue().getResponseReceived().equals(NO)
                        && respondentSumTypeItem.getValue().getResponseRespondentAddress() != null) {
                    resetResponseRespondentAddress(respondentSumTypeItem);
                }
                if (Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseContinue())) {
                    respondentSumTypeItem.getValue().setResponseContinue(YES);
                }
            }
        } else {
            caseData.setRespondent(MISSING_RESPONDENT);
        }
    }

    private void resetResponseRespondentAddress(RespondentSumTypeItem respondentSumTypeItem) {
        if (!Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine1())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine1("");
        }
        if (!Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine2())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine2("");
        }
        if (!Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine3())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine3("");
        }
        if (!Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getCountry())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setCountry("");
        }
        if (!Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getCounty())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setCounty("");
        }
        if (!Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getPostCode())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setPostCode("");
        }
        if (!Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getPostTown())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setPostTown("");
        }
    }

    private void struckOutDefaults(CaseData caseData) {
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                if (respondentSumTypeItem.getValue().getResponseStruckOut() == null) {
                    respondentSumTypeItem.getValue().setResponseStruckOut(NO);
                }
            }
        }
    }

    private void flagsImageFileNameDefaults(CaseData caseData) {
        if (isNullOrEmpty(caseData.getFlagsImageFileName())) {
            caseData.setFlagsImageFileName(DEFAULT_FLAGS_IMAGE_FILE_NAME);
        }
    }

    public void dateToCurrentPosition(CaseData caseData) {
        if (!isNullOrEmpty(caseData.getPositionType()) && positionChanged(caseData)) {
            caseData.setDateToPosition(LocalDate.now().toString());
            caseData.setCurrentPosition(caseData.getPositionType());
        }
    }

    public void setNextListedDate(CaseData caseData) {
        List<String> dates = new ArrayList<>();
        String nextListedDate = "";

        if (!CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                dates.addAll(getListedDates(hearingTypeItem));
            }
            for (String date : dates) {
                LocalDateTime parsedDate = LocalDateTime.parse(date);
                if (nextListedDate.equals("") && parsedDate.isAfter(LocalDateTime.now())
                        || parsedDate.isAfter(LocalDateTime.now())
                        && parsedDate.isBefore(LocalDateTime.parse(nextListedDate))) {
                    nextListedDate = date;
                }
            }
                caseData.setNextListedDate(nextListedDate.split("T")[0]);
        }
    }

    private List<String> getListedDates(HearingTypeItem hearingTypeItem) {
        HearingType hearingType = hearingTypeItem.getValue();
        List<String> dates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(hearingType.getHearingDateCollection())) {
            for (DateListedTypeItem dateListedTypeItem : hearingType.getHearingDateCollection()) {
                DateListedType dateListedType = dateListedTypeItem.getValue();
                if (HEARING_STATUS_LISTED.equals(dateListedType.getHearingStatus())
                        && !Strings.isNullOrEmpty(dateListedType.getListedDate())) {
                    dates.add(dateListedType.getListedDate());
                }
            }
        }
        return dates;
    }

    public void amendRespondentNameRepresentativeNames(CaseData caseData) {
        List<RepresentedTypeRItem> repCollection = new ArrayList<>();
        for (RepresentedTypeRItem respondentRep : emptyIfNull(caseData.getRepCollection())) {
            final List<RespondentSumTypeItem> respondentCollection = caseData.getRespondentCollection();
            Optional<RespondentSumTypeItem> matchedRespondent = respondentCollection.stream()
                    .filter(resp ->
                            resp.getId().equals(respondentRep.getValue().getRespondentId())).findFirst();

            matchedRespondent.ifPresent(respondent ->
                    updateRepWithRespondentDetails(respondent, respondentRep, respondentCollection));

            repCollection.add(respondentRep);
        }

        caseData.setRepCollection(repCollection);
    }

    public void updateWithRespondentIds(CaseData caseData) {
        List<RepresentedTypeRItem> repList = new ArrayList<>();
        for (RepresentedTypeRItem respondentRep : caseData.getRepCollection()) {
            getRespondentSumTypeItem(caseData, respondentRep)
                    .ifPresent(respondent ->
                            respondentRep.getValue().setRespondentId(respondent.getId()));
            repList.add(respondentRep);
        }
        caseData.setRepCollection(repList);
    }

    public Optional<RespondentSumTypeItem> getRespondentSumTypeItem(CaseData caseData,
                                                                    RepresentedTypeRItem respondentRep) {
        final List<RespondentSumTypeItem> respondentCollection = caseData.getRespondentCollection();
        return respondentCollection.stream()
                .filter(resp ->
                        resp.getValue().getRespondentName()
                                .equals(respondentRep.getValue().getRespRepName())).findFirst();
    }

    private void updateRepWithRespondentDetails(RespondentSumTypeItem respondent,
                                                RepresentedTypeRItem respondentRep,
                                               List<RespondentSumTypeItem> respondents) {
        List<DynamicValueType> respondentNameList = DynamicListHelper.createDynamicRespondentName(
                respondents);

        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(respondentNameList);
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("R: " + respondent.getValue().getRespondentName());
        dynamicValueType.setLabel(respondent.getValue().getRespondentName());
        dynamicFixedListType.setValue(dynamicValueType);

        respondentRep.getValue().setDynamicRespRepName(dynamicFixedListType);
        respondentRep.getValue().setRespondentId(respondent.getId());
        respondentRep.getValue().setRespRepName(respondent.getValue().getRespondentName());
    }

    public CaseData struckOutRespondents(CCDRequest ccdRequest) {
        var caseData = ccdRequest.getCaseDetails().getCaseData();
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            List<RespondentSumTypeItem> activeRespondent = new ArrayList<>();
            List<RespondentSumTypeItem> struckRespondent = new ArrayList<>();
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                var respondentSumType = respondentSumTypeItem.getValue();
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
            caseData.setRespondentCollection(Stream.concat(activeRespondent.stream(),
                    struckRespondent.stream()).collect(Collectors.toList()));
            respondentDefaults(caseData);
        }
        return caseData;
    }

    public CaseData continuingRespondent(CCDRequest ccdRequest) {
        var caseData = ccdRequest.getCaseDetails().getCaseData();
        if (CollectionUtils.isEmpty(caseData.getRepCollection())) {
            List<RespondentSumTypeItem> continuingRespondent = new ArrayList<>();
            List<RespondentSumTypeItem> notContinuingRespondent = new ArrayList<>();
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                var respondentSumType = respondentSumTypeItem.getValue();
                if (YES.equals(respondentSumType.getResponseContinue())) {
                    continuingRespondent.add(respondentSumTypeItem);
                } else if (NO.equals(respondentSumType.getResponseContinue())) {
                    notContinuingRespondent.add(respondentSumTypeItem);
                } else {
                    respondentSumType.setResponseContinue(YES);
                    continuingRespondent.add(respondentSumTypeItem);
                }
            }
            caseData.setRespondentCollection(Stream.concat(continuingRespondent.stream(),
                    notContinuingRespondent.stream()).collect(Collectors.toList()));
            respondentDefaults(caseData);
        }
        return caseData;
    }

    private boolean positionChanged(CaseData caseData) {
        return (isNullOrEmpty(caseData.getCurrentPosition())
                || !caseData.getPositionType().equals(caseData.getCurrentPosition()));
    }

    public void amendHearing(CaseData caseData, String caseTypeId) {
        if (!CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                var hearingType =  hearingTypeItem.getValue();
                if (!CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {
                        var dateListedType = dateListedTypeItem.getValue();
                        if (dateListedType.getHearingStatus() == null) {
                            dateListedType.setHearingStatus(HEARING_STATUS_LISTED);
                            dateListedType.setHearingTimingStart(dateListedType.getListedDate());
                            dateListedType.setHearingTimingFinish(dateListedType.getListedDate());
                        }
                        populateHearingVenueFromHearingLevelToDayLevel(dateListedType, hearingType, caseTypeId);
                    }
                }
            }
        }
    }

    public void midEventAmendHearing(CaseData caseData, List<String> errors) {
        if (!CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (!CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {
                        addHearingsOnWeekendError(dateListedTypeItem, errors,
                                hearingTypeItem.getValue().getHearingNumber());
                    }
                }
            }
        }
    }

    private void addHearingsOnWeekendError(DateListedTypeItem dateListedTypeItem, List<String> errors,
                                           String hearingNumber) {
        var date = LocalDateTime.parse(
                dateListedTypeItem.getValue().getListedDate(), OLD_DATE_TIME_PATTERN).toLocalDate();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (SUNDAY.equals(dayOfWeek)
                || SATURDAY.equals(dayOfWeek)) {
            errors.add(LISTED_DATE_ON_WEEKEND_MESSAGE + hearingNumber);
        }
    }

    private void populateHearingVenueFromHearingLevelToDayLevel(DateListedType dateListedType, HearingType hearingType,
                                                                String caseTypeId) {
        if (caseTypeId.equals(SCOTLAND_CASE_TYPE_ID)) {
            setHearingScottishOffices(dateListedType, hearingType);
        }

        if (caseTypeId.equals(NEWCASTLE_CASE_TYPE_ID)) {
            setHearingVenueDayForNewcastleUniqueOffices(dateListedType, hearingType);
        }

        setHearingVenueDay(dateListedType, hearingType);
    }

    private void setHearingVenueDay(DateListedType dateListedType, HearingType hearingType) {
        if (dateListedType.getHearingVenueDay() == null) {
            dateListedType.setHearingVenueDay(hearingType.getHearingVenue());
        }
    }

    private void setHearingVenueDayForNewcastleUniqueOffices(DateListedType dateListedType, HearingType hearingType) {
        if (NEWCASTLE_CFT.equals(hearingType.getHearingVenue())) {
            dateListedType.setHearingVenueDay(NEWCASTLE_CFT);
            dateListedType.setHearingVenueNameForNewcastleCFT(NEWCASTLE_CFCTC);
        }

        if (TEESSIDE_MAGS.equals(hearingType.getHearingVenue())) {
            dateListedType.setHearingVenueDay(TEESSIDE_MAGS);
            dateListedType.setHearingVenueNameForTeessideMags(TEESSIDE_JUSTICE_CENTRE);
        }
    }

    private void setHearingScottishOffices(DateListedType dateListedType, HearingType hearingType) {
        if (hearingType.getHearingAberdeen() != null) {
            if (dateListedType.getHearingAberdeen() == null) {
                log.info("Adding hearing day level Aberdeen");
                dateListedType.setHearingAberdeen(hearingType.getHearingAberdeen());
            }
        } else if (hearingType.getHearingDundee() != null) {
            if (dateListedType.getHearingDundee() == null) {
                log.info("Adding hearing day level Dundee");
                dateListedType.setHearingDundee(hearingType.getHearingDundee());
            }
        } else if (hearingType.getHearingEdinburgh() != null) {
            if (dateListedType.getHearingEdinburgh() == null) {
                log.info("Adding hearing day level Edinburgh");
                dateListedType.setHearingEdinburgh(hearingType.getHearingEdinburgh());
            }
        } else {
            if (dateListedType.getHearingGlasgow() == null) {
                log.info("Adding hearing day level Glasgow");
                dateListedType.setHearingGlasgow(hearingType.getHearingGlasgow());
            }
        }
    }

    public CaseData createECC(CaseDetails caseDetails, String authToken, List<String> errors, String callback) {
        var currentCaseData = caseDetails.getCaseData();
        List<SubmitEvent> submitEvents = getCasesES(caseDetails, authToken);
        if (submitEvents != null && !submitEvents.isEmpty()) {
            var submitEvent = submitEvents.get(0);
            if (ECCHelper.validCaseForECC(submitEvent, errors)) {
                switch (callback) {
                    case MID_EVENT_CALLBACK:
                        Helper.midRespondentECC(currentCaseData, submitEvent.getCaseData());
                        break;
                    case ABOUT_TO_SUBMIT_EVENT_CALLBACK:
                        ECCHelper.createECCLogic(currentCaseData, submitEvent.getCaseData());
                        currentCaseData.setRespondentECC(null);
                        currentCaseData.setCaseSource(FLAG_ECC);
                        break;
                    default:
                        sendUpdateSingleCaseECC(authToken, caseDetails, String.valueOf(submitEvent.getCaseId()));
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
                caseDetails.getCaseTypeId(),
                new ArrayList<>(Collections.singleton(caseDetails.getCaseData().getCaseRefECC())));
    }

    private void sendUpdateSingleCaseECC(String authToken, CaseDetails currentCaseDetails,
                                         String caseIdToLink) {
        try {
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), caseIdToLink);
            CaseData returnedRequestCaseData = returnedRequest.getCaseDetails().getCaseData();
            EccCounterClaimTypeItem eccCounterClaimTypeItem = new EccCounterClaimTypeItem();
            EccCounterClaimType eccCounterClaimType = new EccCounterClaimType();
            eccCounterClaimType.setCounterClaim(currentCaseDetails.getCaseData().getEthosCaseReference());
            eccCounterClaimTypeItem.setId(UUID.randomUUID().toString());
            eccCounterClaimTypeItem.setValue(eccCounterClaimType);
            if (returnedRequestCaseData.getEccCases() != null) {
                returnedRequestCaseData.getEccCases().add(eccCounterClaimTypeItem);
            } else {
                returnedRequestCaseData.setEccCases(
                        new ArrayList<>(Collections.singletonList(eccCounterClaimTypeItem)));
            }
            FlagsImageHelper.buildFlagsImageFileName(returnedRequestCaseData);

            ccdClient.submitEventForCase(authToken, returnedRequestCaseData, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), returnedRequest, caseIdToLink);
        } catch (Exception e) {
            throw new CaseCreationException(MESSAGE + caseIdToLink + e.getMessage());
        }
    }

}
