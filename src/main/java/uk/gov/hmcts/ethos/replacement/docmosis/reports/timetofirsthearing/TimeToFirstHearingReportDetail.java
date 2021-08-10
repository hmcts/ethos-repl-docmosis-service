package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeToFirstHearingReportDetail {

    @JsonProperty("reportOffice")
    private String reportOffice;
    @JsonProperty("caseReference")
    private String caseReference;
    @JsonProperty("conciliationTrack")
    private String conciliationTrack;
    @JsonProperty("receiptDate")
    private String receiptDate;
    @JsonProperty("hearingDate")
    private String hearingDate;
    @JsonProperty("total")
    private String total;
    @JsonProperty("et3ReceivedDate")
    private String et3ReceivedDate;
    @JsonProperty("et3RespondentName")
    private String et3RespondentName;
    @JsonProperty("listedDate")
    private String listedDate;
    @JsonProperty("hearingNumber")
    private String hearingNumber;
    @JsonProperty("hearingType")
    private String hearingType;
    @JsonProperty("hearingStatus")
    private String hearingStatus;
    @JsonProperty("hearingClerk")
    private String hearingClerk;
}
