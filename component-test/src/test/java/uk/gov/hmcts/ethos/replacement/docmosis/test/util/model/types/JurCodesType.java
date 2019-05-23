package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class JurCodesType {

    @JsonProperty("juridictionCodesList")
    private String juridictionCodesList;
    @JsonProperty("juridictionCodesSubList1")
    private String juridictionCodesSubList1;
}
