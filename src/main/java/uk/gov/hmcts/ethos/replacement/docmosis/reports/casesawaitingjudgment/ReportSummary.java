package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ReportSummary {
    private final String office;
    private final List<PositionTypeSummary> positionTypes = new ArrayList<>();
}
