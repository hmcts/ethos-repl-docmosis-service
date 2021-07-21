package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class CcdReportDataSource implements ReportDataSource {
    @Override
    public List<SubmitEvent> getData(Collection<String> caseTypeIds) {
        return null;
    }
}
