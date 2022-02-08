package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import java.util.List;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportSubmitEvent;

public interface RespondentsReportDataSource {
    List<RespondentsReportSubmitEvent> getData(String caseTypeId,String listingDateFrom,String listingDateTo);
}
