package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import java.util.List;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;

public interface ClaimsByHearingVenueReportDataSource {
    List<ClaimsByHearingVenueSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo);
}
