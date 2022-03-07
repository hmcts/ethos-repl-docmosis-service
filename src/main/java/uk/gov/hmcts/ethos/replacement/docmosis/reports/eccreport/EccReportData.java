package uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import java.util.ArrayList;
import java.util.List;

public class EccReportData extends ListingData {

    @JsonIgnore
    private final List<EccReportDetail> reportDetails = new ArrayList<>();

    @JsonIgnore
    private String office;

    public EccReportData(String office) {
        this.office = office;
    }

    public void addReportDetail(List<EccReportDetail> reportDetails) {
        this.reportDetails.addAll(reportDetails);
    }

    public List<EccReportDetail> getReportDetails() {
        return reportDetails;
    }

    public String getOffice() {
        return office;
    }
}
