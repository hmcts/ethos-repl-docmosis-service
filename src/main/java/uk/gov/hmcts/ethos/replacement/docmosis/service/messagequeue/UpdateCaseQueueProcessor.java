package uk.gov.hmcts.ethos.replacement.docmosis.service.messagequeue;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.UpdateCaseQueueMessage;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue.UpdateCaseQueueRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler.UpdateManagementService;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "queue", name = "enabled", havingValue = "true")
public class UpdateCaseQueueProcessor {

    private static final int MAX_RETRIES = 3;
    private static final int LOCK_DURATION_MINUTES = 5;

    private final UpdateCaseQueueRepository updateCaseQueueRepository;
    private final ObjectMapper objectMapper;
    private final UpdateManagementService updateManagementService;
    private final ObjectProvider<UpdateCaseQueueProcessor> selfProvider;

    @Value("${queue.update-case.batch-size:10}")
    private int batchSize;

    @Value("${queue.update-case.threads:15}")
    private int threadCount;

    private ExecutorService executor;
    private String processorId;

    public void init() {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(threadCount);
            try {
                processorId = InetAddress.getLocalHost().getHostName() + "-" + UUID.randomUUID();
            } catch (Exception e) {
                processorId = "processor-" + UUID.randomUUID();
            }
        }
    }

    @Scheduled(fixedDelayString = "${queue.update-case.poll-interval:1000}")
    public void processPendingMessages() {
        init();

        List<UpdateCaseQueueMessage> messages = updateCaseQueueRepository.findPendingMessages(
            LocalDateTime.now(),
            PageRequest.of(0, batchSize)
        );

        if (messages.isEmpty()) {
            return;
        }

        log.info("Found {} pending update-case messages to process", messages.size());

        UpdateCaseQueueProcessor self = selfProvider.getObject();
        messages.forEach(message -> executor.submit(() -> self.processMessage(message)));
    }

    @Transactional
    public void processMessage(UpdateCaseQueueMessage queueMessage) {
        int locked = updateCaseQueueRepository.lockMessage(
            queueMessage.getMessageId(),
            processorId,
            LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES),
            LocalDateTime.now()
        );

        if (locked == 0) {
            log.debug("Message {} already locked by another processor", queueMessage.getMessageId());
            return;
        }

        try {
            UpdateCaseMsg updateCaseMsg = objectMapper.readValue(
                queueMessage.getMessageBody(),
                UpdateCaseMsg.class
            );

            log.info("RECEIVED 'Update Case' ------> ethosCaseRef {} - multipleRef {} - multipleRefLinkMarkUp {}",
                     updateCaseMsg.getEthosCaseReference(),
                     updateCaseMsg.getMultipleRef(),
                     updateCaseMsg.getMultipleReferenceLinkMarkUp());

            handleUpdateCaseMessage(queueMessage, updateCaseMsg);
        } catch (Exception e) {
            selfProvider.getObject().handleError(queueMessage, e);
        }
    }

    private void handleUpdateCaseMessage(UpdateCaseQueueMessage queueMessage, UpdateCaseMsg updateCaseMsg) {
        try {
            updateManagementService.updateLogic(updateCaseMsg);
            updateCaseQueueRepository.markAsCompleted(queueMessage.getMessageId(), LocalDateTime.now());
            log.info("COMPLETED RECEIVED 'Update Case' ----> message with ID {}", queueMessage.getMessageId());
        } catch (IOException e) {
            log.error("Unrecoverable error occurred when handling 'Update Case' message with ID {}",
                      queueMessage.getMessageId(), e);
            selfProvider.getObject().handleUnrecoverableError(queueMessage, updateCaseMsg, e);
        } catch (Exception exception) {
            log.error("Potentially recoverable error occurred when handling 'Update Case' message with ID {}",
                      queueMessage.getMessageId(), exception);
            throw exception;
        }
    }

    @Transactional
    protected void handleError(UpdateCaseQueueMessage queueMessage, Exception exception) {
        log.error("Error processing update-case message {}: {}",
                  queueMessage.getMessageId(), exception.getMessage(), exception);

        if (isUnprocessableEntity(exception)) {
            updateCaseQueueRepository.markAsFailedNoRetry(
                queueMessage.getMessageId(),
                exception.getMessage(),
                LocalDateTime.now()
            );
            return;
        }

        boolean isLastRetry = (queueMessage.getRetryCount() + 1) >= MAX_RETRIES;

        updateCaseQueueRepository.incrementRetryAndMarkFailureIfMax(
            queueMessage.getMessageId(),
            exception.getMessage(),
            MAX_RETRIES,
            LocalDateTime.now()
        );

        if (isLastRetry) {
            log.info("RECOVERABLE FAILURE: Last retry checking if finished for message {}",
                     queueMessage.getMessageId());
            try {
                UpdateCaseMsg updateCaseMsg = objectMapper.readValue(
                    queueMessage.getMessageBody(),
                    UpdateCaseMsg.class
                );
                checkIfFinishWhenError(updateCaseMsg);
            } catch (Exception ex) {
                log.error("Error checking if finished after max retries", ex);
            }
        }
    }

    private boolean isUnprocessableEntity(Exception ex) {
        return ex instanceof HttpClientErrorException httpException
                && httpException.getStatusCode().value() == 422;
    }

    @Transactional
    protected void handleUnrecoverableError(UpdateCaseQueueMessage queueMessage,
                                            UpdateCaseMsg updateCaseMsg,
                                            Exception exception) {
        log.info("UNRECOVERABLE FAILURE: Check if finished");

        updateCaseQueueRepository.markAsFailed(
            queueMessage.getMessageId(),
            exception.getMessage(),
            queueMessage.getRetryCount() + 1,
            QueueMessageStatus.FAILED,
            LocalDateTime.now()
        );

        checkIfFinishWhenError(updateCaseMsg);
    }

    private void checkIfFinishWhenError(UpdateCaseMsg updateCaseMsg) {
        try {
            log.info("Adding unrecoverable error to database");
            updateManagementService.addUnrecoverableErrorToDatabase(updateCaseMsg);

            log.info("Checking if finished");
            updateManagementService.checkIfFinish(updateCaseMsg);
        } catch (Exception exception) {
            log.error("Error in checkIfFinishWhenError", exception);
        }
    }
}
