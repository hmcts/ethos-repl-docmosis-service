package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.SHEET_NAME;

@Slf4j
@Service("excelReadingService")
public class ExcelReadingService {

    private static final String ERROR_SHEET_NAME_NOT_FOUND = "Error: SheetName not found";

    private final ExcelDocManagementService excelDocManagementService;

    @Autowired
    public ExcelReadingService(ExcelDocManagementService excelDocManagementService) {
        this.excelDocManagementService = excelDocManagementService;
    }

    public TreeMap<String, Object> readExcel(String userToken, String documentBinaryUrl, List<String> errors,
                                             MultipleData multipleData, FilterExcelType filter) {

        TreeMap<String, Object> multipleObjects = new TreeMap<>();

        try {

            Sheet datatypeSheet = checkExcelErrors(userToken, documentBinaryUrl, errors);

            if (errors.isEmpty()) {

                populateMultipleObjects(multipleObjects, datatypeSheet, multipleData, filter);

            }

        } catch (IOException e) {
            log.error("Error reading the Excel");
        }

        return multipleObjects;

    }

    public Sheet checkExcelErrors(String userToken, String documentBinaryUrl, List<String> errors) throws IOException {

        InputStream excelInputStream =
                excelDocManagementService.downloadExcelDocument(userToken, documentBinaryUrl);

        Workbook workbook = new XSSFWorkbook(excelInputStream);

        Sheet datatypeSheet = workbook.getSheet(SHEET_NAME);

        if (datatypeSheet == null) {

            errors.add(ERROR_SHEET_NAME_NOT_FOUND);

        }

        return datatypeSheet;

    }

    private void getSubMultipleObjects(TreeMap<String, Object> multipleObjects, String ethosCaseRef, String subMultiple) {

        if (multipleObjects.containsKey(subMultiple)) {
            List<String> list = (List<String>) multipleObjects.get(subMultiple);
            list.add(ethosCaseRef);
            multipleObjects.put(subMultiple, list);

        } else {
            multipleObjects.put(subMultiple, new ArrayList<>(Collections.singletonList(ethosCaseRef)));
        }

    }

    private void populateMultipleObjects(TreeMap<String, Object> multipleObjects, Sheet datatypeSheet,
                                         MultipleData multipleData, FilterExcelType filter) {

        for (Row currentRow : datatypeSheet) {

            if (currentRow.getRowNum() == 0) {
                continue;
            }

            if (filter.equals(FilterExcelType.SUB_MULTIPLE)) {
                if (isMultipleInFlagsAndBelongsSubMultiple(currentRow, multipleData)) {
                    getSubMultipleObjects(multipleObjects,
                            getCellValue(currentRow.getCell(0)),
                            getCellValue(currentRow.getCell(1)));
                }

            } else if (filter.equals(FilterExcelType.FLAGS)) {
                if (isMultipleInFlags(currentRow, multipleData)) {
                    multipleObjects.put(
                            getCellValue(currentRow.getCell(0)),
                            getCellValue(currentRow.getCell(0)));
                }

            } else {
                multipleObjects.put(
                        getCellValue(currentRow.getCell(0)),
                        getMultipleObject(currentRow));

            }
        }
    }

    private String getCellValue(Cell currentCell) {

        if (currentCell.getCellType() == CellType.STRING) {

            return currentCell.getStringCellValue();

        } else if (currentCell.getCellType() == CellType.NUMERIC) {

            return NumberToTextConverter.toText(currentCell.getNumericCellValue());

        } else {

            return "";

        }

    }

    private MultipleObject getMultipleObject(Row currentRow) {

        return MultipleObject.builder()
                .ethosCaseRef(getCellValue(currentRow.getCell(0)))
                .subMultiple(getCellValue(currentRow.getCell(1)))
                .flag1(getCellValue(currentRow.getCell(2)))
                .flag2(getCellValue(currentRow.getCell(3)))
                .EQP(getCellValue(currentRow.getCell(4)))
                .build();
    }

    private boolean isMultipleInFlags(Row currentRow, MultipleData multipleData) {

        return getCellValue(currentRow.getCell(2)).equals(multipleData.getFlag1())
                && getCellValue(currentRow.getCell(3)).equals(multipleData.getFlag2())
                && getCellValue(currentRow.getCell(4)).equals(multipleData.getEQP());
    }

    private boolean isMultipleInFlagsAndBelongsSubMultiple(Row currentRow, MultipleData multipleData) {

        return !getCellValue(currentRow.getCell(1)).equals("") &&
                getCellValue(currentRow.getCell(2)).equals(multipleData.getFlag1())
                && getCellValue(currentRow.getCell(3)).equals(multipleData.getFlag2())
                && getCellValue(currentRow.getCell(4)).equals(multipleData.getEQP());
    }

}
