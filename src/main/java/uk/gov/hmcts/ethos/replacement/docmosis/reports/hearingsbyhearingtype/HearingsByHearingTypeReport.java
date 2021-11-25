package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.ReportListingsTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ecm.common.model.listing.types.ReportListingsType;


@Service
@Slf4j
public class HearingsByHearingTypeReport {

    private static final List<String> subSplitList = List.of("EJ Sit Alone", "Full Panel","JM",
            "Tel Con","Video", "Hybrid", "In Person", "Stage 1", "Stage 2", "Stage 3");

    public ListingData processHearingsByHearingTypeRequest(ListingDetails listingDetails,
                                                           List<SubmitEvent> submitEvents) {

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(listingDetails, submitEvents);
        }

        listingDetails.getCaseData().clearReportFields();
        return listingDetails.getCaseData();
    }

    private void executeReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        log.info(String.format("Hearings by hearing type report case type id %s search results: %d",
                listingDetails.getCaseTypeId(), submitEvents.size()));
        populateLocalReportSummaryHdr(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummary(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummaryHdr2(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummary2(listingDetails.getCaseData(), submitEvents);
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
            var adhocReportType = new AdhocReportType();
            var fields = new Fields();
            fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType()))).count();
            fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.costs = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> HEARING_TYPE_JUDICIAL_COSTS_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.reconsider = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType()))).count();
            fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType()))).count();
            fields.calculateTotal();
            setAdhocReport(adhocReportType, fields, null, true);

        listingData.setLocalReportsSummaryHdr(adhocReportType);
    }

    private void populateLocalReportSummary(ListingData listingData, List<SubmitEvent> submitEventList) {
        List<String> datesList = getDatesList(submitEventList);
        List<AdhocReportTypeItem> adhocReportTypeItemList = new ArrayList<>();
        for (var date : datesList) {
            var adhocReportType = new AdhocReportType();
            var fields = new Fields();
            fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                            b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                            HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                            b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                            HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType()))).count();
            fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                            b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                            HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.costs = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                            b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                            HEARING_TYPE_JUDICIAL_COSTS_HEARING.equals(b.getValue().getHearingType()))).count();
            fields.reconsider = (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                            b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                            HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType()))).count();
            fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                    b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                            b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                            HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType()))).count();
            fields.date = date;
            fields.calculateTotal();
            setAdhocReport(adhocReportType, fields, adhocReportTypeItemList, false);

        }
        listingData.setLocalReportsSummary(adhocReportTypeItemList);
    }

    private void populateLocalReportSummaryHdr2(ListingData listingData, List<SubmitEvent> submitEventList) {
        var adhocReportType = new AdhocReportType();
        List<ReportListingsTypeItem> listingHistory = new ArrayList<>();
        for (var subSplitHeader : subSplitList) {
            var fields = new Fields();
            var reportListingsType = new ReportListingsType();
            fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType())
                                    && isHearingFormatValid(subSplitHeader, b))).count();
            fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType())
                                    && isHearingFormatValid(subSplitHeader, b))).count();
            fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType())
                                    && isHearingFormatValid(subSplitHeader, b))).count();
            fields.costs =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_COSTS_HEARING.equals(b.getValue().getHearingType())
                                    && isHearingFormatValid(subSplitHeader, b))).count();
            fields.reconsider =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType())
                                    && isHearingFormatValid(subSplitHeader, b))).count();
            fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                    && a.getCaseData().getHearingCollection().stream().anyMatch(
                            b -> HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType())
                                    && isHearingFormatValid(subSplitHeader, b))).count();
            fields.calculateTotal();
            String hearingNumber = subSplitHeader + "|" + "hearing:" + fields.hearing + "|" +
                    "hearingPrelim:" + fields.hearingPrelim + "|" +
                    "hearingPrelimCM:" + fields.hearingPrelimCM + "|" +
                    "remedy:" + fields.remedy + "|" +
                    "reconsider:" + fields.reconsider + "|" +
                    "costs:" + fields.costs + "|" +
                    "total:" + fields.total;


            reportListingsType.setHearingNumber(hearingNumber);
            var item = new ReportListingsTypeItem();
            item.setId(UUID.randomUUID().toString());
            item.setValue(reportListingsType);
            listingHistory.add(item);
        }
        adhocReportType.setListingHistory(listingHistory);
        listingData.setLocalReportsSummaryHdr2(adhocReportType);
    }

    private void populateLocalReportSummary2(ListingData listingData, List<SubmitEvent> submitEventList) {
        List<String> datesList = getDatesList(submitEventList);
        List<AdhocReportTypeItem> adhocReportTypeItemList = new ArrayList<>();
        for (var date : datesList) {
            for (var subSplitHeader : subSplitList) {
                var adhocReportType = new AdhocReportType();
                var fields = new Fields();
                fields.hearing =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                                b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                                HEARING_TYPE_JUDICIAL_HEARING.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelimCM =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                                b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                                HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.hearingPrelim =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                                b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                                HEARING_TYPE_PERLIMINARY_HEARING.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.costs =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                                b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                                HEARING_TYPE_JUDICIAL_COSTS_HEARING.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.reconsider =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                                b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                                HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.remedy =  (int) submitEventList.stream().filter(a -> CollectionUtils.isNotEmpty(a.getCaseData().getHearingCollection())
                        && a.getCaseData().getHearingCollection().stream().anyMatch(
                        b -> CollectionUtils.isNotEmpty(b.getValue().getHearingDateCollection()) &&
                                b.getValue().getHearingDateCollection().stream().anyMatch(c-> date.equals(c.getValue().getListedDate())) &&
                                HEARING_TYPE_JUDICIAL_REMEDY.equals(b.getValue().getHearingType())
                                && isHearingFormatValid(subSplitHeader, b))).count();
                fields.date = date;
                fields.calculateTotal();
                setAdhocReport(adhocReportType, fields, adhocReportTypeItemList, false);            }
        }
        listingData.setLocalReportsSummary2(adhocReportTypeItemList);
    }

    private boolean isHearingFormatValid(String subSplitHeader, HearingTypeItem hearingTypeItem) {

        return switch (subSplitHeader) {
            case "Full Panel" -> "Full".equals(hearingTypeItem.getValue().getHearingSitAlone());
            case "EJ Sit Alone" -> YES.equals(hearingTypeItem.getValue().getHearingSitAlone());
            case "JM" -> "JM".equals(hearingTypeItem.getValue().getJudicialMediation());
            case "Tel Con" -> hearingTypeItem.getValue().getHearingFormat().contains("Telephone");
            case "Video", "Hybrid", "In Person" -> hearingTypeItem.getValue().getHearingFormat().contains(subSplitHeader);
            case "Stage 1", "Stage 2", "Stage 3" -> subSplitHeader.equals(hearingTypeItem.getValue().getHearingStage());
            default -> false;
        };
    }
    private void setAdhocReport(AdhocReportType adhocReportType, Fields fields, List<AdhocReportTypeItem> adhocReportTypeItemList, boolean hdrReport) {
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
