package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

@Slf4j
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BfActionReportData extends ListingData {

    @JsonIgnore
    private String office;

    @JsonIgnore
    private String durationDescription;
}
