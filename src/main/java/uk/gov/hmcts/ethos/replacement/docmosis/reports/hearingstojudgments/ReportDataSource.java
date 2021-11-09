package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import uk.gov.hmcts.ecm.common.model.reports.hearingstojudgments.HearingsToJudgmentsSubmitEvent;

import java.util.List;

public interface ReportDataSource {
    List<HearingsToJudgmentsSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo);
}