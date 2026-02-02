package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.multiples.CaseImporterFile;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.ExcelReadingService;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExcelFileSingleCasesImporterTest {

    @Test
    public void shouldImportCases() throws ImportException, IOException {
        var downloadUrl = "a-test-download-url";
        var multipleData = createMultipleData(downloadUrl);
        var authToken = "some-token";
        var ethosCaseReferences = List.of("header", "case1", "case2", "", "case3");
        var workbook = createWorkbook(ethosCaseReferences);
        var excelReadingService = mock(ExcelReadingService.class);
        when(excelReadingService.readWorkbook(authToken, downloadUrl)).thenReturn(workbook);

        var excelFileSingleCasesImporter = new ExcelFileSingleCasesImporter(excelReadingService);

        var cases = excelFileSingleCasesImporter.importCases(multipleData, authToken);
        assertEquals(3, cases.size());
        assertEquals("case1", cases.getFirst());
        assertEquals("case2", cases.get(1));
        assertEquals("case3", cases.get(2));
    }

    @Test(expected = ImportException.class)
    public void shouldThrowImportException() throws ImportException, IOException {
        var downloadUrl = "a-test-download-url";
        var multipleData = createMultipleData(downloadUrl);
        var authToken = "some-token";
        var excelReadingService = mock(ExcelReadingService.class);
        when(excelReadingService.readWorkbook(authToken, downloadUrl)).thenThrow(IOException.class);

        var excelFileSingleCasesImporter = new ExcelFileSingleCasesImporter(excelReadingService);
        excelFileSingleCasesImporter.importCases(multipleData, authToken);
    }

    private MultipleData createMultipleData(String downloadUrl) {
        var multipleData = new MultipleData();
        var caseImporterFile = new CaseImporterFile();
        var uploadedDocument = new UploadedDocumentType();
        uploadedDocument.setDocumentBinaryUrl(downloadUrl);
        caseImporterFile.setUploadedDocument(uploadedDocument);
        multipleData.setBulkAddSingleCasesImportFile(caseImporterFile);

        return multipleData;
    }

    private XSSFWorkbook createWorkbook(List<String> ethosCaseReferences) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet();
        for (var i = 0; i < ethosCaseReferences.size(); i++) {
            var ethosCaseReference = ethosCaseReferences.get(i);
            sheet.createRow(i).createCell(0).setCellValue(ethosCaseReference);
        }
        return workbook;
    }

}
