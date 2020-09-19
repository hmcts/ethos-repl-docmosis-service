package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.types.SingleMoveCasesType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
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

        SingleMoveCasesType singleMoveCasesType = caseDetails.getCaseData().getMoveCases();

        log.info("Validating multiple and subMultiple in singles");

        log.info("---------- Content --------- : " + caseDetails.getCaseData());

        String multipleCaseTypeId = MultiplesHelper.getMultipleCaseTypeIdFromSingle(caseDetails.getCaseTypeId());

        log.info("SingleMoveCasesType: " + singleMoveCasesType);

        String multipleReference = singleMoveCasesType.getUpdatedMultipleRef();

        String subMultipleReference = singleMoveCasesType.getUpdatedSubMultipleName();

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleCaseTypeId,
                multipleReference,
                subMultipleReference,
                errors);

    }

}
