package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseUpdateForCaseWorkerServiceTest {

    @InjectMocks
    private CaseUpdateForCaseWorkerService caseUpdateForCaseWorkerService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;

    @Before
    public void setUp() {
        CaseDetails caseDetails;
        ccdRequest = new CCDRequest();
        submitEvent = new SubmitEvent();
        caseDetails = new CaseDetails();
        caseDetails.setCaseData(new CaseData());
        ccdRequest.setCaseDetails(caseDetails);
        caseUpdateForCaseWorkerService = new CaseUpdateForCaseWorkerService(ccdClient);
    }

    @Test
    public void caseCreationRequest() throws IOException {
        when(ccdClient.startEventForCase(anyString(), any(), anyString())).thenReturn(ccdRequest);
        when(ccdClient.submitEventForCase(anyString(), any(), any(), anyString())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseUpdateForCaseWorkerService.caseUpdateRequest(ccdRequest, "authToken");
        assertEquals(submitEvent1, submitEvent);
    }
}