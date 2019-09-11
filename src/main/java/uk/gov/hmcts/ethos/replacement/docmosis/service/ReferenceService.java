package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;

import java.time.LocalDate;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Service("referenceService")
public class ReferenceService {

    private final SingleRefManchesterRepository singleRefManchesterRepository;
    private final SingleRefScotlandRepository singleRefScotlandRepository;
    private final SingleRefLeedsRepository singleRefLeedsRepository;
    private final SingleRefMidlandsWestRepository singleRefMidlandsWestRepository;

    @Autowired
    public ReferenceService(SingleRefManchesterRepository singleRefManchesterRepository, SingleRefScotlandRepository singleRefScotlandRepository,
                            SingleRefLeedsRepository singleRefLeedsRepository, SingleRefMidlandsWestRepository singleRefMidlandsWestRepository) {
        this.singleRefManchesterRepository = singleRefManchesterRepository;
        this.singleRefScotlandRepository = singleRefScotlandRepository;
        this.singleRefLeedsRepository = singleRefLeedsRepository;
        this.singleRefMidlandsWestRepository = singleRefMidlandsWestRepository;
    }

    public String createReference(String caseTypeId, String caseId) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        switch (caseTypeId) {
            case MANCHESTER_CASE_TYPE_ID:
            case MANCHESTER_USERS_CASE_TYPE_ID:
                return getManchesterOfficeReference(caseId, currentYear);
            case SCOTLAND_CASE_TYPE_ID:
            case SCOTLAND_USERS_CASE_TYPE_ID:
                return getGlasgowOfficeReference(caseId, currentYear);
            case MIDLANDS_WEST_USERS_CASE_TYPE_ID:
                return getMidlandsWestOfficeReference(caseId, currentYear);
        }
        return getLeedsOfficeReference(caseId, currentYear);
    }

    private PreviousRefObject getPreviousReference(SingleRefRepository referenceRepository) {
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

    private String getManchesterOfficeReference(String caseId, String currentYear) {
        log.info("Manchester CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefManchesterRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceManchester singleReferenceManchester = new SingleReferenceManchester(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceManchester singleReferenceManchesterDB = singleRefManchesterRepository.save(singleReferenceManchester);
        return MANCHESTER_OFFICE_NUMBER + singleReferenceManchesterDB.getRef() + "/" + currentYear;
    }

    private String getGlasgowOfficeReference(String caseId, String currentYear) {
        log.info("Scotland CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefScotlandRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceScotland singleReferenceScotland = new SingleReferenceScotland(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceScotland singleReferenceScotlandDB = singleRefScotlandRepository.save(singleReferenceScotland);
        return GLASGOW_OFFICE_NUMBER + singleReferenceScotlandDB.getRef() + "/" + currentYear;
    }

    private String getLeedsOfficeReference(String caseId, String currentYear) {
        log.info("Leeds CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefLeedsRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceLeeds singleReferenceLeeds = new SingleReferenceLeeds(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceLeeds singleReferenceLeedsDB = singleRefLeedsRepository.save(singleReferenceLeeds);
        return LEEDS_OFFICE_NUMBER + singleReferenceLeedsDB.getRef() + "/" + currentYear;
    }

    private String getMidlandsWestOfficeReference(String caseId, String currentYear) {
        log.info("Midlands West CASE TYPE");
        PreviousRefObject previousRefObject = getPreviousReference(singleRefMidlandsWestRepository);
        log.info("PreviousRefObject: " + previousRefObject.toString());
        SingleReferenceMidlandsWest singleReferenceMidlandsWest = new SingleReferenceMidlandsWest(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceMidlandsWest singleReferenceMidlandsWestDB = singleRefMidlandsWestRepository.save(singleReferenceMidlandsWest);
        return MIDLANDS_WEST_OFFICE_NUMBER + singleReferenceMidlandsWestDB.getRef() + "/" + currentYear;
    }
}
