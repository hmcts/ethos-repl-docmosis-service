package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("defaultValuesReaderService")
public class DefaultValuesReaderService {

    public static final String PRE_DEFAULT_XLSX_FILE_PATH = "preDefaultValues.xlsx";
    public static final String POST_DEFAULT_XLSX_FILE_PATH = "postDefaultValues.xlsx";
    private static final String MESSAGE = "Failed to add default values: ";

    public DefaultValues getDefaultValues(String filePath) {
        List<String> values = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            WorkbookFactory.create(inputStream).getSheetAt(0).forEach(row -> {
                if (row.getRowNum() != 0) {
                    row.forEach(cell -> {
                        if (cell.getColumnIndex() == 1) {
                            values.add(cell.getStringCellValue());
                        }
                    });
                }
            });
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + ex.getMessage());
        }
        if (filePath.equals(PRE_DEFAULT_XLSX_FILE_PATH))
            return populatePreDefaultValues(values);
        else {
            return populatePostDefaultValues(values);
        }
    }

    private DefaultValues populatePreDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .claimantTypeOfClaimant(values.get(0))
                .build();
    }

    private DefaultValues populatePostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .build();
    }

}
