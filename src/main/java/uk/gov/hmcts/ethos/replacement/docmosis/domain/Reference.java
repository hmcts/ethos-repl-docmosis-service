package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import javax.persistence.*;

@Entity
@Table(name = "reference")
public class Reference {
    @Id
    @GeneratedValue(generator = "reference_generator")
    @SequenceGenerator(
            name = "reference_generator",
            sequenceName = "reference_sequence",
            initialValue = 0
    )
    private Long id;

    public Long getId() {
        return id;
    }
}
