package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RefDataFixesData {
    @JsonProperty("hearingDateType")
    private String hearingDateType;
    @JsonProperty("date")
    private String date;
    @JsonProperty("dateFrom")
    private String dateFrom;
    @JsonProperty("dateTo")
    private String dateTo;
    @JsonProperty("existingJudgeCode")
    private String existingJudgeCode;
    @JsonProperty("requiredJudgeCode")
    private String requiredJudgeCode;
}

