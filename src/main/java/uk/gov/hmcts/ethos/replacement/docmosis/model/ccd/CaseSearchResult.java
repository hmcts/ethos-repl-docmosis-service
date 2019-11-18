package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import java.util.List;

import static java.util.Collections.emptyList;

public class CaseSearchResult {

    public static final CaseSearchResult EMPTY = new CaseSearchResult(0L, emptyList());

    private Long total;
    private List<SubmitEvent> cases;

    public CaseSearchResult() {
    }

    public CaseSearchResult(Long total, List<SubmitEvent> cases) {
        this.cases = cases;
        this.total = total;
    }

    public List<SubmitEvent> getCases() {
        return cases;
    }

    public Long getTotal() {
        return total;
    }
}
