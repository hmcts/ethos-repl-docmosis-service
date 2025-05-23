package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HearingsByHearingTypeCcdReportDataSourceTest {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "token";
        var caseTypeId = "caseTypeId_Listings";
        var fromDate = "1-1-2022";
        var toDate = "10-1-2022";
        var ccdClient = mock(CcdClient.class);
        var submitEvent = new HearingsByHearingTypeSubmitEvent();
        var submitEvents = List.of(submitEvent);
        when(ccdClient.hearingsByHearingTypeSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        var ccdReportDataSource = new HearingsByHearingTypeCcdReportDataSource(authToken, ccdClient);

        var results = ccdReportDataSource.getData(new ReportParams(caseTypeId,fromDate, toDate));
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.get(0));
    }

    @Test(expected = ReportException.class)
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        var authToken = "token";
        var caseTypeId = "caseTypeId_Listings";
        var fromDate = "1-1-2022";
        var toDate = "10-1-2022";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.hearingsByHearingTypeSearch(anyString(), anyString(), anyString())).thenThrow(new IOException());

        var ccdReportDataSource = new HearingsByHearingTypeCcdReportDataSource(authToken, ccdClient);
        ccdReportDataSource.getData(new ReportParams(caseTypeId, fromDate, toDate));
        fail("Should throw exception instead");
    }

}
