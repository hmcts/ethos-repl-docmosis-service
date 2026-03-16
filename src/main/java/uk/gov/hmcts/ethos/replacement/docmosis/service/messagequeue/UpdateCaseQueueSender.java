package uk.gov.hmcts.ethos.replacement.docmosis.service.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.UpdateCaseQueueMessage;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue.UpdateCaseQueueRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "queue", name = "enabled", havingValue = "true")
public class UpdateCaseQueueSender {

    private final UpdateCaseQueueRepository updateCaseQueueRepository;
    private final ObjectMapper objectMapper;
    private final ObjectProvider<UpdateCaseQueueSender> selfProvider;

    @Transactional
    public void sendMessage(UpdateCaseMsg updateCaseMsg) {
        try {
            String messageBody = objectMapper.writeValueAsString(updateCaseMsg);
            UpdateCaseQueueMessage queueMessage = UpdateCaseQueueMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageBody(messageBody)
                .status(QueueMessageStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .retryCount(0)
                .build();

            updateCaseQueueRepository.save(queueMessage);
            log.debug("Sent UpdateCaseMsg to database queue: ethosCaseRef={}, multipleRef={}",
                      updateCaseMsg.getEthosCaseReference(), updateCaseMsg.getMultipleRef());
        } catch (JsonProcessingException | DataAccessException e) {
            log.error("Failed to send UpdateCaseMsg to queue", e);
            throw new IllegalStateException("Failed to queue update case message", e);
        }
    }

    public void sendMessageAsync(UpdateCaseMsg updateCaseMsg) {
        selfProvider.getObject().sendMessage(updateCaseMsg);
    }
}
