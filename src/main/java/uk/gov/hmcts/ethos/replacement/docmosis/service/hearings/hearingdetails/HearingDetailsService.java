package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.hearingdetails;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.HearingSelectionService;

@Service
public class HearingDetailsService {

    private final HearingSelectionService hearingSelectionService;

    public HearingDetailsService(HearingSelectionService hearingSelectionService) {
        this.hearingSelectionService = hearingSelectionService;
    }

    public void initialiseHearingDetails(CaseData caseData) {
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(hearingSelectionService.getHearingSelection(caseData));
        caseData.setHearingDetailsHearing(dynamicFixedListType);
    }

    public void handleListingSelected(CaseData caseData) {
        var selectedListing = getSelectedListing(caseData);

        caseData.setHearingDetailsStatus(selectedListing.getHearingStatus());
        caseData.setHearingDetailsPostponedBy(selectedListing.getPostponedBy());
        caseData.setHearingDetailsCaseDisposed(selectedListing.getHearingCaseDisposed());
        caseData.setHearingDetailsPartHeard(selectedListing.getHearingPartHeard());
        caseData.setHearingDetailsReservedJudgment(selectedListing.getHearingReservedJudgement());
        caseData.setHearingDetailsAttendeeClaimant(selectedListing.getAttendeeClaimant());
        caseData.setHearingDetailsAttendeeNonAttendees(selectedListing.getAttendeeNonAttendees());
        caseData.setHearingDetailsAttendeeRespNoRep(selectedListing.getAttendeeRespNoRep());
        caseData.setHearingDetailsAttendeeRespAndRep(selectedListing.getAttendeeRespAndRep());
        caseData.setHearingDetailsAttendeeRepOnly(selectedListing.getAttendeeRepOnly());
        caseData.setHearingDetailsTimingStart(selectedListing.getHearingTimingStart());
        caseData.setHearingDetailsTimingBreak(selectedListing.getHearingTimingBreak());
        caseData.setHearingDetailsTimingResume(selectedListing.getHearingTimingResume());
        caseData.setHearingDetailsTimingFinish(selectedListing.getHearingTimingFinish());
        caseData.setHearingDetailsTimingDuration(selectedListing.getHearingTimingDuration());
        caseData.setHearingDetailsHearingNotes2(selectedListing.getHearingNotes2());
    }

    public void updateCase(CaseData caseData) {
        var selectedListing = getSelectedListing(caseData);
        selectedListing.setHearingStatus(caseData.getHearingDetailsStatus());
        selectedListing.setPostponedBy(caseData.getHearingDetailsPostponedBy());
        selectedListing.setHearingCaseDisposed(caseData.getHearingDetailsCaseDisposed());
        selectedListing.setHearingPartHeard(caseData.getHearingDetailsPartHeard());
        selectedListing.setHearingReservedJudgement(caseData.getHearingDetailsReservedJudgment());
        selectedListing.setAttendeeClaimant(caseData.getHearingDetailsAttendeeClaimant());
        selectedListing.setAttendeeNonAttendees(caseData.getHearingDetailsAttendeeNonAttendees());
        selectedListing.setAttendeeRespNoRep(caseData.getHearingDetailsAttendeeRespNoRep());
        selectedListing.setAttendeeRespAndRep(caseData.getHearingDetailsAttendeeRespAndRep());
        selectedListing.setAttendeeRepOnly(caseData.getHearingDetailsAttendeeRepOnly());
        selectedListing.setHearingTimingStart(caseData.getHearingDetailsTimingStart());
        selectedListing.setHearingTimingBreak(caseData.getHearingDetailsTimingBreak());
        selectedListing.setHearingTimingResume(caseData.getHearingDetailsTimingResume());
        selectedListing.setHearingTimingFinish(caseData.getHearingDetailsTimingFinish());
        selectedListing.setHearingTimingDuration(caseData.getHearingDetailsTimingDuration());
        selectedListing.setHearingNotes2(caseData.getHearingDetailsHearingNotes2());

        Helper.updatePostponedDate(caseData);
        FlagsImageHelper.buildFlagsImageFileName(caseData);
    }

    private DateListedType getSelectedListing(CaseData caseData) {
        return hearingSelectionService.getSelectedListing(caseData, caseData.getHearingDetailsHearing());
    }
}