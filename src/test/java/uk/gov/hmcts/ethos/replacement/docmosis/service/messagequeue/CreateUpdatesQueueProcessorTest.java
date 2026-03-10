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
import uk.gov.hmcts.ecm.compat.common.model.servicebus.CreateUpdatesMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.CreateUpdatesQueueMessage;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue.QueueMessageStatus;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleCounterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue.CreateUpdatesQueueRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateUpdatesQueueProcessorTest {

    @Spy
    @InjectMocks
    private CreateUpdatesQueueProcessor createUpdatesQueueProcessor;
    @Mock
    private CreateUpdatesQueueRepository createUpdatesQueueRepository;
    @Mock
    private UpdateCaseQueueSender updateCaseQueueSender;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ObjectProvider<CreateUpdatesQueueProcessor> selfProvider;
    @Mock
    private MultipleCounterRepository multipleCounterRepository;

    private CreateUpdatesQueueMessage queueMessage;

    @Before
    public void setUp() {
        queueMessage = CreateUpdatesQueueMessage.builder()
            .id(1L)
            .messageId("message-id")
            .messageBody("{\"msg\":\"1\"}")
            .status(QueueMessageStatus.PENDING)
            .retryCount(0)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    public void shouldProcessMessage() throws Exception {
        CreateUpdatesMsg createUpdatesMsg = TestMessageHelper.generateCreateUpdatesMsg();

        when(createUpdatesQueueRepository.lockMessage(eq(queueMessage.getMessageId()), any(), any(), any()))
            .thenReturn(1);
        when(objectMapper.readValue(queueMessage.getMessageBody(), CreateUpdatesMsg.class))
            .thenReturn(createUpdatesMsg);

        createUpdatesQueueProcessor.processMessage(queueMessage);

        verify(multipleCounterRepository).persistentQInsertFirstMultipleCountVal(createUpdatesMsg.getMultipleRef());
        verify(updateCaseQueueSender, times(createUpdatesMsg.getEthosCaseRefCollection().size())).sendMessage(any());
        verify(createUpdatesQueueRepository).markAsCompleted(eq(queueMessage.getMessageId()), any());
    }

    @Test
    public void shouldSkipWhenAlreadyLocked() {
        when(createUpdatesQueueRepository.lockMessage(eq(queueMessage.getMessageId()), any(), any(), any()))
            .thenReturn(0);

        createUpdatesQueueProcessor.processMessage(queueMessage);

        verify(createUpdatesQueueRepository, never()).markAsCompleted(any(), any());
        verify(updateCaseQueueSender, never()).sendMessage(any());
    }

    @Test
    public void shouldHandleError() {
        createUpdatesQueueProcessor.handleError(queueMessage, new IllegalStateException("bad"));

        verify(createUpdatesQueueRepository).markAsFailed(eq(queueMessage.getMessageId()), eq("bad"),
            eq(1), eq(QueueMessageStatus.PENDING), any());
    }
}
