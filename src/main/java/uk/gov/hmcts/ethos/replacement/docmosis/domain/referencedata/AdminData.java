package uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AdminData {
    @JsonProperty("hearingDateType")
    private String hearingDateType;
    @JsonProperty("name")
    private String name;
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
    @JsonProperty("tribunalOffice")
    private String tribunalOffice;
}

