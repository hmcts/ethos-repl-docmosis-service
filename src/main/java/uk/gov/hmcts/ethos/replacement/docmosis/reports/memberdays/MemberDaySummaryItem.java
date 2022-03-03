package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDaySummaryItem {

    @JsonProperty("hearingDate")
    private String hearingDate;

    @JsonProperty("fullDays")
    private String fullDays;

    @JsonProperty("halfDays")
    private String halfDays;

    @JsonProperty("totalDays")
    private String totalDays;

}
