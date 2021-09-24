package uk.gov.hmcts.ethos.replacement.docmosis.data;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import java.util.UUID;

public class CourtWorker {

    public final UUID id;
    public final String lookUpId;
    public final String tribunalOffice;
    public final DynamicValueType dynamicValueType;

    public CourtWorker(
            String lookUpId,
            String tribunalOffice,
           DynamicValueType dynamicValueType
    ) {
        this.id = UUID.randomUUID();
        this.lookUpId = lookUpId;
        this.tribunalOffice = tribunalOffice;
        this.dynamicValueType = dynamicValueType;
    }

}
