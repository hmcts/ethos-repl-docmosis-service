package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TornadoService;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_DETAILED_CONFIG;

@Slf4j
@Service("multipleScheduleService")
public class MultipleScheduleService {

    private static final String MESSAGE = "Failed to generate document for case id : ";

    private final TornadoService tornadoService;
    private final ExcelReadingService excelReadingService;
    private final SingleCasesReadingService singleCasesReadingService;

    @Autowired
    public MultipleScheduleService(TornadoService tornadoService,
                                   ExcelReadingService excelReadingService,
                                   SingleCasesReadingService singleCasesReadingService) {
        this.tornadoService = tornadoService;
        this.excelReadingService = excelReadingService;
        this.singleCasesReadingService = singleCasesReadingService;
    }

    public DocumentInfo bulkScheduleLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel to schedule logic");

        FilterExcelType filterExcelType = getFilterExcelTypeByScheduleDoc(multipleDetails.getCaseData());

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails),
                        errors,
                        multipleDetails.getCaseData(),
                        filterExcelType);

        log.info("Pull information from single cases");

        List<SubmitEvent> submitEvents = singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(), multipleObjects, filterExcelType);

        log.info("Generate schedule");

        return generateSchedule(multipleObjects, userToken, multipleDetails, submitEvents, errors);

    }

    private FilterExcelType getFilterExcelTypeByScheduleDoc(MultipleData multipleData) {

        if (Arrays.asList(MULTIPLE_SCHEDULE_CONFIG, MULTIPLE_SCHEDULE_DETAILED_CONFIG)
                .contains(multipleData.getScheduleDocName())) {
            return FilterExcelType.FLAGS;

        } else {
            return FilterExcelType.SUB_MULTIPLE;
        }
    }

    private DocumentInfo generateSchedule(TreeMap<String, Object> multipleObjectsFiltered, String userToken,
                                          MultipleDetails multipleDetails, List<SubmitEvent> submitEvents,
                                          List<String> errors) {

        DocumentInfo documentInfo = new DocumentInfo();

        try {

            if (!multipleObjectsFiltered.keySet().isEmpty()) {

                documentInfo = tornadoService.scheduleMultipleGeneration(userToken, multipleDetails.getCaseData(),
                        multipleObjectsFiltered, submitEvents);

            } else {

                errors.add("No cases searched to generate schedules");

            }

        } catch (Exception ex) {

            throw new DocumentManagementException(MESSAGE + multipleDetails.getCaseId() + ex.getMessage());

        }

        return documentInfo;

    }

}
