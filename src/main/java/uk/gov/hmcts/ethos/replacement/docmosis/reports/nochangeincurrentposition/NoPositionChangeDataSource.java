package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;

import java.util.List;

public interface NoPositionChangeDataSource {
    List<NoPositionChangeSubmitEvent> getData(String caseTypeId, String reportDate);

    List<SubmitMultipleEvent> getMultiplesData(String caseTypeId, List<String> multipleRefsList);
}
