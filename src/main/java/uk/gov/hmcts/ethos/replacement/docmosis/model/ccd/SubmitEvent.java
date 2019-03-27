package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitEvent {
    @JsonProperty("id")
    private long caseId;

    @JsonProperty("case_data")
    private CaseData caseData;

    @JsonProperty("security_classification")
    private String securityClassification;

    @JsonProperty("state")
    private String state;
}
