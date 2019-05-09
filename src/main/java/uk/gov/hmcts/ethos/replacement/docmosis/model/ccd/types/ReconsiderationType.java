package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ReconsiderationType {

    @JsonProperty("judgment_number")
    private String judgmentNumber;
    @JsonProperty("nonHearing")
    private String nonHearing;
    @JsonProperty("response_dateET1")
    private String responseDateET1;
    @JsonProperty("response_dateET3")
    private String responseDateET3;
    @JsonProperty("whoApplied")
    private String whoApplied;
    @JsonProperty("respondentName")
    private String respondentName;
    @JsonProperty("respondentRepName")
    private String respondentRepName;
    @JsonProperty("applicableTo")
    private String applicableTo;
    @JsonProperty("judge's_direction")
    private String judgeDirection;

}
