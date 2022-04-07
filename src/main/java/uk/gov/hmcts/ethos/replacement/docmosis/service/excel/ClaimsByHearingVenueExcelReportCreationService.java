package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportDetail;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ClaimsByHearingVenueExcelReportCreationService {
    private static final String EXCEL_REPORT_WORKBOOK_NAME = "Claims By Hearing Venue Report";
    private static final String CASE_NUMBER_HEADER = "Case Number";
    private static final String DATE_OF_RECEIPT_HEADER = "Date of receipt";
    private static final String CLAIMANT_POSTCODE_HEADER = "Claimant Postcode";
    private static final String CLAIMANT_WORK_POSTCODE_HEADER = "Claimant Work Postcode";
    private static final String RESPONDENT_POSTCODE_HEADER = "Respondent Postcode";
    private static final String RESPONDENT_ET3_POSTCODE_HEADER = "Respondent ET3 Postcode";
    private static final List<String> HEADERS = new ArrayList<>(List.of(
        CASE_NUMBER_HEADER, DATE_OF_RECEIPT_HEADER, CLAIMANT_POSTCODE_HEADER,
        CLAIMANT_WORK_POSTCODE_HEADER, RESPONDENT_POSTCODE_HEADER, RESPONDENT_ET3_POSTCODE_HEADER));

    public byte[] getReportExcelFile(ClaimsByHearingVenueReportData reportData) {
        if (reportData == null) {
            return new byte[0];
        }

        var reportDetails = reportData.getReportDetails();
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(EXCEL_REPORT_WORKBOOK_NAME);
        adjustColumnSize(sheet);
        initializeReportHeaders(reportData, workbook, sheet);
        initializeReportData(workbook, sheet, reportDetails, reportData.getReportPrintedOnDescription());
        return writeExcelFileToByteArray(workbook);
    }

    private void adjustColumnSize(XSSFSheet sheet) {
        //Adjust the column width to fit the content
        for (var i = 0; i <= 5; i++) {
            sheet.setColumnWidth(i, 9000);
        }
    }

    private void initializeReportHeaders(ClaimsByHearingVenueReportData reportData, XSSFWorkbook workbook,
                                           XSSFSheet sheet) {
        CellRangeAddress reportTitleCellRange = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(reportTitleCellRange);
        XSSFRow rowReportTitle = sheet.createRow(0);
        rowReportTitle.setHeight((short)(rowReportTitle.getHeight() * 8));
        CellStyle styleForHeaderCell = getReportTitleCellStyle(workbook);
        createCell(rowReportTitle, 0, reportData.getDocumentName(), styleForHeaderCell);

        CellRangeAddress reportPeriodCellRange = new CellRangeAddress(1, 1, 0, 6);
        sheet.addMergedRegion(reportPeriodCellRange);
        XSSFRow rowReportPeriod = sheet.createRow(1);
        rowReportPeriod.setHeight((short)(rowReportPeriod.getHeight() * 6));
        var styleForSubTitleCell = getReportSubTitleCellStyle(workbook);
        createCell(rowReportPeriod, 0, reportData.getReportPeriodDescription(), styleForSubTitleCell);

        XSSFRow rowHead = sheet.createRow(2);
        rowHead.setHeight((short)(rowHead.getHeight() * 4));
        var styleForColHeaderCell = getHeaderCellStyle(workbook);
        for (var j = 0; j < HEADERS.size(); j++) {
            rowHead.createCell(j).setCellValue(HEADERS.get(j));
            createCell(rowHead, j, HEADERS.get(j), styleForColHeaderCell);
        }
        createCell(rowHead, HEADERS.size(), "", styleForColHeaderCell);
    }

    private CellStyle getReportTitleCellStyle(XSSFWorkbook workbook) {
        var font = getFont(workbook);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        font.setFontHeightInPoints((short)25);
        CellStyle cellStyle = getHeadersCellStyle(workbook);
        cellStyle.setFont(font);
        cellStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
        return cellStyle;
    }

    private CellStyle getReportSubTitleCellStyle(XSSFWorkbook workbook) {
        var font = getFont(workbook);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        font.setFontHeightInPoints((short)20);
        CellStyle cellStyle = getHeadersCellStyle(workbook);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private CellStyle getHeaderCellStyle(XSSFWorkbook workbook) {
        var font = getFont(workbook);
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

    private void initializeReportData(XSSFWorkbook workbook, XSSFSheet sheet,
                                      List<ClaimsByHearingVenueReportDetail> reportDetails,
                                      String reportPrintedOnDescription) {
        if (reportDetails.isEmpty()) {
            return;
        }

        int rowIndex = 3;
        addColumnFilterCellRange(sheet, reportDetails.size());

        for (var claim : reportDetails) {
            constructCaseExcelRow(workbook, sheet, rowIndex, claim);
            rowIndex++;
        }

        addReportAdminDetails(workbook, sheet, rowIndex, reportPrintedOnDescription);
    }

    private void addColumnFilterCellRange(XSSFSheet sheet, int reportDetailsCount) {
        var firstRow = 2;
        var lastRow = firstRow + reportDetailsCount;
        sheet.setAutoFilter(new CellRangeAddress(firstRow, lastRow, 0, 5));
    }

    private void addReportAdminDetails(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex,
                                       String reportPrintedOnDescription) {
        CellRangeAddress reportTitleCellRange = new CellRangeAddress(rowIndex, rowIndex, 0, 6);
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

    private void constructCaseExcelRow(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex,
                                       ClaimsByHearingVenueReportDetail item) {
        XSSFRow row = sheet.createRow(rowIndex);
        row.setHeight((short)(row.getHeight() * 4));
        int columnIndex = 0;
        var cellStyle = getCellStyle(workbook);
        createCell(row, columnIndex, item.getCaseReference(), cellStyle);
        createCell(row, columnIndex + 1, item.getDateOfReceipt(), cellStyle);
        createCell(row, columnIndex + 2, item.getClaimantPostcode(), cellStyle);
        createCell(row, columnIndex + 3, item.getClaimantWorkPostcode(), cellStyle);
        createCell(row, columnIndex + 4, item.getRespondentPostcode(), cellStyle);
        createCell(row, columnIndex + 5, item.getRespondentET3Postcode(), cellStyle);
    }

    private Font getFont(XSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibre");
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        font.setFontHeightInPoints((short)16);
        return font;
    }

    private CellStyle getCellStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        var font = getFont(workbook);
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

    private static byte[] writeExcelFileToByteArray(XSSFWorkbook workbook) {
        var bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            workbook.close();
        } catch (IOException e) {
            throw new ReportException("Error generating the excel report", e);
        }

        return bos.toByteArray();
    }

    private void createCell(XSSFRow row, int cellIndex, String value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);

        if (StringUtils.isNotBlank(value)) {
            cell.setCellValue(value);
        }
    }
}
