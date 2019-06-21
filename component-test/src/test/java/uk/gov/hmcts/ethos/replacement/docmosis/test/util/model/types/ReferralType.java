package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ReferralType {

    @JsonProperty("referralJudge")
    private String referralJudge;
    @JsonProperty("referralExplanation")
    private String referralExplanation;
    @JsonProperty("referralOutcome")
    private String referralOutcome;

}
