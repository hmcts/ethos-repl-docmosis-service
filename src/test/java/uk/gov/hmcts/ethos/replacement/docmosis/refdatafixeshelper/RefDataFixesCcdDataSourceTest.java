package uk.gov.hmcts.ethos.replacement.docmosis.refdatafixeshelper;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.RefDataFixesCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.RefDataFixesException;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RefDataFixesCcdDataSourceTest {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        String authToken = "token";
        String caseTypeId = "caseTypeId";
        String fromDate = "1-1-2022";
        String toDate = "10-1-2022";
        CcdClient ccdClient = mock(CcdClient.class);
        SubmitEvent submitEvent = new SubmitEvent();
        List<SubmitEvent> submitEvents = List.of(submitEvent);
        when(ccdClient.executeElasticSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(authToken);
        List<SubmitEvent> results = dataSource.getDataForJudges(caseTypeId, fromDate, toDate, ccdClient);
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.getFirst());
    }

    @Test(expected = RefDataFixesException.class)
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        String authToken = "token";
        String caseTypeId = "caseTypeId";
        String fromDate = "1-1-2022";
        String toDate = "10-1-2022";
        CcdClient ccdClient = mock(CcdClient.class);
        when(ccdClient.executeElasticSearch(anyString(), anyString(), anyString())).thenThrow(new IOException());
        RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(authToken);
        dataSource.getDataForJudges(caseTypeId, fromDate, toDate, ccdClient);
        fail("Should throw exception instead");
    }

    @Test
    public void shouldReturnSearchResultsForInsertClaimServedDate() throws IOException {
        String authToken = "token";
        String caseTypeId = "caseTypeId";
        String fromDate = "1-1-2022";
        String toDate = "10-1-2022";
        CcdClient ccdClient = mock(CcdClient.class);
        SubmitEvent submitEvent = new SubmitEvent();
        List<SubmitEvent> submitEvents = List.of(submitEvent);
        when(ccdClient.executeElasticSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(authToken);
        List<SubmitEvent> results = dataSource.getDataForInsertClaimDate(caseTypeId, fromDate, toDate, ccdClient);
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.getFirst());
    }

    @Test(expected = RefDataFixesException.class)
    public void shouldThrowReportExceptionWhenSearchFailsForInsertClaimServedDate() throws IOException {
        String authToken = "token";
        String caseTypeId = "caseTypeId";
        String fromDate = "1-1-2022";
        String toDate = "10-1-2022";
        CcdClient ccdClient = mock(CcdClient.class);
        when(ccdClient.executeElasticSearch(anyString(), anyString(), anyString())).thenThrow(new IOException());
        RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(authToken);
        dataSource.getDataForInsertClaimDate(caseTypeId, fromDate, toDate, ccdClient);
        fail("Should throw exception instead");
    }

}
