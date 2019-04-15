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
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseRetrievalForCaseWorkerServiceTest {

    @InjectMocks
    private CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        submitEvent = new SubmitEvent();
        caseRetrievalForCaseWorkerService = new CaseRetrievalForCaseWorkerService(ccdClient);
    }

    @Test
    public void caseRetrievalRequest() throws IOException {
        when(ccdClient.retrieveCase(anyString(), any(), any())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseRetrievalForCaseWorkerService.caseRetrievalRequest(ccdRequest, "authToken");
        assertEquals(submitEvent1, submitEvent);
    }

    @Test
    public void casesRetrievalRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCases(anyString(), any())).thenReturn(submitEventList);
        List<SubmitEvent> submitEventList1 = caseRetrievalForCaseWorkerService.casesRetrievalRequest(ccdRequest, "authToken");
        assertEquals(submitEventList1, submitEventList);
    }

}