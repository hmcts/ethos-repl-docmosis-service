package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "subMultipleReferenceBristol")
public class SubMultipleReferenceBristol extends SubMultipleReference {

    public SubMultipleReferenceBristol(String multipleRef, String previousRef) {
        this.multipleRef = multipleRef;
        this.ref = generateRefNumber(Integer.parseInt(previousRef));
    }
}
