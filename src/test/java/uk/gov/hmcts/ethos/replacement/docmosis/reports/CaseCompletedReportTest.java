package uk.gov.hmcts.ethos.replacement.docmosis.reports;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

public class CaseCompletedReportTest {

    private static final String ZERO = "0";

    @Test
    public void testReportHeaderTotalsAreZeroIfNoCasesExist() {
        // given no cases exist
        // when we generate report data
        // then totals are all zero

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();

        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData listingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(listingData);
    }

    @Test
    public void testIgnoreCaseIfNotClosed() {
        // given case is not closed
        // when we generate report data
        // then no data returned

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();

        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(SUBMITTED_STATE));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData listingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(listingData);
    }



    @Test
    public void testIgnoreCaseIfPositionTypeInvalid() {
        // given case is closed
        // given position type is invalid
        // when we generate report data
        // then no data returned
        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();

        listingDetails.setCaseData(listingData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(SUBMITTED_STATE));

        List<String> invalidPositionTypes = Arrays.asList(POSITION_TYPE_CASE_INPUT_IN_ERROR,
                POSITION_TYPE_CASE_TRANSFERRED_SAME_COUNTRY,
                POSITION_TYPE_CASE_TRANSFERRED_OTHER_COUNTRY);

        CaseData caseData = submitEvents.get(0).getCaseData();
        for (String positionType : invalidPositionTypes) {
            caseData.setPositionType(positionType);
            CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
            ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

            verifyReportHeaderIsZero(reportListingData);
        }
    }

    @Test
    public void testIgnoreCaseIfJurisdictionOutcomeInvalid() {
        // given case is closed
        // given position type is valid
        // given jurisdiction outcome is invalid
        // when we generate report data
        // then no data returned
        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData listingData = new ListingData();

        listingDetails.setCaseData(listingData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(CLOSED_STATE));

        List<String> invalidOutcomes = Arrays.asList("This is not a valid outcome", null);

        CaseData caseData = submitEvents.get(0).getCaseData();
        caseData.setJurCodesCollection(new ArrayList<>());
        for (String outcome : invalidOutcomes) {
            caseData.getJurCodesCollection().add(createJurisdiction(outcome));
            CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
            ListingData reportListingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

            verifyReportHeaderIsZero(reportListingData);
            caseData.getJurCodesCollection().clear();
        }
    }

    @Test
    public void testIgnoreCaseIfItContainsNoHearings() {
        // given case is closed
        // given case position type is valid
        // given case jurisdiction outcome is valid
        // given case has no hearings
        // when we generate report data
        // then no data returned

        ListingDetails listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID);
        ListingData caseData = new ListingData();

        listingDetails.setCaseData(caseData);
        List<SubmitEvent> submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(SUBMITTED_STATE, "A valid outcome", Collections.emptyList()));

        CasesCompletedReport casesCompletedReport = new CasesCompletedReport();
        ListingData listingData = casesCompletedReport.generateReportData(listingDetails, submitEvents);

        verifyReportHeaderIsZero(listingData);
    }

    private SubmitEvent createSubmitEvent(String state) {
        return createSubmitEvent(state, null, null);
    }

    private SubmitEvent createSubmitEvent(String state, String jurisdictionOutcome, List<HearingTypeItem> hearingCollection) {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setState(state);

        CaseData caseData = new CaseData();
        if (jurisdictionOutcome != null) {
            caseData.setJurCodesCollection(new ArrayList<>());
            caseData.getJurCodesCollection().add(createJurisdiction(jurisdictionOutcome));
        }

        caseData.setHearingCollection(hearingCollection);

        submitEvent.setCaseData(caseData);

        return submitEvent;
    }

    private JurCodesTypeItem createJurisdiction(String outcome) {
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJudgmentOutcome(outcome);
        jurCodesTypeItem.setValue(jurCodesType);
        return jurCodesTypeItem;
    }

    private void verifyReportHeaderIsZero(ListingData listingData) {
        AdhocReportType adhocReportType = listingData.getLocalReportsDetailHdr();

        // Report header
        assertEquals(ZERO, adhocReportType.getCasesCompletedHearingTotal());
        assertEquals(ZERO, adhocReportType.getSessionDaysTotal());
        assertEquals(ZERO, adhocReportType.getCompletedPerSessionTotal());
        assertEquals("Newcastle", adhocReportType.getReportOffice());

        // Conciliation - No Conciliation
        assertEquals(ZERO, adhocReportType.getConNoneCasesCompletedHearing());
        assertEquals( ZERO, adhocReportType.getConNoneSessionDays());
        assertEquals(ZERO, adhocReportType.getConNoneCompletedPerSession());

        // Conciliation - Fast Track
        assertEquals(ZERO, adhocReportType.getConFastCasesCompletedHearing());
        assertEquals(ZERO, adhocReportType.getConFastSessionDays());
        assertEquals(ZERO, adhocReportType.getConFastCompletedPerSession());

        // Conciliation - Standard Track
        assertEquals(ZERO, adhocReportType.getConStdCasesCompletedHearing());
        assertEquals(ZERO, adhocReportType.getConStdSessionDays());
        assertEquals(ZERO, adhocReportType.getConStdCompletedPerSession());

        // Conciliation - Open Track
        assertEquals(ZERO, adhocReportType.getConOpenCasesCompletedHearing());
        assertEquals(ZERO, adhocReportType.getConOpenSessionDays());
        assertEquals(ZERO, adhocReportType.getConOpenCompletedPerSession());

        assertTrue(listingData.getLocalReportsDetail().isEmpty());
    }

}
