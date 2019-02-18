package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ResponseType {

    @JsonProperty("response_status")
    private String responseStatus;
    @JsonProperty("response_received")
    private String responseReceived;
    @JsonProperty("response_to_claim")
    private String responseToClaim;
    @JsonProperty("response_referred_to_judge")
    private String response_ReferredToJudge;
    @JsonProperty("response_returned_from_judge")
    private String responseReturnedFromJudge;
    @JsonProperty("response_out_of_time?")
    private String responseOutOfTime;
    @JsonProperty("response_not_on_prescribed_form?")
    private String responseNotOnPrescribedForm;
    @JsonProperty("response_required_info_absent?")
    private String responseRequiredInfoAbsent;
}
