package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service("multipleBatchUpdate1Service")
public class MultipleBatchUpdate1Service {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleBatchUpdate1Service(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void batchUpdate1Logic(String userToken, MultipleDetails multipleDetails,
                                  List<String> errors, TreeMap<String, Object> multipleObjects) {

        log.info("Batch update type = 1");

        log.info("Sending updates to single cases without caseSearched");

        multipleHelperService.sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, errors,
                multipleObjects, null);

    }

}
