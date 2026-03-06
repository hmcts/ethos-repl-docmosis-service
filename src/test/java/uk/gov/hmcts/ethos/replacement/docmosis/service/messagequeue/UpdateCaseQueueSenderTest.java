package uk.gov.hmcts.ethos.replacement.docmosis.service.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectProvider;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue.UpdateCaseQueueRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCaseQueueSenderTest {

    @InjectMocks
    private UpdateCaseQueueSender updateCaseQueueSender;
    @Mock
    private UpdateCaseQueueRepository updateCaseQueueRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ObjectProvider<UpdateCaseQueueSender> selfProvider;

    private UpdateCaseMsg updateCaseMsg;

    @Before
    public void setUp() {
        updateCaseMsg = TestMessageHelper.generateUpdateCaseMsg();
    }

    @Test
    public void shouldSendMessage() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"value\":1}");

        updateCaseQueueSender.sendMessage(updateCaseMsg);

        verify(updateCaseQueueRepository).save(any());
    }

    @Test
    public void shouldThrowOnSerializationError() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fail") {
        });

        assertThrows(IllegalStateException.class, () -> updateCaseQueueSender.sendMessage(updateCaseMsg));
    }

    @Test
    public void shouldSendMessageAsyncViaSelfProvider() {
        UpdateCaseQueueSender senderSpy = Mockito.spy(updateCaseQueueSender);
        when(selfProvider.getObject()).thenReturn(senderSpy);
        updateCaseQueueSender.sendMessageAsync(updateCaseMsg);
        verify(senderSpy).sendMessage(updateCaseMsg);
    }
}
