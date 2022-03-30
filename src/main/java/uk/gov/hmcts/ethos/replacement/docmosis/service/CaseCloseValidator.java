package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;

@Slf4j
@Service("caseCloseValidator")
public class CaseCloseValidator {

    static final String REINSTATE_CANNOT_CASE_CLOSED_ERROR_MESSAGE = "This case cannot be reinstated with a "
            + "current position of Case closed. Please select a different current position.";
    static final String CLOSING_CASE_WITH_BF_OPEN_ERROR = "This case contains one or more outstanding BFs. "
            + "To enable this case to be closed, please clear the outstanding BFs.";

    public List<String> validateReinstateClosedCaseMidEvent(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (CASE_CLOSED_POSITION.equals(caseData.getPositionType())) {
            errors.add(REINSTATE_CANNOT_CASE_CLOSED_ERROR_MESSAGE);
        }
        return errors;
    }

    public static List<String> validateBfActionsForCaseCloseEvent(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (CollectionUtils.isEmpty(caseData.getBfActions())) {
            return errors;
        }
        for (var currentBFActionTypeItem : caseData.getBfActions()) {
            if (isNullOrEmpty(currentBFActionTypeItem.getValue().getCleared())) {
                errors.add(CLOSING_CASE_WITH_BF_OPEN_ERROR);
                return errors;
            }
        }
        return errors;
    }

}
