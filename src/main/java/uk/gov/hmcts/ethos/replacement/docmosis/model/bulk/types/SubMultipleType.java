package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SubMultipleType {

    @JsonProperty("subMultipleNameT")
    private String subMultipleNameT;
    @JsonProperty("subMultipleRefT")
    private String subMultipleRefT;
}
