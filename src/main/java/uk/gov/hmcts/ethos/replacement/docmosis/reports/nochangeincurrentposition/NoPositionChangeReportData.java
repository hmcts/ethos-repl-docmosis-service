package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportDocHelper.addJsonCollection;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_DATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_DETAILS_MULTIPLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_DETAILS_SINGLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_OFFICE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.TOTAL_CASES;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.TOTAL_MULTIPLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.TOTAL_SINGLE;

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

    public NoPositionChangeReportData(NoPositionChangeReportSummary hearingsToJudgmentsReportSummary,
                                      String reportDate) {
        this.reportSummary = hearingsToJudgmentsReportSummary;
        this.setReportDate(reportDate);
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
        sb.append(REPORT_DATE).append(UtilHelper.listingFormatLocalDate(getReportDate())).append(NEW_LINE);
        sb.append(TOTAL_CASES).append(StringUtils.defaultString(reportSummary.getTotalCases(), "0"))
                .append(NEW_LINE);
        sb.append(TOTAL_SINGLE).append(StringUtils.defaultString(reportSummary.getTotalSingleCases(), "0"))
                .append(NEW_LINE);
        sb.append(TOTAL_MULTIPLE).append(StringUtils.defaultString(reportSummary.getTotalMultipleCases(), "0"))
                .append(NEW_LINE);
        addJsonCollection(REPORT_DETAILS_SINGLE, reportDetailsSingle.iterator(), sb);
        addJsonCollection(REPORT_DETAILS_MULTIPLE, reportDetailsMultiple.iterator(), sb);
        return sb;
    }
}
