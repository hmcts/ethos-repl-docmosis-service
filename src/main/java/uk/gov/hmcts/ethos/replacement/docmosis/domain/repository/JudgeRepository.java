package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;

@Repository
public interface JudgeRepository extends JpaRepository<Judge, Integer> {
    List<Judge> findByTribunalOffice(String tribunalOffice);
}

