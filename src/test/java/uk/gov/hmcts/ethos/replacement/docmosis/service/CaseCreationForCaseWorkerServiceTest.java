package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
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
    @Mock
    private SingleReferenceService singleReferenceService;
    @Mock
    private MultipleReferenceService multipleReferenceService;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        caseData.setCaseRefNumberCount("2");
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("Manchester");
        ccdRequest.setCaseDetails(caseDetails);
        submitEvent = new SubmitEvent();
        caseCreationForCaseWorkerService = new CaseCreationForCaseWorkerService(ccdClient, singleReferenceService, multipleReferenceService);
    }

    @Test(expected = Exception.class)
    public void caseCreationRequestException() throws IOException {
        when(ccdClient.startCaseCreation(anyString(), any())).thenThrow(new RuntimeException());
        when(ccdClient.submitCaseCreation(anyString(), any(), any())).thenReturn(submitEvent);
        caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, "authToken");
    }

    @Test
    public void caseCreationRequest() throws IOException {
        when(ccdClient.startCaseCreation(anyString(), any())).thenReturn(ccdRequest);
        when(ccdClient.submitCaseCreation(anyString(), any(), any())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, "authToken");
        assertEquals(submitEvent1, submitEvent);
    }

    @Test
    public void generateCaseRefNumbers() {
        when(singleReferenceService.createReference("Manchester",2)).thenReturn("2100001/2019");
        when(multipleReferenceService.createReference("Manchester_Multiple",1)).thenReturn("2100005");
        CaseData caseData = caseCreationForCaseWorkerService.generateCaseRefNumbers(ccdRequest);
        assertEquals(caseData.getStartCaseRefNumber(),"2100001/2019");
        assertEquals(caseData.getCaseRefNumberCount(),"2");
        assertEquals(caseData.getMultipleRefNumber(),"2100005");
    }
}