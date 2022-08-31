package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

@RequiredArgsConstructor
@Slf4j
public class RefDataFixesCcdDataSource implements RefDataFixesDataSource {

    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<SubmitEvent> getData(ReportParams reportParams) {
        var caseTypeId = Strings.split(reportParams.getCaseTypeId(), "_")[0];
        try {
            var query = RefDataFixesElasticSearchQuery.create(
                    reportParams.getDateFrom(), reportParams.getDateTo());
            return ccdClient.executeElasticSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                    "Failed to get hearings by hearing type search results for case type id %s", caseTypeId), e);
        }
    }
}
