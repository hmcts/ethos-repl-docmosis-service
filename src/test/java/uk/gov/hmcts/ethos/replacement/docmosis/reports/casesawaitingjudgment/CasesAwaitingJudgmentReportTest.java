package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;

@RunWith(SpringJUnit4ClassRunner.class)
public class CasesAwaitingJudgmentReportTest {

    @Mock
    ReportDataSource reportDataSource;

    @InjectMocks
    CasesAwaitingJudgmentReport casesAwaitingJudgmentReport;

    List<SubmitEvent> submitEvents = new ArrayList<>();

    static String validPositionType;

    @Before
    public void setup() {
        submitEvents.clear();
        when(reportDataSource.getData(anyList())).thenReturn(submitEvents);

        validPositionType = CasesAwaitingJudgmentReport.VALID_POSITION_TYPES.stream().findAny().orElseThrow();
    }

    @Test
    public void shouldNotShowClosedCase() {
        // Given a case is closed
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(createSubmitEvent(Constants.CLOSED_STATE));
        ListingData listingData = new ListingData();

        CasesAwaitingJudgmentReportData reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), "User 1");
        assertNotNull(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseWithInvalidPositionType() {
        // Given a case is not closed
        // And a case has an invalid position type
        // When I request report data
        // Then the case should not be in the report data
        submitEvents.add(createSubmitEventAccepted("An invalid position type"));
        ListingData listingData = new ListingData();

        CasesAwaitingJudgmentReportData reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), "User 1");
        assertNotNull(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseIfNoHearingsExist() {
        // Given a case is not closed
        // And has a valid position type
        // And has no hearings
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(createSubmitEventAccepted(validPositionType));
        ListingData listingData = new ListingData();

        CasesAwaitingJudgmentReportData reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), "User 1");
        assertNotNull(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldNotShowCaseIfNoHearingHasBeenHeard() {
        // Given a case is not closed
        // And has a valid position type
        // And has no hearing that has been heard
        // When I request report data
        // Then the case should not be in the report data

        submitEvents.add(createSubmitEventWithHearing(validPositionType, HEARING_STATUS_LISTED));
        ListingData listingData = new ListingData();

        CasesAwaitingJudgmentReportData reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), "User 1");
        assertNotNull(reportData);
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

        submitEvents.add(createSubmitEventWithJudgment(validPositionType));
        ListingData listingData = new ListingData();

        CasesAwaitingJudgmentReportData reportData = casesAwaitingJudgmentReport.runReport(listingData,
                List.of(NEWCASTLE_CASE_TYPE_ID), "User 1");
        assertNotNull(reportData);
        assertTrue(reportData.getReportDetails().isEmpty());
    }

    @Test
    public void shouldShowValidCase() {
        // Given a case is not closed
        // And has been heard
        // And is awaiting judgment
        // When I request report data
        // Then the case is in the report data
        fail();
    }

    @Test
    public void shouldShowTotalPositionValuesInSummary() {
        // Given I have 3 valid cases with position type Draft with Members
        // When I request report data
        // Then the report summary shows 3 Draft with Members
        fail();
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
        fail();
    }



    private SubmitEvent createSubmitEventWithJudgment(String positionType) {
        SubmitEvent submitEvent = createSubmitEventWithHearing(positionType, HEARING_STATUS_HEARD);
        CaseData caseData = submitEvent.getCaseData();

        var judgementType = new JudgementType();
        var judgementTypeItem = new JudgementTypeItem();
        judgementTypeItem.setValue(judgementType);

        var judgments = new ArrayList<JudgementTypeItem>();
        judgments.add(judgementTypeItem);

        caseData.setJudgementCollection(judgments);

        return submitEvent;
    }

    private SubmitEvent createSubmitEventWithHearing(String positionType, String hearingStatus) {
        SubmitEvent submitEvent = createSubmitEvent(ACCEPTED_STATE);
        submitEvent.setCaseData(createCaseData(positionType,  hearingStatus));
        return submitEvent;
    }

    private SubmitEvent createSubmitEventAccepted(String positionType) {
        SubmitEvent submitEvent = createSubmitEvent(ACCEPTED_STATE);
        submitEvent.setCaseData(createCaseData(positionType,  null));
        return submitEvent;
    }

    private CaseData createCaseData(String positionType, String hearingStatus) {
        CaseData caseData = new CaseData();
        caseData.setPositionType(positionType);

        if (hearingStatus != null) {
            var dateListedType = new DateListedType();
            dateListedType.setHearingStatus(hearingStatus);
            var dateListedTypeItem = new DateListedTypeItem();
            dateListedTypeItem.setValue(dateListedType);

            var hearingDates = new ArrayList<DateListedTypeItem>();
            hearingDates.add(dateListedTypeItem);

            var hearingType = new HearingType();
            hearingType.setHearingDateCollection(hearingDates);

            var hearingTypeItem = new HearingTypeItem();
            hearingTypeItem.setValue(hearingType);
            List<HearingTypeItem> hearings = Collections.singletonList(hearingTypeItem);
            caseData.setHearingCollection(hearings);
        }

        return caseData;
    }

    private SubmitEvent createSubmitEvent(String state) {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setState(state);

        return submitEvent;
    }
}
