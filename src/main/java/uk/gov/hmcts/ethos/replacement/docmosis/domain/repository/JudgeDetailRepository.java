package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.judges_data.JudgeDetail;

import java.util.List;

@Repository
public interface JudgeDetailRepository extends JpaRepository<JudgeDetail, Integer> {
    List<JudgeDetail> getJudgeDetailByOffice(String office);
}

