package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.ExcelReadingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
class ExcelFileSingleCasesImporter implements SingleCasesImporter {
    private final ExcelReadingService excelReadingService;

    ExcelFileSingleCasesImporter(ExcelReadingService excelReadingService) {
        this.excelReadingService = excelReadingService;
    }

    @Override
    public List<String> importCases(MultipleData multipleData, String authToken) throws ImportException {
        try {
            var workbook = getWorkbook(multipleData, authToken);
            var ethosCaseReferences = getEthosCasesReferences(workbook);
            workbook.close();

            return ethosCaseReferences;
        } catch (IOException e) {
            throw new ImportException(String.format("Unexpected error when importing Excel file for multiple %s",
                    multipleData.getMultipleReference()), e);
        }
    }

    private XSSFWorkbook getWorkbook(MultipleData multipleData, String authToken) throws IOException {
        var downloadBinaryUrl = multipleData
                .getBulkAddSingleCasesImportFile().getUploadedDocument().getDocumentBinaryUrl();
        return excelReadingService.readWorkbook(authToken, downloadBinaryUrl);
    }

    private List<String> getEthosCasesReferences(XSSFWorkbook workbook) {
        var sheet = workbook.getSheetAt(0);

        var ethosCaseReferences = new ArrayList<String>();

        for (var row : sheet) {
            // Skip header row
            if (row.getRowNum() == 0) {
                continue;
            }
            var cell = row.getCell(0);
            var ethosReference = cell.getStringCellValue();
            if (StringUtils.isNotBlank(ethosReference)) {
                ethosCaseReferences.add(ethosReference);
            }
        }
        return ethosCaseReferences;
    }
}
