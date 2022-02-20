package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SessionDaysCcdReportDataSource implements SessionDaysReportDataSource {

    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<SessionDaysSubmitEvent> getData(String caseTypeId,
                                                String listingDateFrom, String listingDateTo) {
        try {
            var query = SessionDaysElasticSearchQuery.create(listingDateFrom, listingDateTo);
            return ccdClient.sessionDaysSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                    "Failed to get session days search results for case type id %s", caseTypeId), e);
        }
    }
}
