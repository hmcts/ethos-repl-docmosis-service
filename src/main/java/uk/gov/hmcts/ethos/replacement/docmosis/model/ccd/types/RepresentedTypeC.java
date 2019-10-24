package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RepresentedTypeC {

    @JsonProperty("name_of_representative")
    private String nameOfRepresentative;
    @JsonProperty("name_of_organisation")
    private String nameOfOrganisation;
    @JsonProperty("representative_reference")
    private String representativeReference;
    @JsonProperty("representative_occupation")
    private String representativeOccupation;
    @JsonProperty("representative_occupation_other")
    private String representativeOccupationOther;
    @JsonProperty("representative_address")
    private Address representativeAddress;
    @JsonProperty("representative_phone_number")
    private String representativePhoneNumber;
    @JsonProperty("representative_mobile_number")
    private String representativeMobileNumber;
    @JsonProperty("representative_email_address")
    private String representativeEmailAddress;
    @JsonProperty("representative_preference")
    private String representativePreference;

}
