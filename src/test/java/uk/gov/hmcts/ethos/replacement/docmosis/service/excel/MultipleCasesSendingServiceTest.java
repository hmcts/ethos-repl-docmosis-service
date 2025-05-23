package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleCasesSendingServiceTest {

    @Mock
    private CcdClient ccdClient;
    @InjectMocks
    private MultipleCasesSendingService multipleCasesSendingService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private CCDRequest ccdRequest;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        userToken = "authString";
        ccdRequest = new CCDRequest();
    }

    @Test
    public void sendUpdateToMultiple() throws IOException {
        when(ccdClient.startBulkAmendEventForCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseId()))
                .thenReturn(ccdRequest);
        multipleCasesSendingService.sendUpdateToMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseId());
        verify(ccdClient, times(1)).startBulkAmendEventForCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseId());
        verify(ccdClient, times(1)).submitMultipleEventForCase(userToken,
                multipleDetails.getCaseData(),
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                ccdRequest,
                multipleDetails.getCaseId());
        verifyNoMoreInteractions(ccdClient);
    }

    @Test(expected = Exception.class)
    public void sendUpdateToMultipleException() throws IOException {
        when(ccdClient.startBulkAmendEventForCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseId()))
                .thenThrow(new InternalException(ERROR_MESSAGE));
        multipleCasesSendingService.sendUpdateToMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseId());
        verify(ccdClient, times(1)).startBulkAmendEventForCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseId());
        verifyNoMoreInteractions(ccdClient);
    }

}