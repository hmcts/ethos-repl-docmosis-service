package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("singleCaseMultipleDMidEventValidationService")
public class SingleCaseMultipleMidEventValidationService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public SingleCaseMultipleMidEventValidationService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void singleCaseMultipleValidationLogic(String userToken, CaseDetails caseDetails, List<String> errors) {

        log.info(String.format("Validating if single case %s has the correct case type",
                caseDetails.getCaseData().getEthosCaseReference()));

        if (caseDetails.getCaseData().getCaseType().equals(SINGLE_CASE_TYPE)) {

            if (caseDetails.getCaseData().getMultipleFlag().equals(YES)) {

                log.info(String.format("Case %s belongs to a multiple. It cannot be moved to single",
                        caseDetails.getCaseData().getEthosCaseReference()));

                errors.add("Case belongs to a multiple. It cannot be moved to single");

            } else {

                log.info("No changes. Skip validation for case:" + caseDetails.getCaseData().getEthosCaseReference());

            }

            return;

        }

        if (caseDetails.getCaseData().getMultipleFlag().equals(NO)
                && caseDetails.getCaseData().getCaseType().equals(MULTIPLE_CASE_TYPE)) {

            String multipleReference = caseDetails.getCaseData().getMultipleReference();

            log.info("Validating multiple and subMultiple for multipleReference: " + multipleReference);
            String multipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());
            String subMultipleName = caseDetails.getCaseData().getSubMultipleName();

            multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                    multipleCaseTypeId,
                    multipleReference,
                    subMultipleName,
                    errors);

        }

    }

}
