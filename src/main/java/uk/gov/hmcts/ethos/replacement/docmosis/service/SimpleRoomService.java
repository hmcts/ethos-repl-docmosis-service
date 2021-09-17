package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleRoomService implements  RoomService {
    @Override
    public List<DynamicValueType> getRooms(String venueId) {
        var dynamicValueType = new ArrayList<DynamicValueType>();
        dynamicValueType.add(DynamicValueType.create("room1", venueId + " Room 1"));
        dynamicValueType.add(DynamicValueType.create("room2", venueId + " Room 2"));
        dynamicValueType.add(DynamicValueType.create("room3", venueId + " Room 3"));

        return dynamicValueType;
    }
}
