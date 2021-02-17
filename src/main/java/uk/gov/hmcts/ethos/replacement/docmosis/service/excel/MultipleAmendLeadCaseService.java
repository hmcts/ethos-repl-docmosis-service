package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.*;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_IS_NOT_IN_MULTIPLE_ERROR;

@Slf4j
@Service("multipleAmendLeadCaseService")
public class MultipleAmendLeadCaseService {

    private final ExcelReadingService excelReadingService;
    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleAmendLeadCaseService(ExcelReadingService excelReadingService,
                                        ExcelDocManagementService excelDocManagementService,
                                        MultipleHelperService multipleHelperService) {
        this.excelReadingService = excelReadingService;
        this.excelDocManagementService = excelDocManagementService;
        this.multipleHelperService = multipleHelperService;
    }

    public void bulkAmendLeadCaseLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel to amend lead case");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        String amendLeadCase  = multipleDetails.getCaseData().getAmendLeadCase();

        if (checkAmendLeadCaseExistsAndIsDifferent(multipleObjects, multipleDetails.getCaseData(), amendLeadCase)) {

            log.info("Send updates to single cases");

            multipleHelperService.sendUpdatesToSinglesLogic(userToken, multipleDetails,
                    errors, amendLeadCase, multipleObjects, new ArrayList<>(Collections.singletonList(amendLeadCase)));

            log.info("Create a new Excel updating the new lead case");

            excelDocManagementService.generateAndUploadExcel(new ArrayList<>(multipleObjects.values()),
                    userToken, multipleDetails.getCaseData());

        } else {

            log.info("Case is not part of the multiple");

            errors.add(CASE_IS_NOT_IN_MULTIPLE_ERROR);

        }

        log.info("Clearing the payload");

        multipleDetails.getCaseData().setAmendLeadCase(null);

    }

    private boolean checkAmendLeadCaseExistsAndIsDifferent(TreeMap<String, Object> multipleObjects,
                                                           MultipleData multipleData, String amendLeadCase) {

        log.info("AmendLeadCase: " + amendLeadCase);

        String oldLeadCase = MultiplesHelper.getCurrentLead(multipleData.getLeadCase());

        return multipleObjects.keySet().stream().anyMatch(amendLeadCase::equalsIgnoreCase)
                && !oldLeadCase.equals(amendLeadCase);

    }

}
