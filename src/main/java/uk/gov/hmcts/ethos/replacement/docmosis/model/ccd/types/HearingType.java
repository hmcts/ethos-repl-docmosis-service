package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HearingType {

    @JsonProperty("Hearing_status")
    private String hearingStatus;
    @JsonProperty("Postponed_by")
    private String postponedBy;
    @JsonProperty("Hearing_venue")
    private String hearingVenue;
    @JsonProperty("Hearing_date_start")
    private String hearingDateStart;
    @JsonProperty("Hearing_date_finish")
    private String hearingDateFinish;
    @JsonProperty("Hearing_type")
    private String hearingType;
    @JsonProperty("Est_Hearing")
    private EstHearingType estHearing;
    @JsonProperty("hearingSitAlone")
    private String hearingSitAlone;
    @JsonProperty("Hearing_allocate")
    private String hearingAllocate;
    @JsonProperty("Hearing_stage")
    private String hearingStage;
    @JsonProperty("Hearing_room_M")
    private String hearingRoomM;
    @JsonProperty("Hearing_room_L")
    private String hearingRoomL;
    @JsonProperty("Hearing_room_CM")
    private String hearingRoomCM;
    @JsonProperty("Hearing_room_CC")
    private String hearingRoomCC;
    @JsonProperty("Hearing_judge_name")
    private String hearingJudgeName;
    @JsonProperty("Hearing_empr_member")
    private String hearingEmprMember;
    @JsonProperty("Hearing_empe_member")
    private String hearingEmpeMember;
    @JsonProperty("Hearing_clerk")
    private String hearingClerk;
    @JsonProperty("hearing_post")
    private String hearingPost;
    @JsonProperty("Hearing_part_heard")
    private String hearingPartHeard;
    @JsonProperty("Hearing_reserved_judgement")
    private String hearingReservedJudgement;
    @JsonProperty("Hearing_attendance")
    private AttendeeType hearingAttendance;
    @JsonProperty("Hearing_notes")
    private String hearingNotes;

}
