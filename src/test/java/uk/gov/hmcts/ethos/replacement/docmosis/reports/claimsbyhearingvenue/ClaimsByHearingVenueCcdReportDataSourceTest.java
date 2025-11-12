package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClaimsByHearingVenueCcdReportDataSourceTest {
    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "test token";
        var caseTypeId = "test caseTypeId";
        var fromDate = "2021-12-13";
        var toDate = "2021-12-27";
        var ccdClient = mock(CcdClient.class);
        var submitEventOne = new ClaimsByHearingVenueSubmitEvent();
        var submitEventTwo = new ClaimsByHearingVenueSubmitEvent();

        var submitEvents = List.of(submitEventOne, submitEventTwo);
        when(ccdClient.claimsByHearingVenueSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);
        var ccdReportDataSource = new ClaimsByHearingVenueCcdReportDataSource(authToken, ccdClient);
        var results = ccdReportDataSource.getData(caseTypeId,fromDate, toDate);
        assertEquals(2, results.size());
        assertEquals(submitEventOne, results.get(0));
        assertEquals(submitEventTwo, results.get(1));
    }

    @Test
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        var authToken = "test token";
        var caseTypeId = "Test_caseTypeId";
        var fromDate = "2021-12-13";
        var toDate = "2021-12-27";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.claimsByHearingVenueSearch(anyString(), anyString(), anyString()))
            .thenThrow(new IOException());
        var ccdReportDataSource = new ClaimsByHearingVenueCcdReportDataSource(authToken, ccdClient);
        var exception = assertThrows(ReportException.class, () -> {
            ccdReportDataSource.getData(caseTypeId, fromDate, toDate);
        });

        String expectedMessage = "Failed to get claims by hearing venue search results "
        + "for case type id Test_caseTypeId";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}
