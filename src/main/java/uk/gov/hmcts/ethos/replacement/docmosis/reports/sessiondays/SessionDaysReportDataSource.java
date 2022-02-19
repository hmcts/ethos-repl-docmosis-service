package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;


public interface SessionDaysReportDataSource {
    List<SessionDaysSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo);
}
