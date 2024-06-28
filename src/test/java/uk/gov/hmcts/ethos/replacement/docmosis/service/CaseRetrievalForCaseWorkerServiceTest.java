package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseRetrievalForCaseWorkerServiceTest {
     @InjectMocks
    private CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;
    private static final String AUTH_TOKEN = "authToken";
    private static final String MESSAGE = "Failed to retrieve case for : ";
    @Before
    public void setUp() {
        CaseDetails caseDetails;
        ccdRequest = new CCDRequest();
        caseDetails = new CaseDetails();
        caseDetails.setJurisdiction("TRIBUNALS");
        caseDetails.setCaseTypeId("Manchester_V3");
        ccdRequest.setCaseDetails(caseDetails);
        submitEvent = new SubmitEvent();
        caseRetrievalForCaseWorkerService = new CaseRetrievalForCaseWorkerService(ccdClient);
    }

    @Test(expected = Exception.class)
    public void caseRetrievalRequestException() throws IOException {
        when(ccdClient.retrieveCase(anyString(), anyString(), anyString(), any())).thenThrow(new InternalException(ERROR_MESSAGE));
        caseRetrievalForCaseWorkerService.caseRetrievalRequest(AUTH_TOKEN, ccdRequest.getCaseDetails().getCaseTypeId(),
                ccdRequest.getCaseDetails().getJurisdiction(), "11111");
    }

    @Test
    public void caseRetrievalRequest() throws IOException {
        when(ccdClient.retrieveCase(anyString(), anyString(), anyString(), any())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseRetrievalForCaseWorkerService.caseRetrievalRequest(AUTH_TOKEN,
                ccdRequest.getCaseDetails().getCaseTypeId(), ccdRequest.getCaseDetails().getJurisdiction(), "11111");
        assertEquals(submitEvent, submitEvent1);
    }

    @Test(expected = Exception.class)
    public void casesRetrievalRequestException() throws IOException {
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenThrow(new InternalException(ERROR_MESSAGE));
        caseRetrievalForCaseWorkerService.casesRetrievalRequest(ccdRequest, AUTH_TOKEN);
    }

    @Test
    public void casesRetrievalRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenReturn(submitEventList);
        List<SubmitEvent> submitEventList1 = caseRetrievalForCaseWorkerService.casesRetrievalRequest(ccdRequest,
                AUTH_TOKEN);
        assertEquals(submitEventList, submitEventList1);
    }

    @Test(expected = Exception.class)
    public void casesRetrievalESRequestException() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), any())).thenThrow(new InternalException(ERROR_MESSAGE));
        caseRetrievalForCaseWorkerService.casesRetrievalESRequest("1111", AUTH_TOKEN,
                ccdRequest.getCaseDetails().getCaseTypeId(), new ArrayList<>(Collections.singleton("1")));
    }

    @Test
    public void casesRetrievalESRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), any())).thenReturn(submitEventList);
        List<SubmitEvent> submitEventList1 = caseRetrievalForCaseWorkerService.casesRetrievalESRequest(
                "1111", AUTH_TOKEN, ccdRequest.getCaseDetails().getCaseTypeId(),
                new ArrayList<>(Collections.singleton("1")));
        assertEquals(submitEventList, submitEventList1);
    }

    @Test
    public void testCaseRefRetrievalRequest_Success() throws Exception {
        String ethosCaseReference = "R5000656/2020";
        String currentCaseId = "123456";
        when(ccdClient.retrieveTransferredCaseReference(any(), any(), any(), any()))
                .thenReturn(ethosCaseReference);

        String result = caseRetrievalForCaseWorkerService.caseRefRetrievalRequest(
                AUTH_TOKEN, "Newcastle", "EMPLOYMENT", currentCaseId);

        assertEquals(ethosCaseReference, result);
        verify(ccdClient, times(1)).retrieveTransferredCaseReference(
                AUTH_TOKEN, "Newcastle", "EMPLOYMENT", currentCaseId);
    }

    @Test
    public void testCaseRefRetrievalRequest_Exception() throws Exception {
        String errorMessage = "TEST Error Message";
        String caseId = "44456";
        when(ccdClient.retrieveTransferredCaseReference(any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Exception exception = assertThrows(CaseCreationException.class, () -> {
            caseRetrievalForCaseWorkerService.caseRefRetrievalRequest(AUTH_TOKEN, "Newcastle",
                    "EMPLOYMENT", caseId);
        });

        String expectedMessage = MESSAGE + caseId + errorMessage;
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(ccdClient, times(1)).retrieveTransferredCaseReference(
                AUTH_TOKEN, "Newcastle", "EMPLOYMENT", caseId);
    }

    @Test
    public void testTransferSourceCaseRetrievalESRequest() throws IOException {
        String currentCaseId = "123456";
        SubmitEvent submitEvent = getSubmitEvent();
        when(ccdClient.retrieveCasesWithDuplicateEthosRefElasticSearch(any(), any(), any()))
                .thenReturn(List.of(submitEvent));
        List<Pair<String, List<SubmitEvent>>> result =
                caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                        currentCaseId, "Newcastle", AUTH_TOKEN, List.of("Leeds"));

        assertEquals(submitEvent, result.get(0).getSecond().get(0));
        assertEquals("Leeds", result.get(0).getFirst());
    }

    @Test
    public void testTransferSourceCaseRetrievalESRequestContinue() throws IOException {
        String currentCaseId = "123456";
        SubmitEvent submitEventLocal = getSubmitEvent();
        when(ccdClient.retrieveCasesWithDuplicateEthosRefElasticSearch(any(), any(), any()))
                .thenReturn(List.of(submitEventLocal));
        List<Pair<String, List<SubmitEvent>>> result =
                caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                        currentCaseId, "Newcastle", AUTH_TOKEN,
                        List.of("Leeds", "Newcastle", "Manchester"));

        assertEquals(submitEventLocal, result.get(0).getSecond().get(0));
        assertEquals("Leeds", result.get(0).getFirst());
        verify(ccdClient, times(0)).retrieveCasesWithDuplicateEthosRefElasticSearch(
                AUTH_TOKEN, "Newcastle", currentCaseId);
        verify(ccdClient, times(1)).retrieveCasesWithDuplicateEthosRefElasticSearch(
                AUTH_TOKEN, "Leeds", currentCaseId);
        verify(ccdClient, times(1)).retrieveCasesWithDuplicateEthosRefElasticSearch(
                AUTH_TOKEN, "Manchester", currentCaseId);
    }

    @Test
    public void testTransferSourceCaseRetrievalESRequest_When_CaseTypeIdsToCheck_Null() throws IOException {
        String currentCaseId = "123456";
        SubmitEvent submitEvent = getSubmitEvent();
        when(ccdClient.retrieveTransferredCaseElasticSearch(any(), any(), any())).thenReturn(List.of(submitEvent));
        List<Pair<String, List<SubmitEvent>>> result =
                caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                        currentCaseId, "Newcastle", AUTH_TOKEN, new ArrayList<>());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test(expected = Exception.class)
    public void testTransferSourceCaseRetrievalESRequestThrowsException() throws IOException {
        String currentCaseId = "123456";
        SubmitEvent submitEvent = getSubmitEvent();
        when(ccdClient.retrieveTransferredCaseElasticSearch(any(), any(), any()))
                .thenReturn(List.of(submitEvent));
        caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(currentCaseId,
                "Newcastle", AUTH_TOKEN, null);
    }

    private SubmitEvent getSubmitEvent() {
        CaseData linkedCaseData = new CaseData();
        linkedCaseData.setEthosCaseReference("R5000656");
        SubmitEvent submitEventTwo = new SubmitEvent();
        submitEventTwo.setCaseId(123456);
        submitEventTwo.setCaseData(linkedCaseData);
        return submitEventTwo;
    }
}