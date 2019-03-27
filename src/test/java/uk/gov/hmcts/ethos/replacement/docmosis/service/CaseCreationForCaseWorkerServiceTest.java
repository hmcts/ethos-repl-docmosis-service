package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseCreationForCaseWorkerServiceTest {

    @InjectMocks
    private CaseCreationForCaseWorkerService caseCreationForCaseWorkerService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        submitEvent = new SubmitEvent();
        caseCreationForCaseWorkerService = new CaseCreationForCaseWorkerService(ccdClient);
    }

    @Test
    public void caseCreationRequest() throws IOException {
        when(ccdClient.startCaseCreation(anyString(), any())).thenReturn(ccdRequest);
        when(ccdClient.submitCaseCreation(anyString(), any(), any())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, "authToken");
        assertEquals(submitEvent1, submitEvent);
    }
}