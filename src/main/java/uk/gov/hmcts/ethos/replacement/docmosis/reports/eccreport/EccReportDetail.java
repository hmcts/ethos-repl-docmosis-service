package uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EccReportDetail {

    @JsonProperty("office")
    private String office;

    @JsonProperty("caseNumber")
    private String caseNumber;

    @JsonProperty("date")
    private String date;

    @JsonProperty("state")
    private String state;

    @JsonProperty("respondentsCount")
    private String respondentsCount;

    @JsonProperty("eccCasesCount")
    private String eccCasesCount;

    @JsonProperty("eccCaseList")
    private String eccCaseList;
}


