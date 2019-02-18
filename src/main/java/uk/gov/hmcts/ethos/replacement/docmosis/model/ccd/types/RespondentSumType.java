package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespondentSumType {

    @JsonProperty("respondent_name")
    private String respondentName;
    @JsonProperty("respondent_address")
    private Address respondentAddress;

}
