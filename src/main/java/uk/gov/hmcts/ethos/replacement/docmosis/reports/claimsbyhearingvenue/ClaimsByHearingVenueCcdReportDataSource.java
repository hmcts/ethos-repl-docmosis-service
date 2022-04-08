package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ClaimsByHearingVenueCcdReportDataSource implements ClaimsByHearingVenueReportDataSource {

    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<ClaimsByHearingVenueSubmitEvent> getData(String caseTypeId,
                                                         String listingDateFrom, String listingDateTo) {
        try {
            var query = ClaimsByHearingVenueESQuery.create(listingDateFrom, listingDateTo);
            return ccdClient.claimsByHearingVenueSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                "Failed to get claims by hearing venue search results for case type id %s", caseTypeId), e);
        }
    }
}
