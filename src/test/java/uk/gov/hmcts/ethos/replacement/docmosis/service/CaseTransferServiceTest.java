package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.EccCounterClaimTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.EccCounterClaimType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BFHelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseTransferServiceTest {

    @InjectMocks
    private CaseTransferService caseTransferService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;
    private String authToken;

    @Mock
    private PersistentQHelperService persistentQHelperService;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        CaseData caseData = MultipleUtil.getCaseData("2123456/2020");
        caseData.setCaseRefNumberCount("2");
        caseData.setPositionTypeCT("PositionTypeCT");
        DynamicFixedListType officeCT = new DynamicFixedListType();
        DynamicValueType valueType = new DynamicValueType();
        valueType.setCode(LEEDS_CASE_TYPE_ID);
        officeCT.setValue(valueType);
        caseData.setOfficeCT(officeCT);
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("Manchester");
        caseDetails.setJurisdiction("Employment");
        caseDetails.setState(ACCEPTED_STATE);
        ccdRequest.setCaseDetails(caseDetails);
        submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setCaseId(12345);
        authToken = "authToken";
    }

    @Test
    public void createCaseTransfer() {
        var errors = caseTransferService.createCaseTransfer(ccdRequest.getCaseDetails(), authToken);

        assertTrue(errors.isEmpty());
        assertEquals("PositionTypeCT", ccdRequest.getCaseDetails().getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID,
                ccdRequest.getCaseDetails().getCaseData().getLinkedCaseCT());
    }

    @Test
    public void createCaseTransferECC() throws IOException {
        CaseData caseData = MultipleUtil.getCaseData("3434232323");
        caseData.setCaseRefNumberCount("2");
        DynamicFixedListType officeCT = new DynamicFixedListType();
        DynamicValueType valueType = new DynamicValueType();
        valueType.setCode(LEEDS_CASE_TYPE_ID);
        officeCT.setValue(valueType);
        caseData.setOfficeCT(officeCT);
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseId(12345);
        submitEvent.getCaseData().setReasonForCT("New Reason");
        ccdRequest.getCaseDetails().getCaseData().setCounterClaim("3434232323");
        EccCounterClaimTypeItem item = new EccCounterClaimTypeItem();
        EccCounterClaimType type = new EccCounterClaimType();
        type.setCounterClaim("2123456/2020");
        item.setId(UUID.randomUUID().toString());
        item.setValue(type);
        caseData.setEccCases(List.of(item));
        submitEvent1.setCaseData(caseData);
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        List<SubmitEvent> submitEventList1 = new ArrayList<>(Collections.singletonList(submitEvent1));
        when(ccdClient.retrieveCasesElasticSearch(authToken, ccdRequest.getCaseDetails().getCaseTypeId(),
                List.of("3434232323"))).thenReturn(submitEventList1);
        when(ccdClient.retrieveCasesElasticSearch(authToken, ccdRequest.getCaseDetails().getCaseTypeId(),
                List.of("2123456/2020"))).thenReturn(submitEventList);
        when(ccdClient.startEventForCase(authToken, "Manchester", "Employment", "12345")).thenReturn(ccdRequest);

        var errors = caseTransferService.createCaseTransfer(ccdRequest.getCaseDetails(), authToken);

        assertTrue(errors.isEmpty());
        assertEquals("PositionTypeCT", submitEvent.getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID, submitEvent.getCaseData().getLinkedCaseCT());
        assertEquals("PositionTypeCT", submitEvent1.getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID, submitEvent1.getCaseData().getLinkedCaseCT());
    }

    @Test
    public void createCaseTransferMultiples() {
        ccdRequest.getCaseDetails().getCaseData().setStateAPI(MULTIPLE);

        var errors = caseTransferService.createCaseTransfer(ccdRequest.getCaseDetails(), authToken);

        assertTrue(errors.isEmpty());
        assertEquals("PositionTypeCT", ccdRequest.getCaseDetails().getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID,
                ccdRequest.getCaseDetails().getCaseData().getLinkedCaseCT());
    }

    @Test
    public void createCaseTransferBfNotCleared() {
        ccdRequest.getCaseDetails().getCaseData().setBfActions(BFHelperTest.generateBFActionTypeItems());
        ccdRequest.getCaseDetails().getCaseData().getBfActions().getFirst().getValue().setCleared(null);

        var errors = caseTransferService.createCaseTransfer(ccdRequest.getCaseDetails(), authToken);

        var expectedBfActionsError = String.format(CaseTransferService.BF_ACTIONS_ERROR_MSG,
                ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference());
        assertEquals(expectedBfActionsError, errors.getFirst());
    }

    @Test
    public void createCaseTransferHearingListed() {
        ccdRequest.getCaseDetails().getCaseData().setHearingCollection(getHearingTypeCollection(HEARING_STATUS_LISTED));

        var errors = caseTransferService.createCaseTransfer(ccdRequest.getCaseDetails(), authToken);

        assertEquals(1, errors.size());
        var expectedHearingsError = String.format(CaseTransferService.HEARINGS_ERROR_MSG,
                ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference());
        assertEquals(expectedHearingsError, errors.getFirst());
    }

    @Test
    public void createCaseTransferHearingListedAndBfNotCleared() {
        ccdRequest.getCaseDetails().getCaseData().setBfActions(BFHelperTest.generateBFActionTypeItems());
        ccdRequest.getCaseDetails().getCaseData().getBfActions().getFirst().getValue().setCleared(null);
        ccdRequest.getCaseDetails().getCaseData().setHearingCollection(getHearingTypeCollection(HEARING_STATUS_LISTED));

        var errors = caseTransferService.createCaseTransfer(ccdRequest.getCaseDetails(), authToken);

        assertEquals(2, errors.size());
        var expectedBfActionsError = String.format(CaseTransferService.BF_ACTIONS_ERROR_MSG,
                ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference());
        assertEquals(expectedBfActionsError, errors.getFirst());
        var expectedHearingsError = String.format(CaseTransferService.HEARINGS_ERROR_MSG,
                ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference());
        assertEquals(expectedHearingsError, errors.get(1));
    }

    @Test
    public void createCaseTransferBfClearedAndNotHearingListed() {
        ccdRequest.getCaseDetails().getCaseData().setBfActions(BFHelperTest.generateBFActionTypeItems());
        ccdRequest.getCaseDetails().getCaseData().setHearingCollection(getHearingTypeCollection(HEARING_STATUS_HEARD));

        var errors = caseTransferService.createCaseTransfer(ccdRequest.getCaseDetails(), authToken);

        assertEquals(0, errors.size());
    }

    private List<HearingTypeItem> getHearingTypeCollection(String hearingState) {
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingStatus(hearingState);
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);
        HearingType hearingType = new HearingType();
        hearingType.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem)));

        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setId("1234");
        hearingTypeItem.setValue(hearingType);
        return new ArrayList<>(Collections.singletonList(hearingTypeItem));
    }
}
