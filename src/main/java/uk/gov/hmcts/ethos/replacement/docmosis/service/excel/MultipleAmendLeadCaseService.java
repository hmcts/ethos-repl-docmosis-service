package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_IS_NOT_IN_MULTIPLE_ERROR;

@Slf4j
@Service("multipleAmendLeadCaseService")
public class MultipleAmendLeadCaseService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleAmendLeadCaseService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public List<?> bulkAmendLeadCaseLogic(String userToken, MultipleDetails multipleDetails,
                                          List<String> errors, TreeMap<String, Object> multipleObjects) {

        String amendLeadCase  = multipleDetails.getCaseData().getAmendLeadCase();

        if (checkAmendLeadCaseExistsAndIsDifferent(multipleObjects, multipleDetails.getCaseData(), amendLeadCase)) {

            log.info("Send updates to single cases");

            multipleHelperService.sendUpdatesToSinglesLogic(userToken, multipleDetails,
                    errors, amendLeadCase, multipleObjects, new ArrayList<>(Collections.singletonList(amendLeadCase)));

        } else {

            log.info("Case is not part of the multiple");

            errors.add(CASE_IS_NOT_IN_MULTIPLE_ERROR);

        }

        return new ArrayList<>(multipleObjects.values());

    }

    private boolean checkAmendLeadCaseExistsAndIsDifferent(TreeMap<String, Object> multipleObjects,
                                                           MultipleData multipleData, String amendLeadCase) {

        log.info("AmendLeadCase: " + amendLeadCase);

        String oldLeadCase = MultiplesHelper.getCurrentLead(multipleData.getLeadCase());

        return multipleObjects.keySet().stream().anyMatch(amendLeadCase::equalsIgnoreCase)
                && !oldLeadCase.equals(amendLeadCase);

    }

}
