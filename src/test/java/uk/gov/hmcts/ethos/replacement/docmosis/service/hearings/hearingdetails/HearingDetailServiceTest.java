package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.hearingdetails;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.HearingSelectionService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.SelectionServiceTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HearingDetailServiceTest {

    private HearingDetailsService hearingDetailsService;
    private DateListedType selectedListing;

    @Before
    public void setup() {
        selectedListing = new DateListedType();
        hearingDetailsService = new HearingDetailsService(mockHearingSelectionService());
    }

    @Test
    public void testInitialiseHearingDetails() {
        var caseData = new CaseData();

        hearingDetailsService.initialiseHearingDetails(caseData);

        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(caseData.getHearingDetailsHearing(), "hearing", "Hearing ");
    }

    @Test
    public void testHandleListingSelected() {
        var caseData = createCaseData();
        var hearingStatus = Constants.HEARING_STATUS_HEARD;
        selectedListing.setHearingStatus(hearingStatus);
        var postponedBy = "Arthur";
        selectedListing.setPostponedBy(postponedBy);
        var caseDisposed = String.valueOf(Boolean.TRUE);
        selectedListing.setHearingCaseDisposed(caseDisposed);
        var partHeard = String.valueOf(Boolean.TRUE);
        selectedListing.setHearingPartHeard(partHeard);
        var reservedJudgment = String.valueOf(Boolean.TRUE);
        selectedListing.setHearingReservedJudgement(reservedJudgment);
        var attendeeClaimant = "1";
        selectedListing.setAttendeeClaimant(attendeeClaimant);
        var attendeeNonAttendees = "2";
        selectedListing.setAttendeeNonAttendees(attendeeNonAttendees);
        var attendeeRespNoRep = "3";
        selectedListing.setAttendeeRespNoRep(attendeeRespNoRep);
        var attendeeRespAndRep = "4";
        selectedListing.setAttendeeRespAndRep(attendeeRespAndRep);
        var attendeeRepOnly = "5";
        selectedListing.setAttendeeRepOnly(attendeeRepOnly);
        var hearingTimeStart = "09:00";
        selectedListing.setHearingTimingStart(hearingTimeStart);
        var hearingTimeBreak = "10:00";
        selectedListing.setHearingTimingBreak(hearingTimeBreak);
        var hearingTimeResume = "11:00";
        selectedListing.setHearingTimingResume(hearingTimeResume);
        var hearingTimeFinish = "12:00";
        selectedListing.setHearingTimingFinish(hearingTimeFinish);
        var duration = "6";
        selectedListing.setHearingTimingDuration(duration);
        var notes = "Some notes";
        selectedListing.setHearingNotes2(notes);

        hearingDetailsService.handleListingSelected(caseData);

        assertEquals(hearingStatus, caseData.getHearingDetailsStatus());
        assertEquals(postponedBy, caseData.getHearingDetailsPostponedBy());
        assertEquals(caseDisposed, caseData.getHearingDetailsCaseDisposed());
        assertEquals(partHeard, caseData.getHearingDetailsPartHeard());
        assertEquals(reservedJudgment, caseData.getHearingDetailsReservedJudgment());
        assertEquals(attendeeClaimant, caseData.getHearingDetailsAttendeeClaimant());
        assertEquals(attendeeNonAttendees, caseData.getHearingDetailsAttendeeNonAttendees());
        assertEquals(attendeeRespNoRep, caseData.getHearingDetailsAttendeeRespNoRep());
        assertEquals(attendeeRespAndRep, caseData.getHearingDetailsAttendeeRespAndRep());
        assertEquals(attendeeRepOnly, caseData.getHearingDetailsAttendeeRepOnly());
        assertEquals(hearingTimeStart, caseData.getHearingDetailsTimingStart());
        assertEquals(hearingTimeBreak, caseData.getHearingDetailsTimingBreak());
        assertEquals(hearingTimeResume, caseData.getHearingDetailsTimingResume());
        assertEquals(hearingTimeFinish, caseData.getHearingDetailsTimingFinish());
        assertEquals(duration, caseData.getHearingDetailsTimingDuration());
        assertEquals(notes, caseData.getHearingDetailsHearingNotes2());
    }

    @Test
    public void testUpdateCase() {
        var caseData = createCaseData();
        var hearingStatus = Constants.HEARING_STATUS_HEARD;
        caseData.setHearingDetailsStatus(hearingStatus);
        var postponedBy = "Arthur";
        caseData.setHearingDetailsPostponedBy(postponedBy);
        var caseDisposed = String.valueOf(Boolean.TRUE);
        caseData.setHearingDetailsCaseDisposed(caseDisposed);
        var partHeard = String.valueOf(Boolean.TRUE);
        caseData.setHearingDetailsPartHeard(partHeard);
        var reservedJudgment = String.valueOf(Boolean.TRUE);
        caseData.setHearingDetailsReservedJudgment(reservedJudgment);
        var attendeeClaimant = "1";
        caseData.setHearingDetailsAttendeeClaimant(attendeeClaimant);
        var attendeeNonAttendees = "2";
        caseData.setHearingDetailsAttendeeNonAttendees(attendeeNonAttendees);
        var attendeeRespNoRep = "3";
        caseData.setHearingDetailsAttendeeRespNoRep(attendeeRespNoRep);
        var attendeeRespAndRep = "4";
        caseData.setHearingDetailsAttendeeRespAndRep(attendeeRespAndRep);
        var attendeeRepOnly = "5";
        caseData.setHearingDetailsAttendeeRepOnly(attendeeRepOnly);
        var hearingTimeStart = "09:00";
        caseData.setHearingDetailsTimingStart(hearingTimeStart);
        var hearingTimeBreak = "10:00";
        caseData.setHearingDetailsTimingBreak(hearingTimeBreak);
        var hearingTimeResume = "11:00";
        caseData.setHearingDetailsTimingResume(hearingTimeResume);
        var hearingTimeFinish = "12:00";
        caseData.setHearingDetailsTimingFinish(hearingTimeFinish);
        var duration = "6";
        caseData.setHearingDetailsTimingDuration(duration);
        var notes = "Some notes";
        caseData.setHearingDetailsHearingNotes2(notes);

        hearingDetailsService.updateCase(caseData);

        assertEquals(hearingStatus, selectedListing.getHearingStatus());
        assertEquals(postponedBy, selectedListing.getPostponedBy());
        assertEquals(caseDisposed, selectedListing.getHearingCaseDisposed());
        assertEquals(partHeard, selectedListing.getHearingPartHeard());
        assertEquals(reservedJudgment, selectedListing.getHearingReservedJudgement());
        assertEquals(attendeeClaimant, selectedListing.getAttendeeClaimant());
        assertEquals(attendeeNonAttendees, selectedListing.getAttendeeNonAttendees());
        assertEquals(attendeeRespNoRep, selectedListing.getAttendeeRespNoRep());
        assertEquals(attendeeRespAndRep, selectedListing.getAttendeeRespAndRep());
        assertEquals(attendeeRepOnly, selectedListing.getAttendeeRepOnly());
        assertEquals(hearingTimeStart, selectedListing.getHearingTimingStart());
        assertEquals(hearingTimeBreak, selectedListing.getHearingTimingBreak());
        assertEquals(hearingTimeResume, selectedListing.getHearingTimingResume());
        assertEquals(hearingTimeFinish, selectedListing.getHearingTimingFinish());
        assertEquals(duration, selectedListing.getHearingTimingDuration());
        assertEquals(notes, selectedListing.getHearingNotes2());
    }

    private HearingSelectionService mockHearingSelectionService() {
        var hearingSelectionService = mock(HearingSelectionService.class);
        var hearings = SelectionServiceTestUtils.createListItems("hearing", "Hearing ");
        when(hearingSelectionService.getHearingSelection(isA(CaseData.class))).thenReturn(hearings);

        when(hearingSelectionService.getSelectedListing(isA(CaseData.class),
                isA(DynamicFixedListType.class))).thenReturn(selectedListing);

        return hearingSelectionService;
    }

    private CaseData createCaseData() {
        var caseData = new CaseData();
        caseData.setHearingDetailsHearing(new DynamicFixedListType());
        return caseData;
    }
}
