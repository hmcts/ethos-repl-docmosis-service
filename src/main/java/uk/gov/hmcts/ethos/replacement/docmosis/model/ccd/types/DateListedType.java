package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DateListedType {

    @JsonProperty("listedDate")
    private String listedDate;
    @JsonProperty("Hearing_status")
    private String hearingStatus;
    @JsonProperty("Postponed_by")
    private String postponedBy;
    @JsonProperty("Hearing_typeReadingDeliberation")
    private String hearingTypeReadingDeliberation;
    @JsonProperty("hearingVenueDay")
    private String hearingVenueDay;
    @JsonProperty("Hearing_room_M")
    private String hearingRoomM;
    @JsonProperty("Hearing_room_L")
    private String hearingRoomL;
    @JsonProperty("Hearing_room_CM")
    private String hearingRoomCM;
    @JsonProperty("Hearing_room_CC")
    private String hearingRoomCC;
    @JsonProperty("Hearing_Glasgow")
    private String hearingGlasgow;
    @JsonProperty("Hearing_Aberdeen")
    private String hearingAberdeen;
    @JsonProperty("Hearing_Dundee")
    private String hearingDundee;
    @JsonProperty("Hearing_Edinburgh")
    private String hearingEdinburgh;
    @JsonProperty("Hearing_room_Glasgow")
    private String hearingRoomGlasgow;
    @JsonProperty("Hearing_room_Cambeltown")
    private String hearingRoomCambeltown;
    @JsonProperty("Hearing_room_Dumfries")
    private String hearingRoomDumfries;
    @JsonProperty("Hearing_room_Oban")
    private String hearingRoomOban;
    @JsonProperty("Hearing_room_FortWilliam")
    private String hearingRoomFortWilliam;
    @JsonProperty("Hearing_room_Kirkcudbright")
    private String hearingRoomKirkcubright;
    @JsonProperty("Hearing_room_Lochmaddy")
    private String hearingRoomLockmaddy;
    @JsonProperty("Hearing_room_Portree")
    private String hearingRoomPortree;
    @JsonProperty("Hearing_room_Stirling")
    private String hearingRoomStirling;
    @JsonProperty("Hearing_room_StornowaySC")
    private String hearingRoomStornowaySC;
    @JsonProperty("Hearing_room_Stranraer")
    private String hearingRoomStranraer;
    @JsonProperty("Hearing_room_Aberdeen")
    private String hearingRoomAberdeen;
    @JsonProperty("Hearing_room_Lerwick")
    private String hearingRoomLerwick;
    @JsonProperty("Hearing_room_RRShetland")
    private String hearingRoomRRShetland;
    @JsonProperty("Hearing_room_Stornoway")
    private String hearingRoomStornoway;
    @JsonProperty("Hearing_room_Wick")
    private String hearingRoomWick;
    @JsonProperty("Hearing_room_Inverness")
    private String hearingRoomInverness;
    @JsonProperty("Hearing_room_Kirkwall")
    private String hearingRoomKirkawall;
    @JsonProperty("Hearing_judge_name")
    private String hearingJudgeName;
    @JsonProperty("Hearing_clerk")
    private String hearingClerk;
    @JsonProperty("hearingCaseDisposed")
    private String hearingCaseDisposed;
    @JsonProperty("Hearing_part_heard")
    private String hearingPartHeard;
    @JsonProperty("Hearing_reserved_judgement")
    private String hearingReservedJudgement;
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
    @JsonProperty("hearingTimingStart")
    private String hearingTimingStart;
    @JsonProperty("hearingTimingBreak")
    private String hearingTimingBreak;
    @JsonProperty("hearingTimingResume")
    private String hearingTimingResume;
    @JsonProperty("hearingTimingFinish")
    private String hearingTimingFinish;
    @JsonProperty("hearingTimingDuration")
    private String hearingTimingDuration;
    @JsonProperty("HearingNotes2")
    private String hearingNotes2;

}
