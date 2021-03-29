package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@NoArgsConstructor
public class SingleReference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    protected Integer counter;
    protected String cyear;

}
