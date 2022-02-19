package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;


import java.util.List;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;

public interface SessionDaysReportDataSource {
    List<SessionDaysSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo);
}
