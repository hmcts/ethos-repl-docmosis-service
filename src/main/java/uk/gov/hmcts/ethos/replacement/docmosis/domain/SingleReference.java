package uk.gov.hmcts.ethos.replacement.docmosis.domain;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    String generateRef(String previousId, String previousYear, String defaultInitValue) {
        if (previousId.equals("")) {
            return defaultInitValue;
        } else if (previousId.equals(DEFAULT_MAX_REF) || (!previousYear.equals("") && !previousYear.equals(this.year)) ) {
            return "1";
        } else {
            return String.valueOf(Integer.parseInt(previousId) + 1);
        }
    }
}
