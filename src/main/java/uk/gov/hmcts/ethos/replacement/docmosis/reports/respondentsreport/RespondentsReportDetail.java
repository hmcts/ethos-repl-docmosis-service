package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespondentsReportDetail {
    @JsonProperty("caseNumber")
    private String caseNumber;

    @JsonProperty("RespondentDataList")
    private List<RespondentFields> respondentDataList;
}


