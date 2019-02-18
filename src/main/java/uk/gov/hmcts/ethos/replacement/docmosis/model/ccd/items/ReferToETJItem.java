package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ReferralType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ReferToETJItem {

    @JsonProperty("id")
    private String id;
    @JsonProperty("value")
    private ReferralType value;
}
