package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table(name = "singleReferenceScotland")
public class SingleReferenceScotland extends SingleReference {

    public SingleReferenceScotland(String caseId, String previousRef, String previousYear, String currentYear) {
        this.caseId = caseId;
        this.year = currentYear;
        this.ref = generateRefNumber(previousRef, previousYear, currentYear);
    }

}
