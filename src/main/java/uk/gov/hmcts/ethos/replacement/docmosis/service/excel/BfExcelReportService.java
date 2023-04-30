package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.et.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.et.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction.BfActionReportData;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CloseResource"})
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
        for (int i = 0; i < 5; i++) {
            sheet.setColumnWidth(i, 9000);
        }
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

        excelCreationService.addReportAdminDetails(workbook, sheet, rowIndex, reportPrintedOnDescription, 5);
    }

    private void addColumnFilterCellRange(XSSFSheet sheet, int reportDetailsCount) {
        int firstRow = 2;
        int lastRow = firstRow + reportDetailsCount;
        sheet.setAutoFilter(new CellRangeAddress(firstRow, lastRow, 0, 5));
    }

    private void constructCaseExcelRow(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex,
                                       BFDateType bfDateType) {
        XSSFRow row = sheet.createRow(rowIndex);
        row.setHeight((short)(row.getHeight() * 4));
        int columnIndex = 0;
        CellStyle cellStyle = excelCreationService.getCellStyle(workbook);
        excelCreationService.createCell(row, columnIndex, bfDateType.getCaseReference(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 1, bfDateType.getBroughtForwardAction(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 2, bfDateType.getBroughtForwardEnteredDate(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 3, bfDateType.getBroughtForwardDate(), cellStyle);
        if (cellStyle != null) {
            cellStyle.setWrapText(true);
        }
        excelCreationService.createCell(row, columnIndex + 4, bfDateType.getBroughtForwardDateReason(), cellStyle);
    }
}
