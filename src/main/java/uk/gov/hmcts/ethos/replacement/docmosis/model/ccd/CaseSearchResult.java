package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import java.util.List;

public class CaseSearchResult {

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
