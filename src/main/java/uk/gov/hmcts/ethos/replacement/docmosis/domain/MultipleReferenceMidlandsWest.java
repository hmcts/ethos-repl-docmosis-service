package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "multipleReferenceMidlandsWest")
public class MultipleReferenceMidlandsWest extends MultipleReference {

    public MultipleReferenceMidlandsWest(String caseId, String previousRef) {
        this.caseId = caseId;
        this.ref = generateRefNumber(previousRef);
    }
}
