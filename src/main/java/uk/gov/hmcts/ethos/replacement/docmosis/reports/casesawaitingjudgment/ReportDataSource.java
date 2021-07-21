package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.util.Collection;
import java.util.List;


public interface ReportDataSource {
    List<SubmitEvent> getData(Collection<String> caseTypeIds);
}
