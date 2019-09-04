package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import javax.persistence.*;

@Entity
@Table(name = "reference")
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caseId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseId() {
        return caseId;
    }
}
