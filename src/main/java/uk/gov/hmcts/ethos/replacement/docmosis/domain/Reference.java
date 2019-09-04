package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import javax.persistence.*;

@Entity
@Table(name = "reference")
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }
}
