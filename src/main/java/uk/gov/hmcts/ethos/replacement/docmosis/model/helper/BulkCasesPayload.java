package uk.gov.hmcts.ethos.replacement.docmosis.model.helper;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;

import java.util.List;

@Data
@NoArgsConstructor
public class BulkCasesPayload {

    private List<String> alreadyTakenIds;
    private List<SubmitEvent> submitEvents;
    private List<MultipleTypeItem> multipleTypeItems;
    private List<String> errors;
}
