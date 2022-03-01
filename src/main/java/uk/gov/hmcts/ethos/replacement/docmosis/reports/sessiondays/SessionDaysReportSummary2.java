package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SessionDaysReportSummary2 {
    private String date;
    private String ftSessionDays;
    private String ptSessionDays;
    private String otherSessionDays;
    private String sessionDaysTotalDetail;
}
