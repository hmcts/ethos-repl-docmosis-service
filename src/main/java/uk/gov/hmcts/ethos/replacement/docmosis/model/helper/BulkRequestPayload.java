package uk.gov.hmcts.ethos.replacement.docmosis.model.helper;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;

import java.util.List;

@Data
@NoArgsConstructor
public class BulkRequestPayload {

    private List<String> errors;
    private BulkDetails bulkDetails;
}
