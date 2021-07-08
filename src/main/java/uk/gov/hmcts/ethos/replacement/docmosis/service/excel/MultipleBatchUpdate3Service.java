package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import java.util.SortedMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

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
                                  List<String> errors, SortedMap<String, Object> multipleObjects) {

        var multipleData = multipleDetails.getCaseData();

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

            //log.info("Removing caseSearched from filtered cases");

            //multipleObjects.remove(caseSearched.getCaseData().getEthosCaseReference());

            log.info("Sending updates to single cases with caseSearched");
//            if (YES.equals(multipleData.getBatchRemoveRespondentRep())) {
//                caseSearched.getCaseData().setRepresentativeClaimantType(null);
//                caseSearched.getCaseData().setClaimantRepresentedQuestion(NO);
//            }
            multipleHelperService.sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, errors,
                    multipleObjects, caseSearched.getCaseData());
        }
        else {

            log.info("No changes then move to open state");

            multipleData.setState(OPEN_STATE);
        }

    }
    private boolean checkAnyChange(MultipleData multipleData) {

        return (
                (multipleData.getBatchUpdateClaimantRep() != null
                        && !multipleData.getBatchUpdateClaimantRep().getValue().getCode().equals(SELECT_NONE_VALUE))
                        || (multipleData.getBatchUpdateJurisdiction() != null
                        && !multipleData.getBatchUpdateJurisdiction().getValue().getCode().equals(SELECT_NONE_VALUE))
                        || (multipleData.getBatchUpdateRespondent() != null
                        && !multipleData.getBatchUpdateRespondent().getValue().getCode().equals(SELECT_NONE_VALUE))
                        || (multipleData.getBatchUpdateJudgment() != null
                        && !multipleData.getBatchUpdateJudgment().getValue().getCode().equals(SELECT_NONE_VALUE))
                        || (multipleData.getBatchUpdateRespondentRep() != null
                        && !multipleData.getBatchUpdateRespondentRep().getValue().getCode().equals(SELECT_NONE_VALUE)
                        )
        );
    }

}
