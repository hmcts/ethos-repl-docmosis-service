package uk.gov.hmcts.ethos.replacement.docmosis.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper;

import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC;

@Service
@Slf4j
public class CasesCompletedReport {

    public static final String CASES_SEARCHED = "Cases searched: ";

    public ListingData generateReportData(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        if (CollectionUtils.isNotEmpty(submitEvents)) {
            log.info(CASES_SEARCHED + submitEvents.size());
            var localReportsDetailHdr = new AdhocReportType();
            List<AdhocReportTypeItem> localReportsDetailList = new ArrayList<>();
            for (SubmitEvent submitEvent : submitEvents) {
                if (validCaseForCasesCompetedReport(submitEvent) && caseContainsHearings(submitEvent.getCaseData())) {
                    AdhocReportTypeItem localReportsDetailItem =
                            getCasesCompletedDetailItem(listingDetails, submitEvent.getCaseData());
                    if (localReportsDetailItem.getValue() != null) {
                        updateCasesCompletedDetailHdr(localReportsDetailItem, localReportsDetailHdr);
                        localReportsDetailList.add(localReportsDetailItem);
                    }
                }
            }
            localReportsDetailHdr.setReportOffice(UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()));
            listingDetails.getCaseData().setLocalReportsDetailHdr(localReportsDetailHdr);
            listingDetails.getCaseData().setLocalReportsDetail(localReportsDetailList);
        }
        listingDetails.getCaseData().clearReportFields();
        return listingDetails.getCaseData();
    }

    private boolean validCaseForCasesCompetedReport(SubmitEvent submitEvent) {
        var caseData = submitEvent.getCaseData();
        return (submitEvent.getState() != null
                && submitEvent.getState().equals(CLOSED_STATE)
                && validPositionTypeForCasesCompetedReport(caseData)
                && validJurisdictionOutcomeForCasesCompetedReport(caseData));
    }

    private boolean validPositionTypeForCasesCompetedReport(CaseData caseData) {
        if (caseData.getPositionType() != null) {
            List<String> invalidPositionTypes = Arrays.asList(POSITION_TYPE_CASE_INPUT_IN_ERROR,
                    POSITION_TYPE_CASE_TRANSFERRED_SAME_COUNTRY,
                    POSITION_TYPE_CASE_TRANSFERRED_OTHER_COUNTRY);
            return invalidPositionTypes.stream().noneMatch(str -> str.equals(caseData.getPositionType()));
        }
        return true;
    }

    private boolean validJurisdictionOutcomeForCasesCompetedReport(CaseData caseData) {
        if (CollectionUtils.isNotEmpty(caseData.getJurCodesCollection())) {
            for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
                var jurCodesType = jurCodesTypeItem.getValue();
                if (jurCodesType.getJudgmentOutcome() != null
                        &&
                        (jurCodesType.getJudgmentOutcome().equals(JURISDICTION_OUTCOME_SUCCESSFUL_AT_HEARING)
                                || jurCodesType.getJudgmentOutcome().equals(JURISDICTION_OUTCOME_UNSUCCESSFUL_AT_HEARING)
                                || jurCodesType.getJudgmentOutcome().equals(JURISDICTION_OUTCOME_DISMISSED_AT_HEARING))) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean validHearingForCasesCompetedReport(HearingTypeItem hearingTypeItem) {
        if (hearingTypeItem.getValue().getHearingType() != null) {
            List<String> validHearingTypes = Arrays.asList(HEARING_TYPE_JUDICIAL_HEARING,
                    HEARING_TYPE_PERLIMINARY_HEARING,
                    HEARING_TYPE_PERLIMINARY_HEARING_CM,
                    HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC);
            return validHearingTypes.stream().anyMatch(str -> str.equals(hearingTypeItem.getValue().getHearingType()));
        }
        return false;
    }

    private boolean caseContainsHearings(CaseData caseData) {
        return (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty());
    }

    private boolean caseContainsHearingDates(HearingTypeItem hearingTypeItem) {
        return (hearingTypeItem.getValue().getHearingDateCollection() != null
                && !hearingTypeItem.getValue().getHearingDateCollection().isEmpty());
    }

    private void updateCasesCompletedDetailHdr(AdhocReportTypeItem localReportsDetailItem,
                                                      AdhocReportType localReportsDetailHdr) {
        var adhocReportType = localReportsDetailItem.getValue();

        int completedAtHearingPerTrack;
        int sessionDaysTakenPerTrack;
        double completedPerSessionDayPerTrack;

        switch (adhocReportType.getConciliationTrackNo()) {
            case CONCILIATION_TRACK_NUMBER_ONE:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConNoneCasesCompletedHearing())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConNoneCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConNoneSessionDays())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConNoneSessionDays());

                completedAtHearingPerTrack++;
                sessionDaysTakenPerTrack += Integer.parseInt(adhocReportType.getSessionDays());
                completedPerSessionDayPerTrack = (double)completedAtHearingPerTrack / sessionDaysTakenPerTrack;

                localReportsDetailHdr.setConNoneCasesCompletedHearing(Integer.toString(completedAtHearingPerTrack));
                localReportsDetailHdr.setConNoneSessionDays(Integer.toString(sessionDaysTakenPerTrack));
                localReportsDetailHdr.setConNoneCompletedPerSession(Double.toString(completedPerSessionDayPerTrack));
                break;
            case CONCILIATION_TRACK_NUMBER_TWO:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConFastCasesCompletedHearing())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConFastCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConFastSessionDays())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConFastSessionDays());

                completedAtHearingPerTrack++;
                sessionDaysTakenPerTrack += Integer.parseInt(adhocReportType.getSessionDays());
                completedPerSessionDayPerTrack = (double)completedAtHearingPerTrack / sessionDaysTakenPerTrack;

                localReportsDetailHdr.setConFastCasesCompletedHearing(Integer.toString(completedAtHearingPerTrack));
                localReportsDetailHdr.setConFastSessionDays(Integer.toString(sessionDaysTakenPerTrack));
                localReportsDetailHdr.setConFastCompletedPerSession(Double.toString(completedPerSessionDayPerTrack));
                break;
            case CONCILIATION_TRACK_NUMBER_THREE:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConStdCasesCompletedHearing())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConStdCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConStdSessionDays())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConStdSessionDays());

                completedAtHearingPerTrack++;
                sessionDaysTakenPerTrack += Integer.parseInt(adhocReportType.getSessionDays());
                completedPerSessionDayPerTrack = (double)completedAtHearingPerTrack / sessionDaysTakenPerTrack;

                localReportsDetailHdr.setConStdCasesCompletedHearing(Integer.toString(completedAtHearingPerTrack));
                localReportsDetailHdr.setConStdSessionDays(Integer.toString(sessionDaysTakenPerTrack));
                localReportsDetailHdr.setConStdCompletedPerSession(Double.toString(completedPerSessionDayPerTrack));
                break;
            case CONCILIATION_TRACK_NUMBER_FOUR:
                completedAtHearingPerTrack = isNullOrEmpty(localReportsDetailHdr.getConOpenCasesCompletedHearing())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConOpenCasesCompletedHearing());
                sessionDaysTakenPerTrack = isNullOrEmpty(localReportsDetailHdr.getConOpenSessionDays())
                        ? 0
                        : Integer.parseInt(localReportsDetailHdr.getConOpenSessionDays());

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

        int completedAtHearingTotal = isNullOrEmpty(localReportsDetailHdr.getCasesCompletedHearingTotal())
                ? 0
                : Integer.parseInt(localReportsDetailHdr.getCasesCompletedHearingTotal());
        int sessionDaysTakenTotal = isNullOrEmpty(localReportsDetailHdr.getSessionDaysTotal())
                ? 0
                : Integer.parseInt(localReportsDetailHdr.getSessionDaysTotal());

        completedAtHearingTotal++;
        sessionDaysTakenTotal += Integer.parseInt(adhocReportType.getSessionDays());
        double completedPerSessionDayTotal = (double)completedAtHearingTotal / sessionDaysTakenTotal;

        localReportsDetailHdr.setCasesCompletedHearingTotal(Integer.toString(completedAtHearingTotal));
        localReportsDetailHdr.setSessionDaysTotal(Integer.toString(sessionDaysTakenTotal));
        localReportsDetailHdr.setCompletedPerSessionTotal(Double.toString(completedPerSessionDayTotal));
    }

    private AdhocReportTypeItem getCasesCompletedDetailItem(ListingDetails listingDetails, CaseData caseData) {
        var adhocReportTypeItem = new AdhocReportTypeItem();
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            if (validHearingForCasesCompetedReport(hearingTypeItem) && caseContainsHearingDates(hearingTypeItem)) {
                var hearingType = hearingTypeItem.getValue();
                var listingData = listingDetails.getCaseData();
                var maxDateListedType =
                        Collections.max(hearingType.getHearingDateCollection(),
                                Comparator.comparing(c -> c.getValue().getListedDate())).getValue();
                if (maxDateListedType != null) {
                    String dateToSearch = ListingHelper.addMillisToDateToSearch(maxDateListedType.getListedDate());
                    var listedDate =  LocalDateTime.parse(dateToSearch,
                            OLD_DATE_TIME_PATTERN).toLocalDate().toString();
                    boolean matchingDateIsValid = validateMatchingDate(listingData, listedDate);
                    boolean casesCompletedIsValid = casesCompletedIsValid(adhocReportTypeItem, maxDateListedType);
                    if (matchingDateIsValid && casesCompletedIsValid) {
                        var adhocReportType = new AdhocReportType();
                        adhocReportType.setCaseReference(caseData.getEthosCaseReference());
                        adhocReportType.setPosition(caseData.getCurrentPosition());
                        adhocReportType.setConciliationTrackNo(
                                getConciliationTrackNumber(caseData.getConciliationTrack()));
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

    private boolean validateMatchingDate(ListingData listingData, String matchingDate) {
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

    private boolean casesCompletedIsValid(AdhocReportTypeItem adhocReportTypeItem,
                                                 DateListedType dateListedType) {
        var adhocReportType = adhocReportTypeItem.getValue();
        if (dateListedType.getHearingCaseDisposed() != null && dateListedType.getHearingCaseDisposed().equals(YES)) {
            if (adhocReportType != null) {
                var currentHearingDate = LocalDateTime.parse(adhocReportType.getHearingDate());
                var newHearingDate = LocalDateTime.parse(dateListedType.getListedDate());
                return newHearingDate.isAfter(currentHearingDate);
            } else {
                return true;
            }
        }
        return false;
    }

    private String getConciliationTrackNumber(String conciliationTrack) {
        if (!isNullOrEmpty(conciliationTrack)) {
            switch (conciliationTrack) {
                case CONCILIATION_TRACK_NO_CONCILIATION:
                    return CONCILIATION_TRACK_NUMBER_ONE;
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
}
