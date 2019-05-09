package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClaimantOtherType {

    @JsonProperty("claimant_occupation")
    private String claimantOccupation;
    @JsonProperty("claimant_employed_from")
    private String claimantEmployedFrom;
    @JsonProperty("claimant_employed_currently")
    private String claimantEmployedCurrently;
    @JsonProperty("claimant_employed_to")
    private String claimantEmployedTo;
    @JsonProperty("claimant_employed_notice_period")
    private String claimantEmployedNoticePeriod;
    @JsonProperty("claimant_disabled")
    private String claimantDisabled;
    @JsonProperty("claimant_disabled_details")
    private String claimantDisabledDetails;
}
