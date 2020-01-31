package uk.gov.hmcts.ethos.replacement.docmosis.model.listing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.hmcts.ethos.replacement.docmosis.model.generic.GenericCaseDetails;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ListingDetails extends GenericCaseDetails {

    @JsonProperty("case_data")
    private ListingData caseData;
}
