package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TornadoService;

import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service("multipleLetterService")
public class MultipleLetterService {

    private static final String MESSAGE = "Failed to generate document for case id : ";

    private final TornadoService tornadoService;
    private final ExcelReadingService excelReadingService;
    private final SingleCasesReadingService singleCasesReadingService;

    @Autowired
    public MultipleLetterService(TornadoService tornadoService,
                                 ExcelReadingService excelReadingService,
                                 SingleCasesReadingService singleCasesReadingService) {
        this.tornadoService = tornadoService;
        this.excelReadingService = excelReadingService;
        this.singleCasesReadingService = singleCasesReadingService;
    }

    public DocumentInfo bulkLetterLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel for letter logic");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.FLAGS);

        DocumentInfo documentInfo = new DocumentInfo();

        if (!multipleObjects.keySet().isEmpty()) {

            log.info("Pull information from first case filtered");

            SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(userToken,
                    multipleDetails.getCaseTypeId(), multipleObjects.firstKey());

            log.info("Generate letter");

            documentInfo = generateLetter(userToken, multipleDetails, submitEvent);

        } else {

            errors.add("No cases searched to generate schedules");

        }

        log.info("Resetting mid fields");

        MultiplesHelper.resetMidFields(multipleDetails.getCaseData());

        return documentInfo;

    }

    private DocumentInfo generateLetter(String userToken, MultipleDetails multipleDetails, SubmitEvent submitEvent) {

        DocumentInfo documentInfo;

        try {
             documentInfo = tornadoService.documentGeneration(
                     userToken, submitEvent.getCaseData(), UtilHelper.getCaseTypeId(multipleDetails.getCaseTypeId()));

        } catch (Exception ex) {

            throw new DocumentManagementException(MESSAGE + multipleDetails.getCaseId() + ex.getMessage());

        }

        return documentInfo;

    }

}
