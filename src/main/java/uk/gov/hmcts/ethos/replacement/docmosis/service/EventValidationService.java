package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.HearingType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> validateHearingNumber(CaseData caseData) {

        List<String> errors = new ArrayList<>();

        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {

            String correspondenceHearingNumber = getCorrespondenceHearingNumber(caseData);
            HearingType hearingType = getHearingByNumber(caseData.getHearingCollection(), correspondenceHearingNumber);

            if (hearingType.getHearingNumber() == null || !hearingType.getHearingNumber().equals(correspondenceHearingNumber)) {
                errors.add(HEARING_NUMBER_MISMATCH_ERROR_MESSAGE);
            }
        }
        else {
            errors.add(EMPTY_HEARING_COLLECTION_ERROR_MESSAGE);
        }

        return errors;
    }
}
