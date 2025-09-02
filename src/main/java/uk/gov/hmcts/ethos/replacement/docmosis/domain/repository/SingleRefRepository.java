package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.SingleReference;

@NoRepositoryBean
public interface SingleRefRepository<T extends SingleReference> extends JpaRepository<T, Integer> {
    @Query(value = "SELECT fn_ethoscaserefgen(:numberCases, :currentYear, :office)", nativeQuery = true)
    String ethosCaseRefGen(@Param("numberCases") int numberCases,
                           @Param("currentYear") int currentYear,
                           @Param("office") String office);
}