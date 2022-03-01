package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SessionDaysReportSummary {
    private final String office;
    private String ftSessionDaysTotal;
    private String ptSessionDaysTotal;
    private String otherSessionDaysTotal;
    private String sessionDaysTotal;
    private String ptSessionDaysPerCent;
}
