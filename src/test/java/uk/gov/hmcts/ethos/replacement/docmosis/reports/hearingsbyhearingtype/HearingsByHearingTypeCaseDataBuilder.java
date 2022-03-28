package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_REMEDY;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeCaseData;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;

public class HearingsByHearingTypeCaseDataBuilder {


    public HearingsByHearingTypeSubmitEvent withNoHearings() {
        HearingsByHearingTypeSubmitEvent submitEvent = new HearingsByHearingTypeSubmitEvent();
        HearingsByHearingTypeCaseData caseData = new HearingsByHearingTypeCaseData();
        caseData.setHearingCollection(null);
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

    private HearingsByHearingTypeSubmitEvent createSubmitEvent(List<HearingTypeItem> hearingCollection, String caseNo, String lead, String mulRef, String mulName) {
        HearingsByHearingTypeSubmitEvent submitEvent = new HearingsByHearingTypeSubmitEvent();
        HearingsByHearingTypeCaseData caseData = new HearingsByHearingTypeCaseData();
        caseData.setHearingCollection(hearingCollection);
        caseData.setEthosCaseReference(caseNo);
        caseData.setLeadClaimant(lead);
        caseData.setMultipleReference(mulRef);
        caseData.setSubMultipleName(mulName);
        caseData.setEthosCaseReference("111");
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

    private DateListedTypeItem createHearingDateListed(String listedDate, String status) {
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(status);
        dateListedType.setHearingClerk("clerk1");
        dateListedTypeItem.setValue(dateListedType);
        dateListedType.setHearingTimingStart(LocalDateTime.now().minusMinutes(480).toString());
        dateListedType.setHearingTimingFinish(LocalDateTime.now().toString());
        dateListedType.setHearingTimingResume(LocalDateTime.now().minusMinutes(230).toString());
        dateListedType.setHearingTimingBreak(LocalDateTime.now().minusMinutes(240).toString());

        return dateListedTypeItem;
    }

    private HearingTypeItem createHearing(String type, String subSplitHeader, DateListedTypeItem... dateListedTypeItems) {
        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        HearingType hearingType = new HearingType();
        hearingType.setHearingType(type);
        hearingType.setHearingNumber("1");
        switch (subSplitHeader) {
            case "Full Panel":
                hearingType.setHearingSitAlone("Full");
                break;
            case "EJ Sit Alone":
                hearingType.setHearingSitAlone("Yes");
                break;
            case "JM":
                hearingType.setJudicialMediation("JM");
                break;
            case "Tel Con":
                hearingType.setHearingFormat(List.of ("Telephone"));
                break;
            case "Video":
                hearingType.setHearingFormat(List.of ("Video"));
                break;
            case "Hybrid":
                hearingType.setHearingFormat(List.of ("Hybrid"));
                break;
            case "In Person":
                hearingType.setHearingFormat(List.of ("In Person"));
                break;
            case "Stage 1":
                hearingType.setHearingStage("Stage 1");
                break;
            case "Stage 2":
                hearingType.setHearingStage("Stage 2");
                break;
            case "Stage 3":
                hearingType.setHearingStage("Stage 3");
                break;
            default:
        }

        List<DateListedTypeItem> hearingDateCollection = new ArrayList<>();
        Collections.addAll(hearingDateCollection, dateListedTypeItems);

        hearingType.setHearingDateCollection(hearingDateCollection);
        hearingTypeItem.setValue(hearingType);
        return hearingTypeItem;
    }

    private List<HearingTypeItem> createHearingCollection(HearingTypeItem... hearings) {
        List<HearingTypeItem> hearingTypeItems = new ArrayList<>();
        Collections.addAll(hearingTypeItems, hearings);
        return hearingTypeItems;
    }

    public List<HearingsByHearingTypeSubmitEvent> createSubmitEvents(String hearingStatus, String mulRef, String mulName) {

        List<HearingsByHearingTypeSubmitEvent> submitEvents = new ArrayList<>();
        DateListedTypeItem dateListedTypeItem = createHearingDateListed("2022-01-01T00:00:00.000",
                hearingStatus);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING, "JM",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "1", "lead1", mulRef,mulName));
        dateListedTypeItem = createHearingDateListed("2022-01-03T00:00:00.000",
                hearingStatus);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_REMEDY, "Hybrid",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "2", "lead2", mulRef,mulName));
        dateListedTypeItem = createHearingDateListed("2022-01-07T00:00:00.000",
                hearingStatus);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Stage 1",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "3", "lead3", mulRef,mulName));
        dateListedTypeItem = createHearingDateListed("2022-01-04T00:00:00.000",
                hearingStatus);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING, "Video",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "4", "lead4", mulRef,mulName));
        dateListedTypeItem = createHearingDateListed("2022-01-10T00:00:00.000",
                hearingStatus);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_PERLIMINARY_HEARING_CM, "Full Panel",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "5", "lead5", mulRef,mulName));
        dateListedTypeItem = createHearingDateListed("2022-01-12T00:00:00.000",
                hearingStatus);
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_RECONSIDERATION, "Yes",
                dateListedTypeItem));
        submitEvents.add(createSubmitEvent(hearings, "6", "lead6", mulRef,mulName));
        return submitEvents;
    }

    public HearingsByHearingTypeSubmitEvent createSubmitEventDateInOutRange() {
        // Hearing outside of range
        var dateListedTypeItem = createHearingDateListed("2021-05-30T00:00:00.000", HEARING_STATUS_HEARD);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING, "Video",
                dateListedTypeItem));
        // Hearing inside of search range
        dateListedTypeItem = createHearingDateListed("2022-01-14T00:00:00.000", HEARING_STATUS_HEARD);
        var hearingTypeItem = createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Video", dateListedTypeItem);
        hearings.add(hearingTypeItem);
        var submitEvent = createSubmitEvent(hearings, "123456", "No", "", "");
        return submitEvent;
    }

    public HearingsByHearingTypeSubmitEvent createSubmitEventNullTime(String propertyToBeNull) {
        List<HearingTypeItem> hearings = new ArrayList<>();
        var dateListedTypeItem = createHearingDateListed("2022-01-17T00:00:00.000", HEARING_STATUS_HEARD);
        if (propertyToBeNull.equals("Start")) {
            dateListedTypeItem.getValue().setHearingTimingStart(null);
        } else if(propertyToBeNull.equals("Finish")) {
            dateListedTypeItem.getValue().setHearingTimingFinish(null);
        } else if(propertyToBeNull.equals("Break")) {
            dateListedTypeItem.getValue().setHearingTimingBreak(null);
        } else if(propertyToBeNull.equals("Resume")) {
            dateListedTypeItem.getValue().setHearingTimingResume(null);
        }
        var hearingTypeItem = createHearing(HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Tel Con", dateListedTypeItem);
        hearings.add(hearingTypeItem);
        var submitEvent = createSubmitEvent(hearings, "123456", "No", "", "");
        return submitEvent;
    }

    public List<HearingsByHearingTypeSubmitEvent> createSubmitEventsWithoutDates() {
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        List<HearingsByHearingTypeSubmitEvent> submitEventsWithoutDates = new ArrayList<>();
        DateListedType type = new DateListedType();
        dateListedTypeItem.setValue(type);
        List<HearingTypeItem> hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_HEARING, "JM",
                dateListedTypeItem));
        submitEventsWithoutDates.add(createSubmitEvent(hearings, "1", "lead1", "multiRef", "subMulti"));
        hearings = createHearingCollection(createHearing(HEARING_TYPE_JUDICIAL_REMEDY, "Hybrid",
                dateListedTypeItem));
        submitEventsWithoutDates.add(createSubmitEvent(hearings, "2", "lead2", "multiRef", "subMulti"));
        return submitEventsWithoutDates;
    }


}
