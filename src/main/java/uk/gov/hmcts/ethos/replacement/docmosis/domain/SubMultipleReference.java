package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@MappedSuperclass
@Data
@NoArgsConstructor
public class SubMultipleReference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    protected Integer multref;
    protected Integer submultref;
}
