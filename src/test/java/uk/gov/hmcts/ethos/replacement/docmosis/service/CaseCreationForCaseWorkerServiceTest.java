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
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BFHelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseCreationForCaseWorkerServiceTest {

    @InjectMocks
    private CaseCreationForCaseWorkerService caseCreationForCaseWorkerService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;
    private String authToken;
    @Mock
    private SingleReferenceService singleReferenceService;
    @Mock
    private MultipleReferenceService multipleReferenceService;
    @Mock
    private PersistentQHelperService persistentQHelperService;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = MultipleUtil.getCaseData("2123456/2020");
        caseData.setCaseRefNumberCount("2");
        caseData.setPositionTypeCT("PositionTypeCT");
        DynamicFixedListType officeCT = new DynamicFixedListType();
        DynamicValueType valueType = new DynamicValueType();
        valueType.setCode(LEEDS_CASE_TYPE_ID);
        officeCT.setValue(valueType);
        caseData.setOfficeCT(officeCT);
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("Manchester");
        caseDetails.setJurisdiction("Employment");
        caseDetails.setState(ACCEPTED_STATE);
        ccdRequest.setCaseDetails(caseDetails);
        submitEvent = new SubmitEvent();
        authToken = "authToken";
    }

    @Test(expected = Exception.class)
    public void caseCreationRequestException() throws IOException {
        when(ccdClient.startCaseCreation(anyString(), any())).thenThrow(new InternalException(ERROR_MESSAGE));
        when(ccdClient.submitCaseCreation(anyString(), any(), any())).thenReturn(submitEvent);
        caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, authToken);
    }

    @Test
    public void caseCreationRequest() throws IOException {
        when(ccdClient.startCaseCreation(anyString(), any())).thenReturn(ccdRequest);
        when(ccdClient.submitCaseCreation(anyString(), any(), any())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, authToken);
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

    @Test
    public void createCaseTransfer() {
        List<String> errors = new ArrayList<>();
        caseCreationForCaseWorkerService.createCaseTransfer(ccdRequest.getCaseDetails(), errors, authToken);
        assertEquals("PositionTypeCT", ccdRequest.getCaseDetails().getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID, ccdRequest.getCaseDetails().getCaseData().getLinkedCaseCT());
    }

    @Test
    public void createCaseTransferECC() throws IOException {
        List<String> errors = new ArrayList<>();
        CaseData caseData = MultipleUtil.getCaseData("3434232323");
        caseData.setCaseRefNumberCount("2");
        caseData.setPositionTypeCT("PositionTypeCT1");
        DynamicFixedListType officeCT = new DynamicFixedListType();
        DynamicValueType valueType = new DynamicValueType();
        valueType.setCode(LONDON_CENTRAL_CASE_TYPE_ID);
        officeCT.setValue(valueType);
        caseData.setOfficeCT(officeCT);
        submitEvent.setCaseData(caseData);
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        ccdRequest.getCaseDetails().getCaseData().setCounterClaim("3434232323");
        when(ccdClient.retrieveCasesElasticSearch(anyString(),anyString(), anyList())).thenReturn(submitEventList);
        caseCreationForCaseWorkerService.createCaseTransfer(ccdRequest.getCaseDetails(), errors, authToken);
        assertEquals("PositionTypeCT", ccdRequest.getCaseDetails().getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID, ccdRequest.getCaseDetails().getCaseData().getLinkedCaseCT());
        assertEquals("PositionTypeCT1", submitEventList.get(0).getCaseData().getPositionType());
        assertEquals("Transferred to " + LONDON_CENTRAL_CASE_TYPE_ID, submitEventList.get(0).getCaseData().getLinkedCaseCT());
    }

    @Test
    public void createCaseTransferMultiples() {
        List<String> errors = new ArrayList<>();
        ccdRequest.getCaseDetails().getCaseData().setStateAPI(MULTIPLE);
        caseCreationForCaseWorkerService.createCaseTransfer(ccdRequest.getCaseDetails(), errors, authToken);
        assertEquals("PositionTypeCT", ccdRequest.getCaseDetails().getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID, ccdRequest.getCaseDetails().getCaseData().getLinkedCaseCT());
    }

    @Test
    public void createCaseTransferBfNotCleared() {
        ccdRequest.getCaseDetails().getCaseData().setBfActions(BFHelperTest.generateBFActionTypeItems());
        ccdRequest.getCaseDetails().getCaseData().getBfActions().get(0).getValue().setCleared(null);
        List<String> errors = new ArrayList<>();
        caseCreationForCaseWorkerService.createCaseTransfer(ccdRequest.getCaseDetails(), errors, authToken);
        assertEquals(1, errors.size());
        assertEquals("There are one or more open Brought Forward actions that must be cleared before the "
                + "case " + ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference() + " can be transferred", errors.get(0));
    }

    @Test
    public void createCaseTransferHearingListed() {
        ccdRequest.getCaseDetails().getCaseData().setHearingCollection(getHearingTypeCollection(HEARING_STATUS_LISTED));
        List<String> errors = new ArrayList<>();
        caseCreationForCaseWorkerService.createCaseTransfer(ccdRequest.getCaseDetails(), errors, authToken);
        assertEquals(1, errors.size());
        assertEquals("There are one or more hearings that have the status Listed. These must be updated "
                + "before the case " + ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference() + " can be transferred", errors.get(0));
    }

    @Test
    public void createCaseTransferHearingListedAndBfNotCleared() {
        ccdRequest.getCaseDetails().getCaseData().setBfActions(BFHelperTest.generateBFActionTypeItems());
        ccdRequest.getCaseDetails().getCaseData().getBfActions().get(0).getValue().setCleared(null);
        ccdRequest.getCaseDetails().getCaseData().setHearingCollection(getHearingTypeCollection(HEARING_STATUS_LISTED));
        List<String> errors = new ArrayList<>();
        caseCreationForCaseWorkerService.createCaseTransfer(ccdRequest.getCaseDetails(), errors, authToken);
        assertEquals(2, errors.size());
        assertEquals("There are one or more open Brought Forward actions that must be cleared before the "
                + "case " +  ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference() + " can be transferred", errors.get(0));
        assertEquals("There are one or more hearings that have the status Listed. These must be updated "
                + "before the case " + ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference() + " can be transferred", errors.get(1));
    }

    @Test
    public void createCaseTransferBfClearedAndNotHearingListed() {
        ccdRequest.getCaseDetails().getCaseData().setBfActions(BFHelperTest.generateBFActionTypeItems());
        ccdRequest.getCaseDetails().getCaseData().setHearingCollection(getHearingTypeCollection(HEARING_STATUS_HEARD));
        List<String> errors = new ArrayList<>();
        caseCreationForCaseWorkerService.createCaseTransfer(ccdRequest.getCaseDetails(), errors, authToken);
        assertEquals(0, errors.size());
    }

    private List<HearingTypeItem> getHearingTypeCollection(String hearingState){
        HearingType hearingType = new HearingType();
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingStatus(hearingState);
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);
        hearingType.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem)));

        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setId("1234");
        hearingTypeItem.setValue(hearingType);
        return new ArrayList<>(Collections.singletonList(hearingTypeItem));
    }

}