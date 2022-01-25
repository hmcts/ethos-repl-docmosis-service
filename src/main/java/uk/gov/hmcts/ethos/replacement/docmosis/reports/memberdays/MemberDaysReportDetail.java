package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

@RequiredArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDaysReportDetail {
    @JsonProperty("hearingDate")
    private String hearingDate;
    private String sortingHearingDate;
    @JsonProperty("parentHearingId")
    private String parentHearingId;

    @JsonProperty("employeeMember")
    private String employeeMember;
    @JsonProperty("employerMember")
    private String employerMember;

    @JsonProperty("caseReference")
    private String caseReference;
    @JsonProperty("hearingNumber")
    private String hearingNumber;
    @JsonProperty("hearingType")
    private String hearingType;
    @JsonProperty("hearingClerk")
    private String hearingClerk;
    @JsonProperty("hearingDuration")
    private String hearingDuration;

    public int comparedTo(MemberDaysReportDetail secondReportDetail) {
        return LocalDate.parse(this.getSortingHearingDate())
            .compareTo(LocalDate.parse(secondReportDetail.getSortingHearingDate()));
    }
}
