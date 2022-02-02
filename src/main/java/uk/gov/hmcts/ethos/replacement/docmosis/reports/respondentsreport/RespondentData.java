package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespondentData {

    @JsonProperty("respondentName")
    private String respondentName;

    @JsonProperty("representativeName")
    private String representativeName;

    @JsonProperty("representativeHasMoreThanOneRespondent")
    private String representativeHasMoreThanOneRespondent;

}
