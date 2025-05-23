package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.CONSTRAINT_KEY;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HIDDEN_SHEET_NAME;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.SHEET_NAME;

@Slf4j
@Service("excelCreationService")
public class ExcelCreationService {

    public byte[] writeExcel(List<?> multipleCollection, List<String> subMultipleCollection, String leadCaseString) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
        XSSFSheet hiddenSheet = workbook.createSheet(HIDDEN_SHEET_NAME);

        enableLocking(sheet);
        enableLocking(hiddenSheet);

        initializeHeaders(workbook, sheet);
        initializeData(workbook, sheet, multipleCollection, subMultipleCollection, leadCaseString);

        adjustColumnSize(sheet);
        createHiddenSheet(workbook, hiddenSheet, subMultipleCollection);
        addSubMultiplesValidation(workbook, sheet, multipleCollection, subMultipleCollection);

        return MultiplesHelper.writeExcelFileToByteArray(workbook);
    }

    private void enableLocking(XSSFSheet sheet) {
        sheet.lockDeleteColumns(true);
        sheet.lockDeleteRows(true);
        sheet.lockFormatCells(true);
        sheet.lockFormatColumns(true);
        sheet.lockFormatRows(true);
        sheet.lockInsertColumns(true);
        sheet.lockInsertRows(true);
        sheet.enableLocking();
        sheet.protectSheet(CONSTRAINT_KEY);
    }

    private CellStyle getStyleForUnLocking(XSSFWorkbook workbook) {
        CellStyle styleForUnLocking = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLUE.getIndex());

        styleForUnLocking.setLocked(false);
        styleForUnLocking.setAlignment(HorizontalAlignment.CENTER);
        styleForUnLocking.setFont(font);

        workbook.lockStructure();

        return styleForUnLocking;
    }

    private static CellStyle getStyleForLocking(XSSFWorkbook workbook, boolean lead) {
        CellStyle styleForLocking = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());

        if (lead) {
            font.setColor(IndexedColors.WHITE.getIndex());
            styleForLocking.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            styleForLocking.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        styleForLocking.setAlignment(HorizontalAlignment.CENTER);
        styleForLocking.setFont(font);

        return styleForLocking;
    }

    private void adjustColumnSize(XSSFSheet sheet) {
        //Adjust the column width to fit the content
        sheet.autoSizeColumn(0);
        sheet.setColumnWidth(1, 8000);
        for (int i = 2; i <= 5; i++) {
            sheet.setColumnWidth(i, 4000);
        }
    }

    private void createHiddenSheet(XSSFWorkbook workbook, XSSFSheet hiddenSheet, List<String> subMultipleCollection) {
        if (!subMultipleCollection.isEmpty()) {
            CellStyle styleForLocking = getStyleForLocking(workbook, false);
            for (int i = 0; i < subMultipleCollection.size(); i++) {
                XSSFRow row = hiddenSheet.createRow(i);
                createCell(row, 0, subMultipleCollection.get(i), styleForLocking);
            }
        }
    }

    private void addSubMultiplesValidation(XSSFWorkbook workbook, XSSFSheet sheet, List<?> multipleCollection,
                                           List<String> subMultipleCollection) {
        if (!subMultipleCollection.isEmpty() && !multipleCollection.isEmpty()) {
            Name namedCell = workbook.createName();
            namedCell.setNameName(HIDDEN_SHEET_NAME);
            namedCell.setRefersToFormula(HIDDEN_SHEET_NAME + "!$A$1:$A$" + subMultipleCollection.size());

            CellRangeAddressList cellRangeAddressList =
                    new CellRangeAddressList(1, multipleCollection.size(), 1, 1);
            DataValidationHelper helper = sheet.getDataValidationHelper();
            DataValidationConstraint constraint = helper.createFormulaListConstraint(HIDDEN_SHEET_NAME);
            DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);

            workbook.setSheetHidden(1, true);
            sheet.addValidationData(dataValidation);
        }
    }

    private void initializeHeaders(XSSFWorkbook workbook, XSSFSheet sheet) {
        XSSFRow rowHead = sheet.createRow(0);
        CellStyle styleForLocking = getStyleForLocking(workbook, false);

        for (int j = 0; j < MultiplesHelper.HEADERS.size(); j++) {
            rowHead.createCell(j).setCellValue(MultiplesHelper.HEADERS.get(j));
            createCell(rowHead, j, MultiplesHelper.HEADERS.get(j), styleForLocking);
        }
    }

    public void createCell(XSSFRow row, int cellIndex, String value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);

        if (!Strings.isNullOrEmpty(value) && !value.isBlank()) {
            cell.setCellValue(value);
        }
    }

    private void initializeData(XSSFWorkbook workbook, XSSFSheet sheet, List<?> multipleCollection,
                                List<String> subMultipleCollection, String leadCaseString) {

        if (multipleCollection.isEmpty()) {
            return;
        }

        boolean isStringRefsList = multipleCollection.get(0) instanceof String;
        log.info(isStringRefsList ? "Initializing multipleRefs" : "Initializing data");

        SortedMap<String, SortedMap<String, Object>> orderedAllCasesList =
                MultiplesHelper.createCollectionOrderedByCaseRef(multipleCollection);
        if (orderedAllCasesList.isEmpty()) {
            return;
        }
        String leadCase = MultiplesHelper.getCurrentLead(leadCaseString);
        final int[] rowIndex = {1};
        orderedAllCasesList.forEach((String caseYear, Map<String, Object> caseYearList) ->
                caseYearList.forEach((String caseNum, Object caseItem) -> {
                    if (isStringRefsList) {
                        constructCaseExcelRow(workbook, sheet, rowIndex[0], (String) caseItem, leadCase, null,
                                !subMultipleCollection.isEmpty());
                    } else {
                        MultipleObject multipleObject = (MultipleObject) caseItem;
                        constructCaseExcelRow(workbook, sheet, rowIndex[0], multipleObject.getEthosCaseRef(), leadCase,
                                multipleObject, !subMultipleCollection.isEmpty());
                    }
                    rowIndex[0]++;
                })
        );
    }

    private void constructCaseExcelRow(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex, String ethosCaseRef,
                                       String leadCase, MultipleObject multipleObject, boolean hasSubMultiples) {
        CellStyle styleForUnLocking = getStyleForUnLocking(workbook);
        CellStyle styleForLocking = getStyleForLocking(workbook, false);
        XSSFRow row = sheet.createRow(rowIndex);
        int columnIndex = 0;

        if (ethosCaseRef.equals(leadCase)) {
            log.info("Lead: " + leadCase);
            CellStyle styleForLockingLead = getStyleForLocking(workbook, true);
            createCell(row, columnIndex, ethosCaseRef, styleForLockingLead);
        } else {
            createCell(row, columnIndex, ethosCaseRef, styleForLocking);
        }

        if (multipleObject == null) {
            for (int k = 0; k < MultiplesHelper.HEADERS.size() - 1; k++) {
                if (k == 0 && !hasSubMultiples) {
                    columnIndex++;
                    createCell(row, columnIndex, "", styleForLocking);
                } else {
                    // Create empty cells unlocked
                    columnIndex++;
                    createCell(row, columnIndex, "", styleForUnLocking);
                }
            }
        } else {
            if (!hasSubMultiples) {
                columnIndex++;
                createCell(row, columnIndex, multipleObject.getSubMultiple(), styleForLocking);
            } else {
                columnIndex++;
                createCell(row, columnIndex, multipleObject.getSubMultiple(), styleForUnLocking);
            }
            // Create these cells unlocked
            columnIndex++;
            createCell(row, columnIndex, multipleObject.getFlag1(), styleForUnLocking);
            columnIndex++;
            createCell(row, columnIndex, multipleObject.getFlag2(), styleForUnLocking);
            columnIndex++;
            createCell(row, columnIndex, multipleObject.getFlag3(), styleForUnLocking);
            columnIndex++;
            createCell(row, columnIndex, multipleObject.getFlag4(), styleForUnLocking);
        }
    }

    public CellStyle getReportTitleCellStyle(XSSFWorkbook workbook) {
        Font font = getFont(workbook);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        font.setFontHeightInPoints((short)25);
        CellStyle cellStyle = getHeadersCellStyle(workbook);
        cellStyle.setFont(font);
        cellStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
        return cellStyle;
    }

    public CellStyle getHeaderCellStyle(XSSFWorkbook workbook) {
        Font font = getFont(workbook);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        CellStyle cellStyle = getHeadersCellStyle(workbook);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return cellStyle;
    }

    private CellStyle getHeadersCellStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    private Font getFont(XSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibre");
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        font.setFontHeightInPoints((short)16);
        return font;
    }

    public CellStyle getReportSubTitleCellStyle(XSSFWorkbook workbook) {
        Font font = getFont(workbook);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        font.setFontHeightInPoints((short)20);
        CellStyle cellStyle = getHeadersCellStyle(workbook);
        cellStyle.setFont(font);
        return cellStyle;
    }

    public void addReportAdminDetails(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex,
                                      String reportPrintedOnDescription, int lastCol) {
        CellRangeAddress reportTitleCellRange = new CellRangeAddress(rowIndex, rowIndex, 0, lastCol);
        sheet.addMergedRegion(reportTitleCellRange);
        XSSFRow rowReportTitle = sheet.createRow(rowIndex);
        rowReportTitle.setHeight((short)(rowReportTitle.getHeight() * 8));
        CellStyle styleForHeaderCell = getCellStyle(workbook);
        styleForHeaderCell.setAlignment(HorizontalAlignment.CENTER);
        styleForHeaderCell.setBorderTop(BorderStyle.THIN);
        styleForHeaderCell.setBorderLeft(BorderStyle.THIN);
        styleForHeaderCell.setBorderRight(BorderStyle.THIN);
        styleForHeaderCell.setBorderBottom(BorderStyle.THIN);
        styleForHeaderCell.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        styleForHeaderCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleForHeaderCell.setFont(getFont(workbook));
        createCell(rowReportTitle, 0, reportPrintedOnDescription, styleForHeaderCell);
    }

    public CellStyle getCellStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = getFont(workbook);
        font.setColor(IndexedColors.BLACK1.getIndex());
        font.setFontHeightInPoints((short)14);
        font.setBold(false);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return cellStyle;
    }

    public void initializeReportHeaders(String documentName, String periodDescription, XSSFWorkbook workbook,
                                        XSSFSheet sheet, List<String> headers) {
        CellRangeAddress reportTitleCellRange = new CellRangeAddress(0, 0, 0, headers.size() - 1);
        sheet.addMergedRegion(reportTitleCellRange);
        XSSFRow rowReportTitle = sheet.createRow(0);
        rowReportTitle.setHeight((short)(rowReportTitle.getHeight() * 8));
        CellStyle styleForHeaderCell = getReportTitleCellStyle(workbook);
        createCell(rowReportTitle, 0, documentName, styleForHeaderCell);

        CellRangeAddress reportPeriodCellRange = new CellRangeAddress(1, 1, 0, headers.size() - 1);
        sheet.addMergedRegion(reportPeriodCellRange);
        XSSFRow rowReportPeriod = sheet.createRow(1);
        rowReportPeriod.setHeight((short)(rowReportPeriod.getHeight() * 6));
        CellStyle styleForSubTitleCell = getReportSubTitleCellStyle(workbook);
        createCell(rowReportPeriod, 0, periodDescription, styleForSubTitleCell);

        XSSFRow rowHead = sheet.createRow(2);
        rowHead.setHeight((short)(rowHead.getHeight() * 4));
        CellStyle styleForColHeaderCell = getHeaderCellStyle(workbook);
        for (int j = 0; j < headers.size(); j++) {
            rowHead.createCell(j).setCellValue(headers.get(j));
            createCell(rowHead, j, headers.get(j), styleForColHeaderCell);
        }
    }
}
