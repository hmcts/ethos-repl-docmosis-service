package uk.gov.hmcts.ethos.replacement.docmosis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimantType {
    @JsonProperty("claimantTitle") private String claimantTitle;
    @JsonProperty("claimantFirstName") private String claimantFirstName;
    @JsonProperty("claimantInitials") private String claimantInitials;
    @JsonProperty("claimantLastName") private String claimantLastName;
    @JsonProperty("claimantDateOfBirth") private String claimantDateOfBirth;
    @JsonProperty("claimantGender") private String claimantGender;
    @JsonProperty("claimantAddressUK") private String claimantAddressUK;
    @JsonProperty("claimantPhoneNumber") private String claimantPhoneNumber;
    @JsonProperty("claimantMobileNumber") private String claimantMobileNumber;
    @JsonProperty("claimantFaxNumber") private String claimantFaxNumber;
    @JsonProperty("claimantEmailAddress") private String claimantEmailAddress;
    @JsonProperty("claimantContactPreference") private String claimantContactPreference;
}
