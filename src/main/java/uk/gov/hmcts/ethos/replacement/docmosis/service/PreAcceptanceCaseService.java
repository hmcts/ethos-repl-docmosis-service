package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("preAcceptanceCaseService")
public class PreAcceptanceCaseService {
    private static final String ACCEPTED_DATE_SHOULD_NOT_BE_EARLIER_THAN_THE_CASE_RECEIVED_DATE =
            "Accepted date should not be earlier than the case received date";
    private static final String REJECTED_DATE_SHOULD_NOT_BE_EARLIER_THAN_THE_CASE_RECEIVED_DATE =
            "Rejected date should not be earlier than the case received date";
    private static final String CASE_DATA_MISSING = "Case data is missing";
    private static final String PRE_ACCEPT_CASE_MISSING = "Pre-acceptance case data is missing";
    private static final String RECEIPT_DATE_MISSING_OR_INVALID = "Receipt date is missing or invalid";
    private static final String CASE_ACCEPTANCE_STATUS_MISSING = "Case acceptance status is missing";
    private static final String ACCEPTED_DATE_MISSING_OR_INVALID = "Accepted date is missing or invalid";
    private static final String REJECTED_DATE_MISSING_OR_INVALID = "Rejected date is missing or invalid";

    public List<String> validateAcceptanceDate(CaseData caseData) {
        List<String> errors = new ArrayList<>();

        if (caseData == null) {
            errors.add(CASE_DATA_MISSING);
            return errors;
        }

        CasePreAcceptType preAcceptCase = caseData.getPreAcceptCase();
        if (preAcceptCase == null) {
            errors.add(PRE_ACCEPT_CASE_MISSING);
            return errors;
        }

        LocalDate receiptDate;
        try {
            receiptDate = LocalDate.parse(caseData.getReceiptDate());
        } catch (Exception e) {
            errors.add(RECEIPT_DATE_MISSING_OR_INVALID);
            return errors;
        }

        if (isNullOrEmpty(preAcceptCase.getCaseAccepted())) {
            errors.add(CASE_ACCEPTANCE_STATUS_MISSING);
            return errors;
        }

        if (YES.equals(preAcceptCase.getCaseAccepted())) {
            LocalDate dateAccepted;
            try {
                dateAccepted = LocalDate.parse(preAcceptCase.getDateAccepted());
            } catch (Exception e) {
                errors.add(ACCEPTED_DATE_MISSING_OR_INVALID);
                return errors;
            }
            if (dateAccepted.isBefore(receiptDate)) {
                errors.add(ACCEPTED_DATE_SHOULD_NOT_BE_EARLIER_THAN_THE_CASE_RECEIVED_DATE);
            }
        } else if (NO.equals(preAcceptCase.getCaseAccepted())) {
            LocalDate dateRejected;
            try {
                dateRejected = LocalDate.parse(preAcceptCase.getDateRejected());
            } catch (Exception e) {
                errors.add(REJECTED_DATE_MISSING_OR_INVALID);
                return errors;
            }
            if (dateRejected.isBefore(receiptDate)) {
                errors.add(REJECTED_DATE_SHOULD_NOT_BE_EARLIER_THAN_THE_CASE_RECEIVED_DATE);
            }
        }

        return errors;
    }
}
