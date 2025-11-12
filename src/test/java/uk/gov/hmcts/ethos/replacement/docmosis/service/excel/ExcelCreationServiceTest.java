package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings({"PMD.LooseCoupling", "PMD.LawOfDemeter", "PMD.TooManyMethods"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelCreationServiceTest {

    @InjectMocks
    private ExcelCreationService excelCreationService;

    String leadLink = "<a target=\"_blank\" href=\"https://www-ccd.perftest.platform.hmcts.net/v2/case/1604313560561842\">245000/2020</a>";

    private TreeMap<String, Object> multipleObjects;

    @Before
    public void setUp() {
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
    }

    @Test
    public void writeExcelObjects() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(multipleObjects.values()),
                new ArrayList<>(Arrays.asList("245000/1", "245000/1")),
                leadLink));
    }

    @Test
    public void writeExcelObjectsEmptySubMultiples() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(multipleObjects.values()),
                new ArrayList<>(),
                leadLink));
    }

    @Test
    public void writeExcelString() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(Arrays.asList("245000/2020", "245001/2020", "245002/2020")),
                new ArrayList<>(),
                leadLink));
    }

    @Test
    public void writeExcelStringEmpty() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(),
                new ArrayList<>(),
                leadLink));
    }

    @Test
    public void reportTitleCellStyleTest() throws IOException {
        try (XSSFWorkbook workbook = createWorkbook()) {
            CellStyle actualCellStyle = excelCreationService.getReportTitleCellStyle(workbook);
            assertEquals(IndexedColors.BLUE_GREY.getIndex(), actualCellStyle.getFillBackgroundColor());
            assertEquals(HorizontalAlignment.CENTER, actualCellStyle.getAlignment());
            assertEquals(FillPatternType.SOLID_FOREGROUND, actualCellStyle.getFillPattern());
            assertEquals(VerticalAlignment.CENTER, actualCellStyle.getVerticalAlignment());
            assertEquals(IndexedColors.LIGHT_GREEN.getIndex(), actualCellStyle.getFillForegroundColor());
        }
    }

    @Test
    public void headerCellStyleTest() throws IOException {
        try (XSSFWorkbook workbook = createWorkbook()) {
            CellStyle actualCellStyle = excelCreationService.getHeaderCellStyle(workbook);
            assertEquals(BorderStyle.THIN, actualCellStyle.getBorderBottom());
            assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), actualCellStyle.getBottomBorderColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, actualCellStyle.getFillPattern());
        }
    }

    private XSSFWorkbook createWorkbook() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet("Sheet1");
        return workbook;
    }

    @Test
    public void reportSubTitleCellStyleTest() throws IOException {
        try (XSSFWorkbook workbook = createWorkbook()) {
            CellStyle actualCellStyle = excelCreationService.getReportSubTitleCellStyle(workbook);
            assertEquals(IndexedColors.LIGHT_GREEN.getIndex(), actualCellStyle.getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, actualCellStyle.getFillPattern());
            assertEquals(HorizontalAlignment.CENTER, actualCellStyle.getAlignment());
            assertEquals(VerticalAlignment.CENTER, actualCellStyle.getVerticalAlignment());
        }
    }

    @Test
    public void addReportAdminDetailsTest() throws IOException {
        try (XSSFWorkbook workbook = createWorkbook()) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            excelCreationService.addReportAdminDetails(workbook,
                    workbook.getSheetAt(0),
                    1,
                    "description", 6);
            assertEquals("description", sheet.getRow(1).getCell(0).getStringCellValue());
        }

    }

    @Test
    public void cellStyleTest() throws IOException {
        try (XSSFWorkbook workbook = createWorkbook()) {
            CellStyle actualCellStyle = excelCreationService.getCellStyle(workbook);
            assertEquals(VerticalAlignment.CENTER, actualCellStyle.getVerticalAlignment());
            assertEquals(IndexedColors.WHITE1.getIndex(), actualCellStyle.getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, actualCellStyle.getFillPattern());
            assertEquals(HorizontalAlignment.CENTER, actualCellStyle.getAlignment());
            assertEquals(BorderStyle.THIN, actualCellStyle.getBorderBottom());
            assertEquals(BorderStyle.NONE, actualCellStyle.getBorderTop());
            assertEquals(BorderStyle.NONE, actualCellStyle.getBorderLeft());
            assertEquals(BorderStyle.NONE, actualCellStyle.getBorderRight());
            assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), actualCellStyle.getBottomBorderColor());
        }
    }

    @Test
    public void initializeReportHeadersTest() throws IOException {
        try (XSSFWorkbook workbook = createWorkbook()) {
            Sheet sheet = workbook.getSheetAt(0);
            excelCreationService.initializeReportHeaders("document1",
                    "description",
                    workbook,
                    workbook.getSheetAt(0),
                    List.of("header1", "header2", "header3", "header4", "header5"));
            assertEquals("description", sheet.getRow(1).getCell(0).getStringCellValue());
            for (int j = 0; j < 5; j++) {
                assertEquals("header" + (j + 1), sheet.getRow(2).getCell(j).getStringCellValue());
            }
        }

    }
}