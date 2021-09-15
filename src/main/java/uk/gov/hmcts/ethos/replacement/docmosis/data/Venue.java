package uk.gov.hmcts.ethos.replacement.docmosis.data;

import java.time.Instant;
import java.util.UUID;

public class Venue {

    public final UUID id;
    public final String tribunalOffice;
    public final String code;
    public final String label;


    public Venue(
            UUID id,
            String tribunalOffice,
            String code,
            String label
    ) {
        this.id = id;
        this.tribunalOffice = tribunalOffice;
        this.code = code;
        this.label = label;
    }

}
