package uk.gov.hmcts.ethos.replacement.docmosis.reports;

import com.google.common.base.Strings;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReport.OLD_DATE_TIME_PATTERN3;

public class ReportCommonMethods {

    private ReportCommonMethods() {
    }

    public static String getHearingDurationInMinutes(DateListedTypeItem c) {
        var dateListedType = c.getValue();
        long duration = 0;
        long breakDuration = 0;

        var hearingTimingBreak = dateListedType.getHearingTimingBreak();
        var hearingTimingResume = dateListedType.getHearingTimingResume();
        //If there was a break and resumption during the hearing
        if (!Strings.isNullOrEmpty(hearingTimingBreak)
                && !Strings.isNullOrEmpty(hearingTimingResume)) {
            var hearingBreak = convertHearingTime(hearingTimingBreak);
            var hearingResume = convertHearingTime(hearingTimingResume);
            breakDuration = ChronoUnit.MINUTES.between(hearingBreak, hearingResume);
        }

        var hearingTimingStart = dateListedType.getHearingTimingStart();
        var hearingTimingFinish = dateListedType.getHearingTimingFinish();
        if (!Strings.isNullOrEmpty(hearingTimingStart)
                && !Strings.isNullOrEmpty(hearingTimingFinish)) {
            var hearingStartTime = convertHearingTime(hearingTimingStart);
            var hearingEndTime = convertHearingTime(hearingTimingFinish);
            long startToEndDiffInMinutes = ChronoUnit.MINUTES.between(hearingStartTime, hearingEndTime);
            duration = startToEndDiffInMinutes - breakDuration;
        }

        return String.valueOf(duration);
    }

    private static LocalDateTime convertHearingTime(String dateToConvert) {
        return dateToConvert.endsWith(".000")
                ? LocalDateTime.parse(dateToConvert, OLD_DATE_TIME_PATTERN)
                : LocalDateTime.parse(dateToConvert, OLD_DATE_TIME_PATTERN3);
    }
}
