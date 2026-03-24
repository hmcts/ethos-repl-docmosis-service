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
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.compat.common.client.CcdClient;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleCreationServiceTest {

    @InjectMocks
    private SingleCreationService singleCreationService;
    @Mock
    private CcdClient ccdClient;

    private SubmitEvent submitEvent;
    private UpdateCaseMsg updateCaseMsg;
    private String userToken;

    @Before
    public void setUp() {
        submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("4150002/2020");
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        caseData.setPreAcceptCase(casePreAcceptType);
        submitEvent.setCaseData(caseData);
        submitEvent.setState(ACCEPTED_STATE);

        updateCaseMsg = TestMessageHelper.generateCreationSingleCaseMsg();
        userToken = "accessToken";

        ReflectionTestUtils.setField(singleCreationService, "ccdGatewayBaseUrl", "http://gateway");
    }

    @Test
    public void shouldCreateTransferredCaseWhenDestinationMissing() throws IOException {
        CCDRequest ccdRequest = new CCDRequest();
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("4450008/2022");
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        ccdRequest.setCaseDetails(caseDetails);

        when(ccdClient.startEventForCase(eq(userToken), any(), any(), any())).thenReturn(ccdRequest);
        when(ccdClient.startCaseCreationTransfer(eq(userToken), any(CaseDetails.class))).thenReturn(ccdRequest);
        when(ccdClient.submitCaseCreation(eq(userToken), any(CaseDetails.class), any())).thenReturn(submitEvent);

        singleCreationService.sendCreation(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient).retrieveCasesElasticSearch(eq(userToken), any(), any());
        verify(ccdClient).startCaseCreationTransfer(eq(userToken), any(CaseDetails.class));
        verify(ccdClient).submitCaseCreation(eq(userToken), any(CaseDetails.class), any());
        verify(ccdClient).startEventForCase(eq(userToken), any(), any(), any());
        verify(ccdClient).submitEventForCase(eq(userToken), any(CaseData.class), any(), any(), any(), any());
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    public void shouldReturnCaseCreationTransferWhenDestinationExists() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(anyString(), any(), any()))
            .thenReturn(new ArrayList<>(Collections.singletonList(submitEvent)));

        singleCreationService.sendCreation(submitEvent, userToken, updateCaseMsg);

        verify(ccdClient).retrieveCasesElasticSearch(eq(userToken), any(), any());
        verify(ccdClient).returnCaseCreationTransfer(eq(userToken), anyString(), anyString(), anyString());
        verify(ccdClient).submitEventForCase(eq(userToken), any(), anyString(), anyString(), any(), anyString());
        verifyNoMoreInteractions(ccdClient);
    }
}
