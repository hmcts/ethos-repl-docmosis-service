package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;

@Slf4j
@Service("excelCreationService")
public class ExcelCreationService {

    //private static final String ERROR_MESSAGE_TITLE = "Not Applicable";
    //private static final String ERROR_MESSAGE = "Cannot change the value";

    public byte[] writeExcel(List<?> multipleCollection) {

//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
//
//        Object[][] data = initializeData(multipleCollection);
//
//        //Add validation header
//        addValidation(sheet, new CellRangeAddressList(FIRST_ROW, 0, FIRST_COL, LAST_COL));
//        //Add validation multipleRefs
//        addValidation(sheet, new CellRangeAddressList(FIRST_ROW, END_ROW, FIRST_COL, 0));
//
//        //Get cell style
//        CellStyle style = getCellStyle(workbook);
//
//        writeRowsAndCells(sheet, data, style);
//
//        //Adjust column size
//        adjustColumnSize(sheet);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAME);

        enableLocking(sheet);

        initializeHeaders(sheet);

        initializeData(workbook, sheet, multipleCollection);

        adjustColumnSize(sheet);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            workbook.write(bos);
            workbook.close();

        } catch (IOException e) {

            log.error("Error generating the excel");

        }

        return bos.toByteArray();
    }

//    private Object[][] initializeData(List<?> multipleCollection) {
//
//        List<String> headers = new ArrayList<>(Arrays.asList(HEADER_1, HEADER_2, HEADER_3, HEADER_4, HEADER_5));
//
//        Object[][] data = new Object[multipleCollection.size()+1][MAX_COLUMN_SIZE_XSLX];
//
//        //Initialize headers
//        for (int j = 0; j < MAX_COLUMN_SIZE_XSLX; j++) {
//            data[0][j] = headers.get(j);
//        }
//
//        //Initialize data
//        if (!multipleCollection.isEmpty()) {
//            if (multipleCollection.get(0) instanceof String) {
//                log.info("Initializing multipleRefs");
//                for (int i = 1; i < data.length; i++) {
//                    data[i][0] = multipleCollection.get(i - 1);
//                }
//            } else {
//                MultipleObject multipleObject;
//                log.info("Initializing data");
//                for (int i = 1; i < data.length; i++) {
//                    multipleObject = (MultipleObject) multipleCollection.get(i - 1);
//                    data[i][0] = multipleObject.getEthosCaseRef();
//                    data[i][1] = multipleObject.getSubMultiple();
//                    data[i][2] = multipleObject.getFlag1();
//                    data[i][3] = multipleObject.getFlag2();
//                    data[i][4] = multipleObject.getEQP();
//                }
//            }
//        }
//
//        return data;
//    }

//    private void writeRowsAndCells(XSSFSheet sheet, Object[][] data, CellStyle style) {
//
//        int rowNum = 0;
//        for (Object[] datatype : data) {
//            Row row = sheet.createRow(rowNum++);
//            int colNum = 0;
//            for (Object field : datatype) {
//                Cell cell = row.createCell(colNum++);
//                if (field instanceof String) {
//                    cell.setCellValue((String) field);
//                }
//                cell.setCellStyle(style);
//            }
//        }
//    }

//    private CellStyle getCellStyle(XSSFWorkbook workbook) {
//
//        //Alignment cells to CENTER
//        CellStyle style = workbook.createCellStyle();
//        style.setAlignment(HorizontalAlignment.CENTER);
//
//        return style;
//    }

//    private void adjustColumnSize(XSSFSheet sheet) {
//
//        //Adjust the column width to fit the content
//        sheet.autoSizeColumn(0);
//        sheet.autoSizeColumn(1);
//    }

//    private void addValidation(XSSFSheet sheet, CellRangeAddressList addressList) {
//
//        DataValidationHelper helper = sheet.getDataValidationHelper();
//        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{CONSTRAINT_KEY});
//        DataValidation dataValidation = helper.createValidation(constraint, addressList);
//        dataValidation.createErrorBox(ERROR_MESSAGE_TITLE, ERROR_MESSAGE);
//        dataValidation.setSuppressDropDownArrow(false);
//        dataValidation.setShowErrorBox(true);
//
//        sheet.addValidationData(dataValidation);
//
//    }

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

    private void adjustColumnSize(XSSFSheet sheet) {

        //Adjust the column width to fit the content
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void initializeHeaders(XSSFSheet sheet) {
        List<String> headers = new ArrayList<>(Arrays.asList(HEADER_1, HEADER_2, HEADER_3, HEADER_4, HEADER_5));

        XSSFRow rowHead = sheet.createRow((short) 0);

        for (int j = 0; j < headers.size(); j++) {
            rowHead.createCell(j).setCellValue(headers.get(j));
        }

    }

    private void createCell(XSSFRow row, int cellIndex, String value, CellStyle styleForUnLocking) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        cell.setCellStyle(styleForUnLocking);
    }

    private void initializeData(XSSFWorkbook workbook, XSSFSheet sheet, List<?> multipleCollection) {

        CellStyle styleForUnLocking = getStyleForUnLocking(workbook);

        if (!multipleCollection.isEmpty()) {
            if (multipleCollection.get(0) instanceof String) {
                log.info("Initializing multipleRefs");

                for (int i = 1; i < multipleCollection.size() + 1; i++) {
                    for (int j = 0; j < 5; j++) {
                        XSSFRow row = sheet.createRow((short) i);
                        row.createCell(j++).setCellValue(multipleCollection.get(i - 1).toString());
                        // Create empty cells unlocked
                        for (int k = 0; k < 4; k++) {
                            createCell(row, j++, "", styleForUnLocking);
                        }
                    }
                }

            } else {
                log.info("Initializing data");

                for (int i = 1; i < multipleCollection.size() + 1; i++) {
                    MultipleObject multipleObject = (MultipleObject) multipleCollection.get(i - 1);
                    for (int j = 0; j < 5; j++) {
                        XSSFRow row = sheet.createRow((short) i);
                        row.createCell(j++).setCellValue(multipleObject.getEthosCaseRef());
                        // Create these cells unlocked
                        createCell(row, j++, multipleObject.getSubMultiple(), styleForUnLocking);
                        createCell(row, j++, multipleObject.getFlag1(), styleForUnLocking);
                        createCell(row, j++, multipleObject.getFlag2(), styleForUnLocking);
                        createCell(row, j++, multipleObject.getEQP(), styleForUnLocking);
                    }
                }
            }
        }
    }
}
