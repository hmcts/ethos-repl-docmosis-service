package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DisposeType {

    @JsonProperty("disposeReason")
    private String disposeReason;
    @JsonProperty("disposeTextArea")
    private String disposeTextArea;
}
