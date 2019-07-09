package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MultipleType {

    @JsonProperty("caseIDM")
    private String caseIDM;
    @JsonProperty("ethosCaseReferenceM")
    private String ethosCaseReferenceM;
    @JsonProperty("leadClaimantM")
    private String leadClaimantM;
    @JsonProperty("multipleReferenceM")
    private String multipleReferenceM;
    @JsonProperty("clerkRespM")
    private String clerkRespM;
    @JsonProperty("claimantSurnameM")
    private String claimantSurnameM;
    @JsonProperty("respondentSurnameM")
    private String respondentSurnameM;
    @JsonProperty("claimantRepM")
    private String claimantRepM;
    @JsonProperty("respondentRepM")
    private String respondentRepM;
    @JsonProperty("fileLocM")
    private String fileLocM;
    @JsonProperty("receiptDateM")
    private String receiptDateM;
    @JsonProperty("acasOfficeM")
    private String acasOfficeM;
    @JsonProperty("positionTypeM")
    private String positionTypeM;
    @JsonProperty("feeGroupReferenceM")
    private String feeGroupReferenceM;
    @JsonProperty("jurCodesCollectionM")
    private String jurCodesCollectionM;
    @JsonProperty("stateM")
    private String stateM;

}
