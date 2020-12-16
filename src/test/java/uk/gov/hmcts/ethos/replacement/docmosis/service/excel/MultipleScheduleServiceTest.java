package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadEvent;
import uk.gov.hmcts.ecm.common.model.schedule.items.ScheduleRespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleRespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;

import java.util.ArrayList;
import java.util.HashSet;
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
    private ExcelDocManagementService excelDocManagementService;
    @InjectMocks
    private MultipleScheduleService multipleScheduleService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private TreeMap<String, Object> multipleObjectsSubMultiple;
    private MultipleDetails multipleDetails;
    private HashSet<SchedulePayloadEvent> schedulePayloadEvents;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleObjectsSubMultiple = MultipleUtil.getMultipleObjectsSubMultiple();
        schedulePayloadEvents = MultipleUtil.getSchedulePayloadEvents();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void bulkScheduleLogicFlags() {
        schedulePayloadEvents.iterator().next().getSchedulePayloadES().setClaimantCompany(null);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(schedulePayloadEvents);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(singleCasesReadingService, times(1)).retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkScheduleLogicFlagsWithoutCompanyNorClaimant() {
        schedulePayloadEvents.iterator().next().getSchedulePayloadES().setClaimantCompany(null);
        schedulePayloadEvents.iterator().next().getSchedulePayloadES().setClaimantIndType(null);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(schedulePayloadEvents);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(singleCasesReadingService, times(1)).retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkScheduleLogicFlagsMultipleRespondents() {
        ScheduleRespondentSumTypeItem respondentSumTypeItem = new ScheduleRespondentSumTypeItem();
        respondentSumTypeItem.setValue(new ScheduleRespondentSumType());
        schedulePayloadEvents.iterator().next().getSchedulePayloadES().getRespondentCollection().add(respondentSumTypeItem);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(schedulePayloadEvents);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(singleCasesReadingService, times(1)).retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkScheduleLogicSubMultiple() {
        multipleDetails.getCaseData().setScheduleDocName(LIST_CASES_CONFIG);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsSubMultiple);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                MultiplesScheduleHelper.getSubMultipleCaseIds(multipleObjectsSubMultiple)))
                .thenReturn(schedulePayloadEvents);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(singleCasesReadingService, times(1)).retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                MultiplesScheduleHelper.getSubMultipleCaseIds(multipleObjectsSubMultiple));
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkScheduleLogicSubMultipleNoCasesFiltered() {
        multipleDetails.getCaseData().setScheduleDocName(LIST_CASES_CONFIG);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                MultiplesScheduleHelper.getSubMultipleCaseIds(multipleObjectsSubMultiple)))
                .thenReturn(schedulePayloadEvents);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verifyNoMoreInteractions(excelDocManagementService);
    }

}