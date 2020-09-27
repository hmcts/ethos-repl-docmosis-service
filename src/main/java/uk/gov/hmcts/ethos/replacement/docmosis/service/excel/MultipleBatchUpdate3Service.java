package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service("multipleBatchUpdate3Service")
public class MultipleBatchUpdate3Service {

    private final MultipleHelperService multipleHelperService;
    private final SingleCasesReadingService singleCasesReadingService;

    @Autowired
    public MultipleBatchUpdate3Service(MultipleHelperService multipleHelperService,
                                       SingleCasesReadingService singleCasesReadingService) {
        this.multipleHelperService = multipleHelperService;
        this.singleCasesReadingService = singleCasesReadingService;
    }

    public void batchUpdate3Logic(String userToken, MultipleDetails multipleDetails,
                                  List<String> errors, TreeMap<String, Object> multipleObjects) {

        MultipleData multipleData = multipleDetails.getCaseData();

        log.info("Batch update type = 3");

        String caseToSearch = multipleData.getBatchUpdateCase();

        log.info("Getting the information from: " + caseToSearch);

        SubmitEvent caseSearched = singleCasesReadingService.retrieveSingleCase(
                userToken,
                multipleDetails.getCaseTypeId(),
                caseToSearch);

        log.info("Sending updates to single cases with caseSearched");

        multipleHelperService.sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, errors,
                multipleObjects, caseSearched.getCaseData());

    }

}
