package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleBatchUpdate2ServiceTest {

    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;
    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private MultipleHelperService multipleHelperService;
    @Mock
    private CcdClient ccdClient;

    @InjectMocks
    private MultipleBatchUpdate2Service multipleBatchUpdate2Service;

    private TreeMap<String, Object> multipleObjectsFlags;
    private TreeMap<String, Object> multipleObjects;
    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitMultipleEvent> submitMultipleEvents;
    private MultiplesHelper multiplesHelper;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.getCaseData().setCaseIdCollection(null);
        userToken = "authString";
        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246000");
        moveCasesType.setUpdatedSubMultipleRef("");
        moveCasesType.setConvertToSingle(YES);
        multipleDetails.getCaseData().setMoveCases(moveCasesType);
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
    }

    @Test
    public void batchUpdate2LogicDetachCases() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(multipleHelperService.getLeadCaseFromExcel(anyString(), any(), anyList()))
                .thenReturn("245003/2020");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void batchUpdate2LogicDetachCasesEmptyNewLeadCase() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(multipleHelperService.getLeadCaseFromExcel(anyString(), any(), anyList()))
                .thenReturn("");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
        assertNull(multipleDetails.getCaseData().getLeadCase());
    }

    @Test
    public void batchUpdate2LogicSameMultipleEmptySubMultiple() {
        multipleDetails.getCaseData().getMoveCases().setConvertToSingle(NO);
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
    }

    @Test
    public void batchUpdate2LogicSameMultipleWithSubMultiple() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        multipleDetails.getCaseData().getMoveCases().setConvertToSingle(NO);
        multipleDetails.getCaseData().getMoveCases().setUpdatedSubMultipleRef("246000/1");
        multipleDetails.setCaseTypeId("Leeds_Multiple");
        multipleDetails.setJurisdiction("Employment");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void batchUpdate2LogicDifferentEmptyMultiple() {
        when(multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                "246001")
        ).thenReturn(submitMultipleEvents);
        when(multipleHelperService.getLeadCaseFromExcel(anyString(), any(), anyList()))
                .thenReturn("");
        multipleDetails.getCaseData().getMoveCases().setConvertToSingle(NO);
        multipleDetails.getCaseData().getMoveCases().setUpdatedMultipleRef("246001");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void batchUpdate2LogicDifferentMultipleEmptySubMultiple() {
        when(multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                "246001")
        ).thenReturn(submitMultipleEvents);
        when(multipleHelperService.getLeadCaseFromExcel(anyString(), any(), anyList()))
                .thenReturn("245003/2020");
        multipleDetails.getCaseData().getMoveCases().setConvertToSingle(NO);
        multipleDetails.getCaseData().getMoveCases().setUpdatedMultipleRef("246001");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void batchUpdate2LogicDifferentMultipleWithSubMultiple() {
        when(multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                "246001")
        ).thenReturn(submitMultipleEvents);
        when(multipleHelperService.getLeadCaseFromExcel(anyString(), any(), anyList()))
                .thenReturn("245003/2020");
        multipleDetails.getCaseData().getMoveCases().setConvertToSingle(NO);
        multipleDetails.getCaseData().getMoveCases().setUpdatedMultipleRef("246001");
        multipleDetails.getCaseData().getMoveCases().setUpdatedSubMultipleRef("246001/1");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

}