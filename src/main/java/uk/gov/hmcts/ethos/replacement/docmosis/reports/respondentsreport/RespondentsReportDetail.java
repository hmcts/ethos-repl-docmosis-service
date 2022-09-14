package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespondentsReportDetail {
    @JsonProperty("caseNumber")
    private String caseNumber;

    @JsonProperty("respondentName")
    private String respondentName;

    @JsonProperty("representativeName")
    private String representativeName;

    @JsonProperty("representativeHasMoreThanOneRespondent")
    private String representativeHasMoreThanOneRespondent;
}


