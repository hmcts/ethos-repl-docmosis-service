package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.compat.common.client.CcdClient;
import uk.gov.hmcts.ecm.compat.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.service.ConciliationTrackService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.MULTIPLE_CASE_TYPE;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleUpdateServiceTest {

    @InjectMocks
    private SingleUpdateService singleUpdateService;
    @Mock
    private CcdClient ccdClient;
    @Mock
    private ConciliationTrackService conciliationTrackService;

    private SubmitEvent submitEvent;
    private List<SubmitMultipleEvent> submitMultipleEvents;
    private UpdateCaseMsg updateCaseMsg;
    private String userToken;

    @Before
    public void setUp() {
        submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("4150002/2020");
        caseData.setEcmCaseType(MULTIPLE_CASE_TYPE);
        caseData.setMultipleReference("4150002");
        caseData.setMultipleReferenceLinkMarkUp("MultipleReferenceLinkMarkUp");
        submitEvent.setCaseData(caseData);
        submitEvent.setState(ACCEPTED_STATE);

        SubmitMultipleEvent submitMultipleEvent = new SubmitMultipleEvent();
        MultipleData multipleData = new MultipleData();
        multipleData.setMultipleReference("4150002");
        submitMultipleEvent.setCaseData(multipleData);
        submitMultipleEvent.setCaseId(1649258182799287L);
        submitMultipleEvents = new ArrayList<>(Collections.singletonList(submitMultipleEvent));

        updateCaseMsg = TestMessageHelper.generateUpdateCaseMsg();
        userToken = "accessToken";

        ReflectionTestUtils.setField(singleUpdateService, "ccdGatewayBaseUrl", "http://gateway");
    }

    @Test
    public void shouldSendUpdateUsingApiRole() throws IOException {
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitEvent);
        when(ccdClient.startEventForCaseAPIRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(getCcdRequest());

        singleUpdateService.sendUpdate(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient).startEventForCaseAPIRole(eq(userToken),
            eq(UtilHelper.getCaseTypeId(updateCaseMsg.getCaseTypeId())),
            eq(updateCaseMsg.getJurisdiction()), any());
        verify(ccdClient).submitEventForCase(eq(userToken), any(),
            eq(UtilHelper.getCaseTypeId(updateCaseMsg.getCaseTypeId())),
            eq(updateCaseMsg.getJurisdiction()), any(), any());
        verify(conciliationTrackService).populateConciliationTrackForJurisdiction(eq(submitEvent.getCaseData()));
    }

    @Test
    public void shouldSendPreAcceptUpdate() throws IOException {
        updateCaseMsg = TestMessageHelper.generatePreAcceptCaseMsg();
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitEvent);
        when(ccdClient.startEventForCasePreAcceptBulkSingle(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(getCcdRequest());

        singleUpdateService.sendUpdate(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient).startEventForCasePreAcceptBulkSingle(eq(userToken),
            eq(UtilHelper.getCaseTypeId(updateCaseMsg.getCaseTypeId())),
            eq(updateCaseMsg.getJurisdiction()), any());
    }

    @Test
    public void shouldSendDisposeUpdate() throws IOException {
        updateCaseMsg = TestMessageHelper.generateCloseCaseMsg();
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitEvent);
        when(ccdClient.startDisposeEventForCase(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(getCcdRequest());

        singleUpdateService.sendUpdate(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient).startDisposeEventForCase(eq(userToken),
            eq(UtilHelper.getCaseTypeId(updateCaseMsg.getCaseTypeId())),
            eq(updateCaseMsg.getJurisdiction()), any());
    }

    @Test
    public void shouldPopulateMultipleReferenceMarkupWhenMissing() throws IOException {
        submitEvent.getCaseData().setMultipleReferenceLinkMarkUp(null);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitEvent);
        when(ccdClient.startEventForCaseAPIRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(getCcdRequest());
        when(ccdClient.retrieveMultipleCasesElasticSearchWithRetries(anyString(), anyString(), anyString()))
            .thenReturn(submitMultipleEvents);

        singleUpdateService.sendUpdate(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient).retrieveMultipleCasesElasticSearchWithRetries(eq(userToken),
            eq(updateCaseMsg.getCaseTypeId()), any());
    }

    @Test
    public void shouldUseMessageMarkupAndSkipMultipleLookup() throws IOException {
        updateCaseMsg.setMultipleReferenceLinkMarkUp("<a href=\"http://example\">4150002</a>");
        submitEvent.getCaseData().setMultipleReferenceLinkMarkUp(null);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitEvent);
        when(ccdClient.startEventForCaseAPIRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(getCcdRequest());

        singleUpdateService.sendUpdate(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient, never()).retrieveMultipleCasesElasticSearchWithRetries(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldSkipMultipleLookupWhenMultipleRefMissing() throws IOException {
        updateCaseMsg.setMultipleReferenceLinkMarkUp(null);
        updateCaseMsg.setMultipleRef(null);
        submitEvent.getCaseData().setMultipleReferenceLinkMarkUp(null);
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitEvent);
        when(ccdClient.startEventForCaseAPIRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(getCcdRequest());

        singleUpdateService.sendUpdate(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient, never()).retrieveMultipleCasesElasticSearchWithRetries(anyString(), anyString(), anyString());
    }

    private CCDRequest getCcdRequest() {
        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(submitEvent.getCaseData());
        ccdRequest.setCaseDetails(caseDetails);
        return ccdRequest;
    }
}
