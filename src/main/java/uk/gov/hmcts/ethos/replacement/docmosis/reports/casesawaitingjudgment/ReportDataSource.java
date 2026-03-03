package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import uk.gov.hmcts.ecm.compat.common.model.reports.casesawaitingjudgment.CasesAwaitingJudgmentSubmitEvent;

import java.util.List;

public interface ReportDataSource {
    List<CasesAwaitingJudgmentSubmitEvent> getData(String caseTypeId);
}