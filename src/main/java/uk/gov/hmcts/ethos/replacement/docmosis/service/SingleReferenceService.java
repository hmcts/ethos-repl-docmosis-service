package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

import java.time.LocalDate;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
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

    @Autowired
    public SingleReferenceService(SingleRefManchesterRepository singleRefManchesterRepository, SingleRefScotlandRepository singleRefScotlandRepository,
                                  SingleRefLeedsRepository singleRefLeedsRepository, SingleRefMidlandsWestRepository singleRefMidlandsWestRepository,
                                  SingleRefMidlandsEastRepository singleRefMidlandsEastRepository, SingleRefBristolRepository singleRefBristolRepository,
                                  SingleRefWalesRepository singleRefWalesRepository, SingleRefNewcastleRepository singleRefNewcastleRepository,
                                  SingleRefWatfordRepository singleRefWatfordRepository, SingleRefLondonCentralRepository singleRefLondonCentralRepository,
                                  SingleRefLondonSouthRepository singleRefLondonSouthRepository, SingleRefLondonEastRepository singleRefLondonEastRepository) {
        this.singleRefManchesterRepository = singleRefManchesterRepository;
        this.singleRefScotlandRepository = singleRefScotlandRepository;
        this.singleRefLeedsRepository = singleRefLeedsRepository;
        this.singleRefMidlandsWestRepository = singleRefMidlandsWestRepository;
        this.singleRefMidlandsEastRepository = singleRefMidlandsEastRepository;
        this.singleRefBristolRepository = singleRefBristolRepository;
        this.singleRefWalesRepository = singleRefWalesRepository;
        this.singleRefNewcastleRepository = singleRefNewcastleRepository;
        this.singleRefWatfordRepository = singleRefWatfordRepository;
        this.singleRefLondonCentralRepository = singleRefLondonCentralRepository;
        this.singleRefLondonSouthRepository = singleRefLondonSouthRepository;
        this.singleRefLondonEastRepository = singleRefLondonEastRepository;
    }

    public synchronized String createReference(String caseTypeId, String caseId) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        switch (caseTypeId) {
            case MANCHESTER_DEV_CASE_TYPE_ID
            case MANCHESTER_USERS_CASE_TYPE_ID:
            case MANCHESTER_CASE_TYPE_ID:
                return getManchesterOfficeReference(caseId, currentYear);
            case SCOTLAND_DEV_CASE_TYPE_ID:
            case SCOTLAND_USERS_CASE_TYPE_ID:
            case SCOTLAND_CASE_TYPE_ID:
                return getGlasgowOfficeReference(caseId, currentYear);
            case MIDLANDS_WEST_DEV_CASE_TYPE_ID:
            case MIDLANDS_WEST_USERS_CASE_TYPE_ID:
            case MIDLANDS_WEST_CASE_TYPE_ID:
                return getMidlandsWestOfficeReference(caseId, currentYear);
            case MIDLANDS_EAST_DEV_CASE_TYPE_ID:
            case MIDLANDS_EAST_USERS_CASE_TYPE_ID:
            case MIDLANDS_EAST_CASE_TYPE_ID:
                return getMidlandsEastOfficeReference(caseId, currentYear);
            case BRISTOL_DEV_CASE_TYPE_ID:
            case BRISTOL_USERS_CASE_TYPE_ID:
            case BRISTOL_CASE_TYPE_ID:
                return getBristolOfficeReference(caseId, currentYear);
            case WALES_DEV_CASE_TYPE_ID:
            case WALES_USERS_CASE_TYPE_ID:
            case WALES_CASE_TYPE_ID:
                return getWalesOfficeReference(caseId, currentYear);
            case NEWCASTLE_DEV_CASE_TYPE_ID:
            case NEWCASTLE_USERS_CASE_TYPE_ID:
            case NEWCASTLE_CASE_TYPE_ID:
                return getNewcastleOfficeReference(caseId, currentYear);
            case WATFORD_DEV_CASE_TYPE_ID:
            case WATFORD_USERS_CASE_TYPE_ID:
            case WATFORD_CASE_TYPE_ID:
                return getWatfordOfficeReference(caseId, currentYear);
            case LONDON_CENTRAL_DEV_CASE_TYPE_ID:
            case LONDON_CENTRAL_USERS_CASE_TYPE_ID:
            case LONDON_CENTRAL_CASE_TYPE_ID:
                return getLondonCentralOfficeReference(caseId, currentYear);
            case LONDON_SOUTH_DEV_CASE_TYPE_ID:
            case LONDON_SOUTH_USERS_CASE_TYPE_ID:
            case LONDON_SOUTH_CASE_TYPE_ID:
                return getLondonSouthOfficeReference(caseId, currentYear);
            case LONDON_EAST_DEV_CASE_TYPE_ID:
            case LONDON_EAST_USERS_CASE_TYPE_ID:
            case LONDON_EAST_CASE_TYPE_ID:
                return getLondonEastOfficeReference(caseId, currentYear);
        }
        return getLeedsOfficeReference(caseId, currentYear);
    }

    private synchronized PreviousRefObject getPreviousReference(SingleRefRepository referenceRepository) {
        SingleReference reference = referenceRepository.findTopByOrderByIdDesc();
        PreviousRefObject previousRefObject = new PreviousRefObject();
        if (reference != null) {
            log.info("Previous REF: " + reference.toString());
            previousRefObject.setPreviousRef(reference.getRef());
            previousRefObject.setPreviousYear(reference.getYear());
        } else {
            log.info("No elements in DB yet");
            previousRefObject.setPreviousRef("");
            previousRefObject.setPreviousYear("");
        }
        return previousRefObject;
    }

    private synchronized String getManchesterOfficeReference(String caseId, String currentYear) {
        log.info("Manchester CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefManchesterRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceManchester singleReferenceManchester = new SingleReferenceManchester(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceManchester singleReferenceManchesterDB = singleRefManchesterRepository.save(singleReferenceManchester);
        return MANCHESTER_OFFICE_NUMBER + singleReferenceManchesterDB.getRef() + "/" + currentYear;
    }

    private synchronized String getGlasgowOfficeReference(String caseId, String currentYear) {
        log.info("Scotland CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefScotlandRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceScotland singleReferenceScotland = new SingleReferenceScotland(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceScotland singleReferenceScotlandDB = singleRefScotlandRepository.save(singleReferenceScotland);
        return GLASGOW_OFFICE_NUMBER + singleReferenceScotlandDB.getRef() + "/" + currentYear;
    }

    private synchronized String getLeedsOfficeReference(String caseId, String currentYear) {
        log.info("Leeds CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefLeedsRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceLeeds singleReferenceLeeds = new SingleReferenceLeeds(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceLeeds singleReferenceLeedsDB = singleRefLeedsRepository.save(singleReferenceLeeds);
        return LEEDS_OFFICE_NUMBER + singleReferenceLeedsDB.getRef() + "/" + currentYear;
    }

    private synchronized String getMidlandsWestOfficeReference(String caseId, String currentYear) {
        log.info("Midlands West CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefMidlandsWestRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceMidlandsWest singleReferenceMidlandsWest = new SingleReferenceMidlandsWest(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceMidlandsWest singleReferenceMidlandsWestDB = singleRefMidlandsWestRepository.save(singleReferenceMidlandsWest);
        return MIDLANDS_WEST_OFFICE_NUMBER + singleReferenceMidlandsWestDB.getRef() + "/" + currentYear;
    }

    private synchronized String getMidlandsEastOfficeReference(String caseId, String currentYear) {
        log.info("Midlands East CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefMidlandsEastRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceMidlandsEast singleReferenceMidlandsEast = new SingleReferenceMidlandsEast(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceMidlandsEast singleReferenceMidlandsEastDB = singleRefMidlandsEastRepository.save(singleReferenceMidlandsEast);
        return MIDLANDS_EAST_OFFICE_NUMBER + singleReferenceMidlandsEastDB.getRef() + "/" + currentYear;
    }

    private synchronized String getBristolOfficeReference(String caseId, String currentYear) {
        log.info("Bristol CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefBristolRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceBristol singleReferenceBristol = new SingleReferenceBristol(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceBristol singleReferenceBristolDB = singleRefBristolRepository.save(singleReferenceBristol);
        return BRISTOL_OFFICE_NUMBER + singleReferenceBristolDB.getRef() + "/" + currentYear;
    }

    private synchronized String getWalesOfficeReference(String caseId, String currentYear) {
        log.info("Wales CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefWalesRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceWales singleReferenceWales = new SingleReferenceWales(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceWales singleReferenceWalesDB = singleRefWalesRepository.save(singleReferenceWales);
        return WALES_OFFICE_NUMBER + singleReferenceWalesDB.getRef() + "/" + currentYear;
    }

    private synchronized String getNewcastleOfficeReference(String caseId, String currentYear) {
        log.info("Newcastle CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefNewcastleRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceNewcastle singleReferenceNewcastle = new SingleReferenceNewcastle(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceNewcastle singleReferenceNewcastleDB = singleRefNewcastleRepository.save(singleReferenceNewcastle);
        return NEWCASTLE_OFFICE_NUMBER + singleReferenceNewcastleDB.getRef() + "/" + currentYear;
    }

    private synchronized String getWatfordOfficeReference(String caseId, String currentYear) {
        log.info("Watford CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefWatfordRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceWatford singleReferenceWatford = new SingleReferenceWatford(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceWatford singleReferenceWatfordDB = singleRefWatfordRepository.save(singleReferenceWatford);
        return WATFORD_OFFICE_NUMBER + singleReferenceWatfordDB.getRef() + "/" + currentYear;
    }

    private synchronized String getLondonCentralOfficeReference(String caseId, String currentYear) {
        log.info("London Central CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefLondonCentralRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceLondonCentral singleReferenceLondonCentral = new SingleReferenceLondonCentral(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceLondonCentral singleReferenceLondonCentralDB = singleRefLondonCentralRepository.save(singleReferenceLondonCentral);
        return LONDON_CENTRAL_OFFICE_NUMBER + singleReferenceLondonCentralDB.getRef() + "/" + currentYear;
    }

    private synchronized String getLondonSouthOfficeReference(String caseId, String currentYear) {
        log.info("London South CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefLondonSouthRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceLondonSouth singleReferenceLondonSouth = new SingleReferenceLondonSouth(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceLondonSouth singleReferenceLondonSouthDB = singleRefLondonSouthRepository.save(singleReferenceLondonSouth);
        return LONDON_SOUTH_OFFICE_NUMBER + singleReferenceLondonSouthDB.getRef() + "/" + currentYear;
    }

    private synchronized String getLondonEastOfficeReference(String caseId, String currentYear) {
        log.info("London East CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefLondonEastRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceLondonEast singleReferenceLondonEast = new SingleReferenceLondonEast(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceLondonEast singleReferenceLondonEastDB = singleRefLondonEastRepository.save(singleReferenceLondonEast);
        return LONDON_EAST_OFFICE_NUMBER + singleReferenceLondonEastDB.getRef() + "/" + currentYear;
    }
}
