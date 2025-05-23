package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_DATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_DETAILS_MULTIPLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_DETAILS_SINGLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.REPORT_OFFICE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.TOTAL_CASES;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.TOTAL_MULTIPLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.TOTAL_SINGLE;

class NoPositionChangeReportDataTests {

    @Test
    void shouldReturnValidJson() throws JsonProcessingException {
        var reportData = setupValidReportData();
        var resultJsonString = reportData.toReportObjectString();

        var expectedJsonString = getExpectedJsonString(reportData);
        assertEquals(expectedJsonString.toString(), resultJsonString.toString());
    }

    @Test
    void shouldReturnValidJsonWithEmptyValues() throws JsonProcessingException {
        var reportSummary = new NoPositionChangeReportSummary("Office");
        var reportData = new NoPositionChangeReportData(reportSummary, "2021-07-07");
        var resultJsonString = reportData.toReportObjectString();

        var expectedJsonString = getExpectedJsonString(reportData);
        assertEquals(expectedJsonString.toString(), resultJsonString.toString());
    }

    private NoPositionChangeReportData setupValidReportData() {
        var reportSummary = new NoPositionChangeReportSummary("Office");
        reportSummary.setTotalCases("0");
        reportSummary.setTotalSingleCases("2");
        reportSummary.setTotalMultipleCases("1");
        var reportData = new NoPositionChangeReportData(reportSummary, "2021-06-07");

        var reportDetailSingle1 = new NoPositionChangeReportDetailSingle();
        reportDetailSingle1.setCaseReference("caseRef1");
        reportDetailSingle1.setDateToPosition("2021-02-03");
        reportDetailSingle1.setCurrentPosition("Test position1");
        reportDetailSingle1.setYear("2021");
        reportDetailSingle1.setRespondent("R1");
        reportData.addReportDetailsSingle(reportDetailSingle1);

        var reportDetailSingle2 = new NoPositionChangeReportDetailSingle();
        reportDetailSingle2.setCaseReference("caseRef2");
        reportDetailSingle2.setDateToPosition("2021-02-04");
        reportDetailSingle2.setCurrentPosition("Test position2");
        reportDetailSingle2.setYear("2022");
        reportDetailSingle2.setRespondent("R2 & Others");
        reportData.addReportDetailsSingle(reportDetailSingle2);

        var reportDetailMultiple = new NoPositionChangeReportDetailMultiple();
        reportDetailMultiple.setCaseReference("caseRef3");
        reportDetailMultiple.setDateToPosition("2021-02-05");
        reportDetailMultiple.setCurrentPosition("Test position3");
        reportDetailMultiple.setYear("2020");
        reportDetailMultiple.setMultipleName("Multi");
        reportData.addReportDetailsMultiple(reportDetailMultiple);
        return reportData;
    }

    private StringBuilder getExpectedJsonString(NoPositionChangeReportData reportData) {
        var sb = new StringBuilder();
        var reportSummary = reportData.getReportSummary();
        sb.append(buildSummaryJsonString(
                reportSummary.getOffice(), reportData.getReportDate(), reportSummary.getTotalCases(),
                reportSummary.getTotalSingleCases(), reportSummary.getTotalMultipleCases()));

        sb.append("\"" + REPORT_DETAILS_SINGLE + "\":[\n");
        if (CollectionUtils.isNotEmpty(reportData.getReportDetailsSingle())
                && reportData.getReportDetailsSingle().get(0) != null) {
            var rdSingle1 = reportData.getReportDetailsSingle().get(0);
            sb.append(buildDetailSingleJsonString(
                    rdSingle1.getCaseReference(), rdSingle1.getYear(), rdSingle1.getCurrentPosition(),
                    rdSingle1.getDateToPosition(), rdSingle1.getRespondent()));
        }
        if (CollectionUtils.isNotEmpty(reportData.getReportDetailsSingle())
                && reportData.getReportDetailsSingle().get(1) != null) {
            var rdSingle2 = reportData.getReportDetailsSingle().get(1);
            sb.append(",\n");
            sb.append(buildDetailSingleJsonString(
                    rdSingle2.getCaseReference(), rdSingle2.getYear(), rdSingle2.getCurrentPosition(),
                    rdSingle2.getDateToPosition(), rdSingle2.getRespondent()
            ));
            sb.append("\n");
        }
        sb.append("],\n");

        sb.append("\"" + REPORT_DETAILS_MULTIPLE + "\":[\n");
        if (CollectionUtils.isNotEmpty(reportData.getReportDetailsSingle())) {
            var rdMultiple = reportData.getReportDetailsMultiple().get(0);
            sb.append(buildDetailMultipleJsonString(
                    rdMultiple.getCaseReference(), rdMultiple.getYear(), rdMultiple.getCurrentPosition(),
                    rdMultiple.getDateToPosition(), rdMultiple.getMultipleName()
            ));
            sb.append("\n");
        }
        sb.append("],\n");
        return sb;
    }

    private StringBuilder buildSummaryJsonString(String office, String reportDate, String totalCases,
                                                  String totalSingle, String totalMultiple) {
        var sb = new StringBuilder();
        sb.append(REPORT_OFFICE).append(StringUtils.defaultString(office, "")).append(NEW_LINE);
        sb.append(REPORT_DATE).append(UtilHelper.listingFormatLocalDate(reportDate)).append(NEW_LINE);
        sb.append(TOTAL_CASES).append(StringUtils.defaultString(totalCases, "0")).append(NEW_LINE);
        sb.append(TOTAL_SINGLE).append(StringUtils.defaultString(totalSingle, "0")).append(NEW_LINE);
        sb.append(TOTAL_MULTIPLE).append(StringUtils.defaultString(totalMultiple, "0")).append(NEW_LINE);
        return sb;
    }

    private StringBuilder buildDetailSingleJsonString(String caseReference, String year, String currentPosition,
                                                      String dateToPosition, String respondent) {
        var sb = new StringBuilder();
        sb.append("{");
        sb.append("\"caseReference\":\"").append(caseReference).append("\",");
        sb.append("\"year\":\"").append(year).append("\",");
        sb.append("\"currentPosition\":\"").append(currentPosition).append("\",");
        sb.append("\"dateToPosition\":\"").append(dateToPosition).append("\",");
        sb.append("\"respondent\":\"").append(respondent).append("\"");
        sb.append("}");
        return sb;
    }

    private StringBuilder buildDetailMultipleJsonString(String caseReference, String year, String currentPosition,
                                                        String dateToPosition, String multipleName) {
        var sb = new StringBuilder();
        sb.append("{");
        sb.append("\"caseReference\":\"").append(caseReference).append("\",");
        sb.append("\"year\":\"").append(year).append("\",");
        sb.append("\"currentPosition\":\"").append(currentPosition).append("\",");
        sb.append("\"dateToPosition\":\"").append(dateToPosition).append("\",");
        sb.append("\"multipleName\":\"").append(multipleName).append("\"");
        sb.append("}");
        return sb;
    }
}
