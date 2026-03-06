package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.compat.common.client.CcdClient;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.MultipleErrors;
import uk.gov.hmcts.ethos.replacement.docmosis.service.AdminUserService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleUpdateServiceTest {

    @InjectMocks
    private MultipleUpdateService multipleUpdateService;
    @Mock
    private CcdClient ccdClient;
    @Mock
    private AdminUserService adminUserService;

    private List<SubmitMultipleEvent> submitMultipleEvents;
    private SubmitMultipleEvent submitMultipleEvent;
    private UpdateCaseMsg updateCaseMsg;
    private String userToken;

    @Before
    public void setUp() {
        submitMultipleEvent = new SubmitMultipleEvent();
        MultipleData multipleData = new MultipleData();
        multipleData.setMultipleReference("4100001");
        submitMultipleEvent.setCaseData(multipleData);
        submitMultipleEvents = new ArrayList<>(Collections.singletonList(submitMultipleEvent));
        updateCaseMsg = TestMessageHelper.generateUpdateCaseMsg();
        userToken = "Token";

        ReflectionTestUtils.setField(multipleUpdateService, "ccdGatewayBaseUrl", "http://gateway");
    }

    @Test
    public void shouldSendUpdateToMultiple() throws IOException {
        when(adminUserService.getAdminUserToken()).thenReturn(userToken);
        when(ccdClient.retrieveMultipleCasesElasticSearchWithRetries(anyString(), anyString(), anyString()))
            .thenReturn(submitMultipleEvents);
        when(ccdClient.submitMultipleEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitMultipleEvent);

        multipleUpdateService.sendUpdateToMultipleLogic(updateCaseMsg, new ArrayList<>());

        verifyBaseCalls(updateCaseMsg);
    }

    @Test
    public void shouldHandleEmptyElasticSearchResults() throws IOException {
        when(adminUserService.getAdminUserToken()).thenReturn(userToken);
        when(ccdClient.retrieveMultipleCasesElasticSearchWithRetries(anyString(), anyString(), anyString()))
            .thenReturn(new ArrayList<>());

        multipleUpdateService.sendUpdateToMultipleLogic(updateCaseMsg, new ArrayList<>());

        verify(ccdClient).retrieveMultipleCasesElasticSearchWithRetries(eq(userToken),
            eq(updateCaseMsg.getCaseTypeId()), eq(updateCaseMsg.getMultipleRef()));
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    public void shouldSkipWhenUpdateCaseMsgNull() throws IOException {
        multipleUpdateService.sendUpdateToMultipleLogic(null, new ArrayList<>());
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    public void shouldSkipWhenErrorsExistForCreationTransfer() throws IOException {
        when(adminUserService.getAdminUserToken()).thenReturn(userToken);
        when(ccdClient.retrieveMultipleCasesElasticSearchWithRetries(anyString(), anyString(), anyString()))
            .thenReturn(submitMultipleEvents);

        UpdateCaseMsg creationMsg = TestMessageHelper.generateCreationSingleCaseMsg();
        MultipleErrors multipleErrors = new MultipleErrors();
        multipleErrors.setDescription("Some error");

        multipleUpdateService.sendUpdateToMultipleLogic(creationMsg,
            new ArrayList<>(Collections.singletonList(multipleErrors)));

        verify(ccdClient).retrieveMultipleCasesElasticSearchWithRetries(eq(userToken),
            eq(creationMsg.getCaseTypeId()), eq(creationMsg.getMultipleRef()));
    }

    @Test
    public void shouldCreateNewMultipleAndSetLinkMarkup() throws IOException {
        when(adminUserService.getAdminUserToken()).thenReturn(userToken);
        when(ccdClient.retrieveMultipleCasesElasticSearchWithRetries(anyString(), anyString(), anyString()))
            .thenReturn(submitMultipleEvents);
        when(ccdClient.startCaseMultipleCreation(anyString(), anyString(), anyString()))
            .thenReturn(mock(CCDRequest.class));
        when(ccdClient.submitMultipleEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
            .thenReturn(submitMultipleEvent);

        SubmitMultipleEvent newSubmitMultipleEvent = new SubmitMultipleEvent();
        MultipleData multipleData = new MultipleData();
        multipleData.setMultipleReference("41005567");
        newSubmitMultipleEvent.setCaseData(multipleData);
        newSubmitMultipleEvent.setCaseId(123456789L);

        when(ccdClient.submitMultipleCreation(anyString(), any(), anyString(), anyString(), any()))
            .thenReturn(newSubmitMultipleEvent);

        UpdateCaseMsg creationMsg = TestMessageHelper.generateCreationSingleCaseMsg();
        creationMsg.setMultipleReferenceLinkMarkUp(null);

        multipleUpdateService.sendUpdateToMultipleLogic(creationMsg, new ArrayList<>());

        assertNotNull(creationMsg.getMultipleReferenceLinkMarkUp());
        verify(ccdClient).retrieveMultipleCasesElasticSearchWithRetries(eq(userToken),
            eq(creationMsg.getCaseTypeId()), eq(creationMsg.getMultipleRef()));
        verify(ccdClient).startCaseMultipleCreation(anyString(), anyString(), anyString());
        verify(ccdClient).submitMultipleCreation(anyString(), any(), anyString(), anyString(), any());
        verify(ccdClient).startBulkAmendEventForCase(eq(userToken),
            eq(creationMsg.getCaseTypeId()), eq(creationMsg.getJurisdiction()), any());
        verify(ccdClient).submitMultipleEventForCase(eq(userToken), any(), eq(creationMsg.getCaseTypeId()),
            eq(creationMsg.getJurisdiction()), any(), any());
    }

    private void verifyBaseCalls(UpdateCaseMsg message) throws IOException {
        verify(ccdClient).retrieveMultipleCasesElasticSearchWithRetries(eq(userToken),
            eq(message.getCaseTypeId()), eq(message.getMultipleRef()));
        verify(ccdClient).startBulkAmendEventForCase(eq(userToken),
            eq(message.getCaseTypeId()), eq(message.getJurisdiction()), any());
        verify(ccdClient).submitMultipleEventForCase(eq(userToken), any(), eq(message.getCaseTypeId()),
            eq(message.getJurisdiction()), any(), any());
        verifyNoMoreInteractions(ccdClient);
    }
}
