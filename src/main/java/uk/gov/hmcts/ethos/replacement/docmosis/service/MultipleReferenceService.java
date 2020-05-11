package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
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

    @Autowired
    public MultipleReferenceService(MultipleRefManchesterRepository multipleRefManchesterRepository, MultipleRefScotlandRepository multipleRefScotlandRepository,
                                    MultipleRefLeedsRepository multipleRefLeedsRepository, MultipleRefMidlandsWestRepository multipleRefMidlandsWestRepository,
                                    MultipleRefMidlandsEastRepository multipleRefMidlandsEastRepository, MultipleRefBristolRepository multipleRefBristolRepository,
                                    MultipleRefWalesRepository multipleRefWalesRepository, MultipleRefNewcastleRepository multipleRefNewcastleRepository,
                                    MultipleRefWatfordRepository multipleRefWatfordRepository, MultipleRefLondonCentralRepository multipleRefLondonCentralRepository,
                                    MultipleRefLondonSouthRepository multipleRefLondonSouthRepository, MultipleRefLondonEastRepository multipleRefLondonEastRepository) {
        this.multipleRefManchesterRepository = multipleRefManchesterRepository;
        this.multipleRefScotlandRepository = multipleRefScotlandRepository;
        this.multipleRefLeedsRepository = multipleRefLeedsRepository;
        this.multipleRefMidlandsWestRepository = multipleRefMidlandsWestRepository;
        this.multipleRefMidlandsEastRepository = multipleRefMidlandsEastRepository;
        this.multipleRefBristolRepository = multipleRefBristolRepository;
        this.multipleRefWalesRepository = multipleRefWalesRepository;
        this.multipleRefNewcastleRepository = multipleRefNewcastleRepository;
        this.multipleRefWatfordRepository = multipleRefWatfordRepository;
        this.multipleRefLondonCentralRepository = multipleRefLondonCentralRepository;
        this.multipleRefLondonSouthRepository = multipleRefLondonSouthRepository;
        this.multipleRefLondonEastRepository = multipleRefLondonEastRepository;
    }

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
        }
        return generateOfficeReference(multipleRefLeedsRepository, numberCases,
                LEEDS_OFFICE_NUMBER, LEEDS_CASE_TYPE_ID);
    }

    private String generateOfficeReference(MultipleRefRepository referenceRepository, int numberCases, String officeNumber, String officeName) {
        return officeNumber + referenceRepository.ethosMultipleCaseRefGen(numberCases, officeName);
    }

}
