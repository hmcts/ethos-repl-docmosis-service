package uk.gov.hmcts.ethos.replacement.docmosis.data;

import java.util.UUID;

public class Room {

    public final UUID id;
    public final String code;
    public final String label;
    public final String venueId;

    public Room(
            UUID id,
            String code,
            String label,
            String venueId
    ) {
        this.id = id;
        this.code = code;
        this.label = label;
        this.venueId = venueId;
    }
}
