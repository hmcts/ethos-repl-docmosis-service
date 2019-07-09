package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;

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

    @JsonProperty("caseIdCollection")
    private List<CaseIdTypeItem> caseIdCollection;
    @JsonProperty("searchCollection")
    private List<SearchTypeItem> searchCollection;
    @JsonProperty("multipleCollection")
    private List<MultipleTypeItem> multipleCollection;

    @JsonProperty("searchCollectionCount")
    private String searchCollectionCount;
    @JsonProperty("multipleCollectionCount")
    private String multipleCollectionCount;

}

