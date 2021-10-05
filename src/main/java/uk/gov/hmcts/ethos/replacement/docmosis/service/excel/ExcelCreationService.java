package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;

import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

@Slf4j
@Service("excelCreationService")
public class ExcelCreationService {

    public byte[] writeExcel(List<?> multipleCollection, List<String> subMultipleCollection, String leadCaseString) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(SHEET_NAME);
        var hiddenSheet = workbook.createSheet(HIDDEN_SHEET_NAME);

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
        for (var i = 2; i <= 5; i++) {
            sheet.setColumnWidth(i, 4000);
        }
    }

    private void createHiddenSheet(XSSFWorkbook workbook, XSSFSheet hiddenSheet, List<String> subMultipleCollection) {
        if (!subMultipleCollection.isEmpty()) {
            CellStyle styleForLocking = getStyleForLocking(workbook, false);
            for (var i = 0; i < subMultipleCollection.size(); i++) {
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

            var cellRangeAddressList =
                    new CellRangeAddressList(1, multipleCollection.size(), 1, 1);
            var helper = sheet.getDataValidationHelper();
            DataValidationConstraint constraint = helper.createFormulaListConstraint(HIDDEN_SHEET_NAME);
            var dataValidation = helper.createValidation(constraint, cellRangeAddressList);
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);

            workbook.setSheetHidden(1, true);
            sheet.addValidationData(dataValidation);
        }
    }

    private void initializeHeaders(XSSFWorkbook workbook, XSSFSheet sheet) {
        XSSFRow rowHead = sheet.createRow(0);
        CellStyle styleForLocking = getStyleForLocking(workbook, false);

        for (var j = 0; j < MultiplesHelper.HEADERS.size(); j++) {
            rowHead.createCell(j).setCellValue(MultiplesHelper.HEADERS.get(j));
            createCell(rowHead, j, MultiplesHelper.HEADERS.get(j), styleForLocking);
        }
    }

    private void createCell(XSSFRow row, int cellIndex, String value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void initializeData(XSSFWorkbook workbook, XSSFSheet sheet, List<?> multipleCollection,
                                List<String> subMultipleCollection, String leadCaseString) {
        String leadCase = MultiplesHelper.getCurrentLead(leadCaseString);
        log.info("Creating lead case EXCEL STRING: " + leadCaseString);
        log.info("Creating lead case EXCEL: " + leadCase);

        if (multipleCollection.isEmpty()) {
            return;
        }

        var isStringRefsList = multipleCollection.get(0) instanceof String;
        log.info(isStringRefsList ? "Initializing multipleRefs" : "Initializing data");

        var orderedAllCasesList = MultiplesHelper.createOrderedCaseList(multipleCollection);

        final int[] rowIndex = {1};
        orderedAllCasesList.forEach((String caseYear, Map<String, Object> caseYearList) ->
            caseYearList.forEach((String caseNum, Object caseItem) -> {
                var multipleObject = isStringRefsList ? null : (MultipleObject) caseItem;
                var ethosCaseRef = isStringRefsList ? (String) caseItem : multipleObject.getEthosCaseRef();

                constructCaseExcelRow(workbook, sheet, rowIndex[0], ethosCaseRef, leadCase, multipleObject,
                        !subMultipleCollection.isEmpty());
                rowIndex[0]++;
            }
        ));
    }

    private void constructCaseExcelRow(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex, String ethosCaseRef,
                                       String leadCase, MultipleObject multipleObject, boolean hasSubMultiples) {
        CellStyle styleForUnLocking = getStyleForUnLocking(workbook);
        CellStyle styleForLocking = getStyleForLocking(workbook, false);
        CellStyle styleForLockingLead = getStyleForLocking(workbook, true);
        XSSFRow row = sheet.createRow(rowIndex);
        int columnIndex = 0;

        if (ethosCaseRef.equals(leadCase)) {
            log.info("Lead: " + leadCase);
            columnIndex++;
            createCell(row, columnIndex, ethosCaseRef, styleForLockingLead);
        } else {
            columnIndex++;
            createCell(row, columnIndex, ethosCaseRef, styleForLocking);
        }

        if (multipleObject == null) {
            for (var k = 0; k < MultiplesHelper.HEADERS.size() - 1; k++) {
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
}
