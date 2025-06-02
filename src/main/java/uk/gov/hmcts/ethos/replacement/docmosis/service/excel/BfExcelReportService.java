package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction.BfActionReportData;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CloseResource"})
@Slf4j
@Service
@RequiredArgsConstructor
public class BfExcelReportService {

    private static final String EXCEL_REPORT_WORKBOOK_NAME = "BF Action Report";
    private static final String CASE_NUMBER_HEADER = "Case No.";
    private static final String ACTION = "Action";
    private static final String DATE_TAKEN = "Date Taken";
    private static final String BF_DATE = "B/F Date";
    private static final String COMMENTS = "Comments";
    private static final List<String> HEADERS = new ArrayList<>(List.of(
            CASE_NUMBER_HEADER, ACTION, DATE_TAKEN,
            BF_DATE, COMMENTS));
    private final ExcelCreationService excelCreationService;

    public byte[] getReportExcelFile(BfActionReportData reportData) {
        if (reportData == null) {
            return new byte[0];
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(EXCEL_REPORT_WORKBOOK_NAME);
        adjustColumnSize(sheet);
        excelCreationService.initializeReportHeaders(reportData.getDocumentName(),
                reportData.getReportPeriodDescription(),
                workbook,
                sheet,
                HEADERS);
        initializeReportData(workbook,
                sheet,
                reportData.getBfDateCollection(),
                reportData.getReportPrintedOnDescription());
        return MultiplesHelper.writeExcelFileToByteArray(workbook);
    }

    private void adjustColumnSize(XSSFSheet sheet) {
        //Adjust the column width to fit the content
        for (int i = 0; i < 4; i++) {
            sheet.setColumnWidth(i, 9000);
        }
        sheet.setColumnWidth(4, 15000);
    }

    private void initializeReportData(XSSFWorkbook workbook, XSSFSheet sheet,
                                      List<BFDateTypeItem> bfDateTypeCollection,
                                      String reportPrintedOnDescription) {
        if (CollectionUtils.isEmpty(bfDateTypeCollection)) {
            return;
        }

        int rowIndex = 3;
        addColumnFilterCellRange(sheet, bfDateTypeCollection.size());

        for (BFDateTypeItem item : bfDateTypeCollection) {
            BFDateType bfDateType = item.getValue();
            constructCaseExcelRow(workbook, sheet, rowIndex, bfDateType);
            rowIndex++;
        }

        excelCreationService.addReportAdminDetails(workbook, sheet, rowIndex, reportPrintedOnDescription, 4);
    }

    private void addColumnFilterCellRange(XSSFSheet sheet, int reportDetailsCount) {
        int firstRow = 2;
        int lastRow = firstRow + reportDetailsCount;
        sheet.setAutoFilter(new CellRangeAddress(firstRow, lastRow, 0, 4));
    }

    private void constructCaseExcelRow(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex,
                                       BFDateType bfDateType) {
        XSSFRow row = sheet.createRow(rowIndex);
        row.setHeight((short)(row.getHeight() * 4));
        int columnIndex = 0;
        CellStyle cellStyle = excelCreationService.getCellStyle(workbook);
        excelCreationService.createCell(row, columnIndex, bfDateType.getCaseReference(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 1, bfDateType.getBroughtForwardAction(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 2,
                formatDate(bfDateType.getBroughtForwardEnteredDate()), cellStyle);
        excelCreationService.createCell(row, columnIndex + 3,
                formatDate(bfDateType.getBroughtForwardDate()), cellStyle);
        if (cellStyle != null) {
            cellStyle.setWrapText(true);
        }
        excelCreationService.createCell(row, columnIndex + 4, bfDateType.getBroughtForwardDateReason(), cellStyle);
    }

    private String formatDate(String bfDate) {
        if (StringUtils.isBlank(bfDate)) {
            return bfDate;
        }
        try {
            var date = OLD_DATE_TIME_PATTERN2.parse(bfDate);
            var targetFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
            return targetFormatter.format(date);
        } catch (DateTimeException e) {
            log.warn(String.format("Unable to parse %s", bfDate), e);
            return bfDate;
        }
    }
}
