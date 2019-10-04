package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "subMultipleReferenceLondonCentral")
public class SubMultipleReferenceLondonCentral extends SubMultipleReference {

    public SubMultipleReferenceLondonCentral(String multipleRef, String previousRef) {
        this.multipleRef = multipleRef;
        this.ref = generateRefNumber(previousRef);
    }
}
