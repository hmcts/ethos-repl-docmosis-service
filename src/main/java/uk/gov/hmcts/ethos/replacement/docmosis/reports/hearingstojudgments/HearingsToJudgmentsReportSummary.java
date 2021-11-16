package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HearingsToJudgmentsReportSummary {
    private final String office;
    private String totalCases;
    private String total4Wk;
    private String total4WkPercent;
    private String totalX4Wk;
    private String totalX4WkPercent;
}
