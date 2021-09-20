package uk.gov.hmcts.ethos.replacement.docmosis.data;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

import java.util.UUID;

public class Room {

    public final UUID id;
    public final String venueId;
    public final DynamicValueType dynamicValueType;

    public Room(
            DynamicValueType dynamicValueType,
            String venueId
    ) {
        this.id = UUID.randomUUID();
        this.dynamicValueType = dynamicValueType;
        this.venueId = venueId;
    }
}
