package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespondentSumType {

    @JsonProperty("response_status")
    private String responseStatus;
    @JsonProperty("responseToClaim")
    private String responseToClaim;
    @JsonProperty("rejection_reason")
    private String rejectionReason;
    @JsonProperty("rejection_reason_other")
    private String rejectionReasonOther;
    @JsonProperty("responseOutOfTime")
    private String responseOutOfTime;
    @JsonProperty("responseNotOnPrescribedForm")
    private String responseNotOnPrescribedForm;
    @JsonProperty("responseRequiredInfoAbsent")
    private String responseRequiredInfoAbsent;
    @JsonProperty("responseNotes")
    private String responseNotes;
    @JsonProperty("response_referred_to_judge")
    private String response_ReferredToJudge;
    @JsonProperty("response_returned_from_judge")
    private String responseReturnedFromJudge;
    @JsonProperty("respondent_name")
    private String respondentName;
    @JsonProperty("respondent_ACAS_question")
    private String respondentACASQuestion;
    @JsonProperty("respondent_ACAS")
    private String respondentACAS;
    @JsonProperty("respondent_ACAS_no")
    private String respondentACASNo;
    @JsonProperty("respondent_address")
    private Address respondentAddress;
    @JsonProperty("responseRespondentAddress")
    private Address responseRespondentAddress;
    @JsonProperty("respondent_phone1")
    private String respondentPhone1;
    @JsonProperty("respondent_phone2")
    private String respondentPhone2;
    @JsonProperty("respondent_email")
    private String respondentEmail;
    @JsonProperty("respondent_contact_preference")
    private String respondentContactPreference;
    @JsonProperty("responseReceivedDate")
    private String responseReceivedDate;
    @JsonProperty("responseRespondentNameQuestion")
    private String responseRespondentNameQuestion;
    @JsonProperty("responseRespondentName")
    private String responseRespondentName;
    @JsonProperty("responseCounterClaim")
    private String responseCounterClaim;

}
