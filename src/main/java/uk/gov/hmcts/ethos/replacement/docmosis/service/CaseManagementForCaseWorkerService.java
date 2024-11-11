package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
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

    @Autowired
    public CaseManagementForCaseWorkerService(CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService,
                                              CcdClient ccdClient,
                                              @Value("${ccd_gateway_base_url}") String ccdGatewayBaseUrl) {
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
        if (isNotEmpty(caseData.getRespondentCollection())) {
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
                if (isNullOrEmpty(respondentSumTypeItem.getValue().getResponseContinue())) {
                    respondentSumTypeItem.getValue().setResponseContinue(YES);
                }
            }
        } else {
            caseData.setRespondent(MISSING_RESPONDENT);
        }
    }

    private void resetResponseRespondentAddress(RespondentSumTypeItem respondentSumTypeItem) {
        if (!isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine1())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine1("");
        }
        if (!isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine2())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine2("");
        }
        if (!isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine3())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine3("");
        }
        if (!isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getCountry())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setCountry("");
        }
        if (!isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getCounty())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setCounty("");
        }
        if (!isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getPostCode())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setPostCode("");
        }
        if (!isNullOrEmpty(respondentSumTypeItem.getValue().getResponseRespondentAddress().getPostTown())) {
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setPostTown("");
        }
    }

    private void struckOutDefaults(CaseData caseData) {
        if (isEmpty(caseData.getRespondentCollection())) {
            return;
        }
        caseData.getRespondentCollection().stream()
                .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getResponseStruckOut() == null)
                .forEach(respondentSumTypeItem -> respondentSumTypeItem.getValue().setResponseStruckOut(NO));
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
        if (HEARING_NUMBER.equals(caseData.getHearingUpdateFilterType()) && caseData.getHearingCollection() != null) {
            if (caseData.getSelectedHearingNumberForUpdate() != null) {
                Optional<HearingTypeItem> hearingForUpdate = caseData.getHearingCollection().stream()
                        .filter(h -> h.getValue().getHearingNumber()
                                .equals(caseData.getSelectedHearingNumberForUpdate()
                                        .getValue().getCode())).findFirst();
                hearingForUpdate.ifPresent(hearingTypeItem -> caseData.setHearingsCollectionForUpdate(
                        Collections.singletonList(hearingTypeItem)));
            }
        } else {
            // when update request is on hearings from more than one hearing, i.e.
            // custom filter for filtering by hearing date
            if (isNotEmpty(caseData.getHearingCollection())) {
                filterValidHearingDates(caseData);
            }
        }

        if (isNotEmpty(caseData.getHearingsCollectionForUpdate())) {
            caseData.getHearingsCollectionForUpdate().stream()
                    .filter(hearingTypeItem ->
                            ObjectUtils.isNotEmpty(hearingTypeItem.getValue().getHearingNotesDocument()))
                    .forEach(hearingTypeItem -> hearingTypeItem.getValue().setDoesHearingNotesDocExist(YES));
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
                if (isNotEmpty(validHearingDates)) {
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
                if (isNotEmpty(validHearingDates)) {
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
        HearingType hearingTypeItemValue = hearingTypeItem.getValue();
        hearingType.setHearingVenue(hearingTypeItemValue.getHearingVenue());
        hearingType.setHearingType(hearingTypeItemValue.getHearingType());
        hearingType.setHearingNotesDocument(hearingTypeItemValue.getHearingNotesDocument());
        hearingType.setHearingFormat(hearingTypeItemValue.getHearingFormat());
        hearingType.setHearingNumber(hearingTypeItemValue.getHearingNumber());
        hearingType.setHearingEstLengthNum(hearingTypeItemValue.getHearingEstLengthNum());
        hearingType.setHearingEstLengthNumType(hearingTypeItemValue.getHearingEstLengthNumType());
        hearingType.setHearingSitAlone(hearingTypeItemValue.getHearingSitAlone());
        hearingType.setJudge(hearingTypeItemValue.getJudge());
        hearingType.setHearingEEMember(hearingTypeItemValue.getHearingEEMember());
        hearingType.setHearingERMember(hearingTypeItemValue.getHearingERMember());
        hearingType.setHearingStage(hearingTypeItemValue.getHearingStage());
        hearingType.setHearingNotes(hearingTypeItemValue.getHearingNotes());

        filteredHearingDates.forEach(hd -> hearingType.getHearingDateCollection().add(hd));
        currentHearingTypeItem.setValue(hearingType);
        caseData.getHearingsCollectionForUpdate().add(currentHearingTypeItem);
    }

    public void updateSelectedHearing(CaseData caseData) {
        if (isEmpty(caseData.getHearingsCollectionForUpdate())) {
            return;
        }

        caseData.getHearingsCollectionForUpdate().forEach(updatedHearing -> {
            Optional<HearingTypeItem> matchingHearing = caseData.getHearingCollection().stream()
                    .filter(h -> h.getId().equals(updatedHearing.getId()))
                    .findFirst();
            matchingHearing.ifPresent(hearingTypeItem -> {
                HearingType sourceHearingType = updatedHearing.getValue();
                HearingType targetHearingType = hearingTypeItem.getValue();

                // update  fields shared at hearing level like Sit Alone or Full Panel
                targetHearingType.setHearingSitAlone(sourceHearingType.getHearingSitAlone());
                targetHearingType.setJudge(sourceHearingType.getJudge());
                targetHearingType.setHearingNotesDocument(isNotEmpty(sourceHearingType.getRemoveHearingNotesDocument())
                        ? null
                        : sourceHearingType.getHearingNotesDocument());
                targetHearingType.setDoesHearingNotesDocExist(null);
                targetHearingType.setRemoveHearingNotesDocument(Collections.emptyList());

                if (FULL_PANEL.equals(sourceHearingType.getHearingSitAlone())) {
                    targetHearingType.setHearingEEMember(sourceHearingType.getHearingEEMember());
                    targetHearingType.setHearingERMember(sourceHearingType.getHearingERMember());
                }

                // update hearing dates for the selected hearing dates collection entries
                updateHearingDates(hearingTypeItem, sourceHearingType);
            });
        });

        caseData.getHearingsCollectionForUpdate().clear();
        caseData.setHearingUpdateFilterType(null);
    }

    private void updateHearingDates(HearingTypeItem matchingHearing, HearingType updatedHearing) {
        // update hearing dates for the selected hearing dates collection entries
        for (var hearingDate : updatedHearing.getHearingDateCollection()) {
            Optional<DateListedTypeItem> hdToUpdate = matchingHearing.getValue().getHearingDateCollection()
                    .stream().filter(uhd -> String.valueOf(uhd.getId())
                            .equals(hearingDate.getId())).findFirst();
            hdToUpdate.ifPresent(dateListedTypeItem -> dateListedTypeItem.setValue(hearingDate.getValue()));
        }
    }

    public void setNextListedDate(CaseData caseData) {
        List<String> dates = new ArrayList<>();
        String nextListedDate = "";

        if (isNotEmpty(caseData.getHearingCollection())) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                dates.addAll(getListedDates(hearingTypeItem));
            }

            for (String date : dates) {
                LocalDateTime parsedDate = LocalDateTime.parse(date);
                if (nextListedDate.isEmpty() && parsedDate.isAfter(LocalDateTime.now())
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
        if (isNotEmpty(hearingType.getHearingDateCollection())) {
            for (DateListedTypeItem dateListedTypeItem : hearingType.getHearingDateCollection()) {
                DateListedType dateListedType = dateListedTypeItem.getValue();
                if (HEARING_STATUS_LISTED.equals(dateListedType.getHearingStatus())
                        && !isNullOrEmpty(dateListedType.getListedDate())) {
                    dates.add(dateListedType.getListedDate());
                }
            }
        }
        return dates;
    }

    public void setMigratedCaseLinkDetails(String authToken, CaseDetails caseDetails) {
        String transferredCaseLinkSourceCaseId = caseDetails.getCaseData().getTransferredCaseLinkSourceCaseId();
        String transferredCaseLinkSourceCaseTypeId = caseDetails.getCaseData().getTransferredCaseLinkSourceCaseTypeId();

        if (transferredCaseLinkSourceCaseId != null && transferredCaseLinkSourceCaseTypeId != null) {
            String ethosCaseReference = caseRetrievalForCaseWorkerService.caseRefRetrievalRequest(authToken,
                    transferredCaseLinkSourceCaseTypeId, EMPLOYMENT_JURISDICTION, transferredCaseLinkSourceCaseId);
            if (ethosCaseReference != null) {
                log.info("None null Ethos Case Reference found: {}.", ethosCaseReference);
                caseDetails.getCaseData().setLinkedCaseCT("Transferred from: ");
                String fullConstructedLink = "<a target=\"_blank\" href=\""
                        + String.format("%s/cases/case-details/%s", ccdGatewayBaseUrl, transferredCaseLinkSourceCaseId)
                        + "\">"
                        + ethosCaseReference + "</a>";
                caseDetails.getCaseData().setTransferredCaseLink(fullConstructedLink);
            }
        } else {
            log.info("Transferred Case Link can not be built for case {} "
                            + " because not source case details are set for {} and {} fields.",
                    caseDetails.getCaseId(), transferredCaseLinkSourceCaseId, transferredCaseLinkSourceCaseTypeId);
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
        if (isEmpty(caseData.getRespondentCollection())) {
            return caseData;
        }
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
                struckRespondent.stream()).toList());
        respondentDefaults(caseData);
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
                    notContinuingRespondent.stream()).toList());
            respondentDefaults(caseData);
        }
        return caseData;
    }

    private boolean positionChanged(CaseData caseData) {
        return (isNullOrEmpty(caseData.getCurrentPosition())
                || !caseData.getPositionType().equals(caseData.getCurrentPosition()));
    }

    public void amendHearing(CaseData caseData, String caseTypeId) {
        if (isEmpty(caseData.getHearingCollection())) {
            return;
        }
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            var hearingType =  hearingTypeItem.getValue();
            if (isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
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

    public void midEventAmendHearing(CaseData caseData, List<String> errors, List<String> warnings) {
        if (isEmpty(caseData.getHearingCollection())) {
            return;
        }

        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            if (isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                for (DateListedTypeItem dateListedTypeItm : hearingTypeItem.getValue().getHearingDateCollection()) {
                    processHearingDates(errors, warnings, hearingTypeItem, dateListedTypeItm);
                }
            }
        }
    }

    private void processHearingDates(List<String> errors, List<String> warnings, HearingTypeItem hearingTypeItem,
                                     DateListedTypeItem dateListedTypeItm) {
        addHearingsOnWeekendError(dateListedTypeItm, errors, hearingTypeItem.getValue().getHearingNumber());
        addHearingsInPastWarning(dateListedTypeItm, warnings, hearingTypeItem.getValue().getHearingNumber());
    }

    private void addHearingsInPastWarning(DateListedTypeItem dateListedTypeItem, List<String> warnings,
                                          String hearingNumber) {
        LocalDate date = LocalDateTime.parse(
                dateListedTypeItem.getValue().getListedDate(), OLD_DATE_TIME_PATTERN).toLocalDate();
        if ((isNullOrEmpty(dateListedTypeItem.getValue().getHearingStatus())
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
        if (SUNDAY.equals(dayOfWeek) || SATURDAY.equals(dayOfWeek)) {
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
                dateListedType.setHearingAberdeen(hearingType.getHearingAberdeen());
            }
        } else if (hearingType.getHearingDundee() != null) {
            if (dateListedType.getHearingDundee() == null) {
                dateListedType.setHearingDundee(hearingType.getHearingDundee());
            }
        } else if (hearingType.getHearingEdinburgh() != null) {
            if (dateListedType.getHearingEdinburgh() == null) {
                dateListedType.setHearingEdinburgh(hearingType.getHearingEdinburgh());
            }
        } else {
            if (dateListedType.getHearingGlasgow() == null) {
                dateListedType.setHearingGlasgow(hearingType.getHearingGlasgow());
            }
        }
    }

    public CaseData createECC(CaseDetails caseDetails, String authToken, List<String> errors, String callback) {
        var currentCaseData = caseDetails.getCaseData();
        List<SubmitEvent> submitEvents = getCasesES(caseDetails, authToken);
        if (isNotEmpty(submitEvents)) {
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
            EccCounterClaimTypeItem eccCounterClaimTypeItem = new EccCounterClaimTypeItem();
            EccCounterClaimType eccCounterClaimType = new EccCounterClaimType();
            eccCounterClaimType.setCounterClaim(currentCaseDetails.getCaseData().getEthosCaseReference());
            eccCounterClaimTypeItem.setId(UUID.randomUUID().toString());
            eccCounterClaimTypeItem.setValue(eccCounterClaimType);
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), caseIdToLink);
            CaseData returnedRequestCaseData = returnedRequest.getCaseDetails().getCaseData();
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
