package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;

@RequiredArgsConstructor
@Slf4j
public class RefDataFixesCcdDataSource implements RefDataFixesDataSource {

    private final String authToken;

    @Override
    public List<SubmitEvent> getData(String caseTypeId, String dateFrom, String dateTo, CcdClient ccdClient) {
        try {
            var query = RefDataFixesElasticSearchQuery.create(
                    dateFrom, dateTo);
            return ccdClient.executeElasticSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                    "Failed to get hearings by hearing type search results for case type id %s", caseTypeId), e);
        }
    }
}
