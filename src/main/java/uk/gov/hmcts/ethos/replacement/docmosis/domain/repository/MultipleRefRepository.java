package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.MultipleReference;

@NoRepositoryBean
public interface MultipleRefRepository<T extends MultipleReference> extends JpaRepository<T, Integer> {
    T findTopByOrderByIdDesc();
}