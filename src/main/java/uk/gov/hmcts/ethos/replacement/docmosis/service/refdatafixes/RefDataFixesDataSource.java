package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.util.List;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;

public interface RefDataFixesDataSource {
    List<SubmitEvent> getData(ReportParams reportParams);
}
