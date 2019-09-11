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

    @Autowired
    public ReferenceService(SingleRefManchesterRepository singleRefManchesterRepository, SingleRefScotlandRepository singleRefScotlandRepository) {
        this.singleRefManchesterRepository = singleRefManchesterRepository;
        this.singleRefScotlandRepository = singleRefScotlandRepository;
    }

    public String createReference(String caseTypeId, String caseId) {
        PreviousRefObject previousRefObject;
        String currentYear = String.valueOf(LocalDate.now().getYear());
        if (caseTypeId.equals(MANCHESTER_CASE_TYPE_ID) || caseTypeId.equals(MANCHESTER_USERS_CASE_TYPE_ID)) {
            log.info("Manchester CASE TYPE");
            previousRefObject = getPreviousReference(singleRefManchesterRepository);
            log.info("PreviousRefObject: " + previousRefObject.toString());
            SingleReferenceManchester singleReferenceManchester = new SingleReferenceManchester(caseId, previousRefObject.getPreviousRef(),
                    previousRefObject.getPreviousYear(), currentYear);
            SingleReferenceManchester singleReferenceManchesterDB = singleRefManchesterRepository.save(singleReferenceManchester);
            return MANCHESTER_OFFICE_NUMBER + singleReferenceManchesterDB.getRef() + "/" + currentYear;
        } else if (caseTypeId.equals(SCOTLAND_CASE_TYPE_ID) || caseTypeId.equals(SCOTLAND_USERS_CASE_TYPE_ID)) {
            log.info("Scotland CASE TYPE");
            previousRefObject = getPreviousReference(singleRefScotlandRepository);
            log.info("PreviousRefObject: " + previousRefObject.toString());
            SingleReferenceScotland singleReferenceScotland = new SingleReferenceScotland(caseId, previousRefObject.getPreviousRef(),
                    previousRefObject.getPreviousYear(), currentYear);
            SingleReferenceScotland singleReferenceScotlandDB = singleRefScotlandRepository.save(singleReferenceScotland);
            return GLASGOW_OFFICE_NUMBER + singleReferenceScotlandDB.getRef() + "/" + currentYear;
        }
        log.info("Other CASE TYPE");
        previousRefObject = getPreviousReference(singleRefScotlandRepository);
        SingleReferenceScotland singleReferenceScotland = new SingleReferenceScotland(caseId, previousRefObject.getPreviousRef(),
                previousRefObject.getPreviousYear(), currentYear);
        SingleReferenceScotland singleReferenceScotlandDB = singleRefScotlandRepository.save(singleReferenceScotland);
        return GLASGOW_OFFICE_NUMBER + singleReferenceScotlandDB.getRef() + "/" + currentYear;
    }

    private PreviousRefObject getPreviousReference(SingleRefRepository referenceRepository) {
        SingleReference reference = referenceRepository.findFirstByOrderByIdAsc();
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


}
