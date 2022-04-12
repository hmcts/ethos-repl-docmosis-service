package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HearingsByHearingTypeReportDetail {
    @JsonProperty("detailDate")
    private String detailDate;
    @JsonProperty("multiSub")
    private String multiSub;
    @JsonProperty("caseReference")
    private String caseReference;
    @JsonProperty("lead")
    private String lead;
    @JsonProperty("hearingType")
    private String hearingType;
    @JsonProperty("hearingNo")
    private String hearingNo;
    @JsonProperty("tel")
    private String tel;
    @JsonProperty("jm")
    private String jm;
    @JsonProperty("duration")
    private String duration;
    @JsonProperty("hearingClerk")
    private String hearingClerk;
}
