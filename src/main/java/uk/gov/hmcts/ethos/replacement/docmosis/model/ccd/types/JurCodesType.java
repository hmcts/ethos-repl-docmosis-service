package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class JurCodesType {

    @JsonProperty("juridictionCodesList")
    private String juridictionCodesList;
    @JsonProperty("judgmentOutcome")
    private String judgmentOutcome;
    @JsonProperty("juridictionCodesSubList1")
    private String juridictionCodesSubList1;
}
