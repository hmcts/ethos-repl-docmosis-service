package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

@Slf4j
@Getter
@Setter
public class BfActionReportData extends ListingData {

    @JsonIgnore
    public String office;

    @JsonIgnore
    public String durationDescription;

}
