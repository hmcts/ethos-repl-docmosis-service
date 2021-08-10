package uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted.CasesCompletedReport.VALID_HEARING_TYPES;

class SessionDays {

    private final ListingData listingData;
    private final CaseData caseData;

    SessionDays(ListingData listingData, CaseData caseData) {
        this.listingData = listingData;
        this.caseData = caseData;
    }

    HearingSession getLatestDisposedHearingSession() {
        var heardSessions = getHeardSessions();
        var disposedOfSessions = getDisposedOfSessions(heardSessions);
        if (disposedOfSessions.isEmpty()) {
            return null;
        }

        var latestHearingSession = Collections.max(disposedOfSessions,
                Comparator.comparing(c -> c.getDateListedType().getListedDate()));

        var sessionDays = getSessionDays(heardSessions, latestHearingSession.getDateListedType().getListedDate());
        latestHearingSession.setSessionDays(sessionDays);
        return latestHearingSession;
    }

    private List<HearingSession> getHeardSessions() {
        var hearingSessions = getHearingSessions();
        return hearingSessions.stream()
                .filter(h -> HEARING_STATUS_HEARD.equals(h.getDateListedType().getHearingStatus()))
                .collect(Collectors.toList());
    }

    private List<HearingSession> getDisposedOfSessions(List<HearingSession> hearingSessions) {
        return hearingSessions.stream()
                .filter(h -> YES.equals(h.getDateListedType().getHearingCaseDisposed()))
                .filter(h -> {
                    var listingDate = h.getDateListedType().getListedDate().substring(0, 10);
                    return ReportHelper.validateMatchingDate(listingData, listingDate);
                })
                .collect(Collectors.toList());
    }

    private long getSessionDays(List<HearingSession> heardSessions, String listedDate) {
        return heardSessions.stream()
                .filter(h -> isOnOrEarlierDate(h.getDateListedType().getListedDate(), listedDate))
                .count();
    }

    private boolean isOnOrEarlierDate(String date, String compareTo) {
        var localDate = LocalDate.parse(date.substring(0, 10), OLD_DATE_TIME_PATTERN2);
        var localDateCompareTo = LocalDate.parse(compareTo.substring(0, 10), OLD_DATE_TIME_PATTERN2);
        var after = localDate.isAfter(localDateCompareTo);
        return !after;
    }

    private List<HearingSession> getHearingSessions() {
        var hearings = caseData.getHearingCollection();
        if (hearings == null) {
            return Collections.emptyList();
        }

        return hearings.stream()
                .filter(this::isValidHearing)
                .map(this::mapToHearingSessions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<HearingSession> mapToHearingSessions(HearingTypeItem hearingTypeItem) {
        var hearingType = hearingTypeItem.getValue();
        return hearingTypeItem.getValue().getHearingDateCollection().stream()
                .map(h -> new HearingSession(hearingType, h.getValue()))
                .collect(Collectors.toList());
    }

    private boolean isValidHearing(HearingTypeItem hearingTypeItem) {
        if (hearingTypeItem.getValue() == null
                || CollectionUtils.isEmpty(hearingTypeItem.getValue().getHearingDateCollection())) {
            return false;
        } else {
            return VALID_HEARING_TYPES.contains(hearingTypeItem.getValue().getHearingType());
        }
    }
}
