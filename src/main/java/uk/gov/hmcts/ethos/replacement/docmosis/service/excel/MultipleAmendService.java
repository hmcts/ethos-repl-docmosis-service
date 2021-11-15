package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Stream;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADD_CASES_TO_MULTIPLE_AMENDMENT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEAD_CASE_AMENDMENT;

@Slf4j
@Service("multipleAmendService")
public class MultipleAmendService {

    private final ExcelReadingService excelReadingService;
    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleAmendLeadCaseService multipleAmendLeadCaseService;
    private final MultipleAmendCaseIdsService multipleAmendCaseIdsService;

    @Autowired
    public MultipleAmendService(ExcelReadingService excelReadingService,
                                ExcelDocManagementService excelDocManagementService,
                                MultipleAmendLeadCaseService multipleAmendLeadCaseService,
                                MultipleAmendCaseIdsService multipleAmendCaseIdsService) {
        this.excelReadingService = excelReadingService;
        this.excelDocManagementService = excelDocManagementService;
        this.multipleAmendLeadCaseService = multipleAmendLeadCaseService;
        this.multipleAmendCaseIdsService = multipleAmendCaseIdsService;
    }

    public void bulkAmendMultipleLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel to amend multiple");
        SortedMap<String, Object> multipleObjects = excelReadingService.readExcel(
                        userToken, MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors, multipleDetails.getCaseData(), FilterExcelType.ALL);

        List<?> newMultipleObjects = new ArrayList<>();

        if (Stream.of(LEAD_CASE_AMENDMENT, ADD_CASES_TO_MULTIPLE_AMENDMENT)
                .anyMatch(multipleDetails.getCaseData().getTypeOfAmendmentMSL()::contains)) {

            if (multipleDetails.getCaseData().getTypeOfAmendmentMSL().contains(LEAD_CASE_AMENDMENT)) {
                log.info("Amend lead case logic");
                newMultipleObjects = multipleAmendLeadCaseService.bulkAmendLeadCaseLogic(userToken,
                        multipleDetails, errors, multipleObjects);
            }

            if (multipleDetails.getCaseData().getTypeOfAmendmentMSL().contains(ADD_CASES_TO_MULTIPLE_AMENDMENT)
                    && errors.isEmpty()) {

                log.info("Amend case ids logic");
                newMultipleObjects = multipleAmendCaseIdsService.bulkAmendCaseIdsLogic(userToken,
                        multipleDetails, errors, multipleObjects);
            }

            if (errors.isEmpty()) {

                log.info("Create a new Excel");
                excelDocManagementService.generateAndUploadExcel(newMultipleObjects, userToken,
                        multipleDetails);

            }

        }

        log.info("Clearing the payload");

        multipleDetails.getCaseData().setAmendLeadCase(null);
        multipleDetails.getCaseData().setCaseIdCollection(null);
        multipleDetails.getCaseData().setTypeOfAmendmentMSL(null);

    }

}
