package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;

import java.time.LocalDate;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

@Slf4j
public class NoPositionChangeReport {
    private final NoPositionChangeDataSource noPositionChangeDataSource;
    private final String reportDate;

    public NoPositionChangeReport(NoPositionChangeDataSource noPositionChangeDataSource, String reportDate) {
        this.noPositionChangeDataSource = noPositionChangeDataSource;
        this.reportDate = reportDate;
    }

    public NoPositionChangeReportData runReport(String caseTypeId) {
        var submitEvents = getCases(caseTypeId);
        var reportData = initReport(caseTypeId);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            populateData(reportData, submitEvents, caseTypeId);
        }

        return reportData;
    }

    private NoPositionChangeReportData initReport(String caseTypeId) {
        var reportSummary = new NoPositionChangeReportSummary(UtilHelper.getListingCaseTypeId(caseTypeId));
        return new NoPositionChangeReportData(reportSummary);
    }

    private List<NoPositionChangeSubmitEvent> getCases(String caseTypeId) {
        return noPositionChangeDataSource.getData(UtilHelper.getListingCaseTypeId(caseTypeId), reportDate);
    }

    private void populateData(NoPositionChangeReportData reportData,
                              List<NoPositionChangeSubmitEvent> submitEvents,
                              String caseTypeId) {
        log.info(String.format("No change in current position case type id %s search results: %d",
                caseTypeId, submitEvents.size()));

        for (var submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (!isValidCase(caseData)) {
                continue;
            }

            if (caseData.getCaseType().equals(SINGLE_CASE_TYPE)) {
                var reportDetailSingle = new NoPositionChangeReportDetailSingle();
                reportDetailSingle.setCaseReference(caseData.getEthosCaseReference());
                reportDetailSingle.setCurrentPosition(caseData.getCurrentPosition());
                reportDetailSingle.setDateToPosition(caseData.getDateToPosition());

                var year = LocalDate.parse(caseData.getReceiptDate(), OLD_DATE_TIME_PATTERN2).getYear();
                reportDetailSingle.setYear(String.valueOf(year));

                var hasMultipleRespondents = CollectionUtils.isNotEmpty(caseData.getRespondentCollection())
                        && caseData.getRespondentCollection().size() > 1;
                var respondent = caseData.getRespondent() + (hasMultipleRespondents ? " & Others" : "");
                reportDetailSingle.setRespondent(respondent);

                reportData.addReportDetailsSingle(reportDetailSingle);
            } else if (caseData.getCaseType().equals(MULTIPLE_CASE_TYPE)) {
                var reportDetailMultiple = new NoPositionChangeReportDetailMultiple();
                reportDetailMultiple.setCaseReference(caseData.getEthosCaseReference());
                reportDetailMultiple.setCurrentPosition(caseData.getCurrentPosition());
                reportDetailMultiple.setDateToPosition(caseData.getDateToPosition());
                reportDetailMultiple.setMultipleName(caseData.getMultipleReference());

                var year = LocalDate.parse(caseData.getReceiptDate(), OLD_DATE_TIME_PATTERN2).getYear();
                reportDetailMultiple.setYear(String.valueOf(year));

                reportData.addReportDetailsMultiple(reportDetailMultiple);
            }
        }

        reportData.getReportSummary().setTotalCases(
                String.valueOf(reportData.getReportDetailsSingle().size()
                        + reportData.getReportDetailsMultiple().size()));
        reportData.getReportSummary().setTotalSingleCases(
                String.valueOf(reportData.getReportDetailsSingle().size()));
        reportData.getReportSummary().setTotalMultipleCases(
                String.valueOf(reportData.getReportDetailsMultiple().size()));
    }

    private boolean isValidCase(NoPositionChangeCaseData caseData) {
        if (caseData == null) {
            return false;
        }

        if (StringUtils.isBlank(caseData.getCurrentPosition())
                || StringUtils.isBlank(caseData.getDateToPosition())) {
            return false;
        }

        var reportDateMinus3Months = LocalDate.parse(reportDate, OLD_DATE_TIME_PATTERN2).minusMonths(3);
        var dateToPosition = LocalDate.parse(caseData.getDateToPosition(), OLD_DATE_TIME_PATTERN2);
        return dateToPosition.isBefore(reportDateMinus3Months);
    }
}
