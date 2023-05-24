package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
public class BfActionReportData extends ListingData {
    @JsonIgnore
    private String office;
    @JsonIgnore
    private String reportPeriodDescription;
    @JsonIgnore
    private String reportPrintedOnDescription;
}
