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
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TornadoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleLetterServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private TornadoService tornadoService;
    @Mock
    private EventValidationService eventValidationService;
    @InjectMocks
    private MultipleLetterService multipleLetterService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitEvent> submitEvents;
    private List<String> errors;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Leeds_Multiple");
        submitEvents = MultipleUtil.getSubmitEvents();
        userToken = "authString";
        errors = new ArrayList<>();
    }

    @Test
    public void bulkLetterLogic() throws IOException {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey()))
                .thenReturn(submitEvents.get(0));
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any()))
                .thenReturn(new DocumentInfo());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors);
        verify(singleCasesReadingService, times(1)).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey());
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test(expected = Exception.class)
    public void bulkLetterLogicException() throws IOException {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey()))
                .thenReturn(submitEvents.get(0));
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any()))
                .thenThrow(new IOException());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors);
        verify(singleCasesReadingService, times(1)).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey());
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkLetterLogicWithoutCases() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors);
        assertEquals("No cases searched to generate schedules", errors.get(0));
    }

}