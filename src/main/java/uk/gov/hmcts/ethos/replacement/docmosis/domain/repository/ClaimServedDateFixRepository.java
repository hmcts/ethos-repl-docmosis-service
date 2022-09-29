package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.ClaimServedDateFix;

@Repository
public interface ClaimServedDateFixRepository<T extends ClaimServedDateFix> extends JpaRepository<T, Integer> {
    @Procedure("fn_AddClaimServedDate")
    String addClaimServedDate(Date fromDate, Date toDate, String office);
}
