package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EstHearingType {

    @JsonProperty("fromDays")
    private String fromDays;
    @JsonProperty("fromHours")
    private String fromHours;
    @JsonProperty("fromMinues")
    private String fromMinutes;
}
