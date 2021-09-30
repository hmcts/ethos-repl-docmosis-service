package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.SelectionServiceTestUtils;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.RoomService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoomSelectionServiceTest {
    @Test
    public void testCreateRoomSelectionNoSelectedRoom() {
        var caseData = mockCaseData();
        var roomService = mockRoomService();
        var selectedListing = mockSelectedListing(null);

        var roomSelectionService = new RoomSelectionService(roomService);
        var actualResult = roomSelectionService.createRoomSelection(caseData, selectedListing);

        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(actualResult, "room", "Room ");
    }

    @Test
    public void testCreateRoomSelectionWithSelectedRoom() {
        var caseData = mockCaseData();
        var roomService = mockRoomService();
        var selectedRoom = DynamicValueType.create("room2", "Room 2");
        var selectedListing = mockSelectedListing(selectedRoom);

        var roomSelectionService = new RoomSelectionService(roomService);
        var actualResult = roomSelectionService.createRoomSelection(caseData, selectedListing);

        SelectionServiceTestUtils.verifyDynamicFixedListSelected(actualResult, "room", "Room ", selectedRoom);
    }

    private CaseData mockCaseData() {
        var caseData = mock(CaseData.class);
        var venue = new DynamicFixedListType();
        venue.setValue(DynamicValueType.create("venue1", "Venue 1"));
        when(caseData.getAllocateHearingVenue()).thenReturn(venue);

        return caseData;
    }

    private RoomService mockRoomService() {
        var dynamicValues = SelectionServiceTestUtils.createListItems("room", "Room ");

        var roomService = mock(RoomService.class);
        when(roomService.getRooms("venue1")).thenReturn(dynamicValues);
        return roomService;
    }

    private DateListedType mockSelectedListing(DynamicValueType selectedRoom) {
        var listing = new DateListedType();
        var dynamicFixedListType = new DynamicFixedListType();
        listing.setHearingRoom(dynamicFixedListType);
        if (selectedRoom != null) {
            dynamicFixedListType.setValue(selectedRoom);
        }

        return listing;
    }
}
