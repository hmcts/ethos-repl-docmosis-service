package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_INIT_REF;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_MAX_REF;

@MappedSuperclass
@Data
@NoArgsConstructor
public class MultipleReference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    protected String caseId;
    protected String ref;

    String generateRefNumber(String previousRef) {
        if (previousRef.equals("")) {
            return DEFAULT_INIT_REF;
        } else if (previousRef.equals(DEFAULT_MAX_REF)) {
            return "00001";
        } else {
            return String.format("%05d", (Integer.parseInt(previousRef) + 1));
        }
    }
}
