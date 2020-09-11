package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service("multipleBatchUpdate1Service")
public class MultipleBatchUpdate1Service {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;

    @Autowired
    public MultipleBatchUpdate1Service(CreateUpdatesBusSender createUpdatesBusSender,
                                       UserService userService) {
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
    }

    public void batchUpdate1Logic(String userToken, MultipleDetails multipleDetails,
                                  List<String> errors, TreeMap<String, Object> multipleObjects) {

        log.info("Batch update type = 1");

        log.info("Send updates to single cases");

        sendUpdatesToSingles(userToken, multipleDetails, errors, multipleObjects);

    }

    private void sendUpdatesToSingles(String userToken, MultipleDetails multipleDetails,
                                      List<String> errors, TreeMap<String, Object> multipleObjects) {

        List<String> multipleObjectsFiltered = new ArrayList<>(multipleObjects.keySet());
        MultipleData multipleData = multipleDetails.getCaseData();
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                username,
                multipleObjectsFiltered,
                PersistentQHelper.getUpdateDataModel(multipleDetails.getCaseData()),
                errors,
                multipleData.getMultipleReference(),
                createUpdatesBusSender,
                String.valueOf(multipleObjectsFiltered.size()));

    }
}
