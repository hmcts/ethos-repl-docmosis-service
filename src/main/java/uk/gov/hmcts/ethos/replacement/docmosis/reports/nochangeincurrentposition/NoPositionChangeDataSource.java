package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import java.util.List;

public interface NoPositionChangeDataSource {
    List<NoPositionChangeSubmitEvent> getData(String caseTypeId, String reportDate);
}
