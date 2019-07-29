package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitBulkEvent {
    @JsonProperty("id")
    private long caseId;

    @JsonProperty("case_data")
    private BulkData caseData;

    @JsonProperty("security_classification")
    private String securityClassification;

    @JsonProperty("state")
    private String state;
}
