package uk.gov.hmcts.ethos.replacement.docmosis.servicebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.CreateUpdatesDto;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.CreationDataModel;
import uk.gov.hmcts.ecm.compat.common.servicebus.ServiceBusSender;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue.CreateUpdatesQueueRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.SCOTLAND_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class CreateUpdatesBusSenderTest {

    private CreateUpdatesBusSender serviceBusCreateUpdatesBusSender;
    private CreateUpdatesBusSender dbCreateUpdatesBusSender;
    @Mock
    private ServiceBusSender serviceBusSender;
    @Mock
    private CreateUpdatesQueueRepository createUpdatesQueueRepository;
    @Mock
    private ObjectProvider<ServiceBusSender> serviceBusSenderProvider;
    @Mock
    private ObjectMapper objectMapper;

    private CreateUpdatesDto createUpdatesDto;

    private CreationDataModel creationDataModel;

    private List<String> ethosCaseRefCollection;

    @Before
    public void setUp() {
        when(serviceBusSenderProvider.getIfAvailable()).thenReturn(serviceBusSender);
        serviceBusCreateUpdatesBusSender = new CreateUpdatesBusSender(serviceBusSenderProvider,
            createUpdatesQueueRepository, objectMapper, false);
        dbCreateUpdatesBusSender = new CreateUpdatesBusSender(serviceBusSenderProvider,
            createUpdatesQueueRepository, objectMapper, true);
        ethosCaseRefCollection = Arrays.asList("4150001/2020", "4150002/2020", "4150003/2020",
            "4150004/2020", "4150005/2020");
        createUpdatesDto = getCreateUpdatesDto(ethosCaseRefCollection);
        creationDataModel = getCreationDataModel(ethosCaseRefCollection);
    }

    @Test
    public void shouldSendToServiceBusWhenQueueDisabled() {
        serviceBusCreateUpdatesBusSender.sendUpdatesToQueue(createUpdatesDto, creationDataModel, new ArrayList<>(),
            String.valueOf(ethosCaseRefCollection.size()));
        verify(serviceBusSender, atLeastOnce()).sendMessage(any());
    }

    @Test
    public void shouldHandleServiceBusException() {
        List<String> errors = new ArrayList<>();
        doThrow(new InternalException(ERROR_MESSAGE))
                .when(serviceBusSender).sendMessage(any());
        serviceBusCreateUpdatesBusSender.sendUpdatesToQueue(createUpdatesDto, creationDataModel, errors,
            String.valueOf(ethosCaseRefCollection.size()));
        verify(serviceBusSender, atLeastOnce()).sendMessage(any());
        assertEquals(1, errors.size());
        assertEquals("Failed to send the message to the queue", errors.getFirst());
    }

    @Test
    public void shouldSaveQueueMessagesWhenQueueEnabled() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"msg\":1}");

        dbCreateUpdatesBusSender.sendUpdatesToQueue(createUpdatesDto, creationDataModel, new ArrayList<>(),
            String.valueOf(ethosCaseRefCollection.size()));

        verify(createUpdatesQueueRepository, atLeastOnce()).save(any());
        Mockito.verifyNoInteractions(serviceBusSender);
    }

    private CreateUpdatesDto getCreateUpdatesDto(List<String> ethosCaseRefCollection) {
        return CreateUpdatesDto.builder()
                .caseTypeId(SCOTLAND_BULK_CASE_TYPE_ID)
                .jurisdiction("EMPLOYMENT")
                .multipleRef("4150001")
                .username("testEmail@hotmail.com")
                .ethosCaseRefCollection(ethosCaseRefCollection)
                .build();
    }

    private CreationDataModel getCreationDataModel(List<String> ethosCaseRefCollection) {
        return CreationDataModel.builder()
                .lead(ethosCaseRefCollection.getFirst())
                .multipleRef("4150001")
                .build();
    }

}
