package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.tasks.ScheduleCallable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.*;

@Slf4j
@Service("multipleScheduleService")
public class MultipleScheduleService {

    private final ExcelReadingService excelReadingService;
    private final SingleCasesReadingService singleCasesReadingService;
    private final ExcelDocManagementService excelDocManagementService;

    public static final int ES_PARTITION_SIZE = 1000;
    public static final int THREAD_NUMBER = 20;

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

        List<SchedulePayload> schedulePayloads =
                getSchedulePayloadCollection(userToken, multipleDetails.getCaseTypeId(),
                        getCaseIdCollectionFromFilter(multipleObjects, filterExcelType), errors);

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

    private List<SchedulePayload> getSchedulePayloadCollection(String userToken, String caseTypeId, List<String> caseIdCollection, List<String> errors) {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

        List<Future<List<SchedulePayload>>> resultList = new ArrayList<>();

        for (List<String> partitionCaseIds : Lists.partition(caseIdCollection, ES_PARTITION_SIZE)) {

            ScheduleCallable scheduleCallable = new ScheduleCallable(singleCasesReadingService, userToken, caseTypeId, partitionCaseIds);

            resultList.add(executor.submit(scheduleCallable));

        }

        List<SchedulePayload> result = new ArrayList<>();

        for (Future<List<SchedulePayload>> fut : resultList){

            try {

                result.addAll(fut.get());

            } catch (InterruptedException | ExecutionException e) {

                errors.add("Error Generating Schedules");

                log.error(e.getMessage(), e);

                Thread.currentThread().interrupt();

            }

        }

        executor.shutdown();

        return result;

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
