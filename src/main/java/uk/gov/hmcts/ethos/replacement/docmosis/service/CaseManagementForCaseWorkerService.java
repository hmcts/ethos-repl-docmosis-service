package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import uk.gov.hmcts.ecm.common.model.ccd.items.EccCounterClaimTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.EccCounterClaimType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ECCHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
@Service("caseManagementForCaseWorkerService")
public class CaseManagementForCaseWorkerService {

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    private final CcdClient ccdClient;

    private static final String EMPLOYMENT_JURISDICTION = "EMPLOYMENT";
    private static final String MISSING_CLAIMANT = "Missing claimant";
    private static final String MISSING_RESPONDENT = "Missing respondent";
    private static final String MESSAGE = "Failed to link ECC case for case id : ";
    private static final String CASE_NOT_FOUND_MESSAGE = "Case Reference Number not found.";
    private static final String PAST_LISTED_DATE = "Hearing %s is listed in the past. If you want to change it, "
            + "please enter a date after today otherwise click Ignore and Continue.";
    public static final String LISTED_DATE_ON_WEEKEND_MESSAGE = "A hearing date you have entered "
            + "falls on a weekend. You cannot list this case on a weekend. Please amend the date of Hearing ";
    private static final String FULL_PANEL = "Full Panel";
    private static final String HEARING_NUMBER = "Hearing Number";
    private static final String SINGLE = "Single";
    private final String ccdGatewayBaseUrl;
    private static final List<String> caseTypeIdsToCheck = List.of("ET_EnglandWales", "ET_Scotland", "Bristol", "Leeds",
            "LondonCentral", "LondonEast", "LondonSouth", "Manchester",
            "MidlandsEast", "MidlandsWest", "Newcastle", "Scotland",
            "Wales", "Watford");

    @Autowired
    public CaseManagementForCaseWorkerService(CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService,
                                              CcdClient ccdClient,
                                              @Value("${ccd_gateway_base_url}")String ccdGatewayBaseUrl) {
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.ccdClient = ccdClient;
        this.ccdGatewayBaseUrl = ccdGatewayBaseUrl;
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

    public void processHearingsForUpdateRequest(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getCaseData();
        //clear pre-existing values
        caseData.getHearingsCollectionForUpdate().clear();

        // check if update is only on one hearing
        if (HEARING_NUMBER.equals(caseData.getHearingUpdateFilterType()) &&
                caseData.getHearingCollection() != null) {
            if (caseData.getSelectedHearingNumberForUpdate() != null) {
                Optional<HearingTypeItem> hearingForUpdate = caseData.getHearingCollection().stream()
                        .filter(h->h.getValue().getHearingNumber()
                                .equals(caseData.getSelectedHearingNumberForUpdate()
                                        .getValue().getCode())).findFirst();
                if (hearingForUpdate.isPresent()) {
                    caseData.setHearingsCollectionForUpdate(new ArrayList<>());
                    caseData.getHearingsCollectionForUpdate().add(hearingForUpdate.get());
                }
            }
        } else {
            // when update request is on hearings from more than one hearing, i.e.
            // custom filter for filtering by hearing date
            if (caseData.getHearingCollection() != null) {
                filterValidHearingDates(caseData);
            }
        }
    }

    private void filterValidHearingDates(CaseData caseData) {
        if (caseData.getUpdateHearingDetails() == null) {
            return;
        }

        //For Single hearing date
        if (SINGLE.equals(caseData.getUpdateHearingDetails().getHearingDateType())) {
            for (var hearingTypeItem : caseData.getHearingCollection()) {
                //get hearings with one or more valid(on the date for Single, within bounds for Range) hearing dates
                List<DateListedTypeItem> validHearingDates = hearingTypeItem.getValue()
                        .getHearingDateCollection().stream()
                        .filter(d -> ReportHelper.getFormattedLocalDate(d.getValue().getListedDate()).equals(
                                caseData.getUpdateHearingDetails().getHearingDate())).toList();

                //prepare hearing with only needed date entries and exclude the ones out of the filter/search criteria
                if(!validHearingDates.isEmpty()) {
                    addFilteredHearingDates(caseData, hearingTypeItem, validHearingDates);
                }
            }
        } else { // for Range of hearing dates

            for (var hearingTypeItem : caseData.getHearingCollection()) {
                List<DateListedTypeItem> validHearingDates = hearingTypeItem.getValue()
                        .getHearingDateCollection().stream()
                        .filter(d -> isInRangeHearingDate(d.getValue().getListedDate(),
                                caseData.getUpdateHearingDetails().getHearingDateFrom(),
                                caseData.getUpdateHearingDetails().getHearingDateTo())).toList();

                // hearing/s with only needed date entries and exclude the ones out of the filter/search criteria
                if(!validHearingDates.isEmpty()) {
                    addFilteredHearingDates(caseData, hearingTypeItem, validHearingDates);
                }
            }
        }
    }

    private boolean isInRangeHearingDate(String dateListed, String from, String to) {
        LocalDate listedLocalDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(dateListed),
                OLD_DATE_TIME_PATTERN2);
        LocalDate fromLocalDate = LocalDate.parse(from, OLD_DATE_TIME_PATTERN2);
        LocalDate toLocalDate = LocalDate.parse(to, OLD_DATE_TIME_PATTERN2);

        return (listedLocalDate.compareTo(fromLocalDate) >= 0)
                && (listedLocalDate.compareTo(toLocalDate) <= 0);
    }

