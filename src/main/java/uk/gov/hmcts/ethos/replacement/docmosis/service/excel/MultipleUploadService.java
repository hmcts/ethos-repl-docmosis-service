package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("multipleUploadService")
public class MultipleUploadService {

    public static final String ERROR_SHEET_NUMBER_ROWS = "Number of rows expected ";

    public static final String ERROR_SHEET_NUMBER_COLUMNS = "Number of columns expected ";

    public static final String ERROR_SHEET_EMPTY = "Empty sheet";

    public static final String ERROR_DOCUMENT_EXTENSION = "Document extension is not valid";

    public static final String EXCEL_DOCUMENT_EXTENSION = "xlsx";

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

            MultipleData multipleData = multipleDetails.getCaseData();

            log.info("Validating document extension");

            validateDocumentImported(multipleData.getCaseImporterFile().getUploadedDocument(), errors);

            if (!errors.isEmpty()) return;

            Sheet datatypeSheet = excelReadingService.checkExcelErrors(
                    userToken,
                    MultiplesHelper.getExcelBinaryUrl(multipleData),
                    errors);

            if (datatypeSheet != null) {

                validateSheet(
                        datatypeSheet,
                        multipleData,
                        errors);

                log.info("Update the document information");
                log.info("File name uploaded: " + multipleData.getCaseImporterFile().getUploadedDocument().getDocumentFilename());

                multipleData.setCaseImporterFile(
                        excelDocManagementService.populateCaseImporterFile(
                                userToken,
                                multipleData.getCaseImporterFile().getUploadedDocument()));

            }

        } catch (IOException e) {

            log.error("Error reading the Excel");

            log.error(e.getMessage());

        }

    }

    private void validateSheet(Sheet datatypeSheet, MultipleData multipleData, List<String> errors) {

        if (datatypeSheet.getRow(0) != null) {

            int collectionSize = multipleData.getCaseIdCollection().size();

            log.info("Case IDs: " + collectionSize);
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

    private void validateDocumentImported(UploadedDocumentType uploadedDocumentType, List<String> errors) {

        if (uploadedDocumentType == null || !checkFileNameExtension(uploadedDocumentType.getDocumentFilename())) {

            log.info("Document extension is not valid");

            errors.add(ERROR_DOCUMENT_EXTENSION);

        }

    }

    private boolean checkFileNameExtension(String filename) {

        String extension = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");

        return extension.equals(EXCEL_DOCUMENT_EXTENSION);

    }

}
