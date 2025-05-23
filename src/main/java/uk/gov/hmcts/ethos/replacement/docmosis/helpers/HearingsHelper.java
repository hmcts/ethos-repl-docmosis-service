package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;

public class HearingsHelper {
    private HearingsHelper() {
    }

    public static final String HEARING_CREATION_NUMBER_ERROR = "A new hearing can only "
            + "be added from the List Hearing menu item";
    public static final String HEARING_CREATION_DAY_ERROR = "A new day for a hearing can "
            + "only be added from the List Hearing menu item";
    public static final String HEARING_FINISH_INVALID = "The finish time for a hearing cannot be the "
            + "same or before the start time for Hearing ";
    public static final String HEARING_START_FUTURE = "Start time can't be in future";
    public static final String HEARING_FINISH_FUTURE = "Finish time can't be in future";
    public static final String HEARING_BREAK_FUTURE = "Break time can't be in future";
    public static final String HEARING_RESUME_FUTURE = "Resume time can't be in future";
    public static final String HEARING_BREAK_RESUME_INVALID = "Hearing %s contains a hearing with break and "
            + "resume times of 00:00:00. If the hearing had a break then please update the times. If there was no "
            + "break, please remove the hearing date and times from the break and resume fields before continuing.";
    public static final String TWO_JUDGES = "Two Judges";
    public static final String TWO_JUDGES_ERROR = "Please choose a different judge for the second "
            + "judge as the same judge has been selected for both judges for hearing %s.";

    public static String findHearingNumber(CaseData caseData, String hearingDate) {
        if (isNotEmpty(caseData.getHearingCollection())) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                    var listedDate = dateListedTypeItem.getValue().getListedDate().substring(0, 10);
                    if (listedDate.equals(hearingDate)) {
                        return hearingTypeItem.getValue().getHearingNumber();
                    }
                }
            }
        }
        return null;
    }

    static boolean isHearingStatusPostponed(DateListedType dateListedType) {
        return dateListedType.getHearingStatus() != null
                && dateListedType.getHearingStatus().equals(HEARING_STATUS_POSTPONED);
    }

    public static List<String> hearingMidEventValidation(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (isEmpty(caseData.getHearingCollection())) {
            return errors;
        }
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            HearingType hearingType = hearingTypeItem.getValue();
            if (isNullOrEmpty(hearingType.getHearingNumber())) {
                errors.add(HEARING_CREATION_NUMBER_ERROR);
                return errors;
            }
            if (isNotEmpty(hearingType.getHearingDateCollection())) {
                listedDateAndJudgeValidation(hearingType, errors);
            }
        }
        return errors;
    }

    private static void listedDateAndJudgeValidation(HearingType hearingType, List<String> errors) {
        if (TWO_JUDGES.equals(hearingType.getHearingSitAlone())
            && !isNullOrEmpty(hearingType.getJudge())
            && !isNullOrEmpty(hearingType.getAdditionalJudge())
            && hearingType.getJudge().equals(hearingType.getAdditionalJudge())) {
                errors.add(String.format(TWO_JUDGES_ERROR, hearingType.getHearingNumber()));
            }
        for (DateListedTypeItem dateListedTypeItem : hearingType.getHearingDateCollection()) {
            if (isNullOrEmpty(dateListedTypeItem.getValue().getListedDate())) {
                errors.add(HEARING_CREATION_DAY_ERROR);
            }
        }
    }

    public static void updatePostponedDate(CaseData caseData) {
        if (caseData.getHearingCollection() != null) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {
                        var dateListedType = dateListedTypeItem.getValue();
                        if (isHearingStatusPostponed(dateListedType) && dateListedType.getPostponedDate() == null) {
                            dateListedType.setPostponedDate(UtilHelper.formatCurrentDate2(LocalDate.now()));
                        }
                        if (dateListedType.getPostponedDate() != null
                                &&
                                (!isHearingStatusPostponed(dateListedType)
                                        || dateListedType.getHearingStatus() == null)) {
                            dateListedType.setPostponedDate(null);
                        }
                    }
                }
            }
        }

    }

    public static List<String> hearingTimeValidation(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (CollectionUtils.isEmpty(caseData.getHearingsCollectionForUpdate())) {
            return errors;
        }
        for (HearingTypeItem hearingTypeItem : caseData.getHearingsCollectionForUpdate()) {
            if (CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
                continue;
            }
            for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                var dateListedType = dateListedTypeItem.getValue();
                if (HEARING_STATUS_HEARD.equals(dateListedType.getHearingStatus())) {
                    checkStartFinishTimes(errors, dateListedType,
                            hearingTypeItem.getValue().getHearingNumber());
                    checkIfDateInFuture(errors, dateListedType);
                    checkBreakResumeTimes(errors, dateListedType,
                            hearingTypeItem.getValue().getHearingNumber());
                }
            }
        }
        return errors;
    }

    private static void checkBreakResumeTimes(List<String> errors, DateListedType dateListedType,
                                              String hearingNumber) {
        var breakTime = !isNullOrEmpty(dateListedType.getHearingTimingBreak())
                ? LocalDateTime.parse(dateListedType.getHearingTimingBreak()).toLocalTime() : null;
        var resumeTime = !isNullOrEmpty(dateListedType.getHearingTimingResume())
                ? LocalDateTime.parse(dateListedType.getHearingTimingResume()).toLocalTime() : null;
        var invalidTime = LocalTime.of(0, 0, 0, 0);
        if (invalidTime.equals(breakTime) || invalidTime.equals(resumeTime)) {
            errors.add(String.format(HEARING_BREAK_RESUME_INVALID, hearingNumber));
        }
    }

    private static void checkIfDateInFuture(List<String> errors, DateListedType dateListedType) {
        if (isDateInFuture(dateListedType.getHearingTimingStart(), LocalDateTime.now())) {
            errors.add(HEARING_START_FUTURE);
        }
        if (isDateInFuture(dateListedType.getHearingTimingResume(), LocalDateTime.now())) {
            errors.add(HEARING_RESUME_FUTURE);
        }
        if (isDateInFuture(dateListedType.getHearingTimingBreak(), LocalDateTime.now())) {
            errors.add(HEARING_BREAK_FUTURE);
        }
        if (isDateInFuture(dateListedType.getHearingTimingFinish(), LocalDateTime.now())) {
            errors.add(HEARING_FINISH_FUTURE);
        }
    }

    public static boolean isDateInFuture(String date, LocalDateTime now) {
        //Azure times are always in UTC and users enter Europe/London Times,
        // so respective zonedDateTimes should be compared.
        return !isNullOrEmpty(date) && LocalDateTime.parse(date).atZone(ZoneId.of("Europe/London"))
                .isAfter(now.atZone(ZoneId.of("UTC")));
    }

    private static void checkStartFinishTimes(List<String> errors, DateListedType dateListedType,
                                              String hearingNumber) {
        var startTime = LocalDateTime.parse(dateListedType.getHearingTimingStart());
        var finishTime = LocalDateTime.parse(dateListedType.getHearingTimingFinish());
        if (!finishTime.isAfter(startTime)) {
            errors.add(HEARING_FINISH_INVALID + hearingNumber);
        }
    }
}
