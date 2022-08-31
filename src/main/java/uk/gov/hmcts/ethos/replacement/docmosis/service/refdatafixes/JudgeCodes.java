package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JudgeCodes {
    @JsonProperty("existingJudgeCode")
    String existingJudgeCode;
    @JsonProperty("requiredJudgeCode")
    String requiredJudgeCode;
    @JsonProperty("office")
    String office;
}
