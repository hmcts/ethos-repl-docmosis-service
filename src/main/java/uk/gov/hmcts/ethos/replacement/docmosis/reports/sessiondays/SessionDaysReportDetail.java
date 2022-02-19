package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SessionDaysReportDetail {
    @JsonProperty("hearingDate")
    private String hearingDate;
    @JsonProperty("hearingJudge")
    private String hearingJudge;
    @JsonProperty("judgeType")
    private String judgeType;
    @JsonProperty("caseReference")
    private String caseReference;
    @JsonProperty("hearingNumber")
    private String hearingNumber;
    @JsonProperty("hearingType")
    private String hearingType;
    @JsonProperty("hearingSitAlone")
    private String hearingSitAlone;
    @JsonProperty("hearingTelConf")
    private String hearingTelConf;
    @JsonProperty("hearingDuration")
    private String hearingDuration;
    @JsonProperty("sessionType")
    private String sessionType;
    @JsonProperty("hearingClerk")
    private String hearingClerk;
}
