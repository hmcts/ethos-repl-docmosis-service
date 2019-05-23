package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClaimantWorkAddressType {

    @JsonProperty("claimant_work_address")
    private Address claimantWorkAddress;
    @JsonProperty("claimant_work_phone_number")
    private String claimantWorkPhoneNumber;
}
