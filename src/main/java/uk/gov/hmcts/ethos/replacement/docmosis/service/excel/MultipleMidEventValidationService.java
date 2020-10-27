package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;

import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;

@Slf4j
@Service("multipleDMidEventValidationService")
public class MultipleMidEventValidationService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleMidEventValidationService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public void multipleValidationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Validating multiple and subMultiple");

        MultipleData multipleData = multipleDetails.getCaseData();

        MoveCasesType moveCasesType = multipleData.getMoveCases();

        String convertToSingle = moveCasesType.getConvertToSingle();

        if (convertToSingle.equals(NO)) {

            String updatedMultipleRef = moveCasesType.getUpdatedMultipleRef();
            String updatedSubMultipleName = moveCasesType.getUpdatedSubMultipleRef();

            if (updatedMultipleRef.equals(multipleData.getMultipleReference())) {

                multipleHelperService.validateSubMultiple(updatedSubMultipleName,
                        multipleData.getSubMultipleCollection(),
                        errors,
                        updatedMultipleRef);

            } else {

                multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                        multipleDetails.getCaseTypeId(),
                        updatedMultipleRef,
                        updatedSubMultipleName,
                        errors);

            }

        }

    }

}
