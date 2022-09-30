package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.CaseDataEntity;

@Transactional
public interface CaseDataRepository extends CrudRepository<CaseDataEntity, Long> {

    @Query(value = "SELECT * FROM CaseDataEntity WHERE reference = :caseReference", nativeQuery = true)
    Optional<CaseDataEntity> findCaseDataByReference(Long caseReference);

//    @Query("SELECT cd FROM case_data cd WHERE cd.id = :id")
//    Optional<CaseDataEntity> findCaseDataById(String id);

    @Query(value = "SELECT * FROM CaseDataEntity WHERE " +
            "created_date >= :startDate " +
            "And created_date <= :endDate " +
            "And case_type_id = :ctID", nativeQuery = true)
    List<CaseDataEntity> findCasesByCreationDateAndCaseType(Timestamp startDate, Timestamp endDate, String ctID);

    @Query(value =
            "SELECT * " +
                    "FROM CaseDataEntity " +
                    "WHERE case_type_id = :ctID " +
                    "AND CAST(data as VARCHAR) LIKE :textToFind " +
                    "AND created_date > :startDate", nativeQuery = true)
    List<CaseDataEntity> findCaseDataEntityByValue(String ctID, String textToFind, Timestamp startDate);
}
