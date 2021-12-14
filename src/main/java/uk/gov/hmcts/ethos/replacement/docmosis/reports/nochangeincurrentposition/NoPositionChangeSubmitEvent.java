package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.hmcts.ecm.common.model.generic.GenericSubmitEvent;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoPositionChangeSubmitEvent extends GenericSubmitEvent {
    @JsonProperty("case_data")
    private NoPositionChangeCaseData caseData;
}
