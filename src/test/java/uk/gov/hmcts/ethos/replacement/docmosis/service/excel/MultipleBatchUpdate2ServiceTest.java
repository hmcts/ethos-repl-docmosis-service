package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleBatchUpdate2ServiceTest {

    @Mock
    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private UserService userService;
    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;
    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private MultipleCasesSendingService multipleCasesSendingService;

    @InjectMocks
    private MultipleBatchUpdate2Service multipleBatchUpdate2Service;

    private TreeMap<String, Object> multipleObjectsFlags;
    private TreeMap<String, Object> multipleObjects;
    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitMultipleEvent> submitMultipleEvents;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        UserDetails userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
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
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(userService, times(1)).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);
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
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                "246001")
        ).thenReturn(submitMultipleEvents);
        multipleDetails.getCaseData().getMoveCases().setConvertToSingle(NO);
        multipleDetails.getCaseData().getMoveCases().setUpdatedMultipleRef("246001");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(2)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void batchUpdate2LogicDifferentMultipleWithSubMultiple() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                "246001")
        ).thenReturn(submitMultipleEvents);
        multipleDetails.getCaseData().getMoveCases().setConvertToSingle(NO);
        multipleDetails.getCaseData().getMoveCases().setUpdatedMultipleRef("246001");
        multipleDetails.getCaseData().getMoveCases().setUpdatedSubMultipleRef("246001/1");
        multipleBatchUpdate2Service.batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verify(excelDocManagementService, times(2)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

}