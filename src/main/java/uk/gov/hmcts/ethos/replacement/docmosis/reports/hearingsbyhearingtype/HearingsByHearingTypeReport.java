package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeCaseData;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_RECONSIDERATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_REMEDY;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper.TWO_JUDGES;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportCommonMethods.getHearingDurationInMinutes;

public class HearingsByHearingTypeReport {
    private final HearingsByHearingTypeReportDataSource reportDataSource;
    private static final String COSTS_HEARING_TYPE = "Costs Hearing";
    private static final String VIDEO = "Video";
    private static final String HYBRID = "Hybrid";
    private static final String IN_PERSON = "In person";
    private static final String STAGE_1 = "Stage 1";
    private static final String STAGE_2 = "Stage 2";
    private static final String STAGE_3 = "Stage 3";
    private static final String SIT_ALONE = "Sit Alone";
    private static final String FULL_PANEL = "Full Panel";

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
        reportFields.setSubSplit("");
        reportFields.setDate("");
    }

    private boolean isValidHearing(DateListedType dateListedType) {
        if (isNullOrEmpty(dateListedType.getListedDate())) {
            return false;
        } else {
            LocalDate listedDate;
            listedDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(dateListedType.getListedDate()));
            if (HEARING_STATUS_HEARD.equals(dateListedType.getHearingStatus())) {
                var startDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(dateFrom));
                var endDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(dateTo));
                return !listedDate.isBefore(startDate) && !listedDate.isAfter(endDate);
            } else {
                return false;
            }
        }
    }

    private void initReportSummary2HdrList(List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(FULL_PANEL));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(SIT_ALONE));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(TWO_JUDGES));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("JM"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr("Tel Con"));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(VIDEO));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(HYBRID));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(IN_PERSON));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(STAGE_1));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(STAGE_2));
        reportSummary2HdrList.add(new HearingsByHearingTypeReportSummary2Hdr(STAGE_3));

    }

    private String getSubSplitSitAlone(HearingTypeItem hearingTypeItem) {
        if (!Strings.isNullOrEmpty(hearingTypeItem.getValue().getHearingSitAlone()))  {
            if (List.of(FULL_PANEL, "Full").contains(hearingTypeItem.getValue().getHearingSitAlone())) {
                return FULL_PANEL;
            } else if (List.of(SIT_ALONE, YES).contains(hearingTypeItem.getValue().getHearingSitAlone())) {
                return SIT_ALONE;
            } else if (TWO_JUDGES.equals(hearingTypeItem.getValue().getHearingSitAlone())) {
                return TWO_JUDGES;
            }
        }
        return "";
    }

    private boolean isSubSplitJM(HearingTypeItem hearingTypeItem) {
        return YES.equals(hearingTypeItem.getValue().getJudicialMediation());
    }

    private String getSubSplitStages(HearingTypeItem hearingTypeItem) {
        if (STAGE_1.equals(hearingTypeItem.getValue().getHearingStage())) {
            return STAGE_1;
        } else if (STAGE_2.equals(hearingTypeItem.getValue().getHearingStage())) {
            return STAGE_2;
        } else if (STAGE_3.equals(hearingTypeItem.getValue().getHearingStage())) {
            return STAGE_3;
        } else {
            return "";
        }
    }

    private String getSubSplitHearingFormat(String format) {
        if ("Telephone".equals(format)) {
            return "Tel Con";
        }
        if (VIDEO.equals(format)) {
            return VIDEO;
        }
        if (HYBRID.equals(format)) {
            return HYBRID;
        }
        if (IN_PERSON.equals(format)) {
            return IN_PERSON;
        }
        return "";
    }

    private List<HearingsByHearingTypeSubmitEvent> getCases(ReportParams params) {
        return reportDataSource.getData(params);
    }

    private void executeReport(
            List<HearingsByHearingTypeSubmitEvent> submitEvents,
            HearingsByHearingTypeReportData reportData) {
        setReportData(submitEvents, reportData);
    }

    private void setReportData(
            List<HearingsByHearingTypeSubmitEvent> submitEvents,
            HearingsByHearingTypeReportData reportData) {
        setLocalReportSummaryHdr(submitEvents, reportData);
        setLocalReportSummary(submitEvents, reportData);
        setLocalReportSummaryHdr2(submitEvents, reportData);
        setLocalReportSummary2(submitEvents, reportData);
        setLocalReportSummaryDetail(submitEvents, reportData);
        reportData.getReportSummaryList().sort(Comparator.comparing(s -> s.getFields().getDate()));
        reportData.getReportSummary2List().sort(Comparator.comparing(s -> s.getFields().getDate()));
        reportData.getReportDetails().sort(Comparator.comparing(HearingsByHearingTypeReportDetail::getDetailDate));
    }

    private void setLocalReportSummaryHdr(
            List<HearingsByHearingTypeSubmitEvent> submitEvents,
            HearingsByHearingTypeReportData reportData) {
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

    private HearingsByHearingTypeReportSummary getSummaryRow(
            String dateListed, List<HearingsByHearingTypeReportSummary> reportSummaryList) {
        var date = LocalDateTime.parse(
                dateListed, OLD_DATE_TIME_PATTERN).toLocalDate().toString();
        var reportSummary = reportSummaryList.stream().filter(a -> date.equals(a.getFields().getDate())).findFirst();
        if (reportSummary.isPresent()) {
            return reportSummary.get();
        } else {
            var newReportSummary = new HearingsByHearingTypeReportSummary();
            var fields = new ReportFields();
            initReportFields(fields);
            fields.setDate(date.replace("T", " "));
            newReportSummary.setFields(fields);
            reportSummaryList.add(newReportSummary);
            return newReportSummary;
        }
    }

    private HearingsByHearingTypeReportSummary2 getSummaryRow2(
            String dateListed, String subSplit,
            List<HearingsByHearingTypeReportSummary2> reportSummaryList2) {
        var date = LocalDateTime.parse(
                dateListed, OLD_DATE_TIME_PATTERN).toLocalDate().toString();
        var reportSummary2 =
                reportSummaryList2.stream().filter(a -> date.equals(a.getFields().getDate())
                        && subSplit.equals(a.getFields().getSubSplit())).findFirst();
        if (reportSummary2.isPresent()) {
            return reportSummary2.get();
        } else {
            var newReportSummary2 = new HearingsByHearingTypeReportSummary2();
            var fields = new ReportFields();
            initReportFields(fields);
            newReportSummary2.setFields(fields);
            newReportSummary2.getFields().setDate(date.replace("T", " "));
            newReportSummary2.getFields().setSubSplit(subSplit);
            reportSummaryList2.add(newReportSummary2);
            return newReportSummary2;
        }
    }

    private HearingsByHearingTypeReportSummary2Hdr getSummaryHdr2Row(
            String subSplit, List<HearingsByHearingTypeReportSummary2Hdr> reportSummaryHdr2List) {

        var reportSummaryHdr2 = reportSummaryHdr2List
                .stream().filter(a -> a.getFields() != null
                        && subSplit.equals(a.getFields().getSubSplit())).findFirst();
        return reportSummaryHdr2.orElse(null);
    }

    private void setLocalReportSummary(
            List<HearingsByHearingTypeSubmitEvent> submitEvents,
            HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportSummary> reportSummaryList = new ArrayList<>();
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        setSummaryFields(hearingTypeItem, reportSummaryList);
                    }
                }
            }
        }

        reportData.addReportSummaryList(reportSummaryList);
    }

    private void setSummaryFields(
            HearingTypeItem hearingTypeItem,
            List<HearingsByHearingTypeReportSummary> reportSummaryList) {
        for (DateListedTypeItem dateListedTypeItem :
                hearingTypeItem.getValue().getHearingDateCollection()) {
            if (isValidHearing(dateListedTypeItem.getValue())) {
                var reportSummary = getSummaryRow(
                        dateListedTypeItem.getValue().getListedDate(), reportSummaryList);
                setReportFields(hearingTypeItem.getValue().getHearingType(),
                        reportSummary.getFields());
            }
        }
    }

    private void setLocalReportSummary2(
            List<HearingsByHearingTypeSubmitEvent> submitEvents,
            HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportSummary2> reportSummaryList2 = new ArrayList<>();
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    setSummary2FieldsForAllDates(hearingTypeItem, reportSummaryList2);
                }
            }
        }
        reportData.addReportSummary2List(reportSummaryList2);
    }

    private void setSummary2FieldsForAllDates(HearingTypeItem hearingTypeItem,
                    List<HearingsByHearingTypeReportSummary2> reportSummaryList2) {
        if (hearingTypeItem.getValue() != null
                && CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
            for (DateListedTypeItem dateListedTypeItem :
                    hearingTypeItem.getValue().getHearingDateCollection()) {
                setSummary2Fields(hearingTypeItem, dateListedTypeItem, reportSummaryList2);
            }
        }
    }

    private void setSummary2Fields(HearingTypeItem hearingTypeItem,
                                    DateListedTypeItem dateListedTypeItem,
                                    List<HearingsByHearingTypeReportSummary2> reportSummaryList2) {
        if (isValidHearing(dateListedTypeItem.getValue())) {
            if (hearingTypeItem.getValue() != null
                    && CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat())) {
                for (String format : hearingTypeItem.getValue().getHearingFormat()) {
                    String subSplit = getSubSplitHearingFormat(format);
                    var reportSummary2 = getSummaryRow2(
                            dateListedTypeItem.getValue().getListedDate(),
                            subSplit, reportSummaryList2);
                    setReportFields(
                            hearingTypeItem.getValue().getHearingType(),
                            reportSummary2.getFields());
                }
            }
            if (isSubSplitJM(hearingTypeItem)) {
                var reportSummary2 = getSummaryRow2(
                        dateListedTypeItem.getValue().getListedDate(), "JM",
                        reportSummaryList2);
                setReportFields(
                        hearingTypeItem.getValue().getHearingType(),
                        reportSummary2.getFields());
            }

            String subSplitStages = getSubSplitStages(hearingTypeItem);
            if (!Strings.isNullOrEmpty(subSplitStages)) {
                var reportSummary2 = getSummaryRow2(
                        dateListedTypeItem.getValue().getListedDate(), subSplitStages,
                        reportSummaryList2);
                setReportFields(
                        hearingTypeItem.getValue().getHearingType(),
                        reportSummary2.getFields());
            }

            String subSplitSitAlone = getSubSplitSitAlone(hearingTypeItem);
            if (!Strings.isNullOrEmpty(subSplitSitAlone)) {
                var reportSummary2 = getSummaryRow2(
                        dateListedTypeItem.getValue().getListedDate(), subSplitSitAlone,
                        reportSummaryList2);
                setReportFields(
                        hearingTypeItem.getValue().getHearingType(),
                        reportSummary2.getFields());
            }

        }
    }

    private void setLocalReportSummaryDetail(
            List<HearingsByHearingTypeSubmitEvent> submitEvents,
            HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportDetail> reportSummaryDetailList = new ArrayList<>();
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    setDetailFields(hearingTypeItem, caseData, reportSummaryDetailList);
                }
            }
        }
        reportData.addReportDetail(reportSummaryDetailList);
    }

    private void setDetailFields(
            HearingTypeItem hearingTypeItem,
            HearingsByHearingTypeCaseData caseData,
            List<HearingsByHearingTypeReportDetail> reportSummaryDetailList) {
        if (hearingTypeItem.getValue() != null
                && CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
            for (DateListedTypeItem dateListedTypeItem :
                    hearingTypeItem.getValue().getHearingDateCollection()) {
                if (isValidHearing(dateListedTypeItem.getValue())) {
                    var detail = getDetail(hearingTypeItem, dateListedTypeItem, caseData);
                    reportSummaryDetailList.add(detail);
                }
            }

        }
    }

    private HearingsByHearingTypeReportDetail getDetail(
            HearingTypeItem hearingTypeItem,
            DateListedTypeItem dateListedTypeItem,
            HearingsByHearingTypeCaseData caseData) {
        var detail = new HearingsByHearingTypeReportDetail();
        detail.setDetailDate(LocalDateTime.parse(
                dateListedTypeItem.getValue().getListedDate(), OLD_DATE_TIME_PATTERN).toLocalDate().toString());
        detail.setHearingType(hearingTypeItem.getValue().getHearingType());
        detail.setHearingNo(hearingTypeItem.getValue().getHearingNumber());
        var mulRef = StringUtils.defaultString(caseData.getMultipleReference(), "0 -  Not Allocated");
        var subMul = StringUtils.defaultString(caseData.getSubMultipleName(), "0 -  Not Allocated");
        detail.setMultiSub(mulRef + ", " + subMul);
        detail.setCaseReference(caseData.getEthosCaseReference());
        detail.setLead(YES.equals(caseData.getLeadClaimant()) ? "Y" : "N");
        detail.setTel(CollectionUtils.isNotEmpty(
                hearingTypeItem.getValue().getHearingFormat())
                && hearingTypeItem.getValue().getHearingFormat()
                .contains("Telephone") ? "Y" : "");
        detail.setJm(YES.equals(
                hearingTypeItem.getValue().getJudicialMediation()) ? "Y" : "");
        detail.setHearingClerk(Strings.isNullOrEmpty(
                dateListedTypeItem.getValue().getHearingClerk()) ? ""
                : dateListedTypeItem.getValue().getHearingClerk());
        detail.setDuration(getHearingDurationInMinutes(dateListedTypeItem));
        return detail;
    }

    private void setLocalReportSummaryHdr2(
            List<HearingsByHearingTypeSubmitEvent> submitEvents,
            HearingsByHearingTypeReportData reportData) {
        List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList = new ArrayList<>();
        initReportSummary2HdrList(reportSummary2HdrList);
        for (HearingsByHearingTypeSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
                for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                    if (hearingTypeItem.getValue() != null
                            && CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        setHdr2Fields(hearingTypeItem, reportSummary2HdrList);
                    }
                }
            }
        }
        removeRowsWithNoData(reportSummary2HdrList);
        reportData.addReportSummary2HdrList(reportSummary2HdrList);
    }

    private void removeRowsWithNoData(List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {
        reportSummary2HdrList.removeIf(row -> row.getFields().getTotal().equals("0"));
    }

    private void setHdr2Fields(
            HearingTypeItem hearingTypeItem,
            List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {
        for (DateListedTypeItem dateListedTypeItem :
                hearingTypeItem.getValue().getHearingDateCollection()) {
            if (isValidHearing(dateListedTypeItem.getValue())) {
                if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat())) {
                    for (String format : hearingTypeItem.getValue().getHearingFormat()) {
                        String subSplit = getSubSplitHearingFormat(format);
                        setReportSummary2HdrFields(subSplit, hearingTypeItem, reportSummary2HdrList);
                    }
                }
                setSubSplit2Hdr(hearingTypeItem, reportSummary2HdrList);
            }
        }
    }

    private void setSubSplit2Hdr(HearingTypeItem hearingTypeItem,
                                 List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {

        if (isSubSplitJM(hearingTypeItem)) {
            setReportSummary2HdrFields("JM", hearingTypeItem, reportSummary2HdrList);
        }

        String subSplitSitAlone = getSubSplitSitAlone(hearingTypeItem);
        if (!Strings.isNullOrEmpty(subSplitSitAlone)) {
            setReportSummary2HdrFields(subSplitSitAlone, hearingTypeItem, reportSummary2HdrList);
        }

        String subSplitStages = getSubSplitStages(hearingTypeItem);
        if (!Strings.isNullOrEmpty(subSplitStages)) {
            setReportSummary2HdrFields(subSplitStages, hearingTypeItem, reportSummary2HdrList);
        }
    }

    private void setReportSummary2HdrFields(
            String subSplit, HearingTypeItem hearingTypeItem,
            List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {
        var reportSummary2Hdr = getSummaryHdr2Row(
                subSplit, reportSummary2HdrList);
        if (reportSummary2Hdr != null) {
            setReportFields(hearingTypeItem.getValue().getHearingType(), reportSummary2Hdr.getFields());
        }
    }
}

