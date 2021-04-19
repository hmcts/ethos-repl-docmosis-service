package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.PersistentQHelperService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.UPDATING_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service("multipleTransferService")
public class MultipleTransferService {

    private final ExcelReadingService excelReadingService;
    private final PersistentQHelperService persistentQHelperService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public void multipleTransferLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Multiple transfer logic");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.ALL);

        if (multipleObjects.keySet().isEmpty()) {

            log.info("No cases in the multiple");

            errors.add("No cases in the multiple");

        } else {

            log.info("Send updates to single cases");

            multipleDetails.getCaseData().setState(UPDATING_STATE);

            sendUpdatesToSinglesCT(userToken, multipleDetails, errors, multipleObjects);

        }

        log.info("Resetting mid fields");

        MultiplesHelper.resetMidFields(multipleDetails.getCaseData());

    }

    private void sendUpdatesToSinglesCT(String userToken, MultipleDetails multipleDetails,
                                        List<String> errors, TreeMap<String, Object> multipleObjects) {

        List<String> ethosCaseRefCollection = new ArrayList<>(multipleObjects.keySet());
        MultipleData multipleData = multipleDetails.getCaseData();

        persistentQHelperService.sendCreationEventToSingles(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                errors,
                ethosCaseRefCollection,
                multipleData.getOfficeMultipleCT().getValue().getCode(),
                multipleData.getPositionTypeCT(),
                ccdGatewayBaseUrl,
                multipleData.getReasonForCT(),
                multipleData.getMultipleReference(),
                YES
        );

    }

}
