package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;

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

        if (caseDetails.getCaseData().getCaseType().equals(MULTIPLE_CASE_TYPE)) {

            errors.add("Case belongs already to a multiple");

            return;

        }

        log.info("Validating multiple and subMultiple in singles");

        String multipleCaseTypeId = MultiplesHelper.getMultipleCaseTypeIdFromSingle(caseDetails.getCaseTypeId());

        String multipleReference = caseDetails.getCaseData().getMultipleReference();

        String subMultipleReference = caseDetails.getCaseData().getSubMultipleReference();

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleCaseTypeId,
                multipleReference,
                subMultipleReference,
                errors);

    }

}
