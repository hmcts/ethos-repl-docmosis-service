package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HearingType {

    @JsonProperty("Hearing_list_or_allocate")
    private String hearingListOrAllocated;
    @JsonProperty("Hearing_date")
    private String hearingDate;
    @JsonProperty("HearingType")
    private String hearingType;
    @JsonProperty("Est_Hearing")
    private EstHearingType estHearing;
    @JsonProperty("Hearing_sit_alone")
    private String hearingSitAlone;
    @JsonProperty("Hearing_notes")
    private String hearingNotes;
    @JsonProperty("Hearing_stage")
    private String hearingStage;
    @JsonProperty("Hearing_part_heard")
    private String hearingPartHeard;
    @JsonProperty("Hearing_reserved_judgement")
    private String hearingReservedJudgement;
    @JsonProperty("Hearing_judgement_in_default")
    private String hearingJudgementInDefault;
    @JsonProperty("Hearing_judge_name")
    private String hearingJudgeName;
    @JsonProperty("Postponed_by")
    private String postponedBy;
    @JsonProperty("Hearing_empr_member")
    private String hearingEmprMember;
    @JsonProperty("Hearing_empe_member")
    private String hearingEmpeMember;
    @JsonProperty("Hearing_attendance")
    private AttendeeType hearingAttendance;
    @JsonProperty("Hearing_judgment_reference")
    private String hearingJudgmentReference;

}
