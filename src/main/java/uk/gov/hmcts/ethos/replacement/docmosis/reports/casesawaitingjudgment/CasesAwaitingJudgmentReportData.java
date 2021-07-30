package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CasesAwaitingJudgmentReportData extends ListingData {
    // JsonIgnore is required on properties so that the report data is not
    // returned to CCD in any callback response.
    // Otherwise this would trigger a CCD Case Data Validation error
    // because the properties are not in the CCD config

    @JsonIgnore
    private final ReportSummary reportSummary;
    @JsonIgnore
    private final List<ReportDetail> reportDetails = new ArrayList<>();

    public CasesAwaitingJudgmentReportData(ReportSummary reportSummary) {
        this.reportSummary = reportSummary;
    }

    public ReportSummary getReportSummary() {
        return reportSummary;
    }

    public void addReportDetail(ReportDetail reportDetail) {
        reportDetails.add(reportDetail);
    }

    public List<ReportDetail> getReportDetails() {
        return reportDetails;
    }


}
