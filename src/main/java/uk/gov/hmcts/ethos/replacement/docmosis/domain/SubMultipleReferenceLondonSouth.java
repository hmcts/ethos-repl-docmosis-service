package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "subMultipleReferenceLondonSouth")
public class SubMultipleReferenceLondonSouth extends SubMultipleReference {

    public SubMultipleReferenceLondonSouth(String multipleRef, String previousRef) {
        this.multipleRef = multipleRef;
        this.ref = generateRefNumber(Integer.parseInt(previousRef));
    }
}
