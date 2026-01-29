package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DocumentHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.joining;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSING_LISTED_CASE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATED_JURISDICTION_CODES_JUDGEMENT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EMPTY_HEARING_COLLECTION_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EMPTY_RESPONDENT_COLLECTION_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FUTURE_RECEIPT_DATE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FUTURE_RESPONSE_RECEIVED_DATE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_NUMBER_MISMATCH_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INVALID_LISTING_DATE_RANGE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_DELETED_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_EXISTENCE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JUDGEMENT_JURISDICTION_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JURISDICTION_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NOT_ALLOCATED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RECEIPT_DATE_LATER_THAN_ACCEPTED_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESP_REP_NAME_MISMATCH_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TARGET_HEARING_DATE_INCREMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.getActiveRespondents;

@Slf4j
@Service("eventValidationService")
public class EventValidationService {

    private static final List<String> INVALID_STATES_FOR_CLOSED_CURRENT_POSITION = List.of(
            SUBMITTED_STATE, ACCEPTED_STATE, REJECTED_STATE);
    public static final String RECEIPT_DATE_LATER_THAN_REJECTED_ERROR_MESSAGE =
            "Receipt date should not be later than rejected date";
    public static final String NO_HEARINGS_LISTED_ERROR_MESSAGE =
            "You can not Allocate/Update Hearings as there are no hearings listed. "
                    + "Use the 'List hearing' event to add new ones.";

    private boolean isReceiptDateEarlier(String date, String error, List<String> errors, LocalDate dateOfReceipt) {
        if (Strings.isNullOrEmpty(date)) {
            return false;
        }
        if (dateOfReceipt.isAfter(LocalDate.parse(date))) {
            errors.add(error);
            return true;
        }
        return false;
    }

    public List<String> validateReceiptDate(CaseDetails caseDetails) {
        List<String> errors = new ArrayList<>();
        CaseData caseData = caseDetails.getCaseData();
        LocalDate dateOfReceipt = LocalDate.parse(caseData.getReceiptDate());
        if (caseData.getPreAcceptCase() != null) {
            if (ACCEPTED_STATE.equals(caseDetails.getState())
                    && isReceiptDateEarlier(caseData.getPreAcceptCase().getDateAccepted(),
                    RECEIPT_DATE_LATER_THAN_ACCEPTED_ERROR_MESSAGE, errors, dateOfReceipt)) {
                return errors;
            }
            if (REJECTED_STATE.equals(caseDetails.getState())
                    && isReceiptDateEarlier(caseData.getPreAcceptCase().getDateRejected(),
                    RECEIPT_DATE_LATER_THAN_REJECTED_ERROR_MESSAGE, errors, dateOfReceipt)) {
                return errors;
            }
        }
        if (dateOfReceipt.isAfter(LocalDate.now())) {
            errors.add(FUTURE_RECEIPT_DATE_ERROR_MESSAGE);
        } else {
            caseData.setTargetHearingDate(dateOfReceipt.plusDays(TARGET_HEARING_DATE_INCREMENT).toString());
        }
        return errors;
    }

    public boolean validateCaseState(CaseDetails caseDetails) {
        var validated = true;
        log.info("Checking whether the case " + caseDetails.getCaseData().getEthosCaseReference()
                + " is in accepted state");
        if (caseDetails.getState().equals(SUBMITTED_STATE)
                && caseDetails.getCaseData().getEcmCaseType().equals(MULTIPLE_CASE_TYPE)) {
            validated = false;
        }
        return validated;
    }

    public boolean validateCurrentPosition(CaseDetails caseDetails) {
        return !(CASE_CLOSED_POSITION.equals(caseDetails.getCaseData().getPositionType())
                && INVALID_STATES_FOR_CLOSED_CURRENT_POSITION.contains(caseDetails.getState()));
    }

    public List<String> validateReceiptDateMultiple(MultipleData multipleData) {
        List<String> errors = new ArrayList<>();
        if (!isNullOrEmpty(multipleData.getReceiptDate())) {
            var dateOfReceipt = LocalDate.parse(multipleData.getReceiptDate());
            if (dateOfReceipt.isAfter(LocalDate.now())) {
                errors.add(FUTURE_RECEIPT_DATE_ERROR_MESSAGE);
            }
        }
        return errors;
    }

