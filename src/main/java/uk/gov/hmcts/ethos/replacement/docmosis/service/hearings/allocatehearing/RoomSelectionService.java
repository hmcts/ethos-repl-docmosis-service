package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ethos.replacement.docmosis.service.RoomService;

@Service
public class RoomSelectionService {
    private final RoomService roomService;

    public RoomSelectionService(RoomService roomService) {
        this.roomService = roomService;
    }

    public DynamicFixedListType createRoomSelection(CaseData caseData, DateListedType selectedListing) {
        var selectedVenue = caseData.getAllocateHearingVenue();
        var venueId = selectedVenue.getValue().getCode();
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(roomService.getRooms(venueId));

        if (selectedListing.hasHearingRoom()) {
            var code = selectedListing.getHearingRoom().getSelectedCode();
            if (dynamicFixedListType.isValidCodeForList(code)) {
                dynamicFixedListType.setValue(selectedListing.getHearingRoom().getValue());
            }
        }
        return dynamicFixedListType;
    }
}
