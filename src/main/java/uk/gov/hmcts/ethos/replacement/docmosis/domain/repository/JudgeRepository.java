package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import java.util.List;

@Repository
public interface JudgeRepository extends JpaRepository<Judge, Integer> {
    List<Judge> findByTribunalOffice(String tribunalOffice);
}

