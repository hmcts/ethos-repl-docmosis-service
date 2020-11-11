package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service("multipleScheduleService")
public class MultipleScheduleService {

    private final ExcelReadingService excelReadingService;
    private final SingleCasesReadingService singleCasesReadingService;
    private final ExcelDocManagementService excelDocManagementService;

    @Autowired
    public MultipleScheduleService(ExcelReadingService excelReadingService,
                                   SingleCasesReadingService singleCasesReadingService,
                                   ExcelDocManagementService excelDocManagementService) {
        this.excelReadingService = excelReadingService;
        this.singleCasesReadingService = singleCasesReadingService;
        this.excelDocManagementService = excelDocManagementService;
    }

    public DocumentInfo bulkScheduleLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel for schedule logic");

        FilterExcelType filterExcelType = MultiplesScheduleHelper.getFilterExcelTypeByScheduleDoc(multipleDetails.getCaseData());

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        filterExcelType);

        log.info("Pull information from single cases");

        log.info("MultipleObjectsKeySet: " + multipleObjects.keySet());
        log.info("MultipleObjectsValues: " + multipleObjects.values());

        List<SchedulePayload> schedulePayloads =
                getSchedulePayloadCollection(userToken, multipleDetails.getCaseTypeId(),
                        getCaseIdCollectionFromFilter(multipleObjects, filterExcelType));

        log.info("Generate schedule");

        DocumentInfo documentInfo = generateSchedule(userToken, multipleObjects, multipleDetails, schedulePayloads, errors);

        log.info("Resetting mid fields");

        MultiplesHelper.resetMidFields(multipleDetails.getCaseData());

        return documentInfo;

    }

    private List<String> getCaseIdCollectionFromFilter(TreeMap<String, Object> multipleObjects, FilterExcelType filterExcelType) {

        if (filterExcelType.equals(FilterExcelType.FLAGS)) {

            return new ArrayList<>(multipleObjects.keySet());

        } else {

            return MultiplesScheduleHelper.getSubMultipleCaseIds(multipleObjects);

        }

    }

    private List<SchedulePayload> getSchedulePayloadCollection(String userToken, String caseTypeId, List<String> caseIdCollection) {

        List<SchedulePayload> schedulePayloads = new ArrayList<>();

        for (List<String> partitionCaseIds : Lists.partition(caseIdCollection, 5000)) {

            log.info("Partition: " + partitionCaseIds);

            List<SubmitEvent> submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                    caseTypeId, caseIdCollection);

            for (SubmitEvent submitEvent : submitEvents) {

                schedulePayloads.add(MultiplesScheduleHelper.getSchedulePayloadFromSubmitEvent(submitEvent));

            }

        }

        return schedulePayloads;

    }

    private DocumentInfo generateSchedule(String userToken, TreeMap<String, Object> multipleObjectsFiltered,
                                          MultipleDetails multipleDetails, List<SchedulePayload> schedulePayloads,
                                          List<String> errors) {

        DocumentInfo documentInfo = new DocumentInfo();

        if (!multipleObjectsFiltered.keySet().isEmpty()) {

            documentInfo = excelDocManagementService.writeAndUploadScheduleDocument(userToken,
                    multipleObjectsFiltered, multipleDetails, schedulePayloads);

        } else {

            errors.add("No cases searched to generate schedules");

        }

        return documentInfo;

    }

}
