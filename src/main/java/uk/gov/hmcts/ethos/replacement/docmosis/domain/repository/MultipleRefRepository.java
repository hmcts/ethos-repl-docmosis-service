package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.MultipleReference;

@NoRepositoryBean
public interface MultipleRefRepository<T extends MultipleReference> extends JpaRepository<T, Integer> {
    @Query(value = "SELECT fn_ethosmultiplecaserefgen(:numberCases, :office)", nativeQuery = true)
    String ethosMultipleCaseRefGen(@Param("numberCases") int numberCases,
                                   @Param("office") String office);
}