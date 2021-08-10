package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportSummary;

import java.util.ArrayList;
import java.util.List;


@Getter
public class TimeToFirstHearingReportData extends ListingData {

    // JsonIgnore is required on properties so that the report data is not
    // returned to CCD in any callback response.
    // Otherwise this would trigger a CCD Case Data Validation error
    // because the properties are not in the CCD config

    @JsonIgnore
    private final TimeToFirstHearingReportSummary reportSummary;
    @JsonIgnore
    private final List<TimeToFirstHearingReportDetail> reportDetails = new ArrayList<>();

    public TimeToFirstHearingReportData(TimeToFirstHearingReportSummary reportSummary) {
        this.reportSummary = reportSummary;
    }

    public TimeToFirstHearingReportSummary getReportSummary() {
        return reportSummary;
    }

    public void addReportDetail(TimeToFirstHearingReportDetail reportDetail) {
        reportDetails.add(reportDetail);
    }

    public List<TimeToFirstHearingReportDetail> getReportDetails() {
        return reportDetails;
    }



}
