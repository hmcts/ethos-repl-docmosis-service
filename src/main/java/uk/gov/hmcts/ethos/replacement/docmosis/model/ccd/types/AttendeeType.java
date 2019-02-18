package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AttendeeType {

    @JsonProperty("attendee_claimant")
    private String attendeeClaimant;
    @JsonProperty("attendee_non_attendees")
    private String attendeeNonAttendees;
    @JsonProperty("attendee_resp_no_rep")
    private String attendeeRespNoRep;
    @JsonProperty("attendee_resp_&_rep")
    private String attendeeRespAndRep;
    @JsonProperty("attendee_rep_only")
    private String attendeeRepOnly;
}
