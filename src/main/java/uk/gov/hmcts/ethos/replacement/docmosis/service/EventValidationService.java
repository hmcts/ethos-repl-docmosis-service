package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
}
