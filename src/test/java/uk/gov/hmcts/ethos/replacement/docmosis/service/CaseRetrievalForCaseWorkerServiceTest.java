package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseRetrievalForCaseWorkerServiceTest {
    private static final String EMPTY_STRING = "";
    @InjectMocks
    private CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;

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
        caseRetrievalForCaseWorkerService.caseRetrievalRequest("authToken", ccdRequest.getCaseDetails().getCaseTypeId(),
                ccdRequest.getCaseDetails().getJurisdiction(), "11111");
    }

    @Test
    public void caseRetrievalRequest() throws IOException {
        when(ccdClient.retrieveCase(anyString(), anyString(), anyString(), any())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseRetrievalForCaseWorkerService.caseRetrievalRequest("authToken",
                ccdRequest.getCaseDetails().getCaseTypeId(), ccdRequest.getCaseDetails().getJurisdiction(), "11111");
        assertEquals(submitEvent, submitEvent1);
    }

    @Test(expected = Exception.class)
    public void casesRetrievalRequestException() throws IOException {
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenThrow(new InternalException(ERROR_MESSAGE));
        caseRetrievalForCaseWorkerService.casesRetrievalRequest(ccdRequest, "authToken");
    }

    @Test
    public void casesRetrievalRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCases(anyString(), any(), any())).thenReturn(submitEventList);
        List<SubmitEvent> submitEventList1 = caseRetrievalForCaseWorkerService.casesRetrievalRequest(ccdRequest, "authToken");
        assertEquals(submitEventList, submitEventList1);
    }

    @Test(expected = Exception.class)
    public void casesRetrievalESRequestException() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), any())).thenThrow(new InternalException(ERROR_MESSAGE));
        caseRetrievalForCaseWorkerService.casesRetrievalESRequest("1111", "authToken",
                ccdRequest.getCaseDetails().getCaseTypeId(), new ArrayList<>(Collections.singleton("1")));
    }

    @Test
    public void casesRetrievalESRequest() throws IOException {
        List<SubmitEvent> submitEventList = Collections.singletonList(submitEvent);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), any())).thenReturn(submitEventList);
        List<SubmitEvent> submitEventList1 = caseRetrievalForCaseWorkerService.casesRetrievalESRequest("1111", "authToken",
                ccdRequest.getCaseDetails().getCaseTypeId(), new ArrayList<>(Collections.singleton("1")));
        assertEquals(submitEventList, submitEventList1);
    }

    @Test
    public void testTransferSourceCaseRetrievalESRequest() throws IOException {
        String currentCaseId = "123456";
        String authToken = "authToken";

        SubmitEvent submitEvent = getSubmitEvent();
        when(ccdClient.retrieveTransferredCaseElasticSearch(any(), any(), any()))
                .thenReturn(List.of(submitEvent));
        Pair<String, List<SubmitEvent>> result = caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(currentCaseId,
                authToken, List.of("Leeds"));

        assertEquals(submitEvent, result.getSecond().get(0));
        assertEquals("Leeds", result.getFirst());
    }

    @Test
    public void testTransferSourceCaseRetrievalESRequest_When_CaseTypeIdsToCheck_Null() throws IOException {
        String currentCaseId = "123456";
        String authToken = "authToken";
        SubmitEvent submitEvent = getSubmitEvent();
        when(ccdClient.retrieveTransferredCaseElasticSearch(any(), any(), any())).thenReturn(List.of(submitEvent));
        Pair<String, List<SubmitEvent>> result = caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                currentCaseId, authToken, new ArrayList<>());
        assertNotNull(result);
        assertEquals(EMPTY_STRING, result.getFirst());
        assertTrue(result.getSecond().isEmpty());
    }

    @Test(expected = Exception.class)
    public void testTransferSourceCaseRetrievalESRequestThrowsException() throws IOException {
        String currentCaseId = "123456";
        String authToken = "authToken";
        SubmitEvent submitEvent = getSubmitEvent();
        when(ccdClient.retrieveTransferredCaseElasticSearch(any(), any(), any()))
                .thenReturn(List.of(submitEvent));
        caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(currentCaseId,
                authToken, null);
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