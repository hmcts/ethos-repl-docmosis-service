package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TribunalOfficeVenueType {

    @JsonProperty("associatedTribunalOffice")
    private String associatedTribunalOffice;
    @JsonProperty("venueNameManchester")
    private String venueNameManchester;
    @JsonProperty("venueNameScotland")
    private String venueNameScotland;
    @JsonProperty("venueNameLondonSouth")
    private String venueNameLondonSouth;
    @JsonProperty("venueNameLondonCentral")
    private String venueNameLondonCentral;
    @JsonProperty("venueNameLondonEast")
    private String venueNameLondonEast;
    @JsonProperty("venueNameNewcastle")
    private String venueNameNewcastle;
    @JsonProperty("venueNameLeeds")
    private String venueNameLeeds;
    @JsonProperty("venueNameBristol")
    private String venueNameBristol;
    @JsonProperty("venueNameCardiff")
    private String venueNameCardiff;
    @JsonProperty("venueNameWatford")
    private String venueNameWatford;
    @JsonProperty("venueNameEastMidlads")
    private String venueNameEastMidlads;
    @JsonProperty("venueNameWestMidlands")
    private String venueNameWestMidlands;
}
