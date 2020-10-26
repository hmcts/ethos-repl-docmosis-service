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
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleCasesReadingServiceTest {

    @Mock
    private CcdClient ccdClient;
    @InjectMocks
    private SingleCasesReadingService singleCasesReadingService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private TreeMap<String, Object> multipleObjectsSubMultiple;
    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitEvent> submitEventList;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleObjectsSubMultiple = MultipleUtil.getMultipleObjectsSubMultiple();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        submitEventList = MultipleUtil.getSubmitEvents();
        userToken = "authString";
    }

    @Test
    public void retrieveSingleCasesFlags() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(userToken,
                multipleDetails.getCaseTypeId(),
                new ArrayList<>(multipleObjectsFlags.keySet())))
                .thenReturn(submitEventList);
        singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags,
                FilterExcelType.FLAGS);
        verify(ccdClient, times(1)).retrieveCasesElasticSearch(userToken,
                "Manchester",
                new ArrayList<>(multipleObjectsFlags.keySet()));
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    public void retrieveSingleCasesFlagsException() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(anyString(),
                anyString(),
                anyList()))
                .thenThrow(new RuntimeException());
        List<SubmitEvent> result = singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsFlags,
                FilterExcelType.FLAGS);
        assertEquals(0, result.size());
    }

    @Test
    public void retrieveSingleCasesSubMultiple() throws IOException {
        when(ccdClient.retrieveCasesElasticSearch(userToken,
                multipleDetails.getCaseTypeId(),
                getSubMultipleCaseIds(multipleObjectsSubMultiple)))
                .thenReturn(submitEventList);
        singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleObjectsSubMultiple,
                FilterExcelType.SUB_MULTIPLE);
        verify(ccdClient, times(1)).retrieveCasesElasticSearch(userToken,
                "Manchester",
                getSubMultipleCaseIds(multipleObjectsSubMultiple));
        verifyNoMoreInteractions(ccdClient);
    }

    private List<String> getSubMultipleCaseIds(TreeMap<String, Object> multipleObjects) {

        List<String> caseIds = new ArrayList<>();

        for (Map.Entry<String, Object> entry : multipleObjects.entrySet()) {
            caseIds.addAll((List<String>) entry.getValue());
        }

        return caseIds;
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
}