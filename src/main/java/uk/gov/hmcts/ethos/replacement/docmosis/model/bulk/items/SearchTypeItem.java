package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.SearchType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SearchTypeItem {

    @JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private SearchType value;
}
