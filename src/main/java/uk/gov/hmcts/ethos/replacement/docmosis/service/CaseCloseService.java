package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;

@Slf4j
@Service("caseCloseService")
public class CaseCloseService {

    static final String REINSTATE_CANNOT_CASE_CLOSED_ERROR_MESSAGE = "This case cannot be reinstated with a "
            + "current position of Case closed. Please select a different current position.";

    public List<String> validateReinstateClosedCaseMidEvent(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (CASE_CLOSED_POSITION.equals(caseData.getPositionType())) {
            errors.add(REINSTATE_CANNOT_CASE_CLOSED_ERROR_MESSAGE);
        }
        return errors;
    }

}
