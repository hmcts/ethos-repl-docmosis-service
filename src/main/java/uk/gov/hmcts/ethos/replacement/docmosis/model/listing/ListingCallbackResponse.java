package uk.gov.hmcts.ethos.replacement.docmosis.model.listing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SignificantItem;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingCallbackResponse {

    private ListingData data;
    private List<String> errors;
    private List<String> warnings;
    private String confirmation_header;
    private SignificantItem significant_item;
}

