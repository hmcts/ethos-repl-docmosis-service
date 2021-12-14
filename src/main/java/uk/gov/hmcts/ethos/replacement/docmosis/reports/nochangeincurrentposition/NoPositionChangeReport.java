package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        if (CollectionUtils.isEmpty(submitEvents)) {
            return reportData;
        }

        var multipleIds = submitEvents.stream()
                .filter(se -> se.getCaseData().getCaseType().equals(MULTIPLE_CASE_TYPE)
                        && StringUtils.isNotBlank(se.getCaseData().getMultipleReference()))
                .map(e -> e.getCaseData().getMultipleReference())
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(multipleIds)) {
            var submitMultipleEvents = getMultipleCases(caseTypeId, multipleIds);
            populateData(reportData, caseTypeId, submitEvents, submitMultipleEvents);
        } else {
            populateData(reportData, caseTypeId, submitEvents);
        }

        return reportData;
    }

    private NoPositionChangeReportData initReport(String caseTypeId) {
        var reportSummary = new NoPositionChangeReportSummary(UtilHelper.getListingCaseTypeId(caseTypeId));
        return new NoPositionChangeReportData(reportSummary, reportDate);
    }

    private List<NoPositionChangeSubmitEvent> getCases(String caseTypeId) {
        return noPositionChangeDataSource.getData(UtilHelper.getListingCaseTypeId(caseTypeId), reportDate);
    }

    private List<SubmitMultipleEvent> getMultipleCases(String casTypeId, List<String> multipleCaseIds) {
        var multipleCaseTypeId = UtilHelper.getListingCaseTypeId(casTypeId) + "_Multiple";
        return noPositionChangeDataSource.getMultiplesData(multipleCaseTypeId, multipleCaseIds);
    }

    private void populateData(NoPositionChangeReportData reportData,
                              String caseTypeId, List<NoPositionChangeSubmitEvent> submitEvents) {
        populateData(reportData, caseTypeId, submitEvents, new ArrayList<>());
    }

    private void populateData(NoPositionChangeReportData reportData,
                              String caseTypeId, List<NoPositionChangeSubmitEvent> submitEvents,
                              List<SubmitMultipleEvent> submitMultipleEvents) {
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
                var multipleCase = submitMultipleEvents.stream()
                        .filter(sme -> sme.getCaseData().getMultipleReference().equals(caseData.getMultipleReference()))
                        .findFirst();

                var reportDetailMultiple = new NoPositionChangeReportDetailMultiple();
                reportDetailMultiple.setCaseReference(caseData.getEthosCaseReference());
                reportDetailMultiple.setCurrentPosition(caseData.getCurrentPosition());
                reportDetailMultiple.setDateToPosition(caseData.getDateToPosition());
                reportDetailMultiple.setMultipleName(multipleCase.isPresent()
                        ? multipleCase.get().getCaseData().getMultipleName() : "");

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

        var reportDateMinus3Months = LocalDate.parse(reportDate, OLD_DATE_TIME_PATTERN2).minusMonths(3).plusDays(1);
        var dateToPosition = LocalDate.parse(caseData.getDateToPosition(), OLD_DATE_TIME_PATTERN2);
        return dateToPosition.isBefore(reportDateMinus3Months);
    }
}
