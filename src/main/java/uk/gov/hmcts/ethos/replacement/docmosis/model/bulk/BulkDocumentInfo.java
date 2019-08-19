package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk;

import lombok.Data;

import java.util.List;

@Data
public class BulkDocumentInfo {

    private String markUps;

    private List<String> errors;

}
