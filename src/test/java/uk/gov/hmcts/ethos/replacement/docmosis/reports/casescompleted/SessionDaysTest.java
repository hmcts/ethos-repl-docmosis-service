package uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_WITHDRAWN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_COSTS_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION_TCC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_RECONSIDERATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_REMEDY;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;

public class SessionDaysTest {

    @Test
    public void shouldGetSessionDaysForSingleHearingSingleSession() {
        var caseDataBuilder = new CaseDataBuilder();
        var caseData = caseDataBuilder
                .withHearing("1", HEARING_TYPE_JUDICIAL_HEARING, "Judge Dave")
                .withHearingSession(0, "2021-07-02T10:00:00", HEARING_STATUS_HEARD, true)
                .build();
        var listingData = createListingData("2021-07-02");

        var sessionDays = new SessionDays(listingData, caseData);

        verifySessionDays(sessionDays, HEARING_TYPE_JUDICIAL_HEARING, 1, "2021-07-02T10:00:00");
    }

    @Test
    public void shouldGetSessionDaysForSingleHearingMultipleSession() {
        var caseDataBuilder = new CaseDataBuilder();
        var caseData = caseDataBuilder
                .withHearing("1", HEARING_TYPE_JUDICIAL_HEARING, "Judge Dave")
                .withHearingSession(0, "2021-07-01T09:00:00", HEARING_STATUS_HEARD, false)
                .withHearingSession(0, "2021-07-02T10:00:00", HEARING_STATUS_HEARD, true)
                .build();
        var listingData = createListingData("2021-07-02");

        var sessionDays = new SessionDays(listingData, caseData);

        verifySessionDays(sessionDays, HEARING_TYPE_JUDICIAL_HEARING, 2, "2021-07-02T10:00:00");
    }

    @Test
    public void shouldGetSessionDaysForMultipleHearings() {
        var caseDataBuilder = new CaseDataBuilder();
        var caseData = caseDataBuilder
                .withHearing("1", HEARING_TYPE_JUDICIAL_HEARING, "Judge Dave")
                .withHearing("2", HEARING_TYPE_JUDICIAL_HEARING, "Judge Brenda")
                .withHearingSession(0, "2021-07-01T09:00:00", HEARING_STATUS_POSTPONED, false)
                .withHearingSession(1, "2021-07-03T09:00:00", HEARING_STATUS_HEARD, false)
                .withHearingSession(1, "2021-07-04T10:00:00", HEARING_STATUS_HEARD, true)
                .build();
        var listingData = createListingData("2021-07-04");

        var sessionDays = new SessionDays(listingData, caseData);

        verifySessionDays(sessionDays, HEARING_TYPE_JUDICIAL_HEARING, 2, "2021-07-04T10:00:00");
    }

    @Test
    public void shouldGetSessionDaysForMultipleHearingsScenario2() {
        var caseDataBuilder = new CaseDataBuilder();
        var caseData = caseDataBuilder
                .withHearing("1", HEARING_TYPE_PERLIMINARY_HEARING, "Judge Dave")
                .withHearing("2", HEARING_TYPE_JUDICIAL_HEARING, "Judge Brenda")
                .withHearingSession(0, "2021-07-01T09:00:00", HEARING_STATUS_POSTPONED, false)
                .withHearingSession(0, "2021-07-02T09:00:00", HEARING_STATUS_HEARD, true)
                .withHearingSession(1, "2021-07-03T10:00:00", HEARING_STATUS_HEARD, true)
                .withHearingSession(1, "2021-07-04T10:00:00", HEARING_STATUS_WITHDRAWN, false)
                .build();
        var listingData = createListingData("2021-07-03");

        var sessionDays = new SessionDays(listingData, caseData);

        verifySessionDays(sessionDays, HEARING_TYPE_JUDICIAL_HEARING, 2, "2021-07-03T10:00:00");
    }

    @Test
    public void shouldGetSessionDaysIgnoreInvalidHearingType() {
        var caseDataBuilder = new CaseDataBuilder();
        var caseData = caseDataBuilder
                .withHearing("1", HEARING_TYPE_PERLIMINARY_HEARING, "Judge Dave")
                .withHearing("2", HEARING_TYPE_JUDICIAL_COSTS_HEARING, "Judge Brenda")
                .withHearingSession(0, "2021-07-01T09:00:00", HEARING_STATUS_POSTPONED, false)
                .withHearingSession(0, "2021-07-02T09:00:00", HEARING_STATUS_HEARD, true)
                .withHearingSession(1, "2021-07-02T10:00:00", HEARING_STATUS_HEARD, true)
                .build();
        var listingData = createListingData("2021-07-02");

        var sessionDays = new SessionDays(listingData, caseData);

        verifySessionDays(sessionDays, HEARING_TYPE_PERLIMINARY_HEARING, 1, "2021-07-02T09:00:00");
    }

