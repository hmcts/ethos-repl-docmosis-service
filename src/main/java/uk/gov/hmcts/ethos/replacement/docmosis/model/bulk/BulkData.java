package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.CorrespondenceType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BulkData {

    @JsonProperty("bulkCaseTitle")
    private String bulkCaseTitle;
    @JsonProperty("multipleReference")
    private String multipleReference;
    @JsonProperty("feeGroupReference")
    private String feeGroupReference;
    @JsonProperty("claimantSurname")
    private String claimantSurname;
    @JsonProperty("respondentSurname")
    private String respondentSurname;
    @JsonProperty("claimantRep")
    private String claimantRep;
    @JsonProperty("respondentRep")
    private String respondentRep;
    @JsonProperty("ethosCaseReference")
    private String ethosCaseReference;
    @JsonProperty("clerkResponsible")
    private String clerkResponsible;
    @JsonProperty("fileLocation")
    private String fileLocation;
    @JsonProperty("jurCodesCollection")
    private List<JurCodesTypeItem> jurCodesCollection;

    @JsonProperty("fileLocation_v2")
    private String fileLocationV2;
    @JsonProperty("feeGroupReference_v2")
    private String feeGroupReferenceV2;
    @JsonProperty("claimantSurname_v2")
    private String claimantSurnameV2;
    @JsonProperty("respondentSurname_v2")
    private String respondentSurnameV2;
    @JsonProperty("multipleReference_v2")
    private String multipleReferenceV2;
    @JsonProperty("clerkResponsible_v2")
    private String clerkResponsibleV2;
    @JsonProperty("positionType_v2")
    private String positionTypeV2;
    @JsonProperty("claimantRep_v2")
    private String claimantRepV2;
    @JsonProperty("respondentRep_v2")
    private String respondentRepV2;
    @JsonProperty("fileLocationGlasgow")
    private String fileLocationGlasgow;
    @JsonProperty("fileLocationAberdeen")
    private String fileLocationAberdeen;
    @JsonProperty("fileLocationDundee")
    private String fileLocationDundee;
    @JsonProperty("fileLocationEdinburgh")
    private String fileLocationEdinburgh;
    @JsonProperty("managingOffice")
    private String managingOffice;
    @JsonProperty("subMultipleName")
    private String subMultipleName;
    @JsonProperty("subMultipleRef")
    private String subMultipleRef;

    @JsonProperty("caseIdCollection")
    private List<CaseIdTypeItem> caseIdCollection;
    @JsonProperty("searchCollection")
    private List<SearchTypeItem> searchCollection;
    @JsonProperty("midSearchCollection")
    private List<MidSearchTypeItem> midSearchCollection;
    @JsonProperty("multipleCollection")
    private List<MultipleTypeItem> multipleCollection;
    @JsonProperty("subMultipleCollection")
    private List<SubMultipleTypeItem> subMultipleCollection;

    @JsonProperty("subMultipleDynamicList")
    private DynamicFixedListType subMultipleDynamicList;

    @JsonProperty("searchCollectionCount")
    private String searchCollectionCount;
    @JsonProperty("multipleCollectionCount")
    private String multipleCollectionCount;

    @JsonProperty("correspondenceType")
    private CorrespondenceType correspondenceType;
    @JsonProperty("correspondenceScotType")
    private CorrespondenceScotType correspondenceScotType;

    @JsonProperty("selectAll")
    private String selectAll;
    @JsonProperty("scheduleDocName")
    private String scheduleDocName;
    @JsonProperty("positionType")
    private String positionType;
    @JsonProperty("flag1")
    private String flag1;
    @JsonProperty("flag2")
    private String flag2;
    @JsonProperty("EQP")
    private String EQP;
    @JsonProperty("submissionRef")
    private String submissionRef;
    @JsonProperty("claimantOrg")
    private String claimantOrg;
    @JsonProperty("respondentOrg")
    private String respondentOrg;
    @JsonProperty("state")
    private String state;

    @JsonProperty("flag1Update")
    private String flag1Update;
    @JsonProperty("flag2Update")
    private String flag2Update;
    @JsonProperty("EQPUpdate")
    private String EQPUpdate;

    @JsonProperty("jurCodesDynamicList")
    private DynamicFixedListType jurCodesDynamicList;
    @JsonProperty("outcomeUpdate")
    private String outcomeUpdate;

    @JsonProperty("filterCases")
    private String filterCases;
    @JsonProperty("docMarkUp")
    private String docMarkUp;

    @JsonProperty("multipleSource")
    private String multipleSource;

}

