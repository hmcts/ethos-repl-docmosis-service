package uk.gov.hmcts.ethos.replacement.docmosis.servicebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.CreateUpdatesDto;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.CreationDataModel;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.messagequeue.CreateUpdatesQueueRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.SCOTLAND_BULK_CASE_TYPE_ID;

@RunWith(SpringJUnit4ClassRunner.class)
public class CreateUpdatesBusSenderTest {

    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private CreateUpdatesQueueRepository createUpdatesQueueRepository;
    @Mock
    private ObjectMapper objectMapper;

    private CreateUpdatesDto createUpdatesDto;

    private CreationDataModel creationDataModel;

    private List<String> ethosCaseRefCollection;

    @Before
    public void setUp() {
        createUpdatesBusSender = new CreateUpdatesBusSender(createUpdatesQueueRepository, objectMapper);
        ethosCaseRefCollection = Arrays.asList("4150001/2020", "4150002/2020", "4150003/2020",
            "4150004/2020", "4150005/2020");
        createUpdatesDto = getCreateUpdatesDto(ethosCaseRefCollection);
        creationDataModel = getCreationDataModel(ethosCaseRefCollection);
    }

    @Test
    public void shouldSaveQueueMessages() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"msg\":1}");

        createUpdatesBusSender.sendUpdatesToQueue(createUpdatesDto, creationDataModel, new ArrayList<>(),
            String.valueOf(ethosCaseRefCollection.size()));

        verify(createUpdatesQueueRepository, atLeastOnce()).save(any());
    }

    @Test
    public void shouldHandleQueueSaveException() throws Exception {
        List<String> errors = new ArrayList<>();
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("boom"));

        createUpdatesBusSender.sendUpdatesToQueue(createUpdatesDto, creationDataModel, errors,
            String.valueOf(ethosCaseRefCollection.size()));

        assertEquals(1, errors.size());
        assertEquals("Failed to send the message to the queue", errors.getFirst());
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
