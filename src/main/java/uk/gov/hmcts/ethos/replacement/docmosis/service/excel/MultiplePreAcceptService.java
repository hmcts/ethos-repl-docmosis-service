package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.UPDATING_STATE;

@Slf4j
@Service("multiplePreAcceptService")
public class MultiplePreAcceptService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultiplePreAcceptService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void bulkPreAcceptLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        if (multipleDetails.getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)) {

            multipleDetails.getCaseData().setState(UPDATING_STATE);

            log.info("Send updates to single cases");

            multipleHelperService.sendPreAcceptToSinglesWithConfirmation(userToken, multipleDetails, errors);

        } else {

            log.info("All cases are in Accepted state");

            errors.add("All cases are in Accepted state");

        }

    }

}
