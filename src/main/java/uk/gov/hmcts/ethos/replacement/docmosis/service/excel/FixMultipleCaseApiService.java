package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.List;
import java.util.SortedMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;

@Slf4j
@Service("fixMultipleCaseApi")
public class FixMultipleCaseApiService {
    private final MultipleHelperService multipleHelperService;
    private final ExcelReadingService excelReadingService;

    @Autowired
    public FixMultipleCaseApiService(MultipleHelperService multipleHelperService,
                                     ExcelReadingService excelReadingService) {
        this.multipleHelperService = multipleHelperService;
        this.excelReadingService = excelReadingService;
    }

    public void fixMultipleCase(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel to update logic");
        SortedMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        multipleHelperService.sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, errors,
                multipleObjects, null);

        log.info("Resetting FixCase field");
        multipleDetails.getCaseData().setIsFixCase(NO);
    }
}
