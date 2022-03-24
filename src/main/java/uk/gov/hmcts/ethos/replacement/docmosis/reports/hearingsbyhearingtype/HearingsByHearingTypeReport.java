package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Math.abs;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

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
        var reportSummaryHdr = new HearingsByHearingTypeReportSummaryHdr();
        var fields = new ReportFields();
        initReportFields(fields);
        reportSummaryHdr.setOffice(UtilHelper.getListingCaseTypeId(caseTypeId));
        reportSummaryHdr.setFields(fields);
        return new HearingsByHearingTypeReportData(reportSummaryHdr);
    }

    private void initReportFields(ReportFields reportFields) {
        reportFields.setHearingCount("0");
        reportFields.setCmCount("0");
        reportFields.setCostsCount("0");
        reportFields.setTotal("0");
        reportFields.setHearingPrelimCount("0");
        reportFields.setReconsiderCount("0");
        reportFields.setRemedyCount("0");
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

    private void initReportSummary2(HearingsByHearingTypeReportSummary2 reportSummary2) {
        reportSummary2.setSubSplit("");
        reportSummary2.setDate("");
        var reportFields = new ReportFields();
        initReportFields(reportFields);
        reportSummary2.setFields(reportFields);
    }

    private void initReportSummary2HdrList(List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Full Panel"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("EJ Sit Alone"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("JM"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Tel Con"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Video"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Hybrid"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("In person"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Stage 1"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Stage 2"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Stage 3"));


    }

    private String getSubSplit(HearingTypeItem hearingTypeItem) {
        if ("Full".equals(hearingTypeItem.getValue().getHearingSitAlone())) {
            return "Full Panel";
        }
        if (YES.equals(hearingTypeItem.getValue().getHearingSitAlone())) {
            return "EJ Sit Alone";
        }
        if ("JM".equals(hearingTypeItem.getValue().getJudicialMediation())) {
            return "JM";
        }

        if ("Stage 1".equals(hearingTypeItem.getValue().getHearingStage())) {
            return "Stage 1";
        }
        if ("Stage 2".equals(hearingTypeItem.getValue().getHearingStage())) {
            return "Stage 2";
        }
        if ("Stage 3".equals(hearingTypeItem.getValue().getHearingStage())) {
            return "Stage 3";
        }
        return "";
    }

    private String getsubSplitHearingFormat(String format) {

            if ("Telephone".equals(format)) {
                return "Tel Con";
            }
            if ("Video".equals(format)) {
                return "Video";
            }
            if ("Hybrid".equals(format)) {
                return "Hybrid";
            }
            if ("In person".equals(format)) {
                return "In person";
            }

        return "";
    }

    private List<HearingsByHearingTypeSubmitEvent> getCases(ReportParams params) {
        return reportDataSource.getData(UtilHelper.getListingCaseTypeId(
                params.getCaseTypeId()), params.getDateFrom(), params.getDateTo());
    }

    private void executeReport(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        setReportData(submitEvents, reportData);
    }

    private void setReportData(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        setLocalReportSummaryHdr(submitEvents, reportData);
        setLocalReportSummary(submitEvents, reportData);
        setLocalReportSummaryHdr2(submitEvents, reportData);
        setLocalReportSummary2(submitEvents, reportData);
        setLocalReportSummaryDetail(submitEvents, reportData);
    }

    private void setLocalReportSummaryHdr(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        setHdrFields(hearingTypeItem, reportData.getReportSummaryHdr().getFields());
                    }
                }
            }
        }
    }

    private void setHdrFields(HearingTypeItem hearingTypeItem, ReportFields fields) {
        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
            if (isValidHearing(dateListedTypeItem.getValue())) {
                setReportFields(hearingTypeItem.getValue().getHearingType(), fields);
            }
        }
    }

    private void setReportFields(String hearingType, ReportFields fields) {
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

    private HearingsByHearingTypeReportSummary getSummaryRow(String dateListed, List<HearingsByHearingTypeReportSummary> reportSummaryList) {
        var date = LocalDateTime.parse(
                dateListed, NEW_DATE_PATTERN).toLocalDate().toString();
        var reportSummary = reportSummaryList.stream().filter(a -> date.equals(a.getDate())).findFirst();
        if (reportSummary.isPresent()) {
           return reportSummary.get();
        } else {
            var newReportSummary = new HearingsByHearingTypeReportSummary();
            newReportSummary.setDate(date.replace("T", " "));
            reportSummaryList.add(newReportSummary);
            return newReportSummary;
        }
    }

    private HearingsByHearingTypeReportSummary2 getSummaryRow2(String dateListed, String subSplit, List<HearingsByHearingTypeReportSummary2> reportSummaryList2) {
        var date = LocalDateTime.parse(
                dateListed, NEW_DATE_PATTERN).toLocalDate().toString();
        var reportSummary2 = reportSummaryList2.stream().filter(a -> date.equals(a.getDate()) && subSplit.equals(a.getSubSplit())).findFirst();
        if (reportSummary2.isPresent()) {
            return reportSummary2.get();
        } else {
            var newReportSummary2 = new HearingsByHearingTypeReportSummary2();
            newReportSummary2.setDate(date.replace("T", " "));
            newReportSummary2.setSubSplit(subSplit);
            reportSummaryList2.add(newReportSummary2);
            return newReportSummary2;
        }
    }

    private HearingsByHearingTypeReportSummary2Hdr getSummaryHdr2Row(String subSplit, List<HearingsByHearingTypeReportSummary2Hdr> reportSummaryHdr2List) {

        var reportSummaryHdr2 = reportSummaryHdr2List.stream().filter(a -> subSplit.equals(a.getSubSplit())).findFirst();
        return reportSummaryHdr2.orElse(null);
    }

    private void setLocalReportSummary(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportSummary> reportSummaryList = new ArrayList<>();
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                            if (isValidHearing(dateListedTypeItem.getValue())) {
                                var reportSummary = getSummaryRow(dateListedTypeItem.getValue().getListedDate(), reportSummaryList);
                                setReportFields(hearingTypeItem.getValue().getHearingType(), reportSummary.getFields());
                            }
                        }

                    }
                }
            }
        }
        reportData.addReportSummaryList(reportSummaryList);
    }

    private void setLocalReportSummary2(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportSummary2> reportSummaryList2 = new ArrayList<>();
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                            if (isValidHearing(dateListedTypeItem.getValue())) {
                                if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat())) {
                                    for (String format : hearingTypeItem.getValue().getHearingFormat()) {
                                        String subSplit = getsubSplitHearingFormat(format);
                                        var reportSummary2 = getSummaryRow2(dateListedTypeItem.getValue().getListedDate(), subSplit, reportSummaryList2);
                                        setReportFields(hearingTypeItem.getValue().getHearingType(), reportSummary2.getFields());
                                    }
                                } else {
                                    String subSplit = getSubSplit(hearingTypeItem);
                                    var reportSummary2 = getSummaryRow2(dateListedTypeItem.getValue().getListedDate(),subSplit, reportSummaryList2);
                                    setReportFields(hearingTypeItem.getValue().getHearingType(), reportSummary2.getFields());
                                }
                            }
                        }

                    }
                }
            }
        }
        reportData.addReportSummary2List(reportSummaryList2);
    }

    private void setLocalReportSummaryDetail(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportDetail> reportSummaryDetailList = new ArrayList<>();
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                            if (isValidHearing(dateListedTypeItem.getValue())) {
                              var detail = new HearingsByHearingTypeReportDetail();
                              detail.setDate(dateListedTypeItem.getValue().getListedDate().replace("T", " "));
                              detail.setHearingType(hearingTypeItem.getValue().getHearingType());
                              detail.setHearingNo(hearingTypeItem.getValue().getHearingNumber());
                              var mulRef = StringUtils.defaultString(caseData.getMultipleReference(), "0 -  Not Allocated");
                              var subMul = StringUtils.defaultString(caseData.getSubMultipleName(), "0 -  Not Allocated");
                              detail.setMultiSub(mulRef + ", " + subMul);
                              detail.setCaseReference(caseData.getEthosCaseReference());
                              detail.setLead(Strings.isNullOrEmpty(caseData.getLeadClaimant()) ? "" : "Y");
                              detail.setTel(CollectionUtils.isNotEmpty(
                                        hearingTypeItem.getValue().getHearingFormat())
                                        && hearingTypeItem.getValue().getHearingFormat()
                                        .contains("Telephone") ? "Y" : "");
                              detail.setJm("JM".equals(
                                        hearingTypeItem.getValue().getJudicialMediation()) ? "Y" : "");
                              detail.setHearingClerk(Strings.isNullOrEmpty(
                                      dateListedTypeItem.getValue().getHearingClerk()) ? ""
                                        : dateListedTypeItem.getValue().getHearingClerk());
                              detail.setDuration(getHearingDuration(dateListedTypeItem));
                              reportSummaryDetailList.add(detail);
                            }
                        }

                    }
                }
            }
        }
        reportData.addReportDetail(reportSummaryDetailList);
    }

    private String getHearingDuration(DateListedTypeItem dateListedTypeItem) {
        LocalDateTime startTime;
        LocalDateTime finishTime;
        LocalDateTime resumeTime;
        LocalDateTime breakTime;
        if (!Strings.isNullOrEmpty(dateListedTypeItem.getValue().getHearingTimingStart())) {
            startTime = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingStart());
        } else {
            return "0";
        }
        if (!Strings.isNullOrEmpty(dateListedTypeItem.getValue().getHearingTimingFinish())) {
            finishTime = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingFinish());
        } else {
            return "0";
        }
        long hearingDuration = ChronoUnit.MINUTES.between(startTime, finishTime);
        if (!Strings.isNullOrEmpty(dateListedTypeItem.getValue().getHearingTimingResume())) {
            resumeTime = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingResume());
        } else {
            return String.valueOf(hearingDuration);
        }
        if (!Strings.isNullOrEmpty(dateListedTypeItem.getValue().getHearingTimingBreak())) {
            breakTime = LocalDateTime.parse(dateListedTypeItem.getValue().getHearingTimingBreak());
        } else {
            return String.valueOf(hearingDuration);
        }
        long diff = hearingDuration - ChronoUnit.MINUTES.between(breakTime, resumeTime);
        return String.valueOf(abs(diff));
    }

    private void setLocalReportSummaryHdr2(List<HearingsByHearingTypeSubmitEvent> submitEvents, HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList = new ArrayList<>();
        initReportSummary2HdrList(reportSummary2HdrList);
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                            if (isValidHearing(dateListedTypeItem.getValue())) {
                                if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat())) {
                                   for (String format : hearingTypeItem.getValue().getHearingFormat()) {
                                       String subSplit = getsubSplitHearingFormat(format);
                                       setReportSummary2HdrFields(subSplit, hearingTypeItem, reportSummary2HdrList);
                                   }
                                } else {
                                    String subSplit = getSubSplit(hearingTypeItem);
                                    setReportSummary2HdrFields(subSplit, hearingTypeItem, reportSummary2HdrList);
                                }
                            }
                        }

                    }
                }
            }
        }
        reportData.addReportSummary2HdrList(reportSummary2HdrList);
    }

    private void setReportSummary2HdrFields(String subSplit, HearingTypeItem hearingTypeItem, List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {
        var reportSummary2Hdr = getSummaryHdr2Row(subSplit, reportSummary2HdrList);
        if (reportSummary2Hdr != null) {
            setReportFields(hearingTypeItem.getValue().getHearingType(), reportSummary2Hdr.getFields());
        }
    }
}
