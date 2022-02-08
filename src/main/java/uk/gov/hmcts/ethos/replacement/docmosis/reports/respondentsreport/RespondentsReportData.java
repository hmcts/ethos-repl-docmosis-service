package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.List;

public class RespondentsReportData extends ListingData {

    @JsonIgnore
    private final RespondentsReportSummary reportSummary;
    @JsonIgnore
    private final List<RespondentsReportDetail> reportDetails = new ArrayList<>();

    public RespondentsReportData(RespondentsReportSummary reportSummary) {
        this.reportSummary = reportSummary;
    }

    public RespondentsReportSummary getReportSummary() {
        return reportSummary;
    }

    public void addReportDetail(List<RespondentsReportDetail> reportDetails) {
        this.reportDetails.addAll(reportDetails);
    }

    public List<RespondentsReportDetail> getReportDetails() {
        return reportDetails;
    }
}
