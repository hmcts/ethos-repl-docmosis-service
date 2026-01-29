package uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.hmcts.ecm.common.model.generic.GenericCaseDetails;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AdminDetails extends GenericCaseDetails {

    @JsonProperty("case_data")
    private AdminData caseData;
}
