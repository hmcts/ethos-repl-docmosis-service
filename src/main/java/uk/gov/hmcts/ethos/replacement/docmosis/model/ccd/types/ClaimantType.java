package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClaimantType {

    @JsonProperty("claimant_title1")
    private String claimantTitle;
    @JsonProperty("claimant_title_other")
    private String claimantTitleOther;
    @JsonProperty("claimant_forenames")
    private String claimantLastName;
    @JsonProperty("claimant_surname")
    private String claimantFirstName;
    @JsonProperty("claimant_date_of_birth")
    private String claimantDateOfBirth;
    @JsonProperty("claimant_gender")
    private String claimantGender;
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

    public String getClaimantName() {
        return String.join(" ", claimantTitle, claimantFirstName, claimantLastName);
    }
}
