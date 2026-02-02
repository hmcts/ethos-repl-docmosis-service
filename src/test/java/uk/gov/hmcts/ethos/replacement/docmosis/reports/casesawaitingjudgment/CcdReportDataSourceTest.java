package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.casesawaitingjudgment.CasesAwaitingJudgmentSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CcdReportDataSourceTest {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var ccdClient = mock(CcdClient.class);
        var submitEvent = new CasesAwaitingJudgmentSubmitEvent();
        var submitEvents = List.of(submitEvent);
        when(ccdClient.casesAwaitingJudgmentSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        var ccdReportDataSource = new CcdReportDataSource(authToken, ccdClient);

        var results = ccdReportDataSource.getData(caseTypeId);
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.getFirst());
    }

    @Test(expected = ReportException.class)
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.casesAwaitingJudgmentSearch(anyString(), anyString(), anyString())).thenThrow(new IOException());

        var ccdReportDataSource = new CcdReportDataSource(authToken, ccdClient);
        ccdReportDataSource.getData(caseTypeId);
        fail("Should throw exception instead");
    }

}
