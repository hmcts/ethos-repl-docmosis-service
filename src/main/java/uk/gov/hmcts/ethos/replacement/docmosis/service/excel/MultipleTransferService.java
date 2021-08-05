package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.CaseMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.types.MultipleObjectType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.PersistentQHelperService;

@Slf4j
@RequiredArgsConstructor
@Service("multipleTransferService")
public class MultipleTransferService {

    private final ExcelReadingService excelReadingService;
    private final PersistentQHelperService persistentQHelperService;
    private final MultipleCasesReadingService multipleCasesReadingService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public void multipleTransferLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Multiple transfer logic");

        SortedMap<String, Object> multipleObjects =
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
                                        List<String> errors, SortedMap<String, Object> multipleObjects) {

        List<String> ethosCaseRefCollection = new ArrayList<>(multipleObjects.keySet());
        var multipleData = multipleDetails.getCaseData();

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
                YES,
                multipleDetails.getCaseId());

    }

    public void populateDataIfComingFromCT(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        if (multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)
                && multipleDetails.getCaseData().getLinkedMultipleCT() != null) {

            String oldCaseTypeId = multipleDetails.getCaseData().getLinkedMultipleCT();
            String multipleReference = multipleDetails.getCaseData().getMultipleReference();

            log.info("Retrieve the old multiple data");

            var oldSubmitMultipleEvent = multipleCasesReadingService.retrieveMultipleCasesWithRetries(
                    userToken,
                    oldCaseTypeId,
                    multipleReference).get(0);

            log.info("Generate case multiple items");

            multipleDetails.getCaseData().setCaseMultipleCollection(generateCaseMultipleItems(
                    userToken,
                    oldSubmitMultipleEvent,
                    errors));

            log.info("Generate linked multiple CT markup");

            multipleDetails.getCaseData().setLinkedMultipleCT(MultiplesHelper.generateMarkUp(
                    ccdGatewayBaseUrl,
                    String.valueOf(oldSubmitMultipleEvent.getCaseId()),
                    multipleReference));

            multipleDetails.getCaseData().setReasonForCT(oldSubmitMultipleEvent.getCaseData().getReasonForCT());
            multipleDetails.getCaseData().setMultipleName(oldSubmitMultipleEvent.getCaseData().getMultipleName());
        }

    }

    private List<CaseMultipleTypeItem> generateCaseMultipleItems(String userToken,
                                                                 SubmitMultipleEvent oldSubmitMultipleEvent,
                                                                 List<String> errors) {

        SortedMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(oldSubmitMultipleEvent.getCaseData()),
                        errors,
                        oldSubmitMultipleEvent.getCaseData(),
                        FilterExcelType.ALL);

        List<CaseMultipleTypeItem> newMultipleObjectsUpdated = new ArrayList<>();

        if (!multipleObjects.keySet().isEmpty()) {

            multipleObjects.forEach((key, value) -> {
                var multipleObject = (MultipleObject) value;
                var caseMultipleTypeItem = new CaseMultipleTypeItem();

                var multipleObjectType = new MultipleObjectType();
                multipleObjectType.setSubMultiple(multipleObject.getSubMultiple());
                multipleObjectType.setEthosCaseRef(multipleObject.getEthosCaseRef());

                caseMultipleTypeItem.setId(UUID.randomUUID().toString());
                caseMultipleTypeItem.setValue(multipleObjectType);

                newMultipleObjectsUpdated.add(caseMultipleTypeItem);
            });

        }

        return newMultipleObjectsUpdated;

    }

}
