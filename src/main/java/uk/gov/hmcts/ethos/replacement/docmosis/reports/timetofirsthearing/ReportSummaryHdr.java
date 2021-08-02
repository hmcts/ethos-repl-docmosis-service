package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportSummaryHdr {
    private final long totalCases;
    private final long wk26Total;
    private final float wk26TotalPercent;
    private final long x26wkTotal;
    private final float x26wkTotalPercent;

}
