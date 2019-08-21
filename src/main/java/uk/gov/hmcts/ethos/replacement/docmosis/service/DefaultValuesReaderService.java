package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Service("defaultValuesReaderService")
public class DefaultValuesReaderService {

    private static final String MESSAGE = "Failed to add default values: ";

    public DefaultValues getDefaultValues(String filePath, CaseDetails caseDetails) {
        List<String> values = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            workbook.getSheetAt(0).forEach(row -> {
                if (row.getRowNum() != 0) {
                    row.forEach(cell -> {
                        if (cell.getColumnIndex() == 1) {
                            if (cell.getCellType() == CellType.STRING) {
                                values.add(cell.getStringCellValue());
                            }
                            else if(cell.getCellType() == CellType.NUMERIC) {
                                values.add(NumberToTextConverter.toText(cell.getNumericCellValue()));
                            }
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
            return populatePostDefaultValues(values, caseDetails);
        }
    }

    private DefaultValues populatePreDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .claimantTypeOfClaimant(values.get(0))
                .build();
    }

    private DefaultValues populatePostDefaultValues(List<String> values, CaseDetails caseDetails) {
        String caseTypeId = caseDetails.getCaseTypeId();
        if (caseTypeId.equals(MANCHESTER_CASE_TYPE_ID) || caseTypeId.equals(MANCHESTER_USERS_CASE_TYPE_ID)) {
            return DefaultValues.builder()
                    .positionType(values.get(0))
                    .tribunalCorrespondenceAddressLine1(values.get(1))
                    .tribunalCorrespondenceAddressLine2(values.get(2))
                    .tribunalCorrespondenceAddressLine3(values.get(3))
                    .tribunalCorrespondenceTown(values.get(4))
                    .tribunalCorrespondencePostCode(values.get(5))
                    .tribunalCorrespondenceTelephone(values.get(6))
                    .tribunalCorrespondenceFax(values.get(7))
                    .tribunalCorrespondenceDX(values.get(8))
                    .tribunalCorrespondenceEmail(values.get(9))
                    .build();
        } else {
            if (caseDetails.getCaseData().getManagingOffice() != null) {
                String managingOffice = caseDetails.getCaseData().getManagingOffice();
                switch (managingOffice) {
                    case GLASGOW_OFFICE:
                        return getGlasgowPostDefaultValues(values);
                    case ABERDEEN_OFFICE:
                        return getAberdeenPostDefaultValues(values);
                    case DUNDEE_OFFICE:
                        return getDundeePostDefaultValues(values);
                    default:
                        return getEdinburghPostDefaultValues(values);
                }
            } else {
                return getGlasgowPostDefaultValues(values);
            }
        }

    }

    public CaseData getCaseData(CaseData caseData, DefaultValues defaultValues) {
        if (caseData.getPositionType() == null) {
            caseData.setPositionType(defaultValues.getPositionType());
        }
        if (defaultValues.getManagingOffice() != null) {
            caseData.setManagingOffice(defaultValues.getManagingOffice());
        }
        caseData.setTribunalCorrespondenceAddress(getTribunalCorrespondenceAddress(defaultValues));
        caseData.setTribunalCorrespondenceTelephone(defaultValues.getTribunalCorrespondenceTelephone());
        caseData.setTribunalCorrespondenceFax(defaultValues.getTribunalCorrespondenceFax());
        caseData.setTribunalCorrespondenceDX(defaultValues.getTribunalCorrespondenceDX());
        caseData.setTribunalCorrespondenceEmail(defaultValues.getTribunalCorrespondenceEmail());
        return caseData;
    }

    private Address getTribunalCorrespondenceAddress(DefaultValues defaultValues) {
        Address address = new Address();
        address.setAddressLine1(Optional.ofNullable(defaultValues.getTribunalCorrespondenceAddressLine1()).orElse(""));
        address.setAddressLine2(Optional.ofNullable(defaultValues.getTribunalCorrespondenceAddressLine2()).orElse(""));
        address.setAddressLine3(Optional.ofNullable(defaultValues.getTribunalCorrespondenceAddressLine3()).orElse(""));
        address.setPostTown(Optional.ofNullable(defaultValues.getTribunalCorrespondenceTown()).orElse(""));
        address.setPostCode(Optional.ofNullable(defaultValues.getTribunalCorrespondencePostCode()).orElse(""));
        return address;
    }

    private DefaultValues getGlasgowPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .tribunalCorrespondenceAddressLine1(values.get(10))
                .tribunalCorrespondenceAddressLine2(values.get(11))
                .tribunalCorrespondenceTown(values.get(12))
                .tribunalCorrespondencePostCode(values.get(13))
                .tribunalCorrespondenceTelephone(values.get(14))
                .tribunalCorrespondenceFax(values.get(15))
                .tribunalCorrespondenceDX(values.get(16))
                .tribunalCorrespondenceEmail(values.get(17))
                .managingOffice(values.get(18))
                .build();
    }

    private DefaultValues getAberdeenPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .tribunalCorrespondenceAddressLine1(values.get(19))
                .tribunalCorrespondenceAddressLine2(values.get(20))
                .tribunalCorrespondenceTown(values.get(21))
                .tribunalCorrespondencePostCode(values.get(22))
                .tribunalCorrespondenceTelephone(values.get(23))
                .tribunalCorrespondenceFax(values.get(24))
                .tribunalCorrespondenceDX(values.get(25))
                .tribunalCorrespondenceEmail(values.get(26))
                .build();
    }

    private DefaultValues getDundeePostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .tribunalCorrespondenceAddressLine1(values.get(27))
                .tribunalCorrespondenceAddressLine2(values.get(28))
                .tribunalCorrespondenceAddressLine3(values.get(29))
                .tribunalCorrespondenceTown(values.get(30))
                .tribunalCorrespondencePostCode(values.get(31))
                .tribunalCorrespondenceTelephone(values.get(32))
                .tribunalCorrespondenceFax(values.get(33))
                .tribunalCorrespondenceDX(values.get(34))
                .tribunalCorrespondenceEmail(values.get(35))
                .build();
    }

    private DefaultValues getEdinburghPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .tribunalCorrespondenceAddressLine1(values.get(36))
                .tribunalCorrespondenceTown(values.get(37))
                .tribunalCorrespondencePostCode(values.get(38))
                .tribunalCorrespondenceTelephone(values.get(39))
                .tribunalCorrespondenceFax(values.get(40))
                .tribunalCorrespondenceDX(values.get(41))
                .tribunalCorrespondenceEmail(values.get(42))
                .build();
    }

}
