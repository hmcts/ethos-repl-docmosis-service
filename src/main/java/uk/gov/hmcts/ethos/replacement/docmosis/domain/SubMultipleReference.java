package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@MappedSuperclass
@Data
@NoArgsConstructor
public class SubMultipleReference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    protected String multipleRef;
    protected int ref;

    int generateRefNumber(int previousRef) {
        if (previousRef == 0) {
            return DEFAULT_INIT_SUB_REF;
        } else {
            return previousRef + 1;
        }
    }
}
