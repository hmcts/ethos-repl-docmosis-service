package uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class ContactDetails {
    @JsonProperty("address1")
    private String address1;

    @JsonProperty("address2")
    private String address2;

    @JsonProperty("address3")
    private String address3;

    @JsonProperty("town")
    private String town;

    @JsonProperty("postcode")
    private String postcode;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("fax")
    private String fax;

    @JsonProperty("dx")
    private String dx;

    @JsonProperty("email")
    private String email;

    @JsonProperty("managing-office")
    private String managingOffice;
}
