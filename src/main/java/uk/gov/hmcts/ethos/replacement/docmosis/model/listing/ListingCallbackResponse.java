package uk.gov.hmcts.ethos.replacement.docmosis.model.listing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.hmcts.ethos.replacement.docmosis.model.generic.GenericCallbackResponse;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingCallbackResponse extends GenericCallbackResponse {

    private ListingData data;
}

