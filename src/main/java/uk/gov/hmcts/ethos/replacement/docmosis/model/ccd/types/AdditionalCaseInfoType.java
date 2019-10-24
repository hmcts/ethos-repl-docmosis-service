package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AdditionalCaseInfoType {

    @JsonProperty("additional_live_appeal")
    private String additionalLiveAppeal;
    @JsonProperty("additional_sensitive")
    private String additionalSensitive;
    @JsonProperty("additional_ind_expert")
    private String additionalIndExpert;
}
