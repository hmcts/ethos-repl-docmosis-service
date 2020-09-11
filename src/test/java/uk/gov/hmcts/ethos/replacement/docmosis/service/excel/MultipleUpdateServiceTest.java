package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleUpdateServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private MultipleBatchUpdate1Service multipleBatchUpdate1Service;
    @Mock
    private MultipleBatchUpdate2Service multipleBatchUpdate2Service;
    @Mock
    private MultipleBatchUpdate3Service multipleBatchUpdate3Service;

    @InjectMocks
    private MultipleUpdateService multipleUpdateService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void bulkUpdate1Logic() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        multipleUpdateService.bulkUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        assertEquals(UPDATING_STATE, multipleDetails.getCaseData().getState());
        verify(multipleBatchUpdate1Service, times(1)).batchUpdate1Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verifyNoMoreInteractions(multipleBatchUpdate1Service);
    }

    @Test
    public void bulkUpdate2Logic() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        multipleDetails.getCaseData().setBatchUpdateType(BATCH_UPDATE_TYPE_2);
        multipleUpdateService.bulkUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        assertEquals(UPDATING_STATE, multipleDetails.getCaseData().getState());
        verify(multipleBatchUpdate2Service, times(1)).batchUpdate2Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verifyNoMoreInteractions(multipleBatchUpdate2Service);
    }
    @Test
    public void bulkUpdate3Logic() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        multipleDetails.getCaseData().setBatchUpdateType(BATCH_UPDATE_TYPE_3);
        multipleUpdateService.bulkUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        assertEquals(UPDATING_STATE, multipleDetails.getCaseData().getState());
        verify(multipleBatchUpdate3Service, times(1)).batchUpdate3Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
        verifyNoMoreInteractions(multipleBatchUpdate3Service);
    }

    @Test
    public void bulkUpdateLogicEmptyOpenState() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        multipleUpdateService.bulkUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        assertEquals(OPEN_STATE, multipleDetails.getCaseData().getState());
    }

}