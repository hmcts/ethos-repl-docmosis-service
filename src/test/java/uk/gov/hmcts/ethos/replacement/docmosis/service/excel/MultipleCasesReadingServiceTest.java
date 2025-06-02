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
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleCasesReadingServiceTest {

    @Mock
    private CcdClient ccdClient;
    @InjectMocks
    private MultipleCasesReadingService multipleCasesReadingService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitMultipleEvent> submitMultipleEvents;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        userToken = "authString";
    }

    @Test
    public void retrieveMultipleCasesWithRetries() throws IOException {
        when(ccdClient.retrieveMultipleCasesElasticSearchWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference()))
                .thenReturn(submitMultipleEvents);
        multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verify(ccdClient, times(1)).retrieveMultipleCasesElasticSearchWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verifyNoMoreInteractions(ccdClient);
    }

    @Test(expected = Exception.class)
    public void retrieveMultipleCasesWithRetriesException() throws IOException {
        when(ccdClient.retrieveMultipleCasesElasticSearchWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference()))
                .thenThrow(new InternalException(ERROR_MESSAGE));
        multipleCasesReadingService.retrieveMultipleCasesWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verify(ccdClient, times(1)).retrieveMultipleCasesElasticSearchWithRetries(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verifyNoMoreInteractions(ccdClient);
    }

    @Test
    public void retrieveMultipleCases() throws IOException {
        when(ccdClient.retrieveMultipleCasesElasticSearch(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference()))
                .thenReturn(submitMultipleEvents);
        multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verify(ccdClient, times(1)).retrieveMultipleCasesElasticSearch(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verifyNoMoreInteractions(ccdClient);
    }

    @Test(expected = Exception.class)
    public void retrieveMultipleCasesException() throws IOException {
        when(ccdClient.retrieveMultipleCasesElasticSearch(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference()))
                .thenThrow(new InternalException(ERROR_MESSAGE));
        multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verify(ccdClient, times(1)).retrieveMultipleCasesElasticSearch(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference());
        verifyNoMoreInteractions(ccdClient);
    }

}