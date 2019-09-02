package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.BroughtForwardDatesType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BroughtForwardDatesTypeItem {

    @JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private BroughtForwardDatesType value;
}
