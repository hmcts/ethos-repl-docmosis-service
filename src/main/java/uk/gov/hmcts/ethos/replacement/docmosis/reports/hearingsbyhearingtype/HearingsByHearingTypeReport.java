package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.ReportListingsTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ecm.common.model.listing.types.ReportListingsType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
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
            "Tel Con", "Video", "Hybrid", "In person", "Stage 1", "Stage 2", "Stage 3");
    private static final Set<String> datesList = new HashSet<>();
    private String costsHearingType = "Costs Hearing";
    private boolean casesExistWithHearingStatusHeard;
    private String listingDateFrom;
    private String listingDateTo;
    private static final String ZERO = "0";
    private static final String HEARING_NUMBER = ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO + "|"
            + ZERO;

    public ListingData processHearingsByHearingTypeRequest(ListingDetails listingDetails,
                                                           List<SubmitEvent> submitEvents,
                                                           String listingDateFrom,
                                                           String listingDateTo) {
        initReport(listingDetails);
        this.listingDateFrom = listingDateFrom;
        this.listingDateTo = listingDateTo;
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
        datesList.clear();
        var reportListingsType = new ReportListingsType();
        reportListingsType.setHearingNumber(HEARING_NUMBER);
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

    private Set<String> getDatesList(List<SubmitEvent> submitEventList) {
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
                    if (checkIfValidHearing(dateListedTypeItem.getValue())) {
                        datesList.add(dateListedTypeItem.getValue().getListedDate());
                    }
                }
            }
        }
        return datesList;
    }

    private boolean checkIfValidHearing(DateListedType dateListedType) {
        LocalDate listedDate;
        var startDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(listingDateFrom));
        var endDate = LocalDate.parse(ReportHelper.getFormattedLocalDate(listingDateTo));
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

    private void populateLocalReportSummaryHdr(ListingData listingData, List<SubmitEvent> submitEventList) {
        var fields = new Fields();
        calculateLocalReportSummaryHdr(submitEventList, fields);
        var adhocReportType = new AdhocReportType();
        setAdhocReport(adhocReportType, fields, null, true);
        listingData.setLocalReportsSummaryHdr(adhocReportType);
    }

    private void calculateLocalReportSummaryHdr(List<SubmitEvent> submitEventList, Fields fields) {
        if (casesExistWithHearingStatusHeard) {
            fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType())
                        && b.getValue().getHearingDateCollection().stream().anyMatch(
                            c -> checkIfValidHearing(c.getValue())
                        ))).count();
            fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(
                                    c -> checkIfValidHearing(c.getValue())
                        ))).count();
            fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType())
                        && b.getValue().getHearingDateCollection().stream().anyMatch(
                            c -> checkIfValidHearing(c.getValue())
                        ))).count();
            fields.costs = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> costsHearingType.equals(b.getValue().getHearingType())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(
                                    c -> checkIfValidHearing(c.getValue())
                        ))).count();
            fields.reconsider = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(
                                    c -> checkIfValidHearing(c.getValue())
                        ))).count();
            fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                    a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType())
                                && b.getValue().getHearingDateCollection().stream().anyMatch(
                                    c -> checkIfValidHearing(c.getValue())
                        ))).count();
            fields.calculateTotal();
        }
    }

    private void populateLocalReportSummary(ListingData listingData, List<SubmitEvent> submitEventList) {
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
            fields.date = date.replace("T", " ");
            fields.calculateTotal();
            var adhocReportType = new AdhocReportType();
            setAdhocReport(adhocReportType, fields, adhocReportTypeItemList, false);
        }
        adhocReportTypeItemList.sort(Comparator.comparing(o -> o.getValue().getDate()));
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
                                && b.getValue().getHearingDateCollection().stream().anyMatch(
                                    c -> checkIfValidHearing(c.getValue()))
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType())
                                    && b.getValue().getHearingDateCollection().stream().anyMatch(
                                        c -> checkIfValidHearing(c.getValue()))
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType())
                                    && b.getValue().getHearingDateCollection().stream().anyMatch(
                                        c -> checkIfValidHearing(c.getValue()))
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.costs =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> costsHearingType.equals(b.getValue().getHearingType())
                                    && b.getValue().getHearingDateCollection().stream().anyMatch(
                                        c -> checkIfValidHearing(c.getValue()))
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.reconsider =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType())
                                    && b.getValue().getHearingDateCollection().stream().anyMatch(
                                        c -> checkIfValidHearing(c.getValue()))
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(
                        a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType())
                                    && b.getValue().getHearingDateCollection().stream().anyMatch(
                                        c -> checkIfValidHearing(c.getValue()))
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
                fields.date = date.replace("T", " ");
                fields.calculateTotal();
                var adhocReportType = new AdhocReportType();
                adhocReportType.setSubSplit(subSplitHeader);
                setAdhocReport(adhocReportType, fields, adhocReportTypeItemList, false);
            }
        }
        adhocReportTypeItemList.sort(Comparator.comparing(o -> o.getValue().getDate()));
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
                checkHearingCollection(adhocReportTypeItemList, caseData);
            }
        }
        adhocReportTypeItemList.sort(Comparator.comparing(o -> o.getValue().getDate()));
        listingData.setLocalReportsDetail(adhocReportTypeItemList);
    }

    private void checkHearingCollection(List<AdhocReportTypeItem> adhocReportTypeItemList, CaseData caseData) {
        for (var hearingTypeItem : caseData.getHearingCollection()) {
            if (CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                continue;
            }
            checkDatesInHearingCollection(adhocReportTypeItemList, caseData, hearingTypeItem);
        }
    }

    private void checkDatesInHearingCollection(List<AdhocReportTypeItem> adhocReportTypeItemList,
                                               CaseData caseData, HearingTypeItem hearingTypeItem) {
        for (var dateListedTypedItem : hearingTypeItem.getValue().getHearingDateCollection()) {
            if (!checkIfValidHearing(dateListedTypedItem.getValue())) {
                continue;
            }
            var adhocReportType = new AdhocReportType();
            adhocReportType.setDate(dateListedTypedItem.getValue().getListedDate().replace("T", " "));
            var mulRef = StringUtils.defaultString(caseData.getMultipleReference(), "0 -  Not Allocated");
            var subMul = StringUtils.defaultString(caseData.getSubMultipleName(), "0 -  Not Allocated");
            adhocReportType.setMultSub(mulRef + ", " + subMul);
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
            case "In person":
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
