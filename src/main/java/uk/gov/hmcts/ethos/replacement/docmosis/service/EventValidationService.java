package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentSumType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.*;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Service("eventValidationService")
public class EventValidationService {

    public List<String> validateReceiptDate(CaseData caseData) {

        List<String> errors = new ArrayList<>();

        LocalDate dateOfReceipt = LocalDate.parse(caseData.getReceiptDate());

        if (dateOfReceipt.isAfter(LocalDate.now())) {
            errors.add(FUTURE_RECEIPT_DATE_ERROR_MESSAGE);
        }
        else{
            caseData.setTargetHearingDate(dateOfReceipt.plusDays(TARGET_HEARING_DATE_INCREMENT).toString());
        }

        return errors;
    }

    public List<String> validateReturnedFromJudgeDate(CaseData caseData) {

        List<String> errors = new ArrayList<>();

        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {

            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();

            while (itr.hasNext()) {

                int index = itr.nextIndex() + 1;

                RespondentSumType respondentSumType = itr.next().getValue();

                if (respondentSumType.getResponseReturnedFromJudge() != null && respondentSumType.getResponse_ReferredToJudge() != null) {

                    LocalDate responseReferredToJudge = LocalDate.parse(respondentSumType.getResponse_ReferredToJudge());
                    LocalDate responseReturnedFromJudge = LocalDate.parse(respondentSumType.getResponseReturnedFromJudge());

                    String respondentName = respondentSumType.getRespondentName() != null ? respondentSumType.getRespondentName() : "missing name";

                    if (responseReturnedFromJudge.isBefore(responseReferredToJudge)) {
                        errors.add(EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE + " for respondent " + index + " (" + respondentName + ")");
                    }
                }
            }
        }

        return errors;
    }

    public List<String> validateHearingNumber(CaseData caseData) {

        List<String> errors = new ArrayList<>();

        String correspondenceHearingNumber = getCorrespondenceHearingNumber(caseData);

        if(correspondenceHearingNumber != null) {

            if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {

                HearingType hearingType = getHearingByNumber(caseData.getHearingCollection(), correspondenceHearingNumber);

                if (hearingType.getHearingNumber() == null || !hearingType.getHearingNumber().equals(correspondenceHearingNumber)) {
                    errors.add(HEARING_NUMBER_MISMATCH_ERROR_MESSAGE);
                }
            }
            else {
                errors.add(EMPTY_HEARING_COLLECTION_ERROR_MESSAGE);
            }
        }

        return errors;
    }
}
