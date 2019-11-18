package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CaseData {

    @JsonProperty("tribunalCorrespondenceAddress")
    private Address tribunalCorrespondenceAddress;
    @JsonProperty("tribunalCorrespondenceTelephone")
    private String tribunalCorrespondenceTelephone;
    @JsonProperty("tribunalCorrespondenceFax")
    private String tribunalCorrespondenceFax;
    @JsonProperty("tribunalCorrespondenceDX")
    private String tribunalCorrespondenceDX;
    @JsonProperty("tribunalCorrespondenceEmail")
    private String tribunalCorrespondenceEmail;
    @JsonProperty("ethosCaseReference")
    private String ethosCaseReference;
    @JsonProperty("caseType")
    private String caseType;
    @JsonProperty("multipleReference")
    private String multipleReference;
    @JsonProperty("leadClaimant1")
    private String leadClaimant;
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
    @JsonProperty("preAcceptCase")
    private CasePreAcceptType preAcceptCase;
    @JsonProperty("receiptDate")
    private String receiptDate;
    @JsonProperty("feeGroupReference")
    private String feeGroupReference;
    @JsonProperty("claimantWorkAddressQuestion")
    private String claimantWorkAddressQuestion;
    @JsonProperty("representativeClaimantType")
    private RepresentedTypeC representativeClaimantType;
    @JsonProperty("responseTypeCollection")
    private List<RespondentSumTypeItem> responseTypeCollection;
    @JsonProperty("responseType")
    private RespondentSumType responseType;
    @JsonProperty("respondentCollection")
    private List<RespondentSumTypeItem> respondentCollection;
    @JsonProperty("repCollection")
    private List<RepresentedTypeRItem> repCollection;
    @JsonProperty("positionType")
    private String positionType;
    @JsonProperty("fileLocation")
    private String fileLocation;
    @JsonProperty("fileLocationGlasgow")
    private String fileLocationGlasgow;
    @JsonProperty("fileLocationAberdeen")
    private String fileLocationAberdeen;
    @JsonProperty("fileLocationDundee")
    private String fileLocationDundee;
    @JsonProperty("fileLocationEdinburgh")
    private String fileLocationEdinburgh;
    @JsonProperty("hearingType")
    private HearingType hearingType;
    @JsonProperty("hearingCollection")
    private List<HearingTypeItem> hearingCollection;
    @JsonProperty("depositType")
    private DepositType depositType;
    @JsonProperty("judgementCollection")
    private List<JudgementTypeItem> judgementCollection;
    @JsonProperty("judgementDetailsCollection")
    private List<JudgementDetailsTypeItem> judgementDetailsCollection;
    @JsonProperty("costsCollection")
    private List<CostsTypeItem> costsCollection;
    @JsonProperty("jurCodesCollection")
    private List<JurCodesTypeItem> jurCodesCollection;
    @JsonProperty("broughtForwardCollection")
    private List<BroughtForwardDatesTypeItem> broughtForwardCollection;
    @JsonProperty("clerkResponsible")
    private String clerkResponsible;
    @JsonProperty("userLocation")
    private String userLocation;
    @JsonProperty("subMultipleReference")
    private String subMultipleReference;
    @JsonProperty("addSubMultipleComment")
    private String addSubMultipleComment;
    @JsonProperty("documentCollection")
    private List<DocumentTypeItem> documentCollection;
    @JsonProperty("referredToJudge")
    private String referredToJudge;
    @JsonProperty("backFromJudge")
    private String backFromJudge;
    @JsonProperty("additionalCaseInfo")
    private AdditionalCaseInfoType additionalCaseInfoType;
    @JsonProperty("correspondenceScotType")
    private CorrespondenceScotType correspondenceScotType;
    @JsonProperty("correspondenceType")
    private CorrespondenceType correspondenceType;
    @JsonProperty("caseNotes")
    private String caseNotes;
    @JsonProperty("claimantWorkAddress")
    private ClaimantWorkAddressType claimantWorkAddress;
    @JsonProperty("claimantRepresentedQuestion")
    private String claimantRepresentedQuestion;
    @JsonProperty("bulkCaseReferenceNumber")
    private String bulkCaseReferenceNumber;
    @JsonProperty("managingOffice")
    private String managingOffice;
    @JsonProperty("allocatedOffice")
    private String allocatedOffice;
    @JsonProperty("caseSource")
    private String caseSource;
    @JsonProperty("state")
    private String state;
    @JsonProperty("stateAPI")
    private String stateAPI;
    @JsonProperty("et3Received")
    private String et3Received;
    @JsonProperty("conciliationTrack")
    private String conciliationTrack;
    @JsonProperty("counterClaim")
    private String counterClaim;
    @JsonProperty("restrictedReporting")
    private RestrictedReportingType restrictedReporting;
    @JsonProperty("printHearingDetails")
    private ListingData printHearingDetails;
    @JsonProperty("printHearingCollection")
    private ListingData printHearingCollection;
    @JsonProperty("targetHearingDate")
    private String targetHearingDate;

    @JsonProperty("EQP")
    private String EQP;
    @JsonProperty("flag1")
    private String flag1;
    @JsonProperty("flag2")
    private String flag2;

}
