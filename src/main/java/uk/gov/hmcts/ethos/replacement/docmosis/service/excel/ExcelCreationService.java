package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
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
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            workbook.write(bos);
            workbook.close();

        } catch (IOException e) {

            log.error("Error generating the excel");

            throw new RuntimeException("Error generating the excel", e);

        }

        return bos.toByteArray();
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

    private void createCell(XSSFRow row, int cellIndex, String value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void initializeData(XSSFWorkbook workbook, XSSFSheet sheet, List<?> multipleCollection,
                                List<String> subMultipleCollection, String leadCaseString) {

        CellStyle styleForUnLocking = getStyleForUnLocking(workbook);
        CellStyle styleForLocking = getStyleForLocking(workbook, false);
        CellStyle styleForLockingLead = getStyleForLocking(workbook, true);
        String leadCase = MultiplesHelper.getCurrentLead(leadCaseString);
        log.info("Creating lead case EXCEL STRING: " + leadCaseString);
        log.info("Creating lead case EXCEL: " + leadCase);

        if (!multipleCollection.isEmpty()) {
            if (multipleCollection.get(0) instanceof String) {
                log.info("Initializing multipleRefs");

                for (int i = 1; i < multipleCollection.size() + 1; i++) {
                    for (int j = 0; j < MultiplesHelper.HEADERS.size(); j++) {
                        XSSFRow row = sheet.createRow(i);
                        String ethosCaseRef =  multipleCollection.get(i - 1).toString();
                        if (ethosCaseRef.equals(leadCase)) {
                            createCell(row, j++, ethosCaseRef, styleForLockingLead);
                        } else {
                            createCell(row, j++, ethosCaseRef, styleForLocking);
                        }

                        for (int k = 0; k < MultiplesHelper.HEADERS.size() - 1; k++) {
                            if (k == 0 && subMultipleCollection.isEmpty()) {
                                createCell(row, j++, "", styleForLocking);
                            } else {
                                // Create empty cells unlocked
                                createCell(row, j++, "", styleForUnLocking);
                            }
                        }
                    }
                }

            } else {
                log.info("Initializing data");

                for (int i = 1; i < multipleCollection.size() + 1; i++) {
                    MultipleObject multipleObject = (MultipleObject) multipleCollection.get(i - 1);
                    for (int j = 0; j < MultiplesHelper.HEADERS.size(); j++) {
                        XSSFRow row = sheet.createRow(i);
                        if (multipleObject.getEthosCaseRef().equals(leadCase)) {
                            log.info("Lead: " + leadCase);
                            createCell(row, j++, multipleObject.getEthosCaseRef(), styleForLockingLead);
                        } else {
                            createCell(row, j++, multipleObject.getEthosCaseRef(), styleForLocking);
                        }
                        if (subMultipleCollection.isEmpty()) {
                            createCell(row, j++, multipleObject.getSubMultiple(), styleForLocking);
                        } else {
                            createCell(row, j++, multipleObject.getSubMultiple(), styleForUnLocking);
                        }
                        // Create these cells unlocked
                        createCell(row, j++, multipleObject.getFlag1(), styleForUnLocking);
                        createCell(row, j++, multipleObject.getFlag2(), styleForUnLocking);
                        createCell(row, j++, multipleObject.getFlag3(), styleForUnLocking);
                        createCell(row, j++, multipleObject.getFlag4(), styleForUnLocking);
                    }
                }
            }
        }
    }
}
