package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import java.util.List;

public interface ClaimsByHearingVenueReportDataSource {
    List<ClaimsByHearingVenueSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo);
}
