package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClaimantType {

    @JsonProperty("claimant_addressUK")
    private Address claimantAddressUK;
    @JsonProperty("claimant_phone_number")
    private String claimantPhoneNumber;
    @JsonProperty("claimant_mobile_number")
    private String claimantMobileNumber;
    @JsonProperty("claimant_fax_number")
    private String claimantFaxNumber;
    @JsonProperty("claimant_email_address")
    private String claimantEmailAddress;
    @JsonProperty("claimant_contact_preference")
    private String claimantContactPreference;
}
