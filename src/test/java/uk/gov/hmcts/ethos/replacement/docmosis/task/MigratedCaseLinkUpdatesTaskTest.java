package uk.gov.hmcts.ethos.replacement.docmosis.task;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.service.AdminUserService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.FeatureToggleService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.exceptions.CaseDuplicateSearchException;
import uk.gov.hmcts.ethos.replacement.docmosis.tasks.MigratedCaseLinkUpdatesTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class MigratedCaseLinkUpdatesTaskTest {
    @Mock
    private FeatureToggleService featureToggleService;
    @Mock
    private AdminUserService adminUserService;
    @Mock
    private CcdClient ccdClient;
    @InjectMocks
    private MigratedCaseLinkUpdatesTask migratedCaseLinkUpdatesTask;
    private static final String ADMIN_TOKEN = "adminToken";
    private static final String ETHOS_REFERENCE = "testEthosRef";
    private static final String TARGET_CASE_TYPE_ID = "TargetCaseType";
    private static final String SOURCE_CASE_TYPE_ID = "SourceCaseType";
    private static final String EVENT_ID = "migrateCaseLinkDetails";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(migratedCaseLinkUpdatesTask, "caseLinkCaseTypeIdString",
                "type1,type2");
    }

    @Test
    void testUpdateTransferredCaseLinks_FeatureDisabled() {
        when(featureToggleService.isUpdateTransferredCaseLinksEnabled()).thenReturn(false);

        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();

        verify(featureToggleService).isUpdateTransferredCaseLinksEnabled();
        verifyNoMoreInteractions(featureToggleService, adminUserService, ccdClient);
    }

    @Test
    void testUpdateTransferredCaseLinks_NoTransferredCases() throws Exception {
        when(featureToggleService.isUpdateTransferredCaseLinksEnabled()).thenReturn(true);
        when(adminUserService.getAdminUserToken()).thenReturn(ADMIN_TOKEN);
        when(ccdClient.buildAndGetElasticSearchRequest(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();

        verify(featureToggleService).isUpdateTransferredCaseLinksEnabled();
        verify(adminUserService).getAdminUserToken();
        verify(ccdClient, times(2))
                .buildAndGetElasticSearchRequest(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    void testUpdateTransferredCaseLinks_WithTransferredCases() throws Exception {
        when(featureToggleService.isUpdateTransferredCaseLinksEnabled()).thenReturn(true);
        when(adminUserService.getAdminUserToken()).thenReturn(ADMIN_TOKEN);

        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("caseRef1");
        caseData.setClaimant("claimant1");
        caseData.setRespondent("respondent1");
        caseData.setReceiptDate("2021-01-01");
        caseData.setFeeGroupReference("feeGroup1");
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);

        when(ccdClient.buildAndGetElasticSearchRequest(any(), any(), any()))
                .thenReturn(Collections.singletonList(submitEvent));

        List<SubmitEvent> duplicates = new ArrayList<>();
        duplicates.add(submitEvent);
        duplicates.add(submitEvent);
        List<Pair<String, List<SubmitEvent>>> coll = Collections.singletonList(Pair.of("type1", duplicates));
        when(migratedCaseLinkUpdatesTask.findCaseByEthosReference(ADMIN_TOKEN, "testEthosRef"))
                .thenReturn(coll);

        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();

        verify(featureToggleService, times(3)).isUpdateTransferredCaseLinksEnabled();
        verify(adminUserService).getAdminUserToken();
        verify(ccdClient, times(43))
                .buildAndGetElasticSearchRequest(any(), any(), any());
    }

    @Test
    void testUpdateTransferredCaseLinks_WithTransferredCases_Null_Casedata_In_Pairs() throws Exception {
        when(featureToggleService.isUpdateTransferredCaseLinksEnabled()).thenReturn(true);
        when(adminUserService.getAdminUserToken()).thenReturn(ADMIN_TOKEN);

        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("caseRef1");
        caseData.setClaimant("claimant1");
        caseData.setRespondent("respondent1");
        caseData.setReceiptDate("2021-01-01");
        caseData.setFeeGroupReference("feeGroup1");
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);

        when(ccdClient.buildAndGetElasticSearchRequest(any(), any(), any()))
                .thenReturn(Collections.singletonList(submitEvent));

        List<SubmitEvent> duplicates = new ArrayList<>();
        SubmitEvent submitEventNull = new SubmitEvent();
        duplicates.add(submitEvent);
        duplicates.add(submitEventNull);

        List<Pair<String, List<SubmitEvent>>> pairsList = new ArrayList<>();
        pairsList.add(Pair.of("type1", List.of(duplicates.get(0))));
        pairsList.add(Pair.of("type1", List.of(duplicates.get(1))));

        when(migratedCaseLinkUpdatesTask.findCaseByEthosReference(ADMIN_TOKEN, "testEthosRef"))
                .thenReturn(pairsList);

        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();
        assertEquals(2, pairsList.size());
        verify(ccdClient, times(43))
                .buildAndGetElasticSearchRequest(any(), any(), any());
        verify(ccdClient, times(0)).startEventForCase(
                anyString(), anyString(), anyString(), anyString(), anyString());
        verify(ccdClient, times(0)).submitEventForCase(
                anyString(), any(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void testUpdateTransferredCaseLinks_WithTransferredCasesAndDuplicates() throws Exception {
        when(featureToggleService.isUpdateTransferredCaseLinksEnabled()).thenReturn(true);
        when(adminUserService.getAdminUserToken()).thenReturn("admin-token");
        SubmitEvent transferredCase = new SubmitEvent();
        transferredCase.setCaseData(new CaseData());
        transferredCase.getCaseData().setEthosCaseReference("ETHOS1231");
        transferredCase.getCaseData().setEthosCaseReference("caseRef11");
        transferredCase.getCaseData().setClaimant("claimant11");
        transferredCase.getCaseData().setRespondent("respondent11");
        transferredCase.getCaseData().setReceiptDate("2021-11-01");
        transferredCase.getCaseData().setFeeGroupReference("feeGroup11");

        List<SubmitEvent> duplicates = mock(List.class);
        duplicates.add(transferredCase);
        duplicates.add(transferredCase);
        when(ccdClient.buildAndGetElasticSearchRequest(anyString(), anyString(), anyString()))
                .thenReturn(duplicates);

        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();

        verify(ccdClient, times(2)).buildAndGetElasticSearchRequest(
                anyString(), anyString(), anyString());
        verify(ccdClient, times(0)).startEventForCase(
                anyString(), anyString(), anyString(), anyString(), anyString());
        verify(ccdClient, times(0)).submitEventForCase(
                anyString(), any(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void testUpdateTransferredCaseLinks_WithTransferredCasesAndNotDuplicates() throws Exception {
        when(featureToggleService.isUpdateTransferredCaseLinksEnabled()).thenReturn(true);
        when(adminUserService.getAdminUserToken()).thenReturn("admin-token");
        SubmitEvent transferredCase1 = new SubmitEvent();
        transferredCase1.setCaseData(new CaseData());
        transferredCase1.getCaseData().setEthosCaseReference("ETHOS1231");
        transferredCase1.getCaseData().setEthosCaseReference("caseRef11");
        transferredCase1.getCaseData().setClaimant("claimant11");
        transferredCase1.getCaseData().setRespondent("respondent11");
        transferredCase1.getCaseData().setReceiptDate("2021-11-01");
        transferredCase1.getCaseData().setFeeGroupReference("feeGroup11");

        SubmitEvent transferredCase2 = new SubmitEvent();
        transferredCase2.setCaseData(new CaseData());
        transferredCase2.getCaseData().setEthosCaseReference("ETHOS1231");
        transferredCase2.getCaseData().setEthosCaseReference("caseRef435");
        transferredCase2.getCaseData().setClaimant("claimant345");
        transferredCase2.getCaseData().setRespondent("respondent345");
        transferredCase2.getCaseData().setReceiptDate("2022-12-02");
        transferredCase2.getCaseData().setFeeGroupReference("feeGroup345");
        when(ccdClient.buildAndGetElasticSearchRequest(anyString(), anyString(), anyString()))
                .thenReturn(List.of(transferredCase1, transferredCase2));

        List<SubmitEvent> notMatchedDuplicates = new ArrayList<>();
        notMatchedDuplicates.add(transferredCase1);
        notMatchedDuplicates.add(transferredCase2);
        when(migratedCaseLinkUpdatesTask.findCaseByEthosReference(ADMIN_TOKEN, "ETHOS1231"))
                .thenReturn(List.of(Pair.of("type1", notMatchedDuplicates)));

        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();
        // 71 invocations of buildAndGetElasticSearchRequest using ccdClient
        // because the method calls were made covering two case types(type1,type2), and two transferred cases during
        // run for each case type
        verify(ccdClient, times(71)).buildAndGetElasticSearchRequest(
                anyString(), anyString(), anyString());
        verify(ccdClient, times(0)).startEventForCase(
                anyString(), anyString(), anyString(), anyString(), anyString());
        verify(ccdClient, times(0)).submitEventForCase(
                anyString(), any(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void shouldHandleExceptionDuringProcessing() throws Exception {
        when(featureToggleService.isUpdateTransferredCaseLinksEnabled()).thenReturn(true);
        when(adminUserService.getAdminUserToken()).thenReturn(ADMIN_TOKEN);
        when(ccdClient.buildAndGetElasticSearchRequest(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Test Exception"));

        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();

        verify(featureToggleService).isUpdateTransferredCaseLinksEnabled();
        verify(adminUserService).getAdminUserToken();
        verify(ccdClient, times(2)).buildAndGetElasticSearchRequest(
                anyString(), anyString(), anyString());
        verify(ccdClient, never()).startEventForCase(anyString(), anyString(), anyString(),
                anyString(), anyString());
        verify(ccdClient, never()).submitEventForCase(anyString(), any(), anyString(),
                anyString(), any(), anyString());
    }

    @Test
    void findCaseByEthosReference_ShouldReturnListOfPairs_WhenDuplicateCasesAreFound() throws IOException {
        List<SubmitEvent> duplicateCases = new ArrayList<>();
        duplicateCases.add(new SubmitEvent());

        when(ccdClient.buildAndGetElasticSearchRequest(
                anyString(), anyString(), anyString())).thenReturn(duplicateCases);

        List<Pair<String, List<SubmitEvent>>> result = migratedCaseLinkUpdatesTask.findCaseByEthosReference(
                ADMIN_TOKEN, ETHOS_REFERENCE);

        assertNotNull(result);
        assertEquals(14, result.size());
        verify(ccdClient, times(14)).buildAndGetElasticSearchRequest(
                anyString(), anyString(), anyString());
    }

    @Test
    void findCaseByEthosReference_ShouldReturnEmptyList_WhenNoDuplicateCasesAreFound() throws IOException {
        List<SubmitEvent> noDuplicates = new ArrayList<>();
        when(ccdClient.buildAndGetElasticSearchRequest(anyString(), anyString(), anyString())).thenReturn(noDuplicates);
        List<Pair<String, List<SubmitEvent>>> result = migratedCaseLinkUpdatesTask.findCaseByEthosReference(
                ADMIN_TOKEN, ETHOS_REFERENCE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ccdClient, times(14)).buildAndGetElasticSearchRequest(
                anyString(), anyString(), anyString());
    }

    @Test
    void findCaseByEthosReference_ShouldThrowException_WhenIOExceptionOccurs() throws IOException {
        when(ccdClient.buildAndGetElasticSearchRequest(anyString(), anyString(), anyString()))
                .thenThrow(new IOException("Test Exception"));

        CaseDuplicateSearchException exception = assertThrows(
                CaseDuplicateSearchException.class,
                () -> migratedCaseLinkUpdatesTask.findCaseByEthosReference(ADMIN_TOKEN, ETHOS_REFERENCE)
        );

        assertEquals("Test Exception", exception.getMessage());
        verify(ccdClient, times(1)).buildAndGetElasticSearchRequest(
                anyString(), anyString(), anyString());
    }

    @Test
    void triggerEventForCase_ShouldTriggerEventSuccessfully() throws Exception {
        CCDRequest ccdRequest = createCCDRequest();
        SubmitEvent targetSubmitEvent = createSubmitEvent(22L, "TargetCcdId");
        SubmitEvent expectedSubmitEvent = createSubmitEvent(12L, "TargetCcdId");
        when(ccdClient.startEventForCase(ADMIN_TOKEN, TARGET_CASE_TYPE_ID,
                "EMPLOYMENT", String.valueOf(targetSubmitEvent.getCaseId()), EVENT_ID))
                .thenReturn(ccdRequest);
        when(ccdClient.submitEventForCase(ADMIN_TOKEN, ccdRequest.getCaseDetails().getCaseData(),
                ccdRequest.getCaseDetails().getCaseTypeId(), "EMPLOYMENT",
                ccdRequest, ccdRequest.getCaseDetails().getCaseId()))
                .thenReturn(expectedSubmitEvent);
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        when(ccdClient.submitEventForCase(
                ADMIN_TOKEN, caseDetails.getCaseData(),
                caseDetails.getCaseTypeId(), "EMPLOYMENT",
                ccdRequest, caseDetails.getCaseId()))
                .thenReturn(targetSubmitEvent);

        SubmitEvent sourceSubmitEvent = createSubmitEvent(12L, "SourceCcdId");
        List<SubmitEvent> duplicates;
        duplicates = new ArrayList<>();
        duplicates.add(sourceSubmitEvent);
        duplicates.add(targetSubmitEvent);
        expectedSubmitEvent.getCaseData().setTransferredCaseLinkSourceCaseId("12");
        expectedSubmitEvent.getCaseData().setTransferredCaseLinkSourceCaseTypeId("SourceCcdId");

        when(migratedCaseLinkUpdatesTask.findCaseByEthosReference(ADMIN_TOKEN, "12"))
                .thenReturn(List.of(Pair.of("type1", duplicates)));

        migratedCaseLinkUpdatesTask.triggerEventForCase(ADMIN_TOKEN, targetSubmitEvent, sourceSubmitEvent,
                TARGET_CASE_TYPE_ID, SOURCE_CASE_TYPE_ID);
        assertEquals("12", expectedSubmitEvent.getCaseData().getTransferredCaseLinkSourceCaseId());
        verify(ccdClient).startEventForCase(ADMIN_TOKEN, TARGET_CASE_TYPE_ID,
                "EMPLOYMENT", String.valueOf(targetSubmitEvent.getCaseId()), EVENT_ID);
        assertEquals(sourceSubmitEvent.getCaseData().getEthosCaseReference(),
                targetSubmitEvent.getCaseData().getEthosCaseReference());
        assertEquals(sourceSubmitEvent.getCaseData().getClaimant(),
                targetSubmitEvent.getCaseData().getClaimant());
        assertEquals(sourceSubmitEvent.getCaseData().getFeeGroupReference(),
                targetSubmitEvent.getCaseData().getFeeGroupReference());
        assertEquals(sourceSubmitEvent.getCaseData().getReceiptDate(),
                targetSubmitEvent.getCaseData().getReceiptDate());
    }

    @Test
    void triggerEventForCase_ShouldNotTriggerEvent_WhenListOfPairsSizeNotTwo() throws IOException {
        SubmitEvent sourceSubmitEvent = createSubmitEvent(12L, "SourceCcdId");
        List<SubmitEvent> duplicates;
        duplicates = new ArrayList<>();
        duplicates.add(sourceSubmitEvent);

        when(mock(MigratedCaseLinkUpdatesTask.class).findCaseByEthosReference(anyString(), anyString()))
                .thenReturn(List.of(Pair.of("type1", List.of(duplicates.get(0)))));
        migratedCaseLinkUpdatesTask.updateTransferredCaseLinks();

        verify(ccdClient, never())
                .startEventForCase(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(ccdClient, never())
                .submitEventForCase(anyString(), any(), any(), any(), any(), anyString());
    }

    @Test
    void triggerEventForCase_ShouldNotTriggerEvent_WhenSourceCaseNotFound() throws IOException {
        SubmitEvent targetSubmitEvent = createSubmitEvent(22L, "TargetCcdId");
        migratedCaseLinkUpdatesTask.triggerEventForCase(ADMIN_TOKEN, targetSubmitEvent, null,
                TARGET_CASE_TYPE_ID, SOURCE_CASE_TYPE_ID);

        verify(ccdClient, never()).startEventForCase(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(ccdClient, never()).submitEventForCase(anyString(), any(), any(), any(), any(), anyString());
    }

    @Test
    void triggerEventForCase_ShouldNotTriggerEvent_WhenSourceCaseDataIsNull() throws Exception {
        SubmitEvent sourceSubmitEvent = createSubmitEvent(12L, "SourceCcdId");
        sourceSubmitEvent.setCaseData(null);
        SubmitEvent targetSubmitEvent = createSubmitEvent(22L, "TargetCcdId");
        migratedCaseLinkUpdatesTask.triggerEventForCase(ADMIN_TOKEN, targetSubmitEvent, sourceSubmitEvent,
                TARGET_CASE_TYPE_ID, SOURCE_CASE_TYPE_ID);

        verify(ccdClient, never()).startEventForCase(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(ccdClient, never()).submitEventForCase(anyString(), any(), any(), any(), any(), anyString());
    }

    @Test
    void haveSameCheckedFieldValues_allFieldsMatch_returnsTrue() {
        CaseData sourceCaseData = new CaseData();
        sourceCaseData.setEthosCaseReference("ETHOS123");
        sourceCaseData.setClaimant("Claimant1");
        sourceCaseData.setFeeGroupReference("FeeGroup1");
        sourceCaseData.setReceiptDate("2023-08-28");
        SubmitEvent sourceSubmitEvent = mock(SubmitEvent.class);
        sourceSubmitEvent.setCaseData(sourceCaseData);

        CaseData targetCaseData = new CaseData();
        targetCaseData.setEthosCaseReference("ETHOS123");
        targetCaseData.setClaimant("Claimant1");
        targetCaseData.setFeeGroupReference("FeeGroup1");
        targetCaseData.setReceiptDate("2023-08-28");
        SubmitEvent targetSubmitEvent = mock(SubmitEvent.class);
        targetSubmitEvent.setCaseData(targetCaseData);

        when(sourceSubmitEvent.getCaseData()).thenReturn(sourceCaseData);
        when(targetSubmitEvent.getCaseData()).thenReturn(targetCaseData);

        boolean result = migratedCaseLinkUpdatesTask.haveSameCheckedFieldValues(targetSubmitEvent, sourceSubmitEvent);

        assertTrue(result);
    }

    @Test
    void haveSameCheckedFieldValues_bothCaseDataNull_returnsFalse() {
        SubmitEvent sourceSubmitEvent = mock(SubmitEvent.class);
        when(sourceSubmitEvent.getCaseData()).thenReturn(null);
        SubmitEvent targetSubmitEvent = mock(SubmitEvent.class);
        when(targetSubmitEvent.getCaseData()).thenReturn(null);

        boolean result = migratedCaseLinkUpdatesTask.haveSameCheckedFieldValues(targetSubmitEvent, sourceSubmitEvent);

        assertFalse(result);
    }

    @Test
    void haveSameCheckedFieldValues_oneCaseDataNull_returnsFalse() {
        SubmitEvent sourceSubmitEvent = mock(SubmitEvent.class);
        when(sourceSubmitEvent.getCaseData()).thenReturn(null);
        SubmitEvent targetSubmitEvent = mock(SubmitEvent.class);

        boolean result = migratedCaseLinkUpdatesTask.haveSameCheckedFieldValues(targetSubmitEvent, sourceSubmitEvent);

        assertFalse(result);
    }

    @Test
    void haveSameCheckedFieldValues_fieldsDoNotMatch_returnsFalse() {
        CaseData sourceCaseData = new CaseData();
        sourceCaseData.setEthosCaseReference("ETHOS123");
        sourceCaseData.setClaimant("Claimant1");
        sourceCaseData.setFeeGroupReference("FeeGroup1");
        sourceCaseData.setReceiptDate("2023-08-28");
        SubmitEvent sourceSubmitEvent = mock(SubmitEvent.class);
        sourceSubmitEvent.setCaseData(sourceCaseData);

        CaseData targetCaseData = new CaseData();
        targetCaseData.setEthosCaseReference("ETHOS654");
        targetCaseData.setClaimant("Claimant2");
        targetCaseData.setFeeGroupReference("FeeGroup2");
        targetCaseData.setReceiptDate("2023-08-29");
        SubmitEvent targetSubmitEvent = mock(SubmitEvent.class);
        targetSubmitEvent.setCaseData(targetCaseData);

        when(sourceSubmitEvent.getCaseData()).thenReturn(sourceCaseData);
        when(targetSubmitEvent.getCaseData()).thenReturn(targetCaseData);

        boolean result = migratedCaseLinkUpdatesTask.haveSameCheckedFieldValues(targetSubmitEvent, sourceSubmitEvent);

        assertFalse(result);
    }

    @Test
    void testGetTargetSubmitEventFromPair_ListSizeNotTwo() {
        List<Pair<String, List<SubmitEvent>>> listOfPairs = List.of(
                Pair.of("caseTypeId1", List.of(new SubmitEvent()))
        );

        SubmitEvent result = migratedCaseLinkUpdatesTask.getTargetSubmitEventFromPair(listOfPairs,
                "caseTypeId1");

        assertNull(result, "Expected null when listOfPairs size is not TWO.");
    }

    @Test
    void testGetTargetSubmitEventFromPair_NoMatchingCaseTypeId() {
        List<Pair<String, List<SubmitEvent>>> listOfPairs = List.of(
                Pair.of("caseTypeId1", List.of(new SubmitEvent())),
                Pair.of("caseTypeId2", List.of(new SubmitEvent()))
        );

        SubmitEvent result = migratedCaseLinkUpdatesTask.getTargetSubmitEventFromPair(listOfPairs,
                "nonExistentCaseTypeId");

        assertNull(result, "Expected null when no matching caseTypeId is found.");
    }

    @Test
    void testGetTargetSubmitEventFromPair_MatchingCaseTypeId() {
        SubmitEvent expectedEvent = new SubmitEvent();
        List<Pair<String, List<SubmitEvent>>> listOfPairs = List.of(
                Pair.of("caseTypeId1", List.of(expectedEvent)),
                Pair.of("caseTypeId2", List.of(new SubmitEvent()))
        );

        SubmitEvent result = migratedCaseLinkUpdatesTask.getTargetSubmitEventFromPair(listOfPairs,
                "caseTypeId1");

        assertEquals(expectedEvent, result, "Expected the SubmitEvent matching the given caseTypeId.");
    }

    @Test
    void testGetTargetSubmitEventFromPair_EmptySubmitEventList() {
        List<Pair<String, List<SubmitEvent>>> listOfPairs = List.of(
                Pair.of("caseTypeId1", List.of()),
                Pair.of("caseTypeId2", List.of(new SubmitEvent()))
        );

        SubmitEvent result = migratedCaseLinkUpdatesTask.getTargetSubmitEventFromPair(listOfPairs,
                "caseTypeId1");

        assertNull(result, "Expected null when the SubmitEvent list is empty.");
    }

    @Test
    void testGetTargetSubmitEventFromPair_NullPairReturned() {
        List<Pair<String, List<SubmitEvent>>> listOfPairs = List.of(
                Pair.of("caseTypeId1", List.of(new SubmitEvent())),
                Pair.of("caseTypeId2", List.of(new SubmitEvent()))
        );

        SubmitEvent result = migratedCaseLinkUpdatesTask.getTargetSubmitEventFromPair(listOfPairs,
                "nonExistentCaseTypeId");

        assertNull(result, "Expected null when no matching caseTypeId is found.");
    }

    @Test
    void testGetSourceSubmitEventFromPair__EmptySourceSubmitEventList() {
        List<Pair<String, List<SubmitEvent>>> listOfPairs = List.of(
                Pair.of("caseTypeId1", List.of()),
                Pair.of("caseTypeId2", List.of(new SubmitEvent()))
        );

        SubmitEvent result = migratedCaseLinkUpdatesTask.getSourceSubmitEventFromPair(listOfPairs,
                "caseTypeId2");

        assertNull(result, "Expected null when the SubmitEvent list is empty.");
    }

    @Test
    void testGetSourceSubmitEventFromPair_MatchingCaseTypeId() {
        SubmitEvent expectedEvent = new SubmitEvent();
        List<Pair<String, List<SubmitEvent>>> listOfPairs = List.of(
                Pair.of("caseTypeId1", List.of(expectedEvent)),
                Pair.of("caseTypeId2", List.of(new SubmitEvent()))
        );

        SubmitEvent result = migratedCaseLinkUpdatesTask.getSourceSubmitEventFromPair(listOfPairs,
                "caseTypeId2");

        assertEquals(expectedEvent, result,
                "Expected the Source SubmitEvent not matching the current/given caseTypeId.");
    }

    private SubmitEvent createSubmitEvent(Long caseId, String ccdId) {
        CaseData caseData = new CaseData();
        caseData.setCcdID(ccdId);
        caseData.setTransferredCaseLinkSourceCaseId(String.valueOf(caseId));
        caseData.setTransferredCaseLinkSourceCaseTypeId("SourceCaseTypeId");
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setCaseId(caseId);
        return submitEvent;
    }

    private CCDRequest createCCDRequest() {
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        caseDetails.setCaseData(caseData);
        CCDRequest ccdRequest = new CCDRequest();
        ccdRequest.setCaseDetails(caseDetails);
        return ccdRequest;
    }
}

