package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_NO_CONCILIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_OPEN_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_STANDARD_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



@Slf4j
public class TimeToFirstHearingReport {

    static final String ZERO = "0";
    static final String ZERO_DECIMAL = "0.00";

    public ListingData generateReportData(ListingDetails listingDetails, List<SubmitEvent> submitEvents) throws IOException {

        initReport(listingDetails);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
           executeReport(listingDetails, submitEvents);
        }

       // listingDetails.getCaseData().clearReportFields();
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
        populateLocalReportSummaryHdr(listingDetails.getCaseData(), submitEvents);
       // populateLocalReportSummaryDetail(listingDetails, submitEvents);

    }
//
//    private void populateLocalReportSummaryDetail(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
//        var localReportsDetailList = listingDetails.getCaseData().getLocalReportsDetail();
//
//        for (var submitEvent : submitEvents) {
//            if (isValidCaseForCasesCompletedReport(submitEvent)) {
//                var localReportsDetailItem =
//                        getLocalReportsDetail(listingDetails, submitEvent.getCaseData());
//                if (localReportsDetailItem != null) {
//                    localReportsDetailList.add(localReportsDetailItem);
//                }
//            }
//        }
//    }
//
//
//    private AdhocReportTypeItem getLocalReportsDetail(ListingDetails listingDetails, CaseData caseData) {
//        AdhocReportType adhocReportType = new AdhocReportType();
//        adhocReportType.setReportOffice(listingDetails.getCaseTypeId());
//        adhocReportType.setCaseReference(caseData.getEthosCaseReference());
//        adhocReportType.setConciliationTrack(getConciliationTrack(caseData));
//        adhocReportType.setReceiptDate(caseData.getReceiptDate());
//
//        adhocReportType.setHearingDate(latestSession.getListedDate());
//        adhocReportType.setDays
//
//                office, caseno, track, receiptDate, HearingDate, Days
//    }
    private void populateLocalReportSummaryHdr(ListingData listingData, List<SubmitEvent> submitEvents) {
       var adhocReportType = listingData.getLocalReportsSummary().get(0).getValue();
       int totalCases = Integer.valueOf(adhocReportType.getConOpenTotal())
               + Integer.valueOf(adhocReportType.getConStdTotal())
               + Integer.valueOf(adhocReportType.getConFastTotal())
               + Integer.valueOf(adhocReportType.getConNoneTotal());

        int totalCasesWithin26Weeks = Integer.valueOf(adhocReportType.getConOpen26wkTotal())
                + Integer.valueOf(adhocReportType.getConStd26wkTotal())
                + Integer.valueOf(adhocReportType.getConFast26wkTotal())
                + Integer.valueOf(adhocReportType.getConNone26wkTotal());

        int totalCasesNotWithin26Weeks = Integer.valueOf(adhocReportType.getXConOpen26wkTotal())
                + Integer.valueOf(adhocReportType.getXConStd26wkTotal())
                + Integer.valueOf(adhocReportType.getXConFast26wkTotal())
                + Integer.valueOf(adhocReportType.getXConNone26wkTotal());

        float totalCasesWithin26WeeksPercent = (totalCasesWithin26Weeks * totalCases) / 100;
        float totalCasesNotWithin26WeeksPercent = (totalCasesNotWithin26Weeks * totalCases) / 100;

        adhocReportType.setTotalCases(String.valueOf(totalCases));
        adhocReportType.setTotal26wk(String.valueOf(totalCasesWithin26Weeks));
        adhocReportType.setTotalx26wk(String.valueOf(totalCasesNotWithin26Weeks));
        adhocReportType.setTotal26wkPerCent(String.valueOf(totalCasesWithin26WeeksPercent));
        adhocReportType.setTotalx26wkPerCent(String.valueOf(totalCasesNotWithin26WeeksPercent));
        listingData.setLocalReportsDetailHdr(adhocReportType);
    }


        private void populateLocalReportSummary(ListingData listingData, List<SubmitEvent> submitEvents) {

        var conNoneTotal = 0;
        var conStdTotal = 0;
        var conFastTotal = 0;
        var conOpenTotal = 0;
        var conNone26wkTotal = 0;
        var conStd26wkTotal = 0;
        var conFast26wkTotal = 0;
        var conOpen26wkTotal = 0;
        var conNone26wkTotalPerCent = 0.00;
        var conStd26wkTotalPerCent = 0.00;
        var conFast26wkTotalPerCent = 0.00;
        var conOpen26wkTotalPerCent = 0.00;
        var xConNone26wkTotal = 0;
        var xConStd26wkTotal = 0;
        var xConFast26wkTotal = 0;
        var xConOpen26wkTotal = 0;
        var xConNone26wkTotalPerCent = 0.00;
        var xConStd26wkTotalPerCent = 0.00;
        var xConFast26wkTotalPerCent = 0.00;
        var xConOpen26wkTotalPerCent = 0.00;
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

            if (getConciliationTrack(submitEvent.getCaseData()).equals(CONCILIATION_TRACK_NO_CONCILIATION)) {
                conNoneTotal = conNoneTotal + 1;
                if (isFirstHearingWithin26Weeks) {
                    conNone26wkTotal = conNone26wkTotal + 1;
                } else {
                    xConNone26wkTotal = xConNone26wkTotal + 1;
                }

            }
            if (getConciliationTrack(submitEvent.getCaseData()).equals(CONCILIATION_TRACK_STANDARD_TRACK)) {
                conStdTotal = conStdTotal + 1;
                if (isFirstHearingWithin26Weeks) {
                    conStd26wkTotal = conStd26wkTotal + 1;
                } else {
                       xConStd26wkTotal = xConStd26wkTotal + 1;
                }

            }
            if (getConciliationTrack(submitEvent.getCaseData()).equals(CONCILIATION_TRACK_FAST_TRACK)) {
                conFastTotal = conFastTotal + 1;
                if (isFirstHearingWithin26Weeks) {
                    conFast26wkTotal = conFast26wkTotal + 1;
                } else {
                       xConFast26wkTotal = xConFast26wkTotal + 1;
                }

            }
            if (getConciliationTrack(submitEvent.getCaseData()).equals(CONCILIATION_TRACK_OPEN_TRACK)) {
               conOpenTotal = conOpenTotal + 1;
               if (isFirstHearingWithin26Weeks) {
                   conOpen26wkTotal = conOpen26wkTotal + 1;
               } else {
                   xConOpen26wkTotal = xConOpen26wkTotal + 1;
               }
            }

        }

        conNone26wkTotalPerCent = (conNone26wkTotal / conNoneTotal) * 100;
        conStd26wkTotalPerCent = (conStd26wkTotal / conStdTotal) * 100;
        conFast26wkTotalPerCent = (conFast26wkTotal / conFastTotal) * 100;
        conOpen26wkTotalPerCent = (conOpen26wkTotal / conOpenTotal) * 100;

        xConNone26wkTotalPerCent = (xConNone26wkTotal / conNoneTotal) * 100;
        xConStd26wkTotalPerCent = (xConStd26wkTotal / conStdTotal) * 100;
        xConFast26wkTotalPerCent = (xConFast26wkTotal / conFastTotal) * 100;
        xConOpen26wkTotalPerCent = (xConOpen26wkTotal / conOpenTotal) * 100;

        adhocReportType.setConNoneTotal(String.valueOf(conNoneTotal));
        adhocReportType.setConStdTotal(String.valueOf(conStdTotal));
        adhocReportType.setConFastTotal(String.valueOf(conFastTotal));
        adhocReportType.setConOpenTotal(String.valueOf(conOpenTotal));
        adhocReportType.setConNone26wkTotal(String.valueOf(conNone26wkTotal));
        adhocReportType.setConStd26wkTotal(String.valueOf(conStd26wkTotal));
        adhocReportType.setConFast26wkTotal(String.valueOf(conFast26wkTotal));
        adhocReportType.setConOpen26wkTotal(String.valueOf(conOpen26wkTotal));
        adhocReportType.setXConNone26wkTotal(String.valueOf(xConNone26wkTotal));
        adhocReportType.setXConStd26wkTotal(String.valueOf(xConStd26wkTotal));
        adhocReportType.setXConFast26wkTotal(String.valueOf(xConFast26wkTotal));
        adhocReportType.setXConOpen26wkTotal(String.valueOf(xConOpen26wkTotal));
        adhocReportType.setConNone26wkTotalPerCent(String.valueOf(conNone26wkTotalPerCent));
        adhocReportType.setConStd26wkTotalPerCent(String.valueOf(conStd26wkTotalPerCent));
        adhocReportType.setConFast26wkTotalPerCent(String.valueOf(conFast26wkTotalPerCent));
        adhocReportType.setConOpen26wkTotalPerCent(String.valueOf(conOpen26wkTotalPerCent));
        adhocReportType.setXConNone26wkTotalPerCent(String.valueOf(xConNone26wkTotalPerCent));
        adhocReportType.setXConStd26wkTotalPerCent(String.valueOf(xConStd26wkTotalPerCent));
        adhocReportType.setXConFast26wkTotalPerCent(String.valueOf(xConFast26wkTotalPerCent));
        adhocReportType.setXConOpen26wkTotalPerCent(String.valueOf(xConOpen26wkTotalPerCent));

        var adhocReportTypeItem = new AdhocReportTypeItem();
        adhocReportTypeItem.setId(UUID.randomUUID().toString());
        adhocReportTypeItem.setValue(adhocReportType);
        listingData.setLocalReportsSummary(Collections.singletonList(adhocReportTypeItem));

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
        List<LocalDate> datesList = new ArrayList<>();
        for (var hearingTypeItem : caseData.getHearingCollection()) {
            datesList = getHearingDateList(hearingTypeItem);
            if (CollectionUtils.isNotEmpty(datesList)) {
                mainDatesList.addAll(datesList);
            }
        }
        if (mainDatesList.size() > 0) {
            Collections.sort(mainDatesList);
            return mainDatesList.get(0);
        }
        return null;
    }

    private List<LocalDate> getHearingDateList(HearingTypeItem hearingTypeItem) {
        var hearingType = hearingTypeItem.getValue();
        List<LocalDate> datesList = new ArrayList<>();
        if (hearingType == null || CollectionUtils.isEmpty(hearingType.getHearingDateCollection())) {
            return null;
        }
        if (Constants.HEARING_TYPE_JUDICIAL_HEARING.equals(hearingType.getHearingType())
                || HEARING_TYPE_PERLIMINARY_HEARING.equals(hearingType.getHearingType())) {
            for (var dateListedItemType : hearingType.getHearingDateCollection()) {
                if (Constants.HEARING_STATUS_HEARD.equals(dateListedItemType.getValue().getHearingStatus())) {
                    LocalDate a = LocalDate.parse(dateListedItemType.getValue().getListedDate());
                    datesList.add(a);
                }
            }
        }
        return datesList;
    }

    private boolean isFirstHearingWithin26Weeks(CaseData caseData, LocalDate firstHearingDate) {
        var receiptDate = LocalDate.parse(caseData.getReceiptDate());
        if (receiptDate.plusWeeks(26).equals(firstHearingDate) || receiptDate.plusWeeks(26).isAfter(firstHearingDate)) {
            return true;
        } else {
            return false;
        }
    }
}
