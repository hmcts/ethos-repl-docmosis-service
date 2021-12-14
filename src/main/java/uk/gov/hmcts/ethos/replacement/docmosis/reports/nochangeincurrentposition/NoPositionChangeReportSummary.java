package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NoPositionChangeReportSummary {
    private final String office;
    private String totalCases;
    private String totalSingleCases;
    private String totalMultipleCases;
}
