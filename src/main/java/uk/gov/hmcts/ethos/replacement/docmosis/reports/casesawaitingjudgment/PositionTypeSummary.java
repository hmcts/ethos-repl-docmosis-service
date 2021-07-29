package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PositionTypeSummary {
    private final String positionTypeName;
    @JsonProperty("ptCount")
    private final int positionTypeCount;
}
