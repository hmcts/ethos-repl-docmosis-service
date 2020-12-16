package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleCasesReadingServiceTest {

    @Mock
    private CcdClient ccdClient;
    @InjectMocks
    private SingleCasesReadingService singleCasesReadingService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitEvent> submitEventList;
    private HashSet<SchedulePayloadEvent> schedulePayloadEvents;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        submitEventList = MultipleUtil.getSubmitEvents();
        schedulePayloadEvents = MultipleUtil.getSchedulePayloadEvents();
        userToken = "authString";
    }

    @Test
    public void retrieveSingleCase() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(Collections.singletonList("240001/2020"))))
                .thenReturn(submitEventList);
        singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                "240001/2020");
        verify(ccdClient, times(1)).retrieveCasesElasticSearch(userToken,
                "Manchester",
                new ArrayList<>(Collections.singletonList("240001/2020")));
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    public void retrieveSingleCaseException() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(anyString(),
                anyString(),
                anyList()))
                .thenThrow(new RuntimeException());
        SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                "240001/2020");
        assertNull(submitEvent);
    }

    @Test
    public void retrieveScheduleCases() throws IOException {
        when(ccdClient.retrieveCasesElasticSearchSchedule(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(Collections.singletonList("240001/2020"))))
                .thenReturn(new ArrayList<>(schedulePayloadEvents));
        singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(Collections.singletonList("240001/2020")));
        verify(ccdClient, times(1)).retrieveCasesElasticSearchSchedule(userToken,
                "Manchester",
                new ArrayList<>(Collections.singletonList("240001/2020")));
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    public void retrieveScheduleCasesException() throws IOException {
        when(ccdClient.retrieveCasesElasticSearchSchedule(anyString(),
                anyString(),
                anyList()))
                .thenThrow(new RuntimeException());
        HashSet<SchedulePayloadEvent> schedulePayloadEventList = singleCasesReadingService.retrieveScheduleCases(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(Collections.singletonList("240001/2020")));
        assertEquals(schedulePayloadEventList, new HashSet<>());
    }

}