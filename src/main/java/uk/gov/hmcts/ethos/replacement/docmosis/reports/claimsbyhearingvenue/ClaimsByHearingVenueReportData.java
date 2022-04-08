package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClaimsByHearingVenueReportData extends ListingData {
    @JsonIgnore
    private String office;
    @JsonIgnore
    private String reportPeriodDescription;
    @JsonIgnore
    private String reportPrintedOnDescription;
    @JsonIgnore
    private final List<ClaimsByHearingVenueReportDetail> reportDetails = new ArrayList<>();
}
