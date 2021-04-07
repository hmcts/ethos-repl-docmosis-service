package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.SubMultipleReferenceMidlandsWest;

@Repository
@Transactional
public interface SubMultipleRefMidlandsWestRepository
        extends SubMultipleRefRepository<SubMultipleReferenceMidlandsWest> {
}