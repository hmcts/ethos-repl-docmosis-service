package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.time.Clock;
import java.util.List;

@Slf4j
public class TimeToFirstHearingReport {

    private final TimeToFirstHearingReportDataSource reportDataSource;
    private final Clock clock;

    public TimeToFirstHearingReport(TimeToFirstHearingReportDataSource reportDataSource) {
        this(reportDataSource, Clock.systemDefaultZone());
    }

    public TimeToFirstHearingReport(TimeToFirstHearingReportDataSource reportDataSource, Clock clock) {
        this.reportDataSource = reportDataSource;
        this.clock = clock;
    }

    public TimeToFirstHearingReportData runReport(String caseTypeId) {
        var submitEvents = getCases(caseTypeId);

        var reportData = initReport(caseTypeId);
        populateData(reportData, submitEvents);

        return reportData;
    }

    private TimeToFirstHearingReportData initReport(String caseTypeId) {
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);

       // var reportSummary = new TimeToFirstHearingReportSummary();
        return null;
       // return new TimeToFirstHearingReportData(reportSummary);
    }

    private List<SubmitEvent> getCases(String caseTypeId) {
        return reportDataSource.getData(UtilHelper.getListingCaseTypeId(caseTypeId));
    }

    private void populateData(TimeToFirstHearingReportData reportData, List<SubmitEvent> submitEvents) {
        for (SubmitEvent submitEvent : submitEvents) {
            if (!isValidCase(submitEvent)) {
                continue;
            }

            // var reportDetail = new ReportDetail();
            var caseData = submitEvent.getCaseData();
            log.info("Adding case {} to Cases Awaiting Judgment report", caseData.getEthosCaseReference());

//            var heardHearing = getLatestHeardHearing(caseData.getHearingCollection());
//            LocalDate today = LocalDate.now(clock);
//            LocalDate listedDate = LocalDate.parse(heardHearing.listedDate, OLD_DATE_TIME_PATTERN);
//
//            reportDetail.setPositionType(caseData.getPositionType());
//            reportDetail.setWeeksSinceHearing(getWeeksSinceHearing(listedDate, today));
//            reportDetail.setDaysSinceHearing(getDaysSinceHearing(listedDate, today));
//            reportDetail.setCaseNumber(caseData.getEthosCaseReference());
//            if (MULTIPLE_CASE_TYPE.equals(caseData.getCaseType())) {
//                reportDetail.setMultipleReference(caseData.getMultipleReference());
//            } else {
//                reportDetail.setMultipleReference(NO_MULTIPLE_REFERENCE);
//            }
//
//            reportDetail.setHearingNumber(heardHearing.hearingNumber);
//            reportDetail.setHearingType(heardHearing.hearingType);
//            reportDetail.setLastHeardHearingDate(formatDate(OLD_DATE_TIME_PATTERN, heardHearing.listedDate));
//            reportDetail.setJudge(heardHearing.judge);
//            reportDetail.setCurrentPosition(caseData.getCurrentPosition());
//            reportDetail.setDateToPosition(formatDate(OLD_DATE_TIME_PATTERN2, caseData.getDateToPosition()));
//            reportDetail.setConciliationTrack(caseData.getConciliationTrack());
//
//            reportData.addReportDetail(reportDetail);
//        }
//
//        sortReportDetails(reportData);
//        addReportSummary(reportData);
        }


    }
    private boolean isValidCase (SubmitEvent submitEvent) {
        return true;
    }
}
