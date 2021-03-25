package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;

import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
public class ReportHelper {

    public static final String CASES_SEARCHED = "Cases searched: ";

    public static ListingData processBroughtForwardDatesRequest(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        if (submitEvents != null && !submitEvents.isEmpty()) {
            log.info(CASES_SEARCHED + submitEvents.size());
            List<BFDateTypeItem> bFDateTypeItems = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                if (submitEvent.getCaseData().getBfActions() != null && !submitEvent.getCaseData().getBfActions().isEmpty()) {
                    for (BFActionTypeItem bfActionTypeItem : submitEvent.getCaseData().getBfActions()) {
                        BFDateTypeItem bFDateTypeItem = getBFDateTypeItem(bfActionTypeItem, listingDetails.getCaseData(), submitEvent.getCaseData());
                        if (bFDateTypeItem.getValue() != null) {
                            bFDateTypeItems.add(bFDateTypeItem);
                        }
                    }
                }
            }
            listingDetails.getCaseData().setBfDateCollection(bFDateTypeItems);
        }
        return clearListingFields(listingDetails.getCaseData());

    }

    public static ListingData processClaimsAcceptedRequest(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        if (submitEvents != null && !submitEvents.isEmpty()) {
            log.info(CASES_SEARCHED + submitEvents.size());
            int totalCases = 0;
            int totalSingles = 0;
            int totalMultiples = 0;
            AdhocReportType localReportsDetailHdr = new AdhocReportType();
            List<AdhocReportTypeItem> localReportsDetailList = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                AdhocReportTypeItem localReportsDetailItem = getClaimsAcceptedDetailItem(listingDetails, submitEvent.getCaseData());
                if (localReportsDetailItem.getValue() != null) {
                    totalCases++;
                    if (localReportsDetailItem.getValue().getCaseType().equals(SINGLE_CASE_TYPE)) totalSingles++;
                    else totalMultiples++;
                    localReportsDetailList.add(localReportsDetailItem);
                }
            }
            localReportsDetailHdr.setTotal(Integer.toString(totalCases));
            localReportsDetailHdr.setSinglesTotal(Integer.toString(totalSingles));
            localReportsDetailHdr.setMultiplesTotal(Integer.toString(totalMultiples));
            listingDetails.getCaseData().setLocalReportsDetailHdr(localReportsDetailHdr);
            listingDetails.getCaseData().setLocalReportsDetail(localReportsDetailList);
        }
        return clearListingFields(listingDetails.getCaseData());

    }

    public static ListingData processLiveCaseloadRequest(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        if (submitEvents != null && !submitEvents.isEmpty()) {
            log.info(CASES_SEARCHED + submitEvents.size());
            List<AdhocReportTypeItem> localReportsDetailList = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                AdhocReportTypeItem localReportsDetailItem = getLiveCaseloadDetailItem(listingDetails, submitEvent.getCaseData());
                if (localReportsDetailItem.getValue() != null) {
                    localReportsDetailList.add(localReportsDetailItem);
                }
            }
            listingDetails.getCaseData().setLocalReportsDetail(localReportsDetailList);
        }
        return clearListingFields(listingDetails.getCaseData());

    }

    public static ListingData processCasesCompletedRequest(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        if (submitEvents != null && !submitEvents.isEmpty()) {
            log.info(CASES_SEARCHED + submitEvents.size());
            AdhocReportType localReportsDetailHdr = new AdhocReportType();
            List<AdhocReportTypeItem> localReportsDetailList = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                if (validCaseForCasesCompetedReport(submitEvent) && caseContainsHearings(submitEvent.getCaseData())) {
                    AdhocReportTypeItem localReportsDetailItem = getCasesCompletedDetailItem(listingDetails, submitEvent.getCaseData());
                    if (localReportsDetailItem.getValue() != null) {
                        updateCasesCompletedDetailHdr(localReportsDetailItem, localReportsDetailHdr);
                        localReportsDetailList.add(localReportsDetailItem);
                    }
                }
            }
            listingDetails.getCaseData().setLocalReportsDetailHdr(localReportsDetailHdr);
            listingDetails.getCaseData().setLocalReportsDetail(localReportsDetailList);
        }
        return clearListingFields(listingDetails.getCaseData());

    }

    public static ListingData clearListingFields(ListingData listingData) {
        listingData.setListingVenueOfficeAber(null);
        listingData.setListingVenueOfficeGlas(null);
        listingData.setVenueGlasgow(null);
        listingData.setVenueAberdeen(null);
        listingData.setVenueDundee(null);
        listingData.setVenueEdinburgh(null);
        listingData.setClerkResponsible(null);
        return listingData;
    }

    private static BFDateTypeItem getBFDateTypeItem(BFActionTypeItem bfActionTypeItem, ListingData listingData, CaseData caseData) {
        BFDateTypeItem bFDateTypeItem = new BFDateTypeItem();
        BFActionType bfActionType = bfActionTypeItem.getValue();
        if (!isNullOrEmpty(bfActionType.getBfDate()) && isNullOrEmpty(bfActionType.getCleared())) {
            boolean matchingDateIsValid = validateMatchingDate(listingData, bfActionType.getBfDate());
            boolean clerkResponsibleIsValid = validateClerkResponsible(listingData, caseData);
            if (matchingDateIsValid && clerkResponsibleIsValid) {
                BFDateType bFDateType = new BFDateType();
                bFDateType.setCaseReference(caseData.getEthosCaseReference());
                bFDateType.setBroughtForwardDate(bfActionType.getBfDate());
                bFDateType.setBroughtForwardDateReason(bfActionType.getNotes());
                bFDateType.setBroughtForwardDateCleared(bfActionType.getCleared());
                bFDateTypeItem.setId(String.valueOf(bfActionTypeItem.getId()));
                bFDateTypeItem.setValue(bFDateType);
            }
        }
        return bFDateTypeItem;
    }

    private static AdhocReportTypeItem getClaimsAcceptedDetailItem(ListingDetails listingDetails, CaseData caseData) {
        AdhocReportTypeItem adhocReportTypeItem = new AdhocReportTypeItem();
        ListingData listingData = listingDetails.getCaseData();
        if (caseData.getPreAcceptCase() != null && caseData.getPreAcceptCase().getDateAccepted() != null) {
            boolean matchingDateIsValid = validateMatchingDate(listingData, caseData.getPreAcceptCase().getDateAccepted());
            if (matchingDateIsValid) {
                AdhocReportType adhocReportType = new AdhocReportType();
                adhocReportType.setCaseType(caseData.getCaseType());
                getCommonReportDetailFields(listingDetails, caseData, adhocReportType);
                adhocReportTypeItem.setValue(adhocReportType);
            }
        }
        return adhocReportTypeItem;
    }

    private static AdhocReportTypeItem getLiveCaseloadDetailItem(ListingDetails listingDetails, CaseData caseData) {
        AdhocReportTypeItem adhocReportTypeItem = new AdhocReportTypeItem();
        ListingData listingData = listingDetails.getCaseData();
        if (caseData.getPreAcceptCase() != null && caseData.getPreAcceptCase().getDateAccepted() != null) {
            boolean matchingDateIsValid = validateMatchingDate(listingData, caseData.getPreAcceptCase().getDateAccepted());
            boolean liveCaseloadIsValid = liveCaseloadIsValid(caseData);
            if (matchingDateIsValid && liveCaseloadIsValid) {
                AdhocReportType adhocReportType = new AdhocReportType();
                adhocReportType.setReportOffice(getTribunalOffice(listingDetails, caseData));
                // TODO : hearingCollection.Hearing_stage implementation
                getCommonReportDetailFields(listingDetails, caseData, adhocReportType);
                adhocReportTypeItem.setValue(adhocReportType);
            }
        }
        return adhocReportTypeItem;
    }

    private static AdhocReportTypeItem getCasesCompletedDetailItem(ListingDetails listingDetails, CaseData caseData) {
        AdhocReportTypeItem adhocReportTypeItem = new AdhocReportTypeItem();
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            if (validHearingForCasesCompetedReport(hearingTypeItem) && caseContainsHearingDates(hearingTypeItem)) {
                HearingType hearingType = hearingTypeItem.getValue();
                ListingData listingData = listingDetails.getCaseData();
                DateListedType maxDateListedType = Collections.max(hearingType.getHearingDateCollection(), Comparator.comparing(c -> c.getValue().getListedDate())).getValue();
                if (maxDateListedType != null) {
                    String listedDate =  LocalDateTime.parse(maxDateListedType.getListedDate(), OLD_DATE_TIME_PATTERN).toLocalDate().toString();
                    boolean matchingDateIsValid = validateMatchingDate(listingData, listedDate);
                    boolean casesCompletedIsValid = casesCompletedIsValid(adhocReportTypeItem, maxDateListedType);
                    if (matchingDateIsValid && casesCompletedIsValid) {
                        AdhocReportType adhocReportType = new AdhocReportType();
                        adhocReportType.setCaseReference(caseData.getEthosCaseReference());
                        adhocReportType.setPosition(caseData.getCurrentPosition());
                        adhocReportType.setConciliationTrackNo(getConciliationTrackNumber(caseData.getConciliationTrack()));
                        adhocReportType.setSessionDays(String.valueOf(hearingType.getHearingDateCollection().size()));
                        adhocReportType.setHearingNumber(hearingType.getHearingNumber());
                        adhocReportType.setHearingDate(maxDateListedType.getListedDate());
                        adhocReportType.setHearingType(hearingType.getHearingType());
                        adhocReportType.setHearingJudge(hearingType.getJudge());
                        adhocReportType.setHearingClerk(maxDateListedType.getHearingClerk());
                        adhocReportTypeItem.setValue(adhocReportType);
                    }
                }
            }
        }
        return adhocReportTypeItem;
    }

    private static void updateCasesCompletedDetailHdr(AdhocReportTypeItem localReportsDetailItem, AdhocReportType localReportsDetailHdr) {
        AdhocReportType adhocReportType = localReportsDetailItem.getValue();

        int completedAtHearingTotal;
        int sessionDaysTakenTotal;
        double completedPerSessionDayTotal;

        int completedAtHearingPerTrack;
        int sessionDaysTakenPerTrack;
        double completedPerSessionDayPerTrack;

        switch (adhocReportType.getConciliationTrackNo()) {
            case CONCILIATION_TRACK_NUMBER_ONE:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConNoneCasesCompletedHearing()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConNoneCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConNoneSessionDays()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConNoneSessionDays());

                completedAtHearingPerTrack++;
                sessionDaysTakenPerTrack += Integer.parseInt(adhocReportType.getSessionDays());
                completedPerSessionDayPerTrack = (double)completedAtHearingPerTrack / sessionDaysTakenPerTrack;

                localReportsDetailHdr.setConNoneCasesCompletedHearing(Integer.toString(completedAtHearingPerTrack));
                localReportsDetailHdr.setConNoneSessionDays(Integer.toString(sessionDaysTakenPerTrack));
                localReportsDetailHdr.setConNoneCompletedPerSession(Double.toString(completedPerSessionDayPerTrack));
                break;
            case CONCILIATION_TRACK_NUMBER_TWO:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConFastCasesCompletedHearing()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConFastCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConFastSessionDays()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConFastSessionDays());

                completedAtHearingPerTrack++;
                sessionDaysTakenPerTrack += Integer.parseInt(adhocReportType.getSessionDays());
                completedPerSessionDayPerTrack = (double)completedAtHearingPerTrack / sessionDaysTakenPerTrack;

                localReportsDetailHdr.setConFastCasesCompletedHearing(Integer.toString(completedAtHearingPerTrack));
                localReportsDetailHdr.setConFastSessionDays(Integer.toString(sessionDaysTakenPerTrack));
                localReportsDetailHdr.setConFastCompletedPerSession(Double.toString(completedPerSessionDayPerTrack));
                break;
            case CONCILIATION_TRACK_NUMBER_THREE:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConStdCasesCompletedHearing()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConStdCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConStdSessionDays()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConStdSessionDays());

                completedAtHearingPerTrack++;
                sessionDaysTakenPerTrack += Integer.parseInt(adhocReportType.getSessionDays());
                completedPerSessionDayPerTrack = (double)completedAtHearingPerTrack / sessionDaysTakenPerTrack;

                localReportsDetailHdr.setConStdCasesCompletedHearing(Integer.toString(completedAtHearingPerTrack));
                localReportsDetailHdr.setConStdSessionDays(Integer.toString(sessionDaysTakenPerTrack));
                localReportsDetailHdr.setConStdCompletedPerSession(Double.toString(completedPerSessionDayPerTrack));
                break;
            case CONCILIATION_TRACK_NUMBER_FOUR:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConOpenCasesCompletedHearing()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConOpenCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConOpenSessionDays()) ? 0 : Integer.parseInt(localReportsDetailHdr.getConOpenSessionDays());

                completedAtHearingPerTrack++;
                sessionDaysTakenPerTrack += Integer.parseInt(adhocReportType.getSessionDays());
                completedPerSessionDayPerTrack = (double)completedAtHearingPerTrack / sessionDaysTakenPerTrack;

                localReportsDetailHdr.setConOpenCasesCompletedHearing(Integer.toString(completedAtHearingPerTrack));
                localReportsDetailHdr.setConOpenSessionDays(Integer.toString(sessionDaysTakenPerTrack));
                localReportsDetailHdr.setConOpenCompletedPerSession(Double.toString(completedPerSessionDayPerTrack));
                break;
            default:
                return;
        }

        completedAtHearingTotal = isNullOrEmpty(localReportsDetailHdr.getCasesCompletedHearingTotal()) ? 0 : Integer.parseInt(localReportsDetailHdr.getCasesCompletedHearingTotal());
        sessionDaysTakenTotal = isNullOrEmpty(localReportsDetailHdr.getSessionDaysTotal()) ? 0 : Integer.parseInt(localReportsDetailHdr.getSessionDaysTotal());

        completedAtHearingTotal++;
        sessionDaysTakenTotal += Integer.parseInt(adhocReportType.getSessionDays());
        completedPerSessionDayTotal = (double)completedAtHearingTotal / sessionDaysTakenTotal;

        localReportsDetailHdr.setCasesCompletedHearingTotal(Integer.toString(completedAtHearingTotal));
        localReportsDetailHdr.setSessionDaysTotal(Integer.toString(sessionDaysTakenTotal));
        localReportsDetailHdr.setCompletedPerSessionTotal(Double.toString(completedPerSessionDayTotal));
    }

    private static boolean validateMatchingDate(ListingData listingData, String matchingDate) {
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (!dateRange) {
            String dateToSearch = listingData.getListingDate();
            return ListingHelper.getMatchingDateBetween(dateToSearch, "", matchingDate, false);
        } else {
            String dateToSearchFrom = listingData.getListingDateFrom();
            String dateToSearchTo = listingData.getListingDateTo();
            return ListingHelper.getMatchingDateBetween(dateToSearchFrom, dateToSearchTo, matchingDate, true);
        }
    }

    private static boolean validateClerkResponsible(ListingData listingData, CaseData caseData) {
        if (listingData.getClerkResponsible() != null) {
            if (caseData.getClerkResponsible() != null) {
                return listingData.getClerkResponsible().equals(caseData.getClerkResponsible());
            }
            return false;
        }
        return true;
    }

    private static void getCommonReportDetailFields(ListingDetails listingDetails, CaseData caseData, AdhocReportType adhocReportType) {
        adhocReportType.setCaseReference(caseData.getEthosCaseReference());
        adhocReportType.setDateOfAcceptance(caseData.getPreAcceptCase().getDateAccepted());
        adhocReportType.setMultipleRef(caseData.getMultipleReference());
        adhocReportType.setLeadCase(caseData.getLeadClaimant());
        adhocReportType.setPosition(caseData.getCurrentPosition());
        adhocReportType.setDateToPosition(caseData.getDateToPosition());
        adhocReportType.setFileLocation(getFileLocation(listingDetails, caseData));
        adhocReportType.setClerk(caseData.getClerkResponsible());
    }

    private static String getFileLocation(ListingDetails listingDetails, CaseData caseData) {
        String caseTypeId = ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId());
        if (!caseTypeId.equals(SCOTLAND_CASE_TYPE_ID)) {
            return caseData.getFileLocation();
        } else {
            switch (caseData.getManagingOffice()) {
                case DUNDEE_OFFICE:
                    return caseData.getFileLocationDundee();
                case GLASGOW_OFFICE:
                    return caseData.getFileLocationGlasgow();
                case ABERDEEN_OFFICE:
                    return caseData.getFileLocationAberdeen();
                case EDINBURGH_OFFICE:
                    return caseData.getFileLocationEdinburgh();
                default:
                    return "";
            }
        }
    }

    private static String getTribunalOffice(ListingDetails listingDetails, CaseData caseData) {
        String caseTypeId = ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId());
        return caseTypeId.equals(SCOTLAND_CASE_TYPE_ID) ? caseData.getManagingOffice() : caseTypeId;
    }

    private static String getConciliationTrackNumber(String conciliationTrack) {
        if (!isNullOrEmpty(conciliationTrack)) {
            switch (conciliationTrack) {
                case CONCILIATION_TRACK_NO_CONCILIATION:
                    return CONCILIATION_TRACK_NUMBER_ONE ;
                case CONCILIATION_TRACK_FAST_TRACK:
                    return CONCILIATION_TRACK_NUMBER_TWO;
                case CONCILIATION_TRACK_STANDARD_TRACK:
                    return CONCILIATION_TRACK_NUMBER_THREE;
                case CONCILIATION_TRACK_OPEN_TRACK:
                    return CONCILIATION_TRACK_NUMBER_FOUR;
                default:
                    return "0";
            }
        }
        return "0";
    }

    private static boolean liveCaseloadIsValid(CaseData caseData) {
        if (caseData.getPositionType() != null) {
            List<String> invalidPositionTypes = Arrays.asList(POSITION_TYPE_REJECTED,
                    POSITION_TYPE_CASE_CLOSED,
                    POSITION_TYPE_CASE_INPUT_IN_ERROR,
                    POSITION_TYPE_CASE_TRANSFERRED_SAME_COUNTRY,
                    POSITION_TYPE_CASE_TRANSFERRED_OTHER_COUNTRY);
            return invalidPositionTypes.stream().noneMatch(str -> str.equals(caseData.getPositionType()));
        }
        return true;
    }

    private static boolean validCaseForCasesCompetedReport(SubmitEvent submitEvent) {
        CaseData caseData = submitEvent.getCaseData();
        return (submitEvent.getState() != null &&
                submitEvent.getState().equals(CLOSED_STATE) &&
                validPositionTypeForCasesCompetedReport(caseData) &&
                validJurisdictionOutcomeForCasesCompetedReport(caseData));
    }

    private static boolean validPositionTypeForCasesCompetedReport(CaseData caseData) {
        if (caseData.getPositionType() != null) {
            List<String> invalidPositionTypes = Arrays.asList(POSITION_TYPE_CASE_INPUT_IN_ERROR,
                    POSITION_TYPE_CASE_TRANSFERRED_SAME_COUNTRY,
                    POSITION_TYPE_CASE_TRANSFERRED_OTHER_COUNTRY);
            return invalidPositionTypes.stream().noneMatch(str -> str.equals(caseData.getPositionType()));
        }
        return true;
    }

    private static boolean validJurisdictionOutcomeForCasesCompetedReport(CaseData caseData) {
        if (caseData.getJurCodesCollection() != null && !caseData.getJurCodesCollection().isEmpty()) {
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                JurCodesType jurCodesType = jurCodesTypeItem.getValue();
                if (jurCodesType.getJudgmentOutcome() != null &&
                        (jurCodesType.getJudgmentOutcome().equals(JURISDICTION_OUTCOME_SUCCESSFUL_AT_HEARING) ||
                                jurCodesType.getJudgmentOutcome().equals(JURISDICTION_OUTCOME_UNSUCCESSFUL_AT_HEARING) ||
                                jurCodesType.getJudgmentOutcome().equals(JURISDICTION_OUTCOME_DISMISSED_AT_HEARING))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean validHearingForCasesCompetedReport(HearingTypeItem hearingTypeItem) {
        if (hearingTypeItem.getValue().getHearingType() != null) {
            List<String> validHearingTypes = Arrays.asList(HEARING_TYPE_JUDICIAL_HEARING,
                    HEARING_TYPE_PERLIMINARY_HEARING,
                    HEARING_TYPE_PERLIMINARY_HEARING_CM,
                    HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC);
            return validHearingTypes.stream().anyMatch(str -> str.equals(hearingTypeItem.getValue().getHearingType()));
        }
        return false;
    }

    private static boolean casesCompletedIsValid(AdhocReportTypeItem adhocReportTypeItem, DateListedType dateListedType) {
        AdhocReportType adhocReportType = adhocReportTypeItem.getValue();
        if (dateListedType.getHearingCaseDisposed() != null && dateListedType.getHearingCaseDisposed().equals(YES)) {
            if (adhocReportType != null) {
                LocalDateTime currentHearingDate = LocalDateTime.parse(adhocReportType.getHearingDate());
                LocalDateTime newHearingDate = LocalDateTime.parse(dateListedType.getListedDate());
                return newHearingDate.isAfter(currentHearingDate);
            } else {
                return true;
            }
        }
        return false;
    }

    private static boolean caseContainsHearings(CaseData caseData) {
        return (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty());
    }

    private static boolean caseContainsHearingDates(HearingTypeItem hearingTypeItem) {
        return (hearingTypeItem.getValue().getHearingDateCollection() != null && !hearingTypeItem.getValue().getHearingDateCollection().isEmpty());
    }

}
