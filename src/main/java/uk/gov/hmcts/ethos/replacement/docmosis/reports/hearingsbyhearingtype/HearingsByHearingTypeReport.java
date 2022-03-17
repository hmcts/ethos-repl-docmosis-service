package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import com.google.common.base.Strings;
import static com.google.common.base.Strings.isNullOrEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportSummary2;

public class HearingsByHearingTypeReport {
    private final HearingsByHearingTypeReportDataSource reportDataSource;
    private static final String COSTS_HEARING_TYPE = "Costs Hearing";
    private static final String COSTS_HEARING_TYPE_SCOTLAND = "Expenses/Wasted Costs Hearing";
    private String dateFrom;
    private String dateTo;

    public HearingsByHearingTypeReport(HearingsByHearingTypeReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public HearingsByHearingTypeReportData generateReport(ReportParams params) {

        var submitEvents = getCases(params);
        var reportData = initReport(params.getCaseTypeId());
        this.dateFrom = params.getDateFrom();
        this.dateTo = params.getDateTo();
        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(submitEvents, reportData);
        }
        return reportData;
    }

    private HearingsByHearingTypeReportData initReport(String caseTypeId) {
        //office = UtilHelper.getListingCaseTypeId(caseTypeId);
        var reportSummaryHdr = new ReportFields();
        reportSummaryHdr.setHearingCount("0");
        reportSummaryHdr.setCmCount("0");
        reportSummaryHdr.setCostsCount("0");
        reportSummaryHdr.setTotal("0");
        reportSummaryHdr.setHearingPrelimCount("0");
        reportSummaryHdr.setReconsiderCount("0");
        reportSummaryHdr.setRemedyCount("0");
        return new HearingsByHearingTypeReportData(reportSummaryHdr);
    }

    private boolean isValidHearing(DateListedType dateListedType) {
        LocalDate listedDate;
        var startDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(dateFrom));
        var endDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(dateTo));
        if (isNullOrEmpty(dateListedType.getListedDate())) {
            return false;
        } else {
            listedDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(dateListedType.getListedDate()));
        }
        if (HEARING_STATUS_HEARD.equals(dateListedType.getHearingStatus())) {
            return (listedDate.isEqual(startDate) || listedDate.isAfter(startDate))
                    && (listedDate.isEqual(endDate) || listedDate.isBefore(endDate));
        }
        return false;
    }

    private void initReportSummary2(SessionDaysReportSummary2 reportSummary2) {
        reportSummary2.setPtSessionDays("0");
        reportSummary2.setOtherSessionDays("0");
        reportSummary2.setFtSessionDays("0");
        reportSummary2.setSessionDaysTotalDetail("0");
    }

    private List<HearingsByHearingTypeSubmitEvent> getCases(ReportParams params) {
        return reportDataSource.getData(UtilHelper.getListingCaseTypeId(
                params.getCaseTypeId()), params.getDateFrom(), params.getDateTo());
    }

    private void executeReport(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        setReportData(submitEvents, reportData);
    }

    private void setReportData(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        setLocalReportSummaries(submitEvents, reportData);

    }

    private void setLocalReportSummaries(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
       List<HearingsByHearingTypeReportSummary> reportSummaryList = new ArrayList<>();
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        setFields(hearingTypeItem, reportData.getReportSummaryHdr(),reportSummaryList );
                    }
                }
            }
        }
    }

    private HearingsByHearingTypeReportSummary getDate(String dateListed, List<HearingsByHearingTypeReportSummary> reportSummaryList) {
        var date = LocalDateTime.parse(
                dateListed, NEW_DATE_PATTERN).toLocalDate().toString();
        if (!Strings.isNullOrEmpty(date) && reportSummaryList.stream().anyMatch(a -> date.equals(a.getDate()))) {

        }
    }
    private void setFields(HearingTypeItem hearingTypeItem, ReportFields fields, List<HearingsByHearingTypeReportSummary> reportSummaryList) {
        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
            if (isValidHearing(dateListedTypeItem.getValue())) {
                setHdrFields(hearingTypeItem.getValue().getHearingType(), fields);
                setSummaryFields
            }
        }
    }

    private void setSummaryFields(String dateListed, ReportFields fields, List<HearingsByHearingTypeReportSummary> reportSummaryList) {
        String date = getDate(dateListed);
    }

    private void setHdrFields(String hearingType, ReportFields fields) {
        switch (hearingType) {
            case HEARING_TYPE_JUDICIAL_HEARING:
                fields.setHearingCount(String.valueOf(Integer.parseInt(fields.getHearingCount()) + 1));
                fields.setTotal(String.valueOf(Integer.parseInt(fields.getTotal()) + 1));
                break;
            case HEARING_TYPE_PERLIMINARY_HEARING_CM:
                fields.setCmCount(String.valueOf(Integer.parseInt(fields.getCmCount()) + 1));
                fields.setTotal(String.valueOf(Integer.parseInt(fields.getTotal()) + 1));
                break;
            case HEARING_TYPE_PERLIMINARY_HEARING:
                fields.setHearingPrelimCount(String.valueOf(Integer.parseInt(fields.getHearingPrelimCount()) + 1));
                fields.setTotal(String.valueOf(Integer.parseInt(fields.getTotal()) + 1));
                break;
            case COSTS_HEARING_TYPE:
            case COSTS_HEARING_TYPE_SCOTLAND:
                fields.setCostsCount(String.valueOf(Integer.parseInt(fields.getCostsCount()) + 1));
                fields.setTotal(String.valueOf(Integer.parseInt(fields.getTotal()) + 1));
                break;
            case HEARING_TYPE_JUDICIAL_RECONSIDERATION:
                fields.setReconsiderCount(String.valueOf(Integer.parseInt(fields.getReconsiderCount()) + 1));
                fields.setTotal(String.valueOf(Integer.parseInt(fields.getTotal()) + 1));
                break;
            case HEARING_TYPE_JUDICIAL_REMEDY:
                fields.setRemedyCount(String.valueOf(Integer.parseInt(fields.getRemedyCount()) + 1));
                fields.setTotal(String.valueOf(Integer.parseInt(fields.getTotal()) + 1));
                break;
            default:
                break;
        }
    }
}
