package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CaseData {

    @JsonProperty("caseNote")
    private String caseNote;
    @JsonProperty("caseAssignee")
    private String caseAssignee;
    @JsonProperty("caseType")
    private String caseType;
    @JsonProperty("multipleType")
    private String multipleType;
    @JsonProperty("multipleReference")
    private String multipleReference;
    @JsonProperty("claimantType")
    private ClaimantType claimantType;
    @JsonProperty("claimantOtherType")
    private ClaimantOtherType claimantOtherType;
    @JsonProperty("if_C_represented")
    private String ifCRepresented;
    @JsonProperty("c_RepresentedType")
    private RepresentedType cRepresentedType;
    @JsonProperty("claimantCollection")
    private List<ClaimantTypeItem> claimantCollection;
    @JsonProperty("receiptDate")
    private String receiptDate;
    @JsonProperty("broughtForwardDate")
    private String broughtForwardDate;
    @JsonProperty("feeGroupReference")
    private String feeGroupReference;
    @JsonProperty("respondentSumType")
    private RespondentSumType respondentSumType;
    @JsonProperty("respondentType")
    private RespondentType respondentType;
    @JsonProperty("if_R_represented")
    private String ifRRepresented;
    @JsonProperty("r_RepresentedType")
    private RepresentedType rRepresentedType;
    @JsonProperty("respondentCollection")
    private List<RespondentSumTypeItem> respondentCollection;
    @JsonProperty("positionType")
    private String positionType;
    @JsonProperty("locationType")
    private String locationType;
    @JsonProperty("hearingCollection")
    private List<HearingTypeItem> hearingCollection;
    @JsonProperty("depositCollection")
    private List<DepositTypeItem> depositCollection;
    @JsonProperty("judgementCollection")
    private List<JudgementTypeItem> judgementCollection;
    @JsonProperty("judgementDetailsCollection")
    private List<JudgementDetailsTypeItem> judgementDetailsCollection;
    @JsonProperty("costsCollection")
    private List<CostsTypeItem> costsCollection;
    @JsonProperty("disposeType")
    private DisposeType disposeType;
    @JsonProperty("NH_JudgementType")
    private NhJudgementType nhJudgementType;
    @JsonProperty("jurCodesCollection")
    private List<JurCodesTypeItem> jurCodesCollection;
    @JsonProperty("tribunalOffice")
    private String tribunalOffice;
    @JsonProperty("acasOffice")
    private String acasOffice;
    @JsonProperty("clerkResponsible")
    private String clerkResponsible;
    @JsonProperty("userLocation")
    private String userLocation;
    @JsonProperty("subMultipleReference")
    private String subMultipleReference;
    @JsonProperty("addSubMultipleComment")
    private String addSubMultipleComment;
    @JsonProperty("panelCollection")
    private List<PanelTypeItem> panelCollection;
    @JsonProperty("documentCollection")
    private List<DocumentTypeItem> documentCollection;
    @JsonProperty("referToETJ")
    private List<ReferralTypeItem> referToETJ;
    @JsonProperty("responseType")
    private ResponseType responseType;
    @JsonProperty("withdrawType")
    private WithdrawType withdrawType;
    @JsonProperty("archiveType")
    private ArchiveType archiveTYpe;
    @JsonProperty("referredToJudge")
    private String referredToJudge;
    @JsonProperty("backFromJudge")
    private String backFromJudge;
    @JsonProperty("responseReceived")
    private String responseReceived;
    @JsonProperty("additionalType")
    private AdditionalType additionalType;
    @JsonProperty("reconsiderationType")
    private ReconsiderationType reconsiderationType;
    @JsonProperty("correspondenceType")
    private CorrespondenceType correspondenceType;
}
