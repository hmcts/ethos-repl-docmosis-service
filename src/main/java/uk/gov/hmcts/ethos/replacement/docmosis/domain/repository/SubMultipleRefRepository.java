package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.SubMultipleReference;

@NoRepositoryBean
public interface SubMultipleRefRepository<T extends SubMultipleReference> extends JpaRepository<T, Integer> {
    @Procedure("fn_ethossubmultiplecaserefgen")
    String ethosSubMultipleCaseRefGen(int multipleRef, int numberCases, String office);
}