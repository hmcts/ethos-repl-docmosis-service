package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_INIT_REF;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_MAX_REF;

@MappedSuperclass
@Data
@NoArgsConstructor
public class SingleReference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    protected String caseId;
    protected String ref;
    protected String year;

    String generateRefNumber(String previousRef, String previousYear, String currentYear) {
        if (previousRef.equals("")) {
            return DEFAULT_INIT_REF;
        } else if (previousRef.equals(DEFAULT_MAX_REF) || (!previousYear.equals("") && !previousYear.equals(currentYear)) ) {
            return "00001";
        } else {
            return String.format("%05d", (Integer.parseInt(previousRef) + 1));
        }
    }
}
