package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;

@Slf4j
@Service("multiplePreAcceptService")
public class MultiplePreAcceptService {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;

    @Autowired
    public MultiplePreAcceptService(CreateUpdatesBusSender createUpdatesBusSender,
                                    UserService userService) {
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
    }

    public void bulkPreAcceptLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Send updates to single cases");

        MultipleData multipleData = multipleDetails.getCaseData();
        List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleData);
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails,
                username,
                ethosCaseRefCollection,
                PersistentQHelper.getPreAcceptDataModel(),
                errors,
                multipleData.getMultipleReference(),
                createUpdatesBusSender,
                String.valueOf(ethosCaseRefCollection.size()));

    }

}
