package uk.gov.hmcts.ethos.replacement.docmosis.model.bulk;

import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;

import java.util.List;

@Data
public class MultipleTypeListCaseRefNumListType {

    private List<MultipleTypeItem> multipleTypeItemList;

    private List<String> caseRefNumberList;

}
