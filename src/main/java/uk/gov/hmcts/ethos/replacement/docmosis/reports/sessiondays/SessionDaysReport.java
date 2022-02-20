package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import com.microsoft.azure.servicebus.primitives.StringUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysCaseData;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice.JpaJudgeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION_TCC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;

public class SessionDaysReport {

    private final SessionDaysReportDataSource reportDataSource;
    private final JpaJudgeService jpaJudgeService;
    private static final String ONE_HOUR = "One Hour";
    private static final String HALF_DAY = "Half Day";
    private static final String FULL_DAY = "Full Day";
    private static final String NONE = "None";

    public SessionDaysReport(SessionDaysReportDataSource reportDataSource, JpaJudgeService jpaJudgeService) {
        this.reportDataSource = reportDataSource;
        this.jpaJudgeService = jpaJudgeService;
    }

    public SessionDaysReportData generateReport(String caseTypeId, String dateFrom, String dateTo) {

        var submitEvents = getCases(caseTypeId, dateFrom, dateTo);
        var reportData = initReport(caseTypeId);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(submitEvents, reportData);
        }
        return reportData;
    }

    private SessionDaysReportData initReport(String caseTypeId) {
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        var reportSummary = new SessionDaysReportSummary(office);
        reportSummary.setFtSessionDaysTotal("0");
        reportSummary.setPtSessionDaysTotal("0");
        reportSummary.setOtherSessionDaysTotal("0");
        reportSummary.setSessionDaysTotal("0");
        reportSummary.setPtSessionDaysPerCent("0.0");
        return new SessionDaysReportData(reportSummary);
    }

    private List<SessionDaysSubmitEvent> getCases(
            String caseTypeId, String listingDateFrom, String listingDateTo) {
        return reportDataSource.getData(UtilHelper.getListingCaseTypeId(
                caseTypeId), listingDateFrom, listingDateTo);
    }

    private void executeReport(List<SessionDaysSubmitEvent> submitEvents, SessionDaysReportData sessionDaysReportData) {
        setReportData(submitEvents, sessionDaysReportData);
    }

    private void setReportData(List<SessionDaysSubmitEvent> submitEvents, SessionDaysReportData reportData) {
        List<SessionDaysReportSummary2> sessionDaysReportSummary2List = new ArrayList<>();
        List<SessionDaysReportDetail> sessionDaysReportDetailList = new ArrayList<>();
        for(SessionDaysSubmitEvent submitEvent : submitEvents) {
            var c = submitEvent.getCaseData();
            setCaseReportSummaries(c, reportData.getReportSummary(), sessionDaysReportSummary2List);
            setReportDetail(c, sessionDaysReportDetailList);
        }
        int ft = Integer.parseInt(reportData.getReportSummary().getFtSessionDaysTotal());
        int pt = Integer.parseInt(reportData.getReportSummary().getPtSessionDaysTotal());
        int ot = Integer.parseInt(reportData.getReportSummary().getOtherSessionDaysTotal());
        int total = ft + pt + ot;
        int ptPercent = (pt / total) * 100;
        reportData.getReportSummary().setSessionDaysTotal(String.valueOf(total));
        reportData.getReportSummary().setPtSessionDaysPerCent(String.valueOf(ptPercent));
        reportData.addReportSummary2List(sessionDaysReportSummary2List);
        reportData.addReportDetail(sessionDaysReportDetailList);
    }

    private SessionDaysReportSummary2 getReportSummary2Item(DateListedType d, List<SessionDaysReportSummary2> sessionDaysReportSummary2List) {
        Optional<SessionDaysReportSummary2> item = sessionDaysReportSummary2List.stream().filter(i -> !Strings.isNullOrEmpty(i.getDate())
                && i.getDate().equals(d.getListedDate())).findFirst();
        if (item.isPresent()) {
            return item.get();
        }
        SessionDaysReportSummary2 summary2 = new SessionDaysReportSummary2();
        summary2.setDate(d.getListedDate());
        sessionDaysReportSummary2List.add(summary2);
        return summary2;
    }

        private void setCaseReportSummaries(SessionDaysCaseData caseData, SessionDaysReportSummary reportSummary, List<SessionDaysReportSummary2> sessionDaysReportSummary2List) {

        int ft = 0, pt = 0, ot = 0, ft2 = 0, pt2 = 0, ot2 = 0;
        for (HearingTypeItem h : getHearings(caseData)) {
            var dates = h.getValue().getHearingDateCollection();
            if (CollectionUtils.isNotEmpty(dates)) {
                for (DateListedTypeItem d : dates) {
                    if (isHearingStatusValid(d)) {
                        String judgeStatus = getJudgeStatus(h.getValue().getJudge());
                        SessionDaysReportSummary2 reportSummary2 = getReportSummary2Item(d.getValue(), sessionDaysReportSummary2List);
                        switch (judgeStatus) {
                            case "SALARIED":
                                 ft = Integer.parseInt(reportSummary.getFtSessionDaysTotal()) + 1;
                                reportSummary.setFtSessionDaysTotal(String.valueOf(ft));
                                 ft2 = Integer.parseInt(reportSummary2.getFtSessionDays()) + 1;
                                reportSummary2.setFtSessionDays(String.valueOf(ft2));
                                break;
                            case "FEES_PAID":
                                 pt = Integer.parseInt(reportSummary.getPtSessionDaysTotal()) + 1;
                                reportSummary.setPtSessionDaysTotal(String.valueOf(pt));
                                 pt2 = Integer.parseInt(reportSummary2.getPtSessionDays()) + 1;
                                reportSummary2.setPtSessionDays(String.valueOf(pt2));
                                break;
                            case "":
                                 ot = Integer.parseInt(reportSummary.getOtherSessionDaysTotal()) + 1;
                                reportSummary.setOtherSessionDaysTotal(String.valueOf(ot));
                                 ot2 = Integer.parseInt(reportSummary2.getOtherSessionDays()) + 1;
                                reportSummary2.setOtherSessionDays(String.valueOf(ot2));
                                break;
                            default:
                                break;
                        }
                        int total = ft2 + pt2 + ot2;
                        reportSummary2.setSessionDaysTotalDetail(String.valueOf(total));
                    }
                }
            }
        }
    }

    private boolean isHearingStatusValid (DateListedTypeItem d) {
        return HEARING_STATUS_HEARD.equals(d.getValue().getHearingStatus());
    }
    private String getJudgeStatus(String judge) {
      Judge j = jpaJudgeService.getJudge(judge);
      if (j != null) {
          return j.getEmploymentStatus().name();
      }
      return "";
    }

    private List<HearingTypeItem> getHearings(SessionDaysCaseData caseData) {
        var hearings = caseData.getHearingCollection();
        if (hearings == null) {
            return Collections.emptyList();
        }
        return hearings;
    }

    private void setReportDetail(SessionDaysCaseData caseData, List<SessionDaysReportDetail> reportDetailList) {
        for (HearingTypeItem h : getHearings(caseData)) {
            var dates = h.getValue().getHearingDateCollection();
            if (CollectionUtils.isNotEmpty(dates)) {
                for (DateListedTypeItem d : dates) {
                    if (isHearingStatusValid(d)) {
                        SessionDaysReportDetail reportDetail = new SessionDaysReportDetail();
                        reportDetail.setHearingDate(d.getValue().getListedDate());
                        reportDetail.setHearingJudge(Strings.isNullOrEmpty(h.getValue().getJudge()) ? "* Not Allocated" : h.getValue().getJudge());
                        String judgeStatus = getJudgeStatus(h.getValue().getJudge());
                        switch (judgeStatus) {
                            case "SALARIED":
                             reportDetail.setJudgeType("FTC");
                                break;
                            case "FEES_PAID":
                                reportDetail.setJudgeType("PTC");
                                break;
                            case "":
                                reportDetail.setJudgeType("*");
                                break;
                            default:
                                break;
                        }
                        reportDetail.setCaseReference(caseData.getEthosCaseReference());
                        reportDetail.setHearingNumber(h.getValue().getHearingNumber());
                        reportDetail.setHearingType(h.getValue().getHearingType());
                        reportDetail.setHearingSitAlone("Sit Alone".equals(h.getValue().getHearingSitAlone()) ? "Y" : "");
                        setTelCon(h, reportDetail);
                        String duration = calculateDuration(d);
                        reportDetail.setHearingDuration(duration);
                        reportDetail.setSessionType(getSessionType(duration));
                        reportDetail.setHearingClerk(d.getValue().getHearingClerk());
                    }
                }
            }
        }
    }

    private void setTelCon(HearingTypeItem h, SessionDaysReportDetail reportDetail) {
        if(HEARING_TYPE_JUDICIAL_MEDIATION_TCC.equals(h.getValue().getHearingType())) {
            reportDetail.setHearingTelConf("Y");
        } else {
            reportDetail.setHearingTelConf("");
        }
    }

    private String calculateDuration(DateListedTypeItem c) {
        var dateListedType = c.getValue();
        long duration = 0;
        long breakDuration = 0;

        var hearingTimingBreak = dateListedType.getHearingTimingBreak();
        var hearingTimingResume = dateListedType.getHearingTimingResume();
        //If there was a break and resumption during the hearing
        if(!StringUtil.isNullOrEmpty(hearingTimingBreak)
                && !StringUtil.isNullOrEmpty(hearingTimingResume)) {
            var hearingBreak = LocalDateTime.parse(hearingTimingBreak, OLD_DATE_TIME_PATTERN);
            var hearingResume = LocalDateTime.parse(hearingTimingResume, OLD_DATE_TIME_PATTERN);
            breakDuration = ChronoUnit.MINUTES.between(hearingBreak, hearingResume);
        }

        var hearingTimingStart = dateListedType.getHearingTimingStart();
        var hearingTimingFinish = dateListedType.getHearingTimingFinish();
        if(!StringUtil.isNullOrEmpty(hearingTimingStart)
                && !StringUtil.isNullOrEmpty(hearingTimingFinish)) {
            var hearingStartTime = LocalDateTime.parse(hearingTimingStart, OLD_DATE_TIME_PATTERN);
            var hearingEndTime = LocalDateTime.parse(hearingTimingFinish, OLD_DATE_TIME_PATTERN);
            long startToEndDiffInMinutes = ChronoUnit.MINUTES.between(hearingStartTime, hearingEndTime);
            duration = startToEndDiffInMinutes - breakDuration;
        }

        return String.valueOf(duration);
    }

    private String getSessionType(String hearingDuration) {
        String sessionType = NONE;
        var duration = Long.parseLong(hearingDuration);
        if(duration > 0 && duration < 60) {
            sessionType = ONE_HOUR;
        } else if (duration >= 60 && duration <= 180) {
            sessionType = HALF_DAY;
        } else if (duration > 180) {
            sessionType = FULL_DAY;
        }
        return sessionType;
    }

}
