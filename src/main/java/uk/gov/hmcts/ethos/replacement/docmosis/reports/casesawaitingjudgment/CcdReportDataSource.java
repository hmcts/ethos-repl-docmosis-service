package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.casesawaitingjudgment.CasesAwaitingJudgmentSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CcdReportDataSource implements ReportDataSource {

    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<CasesAwaitingJudgmentSubmitEvent> getData(String caseTypeId) {
        try {
            var query = ElasticSearchQuery.create();
            return ccdClient.casesAwaitingJudgmentSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                    "Failed to get Cases Awaiting Judgment search results for case type id %s", caseTypeId), e);
        }
    }
}
