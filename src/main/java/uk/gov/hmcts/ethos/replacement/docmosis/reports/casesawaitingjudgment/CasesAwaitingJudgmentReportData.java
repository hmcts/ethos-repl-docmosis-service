package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CasesAwaitingJudgmentReportData {

    private final ListingData listingData;
    private final ReportSummary reportSummary;
    private final List<ReportDetail> reportDetails = new ArrayList<>();

    public CasesAwaitingJudgmentReportData(ListingData listingData, ReportSummary reportSummary) {
        this.listingData = listingData;
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
