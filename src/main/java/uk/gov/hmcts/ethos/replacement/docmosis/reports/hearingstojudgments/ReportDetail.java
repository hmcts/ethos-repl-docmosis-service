package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReportDetail {
    @JsonProperty("reportOffice")
    private String reportOffice;
    @JsonProperty("caseReference")
    private String caseReference;
    @JsonProperty("hearingDate")
    private String hearingDate;
    @JsonProperty("judgementDateSent")
    private String judgementDateSent;
    @JsonProperty("totalDays")
    private String totalDays;
    @JsonProperty("reservedHearing")
    private String reservedHearing;
    @JsonProperty("hearingJudge")
    private String hearingJudge;
}
