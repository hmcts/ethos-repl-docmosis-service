package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import javax.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "singleReferenceManchester")
public class SingleReferenceManchester extends SingleReference {

    public SingleReferenceManchester(String caseId, String previousRef, String previousYear, String currentYear) {
        this.caseId = caseId;
        this.year = currentYear;
        this.ref = generateRefNumber(previousRef, previousYear, currentYear);
    }
}
