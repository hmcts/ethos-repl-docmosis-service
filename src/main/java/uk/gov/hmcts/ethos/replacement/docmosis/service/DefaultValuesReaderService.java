package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABERDEEN_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUNDEE_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EDINBURGH_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PRE_DEFAULT_XLSX_FILE_PATH;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("defaultValuesReaderService")
public class DefaultValuesReaderService {

    private static final String MESSAGE = "Failed to add default values: ";

    public DefaultValues getDefaultValues(String filePath, String managingOffice, String caseTypeId) {
        List<String> values = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            workbook.getSheetAt(0).forEach(row -> {
                if (row.getRowNum() != 0) {
                    row.forEach(cell -> {
                        if (cell.getColumnIndex() == 1) {
                            if (cell.getCellType() == CellType.STRING) {
                                values.add(cell.getStringCellValue());
                            } else if (cell.getCellType() == CellType.NUMERIC) {
                                values.add(NumberToTextConverter.toText(cell.getNumericCellValue()));
                            }
                        }
                    });
                }
            });
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + ex.getMessage());
        }
        if (filePath.equals(PRE_DEFAULT_XLSX_FILE_PATH)) {
            return populatePreDefaultValues(values);
        } else {
            return populatePostDefaultValues(values, managingOffice, caseTypeId);
        }
    }

    private DefaultValues populatePreDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .claimantTypeOfClaimant(values.get(0))
                .build();
    }

    private DefaultValues populatePostDefaultValues(List<String> values, String managingOffice, String caseTypeId) {
        switch (caseTypeId) {
            case MANCHESTER_DEV_CASE_TYPE_ID:
            case MANCHESTER_USERS_CASE_TYPE_ID:
            case MANCHESTER_CASE_TYPE_ID:
                return getManchesterPostDefaultValues(values);
            case BRISTOL_DEV_CASE_TYPE_ID:
            case BRISTOL_USERS_CASE_TYPE_ID:
            case BRISTOL_CASE_TYPE_ID:
                return getBristolPostDefaultValues(values);
            case LEEDS_DEV_CASE_TYPE_ID:
            case LEEDS_USERS_CASE_TYPE_ID:
            case LEEDS_CASE_TYPE_ID:
                return getLeedsPostDefaultValues(values);
            case LONDON_CENTRAL_DEV_CASE_TYPE_ID:
            case LONDON_CENTRAL_USERS_CASE_TYPE_ID:
            case LONDON_CENTRAL_CASE_TYPE_ID:
                return getLondonCentralPostDefaultValues(values);
            case LONDON_EAST_DEV_CASE_TYPE_ID:
            case LONDON_EAST_USERS_CASE_TYPE_ID:
            case LONDON_EAST_CASE_TYPE_ID:
                return getLondonEastPostDefaultValues(values);
            case LONDON_SOUTH_DEV_CASE_TYPE_ID:
            case LONDON_SOUTH_USERS_CASE_TYPE_ID:
            case LONDON_SOUTH_CASE_TYPE_ID:
                return getLondonSouthPostDefaultValues(values);
            case MIDLANDS_EAST_DEV_CASE_TYPE_ID:
            case MIDLANDS_EAST_USERS_CASE_TYPE_ID:
            case MIDLANDS_EAST_CASE_TYPE_ID:
                return getMidlandsEastPostDefaultValues(values);
            case MIDLANDS_WEST_DEV_CASE_TYPE_ID:
            case MIDLANDS_WEST_USERS_CASE_TYPE_ID:
            case MIDLANDS_WEST_CASE_TYPE_ID:
                return getMidlandsWestPostDefaultValues(values);
            case NEWCASTLE_DEV_CASE_TYPE_ID:
            case NEWCASTLE_USERS_CASE_TYPE_ID:
            case NEWCASTLE_CASE_TYPE_ID:
                return getNewcastlePostDefaultValues(values);
            case WALES_DEV_CASE_TYPE_ID:
            case WALES_USERS_CASE_TYPE_ID:
            case WALES_CASE_TYPE_ID:
                return getWalesPostDefaultValues(values);
            case WATFORD_DEV_CASE_TYPE_ID:
            case WATFORD_USERS_CASE_TYPE_ID:
            case WATFORD_CASE_TYPE_ID:
                return getWatfordPostDefaultValues(values);
            case SCOTLAND_DEV_CASE_TYPE_ID:
            case SCOTLAND_USERS_CASE_TYPE_ID:
            case SCOTLAND_CASE_TYPE_ID:
                if (managingOffice != null && !managingOffice.equals("")) {
                    switch (managingOffice) {
                        case EDINBURGH_OFFICE:
                            return getEdinburghPostDefaultValues(values);
                        case ABERDEEN_OFFICE:
                            return getAberdeenPostDefaultValues(values);
                        case DUNDEE_OFFICE:
                            return getDundeePostDefaultValues(values);
                        default:
                            return getGlasgowPostDefaultValues(values);
                    }
                } else {
                    return getGlasgowPostDefaultValues(values);
                }
            default:
                return getManchesterPostDefaultValues(values);
        }
    }

    private DefaultValues getManchesterPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
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
    }

    public void getCaseData(CaseData caseData, DefaultValues defaultValues) {
        if (caseData.getPositionType() == null) {
            caseData.setPositionType(defaultValues.getPositionType());
        }
        if (caseData.getCaseSource() == null || caseData.getCaseSource().trim().equals("")) {
            caseData.setCaseSource(defaultValues.getPositionType());
        }
        if (defaultValues.getManagingOffice() != null) {
            caseData.setManagingOffice(defaultValues.getManagingOffice());
        }
        if (caseData.getCaseType() == null) {
            caseData.setCaseType(defaultValues.getCaseType());
        }
        caseData.setTribunalCorrespondenceAddress(getTribunalCorrespondenceAddress(defaultValues));
        caseData.setTribunalCorrespondenceTelephone(defaultValues.getTribunalCorrespondenceTelephone());
        caseData.setTribunalCorrespondenceFax(defaultValues.getTribunalCorrespondenceFax());
        caseData.setTribunalCorrespondenceDX(defaultValues.getTribunalCorrespondenceDX());
        caseData.setTribunalCorrespondenceEmail(defaultValues.getTribunalCorrespondenceEmail());

        log.info("Adding claimant work address if from respondent");
        if (caseData.getClaimantWorkAddressQuestion() != null && caseData.getClaimantWorkAddressQuestion().equals(YES)
                && caseData.getClaimantWorkAddressQRespondent() != null) {
            ClaimantWorkAddressType claimantWorkAddressType = new ClaimantWorkAddressType();
            String respondentName = caseData.getClaimantWorkAddressQRespondent().getValue().getCode();
            if (caseData.getRespondentCollection() != null) {
                Optional<RespondentSumTypeItem> respondentChosen =
                        caseData.getRespondentCollection().stream().filter(respondentSumTypeItem ->
                        respondentSumTypeItem.getValue().getRespondentName().equals(respondentName)).findFirst();
                respondentChosen.ifPresent(respondentSumTypeItem ->
                        claimantWorkAddressType.setClaimantWorkAddress(
                                respondentSumTypeItem.getValue().getRespondentAddress()));
            }
            caseData.setClaimantWorkAddressQRespondent(null);
            caseData.setClaimantWorkAddress(claimantWorkAddressType);
        }
    }

    public ListingData getListingData(ListingData listingData, DefaultValues defaultValues) {
        listingData.setTribunalCorrespondenceAddress(getTribunalCorrespondenceAddress(defaultValues));
        listingData.setTribunalCorrespondenceTelephone(defaultValues.getTribunalCorrespondenceTelephone());
        listingData.setTribunalCorrespondenceFax(defaultValues.getTribunalCorrespondenceFax());
        listingData.setTribunalCorrespondenceDX(defaultValues.getTribunalCorrespondenceDX());
        listingData.setTribunalCorrespondenceEmail(defaultValues.getTribunalCorrespondenceEmail());
        return listingData;
    }

    private Address getTribunalCorrespondenceAddress(DefaultValues defaultValues) {
        Address address = new Address();
        address.setAddressLine1(
                Optional.ofNullable(defaultValues.getTribunalCorrespondenceAddressLine1()).orElse(""));
        address.setAddressLine2(
                Optional.ofNullable(defaultValues.getTribunalCorrespondenceAddressLine2()).orElse(""));
        address.setAddressLine3(
                Optional.ofNullable(defaultValues.getTribunalCorrespondenceAddressLine3()).orElse(""));
        address.setPostTown(
                Optional.ofNullable(defaultValues.getTribunalCorrespondenceTown()).orElse(""));
        address.setPostCode(
                Optional.ofNullable(defaultValues.getTribunalCorrespondencePostCode()).orElse(""));
        return address;
    }

    private DefaultValues getGlasgowPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
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
                .caseType(values.get(128))
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
                .caseType(values.get(128))
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
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(36))
                .tribunalCorrespondenceTown(values.get(37))
                .tribunalCorrespondencePostCode(values.get(38))
                .tribunalCorrespondenceTelephone(values.get(39))
                .tribunalCorrespondenceFax(values.get(40))
                .tribunalCorrespondenceDX(values.get(41))
                .tribunalCorrespondenceEmail(values.get(42))
                .build();
    }

    private DefaultValues getBristolPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(43))
                .tribunalCorrespondenceAddressLine2(values.get(44))
                .tribunalCorrespondenceTown(values.get(45))
                .tribunalCorrespondencePostCode(values.get(46))
                .tribunalCorrespondenceTelephone(values.get(47))
                .tribunalCorrespondenceFax(values.get(48))
                .tribunalCorrespondenceDX(values.get(49))
                .tribunalCorrespondenceEmail(values.get(50))
                .build();
    }

    private DefaultValues getLeedsPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(51))
                .tribunalCorrespondenceAddressLine2(values.get(52))
                .tribunalCorrespondenceAddressLine3(values.get(53))
                .tribunalCorrespondenceTown(values.get(54))
                .tribunalCorrespondencePostCode(values.get(55))
                .tribunalCorrespondenceTelephone(values.get(56))
                .tribunalCorrespondenceFax(values.get(57))
                .tribunalCorrespondenceDX(values.get(58))
                .tribunalCorrespondenceEmail(values.get(59))
                .build();
    }

    private DefaultValues getLondonCentralPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(60))
                .tribunalCorrespondenceAddressLine2(values.get(61))
                .tribunalCorrespondenceAddressLine3(values.get(62))
                .tribunalCorrespondenceTown(values.get(63))
                .tribunalCorrespondencePostCode(values.get(64))
                .tribunalCorrespondenceTelephone(values.get(65))
                .tribunalCorrespondenceFax(values.get(66))
                .tribunalCorrespondenceDX(values.get(67))
                .tribunalCorrespondenceEmail(values.get(68))
                .build();
    }

    private DefaultValues getLondonEastPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(69))
                .tribunalCorrespondenceAddressLine2(values.get(70))
                .tribunalCorrespondenceAddressLine3(values.get(71))
                .tribunalCorrespondenceTown(values.get(72))
                .tribunalCorrespondencePostCode(values.get(73))
                .tribunalCorrespondenceTelephone(values.get(74))
                .tribunalCorrespondenceFax(values.get(75))
                .tribunalCorrespondenceEmail(values.get(76))
                .build();
    }

    private DefaultValues getLondonSouthPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(77))
                .tribunalCorrespondenceAddressLine2(values.get(78))
                .tribunalCorrespondenceAddressLine3(values.get(79))
                .tribunalCorrespondenceTown(values.get(80))
                .tribunalCorrespondencePostCode(values.get(81))
                .tribunalCorrespondenceTelephone(values.get(82))
                .tribunalCorrespondenceFax(values.get(83))
                .tribunalCorrespondenceDX(values.get(84))
                .tribunalCorrespondenceEmail(values.get(85))
                .build();
    }

    private DefaultValues getMidlandsEastPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(86))
                .tribunalCorrespondenceAddressLine2(values.get(87))
                .tribunalCorrespondenceTown(values.get(88))
                .tribunalCorrespondencePostCode(values.get(89))
                .tribunalCorrespondenceTelephone(values.get(90))
                .tribunalCorrespondenceDX(values.get(91))
                .tribunalCorrespondenceEmail(values.get(92))
                .build();
    }

    private DefaultValues getMidlandsWestPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(93))
                .tribunalCorrespondenceAddressLine2(values.get(94))
                .tribunalCorrespondenceAddressLine3(values.get(95))
                .tribunalCorrespondenceTown(values.get(96))
                .tribunalCorrespondencePostCode(values.get(97))
                .tribunalCorrespondenceTelephone(values.get(98))
                .tribunalCorrespondenceFax(values.get(99))
                .tribunalCorrespondenceEmail(values.get(100))
                .build();
    }

    private DefaultValues getNewcastlePostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(101))
                .tribunalCorrespondenceAddressLine2(values.get(102))
                .tribunalCorrespondenceAddressLine3(values.get(103))
                .tribunalCorrespondenceTown(values.get(104))
                .tribunalCorrespondencePostCode(values.get(105))
                .tribunalCorrespondenceTelephone(values.get(106))
                .tribunalCorrespondenceFax(values.get(107))
                .tribunalCorrespondenceDX(values.get(108))
                .tribunalCorrespondenceEmail(values.get(109))
                .build();
    }

    private DefaultValues getWalesPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(110))
                .tribunalCorrespondenceAddressLine2(values.get(111))
                .tribunalCorrespondenceAddressLine3(values.get(112))
                .tribunalCorrespondenceTown(values.get(113))
                .tribunalCorrespondencePostCode(values.get(114))
                .tribunalCorrespondenceTelephone(values.get(115))
                .tribunalCorrespondenceFax(values.get(116))
                .tribunalCorrespondenceDX(values.get(117))
                .tribunalCorrespondenceEmail(values.get(118))
                .build();
    }

    private DefaultValues getWatfordPostDefaultValues(List<String> values) {
        return DefaultValues.builder()
                .positionType(values.get(0))
                .caseType(values.get(128))
                .tribunalCorrespondenceAddressLine1(values.get(119))
                .tribunalCorrespondenceAddressLine2(values.get(120))
                .tribunalCorrespondenceAddressLine3(values.get(121))
                .tribunalCorrespondenceTown(values.get(122))
                .tribunalCorrespondencePostCode(values.get(123))
                .tribunalCorrespondenceTelephone(values.get(124))
                .tribunalCorrespondenceFax(values.get(125))
                .tribunalCorrespondenceDX(values.get(126))
                .tribunalCorrespondenceEmail(values.get(127))
                .build();
    }

}
