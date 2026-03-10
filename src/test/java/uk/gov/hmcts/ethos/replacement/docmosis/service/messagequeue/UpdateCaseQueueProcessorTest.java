package uk.gov.hmcts.ethos.replacement.docmosis.service.messagequeue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectProvider;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.UpdateCaseQueueMessage;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue.UpdateCaseQueueRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler.UpdateManagementService;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCaseQueueProcessorTest {

    @Spy
    @InjectMocks
    private UpdateCaseQueueProcessor updateCaseQueueProcessor;
    @Mock
    private UpdateCaseQueueRepository updateCaseQueueRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UpdateManagementService updateManagementService;
    @Mock
    private ObjectProvider<UpdateCaseQueueProcessor> selfProvider;

    private UpdateCaseQueueMessage queueMessage;
    private UpdateCaseMsg updateCaseMsg;

    @Before
    public void setUp() {
        queueMessage = UpdateCaseQueueMessage.builder()
            .id(1L)
            .messageId("message-id")
            .messageBody("{\"msg\":\"1\"}")
            .status(QueueMessageStatus.PENDING)
            .retryCount(2)
            .createdAt(LocalDateTime.now())
            .build();
        updateCaseMsg = TestMessageHelper.generateUpdateCaseMsg();
    }

    @Test
    public void shouldProcessMessage() throws Exception {
        when(updateCaseQueueRepository.lockMessage(eq(queueMessage.getMessageId()), any(), any(), any()))
            .thenReturn(1);
        when(objectMapper.readValue(queueMessage.getMessageBody(), UpdateCaseMsg.class)).thenReturn(updateCaseMsg);

        updateCaseQueueProcessor.processMessage(queueMessage);

        verify(updateManagementService).updateLogic(updateCaseMsg);
        verify(updateCaseQueueRepository).markAsCompleted(eq(queueMessage.getMessageId()), any());
    }

    @Test
    public void shouldSkipWhenAlreadyLocked() {
        when(updateCaseQueueRepository.lockMessage(eq(queueMessage.getMessageId()), any(), any(), any()))
            .thenReturn(0);

        updateCaseQueueProcessor.processMessage(queueMessage);

        verify(updateCaseQueueRepository, never()).markAsCompleted(any(), any());
    }

    @Test
    public void shouldHandleLastRetryErrorAndCheckFinish() throws Exception {
        when(objectMapper.readValue(queueMessage.getMessageBody(), UpdateCaseMsg.class)).thenReturn(updateCaseMsg);

        updateCaseQueueProcessor.handleError(queueMessage, new RuntimeException("bad"));

        verify(updateCaseQueueRepository).markAsFailed(eq(queueMessage.getMessageId()), eq("bad"),
            eq(3), eq(QueueMessageStatus.FAILED), any());
        verify(updateManagementService).addUnrecoverableErrorToDatabase(updateCaseMsg);
        verify(updateManagementService).checkIfFinish(updateCaseMsg);
    }

    @Test
    public void shouldHandleUnrecoverableError() throws Exception {
        updateCaseQueueProcessor.handleUnrecoverableError(queueMessage, updateCaseMsg, new IOException("failed"));

        verify(updateCaseQueueRepository).markAsFailed(eq(queueMessage.getMessageId()), eq("failed"),
            eq(3), eq(QueueMessageStatus.FAILED), any());
        verify(updateManagementService).addUnrecoverableErrorToDatabase(updateCaseMsg);
        verify(updateManagementService).checkIfFinish(updateCaseMsg);
    }
}
