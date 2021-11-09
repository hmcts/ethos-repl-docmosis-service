package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ReportSummary {
    private final String office;
    private String totalCases;
    private String total4wk;
    private String total4wkPerCent;
    private String totalx4wk;
    private String totalx4wkPerCent;
}
