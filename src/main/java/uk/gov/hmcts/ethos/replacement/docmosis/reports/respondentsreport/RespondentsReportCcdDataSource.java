package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class RespondentsReportCcdDataSource implements RespondentsReportDataSource {
    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<RespondentsReportSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo) {
        try {
            var query = RespondentsReportElasticSearchQuery.create(listingDateFrom, listingDateTo);
            return ccdClient.respondentsReportSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                    "Failed to get Respondent Report search results for case type id %s", caseTypeId), e);
        }
    }
}
