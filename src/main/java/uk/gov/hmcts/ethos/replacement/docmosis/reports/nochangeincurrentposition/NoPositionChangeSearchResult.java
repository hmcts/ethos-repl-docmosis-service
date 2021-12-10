package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoPositionChangeSearchResult {
    private Long total;
    private List<NoPositionChangeSubmitEvent> cases;
}
