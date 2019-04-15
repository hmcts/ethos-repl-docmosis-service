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

    @JsonProperty("tribunalOffices")
    private String tribunalOffices;
    @JsonProperty("caseType")
    private String caseType;
    @JsonProperty("multipleType")
    private String multipleType;
    @JsonProperty("multipleReference")
    private String multipleReference;
    @JsonProperty("claimant_TypeOfClaimant")
    private String claimantTypeOfClaimant;
    @JsonProperty("claimant_Company")
    private String claimantCompany;
    @JsonProperty("claimantIndType")
    private ClaimantIndType claimantIndType;
    @JsonProperty("claimantType")
    private ClaimantType claimantType;
    @JsonProperty("claimantOtherType")
    private ClaimantOtherType claimantOtherType;
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
    @JsonProperty("repSumType")
    private RepresentedType repSumType;
    @JsonProperty("respondentCollection")
    private List<RespondentSumTypeItem> respondentCollection;
    @JsonProperty("repCollection")
    private List<RepresentedTypeItem> repCollection;
    @JsonProperty("positionType")
    private String positionType;
    @JsonProperty("fileLocation")
    private String fileLocation;
    @JsonProperty("hearingType")
    private HearingType hearingType;
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
    private NhJudgementType NH_JudgementType;
    @JsonProperty("jurCodesCollection")
    private List<JurCodesTypeItem> jurCodesCollection;
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
    @JsonProperty("responseTypeCollection")
    private List<ResponseTypeItem> responseTypeCollection;
    @JsonProperty("withdrawType")
    private WithdrawType withdrawType;
    @JsonProperty("archiveType")
    private ArchiveType archiveType;
    @JsonProperty("referredToJudge")
    private String referredToJudge;
    @JsonProperty("backFromJudge")
    private String backFromJudge;
    @JsonProperty("additionalType")
    private AdditionalType additionalType;
    @JsonProperty("reconsiderationType")
    private ReconsiderationType reconsiderationType;
    @JsonProperty("correspondenceType")
    private CorrespondenceType correspondenceType;
    @JsonProperty("caseNotes")
    private String caseNotes;

}
