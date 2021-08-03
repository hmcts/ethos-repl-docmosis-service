package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.CaseMultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.PersistentQHelperService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIGRATION_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.UPDATING_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleTransferServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private PersistentQHelperService persistentQHelperService;
    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;

    @InjectMocks
    private MultipleTransferService multipleTransferService;

    private TreeMap<String, Object> multipleObjects;
    private MultipleDetails multipleDetails;
    private List<SubmitMultipleEvent> submitMultipleEvents;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        userToken = "authString";
    }

    @Test
    public void multipleTransferLogic() {

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        multipleTransferService.multipleTransferLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        assertEquals(UPDATING_STATE, multipleDetails.getCaseData().getState());
        verify(persistentQHelperService, times(1)).sendCreationEventToSingles(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                new ArrayList<>(),
                new ArrayList<>(multipleObjects.keySet()),
                "Manchester",
                "PositionTypeCT",
                null,
                multipleDetails.getCaseData().getReasonForCT(),
                multipleDetails.getCaseData().getMultipleReference(),
                YES,
                multipleDetails.getCaseId());
        verifyNoMoreInteractions(persistentQHelperService);

    }

    @Test
    public void multipleTransferLogicEmptyCollection() {

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        multipleTransferService.multipleTransferLogic(userToken,
                multipleDetails,
                new ArrayList<>());
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
                new ArrayList<>());

        assertEquals("<a target=\"_blank\" href=\"null/cases/case-details/0\">246000</a>",
                multipleDetails.getCaseData().getLinkedMultipleCT());
        List<CaseMultipleTypeItem> caseMultipleTypeItemList = multipleDetails.getCaseData().getCaseMultipleCollection();
        assertEquals("MultipleObjectType(ethosCaseRef=245000/2020, subMultiple=245000, flag1=null, "
                + "flag2=null, flag3=null, flag4=null)", caseMultipleTypeItemList.get(0).getValue().toString());
        assertEquals("MultipleObjectType(ethosCaseRef=245003/2020, subMultiple=245003, flag1=null, "
                + "flag2=null, flag3=null, flag4=null)", caseMultipleTypeItemList.get(1).getValue().toString());
        assertEquals("MultipleObjectType(ethosCaseRef=245004/2020, subMultiple=245002, flag1=null, "
                + "flag2=null, flag3=null, flag4=null)", caseMultipleTypeItemList.get(2).getValue().toString());

    }

}