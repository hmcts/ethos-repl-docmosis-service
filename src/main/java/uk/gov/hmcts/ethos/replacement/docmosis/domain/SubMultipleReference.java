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
    protected String ref;

    String generateRefNumber(String previousRef) {
        if (previousRef.equals("")) {
            return DEFAULT_INIT_SUB_REF;
        } else {
            return String.valueOf(Integer.parseInt(previousRef) + 1);
        }
    }
}
