package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;


@Service
@Slf4j
public class HearingsByHearingTypeReport {

    HearingTypes full;
    HearingTypes sitAlone;
    HearingTypes telCon;
    HearingTypes hybrid;
    HearingTypes video;
    HearingTypes stage1;
    HearingTypes stage2;
    HearingTypes stage3;
    HearingTypes inPerson;
    HearingTypes jm;
    private final static String SUB_SPLIT_STRING = "Hearing %d,Remedy %d,Reconsider %d,Costs %d,Prelim %d,PrelimCM %d";

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
        populateLocalReportSummary(listingDetails.getCaseData(), submitEvents, true);
        populateLocalReportSummaryHdr2(listingDetails.getCaseData(), submitEvents);
        populateLocalReportSummary(listingDetails.getCaseData(), submitEvents, false);
    }

    private static class HearingTypes {
        int hearing;
        int remedy;
        int reconsider;
        int costs;
        int prelim;
        int prelimCM;
    }

    private void setLocalReportSummaryHdr2Fields(CaseData caseData) {
        for (var hearingTypeItem : caseData.getHearingCollection()) {
            if (YES.equals(hearingTypeItem.getValue().getHearingSitAlone())) {
                setHearingsHdr2(hearingTypeItem, sitAlone);
            } else if ("Full".equals(hearingTypeItem.getValue().getHearingSitAlone())) {
                setHearingsHdr2(hearingTypeItem, full);
            }
            if (YES.equals(hearingTypeItem.getValue().getJudicialMediation())) {
                setHearingsHdr2(hearingTypeItem, jm);
            }
            setHearingFormats(hearingTypeItem);
            if ("Stage 1".equals(hearingTypeItem.getValue().getHearingStage())) {
                setHearingsHdr2(hearingTypeItem, stage1);
            } else if ("Stage 2".equals(hearingTypeItem.getValue().getHearingStage())) {
                setHearingsHdr2(hearingTypeItem, stage2);
            } else if ("Stage 3".equals(hearingTypeItem.getValue().getHearingStage())) {
                setHearingsHdr2(hearingTypeItem, stage3);
            }
        }
    }

    private void setHearingFormats (HearingTypeItem hearingTypeItem) {
        if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat()) &&
                hearingTypeItem.getValue().getHearingFormat().contains(MSL_HEARING_FORMAT_TELEPHONE)) {
            setHearingsHdr2(hearingTypeItem, telCon);
        }
        if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat()) &&
                hearingTypeItem.getValue().getHearingFormat().contains("Video")) {
            setHearingsHdr2(hearingTypeItem, video);
        }
        if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat()) &&
                hearingTypeItem.getValue().getHearingFormat().contains("Hybrid")) {
            setHearingsHdr2(hearingTypeItem, hybrid);
        }
        if (CollectionUtils.isNotEmpty(hearingTypeItem.getValue().getHearingFormat()) &&
                hearingTypeItem.getValue().getHearingFormat().contains("In Person")) {
            setHearingsHdr2(hearingTypeItem, inPerson);
        }
    }
    private void populateLocalReportSummaryHdr2(ListingData listingData, List<SubmitEvent> submitEvents) {

         full = new HearingTypes();
         sitAlone = new HearingTypes();
         telCon = new HearingTypes();
         hybrid = new HearingTypes();
         video = new HearingTypes();
         stage1 = new HearingTypes();
         stage2 = new HearingTypes();
         stage3 = new HearingTypes();
         inPerson = new HearingTypes();
         jm = new HearingTypes();
         for (var submitEvent : submitEvents) {
          var caseData = submitEvent.getCaseData();
          if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
              continue;
          }
          setLocalReportSummaryHdr2Fields(caseData);
         }
        var adhocReportType = new AdhocReportType();
        adhocReportType.setHearingSitAlone(getSubSplitString(sitAlone));
        adhocReportType.setHearingFull(getSubSplitString(full));
        adhocReportType.setHearingTelConf(getSubSplitString(telCon));
        adhocReportType.setJudicialMediation(getSubSplitString(jm));
        adhocReportType.setHearingStage1(getSubSplitString(stage1));
        adhocReportType.setHearingStage2(getSubSplitString(stage2));
        adhocReportType.setStage(getSubSplitString(stage3));
        adhocReportType.setHearingPH(getSubSplitString(inPerson));
        adhocReportType.setReservedHearing(getSubSplitString(video));
        adhocReportType.setHearingInterloc(getSubSplitString(hybrid));
        listingData.setLocalReportsSummaryHdr2(adhocReportType);
    }

    private String getSubSplitString(HearingTypes hearingTypes) {
        return String.format(SUB_SPLIT_STRING, hearingTypes.hearing,
                hearingTypes.remedy,
                hearingTypes.reconsider,
                hearingTypes.costs,
                hearingTypes.prelim,
                hearingTypes.prelimCM);
    }
    private void populateLocalReportSummaryHdr(ListingData listingData, List<SubmitEvent> submitEvents) {
        var hearingCasesCount = 0;
        var remedyCasesCount = 0;
        var reconsiderCasesCount = 0;
        var costsCasesCount = 0;
        var hearingPrelimCount = 0;
        var hearingPrelimCMCount = 0;
        var total = 0;

        for (var submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
                continue;
            }
            for (var hearingTypeItem : caseData.getHearingCollection()) {
                if (HEARING_TYPE_JUDICIAL_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
                    hearingCasesCount = hearingCasesCount + 1;
                } else if (HEARING_TYPE_JUDICIAL_REMEDY.equals(hearingTypeItem.getValue().getHearingType())) {
                    remedyCasesCount = remedyCasesCount + 1;
                } else if (HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(hearingTypeItem.getValue().getHearingType())) {
                    reconsiderCasesCount = reconsiderCasesCount + 1;
                } else if (HEARING_TYPE_JUDICIAL_COSTS_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
                    costsCasesCount = costsCasesCount + 1;
                } else if (HEARING_TYPE_PERLIMINARY_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
                    hearingPrelimCount = hearingPrelimCount + 1;
                } else if (HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(hearingTypeItem.getValue().getHearingType())) {
                    hearingPrelimCMCount = hearingPrelimCMCount + 1;
                }
                total = total + 1;
            }
        }
        var adhocReportType = new AdhocReportType();
        adhocReportType.setHearing(String.valueOf(hearingCasesCount));
        adhocReportType.setRemedy(String.valueOf(remedyCasesCount));
        adhocReportType.setReconsider(String.valueOf(reconsiderCasesCount));
        adhocReportType.setCosts(String.valueOf(costsCasesCount));
        adhocReportType.setHearingPrelim(String.valueOf(hearingPrelimCount));
        adhocReportType.setHearingCM(String.valueOf(hearingPrelimCMCount));
        adhocReportType.setTotalCases(String.valueOf(remedyCasesCount));
        listingData.setLocalReportsDetailHdr(adhocReportType);
    }

   private void populateLocalReportSummary (ListingData listingData, List<SubmitEvent> submitEvents, boolean localSummary) {
       List<AdhocReportTypeItem> adhocReportTypeItemList = new ArrayList<>();
        for (var submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
                continue;
            }
            for (var hearingTypeItem : caseData.getHearingCollection()) {
                if (CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                    continue;
                }
                if (localSummary) {
                    setAdhocTypeItemForLocalReportSummary(hearingTypeItem, adhocReportTypeItemList);
                } else {
                    setAdhocTypeItemForLocalReportSummary2(hearingTypeItem, adhocReportTypeItemList);
                }
            }
        }
        if (localSummary) {
            listingData.setLocalReportsSummary(adhocReportTypeItemList);
        } else {
            listingData.setLocalReportsSummary2(adhocReportTypeItemList);
        }
   }

   private void setAdhocTypeItemForLocalReportSummary(HearingTypeItem hearingTypeItem, List<AdhocReportTypeItem> adhocReportTypeItemList) {
       for (var dateListedItem : hearingTypeItem.getValue().getHearingDateCollection()) {
           if (HEARING_STATUS_HEARD.equals(dateListedItem.getValue().getHearingStatus())) {
               if (adhocReportTypeItemList.stream().noneMatch(a-> a != null && a.getValue().getDate().
                       equals(dateListedItem.getValue().getListedDate()))) {
                   var adhocReportTypeItem = new AdhocReportTypeItem();
                   var adhocReportType = new AdhocReportType();
                   adhocReportType.setDate(dateListedItem.getValue().getListedDate());
                   setHearing(hearingTypeItem, adhocReportType);
                   adhocReportTypeItem.setId(UUID.randomUUID().toString());
                   adhocReportTypeItem.setValue(adhocReportType);
                   adhocReportTypeItemList.add(adhocReportTypeItem);
               } else {
                   adhocReportTypeItemList.stream().filter(
                           a -> a.getValue().getDate().equals(dateListedItem.getValue().getListedDate())).findFirst()
                   .ifPresent(adhocReportTypeItem -> setHearing(hearingTypeItem, adhocReportTypeItem.getValue()));
               }
           }
       }
   }

    private void setAdhocTypeItemForLocalReportSummary2(HearingTypeItem hearingTypeItem, List<AdhocReportTypeItem> adhocReportTypeItemList) {
        for (var dateListedItem : hearingTypeItem.getValue().getHearingDateCollection()) {
            if (HEARING_STATUS_HEARD.equals(dateListedItem.getValue().getHearingStatus())) {
                if (adhocReportTypeItemList.stream().noneMatch(a-> a != null && a.getValue().getDate().
                        equals(dateListedItem.getValue().getListedDate()))) {
                    var adhocReportTypeItem = new AdhocReportTypeItem();
                    var adhocReportType = new AdhocReportType();
                    adhocReportType.setDate(dateListedItem.getValue().getListedDate());
                    setHearing(hearingTypeItem, adhocReportType);
                    adhocReportTypeItem.setId(UUID.randomUUID().toString());
                    adhocReportTypeItem.setValue(adhocReportType);
                    adhocReportTypeItemList.add(adhocReportTypeItem);
                } else {
                    adhocReportTypeItemList.stream().filter(
                            a -> a.getValue().getDate().equals(dateListedItem.getValue().getListedDate())).findFirst()
                            .ifPresent(adhocReportTypeItem -> setHearing(hearingTypeItem, adhocReportTypeItem.getValue()));
                }
            }
        }
    }

    private String setCount(String current) {
        if (Strings.isNullOrEmpty(current)) {
            return "1";
        } else {
            var currentCount = Integer.parseInt(current);
            return String.valueOf(currentCount + 1);
        }
    }
   private void setHearing(HearingTypeItem hearingTypeItem, AdhocReportType adhocReportType) {
       if (HEARING_TYPE_JUDICIAL_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
           adhocReportType.setHearing(setCount(adhocReportType.getHearing()));
       } else if (HEARING_TYPE_JUDICIAL_REMEDY.equals(hearingTypeItem.getValue().getHearingType())) {
           adhocReportType.setRemedy(setCount(adhocReportType.getRemedy()));

       } else if (HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(hearingTypeItem.getValue().getHearingType())) {
           adhocReportType.setReconsider(setCount(adhocReportType.getReconsider()));
       } else if (HEARING_TYPE_JUDICIAL_COSTS_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
           adhocReportType.setCosts(setCount(adhocReportType.getCosts()));
       } else if (HEARING_TYPE_PERLIMINARY_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
           adhocReportType.setHearingPrelim(setCount(adhocReportType.getHearingPrelim()));
       } else if (HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(hearingTypeItem.getValue().getHearingType())) {
           adhocReportType.setHearingCM(setCount(adhocReportType.getHearingCM()));
       }
   }

    private void setHearingsHdr2(HearingTypeItem hearingTypeItem, HearingTypes hearingTypes) {
        if (HEARING_TYPE_JUDICIAL_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
         hearingTypes.hearing = hearingTypes.hearing  + 1;
        } else if (HEARING_TYPE_JUDICIAL_REMEDY.equals(hearingTypeItem.getValue().getHearingType())) {
            hearingTypes.remedy = hearingTypes.remedy + 1;
        } else if (HEARING_TYPE_JUDICIAL_RECONSIDERATION.equals(hearingTypeItem.getValue().getHearingType())) {
            hearingTypes.reconsider = hearingTypes.reconsider + 1;
        } else if (HEARING_TYPE_JUDICIAL_COSTS_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
            hearingTypes.costs = hearingTypes.costs + 1;
        } else if (HEARING_TYPE_PERLIMINARY_HEARING.equals(hearingTypeItem.getValue().getHearingType())) {
            hearingTypes.prelim = hearingTypes.prelim + 1;
        } else if (HEARING_TYPE_PERLIMINARY_HEARING_CM.equals(hearingTypeItem.getValue().getHearingType())) {
            hearingTypes.prelimCM = hearingTypes.prelimCM + 1;
        }
    }

//    private boolean isHearingStatusValid(CaseData caseData) {
//        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
//            return false;
//        }
//        for (var hearingTypeItem : caseData.getHearingCollection()) {
//            if (CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
//                return false;
//            }
//            for (var dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
//                if (HEARING_STATUS_HEARD.equals(dateListedTypeItem.getValue().getHearingStatus())) {
//
//                }
//            }
//        }
//    }

}
