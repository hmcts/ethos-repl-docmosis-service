package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

import java.io.IOException;
import java.time.Clock;
import java.util.List;

@Slf4j
public class TimeToFirstHearingReport {


    public ListingData generateReportData(ListingDetails listingDetails, List<SubmitEvent> submitEvents) throws IOException {

        var reportData = initReport(listingDetails.getCaseTypeId());
        populateData(reportData, submitEvents);

        return reportData;
    }

    private ListingData initReport(String caseTypeId) {

        String ConNoneTotal;
        String ConStdTotal;
        String ConFastTotal;
        String ConOpenTotal;
        String ConNone26wkTotal;
        String ConStd26wkTotal;
        String ConFast26wkTotal;
        String ConOpen26wkTotal;
        String ConNone26wkTotalPerCent;
        String ConStd26wkTotalPerCent;
        String ConFast26wkTotalPerCent;
        String ConOpen26wkTotalPerCent;
        String xConNone26wkTotal;
        String xConStd26wkTotal;
        String xConFast26wkTotal;
        String xConOpen26wkTotal;
        String xConNone26wkTotalPerCent;
        String xConStd26wkTotalPerCent;
        String xConFast26wkTotalPerCent;
        String xConOpen26wkTotalPerCent;




        return null;
       // return new TimeToFirstHearingReportData(reportSummary);
    }

    private void populateData(ListingData reportData, List<SubmitEvent> submitEvents) {
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
