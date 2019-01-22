package uk.gov.hmcts.ethos.replacement.docmosis.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentRequest {
//    @JsonProperty("caseReferenceNumber")
//    private String caseReferenceNumber;
//    @JsonProperty("solicitorReferenceNumber")
//    private String solicitorReferenceNumber;
//    @JsonProperty("name")
//    private String name;
//    @JsonProperty("notificationEmail")
//    private String notificationEmail;

    @JsonProperty("ifRepresented")
    private String ifRepresented;
    @JsonProperty("ifRefused")
    private String ifRefused;
    @JsonProperty("representativeType")
    private RepresentativeType representativeType;
    @JsonProperty("createdDate")
    private String createdDate;
    @JsonProperty("receivedDate")
    private String receivedDate;
    @JsonProperty("hearingDate")
    private String hearingDate;
    @JsonProperty("caseNo")
    private String caseNo;
    @JsonProperty("claimant")
    private String claimant;
    @JsonProperty("respondent")
    private String respondent;
    @JsonProperty("clerk")
    private String clerk;
    @JsonProperty("judgeSurname")
    private String judgeSurname;

    @JsonProperty("claimantType")
    private ClaimantType claimantType;
    @JsonProperty("respondentType")
    private RespondentType respondentType;

}
