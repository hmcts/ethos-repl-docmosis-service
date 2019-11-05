package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
    @JsonProperty("positionTypeM")
    private String positionTypeM;
    @JsonProperty("feeGroupReferenceM")
    private String feeGroupReferenceM;
    @JsonProperty("jurCodesCollectionTextM")
    private String jurCodesCollectionM;
    @JsonProperty("stateM")
    private String stateM;
    @JsonProperty("subMultipleM")
    private String subMultipleM;
    @JsonProperty("subMultipleTitleM")
    private String subMultipleTitleM;
    @JsonProperty("currentPositionM")
    private String currentPositionM;
    @JsonProperty("claimantAddressLine1M")
    private String claimantAddressLine1M;
    @JsonProperty("claimantPostCodeM")
    private String claimantPostCodeM;
    @JsonProperty("respondentAddressLine1M")
    private String respondentAddressLine1M;
    @JsonProperty("respondentPostCodeM")
    private String respondentPostCodeM;
    @JsonProperty("flag1M")
    private String flag1M;
    @JsonProperty("flag2M")
    private String flag2M;
    @JsonProperty("EQPM")
    private String EQPM;
    @JsonProperty("respondentRepOrgM")
    private String respondentRepOrgM;
    @JsonProperty("claimantRepOrgM")
    private String claimantRepOrgM;

}
