package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CourtWorker;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CourtWorkerType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;

import java.util.List;

@Repository
public interface CourtWorkerRepository extends JpaRepository<CourtWorker, Integer> {
    List<CourtWorker> findByTribunalOfficeAndType(TribunalOffice tribunalOffice, CourtWorkerType courtWorkerType);
}

