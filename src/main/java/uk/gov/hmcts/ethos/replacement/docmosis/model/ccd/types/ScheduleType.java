package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.ScheduleAttendanceItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.ScheduleUpdateItem;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ScheduleType {

    @JsonProperty("scheduleClerk")
    private String scheduleClerk;
    @JsonProperty("scheduleJudge")
    private String scheduleJudge;
    @JsonProperty("scheduleOther")
    private String scheduleOther;
    @JsonProperty("scheduleRooms")
    private String scheduleRooms;
    @JsonProperty("scheduleVenues")
    private String scheduleVenues;
    @JsonProperty("scheduleUpdates")
    private List<ScheduleUpdateItem> scheduleUpdates;
    @JsonProperty("scheduleDateTime")
    private String scheduleDateTime;
    @JsonProperty("scheduleDuration")
    private String scheduleDuration;
    @JsonProperty("scheduleTextArea")
    private String scheduleTextArea;
    @JsonProperty("scheduleAttendance")
    private List<ScheduleAttendanceItem> scheduleAttendance;
    @JsonProperty("scheduleConditions")
    private List<String> scheduleConditions;

}
