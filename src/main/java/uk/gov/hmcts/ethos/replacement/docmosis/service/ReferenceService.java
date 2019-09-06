package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.ReferenceRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.Reference;

@Slf4j
@Service("referenceService")
public class ReferenceService {

    private final ReferenceRepository referenceRepository;

    @Autowired
    public ReferenceService(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    public void getReference() {
        Reference reference = referenceRepository.findFirstByOrderByIdAsc();
        log.info("REFERENCE PREVIOUS ONE: " + reference.toString());
    }

    public Reference createReference(String caseId) {
        return referenceRepository.save(new Reference(caseId));
    }
}
