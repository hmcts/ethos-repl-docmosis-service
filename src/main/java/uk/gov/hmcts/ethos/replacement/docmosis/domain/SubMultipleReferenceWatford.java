package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "subMultipleReferenceWatford")
public class SubMultipleReferenceWatford extends SubMultipleReference {

    public SubMultipleReferenceWatford(String multipleRef, String previousRef) {
        this.multipleRef = multipleRef;
        this.ref = generateRefNumber(Integer.parseInt(previousRef));
    }
}
