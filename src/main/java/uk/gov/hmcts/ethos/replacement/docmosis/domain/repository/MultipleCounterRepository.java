package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.MultipleCounter;

import java.util.List;

@Repository
@Transactional
public interface MultipleCounterRepository extends JpaRepository<MultipleCounter, Integer> {

    @Query(value = "SELECT fn_persistentQ_getNextMultipleCountVal(:multipleRef)", nativeQuery = true)
    int persistentQGetNextMultipleCountVal(@Param("multipleRef") String multipleRef);

    @Query(value = "SELECT fn_persistentQ_InsertFirstMultipleCountVal(:multipleRef)", nativeQuery = true)
    int persistentQInsertFirstMultipleCountVal(@Param("multipleRef") String multipleRef);

    List<MultipleCounter> findByMultipleref(String multipleRef);

    @Modifying
    @Query(value = "DELETE FROM multiplecounter WHERE multipleref = :multipleRef", nativeQuery = true)
    void deleteByMultipleref(@Param("multipleRef") String multipleRef);
}
