package uk.gov.hmcts.ethos.replacement.docmosis.model.listing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.items.ListingTypeItem;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ListingData {

    @JsonProperty("tribunalCorrespondenceAddress")
    private Address tribunalCorrespondenceAddress;
    @JsonProperty("tribunalCorrespondenceTelephone")
    private String tribunalCorrespondenceTelephone;
    @JsonProperty("tribunalCorrespondenceFax")
    private String tribunalCorrespondenceFax;
    @JsonProperty("tribunalCorrespondenceDX")
    private String tribunalCorrespondenceDX;
    @JsonProperty("tribunalCorrespondenceEmail")
    private String tribunalCorrespondenceEmail;
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
    @JsonProperty("listingCollection")
    private List<ListingTypeItem> listingCollection;
    @JsonProperty("listingVenueOfficeGlas")
    private String listingVenueOfficeGlas;
    @JsonProperty("listingVenueOfficeAber")
    private String listingVenueOfficeAber;
    @JsonProperty("hearingDocType")
    private String hearingDocType;
    @JsonProperty("hearingDocETCL")
    private String hearingDocETCL;
    @JsonProperty("roomOrNoRoom")
    private String roomOrNoRoom;
    @JsonProperty("docMarkUp")
    private String docMarkUp;
}

