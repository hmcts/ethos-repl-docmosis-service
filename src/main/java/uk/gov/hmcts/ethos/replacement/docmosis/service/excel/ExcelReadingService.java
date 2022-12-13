package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.CONSTRAINT_KEY;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_2;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_3;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_4;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_5;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_6;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.SHEET_NAME;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper.SELECT_ALL;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper.NOT_ALLOCATED;

@Slf4j
@Service("excelReadingService")
public class ExcelReadingService {

    private static final String ERROR_SHEET_NAME_NOT_FOUND = "Worksheet name not found";
    private static final String ERROR_DOCUMENT_NOT_VALID = "Document uploaded not valid";

    private final ExcelDocManagementService excelDocManagementService;

    @Autowired
    public ExcelReadingService(ExcelDocManagementService excelDocManagementService, CcdClient ccdClient) {
        this.excelDocManagementService = excelDocManagementService;
    }

    public XSSFWorkbook readWorkbook(String userToken, String documentBinaryUrl) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        var excelInputStream =
                excelDocManagementService.downloadExcelDocument(userToken, documentBinaryUrl);
        return new XSSFWorkbook(excelInputStream);
    }

    public SortedMap<String, Object> readExcel(String userToken, String documentBinaryUrl, List<String> errors,
                                               MultipleData multipleData, FilterExcelType filter) {

        SortedMap<String, Object> multipleObjects = new TreeMap<>();

        try {

            XSSFSheet datatypeSheet = checkExcelErrors(userToken, documentBinaryUrl, errors);

            if (errors.isEmpty()) {

                populateMultipleObjects(multipleObjects, datatypeSheet, multipleData, filter);

            }

        } catch (IOException e) {

            throw new RuntimeException("Error reading the excel for multiple reference"
                    + multipleData.getMultipleReference(), e);

        }

        return multipleObjects;

    }

    public XSSFSheet checkExcelErrors(String userToken, String documentBinaryUrl, List<String> errors)
            throws IOException {

        var workbook = readWorkbook(userToken, documentBinaryUrl);

        XSSFSheet datatypeSheet = workbook.getSheet(SHEET_NAME);

        if (datatypeSheet == null) {

            errors.add(ERROR_SHEET_NAME_NOT_FOUND);

        } else if (!datatypeSheet.validateSheetPassword(CONSTRAINT_KEY)) {

            errors.add(ERROR_DOCUMENT_NOT_VALID);

        }

        return datatypeSheet;

    }

    private void getSubMultipleObjects(SortedMap<String, Object> multipleObjects, String ethosCaseRef,
                                       String subMultiple) {

        if (multipleObjects.containsKey(subMultiple)) {
            List<String> list = (List<String>) multipleObjects.get(subMultiple);
            list.add(ethosCaseRef);
            multipleObjects.put(subMultiple, list);

        } else {
            multipleObjects.put(subMultiple, new ArrayList<>(Collections.singletonList(ethosCaseRef)));
        }

    }

    private void getFlagObjects(SortedMap<String, Object> multipleObjects, String subMultiple,
                                String flag1, String flag2, String flag3, String flag4) {

        populateTreeMapWithSet(multipleObjects, HEADER_2, subMultiple);
        populateTreeMapWithSet(multipleObjects, HEADER_3, flag1);
        populateTreeMapWithSet(multipleObjects, HEADER_4, flag2);
        populateTreeMapWithSet(multipleObjects, HEADER_5, flag3);
        populateTreeMapWithSet(multipleObjects, HEADER_6, flag4);

    }

    private void populateTreeMapWithSet(SortedMap<String, Object> multipleObjects, String key, String value) {

        if (multipleObjects.containsKey(key)) {
            HashSet<String> set = (HashSet<String>) multipleObjects.get(key);
            set.add(value);
            multipleObjects.put(key, set);

        } else {
            multipleObjects.put(key, new HashSet<>(Collections.singletonList(value)));
        }

    }

    private void filterSubMultiple(Row currentRow, MultipleData multipleData,
                                   SortedMap<String, Object> multipleObjects) {
        if (isMultipleInFlagsAndBelongsSubMultiple(currentRow, multipleData)) {
            getSubMultipleObjects(multipleObjects,
                    getCellValue(currentRow.getCell(0)),
                    getCellValue(currentRow.getCell(1)));
        } else {
            if (isMultipleInFlags(currentRow, multipleData)) {
                getSubMultipleObjects(multipleObjects,
                        getCellValue(currentRow.getCell(0)),
                        NOT_ALLOCATED);
            }
        }
    }

    private void populateMultipleObjects(SortedMap<String, Object> multipleObjects, XSSFSheet datatypeSheet,
                                         MultipleData multipleData, FilterExcelType filter) {

        for (Row currentRow : datatypeSheet) {

            if (currentRow.getRowNum() == 0) {
                continue;
            }

            if (filter.equals(FilterExcelType.SUB_MULTIPLE)) {
                filterSubMultiple(currentRow, multipleData, multipleObjects);

            } else if (filter.equals(FilterExcelType.FLAGS)) {
                if (isMultipleInFlags(currentRow, multipleData)) {
                    multipleObjects.put(
                            getCellValue(currentRow.getCell(0)),
                            getCellValue(currentRow.getCell(0)));
                }

            } else if (filter.equals(FilterExcelType.DL_FLAGS)) {
                getFlagObjects(multipleObjects,
                        getCellValue(currentRow.getCell(1)),
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

        return isFilterPassed(currentRow.getCell(1), multipleData.getSubMultiple())
                && isFilterPassed(currentRow.getCell(2), multipleData.getFlag1())
                && isFilterPassed(currentRow.getCell(3), multipleData.getFlag2())
                && isFilterPassed(currentRow.getCell(4), multipleData.getFlag3())
                && isFilterPassed(currentRow.getCell(5), multipleData.getFlag4());
    }

    private boolean isMultipleInFlagsAndBelongsSubMultiple(Row currentRow, MultipleData multipleData) {

        return !getCellValue(currentRow.getCell(1)).equals("")
                && isFilterPassed(currentRow.getCell(2), multipleData.getFlag1())
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
