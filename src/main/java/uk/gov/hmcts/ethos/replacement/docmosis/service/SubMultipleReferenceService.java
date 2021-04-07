package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefBristolRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefLeedsRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefLondonCentralRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefLondonEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefLondonSouthRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefManchesterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefMidlandsEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefMidlandsWestRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefNewcastleRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefScotlandRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefWalesRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SubMultipleRefWatfordRepository;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_USERS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_USERS_BULK_CASE_TYPE_ID;

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
            default:
                return generateOfficeReference(
                        subMultipleRefLeedsRepository, numberCases, multipleReference, LEEDS_CASE_TYPE_ID);
        }

    }

    private String generateOfficeReference(SubMultipleRefRepository referenceRepository, int numberCases,
                                           String multipleRef, String officeName) {
        return referenceRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(multipleRef), numberCases, officeName);
    }

}
