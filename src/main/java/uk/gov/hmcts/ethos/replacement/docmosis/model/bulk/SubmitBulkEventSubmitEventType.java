package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk;

import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.util.List;

@Data
public class SubmitBulkEventSubmitEventType {

    private SubmitEvent submitEvent;

    private SubmitBulkEvent submitBulkEvent;

    private BulkDetails bulkDetails;

    private List<SubmitEvent> submitEventList;
}
