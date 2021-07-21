package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.apache.lucene.index.DocIDMerger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class CasesAwaitingJudgmentReportTest {

    @Mock
    ReportDataSource reportDataSource;

    @InjectMocks
    CasesAwaitingJudgmentReport casesAwaitingJudgmentReport;

    @Test
    public void shouldNotShowClosedCase() {
        // Given a case is closed
        // When I request report data
        // Then the case should not be in the report data

        var submitEvents = new ArrayList<>();
        submitEvents.add(createSubmitEvent(Constants.CLOSED_STATE));

        CasesAwaitingJudgmentReportData reportData = casesAwaitingJudgmentReport.runReport(List.of(Constants.NEWCASTLE_CASE_TYPE_ID), "User 1");
        assertNotNull(reportData);
    }

    private SubmitEvent createSubmitEvent(String state) {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setState(state);

        return submitEvent;
    }

    @Test
    public void shouldNotShowCaseWithInvalidPositionType() {
        // Given a case is not closed
        // And a case has an invalid position type
        // When I request report data
        // Then the case should not be in the report data
    }

    @Test
    public void shouldNotShowCaseIfNotHeard() {
        // Given a case is not closed
        // And has a valid position type
        // And has not been heard
        // When I request report data
        // Then the case should not be in the report data
    }

    @Test
    public void shouldNotShowCaseIfHeardButJudgmentMade() {
        // Given a case is not closed
        // And has a valid position type
        // And has been heard
        // And has a judgment
        // When I request report data
        // Then the case should not be in the report data
    }

    @Test
    public void shouldShowValidCase() {
        // Given a case is not closed
        // And has been heard
        // And is awaiting judgment
        // When I request report data
        // Then the case is in the report data
    }

    @Test
    public void shouldShowTotalPositionValuesInSummary() {
        // Given I have 3 valid cases with position type Draft with Members
        // When I request report data
        // Then the report summary shows 3 Draft with Members
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
    }

}
