package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.List;

public class CasesAwaitingJudgmentReportData {

    private ListingData listingData;
    private List<ReportDetail> reportDetails = new ArrayList<>();

    public static CasesAwaitingJudgmentReportData of(ListingData listingData) {
        CasesAwaitingJudgmentReportData casesAwaitingJudgmentReportData = new CasesAwaitingJudgmentReportData();
        casesAwaitingJudgmentReportData.listingData = listingData;
        return casesAwaitingJudgmentReportData;
    }

    public void addReportDetail(ReportDetail reportDetail) {
        reportDetails.add(reportDetail);
    }

    public List<ReportDetail> getReportDetails() {
        return reportDetails;
    }
}