    private void addFilteredHearingDates(CaseData caseData, HearingTypeItem hearingTypeItem,
                                         List<DateListedTypeItem> filteredHearingDates) {
        HearingTypeItem currentHearingTypeItem = new HearingTypeItem();
        currentHearingTypeItem.setId(hearingTypeItem.getId());

        HearingType hearingType = new HearingType();
        hearingType.setHearingVenue(hearingTypeItem.getValue().getHearingVenue());
        hearingType.setHearingType(hearingTypeItem.getValue().getHearingType());

        hearingType.setHearingFormat(hearingTypeItem.getValue().getHearingFormat());
        hearingType.setHearingNumber(hearingTypeItem.getValue().getHearingNumber());
        hearingType.setHearingEstLengthNum(hearingTypeItem.getValue().getHearingEstLengthNum());
        hearingType.setHearingEstLengthNumType(hearingTypeItem.getValue().getHearingEstLengthNumType());
        hearingType.setHearingSitAlone(hearingTypeItem.getValue().getHearingSitAlone());
        hearingType.setJudge(hearingTypeItem.getValue().getJudge());
        hearingType.setHearingEEMember(hearingTypeItem.getValue().getHearingEEMember());
        hearingType.setHearingERMember(hearingTypeItem.getValue().getHearingERMember());
        hearingType.setHearingStage(hearingTypeItem.getValue().getHearingStage());
        hearingType.setHearingNotes(hearingTypeItem.getValue().getHearingNotes());

        filteredHearingDates.forEach(hd -> hearingType.getHearingDateCollection().add(hd));
        currentHearingTypeItem.setValue(hearingType);
        caseData.getHearingsCollectionForUpdate().add(currentHearingTypeItem);
    }

    public void updateSelectedHearing(CaseData caseData) {
        if(caseData.getHearingsCollectionForUpdate() != null) {

            for (HearingTypeItem updatedHearing : caseData.getHearingsCollectionForUpdate()) {
                Optional<HearingTypeItem> matchingHearing = caseData.getHearingCollection().stream()
                        .filter(h -> h.getId().equals(updatedHearing.getId())).findFirst();

                if (matchingHearing.isPresent()) {
                    HearingType sourceHearingType = updatedHearing.getValue();
                    HearingType targetHearingType = matchingHearing.get().getValue();

                    // update  fields shared at hearing level like Sit Alone or Full Panel
                    targetHearingType.setHearingSitAlone(sourceHearingType.getHearingSitAlone());
                    targetHearingType.setJudge(sourceHearingType.getJudge());

                    if(FULL_PANEL.equals(sourceHearingType.getHearingSitAlone())) {
                        targetHearingType.setHearingEEMember(sourceHearingType.getHearingEEMember());
                        targetHearingType.setHearingERMember(sourceHearingType.getHearingERMember());
                    }

                    // update hearing dates for the selected hearing dates collection entries
                    updateHearingDates(matchingHearing.get(), sourceHearingType);
                }
            }

            caseData.getHearingsCollectionForUpdate().clear();
            caseData.setHearingUpdateFilterType(null);
        }
    }

