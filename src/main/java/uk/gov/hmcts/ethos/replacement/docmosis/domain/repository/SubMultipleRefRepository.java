package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.SubMultipleReference;

@NoRepositoryBean
public interface SubMultipleRefRepository<T extends SubMultipleReference> extends JpaRepository<T, Integer> {
    @Query(value = "SELECT fn_ethossubmultiplecaserefgen(:multipleRef, :numberCases, :office)", nativeQuery = true)
    String ethosSubMultipleCaseRefGen(@Param("multipleRef") int multipleRef,
                                      @Param("numberCases") int numberCases, @Param("office") String office);
}