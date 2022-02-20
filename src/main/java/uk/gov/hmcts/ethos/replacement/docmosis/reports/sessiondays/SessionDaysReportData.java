package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SessionDaysReportData extends ListingData {

    @JsonIgnore
    private final SessionDaysReportSummary reportSummary;
    @JsonIgnore
    private final List<SessionDaysReportSummary2> reportSummary2List = new ArrayList<>();
    @JsonIgnore
    private final List<SessionDaysReportDetail> reportDetails = new ArrayList<>();

    public SessionDaysReportData(SessionDaysReportSummary reportSummary) {
        this.reportSummary = reportSummary;
    }

    public SessionDaysReportSummary getReportSummary() {
        return reportSummary;
    }

    public void addReportSummary2List(List<SessionDaysReportSummary2> reportSummary2List) {
        this.reportSummary2List.addAll(reportSummary2List);
    }

    public List<SessionDaysReportSummary2> getReportSummary2List() {
        return reportSummary2List;
    }

    public void addReportDetail(List<SessionDaysReportDetail> reportDetails) {
        this.reportDetails.addAll(reportDetails);
    }

    public List<SessionDaysReportDetail> getReportDetails() {
        return reportDetails;
    }
}
