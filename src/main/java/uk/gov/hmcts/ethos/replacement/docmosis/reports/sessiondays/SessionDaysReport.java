package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;

public class SessionDaysReport {

    private final SessionDaysReportDataSource reportDataSource;

    public SessionDaysReport(SessionDaysReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public SessionDaysReportData generateReport(String caseTypeId, String dateFrom, String dateTo) {

        var submitEvents = getCases(caseTypeId, dateFrom, dateTo);
        var reportData = initReport(caseTypeId);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(reportData, submitEvents);
        }
        return reportData;
    }

    private SessionDaysReportData initReport(String caseTypeId) {
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        var reportSummary = new SessionDaysReportSummary(office);
        var reportSummary2 = new SessionDaysReportSummary2();
        return new SessionDaysReportData(reportSummary, reportSummary2);
    }

    private List<SessionDaysSubmitEvent> getCases(
            String caseTypeId, String listingDateFrom, String listingDateTo) {
        return reportDataSource.getData(UtilHelper.getListingCaseTypeId(
                caseTypeId), listingDateFrom, listingDateTo);
    }

    private void executeReport(SessionDaysReportData sessionDaysReportData,
                               List<SessionDaysSubmitEvent> submitEvents) {
        setReportSummary(sessionDaysReportData);
        setReportSummary2(sessionDaysReportData);
        sessionDaysReportData.addReportDetail(setReportDetail(submitEvents));
    }

    private void setReportSummary(SessionDaysReportData sessionDaysReportData) {

        List<CaseData> cases = new ArrayList<>();
        for(CaseData c : cases) {
           // if (checkIfValidCase(c)) {

            //}
        }
    }


    private List<HearingTypeItem> casesHeard(CaseData caseData) {
        var hearingSessions = getHearings(caseData);
        return new ArrayList<>();
        //return hearingSessions.stream()
                //.filter(h -> HEARING_STATUS_HEARD.equals(h.getDateListedType().getHearingStatus()))
                //.collect(Collectors.toList());

    }

    private List<HearingTypeItem> getHearings(CaseData caseData) {
        var hearings = caseData.getHearingCollection();
        if (hearings == null) {
            return Collections.emptyList();
        }
        return hearings;
    }

    private void setReportSummary2(SessionDaysReportData sessionDaysReportData) {

    }

    private List<SessionDaysReportDetail> setReportDetail(List<SessionDaysSubmitEvent> submitEvents) {
       return new ArrayList<>();
    }
}
