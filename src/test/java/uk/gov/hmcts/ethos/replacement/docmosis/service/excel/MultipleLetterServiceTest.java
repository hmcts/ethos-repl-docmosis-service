package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadEvent;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO_CASES_SEARCHED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper.createDynamicHearingList;

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
    @Mock
    private MultipleDynamicListFlagsService multipleDynamicListFlagsService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitEvent> submitEvents;
    private List<LabelPayloadEvent> labelPayloadEvents;
    private List<String> errors;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Leeds_Multiple");
        submitEvents = MultipleUtil.getSubmitEvents();
        labelPayloadEvents = MultipleUtil.getLabelPayloadEvents();
        userToken = "authString";
        errors = new ArrayList<>();
    }

    @Test
    public void bulkLetterLogic() throws IOException {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvents.getFirst());
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any(), any()))
                .thenReturn(new DocumentInfo());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors,
                false);
        verify(singleCasesReadingService, times(1)).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource());
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test(expected = Exception.class)
    public void bulkLetterLogicException() throws IOException {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvents.getFirst());
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any(), any()))
                .thenThrow(new IOException());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors,
                false);
        verify(singleCasesReadingService, times(1)).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource());
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkLetterLogicWithoutCases() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors,
                false);
        assertEquals(NO_CASES_SEARCHED, errors.getFirst());
    }

    @Test
    public void bulkLetterLogicForLabels() throws IOException {
        CorrespondenceType correspondenceType = new CorrespondenceType();
        correspondenceType.setTopLevelDocuments(ADDRESS_LABELS_TEMPLATE);
        multipleDetails.getCaseData().setCorrespondenceType(correspondenceType);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveLabelCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(labelPayloadEvents);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvents.getFirst());
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any(), any()))
                .thenReturn(new DocumentInfo());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors,
                false);
        verify(singleCasesReadingService, times(1)).retrieveLabelCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verify(singleCasesReadingService, times(1)).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource());
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkLetterLogicForLabelsScotland() throws IOException {
        CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(ADDRESS_LABELS_TEMPLATE);
        multipleDetails.getCaseData().setCorrespondenceScotType(correspondenceScotType);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveLabelCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(labelPayloadEvents);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvents.getFirst());
        when(tornadoService.documentGeneration(anyString(), any(), anyString(), any(), any(), any()))
                .thenReturn(new DocumentInfo());
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors,
                false);
        verify(singleCasesReadingService, times(1)).retrieveLabelCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verify(singleCasesReadingService, times(1)).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource());
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkLetterLogicForLabelsValidation() {
        CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(ADDRESS_LABELS_TEMPLATE);
        multipleDetails.getCaseData().setCorrespondenceScotType(correspondenceScotType);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveLabelCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(labelPayloadEvents);
        multipleLetterService.bulkLetterLogic(userToken,
                multipleDetails,
                errors,
                true);
        verify(singleCasesReadingService, times(1)).retrieveLabelCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void dynamicMultipleLetters() {
        MultipleUtil.addHearingToCaseData(submitEvents.getFirst().getCaseData());
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvents.getFirst());
        multipleLetterService.dynamicMultipleLetters(userToken, multipleDetails, errors);
        verify(singleCasesReadingService, times(1)).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags.firstKey(),
                multipleDetails.getCaseData().getMultipleSource());
        assertEquals(1, multipleDetails.getCaseData().getCorrespondenceType().getDynamicHearingNumber()
                .getListItems().size());
        var hearingFromCase = createDynamicHearingList(submitEvents.getFirst().getCaseData()).getFirst();
        assertEquals(hearingFromCase, multipleDetails.getCaseData().getCorrespondenceType().getDynamicHearingNumber()
                .getListItems().getFirst());
    }

}