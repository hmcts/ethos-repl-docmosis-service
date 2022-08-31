package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RefDataFixesData {
    @JsonProperty("hearingDateType")
    private String hearingDateType;
    @JsonProperty("listingDate")
    private String listingDate;
    @JsonProperty("listingDateFrom")
    private String listingDateFrom;
    @JsonProperty("listingDateTo")
    private String listingDateTo;
    @JsonProperty("listingVenue")
    private String listingVenue;
    @JsonProperty("refDataType")
    private String refDataType;
}