    @Test
    public void shouldGetSessionDaysForValidHearingType() {
        for (var hearingType : CasesCompletedReport.VALID_HEARING_TYPES) {
            var caseDataBuilder = new CaseDataBuilder();
            var caseData = caseDataBuilder
                    .withHearing("1", hearingType, "Judge Dave")
                    .withHearingSession(0, "2021-07-01T09:00:00", HEARING_STATUS_HEARD, false)
                    .withHearingSession(0, "2021-07-02T10:00:00", HEARING_STATUS_HEARD, true)
                    .build();
            var listingData = createListingData("2021-07-02");

            var sessionDays = new SessionDays(listingData, caseData);

            verifySessionDays(sessionDays, hearingType, 2, "2021-07-02T10:00:00");
        }
    }

    @Test
    public void shouldGetNoSessionDaysIfCaseHasNoHearings() {
        var sessionDays = new SessionDays(new ListingData(), new CaseData());
        assertNull(sessionDays.getLatestDisposedHearingSession());
    }

    @Test
    public void shouldGetNoSessionDaysIfCaseHasEmptyHearingsCollection() {
        var caseData = new CaseData();
        caseData.setHearingCollection(new ArrayList<>());
        var sessionDays = new SessionDays(new ListingData(), caseData);
        assertNull(sessionDays.getLatestDisposedHearingSession());
    }

    @Test
    public void shouldGetNoSessionDaysIfCaseHasNoValidHearingType() {
        var invalidHearingTypes = List.of(
                HEARING_TYPE_JUDICIAL_COSTS_HEARING,
                HEARING_TYPE_JUDICIAL_MEDIATION,
                HEARING_TYPE_JUDICIAL_MEDIATION_TCC,
                HEARING_TYPE_JUDICIAL_RECONSIDERATION,
                HEARING_TYPE_JUDICIAL_REMEDY);

        for (var invalidHearingType : invalidHearingTypes) {
            var caseDataBuilder = new CaseDataBuilder();
            var caseData = caseDataBuilder
                    .withHearing("1", invalidHearingType, "Judge Doris")
                    .withHearingSession(0, "2021-07-02T10:00:00", HEARING_STATUS_HEARD, true)
                    .build();

            var listingData = new ListingData();
            listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
            listingData.setListingDate("2021-07-02");
            var sessionDays = new SessionDays(listingData, caseData);
            assertNull(sessionDays.getLatestDisposedHearingSession());
        }
    }

    @Test
    public void shouldGetNoSessionDaysIfNotEqualsListedDate() {
        var caseDataBuilder = new CaseDataBuilder();
        var caseData = caseDataBuilder
                .withHearing("1", HEARING_TYPE_JUDICIAL_HEARING, "Judge Dave")
                .withHearingSession(0, "2021-07-01T10:00:00", HEARING_STATUS_HEARD, true)
                .build();

        var listingData = new ListingData();
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setListingDate("2021-07-02");
        var sessionDays = new SessionDays(listingData, caseData);
        assertNull(sessionDays.getLatestDisposedHearingSession());
    }

    @Test
    public void shouldGetNoSessionDaysIfHearingNotDisposed() {
        var caseDataBuilder = new CaseDataBuilder();
        var caseData = caseDataBuilder
                .withHearing("1", HEARING_TYPE_JUDICIAL_HEARING, "Judge Dave")
                .withHearingSession(0, "2021-07-01T10:00:00", HEARING_STATUS_HEARD, false)
                .build();

        var listingData = new ListingData();
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setListingDate("2021-07-01");
        var sessionDays = new SessionDays(listingData, caseData);
        assertNull(sessionDays.getLatestDisposedHearingSession());
    }

    private ListingData createListingData(String listingDate) {
        var listingData = new ListingData();
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setListingDate(listingDate);
        return listingData;
    }

    private void verifySessionDays(SessionDays sessionDays, String expectedHearingType, int expectedSessionDaysCount,
                                   String expectedListedDate) {
        var latestDisposedHearingSession = sessionDays.getLatestDisposedHearingSession();
        assertEquals(expectedHearingType, latestDisposedHearingSession.getHearingType().getHearingType());
        assertEquals(expectedSessionDaysCount, latestDisposedHearingSession.getSessionDays());
        assertEquals(expectedListedDate, latestDisposedHearingSession.getDateListedType().getListedDate());
    }

}
