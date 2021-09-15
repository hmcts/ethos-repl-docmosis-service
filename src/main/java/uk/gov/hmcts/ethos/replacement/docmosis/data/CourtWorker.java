package uk.gov.hmcts.ethos.replacement.docmosis.data;

import java.util.UUID;

public class CourtWorker {

    public final UUID id;
    public final String lookUpId;
    public final String tribunalOffice;
    public final String code;
    public final String label;

    public CourtWorker(
            UUID id,
            String lookUpId,
            String tribunalOffice,
            String code,
            String label
    ) {
        this.id = id;
        this.lookUpId = lookUpId;
        this.tribunalOffice = tribunalOffice;
        this.code = code;
        this.label = label;
    }
}
