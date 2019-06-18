package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types.JurCodesType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class JurCodesTypeItem {

    @JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private JurCodesType value;
}
