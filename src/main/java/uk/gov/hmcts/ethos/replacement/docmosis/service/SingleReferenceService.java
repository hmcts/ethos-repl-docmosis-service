package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefBristolRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLeedsRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLondonCentralRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLondonEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLondonSouthRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefManchesterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefMidlandsEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefMidlandsWestRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefNewcastleRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefScotlandRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefWalesRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefWatfordRepository;
import java.time.LocalDate;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_USERS_CASE_TYPE_ID;

@Slf4j
@RequiredArgsConstructor
@Service("singleReferenceService")
public class SingleReferenceService {

    private final SingleRefManchesterRepository singleRefManchesterRepository;
    private final SingleRefScotlandRepository singleRefScotlandRepository;
    private final SingleRefLeedsRepository singleRefLeedsRepository;
    private final SingleRefMidlandsWestRepository singleRefMidlandsWestRepository;
    private final SingleRefMidlandsEastRepository singleRefMidlandsEastRepository;
    private final SingleRefBristolRepository singleRefBristolRepository;
    private final SingleRefWalesRepository singleRefWalesRepository;
    private final SingleRefNewcastleRepository singleRefNewcastleRepository;
    private final SingleRefWatfordRepository singleRefWatfordRepository;
    private final SingleRefLondonCentralRepository singleRefLondonCentralRepository;
    private final SingleRefLondonSouthRepository singleRefLondonSouthRepository;
    private final SingleRefLondonEastRepository singleRefLondonEastRepository;

    public synchronized String createReference(String caseTypeId, int numberCases) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        switch (caseTypeId) {
            case MANCHESTER_DEV_CASE_TYPE_ID:
            case MANCHESTER_USERS_CASE_TYPE_ID:
            case MANCHESTER_CASE_TYPE_ID:
                return generateOfficeReference(singleRefManchesterRepository, currentYear, numberCases,
                        MANCHESTER_OFFICE_NUMBER, MANCHESTER_CASE_TYPE_ID);
            case SCOTLAND_DEV_CASE_TYPE_ID:
            case SCOTLAND_USERS_CASE_TYPE_ID:
            case SCOTLAND_CASE_TYPE_ID:
                return generateOfficeReference(singleRefScotlandRepository, currentYear, numberCases,
                        GLASGOW_OFFICE_NUMBER, SCOTLAND_CASE_TYPE_ID);
            case MIDLANDS_WEST_DEV_CASE_TYPE_ID:
            case MIDLANDS_WEST_USERS_CASE_TYPE_ID:
            case MIDLANDS_WEST_CASE_TYPE_ID:
                return generateOfficeReference(singleRefMidlandsWestRepository, currentYear, numberCases,
                        MIDLANDS_WEST_OFFICE_NUMBER, MIDLANDS_WEST_CASE_TYPE_ID);
            case MIDLANDS_EAST_DEV_CASE_TYPE_ID:
            case MIDLANDS_EAST_USERS_CASE_TYPE_ID:
            case MIDLANDS_EAST_CASE_TYPE_ID:
                return generateOfficeReference(singleRefMidlandsEastRepository, currentYear, numberCases,
                        MIDLANDS_EAST_OFFICE_NUMBER, MIDLANDS_EAST_CASE_TYPE_ID);
            case BRISTOL_DEV_CASE_TYPE_ID:
            case BRISTOL_USERS_CASE_TYPE_ID:
            case BRISTOL_CASE_TYPE_ID:
                return generateOfficeReference(singleRefBristolRepository, currentYear, numberCases,
                        BRISTOL_OFFICE_NUMBER, BRISTOL_CASE_TYPE_ID);
            case WALES_DEV_CASE_TYPE_ID:
            case WALES_USERS_CASE_TYPE_ID:
            case WALES_CASE_TYPE_ID:
                return generateOfficeReference(singleRefWalesRepository, currentYear, numberCases,
                        WALES_OFFICE_NUMBER, WALES_CASE_TYPE_ID);
            case NEWCASTLE_DEV_CASE_TYPE_ID:
            case NEWCASTLE_USERS_CASE_TYPE_ID:
            case NEWCASTLE_CASE_TYPE_ID:
                return generateOfficeReference(singleRefNewcastleRepository, currentYear, numberCases,
                        NEWCASTLE_OFFICE_NUMBER, NEWCASTLE_CASE_TYPE_ID);
            case WATFORD_DEV_CASE_TYPE_ID:
            case WATFORD_USERS_CASE_TYPE_ID:
            case WATFORD_CASE_TYPE_ID:
                return generateOfficeReference(singleRefWatfordRepository, currentYear, numberCases,
                        WATFORD_OFFICE_NUMBER, WATFORD_CASE_TYPE_ID);
            case LONDON_CENTRAL_DEV_CASE_TYPE_ID:
            case LONDON_CENTRAL_USERS_CASE_TYPE_ID:
            case LONDON_CENTRAL_CASE_TYPE_ID:
                return generateOfficeReference(singleRefLondonCentralRepository, currentYear, numberCases,
                        LONDON_CENTRAL_OFFICE_NUMBER, LONDON_CENTRAL_CASE_TYPE_ID);
            case LONDON_SOUTH_DEV_CASE_TYPE_ID:
            case LONDON_SOUTH_USERS_CASE_TYPE_ID:
            case LONDON_SOUTH_CASE_TYPE_ID:
                return generateOfficeReference(singleRefLondonSouthRepository, currentYear, numberCases,
                        LONDON_SOUTH_OFFICE_NUMBER, LONDON_SOUTH_CASE_TYPE_ID);
            case LONDON_EAST_DEV_CASE_TYPE_ID:
            case LONDON_EAST_USERS_CASE_TYPE_ID:
            case LONDON_EAST_CASE_TYPE_ID:
                return generateOfficeReference(singleRefLondonEastRepository, currentYear, numberCases,
                        LONDON_EAST_OFFICE_NUMBER, LONDON_EAST_CASE_TYPE_ID);
            default:
                return generateOfficeReference(singleRefLeedsRepository, currentYear, numberCases,
                        LEEDS_OFFICE_NUMBER, LEEDS_CASE_TYPE_ID);
        }

    }

    private String generateOfficeReference(SingleRefRepository referenceRepository, String currentYear,
                                           int numberCases, String officeNumber, String officeName) {
        return officeNumber + referenceRepository.ethosCaseRefGen(numberCases, Integer.parseInt(currentYear),
                officeName);
    }

}
