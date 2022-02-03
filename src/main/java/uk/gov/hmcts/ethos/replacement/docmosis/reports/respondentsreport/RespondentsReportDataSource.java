package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import java.util.List;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeSubmitEvent;

public interface RespondentsReportDataSource {
    List<NoPositionChangeSubmitEvent> getData(String caseTypeId, String reportDate);

    List<SubmitMultipleEvent> getData(String caseTypeId, List<String> multipleRefsList);
}
