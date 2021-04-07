package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefBristolRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLeedsRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLondonCentralRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLondonEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLondonSouthRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefManchesterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefMidlandsEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefMidlandsWestRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefNewcastleRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefScotlandRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefWalesRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefWatfordRepository;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_USERS_BULK_CASE_TYPE_ID;

@Slf4j
@RequiredArgsConstructor
@Service("multipleReferenceService")
public class MultipleReferenceService {

    private final MultipleRefManchesterRepository multipleRefManchesterRepository;
    private final MultipleRefScotlandRepository multipleRefScotlandRepository;
    private final MultipleRefLeedsRepository multipleRefLeedsRepository;
    private final MultipleRefMidlandsWestRepository multipleRefMidlandsWestRepository;
    private final MultipleRefMidlandsEastRepository multipleRefMidlandsEastRepository;
    private final MultipleRefBristolRepository multipleRefBristolRepository;
    private final MultipleRefWalesRepository multipleRefWalesRepository;
    private final MultipleRefNewcastleRepository multipleRefNewcastleRepository;
    private final MultipleRefWatfordRepository multipleRefWatfordRepository;
    private final MultipleRefLondonCentralRepository multipleRefLondonCentralRepository;
    private final MultipleRefLondonSouthRepository multipleRefLondonSouthRepository;
    private final MultipleRefLondonEastRepository multipleRefLondonEastRepository;

    public synchronized String createReference(String caseTypeId, int numberCases) {
        switch (caseTypeId) {
            case MANCHESTER_DEV_BULK_CASE_TYPE_ID:
            case MANCHESTER_USERS_BULK_CASE_TYPE_ID:
            case MANCHESTER_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefManchesterRepository, numberCases,
                        MANCHESTER_OFFICE_NUMBER, MANCHESTER_CASE_TYPE_ID);
            case SCOTLAND_DEV_BULK_CASE_TYPE_ID:
            case SCOTLAND_USERS_BULK_CASE_TYPE_ID:
            case SCOTLAND_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefScotlandRepository, numberCases,
                        GLASGOW_OFFICE_NUMBER, SCOTLAND_CASE_TYPE_ID);
            case MIDLANDS_WEST_DEV_BULK_CASE_TYPE_ID:
            case MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID:
            case MIDLANDS_WEST_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefMidlandsWestRepository, numberCases,
                        MIDLANDS_WEST_OFFICE_NUMBER, MIDLANDS_WEST_CASE_TYPE_ID);
            case MIDLANDS_EAST_DEV_BULK_CASE_TYPE_ID:
            case MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID:
            case MIDLANDS_EAST_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefMidlandsEastRepository, numberCases,
                        MIDLANDS_EAST_OFFICE_NUMBER, MIDLANDS_EAST_CASE_TYPE_ID);
            case BRISTOL_DEV_BULK_CASE_TYPE_ID:
            case BRISTOL_USERS_BULK_CASE_TYPE_ID:
            case BRISTOL_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefBristolRepository, numberCases,
                        BRISTOL_OFFICE_NUMBER, BRISTOL_CASE_TYPE_ID);
            case WALES_DEV_BULK_CASE_TYPE_ID:
            case WALES_USERS_BULK_CASE_TYPE_ID:
            case WALES_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefWalesRepository, numberCases,
                        WALES_OFFICE_NUMBER, WALES_CASE_TYPE_ID);
            case NEWCASTLE_DEV_BULK_CASE_TYPE_ID:
            case NEWCASTLE_USERS_BULK_CASE_TYPE_ID:
            case NEWCASTLE_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefNewcastleRepository, numberCases,
                        NEWCASTLE_OFFICE_NUMBER, NEWCASTLE_CASE_TYPE_ID);
            case WATFORD_DEV_BULK_CASE_TYPE_ID:
            case WATFORD_USERS_BULK_CASE_TYPE_ID:
            case WATFORD_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefWatfordRepository, numberCases,
                        WATFORD_OFFICE_NUMBER, WATFORD_CASE_TYPE_ID);
            case LONDON_CENTRAL_DEV_BULK_CASE_TYPE_ID:
            case LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID:
            case LONDON_CENTRAL_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefLondonCentralRepository, numberCases,
                        LONDON_CENTRAL_OFFICE_NUMBER, LONDON_CENTRAL_CASE_TYPE_ID);
            case LONDON_SOUTH_DEV_BULK_CASE_TYPE_ID:
            case LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID:
            case LONDON_SOUTH_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefLondonSouthRepository, numberCases,
                        LONDON_SOUTH_OFFICE_NUMBER, LONDON_SOUTH_CASE_TYPE_ID);
            case LONDON_EAST_DEV_BULK_CASE_TYPE_ID:
            case LONDON_EAST_USERS_BULK_CASE_TYPE_ID:
            case LONDON_EAST_BULK_CASE_TYPE_ID:
                return generateOfficeReference(multipleRefLondonEastRepository, numberCases,
                        LONDON_EAST_OFFICE_NUMBER, LONDON_EAST_CASE_TYPE_ID);
            default:
                return generateOfficeReference(multipleRefLeedsRepository, numberCases,
                        LEEDS_OFFICE_NUMBER, LEEDS_CASE_TYPE_ID);
        }

    }

    private String generateOfficeReference(MultipleRefRepository referenceRepository, int numberCases,
                                           String officeNumber, String officeName) {
        return officeNumber + referenceRepository.ethosMultipleCaseRefGen(numberCases, officeName);
    }

}
