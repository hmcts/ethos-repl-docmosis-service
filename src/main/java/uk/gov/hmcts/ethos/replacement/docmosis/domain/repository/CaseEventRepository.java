package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.CaseEvent;

public interface CaseEventRepository extends JpaRepository<CaseEvent, UUID> {
//    @Query("SELECT ce FROM case_event ce"
//            + " WHERE ce.caseDataId = :caseDataId"
//            + " AND ce.createdDate < :createdDate"
//            + " ORDER BY ce.createdDate DESC")
//    List<CaseEvent> findEventPriorTo(LocalDateTime createdDate, Long caseDataId, Pageable pageable);

    @Query("SELECT ce FROM CaseEvent ce"
            + " WHERE ce.caseDataId = :caseDataId"
            + " ORDER BY ce.createdDate DESC")
    List<CaseEvent> findLatestEvents(Long caseDataId, Pageable pageable);

    @Query(value =
            "select distinct on(case_data_id) * " +
                    "from CaseEvent " +
                    "where event_id = :eventId " +
                    "and state_id = :state " +
                    "and case_data_id = :caseId" +
                    "order by created_date", nativeQuery = true)
    CaseEvent findFirstCaseEventByEventIdAndState(String eventId, String state, Long caseId);
}
