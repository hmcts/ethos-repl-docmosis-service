package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@RequiredArgsConstructor
@Service("subMultipleReferenceService")
public class SubMultipleReferenceService {

    private final SubMultipleRefManchesterRepository subMultipleRefManchesterRepository;
    private final SubMultipleRefScotlandRepository subMultipleRefScotlandRepository;
    private final SubMultipleRefLeedsRepository subMultipleRefLeedsRepository;
    private final SubMultipleRefMidlandsWestRepository subMultipleRefMidlandsWestRepository;
    private final SubMultipleRefMidlandsEastRepository subMultipleRefMidlandsEastRepository;
    private final SubMultipleRefBristolRepository subMultipleRefBristolRepository;
    private final SubMultipleRefWalesRepository subMultipleRefWalesRepository;
    private final SubMultipleRefNewcastleRepository subMultipleRefNewcastleRepository;
    private final SubMultipleRefWatfordRepository subMultipleRefWatfordRepository;
    private final SubMultipleRefLondonCentralRepository subMultipleRefLondonCentralRepository;
    private final SubMultipleRefLondonSouthRepository subMultipleRefLondonSouthRepository;
    private final SubMultipleRefLondonEastRepository subMultipleRefLondonEastRepository;

    public synchronized String createReference(String caseTypeId, String multipleReference, int numberCases) {
        switch (caseTypeId) {
            case MANCHESTER_DEV_BULK_CASE_TYPE_ID:
            case MANCHESTER_USERS_BULK_CASE_TYPE_ID:
            case MANCHESTER_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefManchesterRepository, numberCases, multipleReference,
                        MANCHESTER_CASE_TYPE_ID);
            case SCOTLAND_DEV_BULK_CASE_TYPE_ID:
            case SCOTLAND_USERS_BULK_CASE_TYPE_ID:
            case SCOTLAND_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefScotlandRepository, numberCases, multipleReference,
                        SCOTLAND_CASE_TYPE_ID);
            case MIDLANDS_WEST_DEV_BULK_CASE_TYPE_ID:
            case MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID:
            case MIDLANDS_WEST_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefMidlandsWestRepository, numberCases, multipleReference,
                        MIDLANDS_WEST_CASE_TYPE_ID);
            case MIDLANDS_EAST_DEV_BULK_CASE_TYPE_ID:
            case MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID:
            case MIDLANDS_EAST_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefMidlandsEastRepository, numberCases, multipleReference,
                        MIDLANDS_EAST_CASE_TYPE_ID);
            case BRISTOL_DEV_BULK_CASE_TYPE_ID:
            case BRISTOL_USERS_BULK_CASE_TYPE_ID:
            case BRISTOL_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefBristolRepository, numberCases, multipleReference,
                        BRISTOL_CASE_TYPE_ID);
            case WALES_DEV_BULK_CASE_TYPE_ID:
            case WALES_USERS_BULK_CASE_TYPE_ID:
            case WALES_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefWalesRepository, numberCases, multipleReference,
                        WALES_CASE_TYPE_ID);
            case NEWCASTLE_DEV_BULK_CASE_TYPE_ID:
            case NEWCASTLE_USERS_BULK_CASE_TYPE_ID:
            case NEWCASTLE_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefNewcastleRepository, numberCases, multipleReference,
                        NEWCASTLE_CASE_TYPE_ID);
            case WATFORD_DEV_BULK_CASE_TYPE_ID:
            case WATFORD_USERS_BULK_CASE_TYPE_ID:
            case WATFORD_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefWatfordRepository, numberCases, multipleReference,
                        WATFORD_CASE_TYPE_ID);
            case LONDON_CENTRAL_DEV_BULK_CASE_TYPE_ID:
            case LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID:
            case LONDON_CENTRAL_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefLondonCentralRepository, numberCases, multipleReference,
                        LONDON_CENTRAL_CASE_TYPE_ID);
            case LONDON_SOUTH_DEV_BULK_CASE_TYPE_ID:
            case LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID:
            case LONDON_SOUTH_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefLondonSouthRepository, numberCases, multipleReference,
                        LONDON_SOUTH_CASE_TYPE_ID);
            case LONDON_EAST_DEV_BULK_CASE_TYPE_ID:
            case LONDON_EAST_USERS_BULK_CASE_TYPE_ID:
            case LONDON_EAST_BULK_CASE_TYPE_ID:
                return generateOfficeReference(subMultipleRefLondonEastRepository, numberCases, multipleReference,
                        LONDON_EAST_CASE_TYPE_ID);
        }
        return generateOfficeReference(subMultipleRefLeedsRepository, numberCases, multipleReference, LEEDS_CASE_TYPE_ID);
    }

    private String generateOfficeReference(SubMultipleRefRepository referenceRepository, int numberCases, String multipleRef, String officeName) {
        return referenceRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(multipleRef), numberCases, officeName);
    }

}
