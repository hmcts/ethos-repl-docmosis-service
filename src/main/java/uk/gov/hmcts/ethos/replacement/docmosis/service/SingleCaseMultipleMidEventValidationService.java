package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.List;

@Slf4j
@Service("singleCaseMultipleDMidEventValidationService")
public class SingleCaseMultipleMidEventValidationService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public SingleCaseMultipleMidEventValidationService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void singleCaseMultipleValidationLogic(String userToken, CaseDetails caseDetails, List<String> errors) {

        log.info("Validating multiple and subMultiple in singles");

        String oldMultipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());

        String multipleCaseType = oldMultipleCaseTypeId.substring(0, oldMultipleCaseTypeId.length() - 1);

        String multipleReference = "";

        String subMultipleReference = "";

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleCaseType,
                multipleReference,
                subMultipleReference,
                errors);

    }

}
