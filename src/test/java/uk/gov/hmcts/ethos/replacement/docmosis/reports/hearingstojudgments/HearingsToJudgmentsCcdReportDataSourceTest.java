package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.hearingstojudgments.HearingsToJudgmentsSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HearingsToJudgmentsCcdReportDataSourceTest {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var fromDate = "10-10-2021";
        var toDate = "10-11-2021";
        var ccdClient = mock(CcdClient.class);
        var submitEvent = new HearingsToJudgmentsSubmitEvent();
        var submitEvents = List.of(submitEvent);
        when(ccdClient.hearingsToJudgementsSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        var ccdReportDataSource = new HearingsToJudgmentsCcdReportDataSource(authToken, ccdClient);

        var results = ccdReportDataSource.getData(caseTypeId, fromDate, toDate);
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.getFirst());
    }

    @Test(expected = ReportException.class)
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var fromDate = "10-10-2021";
        var toDate = "10-11-2021";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.hearingsToJudgementsSearch(anyString(), anyString(), anyString())).thenThrow(new IOException());

        var ccdReportDataSource = new HearingsToJudgmentsCcdReportDataSource(authToken, ccdClient);
        ccdReportDataSource.getData(caseTypeId, fromDate, toDate);
        fail("Should throw exception instead");
    }

}
