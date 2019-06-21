package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AdditionalType {

    @JsonProperty("additional_accepted1")
    private String additionalAccepted1;
    @JsonProperty("additional_accepted2")
    private String additionalAccepted2;
    @JsonProperty("additional_conciliation_track")
    private String additionalConciliationTrack;
    @JsonProperty("additional_live_appeal")
    private String additionalLiveAppeal;
    @JsonProperty("additional_sensitive")
    private String additionalSensitive;
    @JsonProperty("additional_emp_claim_made")
    private String additionalEmpClaimMade;
    @JsonProperty("additional_ind_expert")
    private String additionalIndExpert;
}
