package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RestrictedReportingType {

    @JsonProperty("requestedBy")
    private String requestedBy;
    @JsonProperty("dateCeased")
    private String dateCeased;
    @JsonProperty("imposed")
    private String imposed;
    @JsonProperty("rule503b")
    private String rule503b;
    @JsonProperty("excludedRegister")
    private String excludedRegister;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("excludedNames")
    private String excludedNames;
    @JsonProperty("deletedPhyRegister")
    private String deletedPhyRegister;

}
