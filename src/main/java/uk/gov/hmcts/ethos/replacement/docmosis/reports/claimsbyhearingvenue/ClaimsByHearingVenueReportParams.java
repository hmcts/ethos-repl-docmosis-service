package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

@Value
@EqualsAndHashCode(callSuper = false)
public class ClaimsByHearingVenueReportParams extends ReportParams {
    String hearingType;
    String currentUserFullName;

    public ClaimsByHearingVenueReportParams(String caseTypeId, String dateFrom, String dateTo,
                                            String hearingDateType,  String userFullName) {
        super(caseTypeId, dateFrom, dateTo);
        hearingType = hearingDateType;
        currentUserFullName = userFullName;
    }
}
