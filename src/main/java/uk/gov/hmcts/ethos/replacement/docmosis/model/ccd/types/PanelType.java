package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PanelType {

    @JsonProperty("panelMember")
    private String panelMember;
    @JsonProperty("panelJudge")
    private String panelJudge;
    @JsonProperty("panelClerk")
    private String panelClerk;
    @JsonProperty("panelEEMember")
    private String panelEEMember;
    @JsonProperty("panelERMember")
    private String panelERMember;
    @JsonProperty("panelOtherMember")
    private String panelOtherMember;
    @JsonProperty("panelMedicalExpert")
    private String panelMedicalExpert;
    @JsonProperty("panelIndependentExpert")
    private String panelIndependentExpert;
}
