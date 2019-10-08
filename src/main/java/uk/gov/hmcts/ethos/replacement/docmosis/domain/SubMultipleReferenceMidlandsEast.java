package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "subMultipleReferenceMidlandsEast")
public class SubMultipleReferenceMidlandsEast extends SubMultipleReference {

    public SubMultipleReferenceMidlandsEast(String multipleRef, String previousRef) {
        this.multipleRef = multipleRef;
        this.ref = generateRefNumber(Integer.parseInt(previousRef));
    }
}
