package uk.gov.hmcts.ethos.replacement.docmosis.model.generic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericSubmitEvent {
    @JsonProperty("id")
    private long caseId;

    @JsonProperty("security_classification")
    private String securityClassification;

    @JsonProperty("state")
    private String state;
}
