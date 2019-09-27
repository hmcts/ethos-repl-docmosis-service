package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

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

    public synchronized String createReference(String caseTypeId, String caseId) {
        switch (caseTypeId) {
            case MANCHESTER_BULK_CASE_TYPE_ID:
            case MANCHESTER_USERS_BULK_CASE_TYPE_ID:
                return getManchesterOfficeReference(caseId);
            case SCOTLAND_BULK_CASE_TYPE_ID:
            case SCOTLAND_USERS_BULK_CASE_TYPE_ID:
                return getGlasgowOfficeReference(caseId);
            case MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID:
                return getMidlandsWestOfficeReference(caseId);
            case MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID:
                return getMidlandsEastOfficeReference(caseId);
            case BRISTOL_USERS_BULK_CASE_TYPE_ID:
                return getBristolOfficeReference(caseId);
            case WALES_USERS_BULK_CASE_TYPE_ID:
                return getWalesOfficeReference(caseId);
            case NEWCASTLE_USERS_BULK_CASE_TYPE_ID:
                return getNewcastleOfficeReference(caseId);
            case WATFORD_USERS_BULK_CASE_TYPE_ID:
                return getWatfordOfficeReference(caseId);
            case LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID:
                return getLondonCentralOfficeReference(caseId);
            case LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID:
                return getLondonSouthOfficeReference(caseId);
            case LONDON_EAST_USERS_BULK_CASE_TYPE_ID:
                return getLondonEastOfficeReference(caseId);
        }
        return getLeedsOfficeReference(caseId);
    }

    private PreviousRefObject getPreviousReference(MultipleRefRepository referenceRepository) {
        MultipleReference reference = referenceRepository.findTopByOrderByIdDesc();
        PreviousRefObject previousRefObject = new PreviousRefObject();
        if (reference != null) {
            log.info("Previous REF: " + reference.toString());
            previousRefObject.setPreviousRef(reference.getRef());
        } else {
            log.info("No elements in DB yet");
            previousRefObject.setPreviousRef("");
        }
        return previousRefObject;
    }

    private String getManchesterOfficeReference(String caseId) {
        log.info("Manchester Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefManchesterRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceManchester multipleReferenceManchester = new MultipleReferenceManchester(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceManchester multipleReferenceManchesterDB = multipleRefManchesterRepository.save(multipleReferenceManchester);
        return MANCHESTER_OFFICE_NUMBER + multipleReferenceManchesterDB.getRef();
    }

    private String getGlasgowOfficeReference(String caseId) {
        log.info("Scotland Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefScotlandRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceScotland multipleReferenceScotland = new MultipleReferenceScotland(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceScotland multipleReferenceScotlandDB = multipleRefScotlandRepository.save(multipleReferenceScotland);
        return GLASGOW_OFFICE_NUMBER + multipleReferenceScotlandDB.getRef();
    }

    private String getLeedsOfficeReference(String caseId) {
        log.info("Leeds Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefLeedsRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceLeeds multipleReferenceLeeds = new MultipleReferenceLeeds(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceLeeds multipleReferenceLeedsDB = multipleRefLeedsRepository.save(multipleReferenceLeeds);
        return LEEDS_OFFICE_NUMBER + multipleReferenceLeedsDB.getRef();
    }

    private String getMidlandsWestOfficeReference(String caseId) {
        log.info("Midlands West Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefMidlandsWestRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceMidlandsWest multipleReferenceMidlandsWest = new MultipleReferenceMidlandsWest(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceMidlandsWest multipleReferenceMidlandsWestDB = multipleRefMidlandsWestRepository.save(multipleReferenceMidlandsWest);
        return MIDLANDS_WEST_OFFICE_NUMBER + multipleReferenceMidlandsWestDB.getRef();
    }

    private String getMidlandsEastOfficeReference(String caseId) {
        log.info("Midlands East Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefMidlandsEastRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceMidlandsEast multipleReferenceMidlandsEast = new MultipleReferenceMidlandsEast(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceMidlandsEast multipleReferenceMidlandsEastDB = multipleRefMidlandsEastRepository.save(multipleReferenceMidlandsEast);
        return MIDLANDS_EAST_OFFICE_NUMBER + multipleReferenceMidlandsEastDB.getRef();
    }

    private String getBristolOfficeReference(String caseId) {
        log.info("Bristol Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefBristolRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceBristol multipleReferenceBristol = new MultipleReferenceBristol(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceBristol multipleReferenceBristolDB = multipleRefBristolRepository.save(multipleReferenceBristol);
        return BRISTOL_OFFICE_NUMBER + multipleReferenceBristolDB.getRef();
    }

    private String getWalesOfficeReference(String caseId) {
        log.info("Wales Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefWalesRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceWales multipleReferenceWales = new MultipleReferenceWales(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceWales multipleReferenceWalesDB = multipleRefWalesRepository.save(multipleReferenceWales);
        return WALES_OFFICE_NUMBER + multipleReferenceWalesDB.getRef();
    }

    private String getNewcastleOfficeReference(String caseId) {
        log.info("Newcastle Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefNewcastleRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceNewcastle multipleReferenceNewcastle = new MultipleReferenceNewcastle(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceNewcastle multipleReferenceNewcastleDB = multipleRefNewcastleRepository.save(multipleReferenceNewcastle);
        return NEWCASTLE_OFFICE_NUMBER + multipleReferenceNewcastleDB.getRef();
    }

    private String getWatfordOfficeReference(String caseId) {
        log.info("Watford Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefWatfordRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceWatford multipleReferenceWatford = new MultipleReferenceWatford(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceWatford multipleReferenceWatfordDB = multipleRefWatfordRepository.save(multipleReferenceWatford);
        return WATFORD_OFFICE_NUMBER + multipleReferenceWatfordDB.getRef();
    }

    private String getLondonCentralOfficeReference(String caseId) {
        log.info("London Central Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefLondonCentralRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceLondonCentral multipleReferenceLondonCentral = new MultipleReferenceLondonCentral(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceLondonCentral multipleReferenceLondonCentralDB = multipleRefLondonCentralRepository.save(multipleReferenceLondonCentral);
        return LONDON_CENTRAL_OFFICE_NUMBER + multipleReferenceLondonCentralDB.getRef();
    }

    private String getLondonSouthOfficeReference(String caseId) {
        log.info("London South Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefLondonSouthRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceLondonSouth multipleReferenceLondonSouth = new MultipleReferenceLondonSouth(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceLondonSouth multipleReferenceLondonSouthDB = multipleRefLondonSouthRepository.save(multipleReferenceLondonSouth);
        return LONDON_SOUTH_OFFICE_NUMBER + multipleReferenceLondonSouthDB.getRef();
    }

    private String getLondonEastOfficeReference(String caseId) {
        log.info("London East Multiple Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(multipleRefLondonEastRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        MultipleReferenceLondonEast multipleReferenceLondonEast = new MultipleReferenceLondonEast(caseId, previousRefObject.getPreviousRef());
        MultipleReferenceLondonEast multipleReferenceLondonEastDB = multipleRefLondonEastRepository.save(multipleReferenceLondonEast);
        return LONDON_EAST_OFFICE_NUMBER + multipleReferenceLondonEastDB.getRef();
    }
}
