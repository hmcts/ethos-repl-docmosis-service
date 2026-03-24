package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.compat.common.client.CcdClient;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleTransferServiceTest {

    @InjectMocks
    private SingleTransferService singleTransferService;
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
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        updateCaseMsg = TestMessageHelper.generateCreationSingleCaseMsg();
        userToken = "accessToken";
    }

    @Test
    public void shouldSendTransferredCase() throws IOException {
        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(submitEvent.getCaseData());
        ccdRequest.setCaseDetails(caseDetails);
        when(ccdClient.startCaseTransfer(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(ccdRequest);

        singleTransferService.sendTransferred(submitEvent, userToken, updateCaseMsg);

        assertEquals("Transferred to Manchester", submitEvent.getCaseData().getLinkedCaseCT());

        verify(ccdClient).startCaseTransfer(eq(userToken), any(), any(), any());
        verify(ccdClient).submitEventForCase(eq(userToken), any(), anyString(), anyString(), any(), anyString());
        verifyNoMoreInteractions(ccdClient);
    }
}
