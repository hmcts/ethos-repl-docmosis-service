package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.*;
import uk.gov.hmcts.ecm.common.model.ccd.types.*;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ECCHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INDIVIDUAL_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABOUT_TO_SUBMIT_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MID_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEFAULT_FLAGS_IMAGE_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_ECC;
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
                } else if (NO.equals(respondentSumType.getResponseContinue())){
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
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                var hearingType =  hearingTypeItem.getValue();
                if (hearingTypeItem.getValue().getHearingDateCollection() != null
                        && !hearingTypeItem.getValue().getHearingDateCollection().isEmpty()) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {
                        var dateListedType = dateListedTypeItem.getValue();
                        if (dateListedType.getHearingStatus() == null) {
                            dateListedType.setHearingStatus(HEARING_STATUS_LISTED);
                        }
                        populateHearingVenueFromHearingLevelToDayLevel(dateListedType, hearingType, caseTypeId);
                    }
                }
            }
        }
    }

    private void populateHearingVenueFromHearingLevelToDayLevel(DateListedType dateListedType, HearingType hearingType,
                                                                String caseTypeId) {
        if (caseTypeId.equals(SCOTLAND_CASE_TYPE_ID)) {
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
        if (dateListedType.getHearingVenueDay() == null) {
            dateListedType.setHearingVenueDay(hearingType.getHearingVenue());
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
                        sendUpdateSingleCaseECC(authToken, caseDetails, submitEvent.getCaseData(),
                                String.valueOf(submitEvent.getCaseId()));
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
                                         CaseData originalCaseData, String caseIdToLink) {
        try {
            var eccCounterClaimTypeItem = new EccCounterClaimTypeItem();
            var eccCounterClaimType = new EccCounterClaimType();
            eccCounterClaimType.setCounterClaim(currentCaseDetails.getCaseData().getEthosCaseReference());
            eccCounterClaimTypeItem.setId(UUID.randomUUID().toString());
            eccCounterClaimTypeItem.setValue(eccCounterClaimType);
            if (originalCaseData.getEccCases() != null) {
                originalCaseData.getEccCases().add(eccCounterClaimTypeItem);
            } else {
                originalCaseData.setEccCases(
                        new ArrayList<>(Collections.singletonList(eccCounterClaimTypeItem)));
            }
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
