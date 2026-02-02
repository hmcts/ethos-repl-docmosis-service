package uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.eccreport.EccReportSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EccReportCcdReportDataSourceTest {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "token";
        var caseTypeId = "caseTypeId";
        var fromDate = "1-1-2022";
        var toDate = "10-1-2022";
        var ccdClient = mock(CcdClient.class);
        var submitEvent = new EccReportSubmitEvent();
        var submitEvents = List.of(submitEvent);
        when(ccdClient.eccReportSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        var ccdReportDataSource = new EccReportCcdDataSource(authToken, ccdClient);

        var results = ccdReportDataSource.getData(caseTypeId, fromDate, toDate);
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.getFirst());
    }

    @Test(expected = ReportException.class)
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        var authToken = "token";
        var caseTypeId = "caseTypeId";
        var fromDate = "1-1-2022";
        var toDate = "10-1-2022";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.eccReportSearch(anyString(), anyString(), anyString())).thenThrow(new IOException());

        var ccdReportDataSource = new EccReportCcdDataSource(authToken, ccdClient);
        ccdReportDataSource.getData(caseTypeId, fromDate, toDate);
        fail("Should throw exception instead");
    }

}
