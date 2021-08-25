package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_NO_CONCILIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_OPEN_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_STANDARD_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;

@Service
@Slf4j
public class TimeToFirstHearingReport {

    static final String ZERO = "0";
    static final String ZERO_DECIMAL = "0.00";

    public ListingData generateReportData(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        initReport(listingDetails);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(listingDetails, submitEvents);
        }

        listingDetails.getCaseData().clearReportFields();
        return listingDetails.getCaseData();
    }

    private void initReport(ListingDetails listingDetails) {

        var adhocReportType = new AdhocReportType();

        //LocalReportsSummary fields
        adhocReportType.setConNoneTotal(ZERO);
        adhocReportType.setConStdTotal(ZERO);
        adhocReportType.setConFastTotal(ZERO);
        adhocReportType.setConOpenTotal(ZERO);
        adhocReportType.setConNone26wkTotal(ZERO);
        adhocReportType.setConStd26wkTotal(ZERO);
        adhocReportType.setConFast26wkTotal(ZERO);
        adhocReportType.setConOpen26wkTotal(ZERO);
        adhocReportType.setConNone26wkTotalPerCent(ZERO_DECIMAL);
        adhocReportType.setConStd26wkTotalPerCent(ZERO_DECIMAL);
        adhocReportType.setConFast26wkTotalPerCent(ZERO_DECIMAL);
        adhocReportType.setConOpen26wkTotalPerCent(ZERO_DECIMAL);
        adhocReportType.setXConNone26wkTotal(ZERO);
        adhocReportType.setXConStd26wkTotal(ZERO);
        adhocReportType.setXConFast26wkTotal(ZERO);
        adhocReportType.setXConOpen26wkTotal(ZERO);
        adhocReportType.setXConNone26wkTotalPerCent(ZERO_DECIMAL);
        adhocReportType.setXConStd26wkTotalPerCent(ZERO_DECIMAL);
        adhocReportType.setXConFast26wkTotalPerCent(ZERO_DECIMAL);
        adhocReportType.setXConOpen26wkTotalPerCent(ZERO_DECIMAL);

        //localReportsSummaryHdr fields
        adhocReportType.setTotalCases(ZERO);
        adhocReportType.setTotal26wk(ZERO);
        adhocReportType.setTotal26wkPerCent(ZERO_DECIMAL);
        adhocReportType.setTotalx26wk(ZERO);
        adhocReportType.setTotalx26wkPerCent(ZERO_DECIMAL);

        //localReportsDetail fields
        adhocReportType.setReportOffice("");
        adhocReportType.setCaseReference("");
        adhocReportType.setConciliationTrack("");
        adhocReportType.setReceiptDate("");
        adhocReportType.setHearingDate("");
        adhocReportType.setTotal("");

        var listingData = listingDetails.getCaseData();
        listingData.setLocalReportsDetailHdr(adhocReportType);
        listingData.setLocalReportsSummary(new ArrayList<>());
        listingData.setLocalReportsDetail(new ArrayList<>());
    }

    private void executeReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        log.info(String.format("Time to first hearing report case type id %s search results: %d",
                listingDetails.getCaseTypeId(), submitEvents.size()));
        populateLocalReportSummary(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummaryHdr(listingDetails.getCaseData());
        populateLocalReportSummaryDetail(listingDetails, submitEvents);

    }

    private void populateLocalReportSummaryDetail(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        var localReportsDetailList = listingDetails.getCaseData().getLocalReportsDetail();
        for (var submitEvent : submitEvents) {
            var localReportsDetailItem =
                    getLocalReportsDetail(listingDetails, submitEvent.getCaseData());
            if (localReportsDetailItem != null) {
                localReportsDetailList.add(localReportsDetailItem);
            }
        }
        listingDetails.getCaseData().setLocalReportsDetail(localReportsDetailList);
    }

    private AdhocReportTypeItem getLocalReportsDetail(ListingDetails listingDetails, CaseData caseData) {

        var firstHearingDate = getFirstHearingDate(caseData);
        if (firstHearingDate == null || isFirstHearingWithin26Weeks(caseData, firstHearingDate)) {
            return null;
        }
        var adhocReportType = new AdhocReportType();
        adhocReportType.setHearingDate(firstHearingDate.toString());
        adhocReportType.setReportOffice(listingDetails.getCaseTypeId());
        adhocReportType.setCaseReference(caseData.getEthosCaseReference());
        adhocReportType.setConciliationTrack(getConciliationTrack(caseData));
        if (!Strings.isNullOrEmpty(caseData.getReceiptDate())) {
            var duration = Duration.between(firstHearingDate.atStartOfDay(),
                    LocalDate.parse(caseData.getReceiptDate()).atStartOfDay());
            adhocReportType.setDelayedDaysForFirstHearing(String.valueOf(duration.toDays()));
            adhocReportType.setReceiptDate(caseData.getReceiptDate());
        }
        var adhocReportTypeItem = new AdhocReportTypeItem();
        adhocReportTypeItem.setId(UUID.randomUUID().toString());
        adhocReportTypeItem.setValue(adhocReportType);
        return adhocReportTypeItem;

    }

    private void populateLocalReportSummaryHdr(ListingData listingData) {
        var adhocReportType = listingData.getLocalReportsSummary().get(0).getValue();
        int totalCases = Integer.parseInt(adhocReportType.getConOpenTotal())
                + Integer.parseInt(adhocReportType.getConStdTotal())
                + Integer.parseInt(adhocReportType.getConFastTotal())
                + Integer.parseInt(adhocReportType.getConNoneTotal());

        int totalCasesWithin26Weeks = Integer.parseInt(adhocReportType.getConOpen26wkTotal())
                + Integer.parseInt(adhocReportType.getConStd26wkTotal())
                + Integer.parseInt(adhocReportType.getConFast26wkTotal())
                + Integer.parseInt(adhocReportType.getConNone26wkTotal());

        int totalCasesNotWithin26Weeks = Integer.parseInt(adhocReportType.getXConOpen26wkTotal())
                + Integer.parseInt(adhocReportType.getXConStd26wkTotal())
                + Integer.parseInt(adhocReportType.getXConFast26wkTotal())
                + Integer.parseInt(adhocReportType.getXConNone26wkTotal());

        float totalCasesWithin26WeeksPercent = (totalCases != 0) ?
                ((float)totalCasesWithin26Weeks / totalCases) * 100 : 0;
        float totalCasesNotWithin26WeeksPercent = (totalCases != 0) ?
                ((float)totalCasesNotWithin26Weeks / totalCases) * 100 : 0;

        adhocReportType.setTotalCases(String.valueOf(totalCases));
        adhocReportType.setTotal26wk(String.valueOf(totalCasesWithin26Weeks));
        adhocReportType.setTotalx26wk(String.valueOf(totalCasesNotWithin26Weeks));
        adhocReportType.setTotal26wkPerCent(String.valueOf(totalCasesWithin26WeeksPercent));
        adhocReportType.setTotalx26wkPerCent(String.valueOf(totalCasesNotWithin26WeeksPercent));
        listingData.setLocalReportsDetailHdr(adhocReportType);
    }

    static class ReportSummary {
        int conNoneTotal;
        int conStdTotal;
        int conFastTotal;
        int conOpenTotal;
        int conNone26WkTotal;
        int conStd26WkTotal;
        int conFast26WkTotal;
        int conOpen26WkTotal;
        int xConNone26WkTotal;
        int xConStd26WkTotal;
        int xConFast26WkTotal;
        int xConOpen26WkTotal;
    }
    private void populateLocalReportSummary(ListingData listingData, List<SubmitEvent> submitEvents) {

        var reportSummary = new ReportSummary();
        var adhocReportType = listingData.getLocalReportsDetailHdr();
        LocalDate firstHearingDate;
        for (var submitEvent : submitEvents) {
            firstHearingDate = getFirstHearingDate(submitEvent.getCaseData());
            if (firstHearingDate == null) {
                continue;
            }
            boolean isFirstHearingWithin26Weeks = isFirstHearingWithin26Weeks(
                    submitEvent.getCaseData(),
                    firstHearingDate);

            switch(getConciliationTrack(submitEvent.getCaseData())) {
                case CONCILIATION_TRACK_NO_CONCILIATION:
                    reportSummary = updateNoTrack(reportSummary, isFirstHearingWithin26Weeks);
                    break;
                case CONCILIATION_TRACK_STANDARD_TRACK:
                    reportSummary = updateStandardTrack(reportSummary, isFirstHearingWithin26Weeks);
                    break;
                case CONCILIATION_TRACK_FAST_TRACK:
                    reportSummary =  updateFastTrack(reportSummary, isFirstHearingWithin26Weeks);
                    break;
                case CONCILIATION_TRACK_OPEN_TRACK:
                    reportSummary = updateOpenTrack(reportSummary, isFirstHearingWithin26Weeks);
                    break;
                default:
                    break;
            }
        }

        adhocReportType.setConNoneTotal(String.valueOf(reportSummary.conNoneTotal));
        adhocReportType.setConStdTotal(String.valueOf(reportSummary.conStdTotal));
        adhocReportType.setConFastTotal(String.valueOf(reportSummary.conFastTotal));
        adhocReportType.setConOpenTotal(String.valueOf(reportSummary.conOpenTotal));
        adhocReportType.setConNone26wkTotal(String.valueOf(reportSummary.conNone26WkTotal));
        adhocReportType.setConStd26wkTotal(String.valueOf(reportSummary.conStd26WkTotal));
        adhocReportType.setConFast26wkTotal(String.valueOf(reportSummary.conFast26WkTotal));
        adhocReportType.setConOpen26wkTotal(String.valueOf(reportSummary.conOpen26WkTotal));
        adhocReportType.setXConNone26wkTotal(String.valueOf(reportSummary.xConNone26WkTotal));
        adhocReportType.setXConStd26wkTotal(String.valueOf(reportSummary.xConStd26WkTotal));
        adhocReportType.setXConFast26wkTotal(String.valueOf(reportSummary.xConFast26WkTotal));
        adhocReportType.setXConOpen26wkTotal(String.valueOf(reportSummary.xConOpen26WkTotal));
        setPercent(adhocReportType);

        var adhocReportTypeItem = new AdhocReportTypeItem();
        adhocReportTypeItem.setId(UUID.randomUUID().toString());
        adhocReportTypeItem.setValue(adhocReportType);
        listingData.setLocalReportsSummary(Collections.singletonList(adhocReportTypeItem));
    }

    private ReportSummary updateNoTrack(ReportSummary reportSummary, boolean isFirstHearingWithin26Weeks) {
        reportSummary.conNoneTotal = reportSummary.conNoneTotal + 1;
        if (isFirstHearingWithin26Weeks) {
            reportSummary.conNone26WkTotal = reportSummary.conNone26WkTotal + 1;
        } else {
            reportSummary.xConNone26WkTotal = reportSummary.xConNone26WkTotal + 1;
        }
        return reportSummary;
    }

    private ReportSummary updateStandardTrack(ReportSummary reportSummary, boolean isFirstHearingWithin26Weeks) {
        reportSummary.conStdTotal = reportSummary.conStdTotal + 1;
        if (isFirstHearingWithin26Weeks) {
            reportSummary.conStd26WkTotal = reportSummary.conStd26WkTotal + 1;
        } else {
            reportSummary.xConStd26WkTotal = reportSummary.xConStd26WkTotal + 1;
        }
        return reportSummary;
    }

    private ReportSummary updateFastTrack(ReportSummary reportSummary, boolean isFirstHearingWithin26Weeks) {
        reportSummary.conFastTotal = reportSummary.conFastTotal + 1;
        if (isFirstHearingWithin26Weeks) {
            reportSummary.conFast26WkTotal = reportSummary.conFast26WkTotal + 1;
        } else {
            reportSummary.xConFast26WkTotal = reportSummary.xConFast26WkTotal + 1;
        }
        return reportSummary;
    }

    private ReportSummary updateOpenTrack(ReportSummary reportSummary, boolean isFirstHearingWithin26Weeks) {
        reportSummary.conOpenTotal = reportSummary.conOpenTotal + 1;
        if (isFirstHearingWithin26Weeks) {
            reportSummary.conOpen26WkTotal = reportSummary.conOpen26WkTotal + 1;
        } else {
            reportSummary.xConOpen26WkTotal = reportSummary.xConOpen26WkTotal + 1;
        }
        return reportSummary;
    }

    private void setPercent(AdhocReportType adhocReportType) {
        var conNone26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConNoneTotal()) != 0)
                ? (Double.parseDouble(adhocReportType.getConNone26wkTotal())
                / Integer.parseInt(adhocReportType.getConNoneTotal())) * 100 : 0;
        var conStd26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConStdTotal()) != 0)
                ? (Double.parseDouble(adhocReportType.getConStd26wkTotal())
                / Integer.parseInt(adhocReportType.getConStdTotal())) * 100 : 0;
        var conFast26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConFastTotal()) != 0)
                ? (Double.parseDouble(adhocReportType.getConFast26wkTotal())
                / Integer.parseInt(adhocReportType.getConFastTotal())) * 100 : 0;
        var conOpen26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConOpenTotal()) != 0)
                ? (Double.parseDouble(adhocReportType.getConOpen26wkTotal())
                / Integer.parseInt(adhocReportType.getConOpenTotal())) * 100 : 0;

        var xConNone26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConNoneTotal()) != 0)
                ? (Double.parseDouble(adhocReportType.getXConNone26wkTotal())
                / Integer.parseInt(adhocReportType.getConNoneTotal())) * 100 : 0;
        var xConStd26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConStdTotal()) != 0)
                ? (Double.parseDouble(adhocReportType.getXConStd26wkTotal())
                / Integer.parseInt(adhocReportType.getConStdTotal())) * 100 : 0;
        var  xConFast26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConFastTotal()) != 0)
                ? (Double.parseDouble(adhocReportType.getXConFast26wkTotal())
                / Integer.parseInt(adhocReportType.getConFastTotal())) * 100 : 0;
        var  xConOpen26wkTotalPerCent = (Integer.parseInt(adhocReportType.getConOpenTotal()) != 0)
                ? ((float)Double.parseDouble(adhocReportType.getXConOpen26wkTotal())
                / Integer.parseInt(adhocReportType.getConOpenTotal())) * 100 : 0;

        adhocReportType.setConNone26wkTotalPerCent(String.valueOf(conNone26wkTotalPerCent));
        adhocReportType.setConStd26wkTotalPerCent(String.valueOf(conStd26wkTotalPerCent));
        adhocReportType.setConFast26wkTotalPerCent(String.valueOf(conFast26wkTotalPerCent));
        adhocReportType.setConOpen26wkTotalPerCent(String.valueOf(conOpen26wkTotalPerCent));
        adhocReportType.setXConNone26wkTotalPerCent(String.valueOf(xConNone26wkTotalPerCent));
        adhocReportType.setXConStd26wkTotalPerCent(String.valueOf(xConStd26wkTotalPerCent));
        adhocReportType.setXConFast26wkTotalPerCent(String.valueOf(xConFast26wkTotalPerCent));
        adhocReportType.setXConOpen26wkTotalPerCent(String.valueOf(xConOpen26wkTotalPerCent));
    }
    private String getConciliationTrack(CaseData caseData) {
        return StringUtils.isNotBlank(caseData.getConciliationTrack())
                ? caseData.getConciliationTrack() : CONCILIATION_TRACK_NO_CONCILIATION;
    }

    private LocalDate getFirstHearingDate(CaseData caseData) {
        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            return null;
        }
        List<LocalDate> mainDatesList = new ArrayList<>();
        List<LocalDate> datesList;
        for (var hearingTypeItem : caseData.getHearingCollection()) {
            datesList = getHearingDateList(hearingTypeItem);
            if (CollectionUtils.isNotEmpty(datesList)) {
                mainDatesList.addAll(datesList);
            }
        }
        if (CollectionUtils.isNotEmpty(mainDatesList)) {
            Collections.sort(mainDatesList);
            return mainDatesList.get(0);
        }
        return null;
    }

    private List<LocalDate> getHearingDateList(HearingTypeItem hearingTypeItem) {
        var hearingType = hearingTypeItem.getValue();
        List<LocalDate> datesList = new ArrayList<>();
        if (hearingType == null || CollectionUtils.isEmpty(hearingType.getHearingDateCollection())) {
            return datesList;
        }
        if (Constants.HEARING_TYPE_JUDICIAL_HEARING.equals(hearingType.getHearingType())
                || HEARING_TYPE_PERLIMINARY_HEARING.equals(hearingType.getHearingType())) {
            for (var dateListedItemType : hearingType.getHearingDateCollection()) {
                if (Constants.HEARING_STATUS_HEARD.equals(dateListedItemType.getValue().getHearingStatus())) {
                    var date = LocalDate.parse(dateListedItemType.getValue().getListedDate());
                    datesList.add(date);
                }
            }
        }
        return datesList;
    }

    private boolean isFirstHearingWithin26Weeks(CaseData caseData, LocalDate firstHearingDate) {
        var receiptDate = LocalDate.parse(caseData.getReceiptDate());
        return receiptDate.plusWeeks(26).equals(firstHearingDate) || receiptDate.plusWeeks(26).isAfter(firstHearingDate);
    }
}
