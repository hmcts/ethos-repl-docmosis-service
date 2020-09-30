package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.List;

@Slf4j
@Service("multiplePreAcceptService")
public class MultiplePreAcceptService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultiplePreAcceptService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void bulkPreAcceptLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Send updates to single cases");

        multipleHelperService.sendPreAcceptToSinglesWithConfirmation(userToken, multipleDetails, errors);

    }

}
