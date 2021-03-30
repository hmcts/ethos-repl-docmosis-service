package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.TYPE_AMENDMENT_ADDITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TYPE_AMENDMENT_LEAD;

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

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        List<?> newMultipleObjects = new ArrayList<>();

        if (Stream.of(TYPE_AMENDMENT_LEAD, TYPE_AMENDMENT_ADDITION)
                .anyMatch(multipleDetails.getCaseData().getTypeOfAmendmentMSL()::contains)) {

            if (multipleDetails.getCaseData().getTypeOfAmendmentMSL().contains(TYPE_AMENDMENT_LEAD)) {

                log.info("Amend lead case logic");

                newMultipleObjects = multipleAmendLeadCaseService.bulkAmendLeadCaseLogic(userToken,
                        multipleDetails, errors, multipleObjects);

            }

            if (multipleDetails.getCaseData().getTypeOfAmendmentMSL().contains(TYPE_AMENDMENT_ADDITION)
                    && errors.isEmpty()) {

                log.info("Amend case ids logic");

                newMultipleObjects = multipleAmendCaseIdsService.bulkAmendCaseIdsLogic(userToken,
                        multipleDetails, errors, multipleObjects);

            }

            if (errors.isEmpty()) {

                log.info("Create a new Excel");

                excelDocManagementService.generateAndUploadExcel(newMultipleObjects, userToken,
                        multipleDetails.getCaseData());

            }

        }

        log.info("Clearing the payload");

        multipleDetails.getCaseData().setAmendLeadCase(null);
        multipleDetails.getCaseData().setCaseIdCollection(null);
        multipleDetails.getCaseData().setTypeOfAmendmentMSL(null);

    }

}
