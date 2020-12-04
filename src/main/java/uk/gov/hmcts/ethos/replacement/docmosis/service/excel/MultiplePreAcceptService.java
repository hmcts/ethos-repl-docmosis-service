package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("multiplePreAcceptService")
public class MultiplePreAcceptService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultiplePreAcceptService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void bulkPreAcceptLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        if (multipleDetails.getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)
                && !checkPreAcceptAlreadyDone(multipleDetails.getCaseData())) {

            multipleDetails.getCaseData().setState(UPDATING_STATE);

            multipleDetails.getCaseData().setPreAcceptDone(YES);

            log.info("Send updates to single cases");

            multipleHelperService.sendPreAcceptToSinglesWithConfirmation(userToken, multipleDetails, errors);

        } else {

            log.info("All cases are in Accepted state");

            errors.add("All cases are in Accepted state");

        }

    }

    private boolean checkPreAcceptAlreadyDone(MultipleData multipleData) {

        return multipleData.getPreAcceptDone() != null && multipleData.getPreAcceptDone().equals(YES);

    }

}
