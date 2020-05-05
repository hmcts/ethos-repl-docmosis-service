package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.MultipleReferenceWatford;

@Repository
@Transactional
public interface MultipleRefWatfordRepository extends MultipleRefRepository<MultipleReferenceWatford> {
}