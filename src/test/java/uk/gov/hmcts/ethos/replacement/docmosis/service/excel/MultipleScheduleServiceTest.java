package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TornadoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIST_CASES_CONFIG;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleScheduleServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private TornadoService tornadoService;
    @InjectMocks
    private MultipleScheduleService multipleScheduleService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private TreeMap<String, Object> multipleObjectsSubMultiple;
    private MultipleDetails multipleDetails;
    private List<SubmitEvent> submitEvents;
    private DocumentInfo documentInfo;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleObjectsSubMultiple = MultipleUtil.getMultipleObjectsSubMultiple();
        submitEvents = MultipleUtil.getSubmitEvents();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        documentInfo = new DocumentInfo();
        documentInfo.setMarkUp("<a target=\"_blank\" href=\"null/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4/binary\">Document</a>");
        userToken = "authString";
    }

    @Test
    public void bulkScheduleLogicFlags() throws IOException {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags,
                FilterExcelType.FLAGS))
                .thenReturn(submitEvents);
        when(tornadoService.scheduleMultipleGeneration(userToken,
                multipleDetails.getCaseData(),
                multipleObjectsFlags,
                submitEvents))
        .thenReturn(documentInfo);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(singleCasesReadingService, times(1)).retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags,
                FilterExcelType.FLAGS);
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkScheduleLogicSubMultiple() throws IOException {
        multipleDetails.getCaseData().setScheduleDocName(LIST_CASES_CONFIG);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsSubMultiple);
        when(singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsSubMultiple,
                FilterExcelType.SUB_MULTIPLE))
                .thenReturn(submitEvents);
        when(tornadoService.scheduleMultipleGeneration(userToken,
                multipleDetails.getCaseData(),
                multipleObjectsSubMultiple,
                submitEvents))
                .thenReturn(documentInfo);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(singleCasesReadingService, times(1)).retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsSubMultiple,
                FilterExcelType.SUB_MULTIPLE);
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkScheduleLogicSubMultipleNoCasesFiltered() {
        multipleDetails.getCaseData().setScheduleDocName(LIST_CASES_CONFIG);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        when(singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsSubMultiple,
                FilterExcelType.SUB_MULTIPLE))
                .thenReturn(submitEvents);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verifyNoMoreInteractions(tornadoService);
    }

    @Test(expected = Exception.class)
    public void bulkScheduleLogicSubMultipleException() throws IOException {
        multipleDetails.getCaseData().setScheduleDocName(LIST_CASES_CONFIG);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsSubMultiple);
        when(singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsSubMultiple,
                FilterExcelType.SUB_MULTIPLE))
                .thenReturn(submitEvents);
        when(tornadoService.scheduleMultipleGeneration(userToken,
                multipleDetails.getCaseData(),
                multipleObjectsSubMultiple,
                submitEvents))
                .thenThrow(new RuntimeException());
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
    }

}