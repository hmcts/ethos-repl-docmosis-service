package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types.HearingType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HearingTypeItem {

    @JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private HearingType value;
}
