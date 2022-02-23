package uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportSummary;

public class EccReportData extends ListingData {

    @JsonIgnore
    private final List<EccReportDetail> reportDetails = new ArrayList<>();

    public void addReportDetail(List<EccReportDetail> reportDetails) {
        this.reportDetails.addAll(reportDetails);
    }

    public List<EccReportDetail> getReportDetails() {
        return reportDetails;
    }
}
