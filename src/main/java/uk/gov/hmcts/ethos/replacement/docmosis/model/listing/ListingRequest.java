package uk.gov.hmcts.ethos.replacement.docmosis.model.listing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ethos.replacement.docmosis.model.generic.GenericRequest;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingRequest extends GenericRequest {

    @JsonProperty("case_details")
    private ListingDetails caseDetails;
}
