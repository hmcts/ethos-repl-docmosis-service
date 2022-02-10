package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;

public class RespondentsReportCcdReportDataSourceTest {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "token";
        var caseTypeId = "caseTypeId";
        var fromDate = "1-1-2022";
        var toDate = "10-1-2022";
        var ccdClient = mock(CcdClient.class);
        var submitEvent = new RespondentsReportSubmitEvent();
        var submitEvents = List.of(submitEvent);
        when(ccdClient.respondentsReportSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        var ccdReportDataSource = new RespondentsReportCcdDataSource(authToken, ccdClient);

        var results = ccdReportDataSource.getData(caseTypeId,fromDate, toDate);
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.get(0));
    }

    @Test(expected = ReportException.class)
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        var authToken = "token";
        var caseTypeId = "caseTypeId";
        var fromDate = "1-1-2022";
        var toDate = "10-1-2022";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.respondentsReportSearch(anyString(), anyString(), anyString())).thenThrow(new IOException());

        var ccdReportDataSource = new RespondentsReportCcdDataSource(authToken, ccdClient);
        ccdReportDataSource.getData(caseTypeId, fromDate, toDate);
        fail("Should throw exception instead");
    }

}
