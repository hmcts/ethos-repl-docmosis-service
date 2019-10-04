package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "subMultipleReferenceManchester")
public class SubMultipleReferenceManchester extends SubMultipleReference {

    public SubMultipleReferenceManchester(String multipleRef, String previousRef) {
        this.multipleRef = multipleRef;
        this.ref = generateRefNumber(previousRef);
    }
}
