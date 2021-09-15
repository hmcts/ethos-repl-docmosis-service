package uk.gov.hmcts.ethos.replacement.docmosis.service;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

import java.util.List;

public interface RoomService {
    List<DynamicValueType> getRooms(String venueId);
}
