package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClaimantType {

    @JsonProperty("claimant_addressUK")
    private Address claimantAddressUK;
    @JsonProperty("claimant_phone_number")
    private String claimantPhoneNumber;
    @JsonProperty("claimant_mobile_number")
    private String claimantMobileNumber;
    @JsonProperty("claimant_email_address")
    private String claimantEmailAddress;
    @JsonProperty("claimant_contact_preference")
    private String claimantContactPreference;
}
