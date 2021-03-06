package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import static com.google.common.base.Strings.isNullOrEmpty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ecm.common.model.helper.ScheduleConstants.*;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper.NOT_ALLOCATED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper.SUB_ZERO;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesSchedulePrinter;

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

        var bos = new ByteArrayOutputStream();

        try {

            workbook.write(bos);
            workbook.close();

        } catch (IOException e) {

            log.error("Error generating the excel");

            throw new RuntimeException("Error generating the excel", e);

        }

        return bos.toByteArray();
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

        if (!schedulePayloads.isEmpty()) {

            if (scheduleTemplate.equals(MULTIPLE_SCHEDULE_CONFIG)) {

                log.info("Multiple schedule");
                for (var j = 0; j < multipleHeaders.size(); j++) {
                    createCell(tableTitleRow, j, multipleHeaders.get(j),
                            MultiplesSchedulePrinter.getHeader3CellStyle(workbook));
                }
                for (var i = 0; i < schedulePayloads.size(); i++) {
                    var schedulePayload = schedulePayloads.get(i);

                    for (var j = 0; j < multipleHeaders.size(); j++) {
                        XSSFRow row = sheet.createRow(i + startingRow);
                        createCell(row, j++, schedulePayload.getEthosCaseRef(), cellStyle);
                        createCell(row, j++, getClaimantVsRespondent(schedulePayload), cellStyle);
                    }
                }

            } else if (scheduleTemplate.equals(MULTIPLE_SCHEDULE_DETAILED_CONFIG)) {

                log.info("Multiple schedule detailed");
                for (var j = 0; j < multipleDetailedHeaders.size(); j++) {
                    createCell(tableTitleRow, j, multipleDetailedHeaders.get(j),
                            MultiplesSchedulePrinter.getHeader3CellStyle(workbook));
                }
                for (var i = 0; i < schedulePayloads.size(); i++) {
                    var schedulePayload = schedulePayloads.get(i);

                    for (var j = 0; j < multipleDetailedHeaders.size(); j++) {
                        XSSFRow row = sheet.createRow(i + startingRow);
                        row.setHeightInPoints(((float)4.5 * sheet.getDefaultRowHeightInPoints()));
                        createCell(row, j++, schedulePayload.getEthosCaseRef(), cellStyle);
                        createCell(row, j++, getClaimantAddress(schedulePayload), cellStyle);
                        createCell(row, j++, getRespondentAddress(schedulePayload), cellStyle);
                    }
                }

            }
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
                                                  SortedMap<String, List<SchedulePayload>> schedulePayloadTreeMap) {

        var cellStyle = MultiplesSchedulePrinter.getRowCellStyle(workbook);
        var startingRow = 2;

        if (!schedulePayloadTreeMap.isEmpty()) {
            log.info("Sub Multiple schedule");

            for (Map.Entry<String, List<SchedulePayload>> entry : schedulePayloadTreeMap.entrySet()) {
                List<SchedulePayload> schedulePayloads = entry.getValue();
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
                for (var i = 0; i < schedulePayloads.size(); i++) {
                    var schedulePayload = schedulePayloads.get(i);
                    XSSFRow row = sheet.createRow(startingRow + 2 + i);
                    for (var j = 0; j < subMultipleHeaders.size(); j++) {
                        createCell(row, j++, schedulePayload.getEthosCaseRef(), cellStyle);
                        createCell(row, j++, schedulePayload.getClaimantName(), cellStyle);
                        createCell(row, j++, schedulePayload.getPositionType(), cellStyle);
                    }
                }
                startingRow += 3 + schedulePayloads.size();
            }

        }
    }

    private void initializeSubMultipleDataLogic(XSSFWorkbook workbook, XSSFSheet sheet,
                                                MultipleData multipleData, List<SchedulePayload> schedulePayloads,
                                                SortedMap<String, Object> multipleObjectsFiltered) {

        Map<String, SchedulePayload> scheduleEventMap = schedulePayloads.stream()
                .collect(Collectors.toMap(SchedulePayload::getEthosCaseRef, schedulePayload -> schedulePayload));

        SortedMap<String, List<SchedulePayload>> schedulePayloadTreeMap =
                MultiplesScheduleHelper.getMultipleTreeMap(multipleObjectsFiltered, scheduleEventMap);

        initializeSubMultipleData(workbook, sheet, multipleData, schedulePayloadTreeMap);

    }
}
