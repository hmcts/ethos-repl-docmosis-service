package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.compat.common.client.CcdClient;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.service.AdminUserService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleReadingServiceTest {

    @InjectMocks
    private SingleReadingService singleReadingService;
    @Mock
    private CcdClient ccdClient;
    @Mock
    private AdminUserService adminUserService;
    @Mock
    private SingleUpdateService singleUpdateService;
    @Mock
    private SingleCreationService singleCreationService;
    @Mock
    private SingleTransferService singleTransferService;

    private List<SubmitEvent> submitEvents;
    private UpdateCaseMsg updateCaseMsg;
    private String userToken;

    @Before
    public void setUp() {
        SubmitEvent submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("4150002/2020");
        submitEvent.setCaseData(caseData);

        submitEvents = new ArrayList<>(Collections.singletonList(submitEvent));
        updateCaseMsg = TestMessageHelper.generateUpdateCaseMsg();
        userToken = "Token";
    }

    @Test
    public void shouldSendUpdateToSingle() throws IOException {
        when(adminUserService.getAdminUserToken()).thenReturn(userToken);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(submitEvents);

        singleReadingService.sendUpdateToSingleLogic(updateCaseMsg);

        verify(singleUpdateService).sendUpdate(eq(submitEvents.get(0)), eq(userToken), eq(updateCaseMsg));
        verifyNoMoreInteractions(singleUpdateService);
    }

    @Test
    public void shouldSendCreationAndTransfer() throws IOException {
        updateCaseMsg = TestMessageHelper.generateCreationSingleCaseMsg();
        when(adminUserService.getAdminUserToken()).thenReturn(userToken);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(submitEvents);

        singleReadingService.sendUpdateToSingleLogic(updateCaseMsg);

        verify(singleCreationService).sendCreation(eq(submitEvents.get(0)), eq(userToken), eq(updateCaseMsg));
        verify(singleTransferService).sendTransferred(eq(submitEvents.get(0)), eq(userToken), eq(updateCaseMsg));
    }

    @Test
    public void shouldSkipWhenNoCasesReturned() throws IOException {
        when(adminUserService.getAdminUserToken()).thenReturn(userToken);
        when(ccdClient.retrieveCasesElasticSearch(anyString(), anyString(), anyList())).thenReturn(null);

        singleReadingService.sendUpdateToSingleLogic(updateCaseMsg);

        verifyNoMoreInteractions(singleCreationService);
        verifyNoMoreInteractions(singleUpdateService);
    }
}
