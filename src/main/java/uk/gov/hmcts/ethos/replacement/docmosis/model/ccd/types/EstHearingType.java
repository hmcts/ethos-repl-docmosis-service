package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EstHearingType {

    @JsonProperty("est_Hearing_length_number")
    private String estHearingLengthNumber;
    @JsonProperty("est_Hearing_length_num")
    private String estHearingLengthNum;

    public String toString() {
        return String.join(" ", estHearingLengthNumber, estHearingLengthNum);
    }
}
