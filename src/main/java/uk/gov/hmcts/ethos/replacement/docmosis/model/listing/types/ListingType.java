package uk.gov.hmcts.ethos.replacement.docmosis.model.listing.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ListingType {

    @JsonProperty("causeListDate")
    private String causeListDate;
    @JsonProperty("causeListTime")
    private String causeListTime;
    @JsonProperty("causeListVenue")
    private String causeListVenue;
    @JsonProperty("elmoCaseReference")
    private String elmoCaseReference;
    @JsonProperty("jurisdictionCodesList")
    private String jurisdictionCodesList;
    @JsonProperty("hearingType")
    private String hearingType;
    @JsonProperty("positionType")
    private String positionType;
    @JsonProperty("hearingJudgeName")
    private String hearingJudgeName;
    @JsonProperty("hearingEEMember")
    private String hearingEEMember;
    @JsonProperty("hearingERMember")
    private String hearingERMember;
    @JsonProperty("clerkResponsible")
    private String clerkResponsible;
    @JsonProperty("hearingDay")
    private String hearingDay;
    @JsonProperty("claimantName")
    private String claimantName;
    @JsonProperty("claimantTown")
    private String claimantTown;
    @JsonProperty("claimantRepresentative")
    private String claimantRepresentative;
    @JsonProperty("respondent")
    private String respondent;
    @JsonProperty("respondentTown")
    private String respondentTown;
    @JsonProperty("respondentRepresentative")
    private String respondentRepresentative;
    @JsonProperty("estHearingLength")
    private String estHearingLength;
    @JsonProperty("Hearing_panel")
    private String hearingPanel;

    @JsonProperty("Hearing_room")
    private String hearingRoom;
    @JsonProperty("resp_others")
    private String respondentOthers;
    @JsonProperty("Hearing_notes")
    private String hearingNotes;

}
