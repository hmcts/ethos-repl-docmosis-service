package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class JudgementType {

    @JsonProperty("judgementFinal")
    private String judgementFinal;
    @JsonProperty("judgementTextArea")
    private String judgementTextArea;
}
