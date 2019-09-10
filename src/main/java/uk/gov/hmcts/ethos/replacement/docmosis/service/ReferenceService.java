package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;

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

    public SingleReference createReference(String caseTypeId, String caseId) {
        PreviousRefObject previousRefObject;
        if (caseTypeId.equals(MANCHESTER_CASE_TYPE_ID) || caseTypeId.equals(MANCHESTER_USERS_CASE_TYPE_ID)) {
            previousRefObject = getPreviousReference(singleRefManchesterRepository);
            return singleRefManchesterRepository.save(new SingleReferenceManchester(caseId, previousRefObject.getPreviousId(), previousRefObject.getPreviousYear()));
        } else if (caseTypeId.equals(SCOTLAND_CASE_TYPE_ID) || caseTypeId.equals(SCOTLAND_USERS_CASE_TYPE_ID)) {
            previousRefObject = getPreviousReference(singleRefScotlandRepository);
            return singleRefScotlandRepository.save(new SingleReferenceScotland(caseId, previousRefObject.getPreviousId(), previousRefObject.getPreviousYear()));
        }
        previousRefObject = getPreviousReference(singleRefScotlandRepository);
        return singleRefScotlandRepository.save(new SingleReferenceScotland(caseId, previousRefObject.getPreviousId(), previousRefObject.getPreviousYear()));
    }

    private PreviousRefObject getPreviousReference(SingleRefRepository referenceRepository) {
        SingleReference reference = referenceRepository.findFirstByOrderByIdAsc();
        PreviousRefObject previousRefObject = new PreviousRefObject();
        if (reference != null) {
            log.info("Previous REF: " + reference.toString());
            previousRefObject.setPreviousId(reference.getCaseId());
            previousRefObject.setPreviousYear(reference.getYear());
        } else {
            log.info("No elements in DB yet");
            previousRefObject.setPreviousId("");
            previousRefObject.setPreviousYear("");
        }
        return previousRefObject;
    }
}
