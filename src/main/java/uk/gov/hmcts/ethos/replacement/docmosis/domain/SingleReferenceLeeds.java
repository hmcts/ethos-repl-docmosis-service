package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "singleReferenceLeeds")
public class SingleReferenceLeeds extends SingleReference {

    public SingleReferenceLeeds(String caseId, String previousRef, String previousYear, String currentYear) {
        this.caseId = caseId;
        this.year = currentYear;
        this.ref = generateRefNumber(previousRef, previousYear, currentYear);
    }
}
