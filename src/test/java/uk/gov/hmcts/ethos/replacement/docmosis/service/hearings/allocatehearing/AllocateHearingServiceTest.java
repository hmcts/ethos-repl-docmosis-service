package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CourtWorkerType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.HearingSelectionService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.CourtWorkerService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AllocateHearingServiceTest {

    private AllocateHearingService allocateHearingService;

    private CaseData caseData;
    private final TribunalOffice tribunalOffice = TribunalOffice.ABERDEEN;
    private HearingType selectedHearing;
    private DateListedType selectedListing;

    @Before
    public void setup() {
        caseData = createCaseData();

        var hearingSelectionService = mockHearingSelectionService();
        var judgeSelectionService = mockJudgeSelectionService();
        var venueSelectionService = mockVenueSelectionService();
        var roomSelectionService = mockRoomSelectionService();
        var courtWorkerService = mockCourtWorkerService();
        allocateHearingService = new AllocateHearingService(hearingSelectionService, judgeSelectionService,
                venueSelectionService, roomSelectionService, courtWorkerService);
    }

    @Test
    public void testInitialiseAllocatedHearing() {
        allocateHearingService.initialiseAllocateHearing(caseData);

        var hearings = caseData.getAllocateHearingHearing();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(hearings, "hearing", "Hearing ");
    }

    @Test
    public void testHandleListingSelectedNoExistingSelections() {
        // Arrange
        var hearingSitAlone = String.valueOf(Boolean.TRUE);
        selectedHearing.setHearingSitAlone(hearingSitAlone);

        var hearingStatus = Constants.HEARING_STATUS_HEARD;
        var postponedBy = "Barney";
        selectedListing.setHearingStatus(hearingStatus);
        selectedListing.setPostponedBy(postponedBy);

        // Act
        allocateHearingService.handleListingSelected(caseData);

        // Assert
        var judges = caseData.getAllocateHearingJudge();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(judges, "judge", "Judge ");
        var venues = caseData.getAllocateHearingVenue();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(venues, "venue", "Venue ");
        var clerks = caseData.getAllocateHearingClerk();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(clerks, "clerk", "Clerk ");
        var employerMembers = caseData.getAllocateHearingEmployerMember();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(employerMembers, "employerMember", "Employer Member ");
        var employeeMembers = caseData.getAllocateHearingEmployeeMember();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(employeeMembers, "employeeMember", "Employee Member ");

        assertEquals(hearingSitAlone, caseData.getAllocateHearingSitAlone());
        assertEquals(postponedBy, caseData.getAllocateHearingPostponedBy());
        assertEquals(hearingStatus, caseData.getAllocateHearingStatus());
    }

    @Test
    public void testHandleListingSelectedWithExistingSelections() {
        // Arrange
        var hearingSitAlone = String.valueOf(Boolean.TRUE);
        selectedHearing.setHearingSitAlone(hearingSitAlone);
        var selectedEmployerMember = DynamicValueType.create("employerMember2", "Employer Member 2");
        selectedHearing.setHearingERMember(createDynamicFixedList(selectedEmployerMember));
        var selectedEmployeeMember = DynamicValueType.create("employeeMember2", "Employee Member 2");
        selectedHearing.setHearingEEMember(createDynamicFixedList(selectedEmployeeMember));

        var hearingStatus = Constants.HEARING_STATUS_HEARD;
        var postponedBy = "Barney";
        selectedListing.setHearingStatus(hearingStatus);
        selectedListing.setPostponedBy(postponedBy);
        var selectedClerk = DynamicValueType.create("clerk2", "Clerk 2");
        selectedListing.setHearingClerk(createDynamicFixedList(selectedClerk));

        // Act
        allocateHearingService.handleListingSelected(caseData);

        // Assert
        var judges = caseData.getAllocateHearingJudge();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(judges, "judge", "Judge ");
        var venues = caseData.getAllocateHearingVenue();
        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(venues, "venue", "Venue ");
        var clerks = caseData.getAllocateHearingClerk();
        SelectionServiceTestUtils.verifyDynamicFixedListSelected(clerks, "clerk", "Clerk ", selectedClerk);
        var employerMembers = caseData.getAllocateHearingEmployerMember();
        SelectionServiceTestUtils.verifyDynamicFixedListSelected(employerMembers, "employerMember", "Employer Member ",
                selectedEmployerMember);
        var employeeMembers = caseData.getAllocateHearingEmployeeMember();
        SelectionServiceTestUtils.verifyDynamicFixedListSelected(employeeMembers, "employeeMember", "Employee Member ",
                selectedEmployeeMember);

        assertEquals(hearingSitAlone, caseData.getAllocateHearingSitAlone());
        assertEquals(postponedBy, caseData.getAllocateHearingPostponedBy());
        assertEquals(hearingStatus, caseData.getAllocateHearingStatus());
    }

    @Test
    public void testPopulateRooms() {
        allocateHearingService.populateRooms(caseData);

        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(caseData.getAllocateHearingRoom(),
                "room", "Room ");
    }

    @Test
    public void testUpdateCase() {
        // Arrange
        var sitAlone = String.valueOf(Boolean.TRUE);
        var judge = createDynamicFixedList(DynamicValueType.create("judge2", "Judge 2"));
        var employerMember = createDynamicFixedList(DynamicValueType.create("employerMember2", "Employer Member 2"));
        var employeeMember = createDynamicFixedList(DynamicValueType.create("employeeMember2", "Employee Member 2"));
        var hearingStatus = Constants.HEARING_STATUS_POSTPONED;
        var postponedBy = "Doris";
        var venue = createDynamicFixedList(DynamicValueType.create("venue2", "Venue 2"));
        var room = createDynamicFixedList(DynamicValueType.create("room2", "Room 2"));
        var clerk = createDynamicFixedList(DynamicValueType.create("clerk2", "Clerk 2"));
        caseData.setAllocateHearingSitAlone(sitAlone);
        caseData.setAllocateHearingJudge(judge);
        caseData.setAllocateHearingEmployerMember(employerMember);
        caseData.setAllocateHearingEmployeeMember(employeeMember);
        caseData.setAllocateHearingStatus(hearingStatus);
        caseData.setAllocateHearingPostponedBy(postponedBy);
        caseData.setAllocateHearingVenue(venue);
        caseData.setAllocateHearingRoom(room);
        caseData.setAllocateHearingClerk(clerk);

        // Act
        allocateHearingService.updateCase(caseData);

        // Assert
        assertEquals(sitAlone, selectedHearing.getHearingSitAlone());
        assertEquals(judge.getSelectedCode(), selectedHearing.getJudge().getSelectedCode());
        assertEquals(judge.getSelectedLabel(), selectedHearing.getJudge().getSelectedLabel());
        assertEquals(employerMember.getSelectedCode(), selectedHearing.getHearingERMember().getSelectedCode());
        assertEquals(employerMember.getSelectedLabel(), selectedHearing.getHearingERMember().getSelectedLabel());
        assertEquals(employeeMember.getSelectedCode(), selectedHearing.getHearingEEMember().getSelectedCode());
        assertEquals(employeeMember.getSelectedLabel(), selectedHearing.getHearingEEMember().getSelectedLabel());
        assertEquals(hearingStatus, selectedListing.getHearingStatus());
        assertEquals(postponedBy, selectedListing.getPostponedBy());
        assertEquals(venue.getSelectedCode(), selectedListing.getHearingVenueDay().getSelectedCode());
        assertEquals(venue.getSelectedLabel(), selectedListing.getHearingVenueDay().getSelectedLabel());
        assertEquals(room.getSelectedCode(), selectedListing.getHearingRoom().getSelectedCode());
        assertEquals(room.getSelectedLabel(), selectedListing.getHearingRoom().getSelectedLabel());
        assertEquals(clerk.getSelectedCode(), selectedListing.getHearingClerk().getSelectedCode());
        assertEquals(clerk.getSelectedLabel(), selectedListing.getHearingClerk().getSelectedLabel());
        assertNotNull(selectedListing.getPostponedDate());
    }

    private CaseData createCaseData() {
        var caseData = new CaseData();
        caseData.setOwningOffice(tribunalOffice.name());
        caseData.setAllocateHearingHearing(new DynamicFixedListType());

        selectedHearing = new HearingType();
        selectedListing = new DateListedType();
        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(selectedListing);
        selectedHearing.setHearingDateCollection(List.of(dateListedTypeItem));
        var hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(selectedHearing);
        caseData.setHearingCollection(List.of(hearingTypeItem));

        return caseData;
    }

    private HearingSelectionService mockHearingSelectionService() {
        var hearingSelectionService = mock(HearingSelectionService.class);
        var hearings = SelectionServiceTestUtils.createListItems("hearing", "Hearing ");
        when(hearingSelectionService.getHearingSelection(isA(CaseData.class))).thenReturn(hearings);

        when(hearingSelectionService.getSelectedHearing(isA(CaseData.class),
                isA(DynamicFixedListType.class))).thenReturn(selectedHearing);
        when(hearingSelectionService.getSelectedListing(isA(CaseData.class),
                isA(DynamicFixedListType.class))).thenReturn(selectedListing);

        return hearingSelectionService;
    }

    private JudgeSelectionService mockJudgeSelectionService() {
        var judgeSelectionService = mock(JudgeSelectionService.class);
        var judges = SelectionServiceTestUtils.createListItems("judge", "Judge ");
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(judges);
        when(judgeSelectionService.createJudgeSelection(isA(CaseData.class),
                isA(HearingType.class))).thenReturn(dynamicFixedListType);

        return judgeSelectionService;
    }

    private VenueSelectionService mockVenueSelectionService() {
        var venueSelectionService = mock(VenueSelectionService.class);
        var venues = SelectionServiceTestUtils.createListItems("venue", "Venue ");
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(venues);
        when(venueSelectionService.createVenueSelection(isA(CaseData.class),
                isA(DateListedType.class))).thenReturn(dynamicFixedListType);

        return venueSelectionService;
    }

    private RoomSelectionService mockRoomSelectionService() {
        var roomSelectionService = mock(RoomSelectionService.class);
        var rooms = SelectionServiceTestUtils.createListItems("room", "Room ");
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(rooms);
        when(roomSelectionService.createRoomSelection(isA(CaseData.class),
                isA(DateListedType.class))).thenReturn(dynamicFixedListType);

        return roomSelectionService;
    }

    private CourtWorkerService mockCourtWorkerService() {
        var courtWorkerService = mock(CourtWorkerService.class);
        var clerks = SelectionServiceTestUtils.createListItems("clerk", "Clerk ");
        when(courtWorkerService.getCourtWorkerByTribunalOffice(tribunalOffice,
                CourtWorkerType.CLERK)).thenReturn(clerks);

        var employerMembers = SelectionServiceTestUtils.createListItems("employerMember", "Employer Member ");
        when(courtWorkerService.getCourtWorkerByTribunalOffice(tribunalOffice,
                CourtWorkerType.EMPLOYER_MEMBER)).thenReturn(employerMembers);

        var employeeMembers = SelectionServiceTestUtils.createListItems("employeeMember", "Employee Member ");
        when(courtWorkerService.getCourtWorkerByTribunalOffice(tribunalOffice,
                CourtWorkerType.EMPLOYEE_MEMBER)).thenReturn(employeeMembers);

        return courtWorkerService;
    }



    private DynamicFixedListType createDynamicFixedList(DynamicValueType value) {
        var dynmamicFixedListType = new DynamicFixedListType();
        dynmamicFixedListType.setValue(value);
        return dynmamicFixedListType;
    }
}
