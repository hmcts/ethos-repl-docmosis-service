package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DepositType {

    @JsonProperty("depositParty")
    private String depositParty;
    @JsonProperty("depositCovers")
    private String depositCovers;
    @JsonProperty("depositStatus")
    private String depositStatus;
    @JsonProperty("depositamount")
    private String depositAmount;
    @JsonProperty("depositDueDate")
    private String depositDueDate;
    @JsonProperty("depositOrderSent")
    private String depositOrderSent;
    @JsonProperty("depositRequestJudge")
    private String depositRequestJudge;
}
