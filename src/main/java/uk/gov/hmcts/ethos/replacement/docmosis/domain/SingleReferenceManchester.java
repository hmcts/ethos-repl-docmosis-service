package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import javax.persistence.*;
import java.time.LocalDate;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_MANCHESTER_INIT;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.MANCHESTER_OFFICE_NUMBER;

@Entity
@Table(name = "singleReferenceManchester")
public class SingleReferenceManchester extends SingleReference {

    public SingleReferenceManchester(String caseId, String previousId, String previousYear) {
        this.caseId = caseId;
        this.year = String.valueOf(LocalDate.now().getYear());
        this.ref = MANCHESTER_OFFICE_NUMBER + generateRef(previousId, previousYear, DEFAULT_MANCHESTER_INIT) + "/" + this.year;
    }
}
