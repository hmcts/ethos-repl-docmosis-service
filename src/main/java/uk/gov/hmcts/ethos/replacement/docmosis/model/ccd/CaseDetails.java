package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CaseDetails {

    @JsonProperty("id")
    private String caseId;
    @JsonProperty("jurisdiction")
    private String jurisdiction;
    @JsonProperty("state")
    private String state;
    @JsonProperty("case_data")
    private CaseData caseData;

//    @JsonProperty("data_classification")
//    private Map<String, JsonNode> dataClassification;
}
