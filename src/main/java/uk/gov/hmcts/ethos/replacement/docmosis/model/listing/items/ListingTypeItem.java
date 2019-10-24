package uk.gov.hmcts.ethos.replacement.docmosis.model.listing.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.types.ListingType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ListingTypeItem {

    @JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private ListingType value;
}
