package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReportDetail {

    static final String NO_MULTIPLE_REFERENCE = "0/0";

    @JsonIgnore
    private String positionType;
    @JsonProperty("wsh")
    private long weeksSinceHearing;
    @JsonProperty("dsh")
    private long daysSinceHearing;
    @JsonProperty("cn")
    private String caseNumber;
    @JsonProperty("mr")
    private String multipleReference;
    @JsonProperty("lhhd")
    private String lastHeardHearingDate;
    @JsonProperty("hn")
    private String hearingNumber;
    @JsonProperty("ht")
    private String hearingType;
    @JsonProperty("judge")
    private String judge;
    @JsonProperty("cp")
    private String currentPosition;
    @JsonProperty("dtp")
    private String dateToPosition;
    @JsonProperty("ct")
    private String conciliationTrack;
}
