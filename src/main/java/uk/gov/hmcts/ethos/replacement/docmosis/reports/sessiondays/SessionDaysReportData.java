package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

@Getter
public class SessionDaysReportData extends ListingData {

    @JsonIgnore
    private final SessionDaysReportSummary reportSummary;
    @JsonIgnore
    private final SessionDaysReportSummary2 reportSummary2;
    @JsonIgnore
    private final List<SessionDaysReportDetail> reportDetails = new ArrayList<>();

    public SessionDaysReportData(SessionDaysReportSummary reportSummary, SessionDaysReportSummary2 reportSummary2) {
        this.reportSummary = reportSummary;
        this.reportSummary2 = reportSummary2;
    }

    public SessionDaysReportSummary getReportSummary() {
        return reportSummary;
    }

    public void addReportDetail(List<SessionDaysReportDetail> reportDetails) {
        this.reportDetails.addAll(reportDetails);
    }
    public List<SessionDaysReportDetail> getReportDetails() {
        return reportDetails;
    }
}
