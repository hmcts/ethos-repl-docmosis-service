package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesSchedulePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIST_CASES_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_DETAILED_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.HEADER_1;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.HEADER_2;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.HEADER_3;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.HEADER_4;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.HEADER_5;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.HEADER_6;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.HEADER_SCHEDULE;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.NEW_LINE_CELL;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.SCHEDULE_SHEET_NAME;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper.NOT_ALLOCATED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper.SUB_ZERO;

@Slf4j
@Service("scheduleCreationService")
public class ScheduleCreationService {

    private final List<String> multipleHeaders = new ArrayList<>(Arrays.asList(HEADER_1, HEADER_2));
    private final List<String> multipleDetailedHeaders = new ArrayList<>(Arrays.asList(HEADER_1, HEADER_3, HEADER_4));
    private final List<String> subMultipleHeaders = new ArrayList<>(Arrays.asList(HEADER_1, HEADER_5, HEADER_6));

    public byte[] writeSchedule(MultipleData multipleData, List<SchedulePayload> schedulePayloads,
                                SortedMap<String, Object> multipleObjectsFiltered) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(SCHEDULE_SHEET_NAME);

        initializeHeaders(workbook, sheet, multipleData);

        if (Arrays.asList(MULTIPLE_SCHEDULE_CONFIG, MULTIPLE_SCHEDULE_DETAILED_CONFIG)
                .contains(multipleData.getScheduleDocName())) {
            initializeData(workbook, sheet, schedulePayloads, multipleData.getScheduleDocName());
        } else {
            initializeSubMultipleDataLogic(workbook, sheet, multipleData, schedulePayloads, multipleObjectsFiltered);
        }

        MultiplesSchedulePrinter.adjustColumnSize(sheet);

