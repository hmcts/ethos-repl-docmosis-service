package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import javax.persistence.*;
import java.time.LocalDate;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_SCOTLAND_INIT;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.SCOTLAND_OFFICE_NUMBER;

@Entity
@Table(name = "singleReferenceScotland")
public class SingleReferenceScotland extends SingleReference {

    public SingleReferenceScotland(String caseId, String previousId, String previousYear) {
        this.caseId = caseId;
        this.year = String.valueOf(LocalDate.now().getYear());
        this.ref = SCOTLAND_OFFICE_NUMBER + generateRef(previousId, previousYear, DEFAULT_SCOTLAND_INIT) + "/" + this.year;
    }

}
