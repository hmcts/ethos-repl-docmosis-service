package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportDetail;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.CloseResource", "PMD.LawOfDemeter"})
public class ClaimsByHearingVenueExcelReportCreationService {
    private static final String EXCEL_REPORT_WORKBOOK_NAME = "Claims By Hearing Venue Report";
    private static final String CASE_NUMBER_HEADER = "Case Number";
    private static final String DATE_OF_RECEIPT_HEADER = "Date of receipt";
    private static final String CLAIMANT_POSTCODE_HEADER = "Claimant Postcode";
    private static final String CLAIMANT_WORK_POSTCODE_HEADER = "Claimant Work Postcode";
    private static final String RESPONDENT_POSTCODE_HEADER = "Respondent Postcode";
    private static final String RESPONDENT_ET3_POSTCODE_HEADER = "Respondent ET3 Postcode";
    private final ExcelCreationService excelCreationService;
    private static final List<String> HEADERS = new ArrayList<>(List.of(
            CASE_NUMBER_HEADER, DATE_OF_RECEIPT_HEADER, CLAIMANT_POSTCODE_HEADER,
            CLAIMANT_WORK_POSTCODE_HEADER, RESPONDENT_POSTCODE_HEADER, RESPONDENT_ET3_POSTCODE_HEADER));

    public byte[] getReportExcelFile(ClaimsByHearingVenueReportData reportData) {
        if (reportData == null) {
            return new byte[0];
        }

        List<ClaimsByHearingVenueReportDetail> reportDetails = reportData.getReportDetails();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(EXCEL_REPORT_WORKBOOK_NAME);
        adjustColumnSize(sheet);
        excelCreationService.initializeReportHeaders(reportData.getDocumentName(),
                reportData.getReportPeriodDescription(),
                workbook,
                sheet,
                HEADERS);
        initializeReportData(workbook, sheet, reportDetails, reportData.getReportPrintedOnDescription());
        return MultiplesHelper.writeExcelFileToByteArray(workbook);
    }

    private void adjustColumnSize(XSSFSheet sheet) {
        //Adjust the column width to fit the content
        for (int i = 0; i <= 5; i++) {
            sheet.setColumnWidth(i, 9000);
        }
    }

    private void initializeReportData(XSSFWorkbook workbook, XSSFSheet sheet,
                                      List<ClaimsByHearingVenueReportDetail> reportDetails,
                                      String reportPrintedOnDescription) {
        if (reportDetails.isEmpty()) {
            return;
        }

        int rowIndex = 3;
        addColumnFilterCellRange(sheet, reportDetails.size());

        for (ClaimsByHearingVenueReportDetail claim : reportDetails) {
            constructCaseExcelRow(workbook, sheet, rowIndex, claim);
            rowIndex++;
        }
        excelCreationService.addReportAdminDetails(workbook, sheet, rowIndex, reportPrintedOnDescription, 6);
    }

    private void addColumnFilterCellRange(XSSFSheet sheet, int reportDetailsCount) {
        int firstRow = 2;
        int lastRow = firstRow + reportDetailsCount;
        sheet.setAutoFilter(new CellRangeAddress(firstRow, lastRow, 0, 5));
    }

    private void constructCaseExcelRow(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex,
                                       ClaimsByHearingVenueReportDetail item) {
        XSSFRow row = sheet.createRow(rowIndex);
        row.setHeight((short)(row.getHeight() * 4));
        int columnIndex = 0;
        CellStyle cellStyle = excelCreationService.getCellStyle(workbook);
        excelCreationService.createCell(row, columnIndex, item.getCaseReference(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 1, item.getDateOfReceipt(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 2, item.getClaimantPostcode(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 3, item.getClaimantWorkPostcode(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 4, item.getRespondentPostcode(), cellStyle);
        excelCreationService.createCell(row, columnIndex + 5, item.getRespondentET3Postcode(), cellStyle);
    }
}