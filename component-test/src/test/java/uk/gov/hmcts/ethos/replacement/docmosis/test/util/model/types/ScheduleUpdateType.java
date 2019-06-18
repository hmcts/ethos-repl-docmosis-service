package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ScheduleUpdateType {

    @JsonProperty("scheduleUpdateDate")
    private String scheduleUpdateDate;
    @JsonProperty("scheduleUpdateText")
    private String scheduleUpdateText;

}