    private void updateHearingDates(HearingTypeItem matchingHearing, HearingType updatedHearing) {
        // update hearing dates for the selected hearing dates collection entries
        for (var hearingDate : updatedHearing.getHearingDateCollection()) {
            Optional<DateListedTypeItem> hdToUpdate = matchingHearing.getValue().getHearingDateCollection()
                    .stream().filter(uhd -> String.valueOf(uhd.getId())
                            .equals(hearingDate.getId())).findFirst();
            if(hdToUpdate.isPresent()) {
                hdToUpdate.get().setValue(hearingDate.getValue());
            }
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

    public void setMigratedCaseLinkDetails(String authToken, CaseDetails caseDetails) {
        // get a target case data using the source case data and
        // elastic search query
        List<SubmitEvent> submitEvent = caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                caseDetails.getCaseId(), authToken, caseTypeIdsToCheck);
        if (CollectionUtils.isEmpty(submitEvent) || submitEvent.get(0) == null) {
            return;
        }
        log.info("SubmitEvent is retrieved from ES for the update target case: {}.", submitEvent.get(0).getCaseId());

        String sourceCaseId = String.valueOf(submitEvent.get(0).getCaseId());
        SubmitEvent fullSourceCase = caseRetrievalForCaseWorkerService.caseRetrievalRequest(authToken,
                caseDetails.getCaseTypeId(), EMPLOYMENT_JURISDICTION, sourceCaseId);
        if (fullSourceCase == null || fullSourceCase.getCaseData() == null) {
            return;
        }
        log.info("Full Source Case with data is retrieved via caseRetrievalRequest: {}.", fullSourceCase.getCaseId());

        if (fullSourceCase.getCaseData().getEthosCaseReference() != null) {
            caseDetails.getCaseData().setTransferredCaseLink("<a target=\"_blank\" href=\""
                    + String.format("%s/cases/case-details/%s", ccdGatewayBaseUrl, sourceCaseId) + "\">"
                    + fullSourceCase.getCaseData().getEthosCaseReference() + "</a>");
        }
    }

    public void amendRespondentNameRepresentativeNames(CaseData caseData) {
        List<RepresentedTypeRItem> repCollection = new ArrayList<>();
        for (RepresentedTypeRItem respondentRep : emptyIfNull(caseData.getRepCollection())) {
            final List<RespondentSumTypeItem> respondentCollection = caseData.getRespondentCollection();
            Optional<RespondentSumTypeItem> matchedRespondent = respondentCollection.stream()
                    .filter(resp ->
                            resp.getId().equals(respondentRep.getValue().getRespondentId())).findFirst();
            matchedRespondent.ifPresent(respondent -> updateRepWithRespondentDetails(respondent,
                    respondentRep, respondentCollection));
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

    public void midEventAmendHearing(CaseData caseData, List<String> errors, List<String> warnings) {
        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            return;
        }

        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                for (DateListedTypeItem dateListedTypeItm : hearingTypeItem.getValue().getHearingDateCollection()) {
                    processHearingDates(errors, warnings, hearingTypeItem, dateListedTypeItm);
                }
            }
        }
    }

    private void processHearingDates(List<String> errors, List<String> warnings, HearingTypeItem hearingTypeItem,
                                     DateListedTypeItem dateListedTypeItm) {
        addHearingsOnWeekendError(dateListedTypeItm, errors,
                hearingTypeItem.getValue().getHearingNumber());
        addHearingsInPastWarning(dateListedTypeItm, warnings, hearingTypeItem.getValue().getHearingNumber());
    }

    private void addHearingsInPastWarning(DateListedTypeItem dateListedTypeItem, List<String> warnings,
                                          String hearingNumber) {
        LocalDate date = LocalDateTime.parse(
                dateListedTypeItem.getValue().getListedDate(), OLD_DATE_TIME_PATTERN).toLocalDate();
        if ((Strings.isNullOrEmpty(dateListedTypeItem.getValue().getHearingStatus())
                || HEARING_STATUS_LISTED.equals(dateListedTypeItem.getValue().getHearingStatus()))
                && date.isBefore(LocalDate.now())) {
            warnings.add(PAST_LISTED_DATE.formatted(hearingNumber));

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
