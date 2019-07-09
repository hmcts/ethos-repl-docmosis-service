package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SearchType {

    @JsonProperty("caseIDS")
    private String caseIDS;
    @JsonProperty("ethosCaseReferenceS")
    private String ethosCaseReferenceS;
    @JsonProperty("leadClaimantS")
    private String leadClaimantS;
    @JsonProperty("clerkRespS")
    private String clerkRespS;
    @JsonProperty("claimantSurnameS")
    private String claimantSurnameS;
    @JsonProperty("respondentSurnameS")
    private String respondentSurnameS;
    @JsonProperty("claimantRepS")
    private String claimantRepS;
    @JsonProperty("respondentRepS")
    private String respondentRepS;
    @JsonProperty("fileLocS")
    private String fileLocS;
    @JsonProperty("receiptDateS")
    private String receiptDateS;
    @JsonProperty("acasOfficeS")
    private String acasOfficeS;
    @JsonProperty("positionTypeS")
    private String positionTypeS;
    @JsonProperty("feeGroupReferenceS")
    private String feeGroupReferenceS;
    @JsonProperty("jurCodesCollectionS")
    private String jurCodesCollectionS;
    @JsonProperty("stateS")
    private String stateS;
}
