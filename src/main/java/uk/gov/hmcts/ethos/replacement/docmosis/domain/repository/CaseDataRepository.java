package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.CaseDataEntity;

public interface CaseDataRepository extends JpaRepository<CaseDataEntity, UUID> {

    @Query(value = "SELECT * FROM case_data cd WHERE cd.reference = :caseReference", nativeQuery = true)
    Optional<CaseDataEntity> findCaseDataByReference(Long caseReference);

    @Query("SELECT cd FROM case_data cd WHERE cd.id = :id")
    Optional<CaseDataEntity> findCaseDataById(String id);

    @Query(value = "SELECT * FROM case_data cd WHERE " +
            "cd.created_date >= :startDate " +
            "cd.created_date <= :endDate " +
            "and cd.case_type_id = :ctID", nativeQuery = true)
    List<CaseDataEntity> findCasesByCreationDateAndCaseType(Timestamp startDate, Timestamp endDate, String ctID);

    @Query(value =
            "SELECT * " +
                    "FROM case_data cd " +
                    "WHERE cd.case_type_id = :ctID " +
                    "AND CAST(cd.data as VARCHAR) LIKE :textToFind " +
                    "AND cd.created_date > :startDate", nativeQuery = true)
    List<CaseDataEntity> findCaseDataEntityByValue(String ctID, String textToFind, Timestamp startDate);
}
