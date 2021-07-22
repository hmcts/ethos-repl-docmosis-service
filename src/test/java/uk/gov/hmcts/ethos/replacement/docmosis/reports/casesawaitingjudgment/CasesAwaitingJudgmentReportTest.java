package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;

@RunWith(SpringJUnit4ClassRunner.class)
public class CasesAwaitingJudgmentReportTest {

    @Mock
    ReportDataSource reportDataSource;

    @InjectMocks
    CasesAwaitingJudgmentReport casesAwaitingJudgmentReport;

    CaseDataBuilder caseDataBuilder;

    List<SubmitEvent> submitEvents = new ArrayList<>();

    final String validPositionType;
    static final String USER = "Test User";
    static final String LISTING_DATE = "1970-01-01T00:00:00";

    public CasesAwaitingJudgmentReportTest() {
        caseDataBuilder = new CaseDataBuilder();
        validPositionType = CasesAwaitingJudgmentReport.VALID_POSITION_TYPES.stream().findAny().orElseThrow();
    }

    @Before
    public void setup() {
        submitEvents.clear();
        when(reportDataSource.getData(anyList())).thenReturn(submitEvents);
    }

    @Test
    public void shouldNotShowClosedCase() {
        // Given a case is closed
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.buildAsSubmitEvent(CLOSED_STATE));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertNotNull(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseWithInvalidPositionType() {
        // Given a case is not closed
        // And a case has an invalid position type
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.withPositionType("An invalid position type").buildAsSubmitEvent(ACCEPTED_STATE));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseIfNoHearingsExist() {
        // Given a case is not closed
        // And has a valid position type
        // And has no hearings
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(caseDataBuilder.withPositionType(validPositionType).buildAsSubmitEvent(ACCEPTED_STATE));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseIfNoHearingHasBeenHeard() {
        // Given a case is not closed
        // And has a valid position type
        // And has no hearing that has been heard
        // When I request report data
        // Then the case should not be in the report data

        var submitEvent = caseDataBuilder.withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_LISTED)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseIfHeardButJudgmentMade() {
        // Given a case is not closed
        // And has a valid position type
        // And has been heard
        // And has a judgment
        // When I request report data
        // Then the case should not be in the report data

        var submitEvent = caseDataBuilder.withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .withJudgment()
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldShowValidCase() {
        // Given a case is not closed
        // And has been heard
        // And is awaiting judgment
        // When I request report data
        // Then the case is in the report data

        var submitEvent = caseDataBuilder.withPositionType(validPositionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertEquals(1, reportData.getReportDetails().size());
    }

    @Test
    public void shouldShowTotalPositionValuesInSummary() {
        // Given I have 3 valid cases with position type Draft with Members
        // When I request report data
        // Then the report summary shows 3 Draft with Members
        var positionType = "Draft with members";
        submitEvents.add(createValidSubmitEvent(positionType));
        submitEvents.add(createValidSubmitEvent(positionType));
        submitEvents.add(createValidSubmitEvent(positionType));
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertEquals(3, reportData.getReportDetails().size());
        assertEquals(1, reportData.getReportSummary().getPositionTypes().size());
        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType));
        assertEquals(3, reportData.getReportSummary().getPositionTypes().get(positionType).intValue());
    }

    @Test
    public void shouldShowMultiplePositionValuesInSummary() {
        // Given I have 3 valid cases with position type Draft with Members
        // And I have 2 valid cases with position type Awaiting written reasons
        // And I have 1 valid case with position type Fair copy, to chairman for signature
        // When I request report data
        // Then the report summary shows 3 rows:
        //    | Draft with members                   | 3 |
        //    | Awaiting written reasons             | 2 |
        //    | Fair copy, to chairman for signature | 1 |
        var positionType1 = "Draft with members";
        submitEvents.add(createValidSubmitEvent(positionType1));
        submitEvents.add(createValidSubmitEvent(positionType1));
        submitEvents.add(createValidSubmitEvent(positionType1));
        var positionType2 = "Awaiting written reasons";
        submitEvents.add(createValidSubmitEvent(positionType2));
        submitEvents.add(createValidSubmitEvent(positionType2));
        var positionType3 = "Fair copy, to chairman for signature";
        submitEvents.add(createValidSubmitEvent(positionType3));

        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertEquals(6, reportData.getReportDetails().size());
        assertEquals(3, reportData.getReportSummary().getPositionTypes().size());

        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType1));
        assertEquals(3, reportData.getReportSummary().getPositionTypes().get(positionType1).intValue());
        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType2));
        assertEquals(2, reportData.getReportSummary().getPositionTypes().get(positionType2).intValue());
        assertTrue(reportData.getReportSummary().getPositionTypes().containsKey(positionType3));
        assertEquals(1, reportData.getReportSummary().getPositionTypes().get(positionType3).intValue());
    }

    @Test
    public void shouldContainCorrectDetailValuesForSingleHearingCase() {
        // Given I have a valid case
        // When I request report data
        // Then I have correct report detail values for the case
        var listedDate = "2021-07-21T10:00:00";
        var expectedWeeksSinceHearing = 2;
        var expectedDaysSinceHearing = 100;
        var caseReference = "2500123/2021";
        var expectedMultipleReference = ReportDetail.NO_MULTIPLE_REFERENCE;
        var expectedLastHeardHearingDate = listedDate;
        var hearingNumber = 1;
        var hearingType = Constants.HEARING_TYPE_JUDICIAL_COSTS_HEARING;
        var judge = "Hugh Parkfield";

        SubmitEvent submitEvent = caseDataBuilder.withEthosCaseReference(caseReference)
                .withPositionType(validPositionType)
                .withHearing(listedDate, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE);
        submitEvents.add(submitEvent);
        var caseData = submitEvents.get(0).getCaseData();
        caseData.setEthosCaseReference(caseReference);
        var listingData = new ListingData();

        var reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), USER);
        assertEquals(USER, reportData.getReportSummary().getUser());
        assertEquals(1, reportData.getReportDetails().size());
        var reportDetail = reportData.getReportDetails().get(0);
        assertEquals(caseReference, reportDetail.getCaseNumber());

    }

    private SubmitEvent createValidSubmitEvent(String positionType) {
        caseDataBuilder = new CaseDataBuilder();
        return caseDataBuilder.withPositionType(positionType)
                .withHearing(LISTING_DATE, HEARING_STATUS_HEARD)
                .buildAsSubmitEvent(ACCEPTED_STATE);
    }

}
