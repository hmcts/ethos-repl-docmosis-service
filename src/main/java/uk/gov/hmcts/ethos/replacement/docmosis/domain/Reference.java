package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "reference")
@Data
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String caseId;

    public Reference(String caseId) {
        this.caseId = caseId;
    }
}
