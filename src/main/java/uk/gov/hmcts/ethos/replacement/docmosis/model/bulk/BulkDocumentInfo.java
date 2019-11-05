package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk;

import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;

import java.util.List;

@Data
public class BulkDocumentInfo {

    private String markUps;
    private List<String> errors;
    private DocumentInfo documentInfo;

}
