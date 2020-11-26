package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadES;
import uk.gov.hmcts.ecm.common.model.schedule.items.ScheduleRespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleRespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;

import java.util.*;

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
    private List<SchedulePayloadES> schedulePayloadES;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleObjectsSubMultiple = MultipleUtil.getMultipleObjectsSubMultiple();
        schedulePayloadES = MultipleUtil.getSchedulePayloadES();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void bulkScheduleLogicFlags() {
        schedulePayloadES.get(0).setClaimantCompany(null);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(schedulePayloadES);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(singleCasesReadingService, times(1)).retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verifyNoMoreInteractions(singleCasesReadingService);
    }

    @Test
    public void bulkScheduleLogicFlagsEmptySchedules() {
        List<SchedulePayloadES> schedulePayloadES1 = new ArrayList<>(Collections.singletonList(new SchedulePayloadES()));
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(schedulePayloadES1);
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
        schedulePayloadES.get(0).setClaimantCompany(null);
        schedulePayloadES.get(0).setClaimantIndType(null);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(schedulePayloadES);
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
        schedulePayloadES.get(0).getRespondentCollection().add(respondentSumTypeItem);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        when(singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(schedulePayloadES);
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
                .thenReturn(schedulePayloadES);
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
                .thenReturn(schedulePayloadES);
        multipleScheduleService.bulkScheduleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verifyNoMoreInteractions(excelDocManagementService);
    }

}