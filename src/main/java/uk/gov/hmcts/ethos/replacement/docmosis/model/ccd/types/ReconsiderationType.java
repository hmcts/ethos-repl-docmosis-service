package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ReconsiderationType {

    @JsonProperty("application_date")
    private String applicationDate;
    @JsonProperty("who_applied?")
    private String whoApplied;
    @JsonProperty("judge's_direction")
    private String judgeDirection;
    @JsonProperty("reconsideration_hearing_date")
    private String reconsiderationHearingDate;
    @JsonProperty("judge's_decision")
    private String judgeDecision;

}
