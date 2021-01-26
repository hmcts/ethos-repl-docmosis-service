package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.List;
import java.util.TreeMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.OPEN_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SELECT_NONE_VALUE;

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
                caseToSearch,
                multipleData.getMultipleSource());

        log.info("Checking if there will be any change. Otherwise moving to open state");

        if (checkAnyChange(multipleData)) {

            log.info("Removing caseSearched from filtered cases");

            multipleObjects.remove(caseSearched.getCaseData().getEthosCaseReference());

            log.info("Sending updates to single cases with caseSearched");

            multipleHelperService.sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, errors,
                    multipleObjects, caseSearched.getCaseData());

        } else {

            log.info("No changes then move to open state");

            multipleData.setState(OPEN_STATE);

        }

    }

    private boolean checkAnyChange(MultipleData multipleData) {

        return (
                (multipleData.getBatchUpdateClaimantRep() != null &&
                        !multipleData.getBatchUpdateClaimantRep().getValue().getCode().equals(SELECT_NONE_VALUE))
                ||
                (multipleData.getBatchUpdateJurisdiction() != null &&
                        !multipleData.getBatchUpdateJurisdiction().getValue().getCode().equals(SELECT_NONE_VALUE))
                ||
                (multipleData.getBatchUpdateRespondent() != null &&
                        !multipleData.getBatchUpdateRespondent().getValue().getCode().equals(SELECT_NONE_VALUE))
        );

    }

}
