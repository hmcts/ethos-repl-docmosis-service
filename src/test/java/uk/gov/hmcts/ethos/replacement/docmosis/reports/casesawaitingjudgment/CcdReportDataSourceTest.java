package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CcdReportDataSourceTest {

    @Test
    public void shouldReturnSearchResults() throws IOException {
        var authToken = "A test token";
        var ccdClient = mock(CcdClient.class);
        var submitEvent = new SubmitEvent();
        var submitEvents = List.of(submitEvent);
        when(ccdClient.executeElasticSearch(anyString(), anyString(), anyString())).thenReturn(submitEvents);

        var ccdReportDataSource = new CcdReportDataSource(authToken, ccdClient);

        var results = ccdReportDataSource.getData("TestCaseType");
        assertEquals(1, results.size());
        assertEquals(submitEvent, results.get(0));
    }

}
