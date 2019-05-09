package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RespondentSumType {

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
    @JsonProperty("respondent_phone1")
    private String respondentPhone1;
    @JsonProperty("respondent_phone2")
    private String respondentPhone2;
    @JsonProperty("respondent_email")
    private String respondentEmail;
    @JsonProperty("respondent_contact_preference")
    private String respondentContactPreference;

}
