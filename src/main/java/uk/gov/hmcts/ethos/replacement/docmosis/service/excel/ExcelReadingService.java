package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper.SELECT_ALL;

@Slf4j
@Service("excelReadingService")
public class ExcelReadingService {

    private static final String ERROR_SHEET_NAME_NOT_FOUND = "SheetName not found";

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

    private void getFlagObjects(TreeMap<String, Object> multipleObjects, String flag1, String flag2, String flag3, String flag4) {

        populateTreeMapWithSet(multipleObjects, HEADER_3, flag1);
        populateTreeMapWithSet(multipleObjects, HEADER_4, flag2);
        populateTreeMapWithSet(multipleObjects, HEADER_5, flag3);
        populateTreeMapWithSet(multipleObjects, HEADER_6, flag4);

    }

    private void populateTreeMapWithSet(TreeMap<String, Object> multipleObjects, String key, String value) {

        if (multipleObjects.containsKey(key)) {
            HashSet<String> set = (HashSet<String>) multipleObjects.get(key);
            set.add(value);
            multipleObjects.put(key, set);

        } else {
            multipleObjects.put(key, new HashSet<>(Collections.singletonList(value)));
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

            } else if (filter.equals(FilterExcelType.DL_FLAGS)) {
                getFlagObjects(multipleObjects,
                        getCellValue(currentRow.getCell(2)),
                        getCellValue(currentRow.getCell(3)),
                        getCellValue(currentRow.getCell(4)),
                        getCellValue(currentRow.getCell(5)));

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
                .flag3(getCellValue(currentRow.getCell(4)))
                .flag4(getCellValue(currentRow.getCell(5)))
                .build();
    }

    private boolean isMultipleInFlags(Row currentRow, MultipleData multipleData) {

        return isFilterPassed(currentRow.getCell(2), multipleData.getFlag1())
                && isFilterPassed(currentRow.getCell(3), multipleData.getFlag2())
                && isFilterPassed(currentRow.getCell(4), multipleData.getFlag3())
                && isFilterPassed(currentRow.getCell(5), multipleData.getFlag4());
    }

    private boolean isMultipleInFlagsAndBelongsSubMultiple(Row currentRow, MultipleData multipleData) {

        return !getCellValue(currentRow.getCell(1)).equals("") &&
                isFilterPassed(currentRow.getCell(2), multipleData.getFlag1())
                && isFilterPassed(currentRow.getCell(3), multipleData.getFlag2())
                && isFilterPassed(currentRow.getCell(4), multipleData.getFlag3())
                && isFilterPassed(currentRow.getCell(5), multipleData.getFlag4());
    }

    private boolean isFilterPassed(Cell cell, DynamicFixedListType flag) {

        if (flag != null) {

            return flag.getValue().getCode().equals(SELECT_ALL)
                    || getCellValue(cell).equals(flag.getValue().getCode());

        } else {

            return getCellValue(cell).equals("");
        }

    }

}
