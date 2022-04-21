package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ClaimsByHearingVenueReportParams extends ReportParams {
    private final String hearingDateType;
    private final String userFullName;

    public ClaimsByHearingVenueReportParams(String caseTypeId, String dateFrom, String dateTo,
                                            String hearingDateType,  String userFullName) {
        super(caseTypeId, dateFrom, dateTo);
        this.hearingDateType = hearingDateType;
        this.userFullName = userFullName;
    }
}
