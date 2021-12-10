package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportDocHelper.addJsonCollection;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_OFFICE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.TOTAL_CASES;

@Getter
public class NoPositionChangeReportData extends ListingData {
    // JsonIgnore is required on properties so that the report data is not
    // returned to CCD in any callback response.
    // Otherwise, this would trigger a CCD Case Data Validation error
    // because the properties are not in the CCD config

    @JsonIgnore
    private final NoPositionChangeReportSummary reportSummary;
    @JsonIgnore
    private final List<NoPositionChangeReportDetailSingle> reportDetailsSingle = new ArrayList<>();
    @JsonIgnore
    private final List<NoPositionChangeReportDetailMultiple> reportDetailsMultiple = new ArrayList<>();

    public NoPositionChangeReportData(NoPositionChangeReportSummary hearingsToJudgmentsReportSummary) {
        this.reportSummary = hearingsToJudgmentsReportSummary;
    }

    public void addReportDetailsSingle(NoPositionChangeReportDetailSingle reportDetailSingle) {
        reportDetailsSingle.add(reportDetailSingle);
    }

    public void addReportDetailsMultiple(NoPositionChangeReportDetailMultiple reportDetailMultiple) {
        reportDetailsMultiple.add(reportDetailMultiple);
    }

    public StringBuilder toReportObjectString() throws JsonProcessingException {
        var sb = new StringBuilder();
        sb.append(REPORT_OFFICE).append(reportSummary.getOffice()).append(NEW_LINE);
        sb.append("\"Report_Date\":\"").append(getReportDate()).append(NEW_LINE);
        sb.append(TOTAL_CASES).append(nullCheck(reportSummary.getTotalCases())).append(NEW_LINE);
        sb.append("\"Total_Single\":\"").append(nullCheck(reportSummary.getTotalSingleCases())).append(NEW_LINE);
        sb.append("\"Total_Multiple\":\"").append(nullCheck(reportSummary.getTotalMultipleCases())).append(NEW_LINE);
        addJsonCollection("reportDetailsSingle", reportDetailsSingle.iterator(), sb);
        addJsonCollection("reportDetailsMultiple", reportDetailsMultiple.iterator(), sb);
        return sb;
    }
}
