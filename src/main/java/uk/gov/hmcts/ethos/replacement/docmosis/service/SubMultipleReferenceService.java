package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
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

    @Autowired
    public SubMultipleReferenceService(SubMultipleRefManchesterRepository subMultipleRefManchesterRepository, SubMultipleRefScotlandRepository subMultipleRefScotlandRepository,
                                       SubMultipleRefLeedsRepository subMultipleRefLeedsRepository, SubMultipleRefMidlandsWestRepository subMultipleRefMidlandsWestRepository,
                                       SubMultipleRefMidlandsEastRepository subMultipleRefMidlandsEastRepository, SubMultipleRefBristolRepository subMultipleRefBristolRepository,
                                       SubMultipleRefWalesRepository subMultipleRefWalesRepository, SubMultipleRefNewcastleRepository subMultipleRefNewcastleRepository,
                                       SubMultipleRefWatfordRepository subMultipleRefWatfordRepository, SubMultipleRefLondonCentralRepository subMultipleRefLondonCentralRepository,
                                       SubMultipleRefLondonSouthRepository subMultipleRefLondonSouthRepository, SubMultipleRefLondonEastRepository subMultipleRefLondonEastRepository) {
        this.subMultipleRefManchesterRepository = subMultipleRefManchesterRepository;
        this.subMultipleRefScotlandRepository = subMultipleRefScotlandRepository;
        this.subMultipleRefLeedsRepository = subMultipleRefLeedsRepository;
        this.subMultipleRefMidlandsWestRepository = subMultipleRefMidlandsWestRepository;
        this.subMultipleRefMidlandsEastRepository = subMultipleRefMidlandsEastRepository;
        this.subMultipleRefBristolRepository = subMultipleRefBristolRepository;
        this.subMultipleRefWalesRepository = subMultipleRefWalesRepository;
        this.subMultipleRefNewcastleRepository = subMultipleRefNewcastleRepository;
        this.subMultipleRefWatfordRepository = subMultipleRefWatfordRepository;
        this.subMultipleRefLondonCentralRepository = subMultipleRefLondonCentralRepository;
        this.subMultipleRefLondonSouthRepository = subMultipleRefLondonSouthRepository;
        this.subMultipleRefLondonEastRepository = subMultipleRefLondonEastRepository;
    }

    public synchronized String createReference(String caseTypeId, String multipleReference) {
        String multipleRef = multipleReference.substring(2);
        switch (caseTypeId) {
            case MANCHESTER_BULK_CASE_TYPE_ID:
            case MANCHESTER_USERS_BULK_CASE_TYPE_ID:
                return getManchesterOfficeReference(multipleRef);
            case SCOTLAND_BULK_CASE_TYPE_ID:
            case SCOTLAND_USERS_BULK_CASE_TYPE_ID:
                return getGlasgowOfficeReference(multipleRef);
            case MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID:
                return getMidlandsWestOfficeReference(multipleRef);
            case MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID:
                return getMidlandsEastOfficeReference(multipleRef);
            case BRISTOL_USERS_BULK_CASE_TYPE_ID:
                return getBristolOfficeReference(multipleRef);
            case WALES_USERS_BULK_CASE_TYPE_ID:
                return getWalesOfficeReference(multipleRef);
            case NEWCASTLE_USERS_BULK_CASE_TYPE_ID:
                return getNewcastleOfficeReference(multipleRef);
            case WATFORD_USERS_BULK_CASE_TYPE_ID:
                return getWatfordOfficeReference(multipleRef);
            case LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID:
                return getLondonCentralOfficeReference(multipleRef);
            case LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID:
                return getLondonSouthOfficeReference(multipleRef);
            case LONDON_EAST_USERS_BULK_CASE_TYPE_ID:
                return getLondonEastOfficeReference(multipleRef);
        }
        return getLeedsOfficeReference(multipleRef);
    }

    private synchronized PreviousRefObject getPreviousReference(SubMultipleRefRepository referenceRepository, String multipleRef) {
        SubMultipleReference reference = referenceRepository.findTopByMultipleRefOrderByRefDesc(multipleRef);
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

    private String generateSubMultipleReference(String officeNumber, String multipleRef, String subMultipleRef) {
        return officeNumber + multipleRef + "/" + subMultipleRef;
    }

    private synchronized String getManchesterOfficeReference(String multipleRef) {
        log.info("Manchester Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefManchesterRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceManchester subMultipleReferenceManchester = new SubMultipleReferenceManchester(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceManchester subMultipleReferenceManchesterDB = subMultipleRefManchesterRepository.save(subMultipleReferenceManchester);
        return generateSubMultipleReference(MANCHESTER_OFFICE_NUMBER, multipleRef, subMultipleReferenceManchesterDB.getRef());
    }

    private synchronized String getGlasgowOfficeReference(String multipleRef) {
        log.info("Scotland Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefScotlandRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceScotland subMultipleReferenceScotland = new SubMultipleReferenceScotland(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceScotland subMultipleReferenceScotlandDB = subMultipleRefScotlandRepository.save(subMultipleReferenceScotland);
        return generateSubMultipleReference(GLASGOW_OFFICE_NUMBER, multipleRef, subMultipleReferenceScotlandDB.getRef());
    }

    private synchronized String getLeedsOfficeReference(String multipleRef) {
        log.info("Leeds Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefLeedsRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceLeeds subMultipleReferenceLeeds = new SubMultipleReferenceLeeds(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceLeeds subMultipleReferenceLeedsDB = subMultipleRefLeedsRepository.save(subMultipleReferenceLeeds);
        return generateSubMultipleReference(LEEDS_OFFICE_NUMBER, multipleRef, subMultipleReferenceLeedsDB.getRef());
    }

    private synchronized String getMidlandsWestOfficeReference(String multipleRef) {
        log.info("Midlands West Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefMidlandsWestRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceMidlandsWest subMultipleReferenceMidlandsWest = new SubMultipleReferenceMidlandsWest(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceMidlandsWest subMultipleReferenceMidlandsWestDB = subMultipleRefMidlandsWestRepository.save(subMultipleReferenceMidlandsWest);
        return generateSubMultipleReference(MIDLANDS_WEST_OFFICE_NUMBER, multipleRef, subMultipleReferenceMidlandsWestDB.getRef());
    }

    private synchronized String getMidlandsEastOfficeReference(String multipleRef) {
        log.info("Midlands East Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefMidlandsEastRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceMidlandsEast subMultipleReferenceMidlandsEast = new SubMultipleReferenceMidlandsEast(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceMidlandsEast subMultipleReferenceMidlandsEastDB = subMultipleRefMidlandsEastRepository.save(subMultipleReferenceMidlandsEast);
        return generateSubMultipleReference(MIDLANDS_EAST_OFFICE_NUMBER, multipleRef, subMultipleReferenceMidlandsEastDB.getRef());
    }

    private synchronized String getBristolOfficeReference(String multipleRef) {
        log.info("Bristol Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefBristolRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceBristol subMultipleReferenceBristol = new SubMultipleReferenceBristol(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceBristol subMultipleReferenceBristolDB = subMultipleRefBristolRepository.save(subMultipleReferenceBristol);
        return generateSubMultipleReference(BRISTOL_OFFICE_NUMBER, multipleRef, subMultipleReferenceBristolDB.getRef());
    }

    private synchronized String getWalesOfficeReference(String multipleRef) {
        log.info("Wales Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefWalesRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceWales subMultipleReferenceWales = new SubMultipleReferenceWales(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceWales subMultipleReferenceWalesDB = subMultipleRefWalesRepository.save(subMultipleReferenceWales);
        return generateSubMultipleReference(WALES_OFFICE_NUMBER, multipleRef, subMultipleReferenceWalesDB.getRef());
    }

    private synchronized String getNewcastleOfficeReference(String multipleRef) {
        log.info("Newcastle Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefNewcastleRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceNewcastle subMultipleReferenceNewcastle = new SubMultipleReferenceNewcastle(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceNewcastle subMultipleReferenceNewcastleDB = subMultipleRefNewcastleRepository.save(subMultipleReferenceNewcastle);
        return generateSubMultipleReference(NEWCASTLE_OFFICE_NUMBER, multipleRef, subMultipleReferenceNewcastleDB.getRef());
    }

    private synchronized String getWatfordOfficeReference(String multipleRef) {
        log.info("Watford Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefWatfordRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceWatford subMultipleReferenceWatford = new SubMultipleReferenceWatford(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceWatford subMultipleReferenceWatfordDB = subMultipleRefWatfordRepository.save(subMultipleReferenceWatford);
        return generateSubMultipleReference(WATFORD_OFFICE_NUMBER, multipleRef, subMultipleReferenceWatfordDB.getRef());
    }

    private synchronized String getLondonCentralOfficeReference(String multipleRef) {
        log.info("London Central Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefLondonCentralRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceLondonCentral subMultipleReferenceLondonCentral = new SubMultipleReferenceLondonCentral(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceLondonCentral subMultipleReferenceLondonCentralDB = subMultipleRefLondonCentralRepository.save(subMultipleReferenceLondonCentral);
        return generateSubMultipleReference(LONDON_CENTRAL_OFFICE_NUMBER, multipleRef, subMultipleReferenceLondonCentralDB.getRef());
    }

    private synchronized String getLondonSouthOfficeReference(String multipleRef) {
        log.info("London South Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefLondonSouthRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceLondonSouth subMultipleReferenceLondonSouth = new SubMultipleReferenceLondonSouth(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceLondonSouth subMultipleReferenceLondonSouthDB = subMultipleRefLondonSouthRepository.save(subMultipleReferenceLondonSouth);
        return generateSubMultipleReference(LONDON_SOUTH_OFFICE_NUMBER, multipleRef, subMultipleReferenceLondonSouthDB.getRef());
    }

    private synchronized String getLondonEastOfficeReference(String multipleRef) {
        log.info("London East Multiple CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(subMultipleRefLondonEastRepository, multipleRef);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SubMultipleReferenceLondonEast subMultipleReferenceLondonEast = new SubMultipleReferenceLondonEast(multipleRef, previousRefObject.getPreviousRef());
        SubMultipleReferenceLondonEast subMultipleReferenceLondonEastDB = subMultipleRefLondonEastRepository.save(subMultipleReferenceLondonEast);
        return generateSubMultipleReference(LONDON_EAST_OFFICE_NUMBER, multipleRef, subMultipleReferenceLondonEastDB.getRef());
    }
}
