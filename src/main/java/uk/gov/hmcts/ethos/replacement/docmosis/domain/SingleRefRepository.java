package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SingleRefRepository<T extends SingleReference> extends JpaRepository<T, Integer> {
    T findFirstByOrderByIdAsc();
}