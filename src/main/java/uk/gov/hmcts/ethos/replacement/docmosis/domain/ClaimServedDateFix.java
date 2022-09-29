package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import javax.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name="case_data")
public class ClaimServedDateFix {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    protected Integer counter;
}
