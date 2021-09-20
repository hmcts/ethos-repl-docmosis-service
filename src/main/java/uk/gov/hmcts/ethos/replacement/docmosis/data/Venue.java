package uk.gov.hmcts.ethos.replacement.docmosis.data;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

import java.util.UUID;

public class Venue {

    public final UUID id;
    public final String tribunalOffice;
    public final DynamicValueType dynamicValueType;


    public Venue(
            String tribunalOffice,
            DynamicValueType dynamicValueType
    ) {
        this.id = UUID.randomUUID();
        this.tribunalOffice = tribunalOffice;
        this.dynamicValueType = dynamicValueType;
    }

}
