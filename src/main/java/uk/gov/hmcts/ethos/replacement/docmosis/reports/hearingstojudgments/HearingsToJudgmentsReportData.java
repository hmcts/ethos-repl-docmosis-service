package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HearingsToJudgmentsReportData extends ListingData {
    // JsonIgnore is required on properties so that the report data is not
    // returned to CCD in any callback response.
    // Otherwise, this would trigger a CCD Case Data Validation error
    // because the properties are not in the CCD config

    @JsonIgnore
    private final HearingsToJudgmentsReportSummary hearingsToJudgmentsReportSummary;
    @JsonIgnore
    private final List<HearingsToJudgmentsReportDetail> hearingsToJudgmentsReportDetails = new ArrayList<>();

    public HearingsToJudgmentsReportData(HearingsToJudgmentsReportSummary hearingsToJudgmentsReportSummary) {
        this.hearingsToJudgmentsReportSummary = hearingsToJudgmentsReportSummary;
    }

    public HearingsToJudgmentsReportSummary getHearingsToJudgmentsReportSummary() {
        return hearingsToJudgmentsReportSummary;
    }

    public void addReportDetail(HearingsToJudgmentsReportDetail hearingsToJudgmentsReportDetail) {
        hearingsToJudgmentsReportDetails.add(hearingsToJudgmentsReportDetail);
    }

    public List<HearingsToJudgmentsReportDetail> getReportDetails() {
        return hearingsToJudgmentsReportDetails;
    }
}
