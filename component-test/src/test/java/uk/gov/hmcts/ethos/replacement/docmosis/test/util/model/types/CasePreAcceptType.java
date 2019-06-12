package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CasePreAcceptType {

    @JsonProperty("caseAccepted")
    private String caseAccepted;
    @JsonProperty("dateAccepted")
    private String dateAccepted;
    @JsonProperty("rejectReason")
    private String rejectReason;
    @JsonProperty("caseReferred")
    private String caseReferred;
    @JsonProperty("caseReferredDate")
    private String caseReferredDate;
    @JsonProperty("caseJudge")
    private String caseJudge;
    @JsonProperty("caseEJReferredDate")
    private String caseEJReferredDate;
    @JsonProperty("caseEJReferredDateReturn")
    private String caseEJReferredDateReturn;
}
