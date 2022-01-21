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
    public String hearingDate;

    @JsonProperty("fullDays")
    public String fullDays;

    @JsonProperty("halfDays")
    public String halfDays;

    @JsonProperty("totalDays")
    public String totalDays;

}
