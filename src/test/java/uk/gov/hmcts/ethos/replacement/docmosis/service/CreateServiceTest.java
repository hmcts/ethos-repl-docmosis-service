package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.CreateService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.CreateService.CREATE_EXIST_ERROR_MESSAGE;

public class CreateServiceTest {
    private CcdClient ccdClient;
    private CreateService createService;
    private String userToken;

    @BeforeEach
    void setUp() {
        ccdClient = mock(CcdClient.class);
        createService = new CreateService(ccdClient);
        userToken = "userToken";
    }

    @Test
    void initCreateAdmin_NotExist_shouldReturnNoError() throws IOException {
        when(ccdClient.executeElasticSearch(anyString(), anyString(), anyString())).thenReturn(new ArrayList<>());
        List<String> errors = createService.initCreateAdmin(userToken);
        assertEquals(0, errors.size());
    }

    @Test
    void initCreateAdmin_Exist_shouldReturnError() throws IOException {
        var submitEvent = new SubmitEvent();
        when(ccdClient.executeElasticSearch(anyString(), anyString(), anyString())).thenReturn(List.of(submitEvent));
        List<String> errors = createService.initCreateAdmin(userToken);
        assertEquals(1, errors.size());
        assertEquals(CREATE_EXIST_ERROR_MESSAGE, errors.getFirst());
    }
}
