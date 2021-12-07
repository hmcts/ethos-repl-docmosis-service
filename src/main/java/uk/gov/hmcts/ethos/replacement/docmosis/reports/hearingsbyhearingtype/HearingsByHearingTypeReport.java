package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.ReportListingsTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ecm.common.model.listing.types.ReportListingsType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static java.lang.Math.abs;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_RECONSIDERATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_REMEDY;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Service
@Slf4j
public class HearingsByHearingTypeReport {

    private static final List<String> subSplitList = List.of("EJ Sit Alone", "Full Panel", "JM",
            "Tel Con", "Video", "Hybrid", "In Person", "Stage 1", "Stage 2", "Stage 3");
    private String costsHearingType = "Costs Hearing";
    private boolean casesExistWithHearingStatusHeard;
    private static final String ZERO = "0";
    private static final String hearingNumber = ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO;

    public ListingData processHearingsByHearingTypeRequest(ListingDetails listingDetails,
                                                           List<SubmitEvent> submitEvents) {

        initReport(listingDetails);
        casesExistWithHearingStatusHeard = CollectionUtils.isNotEmpty(getDatesList(submitEvents));
        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(listingDetails, submitEvents);
        }

        listingDetails.getCaseData().clearReportFields();
        return listingDetails.getCaseData();
    }

    private void executeReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        log.info(String.format("Hearings by hearing type report case type id %s search results: %d",
                listingDetails.getCaseTypeId(), submitEvents.size()));
        if (UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()).toLowerCase().contains("scotland")) {
            costsHearingType = "Expenses/Wasted Costs Hearing";
        }
        populateLocalReportSummaryHdr(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummary(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummaryHdr2(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummary2(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummaryDetail(listingDetails.getCaseData(), submitEvents);

        if (CollectionUtils.isNotEmpty(listingDetails.getCaseData().getLocalReportsSummary())) {
            listingDetails.getCaseData().getLocalReportsSummary().get(0).getValue().setReportOffice(
                    UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()));
        }

    }

    private void initReport(ListingDetails listingDetails) {

        var reportListingsType = new ReportListingsType();
        reportListingsType.setHearingNumber(hearingNumber);
        var item = new ReportListingsTypeItem();
        item.setId(UUID.randomUUID().toString());
        item.setValue(reportListingsType);
        List<ReportListingsTypeItem> listingHistory = new ArrayList<>();
        listingHistory.add(item);
        var adhocReportType = new AdhocReportType();
        initSummaryFields(adhocReportType);
        adhocReportType.setListingHistory(listingHistory);
        adhocReportType.setReportOffice(
                UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()));

        var listingData = listingDetails.getCaseData();
        listingData.setLocalReportsSummaryHdr(adhocReportType);
        listingData.setLocalReportsSummaryHdr2(adhocReportType);

        var adhocReportTypeItem = new AdhocReportTypeItem();
        adhocReportTypeItem.setId(UUID.randomUUID().toString());
        adhocReportTypeItem.setValue(adhocReportType);
        listingData.setLocalReportsSummary(List.of(adhocReportTypeItem));
        listingData.setLocalReportsSummary2(List.of(adhocReportTypeItem));
        listingData.setLocalReportsDetail(List.of(adhocReportTypeItem));
    }

    private void initSummaryFields(AdhocReportType adhocReportType) {
        adhocReportType.setCosts(ZERO);
        adhocReportType.setRemedy(ZERO);
        adhocReportType.setHearingCM(ZERO);
        adhocReportType.setHearingPrelim(ZERO);
        adhocReportType.setReconsider(ZERO);
        adhocReportType.setHearing(ZERO);
        adhocReportType.setTotal(ZERO);
        adhocReportType.setDate(ZERO);
        adhocReportType.setSubSplit(ZERO);
        adhocReportType.setCaseReference(ZERO);
        adhocReportType.setLeadCase(ZERO);
        adhocReportType.setHearingType(ZERO);
        adhocReportType.setJudicialMediation(ZERO);
        adhocReportType.setHearingTelConf(ZERO);
        adhocReportType.setHearingDuration(ZERO);
        adhocReportType.setHearingClerk(ZERO);
    }

    private static class Fields {
        int hearing;
        int remedy;
        int reconsider;
        int costs;
        int hearingPrelim;
        int hearingPrelimCM;
        int total;
        String date;

        public void calculateTotal() {
            total = hearingPrelimCM + hearingPrelim + hearing + remedy + reconsider + costs;
        }
    }

    private List<String> getDatesList(List<SubmitEvent> submitEventList) {
        List<String> datesList = new ArrayList<>();
        for (var submitEvent : submitEventList) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
                continue;
            }
            for (var hearingTypeItem : caseData.getHearingCollection()) {
                if (CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                    continue;
                }
                for (var dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                    if (HEARING_STATUS_HEARD.equals(dateListedTypeItem.getValue().getHearingStatus())) {
                        datesList.add(dateListedTypeItem.getValue().getListedDate());
                    }
                }
            }
        }
        return datesList;
    }

    private void populateLocalReportSummaryHdr(ListingData listingData, List<SubmitEvent> submitEventList) {
        var fields = new Fields();
        if (casesExistWithHearingStatusHeard) {
            fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType()))).count();
            fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.costs = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> costsHearingType.equals(b.getValue().getHearingType()))).count();
            fields.reconsider = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType()))).count();
            fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType()))).count();
            fields.calculateTotal();
        }
        var adhocReportType = new AdhocReportType();
        setAdhocReport(adhocReportType, fields, null, true);
        listingData.setLocalReportsSummaryHdr(adhocReportType);
    }

    private void populateLocalReportSummary(ListingData listingData, List<SubmitEvent> submitEventList) {
        List<String> datesList = getDatesList(submitEventList);
        List<AdhocReportTypeItem> adhocReportTypeItemList = new ArrayList<>();
        for (var date : datesList) {
            var fields = new Fields();

            fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                            && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                            c.getValue().getListedDate()))
                            && HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType()))).count();

            fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                            && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                            c.getValue().getListedDate()))
                            && HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType()))).count();
            fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                            && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                            c.getValue().getListedDate()))
                            && HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.costs = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                            && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                            c.getValue().getListedDate()))
                            && costsHearingType.equals(b.getValue().getHearingType()))).count();
            fields.reconsider = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                            && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                            c.getValue().getListedDate()))
                            && HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType()))).count();
            fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                            && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                            c.getValue().getListedDate()))
                            && HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType()))).count();
            fields.date = date;
            fields.calculateTotal();
            var adhocReportType = new AdhocReportType();
            setAdhocReport(adhocReportType, fields, adhocReportTypeItemList, false);
        }

        listingData.setLocalReportsSummary(adhocReportTypeItemList);
    }

    private void populateLocalReportSummaryHdr2(ListingData listingData, List<SubmitEvent> submitEventList) {
        var adhocReportType = new AdhocReportType();
        List<ReportListingsTypeItem> listingHistory = new ArrayList<>();
        if (casesExistWithHearingStatusHeard) {
            for (var subSplitHeader : subSplitList) {
                var fields = new Fields();
                fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.costs =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> costsHearingType.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.reconsider =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.calculateTotal();
                String hearingNumber = fields.hearing + "|"
                        + fields.hearingPrelim + "|"
                        + fields.hearingPrelimCM + "|"
                        + fields.remedy + "|"
                        + fields.reconsider + "|"
                        + fields.costs + "|"
                        + fields.total + "|"
                        + subSplitHeader;

                var reportListingsType = new ReportListingsType();
                reportListingsType.setHearingNumber(hearingNumber);
                var item = new ReportListingsTypeItem();
                item.setId(UUID.randomUUID().toString());
                item.setValue(reportListingsType);
                listingHistory.add(item);
            }
        }

        adhocReportType.setListingHistory(listingHistory);
        listingData.setLocalReportsSummaryHdr2(adhocReportType);
    }

    private void populateLocalReportSummary2(ListingData listingData, List<SubmitEvent> submitEventList) {
        List<String> datesList = getDatesList(submitEventList);
        List<AdhocReportTypeItem> adhocReportTypeItemList = new ArrayList<>();
        for (var date : datesList) {
            for (var subSplitHeader : subSplitList) {
                var fields = new Fields();
                fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                                c.getValue().getListedDate()))
                                && HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                                c.getValue().getListedDate()))
                                && HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                                c.getValue().getListedDate()))
                                && HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.costs =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                                c.getValue().getListedDate()))
                                && costsHearingType.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.reconsider =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                                c.getValue().getListedDate()))
                                && HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(c -> date.equals(
                                c.getValue().getListedDate()))
                                && HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.date = date;
                fields.calculateTotal();
                var adhocReportType = new AdhocReportType();
                adhocReportType.setSubSplit(subSplitHeader);
                setAdhocReport(adhocReportType, fields, adhocReportTypeItemList, false);
            }
        }
        listingData.setLocalReportsSummary2(adhocReportTypeItemList);
    }

    private void populateLocalReportSummaryDetail(ListingData listingData, List<SubmitEvent> submitEventList) {

        List<AdhocReportTypeItem> adhocReportTypeItemList = new ArrayList<>();
        if (casesExistWithHearingStatusHeard) {
            for (var submitEvent : submitEventList) {
                var caseData = submitEvent.getCaseData();
                if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
                    continue;
                }
                for (var hearingTypeItem : caseData.getHearingCollection()) {
                    if (CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                        continue;
                    }
                    for (var dateListedTypedItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                        if (!HEARING_STATUS_HEARD.equals(dateListedTypedItem.getValue().getHearingStatus())) {
                            continue;
                        }
                        var adhocReportType = new AdhocReportType();
                        adhocReportType.setDate(dateListedTypedItem.getValue().getListedDate());
                        adhocReportType.setMultSub(
                                caseData.getMultipleReference() + ", " + caseData.getSubMultipleName());
                        adhocReportType.setCaseReference(caseData.getEthosCaseReference());
                        adhocReportType.setLeadCase(Strings.isNullOrEmpty(caseData.getLeadClaimant()) ? "" : "Y");
                        adhocReportType.setHearingNumber(hearingTypeItem.getValue().getHearingNumber());
                        adhocReportType.setHearingType(hearingTypeItem.getValue().getHearingType());
                        adhocReportType.setHearingTelConf(CollectionUtils.isNotEmpty(
                                hearingTypeItem.getValue().getHearingFormat())
                                && hearingTypeItem.getValue().getHearingFormat()
                                .contains("Telephone") ? "Y" : "");
                        adhocReportType.setJudicialMediation("JM".equals(
                                hearingTypeItem.getValue().getJudicialMediation()) ? "Y" : "");
                        adhocReportType.setHearingClerk(Strings.isNullOrEmpty(
                                dateListedTypedItem.getValue().getHearingClerk()) ? ""
                                : dateListedTypedItem.getValue().getHearingClerk());
                        adhocReportType.setHearingDuration(getHearingDuration(dateListedTypedItem));
                        var adhocReportTypeItem = new AdhocReportTypeItem();
                        adhocReportTypeItem.setId(UUID.randomUUID().toString());
                        adhocReportTypeItem.setValue(adhocReportType);
                        adhocReportTypeItemList.add(adhocReportTypeItem);
                    }
                }
            }
        }

        listingData.setLocalReportsDetail(adhocReportTypeItemList);
    }

    private String getHearingDuration(DateListedTypeItem d) {
        LocalDateTime s;
        LocalDateTime f;
        LocalDateTime r;
        LocalDateTime b;
        if (!Strings.isNullOrEmpty(d.getValue().getHearingTimingStart())) {
            s = LocalDateTime.parse(d.getValue().getHearingTimingStart());
        } else {
            return "0";
        }
        if (!Strings.isNullOrEmpty(d.getValue().getHearingTimingFinish())) {
            f = LocalDateTime.parse(d.getValue().getHearingTimingFinish());
        } else {
            return "0";
        }
        long g = ChronoUnit.MINUTES.between(f, s);
        if (!Strings.isNullOrEmpty(d.getValue().getHearingTimingResume())) {
            r = LocalDateTime.parse(d.getValue().getHearingTimingResume());
        } else {
            return String.valueOf(g);
        }
        if (!Strings.isNullOrEmpty(d.getValue().getHearingTimingBreak())) {
            b = LocalDateTime.parse(d.getValue().getHearingTimingBreak());
        } else {
            return String.valueOf(g);
        }
        long diff = g - (ChronoUnit.MINUTES.between(r, b));
        return String.valueOf(abs(diff));
    }

    private boolean isHearingFormatValid(String subSplitHeader, HearingTypeItem hearingTypeItem) {

        switch (subSplitHeader) {
            case "Full Panel":
                return "Full".equals(hearingTypeItem.getValue().getHearingSitAlone());
            case "EJ Sit Alone":
                return YES.equals(hearingTypeItem.getValue().getHearingSitAlone());
            case "JM":
                return "JM".equals(hearingTypeItem.getValue().getJudicialMediation());
            case "Tel Con":
                return CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat())
                        && hearingTypeItem.getValue().getHearingFormat().contains("Telephone");
            case "Video":
            case "Hybrid":
            case "In Person":
                return CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat())
                        && hearingTypeItem.getValue().getHearingFormat().contains(subSplitHeader);
            case "Stage 1":
            case "Stage 2":
            case "Stage 3":
                return subSplitHeader.equals(hearingTypeItem.getValue().getHearingStage());
            default:
                return false;
        }
    }

    private void setAdhocReport(AdhocReportType adhocReportType, Fields fields,
                                List<AdhocReportTypeItem> adhocReportTypeItemList, boolean hdrReport) {
        adhocReportType.setDate(fields.date);
        adhocReportType.setHearing(String.valueOf(fields.hearing));
        adhocReportType.setHearingCM(String.valueOf(fields.hearingPrelimCM));
        adhocReportType.setHearingPrelim(String.valueOf(fields.hearingPrelim));
        adhocReportType.setCosts(String.valueOf(fields.costs));
        adhocReportType.setReconsider(String.valueOf(fields.reconsider));
        adhocReportType.setRemedy(String.valueOf(fields.remedy));
        adhocReportType.setTotal(String.valueOf(fields.total));
        if (!hdrReport) {
            var adhocReportTypeItem = new AdhocReportTypeItem();
            adhocReportTypeItem.setId(UUID.randomUUID().toString());
            adhocReportTypeItem.setValue(adhocReportType);
            adhocReportTypeItemList.add(adhocReportTypeItem);
        }
    }
}
