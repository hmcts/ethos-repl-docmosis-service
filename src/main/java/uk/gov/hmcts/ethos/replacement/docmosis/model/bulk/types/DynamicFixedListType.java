package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DynamicFixedListType {

    private DynamicValueType value;
    @JsonProperty("list_items")
    private List<DynamicValueType> listItems;

    public DynamicFixedListType(String value) {
        this.value = new DynamicValueType(value, value);
    }

    public DynamicFixedListType() {}
}
