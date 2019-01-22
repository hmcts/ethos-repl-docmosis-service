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
public class RespondentType {
    @JsonProperty("respondentName") private String respondentName;
    @JsonProperty("respondentAddress") private String respondentAddress;
}