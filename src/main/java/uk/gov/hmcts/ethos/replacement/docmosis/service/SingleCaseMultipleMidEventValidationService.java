package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("singleCaseMultipleDMidEventValidationService")
public class SingleCaseMultipleMidEventValidationService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public SingleCaseMultipleMidEventValidationService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void singleCaseMultipleValidationLogic(String userToken, CaseDetails caseDetails, List<String> errors) {

        log.info("Validating if single case has the correct case type");

        if (caseDetails.getCaseData().getCaseType().equals(SINGLE_CASE_TYPE)) {

            if (caseDetails.getCaseData().getMultipleFlag().equals(YES)) {

                log.info("Case belongs to a multiple. It cannot be moved to single");

                errors.add("Case belongs to a multiple. It cannot be moved to single");

            } else {

                log.info("No changes. Skip validation");

            }

            return;

        }

        if (caseDetails.getCaseData().getMultipleFlag().equals(NO)
                && caseDetails.getCaseData().getCaseType().equals(MULTIPLE_CASE_TYPE)) {

            log.info("Validating multiple and subMultiple in singles");

            String multipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());

            String multipleReference = caseDetails.getCaseData().getMultipleReference();

            String subMultipleName = caseDetails.getCaseData().getSubMultipleName();

            log.info("MultipleReference: " + multipleReference);

            multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                    multipleCaseTypeId,
                    multipleReference,
                    subMultipleName,
                    errors);

        }

    }

}
