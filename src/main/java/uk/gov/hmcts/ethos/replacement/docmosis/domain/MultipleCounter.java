package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "multiplecounter")
public class MultipleCounter {

    @Id
    private String multipleref;
    private Integer counter;
}
