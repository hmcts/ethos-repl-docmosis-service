package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "multipleReferenceScotland")
public class MultipleReferenceScotland extends MultipleReference {

    public MultipleReferenceScotland(String caseId, String previousRef) {
        this.caseId = caseId;
        this.ref = generateRefNumber(previousRef);
    }
}
