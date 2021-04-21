package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class MultiplesSchedulePrinter {

    private MultiplesSchedulePrinter() {
    }

    public static CellStyle getHeader1CellStyle(XSSFWorkbook workbook) {
        return getCellStyle(workbook, IndexedColors.BLACK.getIndex(), 14, true);
    }

    public static CellStyle getHeader2CellStyle(XSSFWorkbook workbook) {
        return getCellStyle(workbook, IndexedColors.BLACK.getIndex(), 12, true);
    }

    public static CellStyle getHeader3CellStyle(XSSFWorkbook workbook) {
        return getCellStyle(workbook, IndexedColors.BLACK.getIndex(), 10, true);
    }

    public static CellStyle getRowCellStyle(XSSFWorkbook workbook) {
        return getCellStyle(workbook, IndexedColors.BLACK.getIndex(), 10, false);
    }

    public static CellStyle getCellStyle(XSSFWorkbook workbook, short color, int height, boolean bold) {

        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(color);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        font.setFontHeightInPoints((short) height);
        font.setBold(bold);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        return cellStyle;

    }

    public static void adjustColumnSize(XSSFSheet sheet) {

        //Adjust the column width to fit the content
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

    }

}
