package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClaimantOtherType {

    @JsonProperty("claimant_workplace")
    private String claimantWorkPlace;
    @JsonProperty("claimant_occupation")
    private String claimantOccupation;
    @JsonProperty("claimant_employed_from")
    private String claimantEmployedFrom;
    @JsonProperty("claimant_employed_currently")
    private String claimantEmployedCurrently;
    @JsonProperty("claimant_employed_to")
    private String claimantEmployedTo;
    @JsonProperty("claimant_work_address")
    private Address claimantWorkAddress;
    @JsonProperty("claimant_work_phone_number")
    private String claimantWorkPhoneNumber;
}
