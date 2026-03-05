package uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.CreateUpdatesQueueMessage;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CreateUpdatesQueueRepository extends JpaRepository<CreateUpdatesQueueMessage, Long> {

    @Query("SELECT m FROM CreateUpdatesQueueMessage m "
           + "WHERE m.status = "
           + "uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus.PENDING "
           + "AND (m.lockedUntil IS NULL OR m.lockedUntil < :now) "
           + "ORDER BY m.createdAt ASC")
    List<CreateUpdatesQueueMessage> findPendingMessages(@Param("now") LocalDateTime now,
                                                        Pageable pageable);

    @Modifying
    @Query("UPDATE CreateUpdatesQueueMessage m "
           + "SET m.status = "
           + "uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus.PROCESSING, "
           + "m.lockedBy = :lockedBy, "
           + "m.lockedUntil = :lockedUntil "
           + "WHERE m.messageId = :messageId "
           + "AND m.status = "
           + "uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus.PENDING "
           + "AND (m.lockedUntil IS NULL OR m.lockedUntil < :now)")
    int lockMessage(@Param("messageId") String messageId,
                    @Param("lockedBy") String lockedBy,
                    @Param("lockedUntil") LocalDateTime lockedUntil,
                    @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE CreateUpdatesQueueMessage m "
           + "SET m.status = uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus.COMPLETED, "
           + "m.processedAt = :processedAt, "
           + "m.lockedBy = NULL, "
           + "m.lockedUntil = NULL "
           + "WHERE m.messageId = :messageId")
    void markAsCompleted(@Param("messageId") String messageId,
                         @Param("processedAt") LocalDateTime processedAt);

    @Modifying
    @Query("UPDATE CreateUpdatesQueueMessage m "
           + "SET m.status = :status, "
           + "m.errorMessage = :errorMessage, "
           + "m.retryCount = :retryCount, "
           + "m.lockedBy = NULL, "
           + "m.lockedUntil = NULL, "
           + "m.processedAt = CASE "
           + "WHEN :status = uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus.FAILED "
           + "THEN :processedAt ELSE NULL END "
           + "WHERE m.messageId = :messageId")
    void markAsFailed(@Param("messageId") String messageId,
                      @Param("errorMessage") String errorMessage,
                      @Param("retryCount") int retryCount,
                      @Param("status") QueueMessageStatus status,
                      @Param("processedAt") LocalDateTime processedAt);
}
