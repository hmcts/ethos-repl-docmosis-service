package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.ESHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportDataSource;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class CcdReportDataSource implements ReportDataSource {

    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<SubmitEvent> getData(String caseTypeId) {
        try {
            var query = ESHelper.getNotMatchQuery("state", "Closed");
            return ccdClient.executeElasticSearch(authToken, caseTypeId, query);
        } catch (IOException e) {
            throw new ReportException(String.format("Failed to get report data for case type id %s", caseTypeId), e);
        }
    }
}
