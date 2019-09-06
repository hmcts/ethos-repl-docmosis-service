package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "reference")
@Data
@NoArgsConstructor
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String caseId;
    private String ref;
    private String year;

    public Reference(String caseId, String previousId) {
        this.caseId = caseId;
        this.year = "2019";
        if (previousId != null) {
            this.ref = previousId + "/" + "2019";
        }
    }
}
