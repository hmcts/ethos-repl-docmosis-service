package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "multipleReferenceBristol")
public class MultipleReferenceBristol extends MultipleReference {

    public MultipleReferenceBristol(String caseId, String previousRef) {
        this.caseId = caseId;
        this.ref = generateRefNumber(previousRef);
    }
}
