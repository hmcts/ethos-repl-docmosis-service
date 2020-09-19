package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.SubMultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
@Service("multipleHelperService")
public class MultipleHelperService {

    private final SingleCasesReadingService singleCasesReadingService;
    private final MultipleCasesReadingService multipleCasesReadingService;
    private final ExcelReadingService excelReadingService;
    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleCasesSendingService multipleCasesSendingService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    @Autowired
    public MultipleHelperService(SingleCasesReadingService singleCasesReadingService,
                                 MultipleCasesReadingService multipleCasesReadingService,
                                 ExcelReadingService excelReadingService,
                                 ExcelDocManagementService excelDocManagementService,
                                 MultipleCasesSendingService multipleCasesSendingService) {
        this.singleCasesReadingService = singleCasesReadingService;
        this.multipleCasesReadingService = multipleCasesReadingService;
        this.excelReadingService = excelReadingService;
        this.excelDocManagementService = excelDocManagementService;
        this.multipleCasesSendingService = multipleCasesSendingService;
    }

    public void addLeadMarkUp(String userToken, String caseTypeId, MultipleData multipleData, String newLeadCaseId) {

        SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(
                userToken,
                caseTypeId,
                newLeadCaseId);

        if (submitEvent != null) {

            multipleData.setLeadCase(MultiplesHelper.generateLeadMarkUp(
                    ccdGatewayBaseUrl,
                    String.valueOf(submitEvent.getCaseId()),
                    newLeadCaseId));

        } else {

            log.info("No lead case found for: " + newLeadCaseId);

        }

    }

    public void validateExternalMultipleAndSubMultiple(String userToken, String caseTypeId, String multipleRef,
                                                       String subMultipleName, List<String> errors) {

        List<SubmitMultipleEvent> multipleEvents =
                multipleCasesReadingService.retrieveMultipleCases(
                        userToken,
                        caseTypeId,
                        multipleRef);

        if (!multipleEvents.isEmpty()) {

            SubmitMultipleEvent multipleEvent = multipleEvents.get(0);

            validateSubMultiple(subMultipleName,
                    multipleEvent.getCaseData().getSubMultipleCollection(),
                    errors,
                    multipleRef);

        } else {

            errors.add("Multiple " + multipleRef + " does not exist");

        }

    }

    public void validateSubMultiple(String subMultipleName,
                                     List<SubMultipleTypeItem> subMultiples,
                                     List<String> errors,
                                     String multipleReference) {

        if (subMultipleName != null && !doesSubMultipleExist(subMultiples, subMultipleName)) {

            errors.add("Sub multiple " + subMultipleName + " does not exist in " + multipleReference);

        }

    }

    private boolean doesSubMultipleExist(List<SubMultipleTypeItem> subMultiples, String subMultipleName) {

        if (subMultiples != null) {

            return subMultiples
                    .stream()
                    .anyMatch(p -> p.getValue().getSubMultipleName().equals(subMultipleName));

        } else {

            return false;

        }

    }

    public void moveCasesAndSendUpdateToMultiple(String userToken, String newSubMultipleName,
                                                 String jurisdiction, String caseTypeId,
                                                 String newMultipleCaseId, MultipleData newMultipleData,
                                                 List<String> casesFiltered, List<String> errors) {

        if (isNullOrEmpty(newSubMultipleName)) {

            log.info("Moving single cases without sub multiples");

            readUpdatedExcelAndAddCasesInMultiple(userToken, newMultipleData, errors,
                    casesFiltered, "");

        } else {

            log.info("Moving single cases with sub multiples");

            readUpdatedExcelAndAddCasesInMultiple(userToken, newMultipleData, errors,
                    casesFiltered, newSubMultipleName);

        }

        log.info("Send update to the multiple with new excel");

        multipleCasesSendingService.sendUpdateToMultiple(userToken, caseTypeId, jurisdiction,
                newMultipleData, newMultipleCaseId);

    }

    private void readUpdatedExcelAndAddCasesInMultiple(String userToken, MultipleData newMultipleData,
                                                       List<String> errors, List<String> multipleObjectsFiltered,
                                                       String newSubMultipleName) {

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(newMultipleData),
                        errors,
                        newMultipleData,
                        FilterExcelType.ALL);

        List<MultipleObject> newMultipleObjectsUpdated = addCasesInMultiple(multipleObjectsFiltered,
                multipleObjects, newSubMultipleName);

        excelDocManagementService.generateAndUploadExcel(newMultipleObjectsUpdated, userToken, newMultipleData);

    }

    private List<MultipleObject> addCasesInMultiple(List<String> multipleObjectsFiltered,
                                                    TreeMap<String, Object> multipleObjects,
                                                    String newSubMultipleName) {

        List<MultipleObject> multipleObjectsToBeAdded = new ArrayList<>();
        List<MultipleObject> newMultipleObjectsUpdated = new ArrayList<>();

        for (String ethosCaseReference : multipleObjectsFiltered) {

            multipleObjectsToBeAdded.add(MultiplesHelper.createMultipleObject(ethosCaseReference, newSubMultipleName));
        }

        multipleObjects.forEach((key, value) -> newMultipleObjectsUpdated.add((MultipleObject) value));

        newMultipleObjectsUpdated.addAll(multipleObjectsToBeAdded);

        return newMultipleObjectsUpdated;

    }

}
