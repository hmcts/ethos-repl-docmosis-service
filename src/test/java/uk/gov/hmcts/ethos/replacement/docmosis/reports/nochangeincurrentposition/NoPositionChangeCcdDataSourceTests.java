package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NoPositionChangeCcdDataSourceTests {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var reportDate = "2021-07-10";
        var ccdClient = mock(CcdClient.class);
        var searchResult = new NoPositionChangeSearchResult();
        searchResult.setCases(List.of(new NoPositionChangeSubmitEvent()));
        when(ccdClient.runElasticSearch(anyString(), anyString(), anyString(), eq(NoPositionChangeSearchResult.class)))
                .thenReturn(searchResult);

        var ccdReportDataSource = new NoPositionChangeCcdDataSource(authToken, ccdClient);
        var results = ccdReportDataSource.getData(caseTypeId, reportDate);

        assertEquals(1, results.size());
        assertEquals(searchResult.getCases().get(0), results.get(0));
    }

    @Test(expected = ReportException.class)
    public void shouldThrowReportExceptionWhenSearchFails() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var reportDate = "2021-07-10";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.runElasticSearch(anyString(), anyString(), anyString(), eq(NoPositionChangeSearchResult.class)))
                .thenThrow(new IOException());

        var ccdReportDataSource = new NoPositionChangeCcdDataSource(authToken, ccdClient);
        ccdReportDataSource.getData(caseTypeId, reportDate);
        fail("Should throw exception instead");
    }

    @Test
    public void shouldReturnMultipleSearchResults() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var ccdClient = mock(CcdClient.class);
        var searchResult = new ArrayList<SubmitMultipleEvent>();
        searchResult.add(new SubmitMultipleEvent());
        when(ccdClient.buildAndGetElasticSearchRequestWithRetriesMultiples(anyString(), anyString(), anyString()))
                .thenReturn(searchResult);

        var ccdReportDataSource = new NoPositionChangeCcdDataSource(authToken, ccdClient);
        var results = ccdReportDataSource.getMultiplesData(caseTypeId, new ArrayList<>());

        assertEquals(1, results.size());
        assertEquals(searchResult.get(0), results.get(0));
    }

    @Test(expected = ReportException.class)
    public void shouldThrowReportExceptionWhenMultipleSearchFails() throws IOException {
        var authToken = "A test token";
        var caseTypeId = "A test case type";
        var ccdClient = mock(CcdClient.class);
        when(ccdClient.buildAndGetElasticSearchRequestWithRetriesMultiples(anyString(), anyString(), anyString()))
                .thenThrow(new IOException());

        var ccdReportDataSource = new NoPositionChangeCcdDataSource(authToken, ccdClient);
        ccdReportDataSource.getMultiplesData(caseTypeId, new ArrayList<>());
        fail("Should throw exception instead");
    }
}
