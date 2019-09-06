package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "reference")
@Data
public class Reference {

    @Id
    @SequenceGenerator(name="seq",sequenceName="seqOffice")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    private Long id;

    private String caseId;

    public Reference(String caseId) {
        this.caseId = caseId;
    }
}
