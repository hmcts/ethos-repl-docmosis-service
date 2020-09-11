package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service("multipleBatchUpdate3Service")
public class MultipleBatchUpdate3Service {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;

    @Autowired
    public MultipleBatchUpdate3Service(CreateUpdatesBusSender createUpdatesBusSender,
                                       UserService userService) {
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
    }

    public void batchUpdate3Logic(String userToken, MultipleDetails multipleDetails,
                                  List<String> errors, TreeMap<String, Object> multipleObjects) {

        log.info("Batch update type = 3");

    }

}