    public List<String> validateActiveRespondents(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (getActiveRespondents(caseData).isEmpty()) {
            errors.add(EMPTY_RESPONDENT_COLLECTION_ERROR_MESSAGE);
        }
        return errors;
    }

    public List<String> validateET3ResponseFields(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();
            while (itr.hasNext()) {
                int index = itr.nextIndex() + 1;
                var respondentSumType = itr.next().getValue();
                validateResponseReceivedDateDate(respondentSumType, errors, index);
                validateResponseReturnedFromJudgeDate(respondentSumType, errors, index);
            }
        }
        return errors;
    }

    public List<String> validateAndSetRespRepNames(uk.gov.hmcts.ecm.common.model.ccd.CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())
            && CollectionUtils.isNotEmpty(caseData.getRepCollection())) {
            List<RepresentedTypeRItem> repCollection = caseData.getRepCollection();
            List<RepresentedTypeRItem> updatedRepList = new ArrayList<>();
            int repCollectionSize = caseData.getRepCollection().size();

            //reverse update it - from the last to the first element by removing repetition
            for (int index = repCollectionSize - 1;  index > -1; index--) {
                String tempCollCurrentName = repCollection.get(index).getValue()
                        .getDynamicRespRepName().getValue().getLabel();
                if (isValidRespondentName(caseData, tempCollCurrentName)) {
                    if (!repCollection.isEmpty()
                        && updatedRepList.stream()
                        .noneMatch(r -> r.getValue().getDynamicRespRepName().getValue().getLabel()
                            .equals(tempCollCurrentName))) {
                        repCollection.get(index).getValue().setRespRepName(tempCollCurrentName);
                        updatedRepList.add(repCollection.get(index));
                    }
                } else {
                    errors.add(RESP_REP_NAME_MISMATCH_ERROR_MESSAGE + " - " + tempCollCurrentName);
                    return errors;
                }
            }

            //clear the old rep collection
            if (repCollectionSize > 0) {
                caseData.getRepCollection().subList(0, repCollectionSize).clear();
            }

            //populate the rep collection with the new & updated rep entries and
            //sort the collection by respondent name
            updatedRepList.sort((o1, o2) -> o1.getValue().getRespRepName().compareTo(o2.getValue().getRespRepName()));
            caseData.setRepCollection(updatedRepList);
        }

        return errors;
    }

    private boolean isValidRespondentName(uk.gov.hmcts.ecm.common.model.ccd.CaseData caseData,
                                          String tempCollCurrentName) {
        boolean isValidName = false;
        if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())) {
            var respRepNames = caseData.getRespondentCollection()
                .stream()
                .map(e -> e.getValue().getRespondentName())
                .toList();

            if (!respRepNames.isEmpty()) {
                isValidName = respRepNames.contains(tempCollCurrentName);
            }
        }

        return isValidName;
    }

    public List<String> validateHearingNumber(CaseData caseData, CorrespondenceType correspondenceType,
                                              CorrespondenceScotType correspondenceScotType) {
        List<String> errors = new ArrayList<>();
        String correspondenceHearingNumber = DocumentHelper.getCorrespondenceHearingNumber(
                correspondenceType, correspondenceScotType);
        if (correspondenceHearingNumber != null) {
            if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
                var hearingType = DocumentHelper.getHearingByNumber(
                        caseData.getHearingCollection(), correspondenceHearingNumber);
                if (hearingType.getHearingNumber() == null
                        || !hearingType.getHearingNumber().equals(correspondenceHearingNumber)) {
                    errors.add(HEARING_NUMBER_MISMATCH_ERROR_MESSAGE);
                }
            } else {
                errors.add(EMPTY_HEARING_COLLECTION_ERROR_MESSAGE);
            }
        }
        return errors;
    }

    public void validateJurisdictionCodes(CaseData caseData, List<String> errors) {
        validateDuplicatedJurisdictionCodes(caseData, errors);
        validateJurisdictionCodesExistenceInJudgement(caseData, errors);
    }

    private void validateJurisdictionCodesExistenceInJudgement(CaseData caseData, List<String> errors) {

        Set<String> jurCodesCollectionWithinJudgement = new HashSet<>();
        List<String> jurCodesCollection = Helper.getJurCodesCollection(caseData.getJurCodesCollection());
        if (caseData.getJudgementCollection() != null && !caseData.getJudgementCollection().isEmpty()) {
            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                jurCodesCollectionWithinJudgement.addAll(
                        Helper.getJurCodesCollection(judgementTypeItem.getValue().getJurisdictionCodes()));
            }
        }
        log.info("Check if all jurCodesCollectionWithinJudgement are in jurCodesCollection");
        Set<String> result = jurCodesCollectionWithinJudgement.stream()
                .distinct()
                .filter(jurCode -> !jurCodesCollection.contains(jurCode))
                .collect(Collectors.toSet());

        if (!result.isEmpty()) {
            log.info("jurCodesCollectionWithinJudgement are not in jurCodesCollection: " + result);
            errors.add(JURISDICTION_CODES_DELETED_ERROR + result);
        }
    }

    private void validateDuplicatedJurisdictionCodes(CaseData caseData, List<String> errors) {
        if (caseData.getJurCodesCollection() != null && !caseData.getJurCodesCollection().isEmpty()) {
            var counter = 0;
            Set<String> uniqueCodes = new HashSet<>();
            List<String> duplicateCodes = new ArrayList<>();
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                counter++;
                String code = jurCodesTypeItem.getValue().getJuridictionCodesList();
                if (!uniqueCodes.add(code)) {
                    duplicateCodes.add(" \"" + code + "\" " + "in Jurisdiction" + " " + counter + " ");
                }
            }
            if (!duplicateCodes.isEmpty()) {
                errors.add(DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE + StringUtils.join(duplicateCodes, '-'));
            }
        }
    }

    private String getJurisdictionOutcomeNotAllocatedErrorText(boolean partOfMultiple,
                                                               String ethosReference) {
        if (partOfMultiple) {
            return ethosReference + " - " + JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE;
        }
        return JURISDICTION_OUTCOME_NOT_ALLOCATED_ERROR_MESSAGE;
    }

    private String getJurisdictionOutcomeErrorText(boolean partOfMultiple, boolean hasJurisdictions,
                                                   String ethosReference) {
        if (partOfMultiple) {
            if (hasJurisdictions) {
                return ethosReference + " - " + MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE;
            }
            return ethosReference + " - " + MISSING_JURISDICTION_MESSAGE;
        }

        if (hasJurisdictions) {
            return MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE;
        }
        return MISSING_JURISDICTION_MESSAGE;
    }

    private void validateResponseReturnedFromJudgeDate(RespondentSumType respondentSumType, List<String> errors,
                                                       int index) {
        if (respondentSumType.getResponse_ReferredToJudge() != null
                && respondentSumType.getResponseReturnedFromJudge() != null) {
            var responseReferredToJudge = LocalDate.parse(respondentSumType.getResponse_ReferredToJudge());
            var responseReturnedFromJudge = LocalDate.parse(respondentSumType.getResponseReturnedFromJudge());
            if (responseReturnedFromJudge.isBefore(responseReferredToJudge)) {
                String respondentName = respondentSumType.getRespondentName() != null
                        ? respondentSumType.getRespondentName()
                        : "missing name";
                errors.add(EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE + " for respondent "
                        + index + " (" + respondentName + ")");
            }
        }
    }

    private void validateResponseReceivedDateDate(RespondentSumType respondentSumType, List<String> errors, int index) {
        if (respondentSumType.getResponseReceivedDate() != null) {
            var responseReceivedDate = LocalDate.parse(respondentSumType.getResponseReceivedDate());
            if (responseReceivedDate.isAfter(LocalDate.now())) {
                String respondentName = respondentSumType.getRespondentName() != null
                        ? respondentSumType.getRespondentName()
                        : "missing name";
                errors.add(FUTURE_RESPONSE_RECEIVED_DATE_ERROR_MESSAGE + " for respondent "
                        + index + " (" + respondentName + ")");
            }
        }
    }

    private void populateJurCodesDuplicatedWithinJudgement(List<String> jurCodesCollectionWithinJudgement,
                                                           Map<String, List<String>> duplicatedJurCodesMap,
                                                           String key) {
        List<String> duplicatedJurCodes = jurCodesCollectionWithinJudgement.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(element -> element.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();
        if (!duplicatedJurCodes.isEmpty()) {
            duplicatedJurCodesMap.put(key, duplicatedJurCodes);
        }
    }

    private void getJurisdictionCodesErrors(List<String> errors, List<String> jurCodesDoesNotExist,
                                            Map<String, List<String>> duplicatedJurCodesMap) {
        if (!jurCodesDoesNotExist.isEmpty()) {
            errors.add(JURISDICTION_CODES_EXISTENCE_ERROR + String.join(", ", jurCodesDoesNotExist));
        }
        if (!duplicatedJurCodesMap.isEmpty()) {
            String duplicates = duplicatedJurCodesMap.entrySet()
                    .stream()
                    .map(e -> e.getKey() + " - " + e.getValue())
                    .collect(joining(" & "));
            errors.add(DUPLICATED_JURISDICTION_CODES_JUDGEMENT_ERROR + duplicates);
        }
    }

    public List<String> validateJurisdictionCodesWithinJudgement(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(caseData.getJudgementCollection())) {

            List<String> jurCodesCollection = Helper.getJurCodesCollection(caseData.getJurCodesCollection());
            List<String> jurCodesDoesNotExist = new ArrayList<>();
            Map<String, List<String>> duplicatedJurCodesMap = new HashMap<>();

            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                var judgementType = judgementTypeItem.getValue();
                List<String> jurCodesCollectionWithinJudgement =
                        Helper.getJurCodesCollection(judgementType.getJurisdictionCodes());

                log.info("Check if jurCodes collection within judgement exist in jurCodesCollection");
                jurCodesDoesNotExist.addAll(jurCodesCollectionWithinJudgement.stream()
                        .filter(element -> !jurCodesCollection.contains(element))
                        .toList());

                log.info("Check if jurCodes collection has duplicates");
                populateJurCodesDuplicatedWithinJudgement(jurCodesCollectionWithinJudgement,
                        duplicatedJurCodesMap,
                        judgementType.getJudgementType());

            }
            getJurisdictionCodesErrors(errors, jurCodesDoesNotExist, duplicatedJurCodesMap);
        }
        return errors;
    }

    public List<String> validateJudgementDates(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        log.info("Check if dates are not in future for case: " + caseData.getEthosCaseReference());
        if (CollectionUtils.isNotEmpty(caseData.getJudgementCollection())) {
            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                var judgementType = judgementTypeItem.getValue();
                if (LocalDate.parse(judgementType.getDateJudgmentMade()).isAfter(LocalDate.now())) {
                    errors.add("Date of Judgement Made can't be in future");
                }
                if (LocalDate.parse(judgementType.getDateJudgmentSent()).isAfter(LocalDate.now())) {
                    errors.add("Date of Judgement Sent can't be in future");
                }
            }
        }
        return errors;
    }

    public void validateJudgementsHasJurisdiction(CaseData caseData, boolean partOfMMultiple, List<String> errors) {
        if (CollectionUtils.isEmpty(caseData.getJudgementCollection())) {
            return;
        }

        for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
            var judgementType = judgementTypeItem.getValue();
            if (CollectionUtils.isEmpty(judgementType.getJurisdictionCodes())) {
                if (partOfMMultiple) {
                    errors.add(caseData.getEthosCaseReference() + " - " + MISSING_JUDGEMENT_JURISDICTION_MESSAGE);
                } else {
                    errors.add(MISSING_JUDGEMENT_JURISDICTION_MESSAGE);
                }
                break;
            }
        }
    }

    public List<String> validateListingDateRange(String listingFrom, String listingTo) {

        List<String> errors = new ArrayList<>();
        if (listingFrom != null && listingTo != null) {
            var startDate = LocalDate.parse(listingFrom);
            var endDate = LocalDate.parse(listingTo);
            var numberOfDays = DAYS.between(startDate, endDate);
            if (numberOfDays > 30) {
                errors.add(INVALID_LISTING_DATE_RANGE_ERROR_MESSAGE);
            }
        }
        return errors;
    }

    public void validateHearingStatusForCaseCloseEvent(CaseData caseData, List<String> errors) {
        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            return;
        }

        for (var currentHearingTypeItem : caseData.getHearingCollection()) {
            for (var currentDateListedTypeItem : currentHearingTypeItem.getValue().getHearingDateCollection()) {
                if (HEARING_STATUS_LISTED.equals(currentDateListedTypeItem.getValue().getHearingStatus())) {
                    errors.add(CLOSING_LISTED_CASE_ERROR);
                    return;
                }
            }
        }
    }

    public List<String> validateHearingsForAllocationOrUpdate(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (caseData != null && (CollectionUtils.isEmpty(caseData.getHearingCollection()))) {
            errors.add(NO_HEARINGS_LISTED_ERROR_MESSAGE);
            return errors;
        }
        return errors;
    }

    public void validateHearingJudgeAllocationForCaseCloseEvent(CaseData caseData, List<String> errors) {

        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            return;
        }

        for (var currentHearingTypeItem : caseData.getHearingCollection()) {
            for (var currentDateListedTypeItem : currentHearingTypeItem.getValue().getHearingDateCollection()) {
                if (HEARING_STATUS_HEARD.equals(currentDateListedTypeItem.getValue().getHearingStatus())
                        && currentHearingTypeItem.getValue().getJudge() == null) {
                    errors.add(CLOSING_HEARD_CASE_WITH_NO_JUDGE_ERROR);
                    return;
                }
            }
        }
    }

    public List<String> validateCaseBeforeCloseEvent(CaseData caseData, boolean isRejected, boolean partOfMultiple,
                                                     List<String> errors) {
        validateJurisdictionOutcome(caseData, isRejected, partOfMultiple, errors);
        validateJudgementsHasJurisdiction(caseData, partOfMultiple, errors);
        validateHearingStatusForCaseCloseEvent(caseData, errors);
        validateHearingJudgeAllocationForCaseCloseEvent(caseData, errors);
        errors.addAll(CaseCloseValidator.validateBfActionsForCaseCloseEvent(caseData));
        return errors;
    }

    public void validateJurisdictionOutcome(CaseData caseData, boolean isRejected, boolean partOfMultiple,
                                            List<String> errors) {
        if (caseData.getJurCodesCollection() != null && !caseData.getJurCodesCollection().isEmpty()) {
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                var jurCodesType = jurCodesTypeItem.getValue();
                if (jurCodesType.getJudgmentOutcome() == null) {
                    errors.add(getJurisdictionOutcomeErrorText(partOfMultiple, true,
                            caseData.getEthosCaseReference()));
                    return;
                } else if (NOT_ALLOCATED.equals(jurCodesType.getJudgmentOutcome())) {
                    errors.add(getJurisdictionOutcomeNotAllocatedErrorText(partOfMultiple,
                            caseData.getEthosCaseReference()));
                }
            }
        } else if (!isRejected) {
            errors.add(getJurisdictionOutcomeErrorText(partOfMultiple, false,
                    caseData.getEthosCaseReference()));
        }
    }

    public void validateRestrictedReportingNames(CaseData caseData) {
        if (caseData.getRestrictedReporting() != null) {
            var restrictedReportingType = caseData.getRestrictedReporting();
            var dynamicListCode = restrictedReportingType.getDynamicRequestedBy().getValue().getCode();
            if (dynamicListCode.startsWith("R: ")) {
                restrictedReportingType.setRequestedBy(RESPONDENT_TITLE);
            } else if (dynamicListCode.startsWith("C: ")) {
                restrictedReportingType.setRequestedBy(CLAIMANT_TITLE);
            } else {
                restrictedReportingType.setRequestedBy(dynamicListCode);
            }
        }
    }
}
