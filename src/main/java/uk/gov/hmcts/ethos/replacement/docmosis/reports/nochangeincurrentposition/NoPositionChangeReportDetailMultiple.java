package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NoPositionChangeReportDetailMultiple {
    @JsonProperty("caseReference")
    private String caseReference;

    @JsonProperty("year")
    private String year;

    @JsonProperty("currentPosition")
    private String currentPosition;

    @JsonProperty("dateToPosition")
    private String dateToPosition;

    @JsonProperty("multipleName")
    private String multipleName;
}