        return MultiplesHelper.writeExcelFileToByteArray(workbook);
    }

    private void initializeHeaders(XSSFWorkbook workbook, XSSFSheet sheet, MultipleData multipleData) {
        String multipleTitle = multipleData.getMultipleReference() + " - " + multipleData.getMultipleName();
        XSSFRow rowHead1 = sheet.createRow(0);

        if (multipleData.getScheduleDocName().equals(LIST_CASES_CONFIG)) {
            var header1CellStyle = MultiplesSchedulePrinter.getHeader1CellStyle(workbook);
            createCell(rowHead1, 0, "List of cases for ", header1CellStyle);
            createCell(rowHead1, 1, multipleTitle, header1CellStyle);
        } else {
            var header2CellStyle = MultiplesSchedulePrinter.getHeader2CellStyle(workbook);
            createCell(rowHead1, 1, HEADER_SCHEDULE, MultiplesSchedulePrinter.getHeader1CellStyle(workbook));
            XSSFRow rowHead2 = sheet.createRow(1);
            createCell(rowHead2, 0, "Multiple: ", header2CellStyle);
            createCell(rowHead2, 1, multipleTitle, header2CellStyle);
        }
    }

    private void createCell(XSSFRow row, int cellIndex, String value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String getClaimantVsRespondent(SchedulePayload schedulePayload) {
        return schedulePayload.getClaimantName() + " -v- " + schedulePayload.getRespondentName();
    }

    private void initializeData(XSSFWorkbook workbook, XSSFSheet sheet,
                                List<SchedulePayload> schedulePayloads, String scheduleTemplate) {

        var cellStyle = MultiplesSchedulePrinter.getRowCellStyle(workbook);
        var startingRow = 4;
        XSSFRow tableTitleRow = sheet.createRow(3);

        if (schedulePayloads.isEmpty()) {
            return;
        }

        var orderedScheduleCollection = MultiplesHelper.createCollectionOrderedByCaseRef(schedulePayloads);

        if (scheduleTemplate.equals(MULTIPLE_SCHEDULE_CONFIG)) {
            log.info("Multiple schedule");
            for (var j = 0; j < multipleHeaders.size(); j++) {
                createCell(tableTitleRow, j, multipleHeaders.get(j),
                        MultiplesSchedulePrinter.getHeader3CellStyle(workbook));
            }

            final int[] rowIndex = {1};
            orderedScheduleCollection.forEach((String caseYear, Map<String, Object> scheduleYearList) ->
                scheduleYearList.forEach((String caseNum, Object item) -> {
                    var columnIndex = 1;
                    var schedulePayload = (SchedulePayload) item;
                    XSSFRow row = sheet.createRow(rowIndex[0] + startingRow);
                    createCell(row, columnIndex, schedulePayload.getEthosCaseRef(), cellStyle);
                    columnIndex++;
                    createCell(row, columnIndex, getClaimantVsRespondent(schedulePayload), cellStyle);
                    rowIndex[0]++;
                })
            );
        } else if (scheduleTemplate.equals(MULTIPLE_SCHEDULE_DETAILED_CONFIG)) {

            log.info("Multiple schedule detailed");
            for (var j = 0; j < multipleDetailedHeaders.size(); j++) {
                createCell(tableTitleRow, j, multipleDetailedHeaders.get(j),
                        MultiplesSchedulePrinter.getHeader3CellStyle(workbook));
            }

            final int[] rowIndex = {1};
            orderedScheduleCollection.forEach((String caseYear, Map<String, Object> scheduleYearList) ->
                scheduleYearList.forEach((String caseNum, Object item) -> {
                    var columnIndex = 1;
                    var schedulePayload = (SchedulePayload) item;
                    XSSFRow row = sheet.createRow(rowIndex[0] + startingRow);
                    row.setHeightInPoints(((float) 4.5 * sheet.getDefaultRowHeightInPoints()));
                    createCell(row, columnIndex, schedulePayload.getEthosCaseRef(), cellStyle);
                    columnIndex++;
                    createCell(row, columnIndex, getClaimantAddress(schedulePayload), cellStyle);
                    columnIndex++;
                    createCell(row, columnIndex, getRespondentAddress(schedulePayload), cellStyle);
                    rowIndex[0]++;
                })
            );
        }
    }

    private String getClaimantAddress(SchedulePayload schedulePayload) {
        var sb = new StringBuilder();
        sb.append(schedulePayload.getClaimantName());
        if (!isNullOrEmpty(schedulePayload.getClaimantAddressLine1())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getClaimantAddressLine1());
        }
        if (!isNullOrEmpty(schedulePayload.getClaimantAddressLine2())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getClaimantAddressLine2());
        }
        if (!isNullOrEmpty(schedulePayload.getClaimantAddressLine3())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getClaimantAddressLine3());
        }
        if (!isNullOrEmpty(schedulePayload.getClaimantTown())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getClaimantTown());
        }
        if (!isNullOrEmpty(schedulePayload.getClaimantPostCode())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getClaimantPostCode());
        }
        return sb.toString();
    }

    private String getRespondentAddress(SchedulePayload schedulePayload) {
        var sb = new StringBuilder();
        sb.append(schedulePayload.getRespondentName());
        if (!isNullOrEmpty(schedulePayload.getRespondentAddressLine1())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getRespondentAddressLine1());
        }
        if (!isNullOrEmpty(schedulePayload.getRespondentAddressLine2())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getRespondentAddressLine2());
        }
        if (!isNullOrEmpty(schedulePayload.getRespondentAddressLine3())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getRespondentAddressLine3());
        }
        if (!isNullOrEmpty(schedulePayload.getRespondentTown())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getRespondentTown());
        }
        if (!isNullOrEmpty(schedulePayload.getRespondentPostCode())) {
            sb.append(NEW_LINE_CELL).append(schedulePayload.getRespondentPostCode());
        }
        return sb.toString();
    }

    private String getSubMultipleRef(MultipleData multipleData, String subMultipleName) {

        return multipleData.getSubMultipleCollection().stream()
                .filter(subMultipleTypeItem ->
                        subMultipleTypeItem.getValue().getSubMultipleName().equals(subMultipleName))
                .map(subMultipleTypeItem -> subMultipleTypeItem.getValue().getSubMultipleRef())
                .findFirst()
                .orElse("");

    }

    private String getSubMultipleTitle(MultipleData multipleData, String subMultipleName) {

        if (subMultipleName.equals(NOT_ALLOCATED)) {
            return subMultipleName.replace("_", " ")
                    + " " + multipleData.getMultipleReference() + SUB_ZERO;
        } else {
            return "SubMultiple " + subMultipleName + " " + getSubMultipleRef(multipleData, subMultipleName);
        }
    }

    private void initializeSubMultipleData(XSSFWorkbook workbook, XSSFSheet sheet, MultipleData multipleData,
                                           SortedMap<String, SortedMap<String, SortedMap<String, Object>>> schedulePayloadTreeMap) {

        var cellStyle = MultiplesSchedulePrinter.getRowCellStyle(workbook);
        var startingRow = 2;

        if (!schedulePayloadTreeMap.isEmpty()) {
            log.info("Sub Multiple schedule");

            for (Map.Entry<String, SortedMap<String, SortedMap<String, Object>>> entry : schedulePayloadTreeMap.entrySet()) {
                SortedMap<String, SortedMap<String, Object>> schedulePayloads = entry.getValue();
                //TITLE ROW
                XSSFRow subMultipleRow = sheet.createRow(startingRow);
                createCell(subMultipleRow, 0, getSubMultipleTitle(multipleData, entry.getKey()),
                        MultiplesSchedulePrinter.getHeader3CellStyle(workbook));
                //SUBTITLE ROW
                XSSFRow tableTitleRow = sheet.createRow(startingRow + 1);
                for (var j = 0; j < subMultipleHeaders.size(); j++) {
                    createCell(tableTitleRow, j, subMultipleHeaders.get(j),
                            MultiplesSchedulePrinter.getHeader3CellStyle(workbook));
                }
                //DATA ROWS
                final int[] rowIndex = {1};
                int entryStartingRow = startingRow;
                schedulePayloads.forEach((String caseYear, Map<String, Object> scheduleYearList) ->
                    scheduleYearList.forEach((String caseNum, Object caseItem) -> {
                        var columnIndex = 1;
                        var schedulePayload = (SchedulePayload) caseItem;
                        XSSFRow row = sheet.createRow(entryStartingRow + 2 + rowIndex[0]);
                        createCell(row, columnIndex, schedulePayload.getEthosCaseRef(), cellStyle);
                        columnIndex++;
                        createCell(row, columnIndex, schedulePayload.getClaimantName(), cellStyle);
                        columnIndex++;
                        createCell(row, columnIndex, schedulePayload.getPositionType(), cellStyle);
                        rowIndex[0]++;
                    })
                );

                startingRow += 2 + rowIndex[0];
            }
        }
    }

    private void initializeSubMultipleDataLogic(XSSFWorkbook workbook, XSSFSheet sheet,
                                                MultipleData multipleData, List<SchedulePayload> schedulePayloads,
                                                SortedMap<String, Object> multipleObjectsFiltered) {

        Map<String, SchedulePayload> scheduleEventMap = schedulePayloads.stream()
                .collect(Collectors.toMap(SchedulePayload::getEthosCaseRef, schedulePayload -> schedulePayload));

        SortedMap<String, SortedMap<String, SortedMap<String, Object>>> schedulePayloadTreeMap =
                MultiplesScheduleHelper.getMultipleTreeMap(multipleObjectsFiltered, scheduleEventMap);

        initializeSubMultipleData(workbook, sheet, multipleData, schedulePayloadTreeMap);
    }
}
