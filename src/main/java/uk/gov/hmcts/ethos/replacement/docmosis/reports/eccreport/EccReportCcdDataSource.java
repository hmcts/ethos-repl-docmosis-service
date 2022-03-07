package uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.eccreport.EccReportSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class EccReportCcdDataSource implements EccReportDataSource {
    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<EccReportSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo) {
        try {
            var query = EccReportElasticSearchQuery.create(listingDateFrom, listingDateTo);
            return ccdClient.eccReportSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                    "Failed to get Ecc Report search results for case type id %s", caseTypeId), e);
        }
    }
}
