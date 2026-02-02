package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.CaseMultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseTransferService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.PersistentQHelperService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIGRATION_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.UPDATING_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.CaseTransferService.BF_ACTIONS_ERROR_MSG;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.CaseTransferService.HEARINGS_ERROR_MSG;

@RequiredArgsConstructor
@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleTransferServiceTest {

    private String ccdGatewayBaseUrl;

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private PersistentQHelperService persistentQHelperService;
    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;
    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private CaseTransferService caseTransferService;

    @InjectMocks
    private MultipleTransferService multipleTransferService;
    private TreeMap<String, Object> multipleObjects;
    private MultipleDetails multipleDetails;
    private List<SubmitMultipleEvent> submitMultipleEvents;
    private List<SubmitEvent> submitEvents;
    private String userToken;
    private List<String> errors;

    @Before
    public void setUp() {
        ccdGatewayBaseUrl = null;
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseId("1559817606275162");
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        userToken = "authString";
        submitEvents = MultipleUtil.getSubmitEvents();
        errors = new ArrayList<>();
    }

    @Test
    public void multipleTransferLogic() {

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(singleCasesReadingService.retrieveSingleCases(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(submitEvents);
        multipleTransferService.multipleTransferLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
        assertEquals(UPDATING_STATE, multipleDetails.getCaseData().getState());
        verify(persistentQHelperService, times(1))
            .sendCreationEventToSingles(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                errors,
                new ArrayList<>(multipleObjects.keySet()),
                "Manchester",
                "PositionTypeCT",
                ccdGatewayBaseUrl,
                multipleDetails.getCaseData().getReasonForCT(),
                multipleDetails.getCaseData().getMultipleReference(),
                YES,
                MultiplesHelper.generateMarkUp(ccdGatewayBaseUrl,
                        multipleDetails.getCaseId(),
                        multipleDetails.getCaseData().getMultipleReference()));

        verifyNoMoreInteractions(persistentQHelperService);

    }

    @Test
    public void multipleTransferLogicEmptyCollection() {

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        multipleTransferService.multipleTransferLogic(userToken,
                multipleDetails,
                errors);
        verifyNoMoreInteractions(persistentQHelperService);

    }

    @Test
    public void populateDataIfComingFromCT() {

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                "Bristol",
                "246000")
        ).thenReturn(submitMultipleEvents);

        multipleDetails.getCaseData().setLinkedMultipleCT("Bristol");
        multipleDetails.getCaseData().setMultipleSource(MIGRATION_CASE_SOURCE);

        multipleTransferService.populateDataIfComingFromCT(userToken,
                multipleDetails,
                errors);

        assertEquals("<a target=\"_blank\" href=\"null/cases/case-details/0\">246000</a>",
                multipleDetails.getCaseData().getLinkedMultipleCT());
        List<CaseMultipleTypeItem> caseMultipleTypeItemList = multipleDetails.getCaseData().getCaseMultipleCollection();
        assertEquals("MultipleObjectType(ethosCaseRef=245000/2020, subMultiple=245000, flag1=null, "
                + "flag2=null, flag3=null, flag4=null)", caseMultipleTypeItemList.getFirst().getValue().toString());
        assertEquals("MultipleObjectType(ethosCaseRef=245003/2020, subMultiple=245003, flag1=null, "
                + "flag2=null, flag3=null, flag4=null)", caseMultipleTypeItemList.get(1).getValue().toString());
        assertEquals("MultipleObjectType(ethosCaseRef=245004/2020, subMultiple=245002, flag1=null, "
                + "flag2=null, flag3=null, flag4=null)", caseMultipleTypeItemList.get(2).getValue().toString());

    }

    @Test
    public void validateCasesBeforeTransfer() {
        var caseData = new CaseData();
        caseData.setEthosCaseReference("245004/2020");

        // Un cleared BF Action
        var bfActionType = new BFActionType();
        bfActionType.setDateEntered("2020-11-11");
        var bfActionTypeItem = new BFActionTypeItem();
        bfActionTypeItem.setValue(bfActionType);
        caseData.setBfActions(List.of(bfActionTypeItem));

        // Listed hearing
        var dateListedType = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_LISTED);
        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(dateListedType);
        var hearingType = new HearingType();
        hearingType.setHearingDateCollection(List.of(dateListedTypeItem));
        var hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(hearingType);
        caseData.setHearingCollection(List.of(hearingTypeItem));

        var submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(ACCEPTED_STATE);
        submitEvent.setCaseId(1232121232);

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(singleCasesReadingService.retrieveSingleCases(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(new ArrayList<>(Collections.singletonList(submitEvent)));
        doCallRealMethod().when(caseTransferService).validateCase(isA(CaseData.class), anyList());

        caseTransferService.validateCase(caseData, errors);
        assertEquals(2, errors.size());
        assertEquals(String.format(BF_ACTIONS_ERROR_MSG, caseData.getEthosCaseReference()), errors.getFirst());
        assertEquals(String.format(HEARINGS_ERROR_MSG, caseData.getEthosCaseReference()), errors.get(1));

    }

    @Test
    public void validateCasesBeforeTransfer_withoutErrors() {
        var caseData = new CaseData();
        caseData.setEthosCaseReference("245004/2020");

        // Cleared BF
        var bfActionType = new BFActionType();
        bfActionType.setDateEntered("2020-11-11");
        bfActionType.setCleared("2020-11-10");
        var bfActionTypeItem = new BFActionTypeItem();
        bfActionTypeItem.setValue(bfActionType);
        caseData.setBfActions(List.of(bfActionTypeItem));

        // 'Heard' hearing
        var dateListedType = new DateListedType();
        dateListedType.setHearingStatus(HEARING_STATUS_HEARD);
        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(dateListedType);
        var hearingType = new HearingType();
        hearingType.setHearingDateCollection(List.of(dateListedTypeItem));
        var hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(hearingType);
        caseData.setHearingCollection(List.of(hearingTypeItem));

        var submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(ACCEPTED_STATE);
        submitEvent.setCaseId(1232121232);

        var submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseData(caseData);
        submitEvent2.setState(ACCEPTED_STATE);
        submitEvent2.setCaseId(1232121232);

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        List<String> ethosCaseRefCollection = new ArrayList<>(multipleObjects.keySet());
        when(singleCasesReadingService.retrieveSingleCases(userToken, multipleDetails.getCaseTypeId(),
                ethosCaseRefCollection, multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(List.of(submitEvent, submitEvent2));
        doCallRealMethod().when(caseTransferService).validateCase(isA(CaseData.class), anyList());
        multipleTransferService.multipleTransferLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
    }

}