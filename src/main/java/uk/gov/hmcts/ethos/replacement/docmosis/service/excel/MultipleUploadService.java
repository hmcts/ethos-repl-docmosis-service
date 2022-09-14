package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service("multipleUploadService")
public class MultipleUploadService {

    public static final String ERROR_SHEET_NUMBER_ROWS = "Number of rows expected ";

    public static final String ERROR_SHEET_NUMBER_COLUMNS = "Number of columns expected ";

    public static final String ERROR_SHEET_EMPTY = "Empty sheet";

    private final ExcelReadingService excelReadingService;

    private final ExcelDocManagementService excelDocManagementService;

    @Autowired
    public MultipleUploadService(ExcelReadingService excelReadingService,
                                 ExcelDocManagementService excelDocManagementService) {
        this.excelReadingService = excelReadingService;
        this.excelDocManagementService = excelDocManagementService;

    }

    public void bulkUploadLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Check errors uploading excel");

        try {

            var multipleData = multipleDetails.getCaseData();

            XSSFSheet datatypeSheet = excelReadingService.checkExcelErrors(
                    userToken,
                    MultiplesHelper.getExcelBinaryUrl(multipleData),
                    errors);

            if (errors.isEmpty()) {

                validateSheet(
                        datatypeSheet,
                        multipleData,
                        errors);

                log.info("Update the document information");
                log.info("File name uploaded: "
                        + multipleData.getCaseImporterFile().getUploadedDocument().getDocumentFilename());

                multipleData.setCaseImporterFile(
                        excelDocManagementService.populateCaseImporterFile(
                                userToken,
                                multipleData.getCaseImporterFile().getUploadedDocument()));

            } else {

                log.info("Errors uploading excel: " + errors);

            }

        } catch (IOException e) {

            log.error("Error reading the Excel");

            throw new RuntimeException("Error reading the Excel", e);

        }

    }

    private void validateSheet(XSSFSheet datatypeSheet, MultipleData multipleData, List<String> errors) {

        if (datatypeSheet.getRow(0) != null) {

            var collectionSize = Integer.parseInt(multipleData.getCaseCounter());

            log.info("Case Counter " + collectionSize);

            log.info("Number of rows: " + datatypeSheet.getLastRowNum());

            if (collectionSize != datatypeSheet.getLastRowNum()) {

                errors.add(ERROR_SHEET_NUMBER_ROWS + collectionSize);

            }

            log.info("Number of columns: " + datatypeSheet.getRow(0).getLastCellNum());

            if (datatypeSheet.getRow(0).getLastCellNum() != MultiplesHelper.HEADERS.size()) {

                errors.add(ERROR_SHEET_NUMBER_COLUMNS + MultiplesHelper.HEADERS.size());

            }

        } else {

            errors.add(ERROR_SHEET_EMPTY);

        }
    }

}