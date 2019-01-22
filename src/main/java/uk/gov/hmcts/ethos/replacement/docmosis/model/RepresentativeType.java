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
public class RepresentativeType {
    @JsonProperty("nameOfRepresentative") private String nameOfRepresentative;
    @JsonProperty("nameOfOrganisation") private String nameOfOrganisation;
    @JsonProperty("representativeAddress") private String representativeAddress;
    @JsonProperty("representativePhoneNumber") private String representativePhoneNumber;
    @JsonProperty("representativeFaxNumber") private String representativeFaxNumber;
    @JsonProperty("representativeDxNumber") private String representativeDxNumber;
    @JsonProperty("representativeEmailAddress") private String representativeEmailAddress;
    @JsonProperty("representativeReference") private String representativeReference;
}
