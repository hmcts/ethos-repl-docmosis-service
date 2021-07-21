package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.Collection;

@Service
public class CasesAwaitingJudgmentReport {

    private final ReportDataSource reportDataSource;

    public CasesAwaitingJudgmentReport(ReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public CasesAwaitingJudgmentReportData runReport(Collection<String> caseTypeIds, String user) {
        return null;
    }

}
