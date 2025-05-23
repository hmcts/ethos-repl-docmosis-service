package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.CaseMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.types.MultipleObjectType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseTransferService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.PersistentQHelperService;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.UUID;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIGRATION_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.UPDATING_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service("multipleTransferService")
public class MultipleTransferService {

    private final ExcelReadingService excelReadingService;
    private final PersistentQHelperService persistentQHelperService;
    private final MultipleCasesReadingService multipleCasesReadingService;
    private final SingleCasesReadingService singleCasesReadingService;
    private final CaseTransferService caseTransferService;

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

            validateCaseBeforeTransfer(userToken, multipleDetails, errors, multipleObjects);

            if (errors.isEmpty()) {
                multipleDetails.getCaseData().setState(UPDATING_STATE);
                multipleDetails.getCaseData().setCaseIdCollection(populateCaseIdsList(multipleObjects));
                sendUpdatesToSinglesCT(userToken, multipleDetails, errors, multipleObjects);
            }
        }

        log.info("Resetting mid fields");

        MultiplesHelper.resetMidFields(multipleDetails.getCaseData());

    }

    private List<CaseIdTypeItem> populateCaseIdsList(SortedMap<String, Object> multipleObjects) {
        List<String> keys = new ArrayList<>(multipleObjects.keySet());
        List<CaseIdTypeItem> caseIdTypeItems = new ArrayList<>();
        keys.forEach(k -> {
            var caseIdTypeItem = new CaseIdTypeItem();
            var multipleObject = (MultipleObject) multipleObjects.get(k);
            var caseTypeItem = new CaseType();
            caseTypeItem.setEthosCaseReference(multipleObject.getEthosCaseRef());
            caseIdTypeItem.setValue(caseTypeItem);
            caseIdTypeItem.setId(UUID.randomUUID().toString());
            caseIdTypeItems.add(caseIdTypeItem);
        });
        return caseIdTypeItems;
    }

    private void validateCaseBeforeTransfer(String userToken, MultipleDetails multipleDetails, List<String> errors,
                                            SortedMap<String, Object> multipleObjects) {
        List<String> ethosCaseRefCollection = new ArrayList<>(multipleObjects.keySet());
        var submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(), ethosCaseRefCollection,
                multipleDetails.getCaseData().getMultipleSource());
        for (var submitEvent : submitEvents) {
            caseTransferService.validateCase(submitEvent.getCaseData(), errors);
        }
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
                MultiplesHelper.generateMarkUp(ccdGatewayBaseUrl,
                        multipleDetails.getCaseId(),
                        multipleData.getMultipleReference())
        );

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
            log.info("Retrieved the old multiple data is: {} \n", oldSubmitMultipleEvent);

            log.info("Generate case multiple items");
            multipleDetails.getCaseData().setCaseMultipleCollection(generateCaseMultipleItems(
                    userToken,
                    oldSubmitMultipleEvent,
                    errors));
            log.info("Generated case multiple items is : {} \n",
                    multipleDetails.getCaseData().getCaseMultipleCollection());

            log.info("Generate linked multiple CT markup");
            multipleDetails.getCaseData().setLinkedMultipleCT(MultiplesHelper.generateMarkUp(
                    ccdGatewayBaseUrl,
                    String.valueOf(oldSubmitMultipleEvent.getCaseId()),
                    multipleReference));
            log.info("Generated linked multiple CT markup is: {} \n",
                    multipleDetails.getCaseData().getLinkedMultipleCT());

            multipleDetails.getCaseData().setMultipleSource(oldSubmitMultipleEvent.getCaseData().getMultipleSource());
            multipleDetails.getCaseData().setPreAcceptDone(oldSubmitMultipleEvent.getCaseData().getPreAcceptDone());
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
