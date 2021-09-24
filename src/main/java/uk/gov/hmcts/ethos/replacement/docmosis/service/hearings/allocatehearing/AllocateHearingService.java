package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CourtWorkerType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.HearingSelectionService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.CourtWorkerService;

@Service
public class AllocateHearingService {

    private final HearingSelectionService hearingSelectionService;
    private final JudgeSelectionService judgeSelectionService;
    private final VenueSelectionService venueSelectionService;
    private final RoomSelectionService roomSelectionService;
    private final CourtWorkerService courtWorkerService;

    public AllocateHearingService(HearingSelectionService hearingSelectionService,
                                  JudgeSelectionService judgeSelectionService,
                                  VenueSelectionService venueSelectionService,
                                  RoomSelectionService roomSelectionService,
                                  CourtWorkerService courtWorkerService) {
        this.hearingSelectionService = hearingSelectionService;
        this.judgeSelectionService = judgeSelectionService;
        this.venueSelectionService = venueSelectionService;
        this.roomSelectionService = roomSelectionService;
        this.courtWorkerService = courtWorkerService;
    }

    public void initialiseAllocateHearing(CaseData caseData) {
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(hearingSelectionService.getHearingSelection(caseData));
        caseData.setAllocateHearingHearing(dynamicFixedListType);
    }

    public void handleListingSelected(CaseData caseData) {
        var selectedHearing = getSelectedHearing(caseData);
        caseData.setAllocateHearingJudge(judgeSelectionService.createJudgeSelection(caseData, selectedHearing));

        var selectedListing = getSelectedListing(caseData);
        caseData.setAllocateHearingVenue(venueSelectionService.createVenueSelection(caseData, selectedListing));
        caseData.setAllocateHearingSitAlone(selectedHearing.getHearingSitAlone());
        caseData.setAllocateHearingStatus(selectedListing.getHearingStatus());
        caseData.setAllocateHearingPostponedBy(selectedListing.getPostponedBy());

        addEmployerMembers(caseData, selectedHearing);
        addEmployeeMembers(caseData, selectedHearing);
        addClerk(caseData, selectedListing);
    }

    public void populateRooms(CaseData caseData) {
        var selectedListing = getSelectedListing(caseData);
        caseData.setAllocateHearingRoom(roomSelectionService.createRoomSelection(caseData, selectedListing));
    }

    public void updateCase(CaseData caseData) {
        var selectedHearing = getSelectedHearing(caseData);
        selectedHearing.setHearingSitAlone(caseData.getAllocateHearingSitAlone());
        selectedHearing.setJudge(caseData.getAllocateHearingJudge());
        selectedHearing.setHearingERMember(caseData.getAllocateHearingEmployerMember());
        selectedHearing.setHearingEEMember(caseData.getAllocateHearingEmployeeMember());

        var selectedListing = getSelectedListing(caseData);
        selectedListing.setHearingStatus(caseData.getAllocateHearingStatus());
        selectedListing.setPostponedBy(caseData.getAllocateHearingPostponedBy());

        var selectedVenue = getSelectedVenue(caseData);
        selectedListing.setHearingVenueDay(selectedVenue);

        var selectedRoom = getSelectedRoom(caseData);
        selectedListing.setHearingRoom(selectedRoom);

        var selectedClerk = getSelectedClerk(caseData);
        selectedListing.setHearingClerk(selectedClerk);

        Helper.updatePostponedDate(caseData);
    }

    private HearingType getSelectedHearing(CaseData caseData) {
        return hearingSelectionService.getSelectedHearing(caseData, caseData.getAllocateHearingHearing());
    }

    private DateListedType getSelectedListing(CaseData caseData) {
        return hearingSelectionService.getSelectedListing(caseData, caseData.getAllocateHearingHearing());
    }

    private DynamicFixedListType getSelectedVenue(CaseData caseData) {
        return caseData.getAllocateHearingVenue();
    }

    private DynamicFixedListType getSelectedRoom(CaseData caseData) {
        return caseData.getAllocateHearingRoom();
    }

    private DynamicFixedListType getSelectedClerk(CaseData caseData) {
        return caseData.getAllocateHearingClerk();
    }

    private void addEmployerMembers(CaseData caseData, HearingType selectedHearing) {
        var dynamicFixedListType = createCourtWorkerDynamicFixedListType(
                TribunalOffice.valueOf(caseData.getOwningOffice()), CourtWorkerType.EMPLOYER_MEMBER);

        if (selectedHearing.hasHearingEmployerMember()) {
            dynamicFixedListType.setValue(selectedHearing.getHearingERMember().getValue());
        }
        caseData.setAllocateHearingEmployerMember(dynamicFixedListType);
    }

    private void addEmployeeMembers(CaseData caseData, HearingType selectedHearing) {
        var dynamicFixedListType = createCourtWorkerDynamicFixedListType(
                TribunalOffice.valueOf(caseData.getOwningOffice()), CourtWorkerType.EMPLOYEE_MEMBER);

        if (selectedHearing.hasHearingEmployeeMember()) {
            dynamicFixedListType.setValue(selectedHearing.getHearingEEMember().getValue());
        }
        caseData.setAllocateHearingEmployeeMember(dynamicFixedListType);
    }

    private void addClerk(CaseData caseData, DateListedType selectedListing) {
        var dynamicFixedListType = createCourtWorkerDynamicFixedListType(
                TribunalOffice.valueOf(caseData.getOwningOffice()), CourtWorkerType.CLERK);

        if (selectedListing.hasHearingClerk()) {
            dynamicFixedListType.setValue(selectedListing.getHearingClerk().getValue());
        }
        caseData.setAllocateHearingClerk(dynamicFixedListType);
    }

    private DynamicFixedListType createCourtWorkerDynamicFixedListType(TribunalOffice tribunalOffice,
                                                                       CourtWorkerType courtWorkerType) {
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(
                courtWorkerService.getCourtWorkerByTribunalOffice(tribunalOffice, courtWorkerType));
        return dynamicFixedListType;
    }
}
