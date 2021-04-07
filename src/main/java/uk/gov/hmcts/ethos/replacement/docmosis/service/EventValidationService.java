package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DepositTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DocumentHelper;

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
import static java.util.stream.Collectors.joining;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATED_JURISDICTION_CODES_JUDGEMENT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUPLICATE_JURISDICTION_CODE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EMPTY_HEARING_COLLECTION_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EMPTY_RESPONDENT_COLLECTION_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FUTURE_RECEIPT_DATE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FUTURE_RESPONSE_RECEIVED_DATE_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_NUMBER_MISMATCH_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_DELETED_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.JURISDICTION_CODES_EXISTENCE_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RECEIPT_DATE_LATER_THAN_ACCEPTED_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESP_REP_NAME_MISMATCH_ERROR_MESSAGE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TARGET_HEARING_DATE_INCREMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.getActiveRespondents;

@Slf4j
@Service("eventValidationService")
public class EventValidationService {

    public List<String> validateReceiptDate(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        LocalDate dateOfReceipt = LocalDate.parse(caseData.getReceiptDate());
        if (caseData.getPreAcceptCase() != null && !isNullOrEmpty(caseData.getPreAcceptCase().getDateAccepted())) {
            LocalDate dateAccepted = LocalDate.parse(caseData.getPreAcceptCase().getDateAccepted());
            if (dateOfReceipt.isAfter(dateAccepted)) {
                errors.add(RECEIPT_DATE_LATER_THAN_ACCEPTED_ERROR_MESSAGE);
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

    public List<String> validateReceiptDateMultiple(MultipleData multipleData) {
        List<String> errors = new ArrayList<>();
        if (!isNullOrEmpty(multipleData.getReceiptDate())) {
            LocalDate dateOfReceipt = LocalDate.parse(multipleData.getReceiptDate());
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
                RespondentSumType respondentSumType = itr.next().getValue();
                validateResponseReceivedDateDate(respondentSumType, errors, index);
                validateResponseReturnedFromJudgeDate(respondentSumType, errors, index);
            }
        }
        return errors;
    }

    public List<String> validateRespRepNames(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (caseData.getRepCollection() != null && !caseData.getRepCollection().isEmpty()) {
            ListIterator<RepresentedTypeRItem> repItr = caseData.getRepCollection().listIterator();
            int index;
            while (repItr.hasNext()) {
                index = repItr.nextIndex() + 1;
                String respRepName = repItr.next().getValue().getRespRepName();
                if (!isNullOrEmpty(respRepName)) {
                    if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
                        ListIterator<RespondentSumTypeItem> respItr = caseData.getRespondentCollection().listIterator();
                        boolean validLink = false;
                        while (respItr.hasNext()) {
                            RespondentSumType respondentSumType = respItr.next().getValue();
                            if ((respRepName.equals(respondentSumType.getRespondentName()))
                                    || (respondentSumType.getResponseRespondentName() != null
                                    && respRepName.equals(respondentSumType.getResponseRespondentName()))) {
                                validLink = true;
                                break;
                            }
                        }
                        if (!validLink) {
                            errors.add(RESP_REP_NAME_MISMATCH_ERROR_MESSAGE + " - " + index);
                        }
                    }
                }
            }
        }
        return errors;
    }

    public List<String> validateHearingNumber(CaseData caseData, CorrespondenceType correspondenceType,
                                              CorrespondenceScotType correspondenceScotType) {
        List<String> errors = new ArrayList<>();
        String correspondenceHearingNumber = DocumentHelper.getCorrespondenceHearingNumber(
                correspondenceType, correspondenceScotType);
        if (correspondenceHearingNumber != null) {
            if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
                HearingType hearingType = DocumentHelper.getHearingByNumber(
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
        List<String> jurCodesCollection = getJurCodesCollection(caseData.getJurCodesCollection());
        if (caseData.getJudgementCollection() != null && !caseData.getJudgementCollection().isEmpty()) {
            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                jurCodesCollectionWithinJudgement.addAll(
                        getJurCodesCollection(judgementTypeItem.getValue().getJurisdictionCodes()));
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
            int counter = 0;
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

    public List<String> validateJurisdictionOutcome(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (caseData.getJurCodesCollection() != null && !caseData.getJurCodesCollection().isEmpty()) {
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                JurCodesType jurCodesType = jurCodesTypeItem.getValue();
                if (jurCodesType.getJudgmentOutcome() == null) {
                    errors.add(MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE);
                    break;
                }
            }
        } else {
            errors.add(MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE);
        }
        return errors;
    }

    private void validateResponseReturnedFromJudgeDate(RespondentSumType respondentSumType, List<String> errors,
                                                       int index) {
        if (respondentSumType.getResponse_ReferredToJudge() != null
                && respondentSumType.getResponseReturnedFromJudge() != null) {
            LocalDate responseReferredToJudge = LocalDate.parse(respondentSumType.getResponse_ReferredToJudge());
            LocalDate responseReturnedFromJudge = LocalDate.parse(respondentSumType.getResponseReturnedFromJudge());
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
            LocalDate responseReceivedDate = LocalDate.parse(respondentSumType.getResponseReceivedDate());
            if (responseReceivedDate.isAfter(LocalDate.now())) {
                String respondentName = respondentSumType.getRespondentName() != null
                        ? respondentSumType.getRespondentName()
                        : "missing name";
                errors.add(FUTURE_RESPONSE_RECEIVED_DATE_ERROR_MESSAGE + " for respondent "
                        + index + " (" + respondentName + ")");
            }
        }
    }

    private List<String> getJurCodesCollection(List<JurCodesTypeItem> jurCodesCollection) {
        return jurCodesCollection != null
                ? jurCodesCollection.stream()
                .map(jurCodesTypeItem -> jurCodesTypeItem.getValue().getJuridictionCodesList())
                .collect(Collectors.toList())
                : new ArrayList<>();
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
                .collect(Collectors.toList());
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
        if (caseData.getJudgementCollection() != null && !caseData.getJudgementCollection().isEmpty()) {

            List<String> jurCodesCollection = getJurCodesCollection(caseData.getJurCodesCollection());
            List<String> jurCodesDoesNotExist = new ArrayList<>();
            Map<String, List<String>> duplicatedJurCodesMap = new HashMap<>();

            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                JudgementType judgementType = judgementTypeItem.getValue();
                List<String> jurCodesCollectionWithinJudgement =
                        getJurCodesCollection(judgementType.getJurisdictionCodes());

                log.info("Check if jurCodes collection within judgement exist in jurCodesCollection");
                jurCodesDoesNotExist.addAll(jurCodesCollectionWithinJudgement.stream()
                        .filter(element -> !jurCodesCollection.contains(element))
                        .collect(Collectors.toList()));

                log.info("Check if jurCodes collection has duplicates");
                populateJurCodesDuplicatedWithinJudgement(jurCodesCollectionWithinJudgement,
                        duplicatedJurCodesMap,
                        judgementType.getJudgementType());
            }

            getJurisdictionCodesErrors(errors, jurCodesDoesNotExist, duplicatedJurCodesMap);
        }
        return errors;
    }

    public List<String> validateDepositRefunded(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (caseData.getDepositCollection() != null && !caseData.getDepositCollection().isEmpty()) {

            for (DepositTypeItem depositTypeItem : caseData.getDepositCollection()) {
                if (!isNullOrEmpty(depositTypeItem.getValue().getDepositAmountRefunded())) {
                    if (isNullOrEmpty(depositTypeItem.getValue().getDepositAmount())
                            || Integer.parseInt(depositTypeItem.getValue().getDepositAmountRefunded())
                            > Integer.parseInt(depositTypeItem.getValue().getDepositAmount())) {
                        errors.add(DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR);
                    }
                }
            }
        }
        return errors;
    }

}
